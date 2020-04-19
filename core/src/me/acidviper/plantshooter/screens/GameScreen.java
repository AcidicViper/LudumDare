package me.acidviper.plantshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.entities.Bullet;
import me.acidviper.plantshooter.entities.Enemy;
import me.acidviper.plantshooter.entities.PlantEntity;
import me.acidviper.plantshooter.entities.Player;
import me.acidviper.plantshooter.generators.EnemyGenerator;
import me.acidviper.plantshooter.objects.Button;
import me.acidviper.plantshooter.objects.Door;

import java.util.ArrayList;
import java.util.Iterator;
@SuppressWarnings("all")
public class GameScreen implements Screen {

    public final short CATEGORY_PLAYER = 0x0001;  // 0000000000000001 in binary
    public final short CATEGORY_MONSTER = 0x0002; // 0000000000000010 in binary
    public final short CATEGORY_STATICOBJECTS = 0x0003; // Something in binary I bet
    public final short CATEGORY_MONSTERSTATICOBJECTS = 0x0004; // Something in binary I bet
    public final short CATEGORY_BULLETS = 0x0005; // Something in binary I bet

    PlantShooter game;

    OrthographicCamera camera;
    FitViewport viewport;
    FitViewport textViewPort;

    OrthogonalTiledMapRenderer renderer;
    TmxMapLoader loader;
    TiledMap map;

    public World world;

    Box2DDebugRenderer Box2dDebugRenderer;

    Player player;

    private TextureAtlas atlas;
    private TextureAtlas enemyAtlas;

    Body groundBody;
    Body wallBody;

    public PlantEntity plant;

    public ArrayList<Enemy> enemyArrayList = new ArrayList<>();
    ArrayList<Bullet> bulletArrayList = new ArrayList<>();

    ArrayList<Enemy> enemyToDelete = new ArrayList<>();
    ArrayList<Bullet> bulletToDelete = new ArrayList<>();

    public Door leftDoor = new Door(9.5f * PlantShooter.PPM, 223, 1);
    public Door rightDoor = new Door(13.5f * PlantShooter.PPM, 223, 2);

    Sprite leftSpawnDoor;
    Sprite rightSpawnDoor;
    Texture spawnDoorTexture;

    boolean touchingLeftDoor = false;
    boolean touchingRightDoor = false;

    boolean inBuyMenu = false;

    Rectangle rect = new Rectangle();

    EnemyGenerator mobGenerator = new EnemyGenerator(1, 10000, this, false);

    public int goldCount = 0;

    // ALL UI BUTTONS
    //PLANT SHIELD THREE BUTTONS
    Button plantOneButton;
    Button plantTwoButton;
    Button plantThreeButton;
    //Damage power
    Button damageOneButton;
    Button damageTwoButton;
    Button damageThreeButton;
    //Fire Rate
    Button fireRateOneButton;
    Button fireRateTwoButton;
    Button fireRateThreeButton;

    boolean shieldOne;
    boolean shieldTwo;
    boolean shieldThree;

    boolean damageOne;
    boolean damageTwo;
    boolean damageThree;

    boolean fireOne;
    boolean fireTwo;
    boolean fireThree;

    public float damage = 0.5f;
    public long fireRate = 1000;
    public int shield = 0;

    long lastFire;

    Sprite damageOneText;
    Sprite damageTwoText;
    Sprite damageThreeText;
    Sprite shieldOneText;
    Sprite shieldTwoText;
    Sprite shieldThreeText;
    Sprite fireRateOneText;
    Sprite fireRateTwoText;
    Sprite fireRateThreeText;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public GameScreen(PlantShooter game) {
        this.game = game;

        atlas = new TextureAtlas("Animations/PlantShooterGame.atlas");
        enemyAtlas = new TextureAtlas("Animations/EnemyPlantShooterGame.atlas");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280 / PlantShooter.PPM, 720 / PlantShooter.PPM);
        viewport = new FitViewport(1280 / PlantShooter.PPM, 720 / PlantShooter.PPM, camera);
        textViewPort = new FitViewport(200, 100);

        viewport.apply();

