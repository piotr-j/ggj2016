package com.mygdx.game.model;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Sacrifice;
import com.mygdx.game.entities.Walker;
import com.mygdx.game.utils.SpriteTween;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class GodsWillManager {

    public GameWorld gameWorld;

    public boolean needSacrifice = false;

    private Entity target;

    private Sprite ray;
    private Sprite rayCircle;

    public GodsWillManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;

        ray = G.assets.getAtlas(G.A.ATLAS).createSprite(G.A.RAY);
        ray.setOrigin(ray.getRegionWidth() / 2 * G.INV_SCALE, ray.getRegionHeight() / 2 * G.INV_SCALE);
        ray.setSize(ray.getRegionWidth() * G.INV_SCALE, ray.getRegionHeight() * G.INV_SCALE);

        rayCircle = G.assets.getAtlas(G.A.ATLAS).createSprite(G.A.RAY_CIRCLE);
        rayCircle.setOrigin(rayCircle.getRegionWidth() / 2 * G.INV_SCALE, rayCircle.getRegionHeight() / 2 * G.INV_SCALE);
        rayCircle.setSize(rayCircle.getRegionWidth() * G.INV_SCALE, rayCircle.getRegionHeight() * G.INV_SCALE);
    }

    public void update(float delta) {

        // Nominate new sacrifice
        if(needSacrifice) {
            Array<Entity> walkers = gameWorld.getEntityManager().getEntitiesClass(Walker.class);

            // No walkers left!
            if(walkers.size <= 0) return;

            // Choose random one
            Entity walker = walkers.random();

            // Replace with sacrifice
            Entity newSacrifice = new Sacrifice(walker.getPosition().x, walker.getPosition().y, 15 * G.INV_SCALE, gameWorld, Color.GREEN);
            gameWorld.getEntityManager().addEntity(newSacrifice);
            gameWorld.getEntityManager().removeEntity(walker);

            // Set target to draw god rays
            target = newSacrifice;
            tweenWill();

            needSacrifice = false;
        }

        // Check if sacrifice alive
        if(gameWorld.getEntityManager().getEntitiesClass(Sacrifice.class).size <= 0) {
            needSacrifice = true;
        }

    }

    private void tweenWill() {
        rayCircle.setAlpha(0.0f);
        ray.setAlpha(0.0f);
        ray.setScale(0f, 0);
        rayCircle.setScale(0.2f, 0.2f);

        Timeline.createSequence()
                .beginParallel()
                    // Ray
                    .push(Tween.to(ray, SpriteTween.SCALEX, 1.0f).target(1).ease(TweenEquations.easeOutBack))
                    .push(Tween.to(ray, SpriteTween.SCALEY, 0.5f).target(1).ease(TweenEquations.easeOutBack))
                    .push(Tween.to(ray, SpriteTween.ALPHA, 0.5f).target(0.4f).ease(TweenEquations.easeInOutCubic))
                    // Circle
                    .push(Tween.to(rayCircle, SpriteTween.SCALEX, 1f).target(1).ease(TweenEquations.easeOutBack))
                    .push(Tween.to(rayCircle, SpriteTween.SCALEY, 1f).target(1).ease(TweenEquations.easeOutBack))
                    .push(Tween.to(rayCircle, SpriteTween.ALPHA, 0.5f).target(0.4f).ease(TweenEquations.easeInOutCubic))
                .end()
                .beginParallel()
                        // Ray
                    .push(Tween.to(ray, SpriteTween.ALPHA, 0.7f).target(0).ease(TweenEquations.easeInOutCubic))
                        // Circle
                    .push(Tween.to(rayCircle, SpriteTween.ALPHA, 0.7f).target(0).ease(TweenEquations.easeInOutCubic))
                .end()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        target = null;
                    }
                })
                .start(gameWorld.getTweenManager());
    }

    public void draw(SpriteBatch batch) {
        if(target != null) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

            ray.setPosition(target.getPosition().x - ray.getWidth() / 2, target.getPosition().y - 1);
            ray.draw(batch);

            rayCircle.setPosition(target.getPosition().x - rayCircle.getWidth() / 2, target.getPosition().y - rayCircle.getHeight() / 2);
            rayCircle.draw(batch);

            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

    }
}
