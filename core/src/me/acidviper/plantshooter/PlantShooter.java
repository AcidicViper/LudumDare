package me.acidviper.plantshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import me.acidviper.plantshooter.screens.GameScreen;

public class PlantShooter extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public static final float PPM = 100;

	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();

		setScreen(new GameScreen(this));

		generator = new FreeTypeFontGenerator(Gdx.files.internal("Font.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 5;
		font = generator.generateFont(parameter);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
