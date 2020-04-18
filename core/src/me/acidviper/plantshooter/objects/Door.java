package me.acidviper.plantshooter.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import me.acidviper.plantshooter.PlantShooter;

public class Door extends Sprite {
    public int doorNumber;
    public Rectangle rec;
    public Texture texture;

    public Door(float x, float y, int doorNumber) {
        texture = new Texture("Sprites/BrightDoor.png");
        setTexture(texture);
        TextureRegion doorImage = new TextureRegion(getTexture(), 0,0, 41,64);
        setBounds(0,0, 41 * 2 / PlantShooter.PPM, 64 * 2 / PlantShooter.PPM);
        setPosition(x / PlantShooter.PPM,y / PlantShooter.PPM);
        setRegion(doorImage);
        this.doorNumber = doorNumber;
        rec = new Rectangle();
        rec.x = x / PlantShooter.PPM;
        rec.y = y / PlantShooter.PPM;
        rec.width = (41 * 2) / PlantShooter.PPM;
        rec.height = (64 * 2) / PlantShooter.PPM;
    }
}
