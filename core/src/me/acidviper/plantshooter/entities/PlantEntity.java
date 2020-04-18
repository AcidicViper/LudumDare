package me.acidviper.plantshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import me.acidviper.plantshooter.PlantShooter;

public class PlantEntity extends Sprite {
    public Body body;
    public int health = 15;

    TextureRegion plantIdle;

    public PlantEntity(int x,int y, World world) {
        setTexture(new Texture("Sprites/PlantSprite.png"));
        plantIdle = new TextureRegion(getTexture(), 0,0, 124,124);
        setBounds(0,0, 124 * 2 / PlantShooter.PPM, 124 * 2 / PlantShooter.PPM);
        setRegion(plantIdle);
        defineBody(x, y, world);
    }

    public void defineBody(int x, int y, World world) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(new Vector2(x / PlantShooter.PPM,y / PlantShooter.PPM));
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getTexture().getWidth() / 2f) / PlantShooter.PPM, (getTexture().getHeight() / 2f) + 5 / PlantShooter.PPM ) ;
        fdef.shape = shape;

        body.createFixture(fdef);
        setPosition( (body.getPosition().x - getWidth() / 2), (body.getPosition().y - getHeight() / 2));

    }
    public void update(float dt) {
        setPosition( (body.getPosition().x - getWidth() / 2) - (1 / PlantShooter.PPM)  + .05f, (body.getPosition().y - getHeight() / 2)  - ( 2 / PlantShooter.PPM) + 1.5f);
    }
}
