package com.mygdx.game.model;

import aurelienribon.tweenengine.TweenManager;
import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.G;
import com.mygdx.game.controls.*;
import com.mygdx.game.entities.Arena;
import com.mygdx.game.entities.*;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.FancyTextSpawner;
import com.mygdx.game.view.ScoreDisplay;

public class GameWorld implements ContactListener {
    public final static int SCORE_TO_WIN = 10;

    private Box2DWorld box2DWorld;

    // Managers
    private EntityManager entityManager;
    private EntityManager messageEntManager;
    private GodsWillManager godManager;
    private TweenManager tweenManager;
    private WaveManager waveManager;

    private ScoreDisplay scoreDisplay;

    // Keep game state
    private Array<Player> players;
    private Stage stage;
    private RayHandler rayHandler;

    public RayHandler getRayHandler () {
        return rayHandler;
    }

    public static enum GameState { WAITING_TO_START, IN_GAME, FINISH };

    private GameState gameState = GameState.WAITING_TO_START;
    public final static float ARENA_X = 2.5f;
    public final static float ARENA_Y = 2.5f;
    public final static float ARENA_WIDTH = G.VP_WIDTH - 5;
    public final static float ARENA_HEIGHT = G.VP_HEIGHT - 5;
    public final static int TEAM_1 = 1;
    public final static int TEAM_2 = 2;
    public final static float SPAWN_X_OFFSET = 7;
    public final static float SPAWN_SPREAD_X = 1;
    public final static float SPAWN_SPREAD_Y = 1;
    private Stack root = new Stack();
    private Table scores;
    private Stack fade;
    private Image tint;
    private Label whoWon;
    private Label toRestart;

    public GameWorld (Stage stage, RayHandler rayHandler) {
        this.stage = stage;
        this.rayHandler = rayHandler;

        Music music = G.assets.get(G.A.MUSIC, Music.class);
        music.setLooping(true);
        music.setVolume(0.6f);
        music.play();

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = G.assets.get(G.A.FONT_UNIVERSIDAD, BitmapFont.class);

        Skin skin = G.assets.get("pack/uiskin.json", Skin.class);
        fade = new Stack();
        fade.setFillParent(true);
        tint = new Image(G.assets.getAtlasRegion("pixel", "pack/entities.atlas"));
        tint.setColor(Color.BLACK);
        tint.getColor().a = 0.5f;
        Image logo = new Image(G.assets.getAtlasRegion("ritualball-logo", "pack/entities.atlas"));
        tint.setFillParent(true);
        fade.add(tint);
        Table texts = new Table();
        texts.add(logo);
        texts.row();
        toRestart = new Label("Restart in 0", style);
        texts.add(toRestart).padTop(300);
        texts.row();
        fade.add(texts);
        logo.setScaling(Scaling.fillX);
        box2DWorld = new Box2DWorld(new Vector2(0, Constants.GRAVITY));

        entityManager = new EntityManager();
        messageEntManager = new EntityManager();
        godManager = new GodsWillManager(this);
        waveManager = new WaveManager(this);
        tweenManager = new TweenManager();

        // Pass all collisions through this class
        box2DWorld.getWorld().setContactListener(this);

        initializeObjects();

        float cx = G.VP_WIDTH / 2;
        float cy = G.VP_HEIGHT / 2;
        int playersPerTeam = 4;
        // Team 1
        PlayerController controller1 = new PlayerController();
        for (int i = 0; i < playersPerTeam; i++) {
            Player player = new Player(cx + SPAWN_X_OFFSET + MathUtils.random(-SPAWN_SPREAD_X, SPAWN_SPREAD_X), cy + MathUtils.random(-SPAWN_SPREAD_Y, SPAWN_SPREAD_Y), .3f, controller1, this, Color.RED, TEAM_1);
            entityManager.addEntity(player);
        }

        // Team 2
        PlayerController controller2 = new PlayerController();
        for (int i = 0; i < playersPerTeam; i++) {
            Player player = new Player(cx - SPAWN_X_OFFSET + MathUtils.random(-SPAWN_SPREAD_X, SPAWN_SPREAD_X), cy + MathUtils.random(-SPAWN_SPREAD_Y, SPAWN_SPREAD_Y), .3f, controller2, this, Color.BLUE, TEAM_2);
            entityManager.addEntity(player);
        }

        root.setFillParent(true);
        stage.addActor(root);

        scores = new Table();
        scores.setFillParent(true);
        root.addActor(scores);
        scores.align(Align.top);


        scoreDisplay = new ScoreDisplay();
        scores.add(scoreDisplay);


        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Table container1 = new Table();
            Table rootContainer = new Table();
            root.addActor(rootContainer);
            rootContainer.setFillParent(true);
            rootContainer.add(container1).fill().expand();
            Table container2 = new Table();
            rootContainer.add(container2).fill().expand();
//            stage.setDebugAll(true);
            // refs are in the stage crap
            new PlayerStageController(controller2, container1, skin, false);
            new PlayerStageController(controller1, container2, skin, true);
            inputMultiplexer.addProcessor(stage);
        } else {
            // Set input processors
            inputMultiplexer.addProcessor(new PlayerArrowsController(controller1));
            inputMultiplexer.addProcessor(new PlayerAWSDController(controller2));

            for (int i = 0; i < Controllers.getControllers().size; i++) {
                if (i == 0) {
                    Controllers.getControllers().get(i).addListener(new PlayerGamepadController(controller1));
                } else if (i == 1) {
                    Controllers.getControllers().get(i).addListener(new PlayerGamepadController(controller2));
                }
            }
        }
        Gdx.input.setInputProcessor(inputMultiplexer);

