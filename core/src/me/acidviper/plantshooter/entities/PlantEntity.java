package me.acidviper.plantshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.screens.GameOverScreen;
import me.acidviper.plantshooter.screens.GameScreen;

public class PlantEntity extends Sprite {
    public Body body;
    public int health = 15;
    public int currentShield;

    TextureRegion plantIdle;
    GameScreen screen;
    public PlantEntity(int x, int y, World world, GameScreen screen) {
        this.screen = screen;
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

        fdef.filter.categoryBits = (short) (screen.CATEGORY_STATICOBJECTS | screen.CATEGORY_MONSTERSTATICOBJECTS);
        fdef.filter.maskBits = (short) (screen.CATEGORY_PLAYER | screen.CATEGORY_MONSTER | screen.CATEGORY_BULLETS);

        body.createFixture(fdef);
        setPosition( (body.getPosition().x - getWidth() / 2), (body.getPosition().y - getHeight() / 2));

    }
    public void update(float dt) {
        setPosition( (body.getPosition().x - getWidth() / 2) - (1 / PlantShooter.PPM)  + .05f, (body.getPosition().y - getHeight() / 2)  - ( 2 / PlantShooter.PPM) + 1.5f);

        if (health < 1) {
            screen.game.setScreen(new GameOverScreen(screen.game, screen.mobGenerator.waveNumber));
        }

        if (health < 12) {
            setTexture(new Texture("Sprites/PlantSecondPhase.png"));
            plantIdle = new TextureRegion(getTexture(), 0,0, 124,124);
            setRegion(plantIdle);
        }

        if (health < 6) {
            setTexture(new Texture("Sprites/ThirdPhasePlantSprite.png"));
            plantIdle = new TextureRegion(getTexture(), 0,0, 124,124);
            setRegion(plantIdle);
        }

        if (health < 3) {
            setTexture(new Texture("Sprites/FinalPlantStage.png"));
            plantIdle = new TextureRegion(getTexture(), 0,0, 124,124);
            setRegion(plantIdle);
        }
    }
}
