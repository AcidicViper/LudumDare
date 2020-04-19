package me.acidviper.plantshooter.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.screens.GameScreen;

public class Enemy extends Sprite {
    private TextureRegion enemyDefault;

    public enum State { RUNNING }
    public State currentState;
    public State previousState;
    private Animation enemyRun;

    private float stateTimer;
    public boolean runningRight;
    public boolean facingRight;

    public int health = 2;
    public float speed = 1.2f;

    World world;
    public Body body;

    GameScreen screen;

    public Enemy(float x, float y, World world, GameScreen screen, boolean goLeft, float Strength) {
        super(screen.getEnemyAtlas().findRegion("IDLEANIMATION"));
        this.screen = screen;
        currentState = State.RUNNING;
        previousState = State.RUNNING;

        health *= Strength;
        speed *= Strength;

        stateTimer = 0;
        runningRight = goLeft;


        Array<TextureRegion> frames = new Array<>();
        for (int i = 5; i < 7; i++) {
            frames.add(new TextureRegion(getTexture(), i * 32, 0 , 32,32));
        }
        enemyRun = new Animation(0.1f, frames);

        enemyDefault = new TextureRegion(getTexture(), 0,0, 32,32);
        setBounds(0,0, 64 / PlantShooter.PPM, 64 / PlantShooter.PPM);
        setRegion(enemyDefault);

        this.world = world;
        defineBody(x,y);

    }

    public void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(new Vector2(x / PlantShooter.PPM,y / PlantShooter.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape() ;
        shape.setRadius(9 / PlantShooter.PPM * 2);
        fdef.shape = shape;

        fdef.filter.categoryBits = screen.CATEGORY_MONSTER;
        fdef.filter.maskBits = (short) (screen.CATEGORY_MONSTERSTATICOBJECTS & screen.CATEGORY_BULLETS);
        body.createFixture(fdef);
    }


    public void update(float dt) {
        setPosition( (body.getPosition().x - getWidth() / 2) - (1 / PlantShooter.PPM)  + .05f, (body.getPosition().y - getHeight() / 2)  - ( 2 / PlantShooter.PPM) + 0.15f);
        setRegion(getFrame(dt));

        if (runningRight) { body.setLinearVelocity(3f , 0f); } else {body.setLinearVelocity(-3f , 0f);}
    }
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch(currentState) {
            case RUNNING:
            default:
                region = (TextureRegion) enemyRun.getKeyFrame(stateTimer, true);
                break;
        }
        if ((body.getLinearVelocity().x <0 || !runningRight) && !region.isFlipX()) {
            region.flip(true,false);
            runningRight = false;
        }
        else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }
    public State getState() {
        return State.RUNNING;
    }
}
