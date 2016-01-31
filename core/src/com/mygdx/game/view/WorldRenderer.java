package com.mygdx.game.view;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.G;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2015-11-16.
 */
public class WorldRenderer {

    private GameWorld gameWorld;

    private OrthographicCamera cam;
    private OrthographicCamera guiCam;
    private Viewport viewport;
    private Viewport guiViewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private RayHandler rayHandler;

    // Used to scale cam for box2d things without memory allocation
//    private Matrix4 camCombinedBox2D = new Matrix4();

    // Ugly screenshake
    public static float SHAKE_TIME = 0;


    public WorldRenderer() {
        cam = new OrthographicCamera();

        // Let's show more game world when window resized
        viewport = new ExtendViewport(G.VP_WIDTH, G.VP_HEIGHT, cam);

        guiCam = new OrthographicCamera();
        guiViewport = new ScreenViewport(guiCam);

        // Batch used for
        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();

        stage = new Stage(guiViewport, batch);
    }

    public void render(float delta) {
        if(SHAKE_TIME > 0) {
            SHAKE_TIME -= delta;
            cam.position.x = G.VP_WIDTH / 2 + MathUtils.random(-10 * G.INV_SCALE, 10 * G.INV_SCALE);
            cam.position.y = G.VP_HEIGHT / 2 + MathUtils.random(-10 * G.INV_SCALE, 10 * G.INV_SCALE);
            Gdx.input.vibrate((int)(SHAKE_TIME * 1000));
        } else {
            cam.position.x = G.VP_WIDTH / 2;
            cam.position.y = G.VP_HEIGHT / 2;
        }

        // Update camera
        cam.update();
        batch.setProjectionMatrix(cam.combined);

//        camCombinedBox2D.set(cam.combined).scl(Box2DWorld.BOX_TO_WORLD);

        // Clear screen
        Gdx.gl.glClearColor(245 / 255f, 226 / 255f, 215 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Render world
        gameWorld.draw(batch);

        batch.end();
        rayHandler.setCombinedMatrix(cam);
        rayHandler.updateAndRender();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Debug render
        if (G.DEBUG) {
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            gameWorld.drawDebug(shapeRenderer);
            shapeRenderer.end();
        }
        if (G.DEBUG_BOX2D) {
            gameWorld.getBox2DWorld().debugRender(cam);
        }

        stage.act(delta);
        stage.draw();
        batch.setColor(Color.WHITE);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        guiViewport.update(width, height, true);
        rayHandler.resizeFBO(width, height);
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public void dispose() {
        batch.dispose();
    }

    public void setWorld (GameWorld world) {
        this.gameWorld = world;
    }

    public Stage getStage () {
        return stage;
    }

    public void setRayHandler (RayHandler rayHandler) {
        this.rayHandler = rayHandler;
    }
}
