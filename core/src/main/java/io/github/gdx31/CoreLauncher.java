package io.github.gdx31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CoreLauncher extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMapRenderer renderer;

    @Override
    public void create() {
        TmxMapLoader loader = new TmxMapLoader();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(300, 200, camera);
        renderer = new OrthogonalTiledMapRenderer(loader.load("platform.tmx"), batch);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        camera.position.set(150, 100, 0);
        viewport.update(width, height, true);
    }
}
