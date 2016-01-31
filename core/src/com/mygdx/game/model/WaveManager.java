package com.mygdx.game.model;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Spectator;
import org.omg.CORBA.MARSHAL;

import java.util.Iterator;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class WaveManager {
    public static final ParticleEffectPool pool = new ParticleEffectPool(G.assets.get("pack/konfetti.p", ParticleEffect.class), 6, 18);
    private static float highMin = 0;
    private static float highMax = 0;
    private static float lowMin = 0;
    private static float lowMax = 0;
    static {
        ParticleEffectPool.PooledEffect effect = pool.obtain();
        ParticleEmitter.ScaledNumericValue angle = effect.getEmitters().first().getAngle();
        highMin = angle.getHighMin();
        highMax = angle.getHighMax();
        lowMin = angle.getLowMin();
        lowMax = angle.getLowMax();
    }
    private float waveAngle;
    private boolean isWaving = false;

    private Array<Entity> alreadyJumped = new Array<Entity>();

    private GameWorld gameWorld;

    private Vector2 screenCenter = new Vector2(G.VP_WIDTH / 2, G.VP_HEIGHT / 2);
    private Vector2 tempVec2 = new Vector2();

    public WaveManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }
    private Array<ParticleEffectPool.PooledEffect> effects = new Array<ParticleEffectPool.PooledEffect>();

    public void update(float delta) {
        Iterator<ParticleEffectPool.PooledEffect> it = effects.iterator();
        while (it.hasNext()) {
            ParticleEffectPool.PooledEffect effect = it.next();
            effect.update(delta);
            if (effect.isComplete()) {
                it.remove();
                WaveManager.pool.free(effect);
            }
        }
        if(!isWaving) return;

        waveAngle += delta * 150;

        for(Entity spect : gameWorld.getEntityManager().getEntitiesClass(Spectator.class)) {
            if(alreadyJumped.contains(spect, true)) continue;

            float angle = tempVec2.set(spect.getPosition()).sub(screenCenter).angle();

            if(waveAngle > angle) {
                alreadyJumped.add(spect);
                ((Spectator)spect).jump();
                if (MathUtils.random() > .66f) {
                    float rotation = spect.getRotation();
                    float highMin = WaveManager.highMin + rotation;
                    float highMax = WaveManager.highMax + rotation;
                    float lowMin = WaveManager.lowMin + rotation;
                    float lowMax = WaveManager.lowMax + rotation;
                    ParticleEffectPool.PooledEffect effect = WaveManager.pool.obtain();
                    effect.start();
                    effect.setPosition(spect.getPosition().x, spect.getPosition().y);
                    for (ParticleEmitter emitter : effect.getEmitters()) {
                        ParticleEmitter.ScaledNumericValue ea = emitter.getAngle();

                        ea.setHigh(highMin, highMax);
                        ea.setLow(lowMin, lowMax);
                    }

                    effects.add(effect);
                }
            }
        }

        if(waveAngle >= 360) {
            isWaving = false;
            waveAngle = 0;
        }
    }

    public void makeWave() {
        waveAngle = 0;
        alreadyJumped.clear();
        isWaving = true;
    }

    public void draw (SpriteBatch batch) {
        for (ParticleEffectPool.PooledEffect effect : effects) {
            effect.draw(batch);
        }
    }
}
