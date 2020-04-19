package me.acidviper.plantshooter.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import me.acidviper.plantshooter.PlantShooter;

public class Button extends Sprite {

    public String text;
    Texture texture;
    public Rectangle rec;
    float x;
    float y;

    Camera camera;
    public Button(float x, float y, String text, Camera camera) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.camera = camera;
        texture = new Texture("Sprites/ButtonIcon.png");
        setTexture(texture);
        TextureRegion doorImage = new TextureRegion(getTexture(), 0,0, 59,30);
        setBounds((x / PlantShooter.PPM) + camera.position.x, (y / PlantShooter.PPM), 59 * 4 / PlantShooter.PPM, 30 * 4 / PlantShooter.PPM);
        setRegion(doorImage);
        rec = new Rectangle();
        rec.x = getX() + 6f;
        rec.y = getY();
        rec.width = (59 * 4) / PlantShooter.PPM;
        rec.height = (30 * 4) / PlantShooter.PPM;
    }

    public void update() {
        rec.x = getX();
        rec.y = getY();
    }
}
