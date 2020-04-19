package me.acidviper.plantshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.acidviper.plantshooter.PlantShooter;

public class MenuScreen implements Screen {
    PlantShooter game;
    OrthographicCamera camera;
    FitViewport port;
    Texture texture;
    Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Succed Purchase.wav"));

    public MenuScreen(PlantShooter game) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        port = new FitViewport(1280, 720, camera);
        texture = new Texture("Sprites/Capture.PNG");
        this.game = game;
    }
    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(texture, 0,0, 1280, 720);
        game.batch.end();
        if (Gdx.input.isTouched()) { clickSound.play(); game.setScreen(new GameScreen(game)); }
    }

    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void show() { }

    @Override public void dispose() {
        texture.dispose();
        game.dispose();
    }
}
