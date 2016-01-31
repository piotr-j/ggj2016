package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class SplashScreen implements Screen {

    private SpriteBatch batch;
    public SplashScreen () {
        batch = new SpriteBatch();
    }

    @Override public void show () {

    }
    private final static float SPLASH_TIME = 1.5f;
    private float timer;
    private Texture logo;
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.4f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (logo == null && G.assets.getManager().isLoaded("pack/ritualball-logo.png", Texture.class)) {
            logo = G.assets.getTexture("pack/ritualball-logo.png");
        }
        if (logo != null) {
            float lw = logo.getWidth() * 0.9f;
            float lh = logo.getHeight() * 0.9f;
            batch.begin();
            batch.draw(logo, Gdx.graphics.getWidth()/2 - lw/2, Gdx.graphics.getHeight()/2 - lh/2, lw, lh);
            batch.end();
        }
        if (G.assets.update() && timer > SPLASH_TIME) {
            G.assets.getManager().unload("pack/ritualball-logo.png");
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
        batch.dispose();
    }
}
