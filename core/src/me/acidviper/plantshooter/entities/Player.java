package me.acidviper.plantshooter.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.screens.GameScreen;



public class Player extends Sprite {
    World world;
    public Body body;
    private TextureRegion playerStanding;

    public enum State { RUNNING, IDLE }
    public State currentState;
    public State previousState;
    private Animation playerRun;
    private Animation playerIdle;

    private float stateTimer;
    public boolean runningRight;

    public boolean canJump;
    public Rectangle rec;

    public int health;
    public GameScreen screen;

    public Player(World world, GameScreen screen, int x, int y) {
        super(screen.getAtlas().findRegion("IDLEANIMATION"));
        this.screen = screen;

        currentState = State.IDLE;
        previousState = State.IDLE;

        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i < 3; i++) {
            frames.add(new TextureRegion(getTexture(), i * 32, 0, 32,32));
        }
        playerIdle = new Animation(0.1f, frames);

        frames.clear();
        for (int i = 5; i < 7; i++) {
            frames.add(new TextureRegion(getTexture(), i * 32, 0 , 32,32));
        }
        playerRun = new Animation(0.1f, frames);

        playerStanding = new TextureRegion(getTexture(), 0,0, 32,32);
        setBounds(0,0, 64 / PlantShooter.PPM, 64 / PlantShooter.PPM);
        setRegion(playerStanding);
        rec = new Rectangle();
        rec.x = x / PlantShooter.PPM;
        rec.y = y / PlantShooter.PPM;
        rec.width = (64)/ PlantShooter.PPM;
        rec.height = (64) / PlantShooter.PPM;
        this.world = world;
        defineBody(x,y);
    }
    public void defineBody(int x, int y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(new Vector2(x / PlantShooter.PPM,y / PlantShooter.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape() ;

        fdef.filter.categoryBits = screen.CATEGORY_PLAYER;
        fdef.filter.maskBits = screen.CATEGORY_STATICOBJECTS;

        shape.setRadius(9 / PlantShooter.PPM * 2);
        fdef.shape = shape;

        body.createFixture(fdef);
    }
    public void update(float dt) {
        setPosition( (body.getPosition().x - getWidth() / 2) - (1 / PlantShooter.PPM)  + .05f, (body.getPosition().y - getHeight() / 2)  - ( 2 / PlantShooter.PPM) + 0.15f);
        setRegion(getFrame(dt));
        rec.x = getX();
        rec.y = getY();
    }
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch(currentState) {
            case RUNNING:
                region = (TextureRegion) playerRun.getKeyFrame(stateTimer, true);
                break;
            case IDLE:
            default:
                region = (TextureRegion) playerIdle.getKeyFrame(stateTimer, true);
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
        if (body.getLinearVelocity().x != 0) { return State.RUNNING; } else { return State.IDLE; }
    }
}