        loader = new TmxMapLoader();
        map = loader.load("Map Assets/Map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map ,  1 / PlantShooter.PPM);

        world = new World(new Vector2(0,-10), true);
        Box2dDebugRenderer = new Box2DDebugRenderer();

        player = new Player(world,this, (int) (10 * PlantShooter.PPM),400);

        plant = new PlantEntity((int) (12 * PlantShooter.PPM), 200, world, this);


        plantOneButton = new Button(-2 * PlantShooter.PPM, 300,"Shield 1", camera);
        plantTwoButton = new Button(0 * PlantShooter.PPM, 300,"Shield 2", camera);
        plantThreeButton = new Button(2 * PlantShooter.PPM,  300,"Shield 3", camera);

        damageOneButton = new Button(-2 * PlantShooter.PPM, 195,"Damage 1", camera);
        damageTwoButton = new Button(0 * PlantShooter.PPM, 195,"Damage 2", camera);
        damageThreeButton = new Button(2 * PlantShooter.PPM,  195,"Damage 3", camera);

        fireRateOneButton = new Button(-2 * PlantShooter.PPM, 90,"Fire 1", camera);
        fireRateTwoButton = new Button(0 * PlantShooter.PPM, 90,"Fire 2", camera);
        fireRateThreeButton = new Button(2 * PlantShooter.PPM, 90,"Fire 3", camera);

        spawnDoorTexture = new Texture("Sprites/DarkerDoor.png");

        leftSpawnDoor = new Sprite(spawnDoorTexture);
        rightSpawnDoor = new Sprite(spawnDoorTexture);

        leftSpawnDoor.setBounds(45 / PlantShooter.PPM,223 / PlantShooter.PPM, 41 * 2 / PlantShooter.PPM, 64 * 2 / PlantShooter.PPM);
        rightSpawnDoor.setBounds(1830 / PlantShooter.PPM, 223 / PlantShooter.PPM, 41 * 2 / PlantShooter.PPM, 64 * 2 / PlantShooter.PPM);

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Font.ttf"));
        parameter.size = (int) (12);
        game.font = generator.generateFont(parameter);


         damageOneText = new Sprite();
         damageTwoText = new Sprite();
         damageThreeText = new Sprite();
         shieldOneText = new Sprite();
         shieldTwoText = new Sprite();
         shieldThreeText = new Sprite();
         fireRateOneText = new Sprite();
         fireRateTwoText = new Sprite();
         fireRateThreeText = new Sprite();




        defineWorldCollider();
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                for (Iterator<Bullet> i = bulletArrayList.iterator(); i.hasNext();) {
                    Bullet bullet = i.next();
                    if (contact.getFixtureA().getBody() == bullet.body) { if (contact.getFixtureB().getBody() == plant.body) {bulletToDelete.add(bullet);}}
                    if (contact.getFixtureB().getBody() == bullet.body) { if (contact.getFixtureA().getBody() == plant.body) {bulletToDelete.add(bullet);}}
                }


                for (Iterator i = enemyArrayList.iterator(); i.hasNext(); ) {
                    Enemy enemy = (Enemy) i.next() ;
                    if (contact.getFixtureA().getBody() == enemy.body && contact.getFixtureB().getBody() == plant.body) {
                        if (plant.currentShield > 0) { plant.currentShield--;enemyToDelete.add(enemy); } else { plant.health--; enemyToDelete.add(enemy); }
                    }
                    if (contact.getFixtureB().getBody() == enemy.body && contact.getFixtureA().getBody() == plant.body) {
                        if (plant.currentShield > 0) { plant.currentShield--;enemyToDelete.add(enemy); } else { plant.health--; enemyToDelete.add(enemy); }
                    }

                        for (Bullet bullet : bulletArrayList) {
                        if (contact.getFixtureA().getBody() == enemy.body && contact.getFixtureB().getBody() ==  bullet.body) {
                                enemy.health -= damage; bulletToDelete.add(bullet);
                        }  else if (contact.getFixtureB().getBody() == enemy.body && contact.getFixtureA().getBody() ==  bullet.body) {
                                enemy.health -= damage; bulletToDelete.add(bullet);
                        }
                    }
                }

                for (Iterator<Bullet> i = bulletArrayList.iterator(); i.hasNext();) {
                    Bullet bullet = i.next();
                    if (contact.getFixtureA().getBody() == bullet.body && !bulletToDelete.contains(bullet))  {bulletToDelete.add(bullet);}
                    if (contact.getFixtureB().getBody() == bullet.body && !bulletToDelete.contains(bullet)) {bulletToDelete.add(bullet);}
                }

                enemyArrayList.removeAll(enemyToDelete);
                bulletArrayList.removeAll(bulletToDelete);
            }

