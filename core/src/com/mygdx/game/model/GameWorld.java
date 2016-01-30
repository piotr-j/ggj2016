package com.mygdx.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.controls.PlayerController;
import com.mygdx.game.controls.PlayerGamepadController;
import com.mygdx.game.entities.Arena;
import com.mygdx.game.entities.*;
import com.mygdx.game.controls.PlayerAWSDController;
import com.mygdx.game.controls.PlayerArrowsController;
import com.mygdx.game.utils.Constants;

public class GameWorld implements ContactListener {

    private Box2DWorld box2DWorld;

    // Managers
    private EntityManager entityManager;
    private GodsWillManager godManager;

    // Keep game state
    private Array<Player> players;
    public static enum GameState { WAITING_TO_START, IN_GAME, FINISH };

    private GameState gameState = GameState.WAITING_TO_START;
    public final static float ARENA_X = 2.5f;
    public final static float ARENA_Y = 2.5f;
    public final static float ARENA_WIDTH = G.VP_WIDTH - 5;
    public final static float ARENA_HEIGHT = G.VP_HEIGHT - 5;
    public final static int TEAM_1 = 1;
    public final static int TEAM_2 = 2;

    public GameWorld() {
        box2DWorld = new Box2DWorld(new Vector2(0, Constants.GRAVITY));

        entityManager = new EntityManager();
        godManager = new GodsWillManager(this);

        // Pass all collisions through this class
        box2DWorld.getWorld().setContactListener(this);

        initializeObjects();

        float cx = G.VP_WIDTH / 2;
        float cy = G.VP_HEIGHT / 2;
        float offset = 7;
        float spreadX = 1;
        float spreadY = 1;
        int playersPerTeam = 4;
        // Team 1
        PlayerController controller1 = new PlayerController();
        for (int i = 0; i < playersPerTeam; i++) {
            Player player = new Player(cx + offset + MathUtils.random(-spreadX, spreadX), cy + MathUtils.random(-spreadY, spreadY), .3f, controller1, this, Color.BLUE, TEAM_1);
            entityManager.addEntity(player);
        }

        // Team 2
        PlayerController controller2 = new PlayerController();
        for (int i = 0; i < playersPerTeam; i++) {
            Player player = new Player(cx - offset + MathUtils.random(-spreadX, spreadX), cy + MathUtils.random(-spreadY, spreadY), .3f, controller2, this, Color.RED, TEAM_2);
            entityManager.addEntity(player);
        }

        // Set input processors
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new PlayerArrowsController(controller1));
        inputMultiplexer.addProcessor(new PlayerAWSDController(controller2));
        Gdx.input.setInputProcessor(inputMultiplexer);

        for(int i = 0; i < Controllers.getControllers().size; i++) {
            if(i == 0) {
                Controllers.getControllers().get(i).addListener(new PlayerGamepadController(controller1));
            } else if(i == 1) {
                Controllers.getControllers().get(i).addListener(new PlayerGamepadController(controller2));
            }
        }
    }

    public void initializeObjects() {
        // Test arena bounds
        createArena(ARENA_X, ARENA_Y, ARENA_WIDTH, ARENA_HEIGHT);

        // Flames!
        Flame flame = new Flame(3, G.VP_HEIGHT / 2, 1, this, Color.ORANGE, TEAM_2);
        entityManager.addEntity(flame);

        Flame flame2 = new Flame(G.VP_WIDTH - 3, G.VP_HEIGHT / 2, 1, this, Color.ORANGE, TEAM_1);
        entityManager.addEntity(flame2);

        // Test sacrifice
//        Sacrifice sacrifice = new Sacrifice(G.VP_WIDTH / 2, G.VP_HEIGHT / 2, 15 * G.INV_SCALE, this, Color.GREEN);
//        entityManager.addEntity(sacrifice);

        // Some walkers
        for (int i = 0; i < 40; i++) {
            float x = MathUtils.random(100 + 15, 100 + 1080 - 15) * G.INV_SCALE;
            float y = MathUtils.random(100 + 15, 100 + 520 - 15) * G.INV_SCALE;
            entityManager.addEntity(new Walker(x, y, .25f, this, Color.YELLOW));
        }
     }

    private void createArena(float x, float y, float width, float height) {
        float w = (G.VP_WIDTH - width)/2;
        float h = (G.VP_HEIGHT - height)/2;
        entityManager.addEntity(new Arena(x, y, width, height, this));

        entityManager.addEntity(new Tribunes(0, 0, G.VP_WIDTH, h, this,
            G.assets.getAtlasRegion(G.A.TRIBUNES_BOT, G.A.ATLAS)));
        entityManager.addEntity(new Tribunes(0, y + height, G.VP_WIDTH, h, this,
            G.assets.getAtlasRegion(G.A.TRIBUNES_TOP, G.A.ATLAS)));

        entityManager.addEntity(new Tribunes(0, y, w, G.VP_HEIGHT - h*2, this,
            G.assets.getAtlasRegion(G.A.TRIBUNES_LEFT, G.A.ATLAS)));
        entityManager.addEntity(new Tribunes(x + width, y, w, G.VP_HEIGHT - h*2, this,
            G.assets.getAtlasRegion(G.A.TRIBUNES_RIGHT, G.A.ATLAS)));
    }

    public void update(float delta) {
        GdxAI.getTimepiece().update(delta);

        // Update physics
        box2DWorld.update(delta);

        // Update entities logic
        entityManager.update(delta);

        godManager.update(delta);
    }

    public void draw(SpriteBatch batch) {
        entityManager.draw(batch);
    }

    public void drawDebug (ShapeRenderer shapeRenderer) {
        entityManager.drawDebug(shapeRenderer);
    }

    public void resetGame() {
        entityManager.reset();
    }

    @Override
    public void beginContact(Contact contact) {
        Object ent1 = contact.getFixtureA().getBody().getUserData();
        Object ent2 = contact.getFixtureB().getBody().getUserData();

        if(!(ent1 instanceof PhysicsObject) || !(ent2 instanceof PhysicsObject)) {
            return;
        }

        PhysicsObject physo1 = (PhysicsObject)ent1;
        PhysicsObject physo2 = (PhysicsObject)ent2;

        physo1.handleBeginContact(physo2, this);
        physo2.handleBeginContact(physo1, this);
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Box2DWorld getBox2DWorld() {
        return box2DWorld;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void dispose() {
        entityManager.dispose();
    }
}
