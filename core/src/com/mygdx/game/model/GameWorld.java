package com.mygdx.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.G;
import com.mygdx.game.entities.Arena;
import com.mygdx.game.controls.PlayerAWSDController;
import com.mygdx.game.controls.PlayerArrowsController;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.Walker;
import com.mygdx.game.utils.Constants;

public class GameWorld implements ContactListener {

    private Box2DWorld box2DWorld;
    private EntityManager entityManager;

    // Keep game state
    public static enum GameState { WAITING_TO_START, IN_GAME, FINISH };
    private GameState gameState = GameState.WAITING_TO_START;
    public final static int ARENA_X = 100;
    public final static int ARENA_Y = 100;
    public final static int ARENA_WIDTH = 1080;
    public final static int ARENA_HEIGHT = 520;

    public GameWorld() {
        box2DWorld = new Box2DWorld(new Vector2(0, Constants.GRAVITY));

        entityManager = new EntityManager();

        // Pass all collisions through this class
        box2DWorld.getWorld().setContactListener(this);

        initializeObjects();

        // Create players
        Player player = new Player(150, 150, 15, this);
        Player player2 = new Player(150, 150, 15, this);
        entityManager.addEntity(player);
        entityManager.addEntity(player2);

        // Set input processors
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new PlayerArrowsController(player));
        inputMultiplexer.addProcessor(new PlayerAWSDController(player2));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void initializeObjects() {
        // Test arena bounds
        createArena(ARENA_X, ARENA_Y, ARENA_WIDTH, ARENA_HEIGHT);

        for (int i = 0; i < 15; i++) {
            entityManager.addEntity(new Walker(MathUtils.random(100 + 15, 100 + 1080 - 15), MathUtils.random(100 + 15, 100 + 520 - 15), 12, this));
        }
     }

    private void createArena(float x, float y, float width, float height) {
        float w = (G.TARGET_WIDTH - width)/2;
        float h = (G.TARGET_HEIGHT - height)/2;
        entityManager.addEntity(new Arena(0, 0, G.TARGET_WIDTH, h, this));
        entityManager.addEntity(new Arena(0, y + height, G.TARGET_WIDTH, h, this));

        entityManager.addEntity(new Arena(0, y, w, G.TARGET_HEIGHT - h*2, this));
        entityManager.addEntity(new Arena(x + width, y, w, G.TARGET_HEIGHT - h*2, this));
    }

    public void update(float delta) {
        GdxAI.getTimepiece().update(delta);

        // Update physics
        box2DWorld.update(delta);

        // Update entities logic
        entityManager.update(delta);
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
