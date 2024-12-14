package io.github.gdx31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;

public class CoreLauncher extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMapRenderer renderer;
    private TiledMap map;
    private WorldState worldState;
    private CollisionHandler collisionHandler;
    private Room currentRoom;
    private MainMenu mainMenu;
    private boolean isMenuVisible;

    @Override
    public void create() {
        TmxMapLoader loader = new TmxMapLoader();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        map = loader.load("platform.tmx");
        worldState = new WorldState(map);
        collisionHandler = new CollisionHandler();

        TiledMapTileLayer layerForSize = map.getLayers().getByType(TiledMapTileLayer.class).get(0);
        int width = layerForSize.getWidth() * layerForSize.getTileWidth();
        int height = layerForSize.getHeight() * layerForSize.getTileHeight();

        viewport = new FitViewport(width, height, camera);
        renderer = new TiledMapMoreRenderer(map, batch);

        currentRoom = worldState.getCurrentRoom(worldState.getHero());

        mainMenu = new MainMenu();
        isMenuVisible = false;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isMenuVisible) {
                hideMenu();
            } else {
                showMenu();
            }
        }

        if (isMenuVisible) {
            mainMenu.render(Gdx.graphics.getDeltaTime());
        } else {
            GameObject hero = worldState.getHero();
            Room newRoom = worldState.getCurrentRoom(hero);
            if (newRoom != currentRoom) {
                currentRoom = newRoom;
                camera.position.set(currentRoom.getBounds().x + currentRoom.getBounds().width / 2, currentRoom.getBounds().y + currentRoom.getBounds().height / 2, 0);
                camera.update();
            }

            renderer.setView(camera);
            renderer.render();

            float deltaTime = Gdx.graphics.getDeltaTime();
            boolean onTheFloor = collisionHandler.moveWithCollisionAndCheckIfOnTheFloor(deltaTime, hero, currentRoom.getCollisions().values());

            for (GameObject gameObject : currentRoom.getGameObjects()) {
                if (gameObject == hero) continue;
                collisionHandler.moveWithCollisionAndCheckIfOnTheFloor(deltaTime, gameObject, currentRoom.getCollisions().values());
                gameObject.tileObject.setX(gameObject.position.x);
                gameObject.tileObject.setY(gameObject.position.y);

            }

            HeroMovementHandler.handleHeroMovement(hero, onTheFloor, deltaTime);

            for (GameObject gameObject : currentRoom.getGameObjects()) {
                if (gameObject == hero) continue;
                if (gameObject.position.overlaps(hero.position)) {
                    if (gameObject.savePoint) {
                        worldState.setSavePoint(gameObject.position.x, gameObject.position.y);
                    }
                    if (gameObject.kills) {
                        worldState.playerKilled();
                    }


                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (isMenuVisible) {
            mainMenu.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (isMenuVisible) {
            mainMenu.hide();
        }
    }

    public void showMenu() {
        isMenuVisible = true;
        mainMenu.show();
    }

    public void hideMenu() {
        isMenuVisible = false;
        mainMenu.hide();
    }
}
