package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.view.WorldRenderer;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class GameScreen implements Screen {

    // Logic
    private GameWorld gameWorld;

    // Renderer
    private WorldRenderer renderer;

    /*
	 * Things for Fixed Timestep - look into render for implementation and docs
	 */
    private float fixedTimestepAccumulator = 0f;
    private final float MAX_ACCUMULATED_TIME = 1.0f;
    public static final float TIMESTEP = 1/60f;


    public GameScreen() {
        this.renderer = new WorldRenderer();
        this.gameWorld = new GameWorld(renderer.getStage());

        renderer.setWorld(gameWorld);
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        /*
		 * Implementation of fixed timestep
		 * docs:
		 * - http://gafferongames.com/game-physics/fix-your-timestep/
		 * - http://temporal.pr0.pl/devblog/download/arts/fixed_step/fixed_step.pdf
		 */

        fixedTimestepAccumulator += delta;
        if(fixedTimestepAccumulator > MAX_ACCUMULATED_TIME)
            fixedTimestepAccumulator = MAX_ACCUMULATED_TIME;

        while (fixedTimestepAccumulator >= TIMESTEP) {

			/*
			 * Update physics
			 */
            gameWorld.update(TIMESTEP);
            fixedTimestepAccumulator -= TIMESTEP;
        }

		/*
		 * Render
		 */
        renderer.render(delta);
}

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        renderer.dispose();
    }
}