            @Override public void endContact(Contact contact) { }
            @Override public void preSolve(Contact contact, Manifold oldManifold) { }
            @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
        });

    }

    public void defineWorldCollider() {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef def = new FixtureDef();
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / PlantShooter.PPM, (rect.getY() + rect.getHeight() / 2) / PlantShooter.PPM);

            wallBody = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2)/ PlantShooter.PPM , (rect.getHeight() / 2) / PlantShooter.PPM);
            def.shape = shape;

            def.filter.categoryBits = CATEGORY_STATICOBJECTS | CATEGORY_MONSTERSTATICOBJECTS;
            def.filter.maskBits = CATEGORY_PLAYER | CATEGORY_MONSTER;

            wallBody.createFixture(def);
        }

        bdef = new BodyDef();
        shape = new PolygonShape();
        def = new FixtureDef();

        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / PlantShooter.PPM, (rect.getY() + rect.getHeight() / 2) / PlantShooter.PPM);

            groundBody = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2)/ PlantShooter.PPM , (rect.getHeight() / 2) / PlantShooter.PPM);
            def.shape = shape;

            def.filter.categoryBits = (short) (CATEGORY_STATICOBJECTS | CATEGORY_MONSTERSTATICOBJECTS);
            def.filter.maskBits = (short) (CATEGORY_PLAYER | CATEGORY_MONSTER);

            groundBody.createFixture(def);
        }
    }


    @Override
    public void render(float delta) {
        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        renderer.setView(camera);
        renderer.render();
        Box2dDebugRenderer.render(world, camera.combined);
        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        leftSpawnDoor.draw(game.batch);
        rightSpawnDoor.draw(game.batch);

        leftDoor.draw(game.batch);
        rightDoor.draw(game.batch);

        player.draw(game.batch);

        plant.draw(game.batch);

        for (Bullet bullet : bulletArrayList) {
            bullet.update(Gdx.graphics.getDeltaTime());
            bullet.draw(game.batch);
        }
        for (Enemy enemy : enemyArrayList) {
            enemy.update(Gdx.graphics.getDeltaTime());
            enemy.draw(game.batch);
        }

        if (inBuyMenu) {
            player.body.setLinearVelocity(0,0);
            Vector3 cameraVec = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            Rectangle mouseRectangle = new Rectangle(cameraVec.x, cameraVec.y, 1 / PlantShooter.PPM, 1 / PlantShooter.PPM);

            ArrayList<Button> buttons = new ArrayList<>();

            // PLANT SHIELD BUTTONS
            plantOneButton.setPosition(((-2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 300 / PlantShooter.PPM);
            plantOneButton.update();

            plantTwoButton.setPosition(((0 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 300 / PlantShooter.PPM);
            plantTwoButton.update();

            plantThreeButton.setPosition(((2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 300 / PlantShooter.PPM);
            plantThreeButton.update();

            //DAMAGE
            damageOneButton.setPosition(((-2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 195 / PlantShooter.PPM);
            damageOneButton.update();

            damageTwoButton.setPosition(((0 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 195 / PlantShooter.PPM);
            damageTwoButton.update();

            damageThreeButton.setPosition(((2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 195 / PlantShooter.PPM);
            damageThreeButton.update();
            // FIRE RATE

            fireRateOneButton.setPosition(((-2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 90 / PlantShooter.PPM);
            fireRateOneButton.update();

            fireRateTwoButton.setPosition(((0 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 90 / PlantShooter.PPM);
            fireRateTwoButton.update();

            fireRateThreeButton.setPosition(((2 * PlantShooter.PPM) / PlantShooter.PPM) + camera.position.x, 90 / PlantShooter.PPM);
            fireRateThreeButton.update();

            plantOneButton.draw(game.batch);
            plantTwoButton.draw(game.batch);
            plantThreeButton.draw(game.batch);

            damageOneButton.draw(game.batch);
            damageTwoButton.draw(game.batch);
            damageThreeButton.draw(game.batch);

            fireRateOneButton.draw(game.batch);
            fireRateTwoButton.draw(game.batch);
            fireRateThreeButton.draw(game.batch);

            game.batch.end();
            textViewPort.apply();
            game.batch.setProjectionMatrix(textViewPort.getCamera().combined);
            game.batch.begin();
            game.font.draw(game.batch, "Test", (camera.position.x), camera.position.y);
            game.batch.end();
            viewport.apply();
            game.batch.setProjectionMatrix(viewport.getCamera().combined);
            game.batch.begin();

            buttons.add(plantOneButton);
            buttons.add(plantTwoButton);
            buttons.add(plantThreeButton);

            buttons.add(damageOneButton);
            buttons.add(damageTwoButton);
            buttons.add(damageThreeButton);

            buttons.add(fireRateOneButton);
            buttons.add(fireRateTwoButton);
            buttons.add(fireRateThreeButton);

            for (Button button : buttons) {
                if(mouseRectangle.overlaps(button.rec)) {
                    if (Gdx.input.justTouched()) {

                        if (button.text.contains("Shield") && button.text.contains("1") && goldCount >= 5) {
                            shieldOne = true;
                            goldCount -= 5;
                        }
                        if (button.text.contains("Shield") && button.text.contains("2")  && goldCount >= 15 && shieldOne) {
                            shieldTwo = true;
                            goldCount -= 15;
                        }
                        if (button.text.contains("Shield") && button.text.contains("3") && goldCount >= 35 && shieldTwo) {
                            shieldThree = true;
                            goldCount -= 35;
                        }



                        if (button.text.contains("Damage") && button.text.contains("1") && goldCount >= 5) {
                            damageOne = true;
                            goldCount -= 5;
                        }
                        if (button.text.contains("Damage") && button.text.contains("2") && goldCount >= 15 && damageOne) {
                            damageTwo = true;
                            goldCount -= 15;
                        }
                        if (button.text.contains("Damage") && button.text.contains("3") && goldCount >= 35 && damageTwo) {
                            damageThree = true;
                            goldCount -= 35;
                        }



                        if (button.text.contains("Fire") && button.text.contains("1") && goldCount >= 5) {
                            fireOne = true;
                            goldCount -= 5;
                        }
                        if (button.text.contains("Fire") && button.text.contains("2") && goldCount >= 15 && fireOne) {
                            fireTwo = true;
                            goldCount -= 15;
                        }
                        if (button.text.contains("Fire") && button.text.contains("3") && goldCount >= 35 && fireTwo) {
                            fireThree = true;
                            goldCount -= 35;
                        }

                    }
                }
            }

        }

        game.batch.end();
    }
    public void update(float dt) {
        long currentTime = System.currentTimeMillis();
        touchingLeftDoor = false; touchingRightDoor = false;
        if (player.rec.overlaps(leftDoor.rec)) { touchingLeftDoor = true; }
        if (player.rec.overlaps(rightDoor.rec)) { touchingRightDoor = true; }

        if (touchingLeftDoor && Gdx.input.isKeyJustPressed(Input.Keys.Z)) { player.body.setTransform(new Vector2(rightDoor.getX() + rightDoor.getWidth() / 2f, player.body.getPosition().y),0); }
        if (touchingRightDoor && Gdx.input.isKeyJustPressed(Input.Keys.Z)) { player.body.setTransform(new Vector2(leftDoor.getX() + leftDoor.getWidth() / 2f, player.body.getPosition().y), 0); }

        if (mobGenerator.inWave && inBuyMenu) { inBuyMenu = false; }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !mobGenerator.inWave) { inBuyMenu = true; }
        Iterator iter = enemyArrayList.iterator();
        while (iter.hasNext()) {
            Enemy enemy = (Enemy) iter.next();
            if (enemy.health < 1) { iter.remove(); goldCount++; enemyToDelete.add(enemy); }
        }

        player.update(Gdx.graphics.getDeltaTime());
        plant.update(Gdx.graphics.getDeltaTime());
        world.step(1/60f, 6,2);
        mobGenerator.update(Gdx.graphics.getDeltaTime());
        for (Bullet bullet : bulletToDelete) {
            world.destroyBody(bullet.body);
        }

        bulletToDelete.clear();

        for (Enemy enemy : enemyToDelete) {
            world.destroyBody(enemy.body);
        }

        enemyToDelete.clear();
        camera.position.x = player.body.getPosition().x;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !inBuyMenu && player.canJump) { player.body.applyLinearImpulse(new Vector2(0 , 4), player.body.getWorldCenter(), true ); player.canJump = false;}
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !inBuyMenu  && player.body.getLinearVelocity().x <= 2) { player.body.applyLinearImpulse(new Vector2(0.1f , 0), player.body.getWorldCenter(), true ); }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !inBuyMenu  && player.body.getLinearVelocity().x >= -2) { player.body.applyLinearImpulse(new Vector2(-0.1f , 0f), player.body.getWorldCenter(), true ); }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (currentTime - lastFire >= fireRate) {
                if (player.runningRight) {
                    bulletArrayList.add(new Bullet((player.getX() + .8f) * PlantShooter.PPM, (player.getY() + .2f) * PlantShooter.PPM, player.runningRight, world, this));
                } else {
                    bulletArrayList.add(new Bullet((player.getX() - .2f) * PlantShooter.PPM, (player.getY() + .2f) * PlantShooter.PPM, player.runningRight, world, this));
                }
                lastFire = currentTime;
            }
        }
    }

    @Override
    public void dispose() {
    }
    public TextureAtlas getAtlas() {
        return atlas;
    }
    public TextureAtlas getEnemyAtlas() {
        return enemyAtlas;
    }
    @Override public void show() { }
    @Override public void resize(int width, int height) { viewport.update(width,height); textViewPort.update(width, height); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
}
