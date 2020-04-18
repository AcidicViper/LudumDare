package me.acidviper.plantshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.screens.GameScreen;

public class Bullet extends Sprite {

    World world;
    boolean facingRight;

    public Body body;
    public TextureRegion bulletImage;

    public  Bullet(float x, float y, boolean facingRight, World world) {
        this.world = world;
        this.facingRight = facingRight;

        setTexture(new Texture("Sprites/Bullet.png"));
        bulletImage = new TextureRegion(getTexture(), 0,0, 16,16);
        setBounds(0,0, 16 * 2 / PlantShooter.PPM, 16 * 2 / PlantShooter.PPM);
        setRegion(bulletImage);
        defineBody(x, y, world);
        if (!facingRight) { flip(true, false);}
    }
    public void defineBody(float x, float y, World world) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(new Vector2(x / PlantShooter.PPM, y / PlantShooter.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getTexture().getWidth() / 2f) / PlantShooter.PPM, (getTexture().getHeight() / 2f) / PlantShooter.PPM ) ;
        fdef.shape = shape;

        body.createFixture(fdef);
        body.setGravityScale(0);
        setPosition( (body.getPosition().x - getWidth() / 2), (body.getPosition().y - getHeight() / 2));
    }
    public void update(float dt) {
        setPosition( (body.getPosition().x - getWidth() / 2) - (1 / PlantShooter.PPM)  + .05f, (body.getPosition().y - getHeight() / 2)  - ( 2 / PlantShooter.PPM));
        if (facingRight) { body.setLinearVelocity(3f , 0f); } else { body.setLinearVelocity(-3f , 0f); }
    }
}
