package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class SplashScreen implements Screen {

    public SplashScreen () {
        // TODO splash screen
    }

    @Override public void show () {

    }
    private final static float SPLASH_TIME = .5f;
    private float timer;
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.75f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (G.assets.update() && timer > SPLASH_TIME) {
            G.game.setScreen(new GameScreen());
        }
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

    }
}
