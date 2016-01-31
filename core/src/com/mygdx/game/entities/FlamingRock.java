package com.mygdx.game.entities;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;

/**
 * Kills entities!
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class FlamingRock extends Entity implements Pool.Poolable {
    public final static Pool<FlamingRock> pool = new Pool<FlamingRock>() {
        @Override protected FlamingRock newObject () {
            return new FlamingRock();
        }
    };
    private ParticleEffect effect;
    private Vector2 start = new Vector2();
    private Vector2 target = new Vector2();
    private float speed = MathUtils.random(1, 2f);
    private boolean active;
    private float timer;
    private float duration;

    public FlamingRock () {
        super(0, 0, 1, 1);

        ParticleEffect src = G.assets.get("pack/trail.p", ParticleEffect.class);
        effect = new ParticleEffect(src);

    }

    public void init(float x, float y, float tx, float ty) {
        position.set(x, y);
        start.set(position);
        target.set(tx, ty);
        effect.reset();
        duration = target.dst(position) / 6;
        active = true;
        light.setActive(true);
        light.setPosition(x, y);

        for (ParticleEmitter emitter : effect.getEmitters()) {
            emitter.setContinuous(true);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;
        effect.draw(batch);
    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
//        shapeRenderer.setColor(color);
//        shapeRenderer.circle(position.x, position.y, bounds.width/2, 32);
    }

    @Override
    public void update(float delta) {
        if (!active) return;
        timer += delta;
        float a = timer/duration;
        if (a <= 1) {
            position.x = Interpolation.linear.apply(start.x, target.x, a);
            position.y = Interpolation.linear.apply(start.y, target.y, a) + ((1+MathUtils.sinDeg(a * 180))*2);
            light.setDistance(1.5f + MathUtils.random(-.25f, .25f));
        } else {
            effect.allowCompletion();
            for (ParticleEmitter emitter : effect.getEmitters()) {
                emitter.setContinuous(false);
            }
            light.setDistance(light.getDistance() * 0.95f);
        }
        light.setPosition(position);
        effect.setPosition(position.x, position.y);
        effect.update(delta);
        if (effect.isComplete()) {
            Gdx.app.log("", "reset");
            FlamingRock.pool.free(this);
        }
    }


    @Override
    public void dispose() {

    }

    @Override public void reset () {
        light.setPosition(-100, -100);
        light.setActive(false);
        active = false;
        timer = 0;
    }

    private boolean added;
    private PointLight light;
    public void addToEngine (GameWorld world) {
        world.getEntityManager().addEntity(this);
        light = new PointLight(world.getRayHandler(), 16, Color.ORANGE, 1.5f, position.x, position.y);
        added = true;
    }

    public boolean addedToEngine () {
        return added;
    }
}