        for (int i = 0; i < 5; i++) {
            float x = G.VP_WIDTH / 10 + G.VP_WIDTH / 5 * i;
            float hOffset = i % 2 != 0?2:0;
            float angleOffset = ((i-2)%3) * 5;
            float midOffset = ((i)%3)*2;
            coneLights.add(new ConeLight(rayHandler, 16, Color.WHITE, 14 + midOffset, x, G.VP_HEIGHT + 4, -90 - angleOffset, 20));
            coneLights.add(new ConeLight(rayHandler, 16, Color.WHITE, 14 + midOffset, x, -4, 90 + angleOffset, 20));
        }

        gameState = GameState.IN_GAME;
    }

    private Array<ConeLight> coneLights = new Array<ConeLight>();

    private int team1Score;
    private int team2Score;
    public void teamScored (int team) {
        if (team == TEAM_1) {
            team1Score++;

            if(team1Score == SCORE_TO_WIN) {
                FancyTextSpawner.spawnText("TEAM " + team, G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 1.3f, this, Color.valueOf("60a7fc"), 10);
                FancyTextSpawner.spawnText("WON!", G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 2.5f - 1.3f, this, Color.valueOf("60a7fc"), 10);
            } else {
                FancyTextSpawner.spawnText("TEAM " + team, G.VP_WIDTH / 2, G.VP_HEIGHT / 2, this, Color.valueOf("60a7fc"), 0);
                FancyTextSpawner.spawnText("SCORED!", G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 2.5f, this, Color.valueOf("60a7fc"), 0);
            }
        } else if (team == TEAM_2) {
            team2Score++;

            if(team2Score == SCORE_TO_WIN) {
                FancyTextSpawner.spawnText("TEAM " + team, G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 1.3f, this, Color.valueOf("fc3552"), 10);
                FancyTextSpawner.spawnText("WON!", G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 2.5f - 1.3f, this, Color.valueOf("fc3552"), 10);
            } else {
                FancyTextSpawner.spawnText("TEAM " + team, G.VP_WIDTH / 2, G.VP_HEIGHT / 2, this, Color.valueOf("fc3552"), 0);
                FancyTextSpawner.spawnText("SCORED!", G.VP_WIDTH / 2, G.VP_HEIGHT / 2 - 2.5f, this, Color.valueOf("fc3552"), 0);
            }
        }

        scoreDisplay.updateScore(this);
        waveManager.makeWave();
        if (team1Score >= SCORE_TO_WIN) {
            gameState = GameState.FINISH;
            whoWon.setText("Team 1 WON!");
            togglePlayerControl(false);
            fade.getColor().a = 0;
            stage.addActor(fade);
            fade.addAction(Actions.fadeIn(0.5f));
//            tint.setColor(0, 0, 1, .5f);
            stage.addActor(scores);
            Gdx.app.log("", "Team 1 won!");
        } else if (team2Score >= SCORE_TO_WIN) {
            gameState = GameState.FINISH;
//            tint.setColor(1, 0, 0, .5f);
            togglePlayerControl(false);
            fade.getColor().a = 0;
            stage.addActor(fade);
            fade.addAction(Actions.fadeIn(0.5f));
            stage.addActor(scores);
            Gdx.app.log("", "Team 2 won!");
        }

//        G.assets.get(G.A.SOUND_GOL, Sound.class).play();
    }

    public void initializeObjects() {
        // Test arena bounds
        createArena(ARENA_X, ARENA_Y, ARENA_WIDTH, ARENA_HEIGHT);

        // Flames!
        Flame flame1 = new Flame(2.5F, G.VP_HEIGHT / 2, 1, this, Color.ORANGE, TEAM_2);
        entityManager.addEntity(flame1);

        Flame flame2 = new Flame(G.VP_WIDTH - 2.5f, G.VP_HEIGHT / 2, 1, this, Color.ORANGE, TEAM_1);
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


        generateAudience();
    }

    private void togglePlayerControl(boolean enabled) {
        Array<Entity> players = entityManager.getEntitiesClass(Player.class);
        for (Entity entity : players) {
            Player player = (Player)entity;
            player.acceptInput(enabled);
            if (!enabled) {
                player.timeout();
            }
        }
    }

    private void generateAudience() {

        // Down side
        for(int i = 0; i < 40; i++) {
            Spectator spect = new Spectator(MathUtils.random(0, G.VP_WIDTH), MathUtils.random(0, ARENA_Y), .3f, this);
            entityManager.addEntity(spect);
        }

        // Up side
        for(int i = 0; i < 40; i++) {
            Spectator spect = new Spectator(MathUtils.random(0, G.VP_WIDTH), MathUtils.random(ARENA_X + ARENA_HEIGHT, G.VP_HEIGHT), .3f, this);
            entityManager.addEntity(spect);
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

        entityManager.addEntity(new Tribunes(0, y, w, G.VP_HEIGHT - h * 2, this,
                G.assets.getAtlasRegion(G.A.TRIBUNES_LEFT, G.A.ATLAS)));
        entityManager.addEntity(new Tribunes(x + width, y, w, G.VP_HEIGHT - h * 2, this,
                G.assets.getAtlasRegion(G.A.TRIBUNES_RIGHT, G.A.ATLAS)));
    }

    private float toRestartDuration = 11;
    private float winTimer = 0;
    public void update(float delta) {
        GdxAI.getTimepiece().update(delta);

        // Update physics
        box2DWorld.update(delta);

        entityManager.update(delta);
        messageEntManager.update(delta);

        godManager.update(delta);

        tweenManager.update(delta);

        waveManager.update(delta);

        if(gameState == GameState.FINISH) {
            winTimer += delta;
            int restart = (int)((toRestartDuration - winTimer) * 100);
            toRestart.setText("Restart in " + (restart/100)+ "!");
            if (winTimer > toRestartDuration) {
                winTimer = 0;
                restartGame();
                fade.addAction(Actions.fadeOut(0.5f));
                gameState = GameState.IN_GAME;
            }
        }

        for (Light light : lightsToDispose) {
            light.remove();
        }
        lightsToDispose.clear();
    }

    private void restartGame () {
        togglePlayerControl(true);
        team1Score = 0;
        team2Score = 0;
        scoreDisplay.updateScore(this);

        int size = entityManager.getEntitiesClass(Walker.class).size;
        for (int i = 0; i < 40 - size; i++) {
            float x = MathUtils.random(100 + 15, 100 + 1080 - 15) * G.INV_SCALE;
            float y = MathUtils.random(100 + 15, 100 + 520 - 15) * G.INV_SCALE;
            entityManager.addEntity(new Walker(x, y, .25f, this, Color.YELLOW));
        }
    }

    public void draw(SpriteBatch batch) {
//        entityManager.draw(batch);
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player || entity instanceof Sacrifice) continue;
            entity.draw(batch);
        }

        waveManager.draw(batch);
    }

    public void drawAfterLights(SpriteBatch batch) {
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player || entity instanceof Sacrifice) {
                entity.draw(batch);
            }
        }
        godManager.draw(batch);
    }

    public void drawAfterStage(SpriteBatch batch) {
        messageEntManager.draw(batch);
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

    public TweenManager getTweenManager() {
        return tweenManager;
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

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public Stage getStage () {
        return stage;
    }

    public void setRayHandler (RayHandler rayHandler) {
        this.rayHandler = rayHandler;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    private Array<Light> lightsToDispose = new Array<Light>();
    public void queueLightDispose (Light light) {
        if (!lightsToDispose.contains(light, true)) {
            lightsToDispose.add(light);
        }
    }

    public EntityManager getMessageEntManager() {
        return messageEntManager;
    }
}
