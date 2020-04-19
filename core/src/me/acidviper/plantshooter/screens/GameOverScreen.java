package me.acidviper.plantshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.acidviper.plantshooter.PlantShooter;

public class GameOverScreen implements Screen {
    PlantShooter game;
    OrthographicCamera camera;
    FitViewport port;
    Texture texture;

    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    FreeTypeFontGenerator generator;
    int wavesSurvived;
    public GameOverScreen(PlantShooter game, int waves) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        port = new FitViewport(1280, 720, camera);
        texture = new Texture("Sprites/GameOver.png");
        this.game = game;
        this.wavesSurvived = waves;

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Font.ttf"));
        parameter.size = 85;
        game.font = generator.generateFont(parameter);
    }
    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.setColor(Color.WHITE);
        game.batch.draw(texture, 0,0, 1280, 720);
        game.font.draw(game.batch, "Thank you for playing my LD Game", camera.position.x - 600,  camera.position.y);
        game.font.draw(game.batch, "You survived " + wavesSurvived + " rounds.", camera.position.x - 450, camera.position.y - 100);

        game.batch.end();
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
