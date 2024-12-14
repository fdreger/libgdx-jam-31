package io.github.gdx31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CoreLauncher extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMapRenderer renderer;
    private TiledMap map;

    private HashMap<TiledMapTileLayer.Cell, Rectangle> collisions;
    private HashMap<Rectangle, TiledMapTileLayer> layerHashMap = new HashMap<>();

    Rectangle intersection = new Rectangle();

    GameObject hero;
    List<GameObject> gameObjects = new ArrayList<>();

    private int width;
    private int height;

    @Override
    public void create() {
        TmxMapLoader loader = new TmxMapLoader();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        map = loader.load("platform.tmx");
        TiledMapTileLayer layerForSize = map.getLayers().getByType(TiledMapTileLayer.class).get(0);
        width = layerForSize.getWidth() * layerForSize.getTileWidth();
        height = layerForSize.getHeight() * layerForSize.getTileHeight();

        viewport = new FitViewport(width, height, camera);
        renderer = new TiledMapMoreRenderer(map, batch);

        collisions = new HashMap<>();
        for (MapLayer mapLayer : map.getLayers().getByType(TiledMapTileLayer.class)) {
            TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
            if (layer.getName().startsWith("# ")) {
                retrieveCollideables(collisions, map, layer.getName());
                Rectangle rectangle = new Rectangle(layer.getOffsetX(), -layer.getOffsetY(), layer.getWidth() * layer.getTileWidth(), layer.getHeight() * layer.getTileHeight());
                layerHashMap.put(rectangle, layer);
            }
        }

        for (MapLayer mapLayer : map.getLayers()) {
            for (MapObject object : mapLayer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    GameObject go = new GameObject();
                    gameObjects.add(go);
                    go.tileObject = (TiledMapTileMapObject) object;
                    go.position.set(go.tileObject.getX(), go.tileObject.getY(), go.tileObject.getTile().getTextureRegion().getRegionWidth(), go.tileObject.getTile().getTextureRegion().getRegionHeight());
                    go.velocity.set(0, 0);
                    go.type = (String) go.tileObject.getProperties().get("type");
                    if (go.type == null) {
                        go.type = go.tileObject.getTile().getProperties().get("type", String.class);
                    }
                    if ("hero".equals(go.type)) {
                        hero = go;
                        hero.position.width = 6;
                        hero.position.height = 6;
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        for (Map.Entry<Rectangle, TiledMapTileLayer> rectangleTiledMapTileLayerEntry : layerHashMap.entrySet()) {
            if (rectangleTiledMapTileLayerEntry.getKey().overlaps(hero.position)) {
                camera.position.set(rectangleTiledMapTileLayerEntry.getKey().x + rectangleTiledMapTileLayerEntry.getKey().width / 2, rectangleTiledMapTileLayerEntry.getKey().y + rectangleTiledMapTileLayerEntry.getKey().height / 2, 0);
                camera.update();
            }
        }

        renderer.setView(camera);
        renderer.render();

        boolean onTheFloor = moveWithCollisionAndCheckIfOnTheFloor(hero.position, collisions.values(), hero.velocity);

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && onTheFloor) {
            hero.velocity.y = 3;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            hero.velocity.x += 0.5f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            hero.velocity.x -= 0.5f;
        }

        // clamp velocity:
        hero.velocity.x = MathUtils.clamp(hero.velocity.x, -1, 1);
        hero.velocity.y = MathUtils.clamp(hero.velocity.y, -5, 5);
        if (hero.velocity.y < -3) {
            hero.velocity.y = -1;
        }

        // dampen horizontal speed:
        float dampingAmount = 0.05f;
        float dampingDirection = Math.signum(hero.velocity.x) * -1;

        if (Math.abs(hero.velocity.x) < dampingAmount) {
            hero.velocity.x = 0;
        } else {
            hero.velocity.x += dampingDirection * dampingAmount;
        }


        if (Math.abs(hero.velocity.x) < dampingAmount) {
            hero.velocity.x = 0;
        }

        // gravity:
        hero.velocity.y -= 0.1f;

        for (GameObject gameObject : gameObjects) {
            gameObject.tileObject.setX(gameObject.position.x);
            gameObject.tileObject.setY(gameObject.position.y);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private boolean moveWithCollisionAndCheckIfOnTheFloor(Rectangle mover, Collection<Rectangle> whatCollides, Vector2 move) {

        boolean onTheFloor = false;

        // generaly, we move first, and only withdraw upon a collision,
        // and we always withdraw in the direction opposite to the movement,
        // making two separate steps along the x and y axis.

        // only X:
        mover.x += move.x;

        for (Rectangle collisionCandidate : whatCollides) {
            if (collisionCandidate.overlaps(mover)) {

                // we need to translate the mover in the direction opposite to its speed, always
                // (NOT the shortest distance)
                float horizontalDirection = -Math.signum(move.x);

                // this could be easily calculated by hand, but since we already using LibGDX,
                // we have the Intersector:
                Intersector.intersectRectangles(collisionCandidate, mover, intersection);

                // the intersection rectangle is now the overlap between the hero and its collision.
                // we move hero in the calculated direction just far enough to avoid the collision
                // (the hero will cling to the surface and slide along)
                mover.x += horizontalDirection * intersection.width;

                hero.velocity.x = 0;
            }
        }

        // now we need to repeat for Y, but NOT from the original position, but from the position
        // already moved horizonally:

        mover.y += move.y;
        for (Rectangle collisionCandidate : whatCollides) {
            if (collisionCandidate.overlaps(mover)) {
                float verticalDirection = -Math.signum(move.y);
                Intersector.intersectRectangles(collisionCandidate, mover, intersection);
                mover.y += verticalDirection * intersection.height;

                // a fine point: usually we need to remember things like "is hero standing on the floor"
                if (verticalDirection == 1) {
                    onTheFloor = true;
                }

                hero.velocity.y = 0;
            }
        }
        return onTheFloor;
    }

    private Map<TiledMapTileLayer.Cell, Rectangle> retrieveCollideables(Map<TiledMapTileLayer.Cell, Rectangle> collideables, TiledMap tiledMap, String layer) {
        if (collideables == null) {
            collideables = new HashMap<>();
        }

        TiledMapTileLayer collisionsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layer);
        int gridTileWidth = collisionsLayer.getTileWidth();
        int gridTileHeight = collisionsLayer.getTileHeight();
        for (int row = 0; row < collisionsLayer.getHeight(); row++) {
            for (int col = 0; col < collisionsLayer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = collisionsLayer.getCell(col, row);

                if (cell == null || "no-collision".equals(cell.getTile().getProperties().get("type"))) continue;

                TextureRegion cellTextureRegion = cell.getTile().getTextureRegion();
                Rectangle cellRectangle = new Rectangle(
                    col * gridTileWidth + collisionsLayer.getOffsetX(),
                    row * gridTileHeight - collisionsLayer.getOffsetY(),
                    cellTextureRegion.getRegionWidth(),
                    cellTextureRegion.getRegionHeight());

                collideables.put(cell, cellRectangle);
            }
        }
        return collideables;
    }
}
