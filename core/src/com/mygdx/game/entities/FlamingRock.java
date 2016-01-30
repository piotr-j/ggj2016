package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.mygdx.game.G;
import com.mygdx.game.model.EntityManager;

/**
 * Kills entities!
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class FlamingRock extends Entity implements Pool.Poolable {
    public final static Pool<FlamingRock> pool = new ReflectionPool<FlamingRock>(FlamingRock.class);
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
        } else {
            effect.allowCompletion();
        }
        effect.setPosition(position.x, position.y);
        effect.update(delta);
        if (effect.isComplete()) {
            FlamingRock.pool.free(this);
        }
    }


    @Override
    public void dispose() {

    }

    @Override public void reset () {
        active = false;
        timer = 0;
    }

    private boolean added;
    public void addToEngine (EntityManager entityManager) {
        entityManager.addEntity(this);
        added = true;
    }

    public boolean addedToEngine () {
        return added;
    }
}
