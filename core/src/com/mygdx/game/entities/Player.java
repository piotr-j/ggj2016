package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.game.G;
import com.mygdx.game.controls.PlayerController;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Player extends Entity implements PhysicsObject, Box2DWorld.JointCallback {

    // Config
    private final float SPEED = 0.7f;
    private final float TIMEOUT_DURATION = 2.0f;
    private final float SACRIFICE_SLOWDOWN = 0.8f;
    private GameWorld gameWorld;
    private final Color color;

    private PlayerController controller;

    // Physics
    private Body body;
    private float mass;
    private boolean flagForDelete = false;
    private Vector2 velocity = new Vector2();

    // Temp
    private Vector2 tempVec2 = new Vector2();
    public int team;

    // Animation
    private float animTime;
    private Animation animation;
    private TextureRegion animFrame;
    private ParticleEffect spawnEffect;

    public Player(float x, float y, float radius, PlayerController controller, GameWorld gameWorld, Color color, int team) {
        super(x, y, radius * 2, radius * 2);
        this.gameWorld = gameWorld;
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
                .angularDamping(10f)
                .linearDamping(10f)
                .position(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();
        mass = body.getMass();
        timeout = TIMEOUT_DURATION;
        spawnEffect = new ParticleEffect(G.assets.get("pack/spawn.p", ParticleEffect.class));
        spawnEffect.setPosition(x, y);
        spawnEffect.reset();
        float[] colors = spawnEffect.getEmitters().first().getTint().getColors();
        if (team == GameWorld.TEAM_1) {
            // R/G/B
            colors[0] = 1;
            colors[1] = 0;
            colors[2] = 0;
        } else if (team == GameWorld.TEAM_2) {
            // R/G/B
            colors[0] = 0;
            colors[1] = 0;
            colors[2] = 1;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isActive()) {
            batch.setColor(1, 1, 1, .5f + MathUtils.sin(timeout * 10)/4);
        }
        animFrame = animation.getKeyFrame(animTime);
        batch.draw(animFrame, position.x - animFrame.getRegionWidth() * G.INV_SCALE / 2,
                position.y - animFrame.getRegionHeight() * G.INV_SCALE / 2,
                animFrame.getRegionWidth() / 2 * G.INV_SCALE, animFrame.getRegionHeight() / 2 * G.INV_SCALE,
                animFrame.getRegionWidth() * G.INV_SCALE, animFrame.getRegionHeight() * G.INV_SCALE, 1, 1, rotation - 90);

        if (!isActive()) {
            batch.setColor(1, 1, 1, 1);
            spawnEffect.draw(batch);
        }
    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
//        shapeRenderer.setColor(color);
//        shapeRenderer.circle(position.x, position.y, bounds.width/2, 16);
    }

    @Override
    public void update(float delta) {
        if (timeout > 0) {
            body.setTransform(-10, -10, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            timeout -= delta;
            spawnEffect.update(delta);
            if (timeout <= 0) {
                body.setTransform(position.x, position.y, rotation);
            } else {
                return;
            }
        }
        position.set(body.getPosition());
        velocity.set(body.getLinearVelocity());
        float angle = body.getAngle();
        rotation = angle * MathUtils.radDeg;

        float animSpeed = MathUtils.clamp(velocity.len(), 0, 1);
        animTime += delta * animSpeed;

        // Transform direction into velocity
        Vector2 direction = controller.getDirection();
        if(!direction.isZero()) {
            tempVec2.set(direction).limit2(1).scl(mass).scl(SPEED);
            if (sacrifice != null) tempVec2.scl(SACRIFICE_SLOWDOWN);
            body.applyLinearImpulse(tempVec2, body.getWorldCenter(), true);
        }

        Vector2 velocity = body.getLinearVelocity();
        if (!velocity.isZero(0.001f)) {
            float desiredAngle = velocity.angleRad();
            float nextAngle = angle + body.getAngularVelocity() / 60.0f;
            float totalRotation = desiredAngle - nextAngle;
            while (totalRotation < -180 * MathUtils.degRad)
                totalRotation += 360 * MathUtils.degRad;
            while (totalRotation > 180 * MathUtils.degRad)
                totalRotation -= 360 * MathUtils.degRad;
            float desiredAngularVelocity = totalRotation * 60;
            float change = 90f * MathUtils.degRad; //allow 1 degree rotation per time step
            desiredAngularVelocity = Math.min(change, Math.max(-change, desiredAngularVelocity));
            float impulse = body.getInertia() * desiredAngularVelocity;
            body.applyAngularImpulse(impulse, true);
        }

        if (sacrifice != null && controller.isShootPressed()) {
            tempVec2.set(1, 0).rotateRad(angle).nor().scl(0.25f);
            shootSacrifice(tempVec2);
        }
    }

    private float timeout;
    public void timeout () {
        timeout = TIMEOUT_DURATION;
        float cx = G.VP_WIDTH / 2;
        float cy = G.VP_HEIGHT / 2;
        if (team == GameWorld.TEAM_2) {
            position.set(cx - GameWorld.SPAWN_X_OFFSET + MathUtils.random(-GameWorld.SPAWN_SPREAD_X, GameWorld.SPAWN_SPREAD_X), cy + MathUtils.random(-GameWorld.SPAWN_SPREAD_Y, GameWorld.SPAWN_SPREAD_Y));
            rotation = 0;
        } else if (team == GameWorld.TEAM_1) {
            position.set(cx + GameWorld.SPAWN_X_OFFSET + MathUtils.random(-GameWorld.SPAWN_SPREAD_X, GameWorld.SPAWN_SPREAD_X), cy + MathUtils.random(-GameWorld.SPAWN_SPREAD_Y, GameWorld.SPAWN_SPREAD_Y));
            rotation = 0;
        }
        spawnEffect.setPosition(position.x, position.y);
        spawnEffect.reset();
    }

    public boolean isActive() {
        return timeout <= 0;
    }

    private void releaseSacrifice () {
        gameWorld.getBox2DWorld().destroyJoint(sacrificeWeld);
        sacrificeWeld = null;
        sacrifice.owner = null;
        sacrifice.captureCoolDown = .55f;
        sacrifice = null;
    }

    private void shootSacrifice (Vector2 impulse) {
        gameWorld.getBox2DWorld().getWorld().destroyJoint(sacrificeWeld);
        sacrifice.getBody().applyLinearImpulse(impulse, sacrifice.getBody().getWorldCenter(), true);
        sacrifice.owner = null;
        sacrifice.captureCoolDown = .55f;
        sacrifice.shootTimer = 1.5f;
        sacrifice = null;
        sacrificeWeld = null;
    }

    @Override
    public void dispose() {

    }


    public Sacrifice sacrifice;
    public WeldJoint sacrificeWeld;
    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        if (!isActive()) return;
        if (psycho2 instanceof Sacrifice) {
            Sacrifice sacrifice = (Sacrifice)psycho2;
            // do we allow for swapping and other stuff?
            if ((sacrifice.owner == null
                || (sacrifice.captureCoolDown <= 0 && sacrifice.owner.team != team)) && this.sacrifice == null) {
                if (sacrifice.owner != null) {
                    sacrifice.owner.releaseSacrifice();
                }
                sacrifice.owner = this;
                sacrifice.team = team;
                sacrifice.captureCoolDown = .55f;
                this.sacrifice = sacrifice;
                Body sacrificeBody = sacrifice.getBody();
                // so its easier to control
                sacrificeBody.setLinearDamping(0);
                sacrificeBody.setAngularDamping(0);
                WeldJointDef wjd = new WeldJointDef();
                wjd.bodyA = this.body;
                wjd.bodyB = sacrificeBody;
                wjd.localAnchorB.set(-.75f, 0);
                world.getBox2DWorld().createJoint(wjd, this);
            }
        }
    }

    @Override public void jointCreated (Joint joint) {
        sacrificeWeld = (WeldJoint)joint;
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
