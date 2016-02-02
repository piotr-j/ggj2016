package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev
 * @since 2015-02-11
 */
public class SplashScreen implements Screen {

    private SpriteBatch batch;
    private Texture bg;
    private Texture choose;

    private OrthographicCamera cam;
    private Viewport viewport;

    private int mode = 0;

    public SplashScreen () {
        cam = new OrthographicCamera(G.TARGET_WIDTH, G.TARGET_HEIGHT);
        viewport = new ExtendViewport(G.TARGET_WIDTH, G.TARGET_HEIGHT, cam);

        batch = new SpriteBatch();
        bg = new Texture(Gdx.files.internal("pack/ritualball-title.png"));
        bg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        choose = new Texture(Gdx.files.internal("singleMulti.png"));
        choose.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override public void show () {

    }
    private final static float SPLASH_TIME = 1.5f;
    private float timer;
    private Texture logo;
    private float scale;
    @Override
    public void render(float delta) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClearColor(0.6f, 0.4f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (logo == null && G.assets.getManager().isLoaded("pack/ritualball-logo.png", Texture.class)) {
            logo = G.assets.getTexture("pack/ritualball-logo.png");
        }
        batch.begin();
        batch.draw(bg, 0, 0, G.TARGET_WIDTH, G.TARGET_HEIGHT);
        scale += delta;
        if (logo != null) {
            float lw = logo.getWidth() * (0.5f + MathUtils.sin(scale * 10)/25);
            float lh = logo.getHeight() * (0.5f + MathUtils.sin(scale * 10)/25);
            batch.draw(logo, G.TARGET_WIDTH/2 - lw/2, G.TARGET_HEIGHT - lh * 1.5f, lw, lh);
        }

        // Draw players
        float lw2 = choose.getWidth();
        float lh2 = choose.getHeight();
        batch.draw(choose, G.TARGET_WIDTH / 2 - lw2 / 2, 10, lw2, lh2);

        batch.end();

        if(Gdx.input.justTouched()) {
            if(Gdx.input.getX() < Gdx.graphics.getWidth() / 2) {
                mode = 1;
            } else {
                mode = 2;
            }
        }

        if(G.assets.update() && mode != 0) {
            G.assets.getManager().unload("pack/ritualball-logo.png");
            G.game.setScreen(new GameScreen(mode));
        }

        timer += delta;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        choose.dispose();
    }
}
