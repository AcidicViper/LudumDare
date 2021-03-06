package me.acidviper.plantshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.acidviper.plantshooter.screens.MenuScreen;

public class PlantShooter extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public static final float PPM = 100;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();

		setScreen(new MenuScreen(this));
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
