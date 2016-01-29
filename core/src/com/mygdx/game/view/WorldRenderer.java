package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Used to scale cam for box2d things without memory allocation
//    private Matrix4 camCombinedBox2D = new Matrix4();


    public WorldRenderer(GameWorld gameWorld) {
        this.gameWorld = gameWorld;

        cam = new OrthographicCamera();

        // Let's show more game world when window resized
        viewport = new ExtendViewport(G.VP_WIDTH, G.VP_HEIGHT, cam);

        // Batch used for
        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();
    }

    public void render(float delta) {
        // Update camera
        cam.update();
        batch.setProjectionMatrix(cam.combined);

//        camCombinedBox2D.set(cam.combined).scl(Box2DWorld.BOX_TO_WORLD);

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Render world
        gameWorld.draw(batch);

        batch.end();

        // Debug render
        if (G.DEBUG) {
            gameWorld.getBox2DWorld().debugRender(cam);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            gameWorld.drawDebug(shapeRenderer);
            shapeRenderer.end();
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public void dispose() {
        batch.dispose();
    }
}
