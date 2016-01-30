package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.G;
import com.mygdx.game.controls.PlayerController;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Player extends Entity implements PhysicsObject {

    // Config
    private final float SPEED = 8;
    private final Color color;

    private PlayerController controller;

    // Physics
    private Body body;
    private boolean flagForDelete = false;
    private Vector2 velocity = new Vector2();

    // Temp
    private Vector2 tempVec2 = new Vector2();
    public int team;

    // Animation
    private float animTime;
    private Animation animation;
    private TextureRegion animFrame;

    public Player(float x, float y, float radius, PlayerController controller, GameWorld gameWorld, Color color, int team) {
        super(x, y, radius * 2, radius * 2);
        this.color = color;

        this.controller = controller;
        this.team = team;

        // Animation
        if(team == 1) {
            this.animation = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.TEAMA));
        } else if (team == 2) {
            this.animation = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.TEAMB));
        }

        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        this.animFrame = animation.getKeyFrame(animTime);

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
//                                .maskBits(Box2DWorld.WALKER_MASK)
//                        .categoryBits(Box2DWorld.CATEGORY.ENEMY)
                        .build())
//                .fixedRotation()
                .angularDamping(3f)
                .linearDamping(10f)
                .position(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();
    }

    @Override
    public void draw(SpriteBatch batch) {
        animFrame = animation.getKeyFrame(animTime);
        batch.draw(animFrame, position.x - animFrame.getRegionWidth() * G.INV_SCALE / 2,
                position.y - animFrame.getRegionHeight() * G.INV_SCALE / 2,
                animFrame.getRegionWidth() / 2 * G.INV_SCALE, animFrame.getRegionHeight() / 2 * G.INV_SCALE,
                animFrame.getRegionWidth() * G.INV_SCALE, animFrame.getRegionHeight() * G.INV_SCALE, 1, 1, rotation - 90);
    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
//        shapeRenderer.setColor(color);
//        shapeRenderer.circle(position.x, position.y, bounds.width/2, 16);
    }

    @Override
    public void update(float delta) {
        position.set(body.getPosition());
        velocity.set(body.getLinearVelocity());
        rotation = body.getAngle() * MathUtils.radDeg;

        float animSpeed = MathUtils.clamp(velocity.len(), 0, 1);
        animTime += delta * animSpeed;

        // Transform direction into velocity
        if(controller.getDirection().x != 0 || controller.getDirection().y != 0) {
            velocity.set(controller.getDirection()).limit(1).scl(SPEED);

            tempVec2.set(body.getLinearVelocity()).lerp(velocity, 0.08f * 60 * delta);

            body.setTransform(position.x, position.y, tempVec2.angle() * MathUtils.degRad);
            body.setLinearVelocity(tempVec2.x, tempVec2.y);
        }

        if (sacrifice != null) {

        }
    }

    @Override
    public void dispose() {

    }


    public Sacrifice sacrifice;
    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        if (psycho2 instanceof Sacrifice) {
            Sacrifice sacrifice = (Sacrifice)psycho2;
            // do we allow for swapping and other stuff?
            if ((sacrifice.owner == null
                || (sacrifice.captureCoolDown <= 0 && sacrifice.owner.team != team)) && this.sacrifice == null) {
                if (sacrifice.owner != null) {
                    sacrifice.owner.sacrifice = null;
                }
                sacrifice.owner = this;
                sacrifice.captureCoolDown = .75f;
                this.sacrifice = sacrifice;
            }
        }
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public boolean getFlagForDelete() {
        return flagForDelete;
    }

    @Override
    public void setFlagForDelete(boolean flag) {
        flagForDelete = flag;
    }
}
