package me.acidviper.plantshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.acidviper.plantshooter.screens.GameScreen;

public class PlantShooter extends Game {
	public SpriteBatch batch;
	BitmapFont font;
	public static final float PPM = 100;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();

		setScreen(new GameScreen(this));
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