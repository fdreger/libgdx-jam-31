package io.github.gdx31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.FWSkin;
import com.github.tommyettinger.textra.TypingLabel;
import com.github.tommyettinger.textra.TypingListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private Stage stage;
    private TypingLabel typingLabel;
    private final Map<String, String> texts = Map.of(
        "1", "This should appear on the top of the screen."

    );
    private int currentTextIndex = ~0;

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

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);

        Skin skin = new FWSkin(Gdx.files.internal("ui/uiskin.json"));
        typingLabel = new TypingLabel("        ", skin);
        typingLabel.setTextSpeed(0.1f);
        typingLabel.setPosition(10, Gdx.graphics.getHeight() - 50);
        stage.addActor(typingLabel);

        typingLabel.setTypingListener(new TypingListenerAdapter() {
            @Override
            public void event(String event) {
                if (event.startsWith("next")) {
                    nextText(event.split(":")[1]);
                }
                eventHappens(event);
            }
        });

    }

    private void eventHappens(String event) {

    }

    private void nextText(String next) {
        typingLabel.setText(texts.get(next));
        typingLabel.restart();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        if (currentTextIndex < 0) {
            currentTextIndex = ~currentTextIndex;
            nextText("1");
        }

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

            HeroMovementHandler.handleHeroMovement(hero, onTheFloor, deltaTime, currentRoom.getCollisions().values());

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

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (isMenuVisible) {
            mainMenu.resize(width, height);
        }
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (isMenuVisible) {
            mainMenu.hide();
        }
        stage.dispose();
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
