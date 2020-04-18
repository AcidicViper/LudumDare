package me.acidviper.plantshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.acidviper.plantshooter.PlantShooter;
import me.acidviper.plantshooter.entities.Bullet;
import me.acidviper.plantshooter.entities.Enemy;
import me.acidviper.plantshooter.entities.PlantEntity;
import me.acidviper.plantshooter.entities.Player;
import me.acidviper.plantshooter.objects.Door;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class GameScreen implements Screen {
    PlantShooter game;

    OrthographicCamera camera;
    FitViewport viewport;

    OrthogonalTiledMapRenderer renderer;
    TmxMapLoader loader;
    TiledMap map;

    World world;

    Box2DDebugRenderer Box2dDebugRenderer;

    Player player;

    private TextureAtlas atlas;
    private TextureAtlas enemyAtlas;

    Body groundBody;

    PlantEntity plant;

    ArrayList<Enemy> enemyArrayList = new ArrayList<>();
    ArrayList<Bullet> bulletArrayList = new ArrayList<>();

    ArrayList<Enemy> enemyToDelete = new ArrayList<>();
    ArrayList<Bullet> bulletToDelete = new ArrayList<>();

    public Door leftDoor = new Door(9.5f * PlantShooter.PPM, 223, 1);
    public Door rightDoor = new Door(13.5f * PlantShooter.PPM, 223, 2);

    boolean touchingLeftDoor = false;
    boolean touchingRightDoor = false;

    long lastEnemySpawn = 0;

    public GameScreen(PlantShooter game) {
        this.game = game;

        atlas = new TextureAtlas("Animations/PlantShooterGame.atlas");
        enemyAtlas = new TextureAtlas("Animations/EnemyPlantShooterGame.atlas");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800 / PlantShooter.PPM, 480 / PlantShooter.PPM);
        viewport = new FitViewport(800 / PlantShooter.PPM, 480 / PlantShooter.PPM, camera);

        loader = new TmxMapLoader();
        map = loader.load("Map Assets/Map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map ,  1 / PlantShooter.PPM);

        world = new World(new Vector2(0,-10), true);
        Box2dDebugRenderer = new Box2DDebugRenderer();

        player = new Player(world,this, (int) (10 * PlantShooter.PPM),400);

        plant = new PlantEntity((int) (12 * PlantShooter.PPM), 200, world);

        defineWorldCollider();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody() == player.body) { if (contact.getFixtureB().getBody() == groundBody) { player.canJump = true;} }
                if (contact.getFixtureB().getBody() == player.body) { if (contact.getFixtureA().getBody() == groundBody) { player.canJump = true;} }


                for (Iterator<Bullet> i = bulletArrayList.iterator(); i.hasNext();) {
                    Bullet bullet = i.next();
                    if (contact.getFixtureA().getBody() == bullet.body) { if (contact.getFixtureB().getBody() == plant.body) {bulletToDelete.add(bullet);}}
                    if (contact.getFixtureB().getBody() == bullet.body) { if (contact.getFixtureA().getBody() == plant.body) {bulletToDelete.add(bullet);}}
                }


                for (Iterator i = enemyArrayList.iterator(); i.hasNext(); ) {
                    Enemy enemy = (Enemy) i.next() ;
                    if (contact.getFixtureA().getBody() == enemy.body && contact.getFixtureB().getBody() == plant.body) { plant.health--;enemyToDelete.add(enemy); }
                    if (contact.getFixtureB().getBody() == enemy.body && contact.getFixtureA().getBody() == plant.body) { plant.health--;enemyToDelete.add(enemy); }

                    if (contact.getFixtureA().getBody() == enemy.body && contact.getFixtureB().getBody() == player.body) { player.body.setLinearVelocity(0,0); player.health--; enemyToDelete.add(enemy);};
                    if (contact.getFixtureB().getBody() == enemy.body && contact.getFixtureA().getBody() == player.body) { player.body.setLinearVelocity(0,0); player.health--; enemyToDelete.add(enemy);};

                        for (Bullet bullet : bulletArrayList) {
                        if (contact.getFixtureA().getBody() == enemy.body && contact.getFixtureB().getBody() ==  bullet.body) {
                                enemy.health--; bulletToDelete.add(bullet);
                        }  else if (contact.getFixtureB().getBody() == enemy.body && contact.getFixtureA().getBody() ==  bullet.body) {
                                enemy.health--; bulletToDelete.add(bullet);
                        }
                    }
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
        //noinspection LibGDXUnsafeIterator
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / PlantShooter.PPM, (rect.getY() + rect.getHeight() / 2) / PlantShooter.PPM);

            groundBody = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2)/ PlantShooter.PPM , (rect.getHeight() / 2) / PlantShooter.PPM);
            def.shape = shape;
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

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

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
        game.batch.end();
    }
    public void update(float dt) {

        if (player.rec.overlaps(leftDoor.rec)) { touchingLeftDoor = true; } else if (player.rec.overlaps(rightDoor.rec)) { touchingRightDoor = true; } else { touchingLeftDoor = false;touchingRightDoor = false; }

        if (touchingLeftDoor && Gdx.input.isKeyJustPressed(Input.Keys.Z)) { player.body.setTransform(new Vector2(rightDoor.getX() + rightDoor.getWidth() / 2f, player.body.getPosition().y),0); }
        if (touchingRightDoor && Gdx.input.isKeyJustPressed(Input.Keys.Z)) { player.body.setTransform(new Vector2(leftDoor.getX() + leftDoor.getWidth() / 2f, player.body.getPosition().y), 0); }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemySpawn >= 1000) {
            lastEnemySpawn = currentTime;
            enemyArrayList.add(new Enemy(25,200,world,this));
        }

        Iterator iter = enemyArrayList.iterator();
        while (iter.hasNext()) {
            Enemy enemy = (Enemy) iter.next();
            if (enemy.health < 1) { iter.remove(); enemyToDelete.add(enemy); }
        }

        player.update(Gdx.graphics.getDeltaTime());
        plant.update(Gdx.graphics.getDeltaTime());
        world.step(1/60f, 6,2);

        for (Bullet bullet : bulletToDelete) {
            world.destroyBody(bullet.body);
        }
        bulletToDelete.clear();
        for (Enemy enemy : enemyToDelete) {
            world.destroyBody(enemy.body);
        }
        enemyToDelete.clear();

        camera.position.x = player.body.getPosition().x;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.canJump) { player.body.applyLinearImpulse(new Vector2(0 , 4), player.body.getWorldCenter(), true ); player.canJump = false;}
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)  && player.body.getLinearVelocity().x <= 2) { player.body.applyLinearImpulse(new Vector2(0.1f , 0), player.body.getWorldCenter(), true ); }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2) { player.body.applyLinearImpulse(new Vector2(-0.1f , 0f), player.body.getWorldCenter(), true ); }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (player.runningRight) {
                bulletArrayList.add(new Bullet((player.getX() + .8f) * PlantShooter.PPM, (player.getY() + .2f) * PlantShooter.PPM, player.runningRight, world));
            } else {
                bulletArrayList.add(new Bullet((player.getX() - .2f) * PlantShooter.PPM, (player.getY() + .2f) * PlantShooter.PPM, player.runningRight, world));
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
    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
}
