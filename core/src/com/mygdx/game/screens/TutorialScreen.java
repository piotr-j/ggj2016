package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class TutorialScreen implements Screen {

    private SpriteBatch batch;
    private Texture tut1;
    private Texture tut2;
    private Texture tut3;
    private Texture tut4;

    public TutorialScreen () {
        batch = new SpriteBatch();
        tut1 = new Texture(Gdx.files.internal("tut/1.jpg"));
    }

    @Override public void show () {

    }
    private final static float TUT_TIME = 10f/4f;
    private final static float TRANSITION_TIME = .5f;
    private float timer;
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.4f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        batch.begin();
        if (timer < TUT_TIME) {
            // TUT 1
            batch.draw(tut1, 0, 0, width, height);
        } else if (timer < TUT_TIME + TRANSITION_TIME && timer > TUT_TIME) {
            if (tut2 == null)
                tut2 = new Texture(Gdx.files.internal("tut/2.jpg"));
            // TRANS 1
            float offset = ((timer - TUT_TIME)/TRANSITION_TIME) * width;
            batch.draw(tut1, - offset, 0, width, height);
            batch.draw(tut2, width -offset, 0, width, height);
        } else if (timer < TUT_TIME * 2 + TRANSITION_TIME && timer > TUT_TIME + TRANSITION_TIME) {
            // TUT 2
            batch.draw(tut2, 0, 0, width, height);
        } else if (timer < TUT_TIME * 2+ TRANSITION_TIME * 2 && timer > TUT_TIME * 2+ TRANSITION_TIME) {
            if (tut3 == null)
                tut3 = new Texture(Gdx.files.internal("tut/2.jpg"));
            // TRANS 2
            float offset = ((timer - (TUT_TIME * 2 + TRANSITION_TIME))/TRANSITION_TIME) * width;
            batch.draw(tut2, - offset, 0, width, height);
            batch.draw(tut3, width - offset, 0, width, height);
        } else if (timer < TUT_TIME * 3 + TRANSITION_TIME * 2 && timer > TUT_TIME * 2+ TRANSITION_TIME * 2) {
            // TUT 3
            batch.draw(tut3, 0, 0, width, height);
        } else if (timer < TUT_TIME * 3 + TRANSITION_TIME * 3 && timer > TUT_TIME * 3 + TRANSITION_TIME * 2) {
            if (tut4 == null)
                tut4 = new Texture(Gdx.files.internal("tut/2.jpg"));
            // TRANS 3
            float offset = ((timer - (TUT_TIME * 3 + TRANSITION_TIME * 2))/TRANSITION_TIME) * width;
            batch.draw(tut3, - offset, 0, width, height);
            batch.draw(tut4, width - offset, 0, width, height);
        } else if (timer < TUT_TIME * 4 + TRANSITION_TIME * 3 && timer > TUT_TIME * 3+ TRANSITION_TIME * 3) {
            // TUT 4
            batch.draw(tut4, 0, 0, width, height);
        } else {
            G.game.setScreen(new GameScreen());
        }
        batch.end();
        timer += delta;
    }

    @Override
    public void resize(int width, int height) {

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
        batch.dispose();
        tut1.dispose();
        tut2.dispose();
        tut3.dispose();
    }
}
