package com.mygdx.game.entities;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;
import com.mygdx.game.utils.BodySteerable;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Walker extends Entity implements PhysicsObject {
    public static boolean debugDrawWander = true;
    // Physics
    private Body body;
    private BodySteerable steerable;
    private Wander<Vector2> wander;
    private boolean flagForDelete = false;

    public Walker(float x, float y, float radius, GameWorld gameWorld) {
        super(x, y, radius * 2, radius * 2);

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
                        .build())
                .angularDamping(1f)
                .position(x, y)
                .angle(MathUtils.random(-MathUtils.PI, MathUtils.PI))
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();

        steerable = new BodySteerable();
        steerable.setMaxLinearAcceleration(2f);
        steerable.setMaxLinearSpeed(1f);
        steerable.setMaxAngularAcceleration(0f);
        steerable.setMaxAngularSpeed(1f);
        steerable.setBoundingRadius(bounds.width / 2);
        steerable.setZeroLinearSpeedThreshold(0.01f);
        steerable.setBody(body);

        wander = new Wander<Vector2>(steerable);
        wander.setFaceEnabled(false)
            .setAlignTolerance(1f)
            .setDecelerationRadius(3f)
            .setTimeToTarget(0.3f)
            .setWanderOffset(7f)
            .setWanderOrientation(MathUtils.random(360))
            .setWanderRadius(4f)
            .setWanderRate(MathUtils.PI2 * 3);
    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
        if (debugDrawWander) {
            Vector2 target = wander.getInternalTargetPosition();
            Vector2 center = wander.getWanderCenter();
            float radius = wander.getWanderRadius();
            shapeRenderer.setColor(Color.CYAN);
            Vector2 pos = body.getPosition();
            shapeRenderer.line(pos.x, pos.y, center.x, center.y);
            shapeRenderer.circle(center.x, center.y, radius, 32);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(target.x, target.y, 0.1f, 8);
            wander.getInternalTargetPosition();
            float decelerationRadius = wander.getDecelerationRadius();
            shapeRenderer.setColor(Color.MAGENTA);
            shapeRenderer.circle(center.x, center.y, decelerationRadius, 32);
        }
    }

    public static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
    @Override
    public void update(float delta) {
        // note this is garbage
        boolean anyAccelerations = false;
        wander.calculateSteering(steeringOutput);
        // Update position and linear velocity.
        if (!steeringOutput.linear.isZero()) {
            // this method internally scales the force by deltaTime
            body.applyForceToCenter(steeringOutput.linear, true);
            anyAccelerations = true;
        }

        // Update orientation and angular velocity
        if (steeringOutput.angular != 0) {
            // this method internally scales the torque by deltaTime
            body.applyTorque(steeringOutput.angular, true);
            anyAccelerations = true;
        }

        if (anyAccelerations) {
            // Cap the linear speed
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = steerable.getMaxLinearSpeed();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }

            // Cap the angular speed
            float maxAngVelocity = steerable.getMaxAngularSpeed();
            if (body.getAngularVelocity() > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {

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
