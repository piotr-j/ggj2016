package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class Spectator extends Entity {

    // Animation
    private float animTime;
    private Animation animIdle;
    private Animation animClap;
    private Animation animJump;
    private TextureRegion animFrame;

    private GameWorld gameWorld;

    // Random cheer
    private float nextCheerTime = 1;

    // Movement
    private Vector2 posOffset = new Vector2();
    private int offsetDirection = -1;
    private float offsetSpeed = MathUtils.random(4, 6);

    // Temp
    private Vector2 tempVec2 = new Vector2();

    // Jump
    private boolean isJumping = false;
    private float jumpTime;
    private float jumpScale = 0;

    private Color color;

    public Spectator(float x, float y, float radius, GameWorld gameWorld) {
        super(x, y, radius * 2, radius * 2);
        this.gameWorld = gameWorld;

        // Animation
        this.animTime = MathUtils.random(0, 2f);
        this.animIdle = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.SPECT_DANCE));
        this.animClap = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.SPECT_CLAP));
        this.animJump = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.SPECT_JUMP));
        this.animIdle.setPlayMode(Animation.PlayMode.LOOP);
        this.animFrame = animIdle.getKeyFrame(animTime);

        posOffset.x = MathUtils.random(-0.3f, 0.3f);

//        color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
        color = new Color(Color.BLACK);
        color.lerp(Color.WHITE, 0.8f + MathUtils.random(-0.4f, 0.2f));
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Jumping is most important
        if(isJumping) {
            animFrame = animJump.getKeyFrame(jumpTime);

            jumpScale = MathUtils.clamp(jumpTime / animJump.getAnimationDuration(), 0, 1);

            // We've got 0 - 2 here
            jumpScale *= 2;

            // We need 0 - 1 - 0
            if(jumpScale > 1) {
                jumpScale = 2 - jumpScale;
            }

            // We have 0 - 1 - 0, here. Let's scale it to 0 - 0.5f - 0
            jumpScale *= 0.4f;

            // Finish cheer
            if(animJump.isAnimationFinished(jumpTime)) {
                isJumping = false;
                jumpTime = 0;
                jumpScale = 0;
            }

        } else {


            if(nextCheerTime < 0) {
                animFrame = animClap.getKeyFrame(Math.abs(nextCheerTime));

                // Finish cheer
                if(animClap.isAnimationFinished(Math.abs(nextCheerTime))) {
                    nextCheerTime = MathUtils.random(0f, 5f);
                }

            } else {
                // Dance
                animFrame = animIdle.getKeyFrame(animTime);
            }

        }

        batch.setColor(color);
        batch.draw(animFrame, position.x + posOffset.x - animFrame.getRegionWidth() * G.INV_SCALE / 2,
                position.y + posOffset.y - animFrame.getRegionHeight() * G.INV_SCALE / 2,
                animFrame.getRegionWidth() / 2 * G.INV_SCALE, animFrame.getRegionHeight() / 2 * G.INV_SCALE,
                animFrame.getRegionWidth() * G.INV_SCALE, animFrame.getRegionHeight() * G.INV_SCALE, 0.7f + jumpScale, 0.7f + jumpScale, rotation);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void update(float delta) {
        if(isJumping) {
            jumpTime += delta;
        }

        animTime += delta;
        nextCheerTime -= delta;

        posOffset.x += delta * offsetDirection / offsetSpeed;
        if(Math.abs(posOffset.x) > 0.3f) {
            offsetDirection *= -1;
        }

        // Rotate to sacrifice
        Array<Entity> sacrifices = gameWorld.getEntityManager().getEntitiesClass(Sacrifice.class);
        if(sacrifices.size > 0) {
            tempVec2.set(position).sub(sacrifices.get(0).position);
            rotation = tempVec2.angle() + 90;
        }
    }

    public void jump() {
        isJumping = true;
        jumpTime = 0;
        jumpScale = 0;
    }

    @Override
    public void dispose() {

    }
}
