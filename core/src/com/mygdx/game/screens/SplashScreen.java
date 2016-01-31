package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class SplashScreen implements Screen {

    private SpriteBatch batch;
    private Texture bg;
    public SplashScreen () {
        batch = new SpriteBatch();
        bg = new Texture(Gdx.files.internal("pack/ritualball-title.png"));
    }

    @Override public void show () {

    }
    private final static float SPLASH_TIME = 1.5f;
    private float timer;
    private Texture logo;
    private float scale;
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.4f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (logo == null && G.assets.getManager().isLoaded("pack/ritualball-logo.png", Texture.class)) {
            logo = G.assets.getTexture("pack/ritualball-logo.png");
        }
        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scale += delta;
        if (logo != null) {
            float lw = logo.getWidth() * (0.5f + MathUtils.sin(scale * 10)/25);
            float lh = logo.getHeight() * (0.5f + MathUtils.sin(scale * 10)/25);
            batch.draw(logo, Gdx.graphics.getWidth()/2 - lw/2, Gdx.graphics.getHeight()/2 - lh/2, lw, lh);
        }
        batch.end();
        if (G.assets.update() && timer > SPLASH_TIME) {
            G.assets.getManager().unload("pack/ritualball-logo.png");
            G.game.setScreen(new TutorialScreen());
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
        bg.dispose();
    }
}
