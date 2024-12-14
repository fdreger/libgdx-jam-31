package io.github.gdx31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenu extends ScreenAdapter {
    private Stage stage;
    private Skin skin;

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton.TextButtonStyle buttonStyle = skin.get(TextButton.TextButtonStyle.class);
        buttonStyle.font.getData().setScale(2); // Increase font size

        TextButton startButton = new TextButton("Start", buttonStyle);
        TextButton optionsButton = new TextButton("Options", buttonStyle);
        TextButton creditsButton = new TextButton("Credits", buttonStyle);

        float buttonWidth = 300;
        float buttonHeight = 100;
        float padding = 20;

        table.add(startButton).width(buttonWidth).height(buttonHeight).pad(padding);
        table.row();
        table.add(optionsButton).width(buttonWidth).height(buttonHeight).pad(padding);
        table.row();
        table.add(creditsButton).width(buttonWidth).height(buttonHeight).pad(padding);
    }

    @Override
    public void render(float delta) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }
}
