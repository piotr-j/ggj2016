package com.mygdx.game.entities;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
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
import com.mygdx.game.utils.Box2dRaycastCollisionDetector;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Walker extends Entity implements PhysicsObject {

    // Debug
    public static boolean debugDrawWander = false;
    public static boolean debugDrawAvoidence = false;
    private final Color color;

    // Physics
    private Body body;
    private BodySteerable steerable;
    private Wander<Vector2> wander;
    private RaycastObstacleAvoidance<Vector2> avoidance;
    private CentralRayWithWhiskersConfiguration<Vector2> rayCfg;
    private BlendedSteering<Vector2> steering;
    private boolean flagForDelete = false;

    public Walker (float x, float y, float radius, GameWorld gameWorld, Color color) {
        super(x, y, radius * 2, radius * 2);
        this.color = color;

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
        steerable.setMaxAngularAcceleration(.5f);
        steerable.setMaxAngularSpeed(3f);
        steerable.setBoundingRadius(bounds.width / 2);
        steerable.setZeroLinearSpeedThreshold(0.01f);
        steerable.setBody(body);

        wander = new Wander<Vector2>(steerable);
        wander.setFaceEnabled(true)
            .setAlignTolerance(.01f)
            .setDecelerationRadius(.5f)
            .setTimeToTarget(0.3f)
            .setWanderOffset(2f)
            .setWanderOrientation(MathUtils.random(360))
            .setWanderRadius(1f)
            .setWanderRate(MathUtils.PI2 * 2);
        rayCfg = new CentralRayWithWhiskersConfiguration<Vector2>(steerable, 1f, .75f, 30f);

        Box2dRaycastCollisionDetector detector = new Box2dRaycastCollisionDetector(
            gameWorld.getBox2DWorld().getWorld());
        avoidance = new RaycastObstacleAvoidance<Vector2>(steerable, rayCfg, detector);
        avoidance.setDistanceFromBoundary(1f);
        steering = new BlendedSteering<Vector2>(steerable);
        steering.add(wander, .5f);
        steering.add(avoidance, .75f);
    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    private static Vector2 tmp = new Vector2();
    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, bounds.width/2, 16);

        if (debugDrawAvoidence || debugDrawWander) {
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
//            wander.getInternalTargetPosition();
//            float decelerationRadius = wander.getDecelerationRadius();
//            shapeRenderer.setColor(Color.MAGENTA);
//            shapeRenderer.circle(center.x, center.y, decelerationRadius, 32);
            }
            if (debugDrawAvoidence) {
                Vector2 pos = body.getPosition();
                float rayLength = rayCfg.getRayLength();
                tmp.set(1, 0).rotateRad(body.getAngle() + 90 * MathUtils.degRad).scl(rayLength);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.line(pos.x, pos.y, pos.x + tmp.x, pos.y + tmp.y);
                float whiskerLength = rayCfg.getWhiskerLength();
                float whiskerAngle = rayCfg.getWhiskerAngle();
                tmp.set(1, 0).rotateRad(body.getAngle() + (90 + whiskerAngle) * MathUtils.degRad).scl(whiskerLength);
                shapeRenderer.setColor(Color.MAGENTA);
                shapeRenderer.line(pos.x, pos.y, pos.x + tmp.x, pos.y + tmp.y);
                tmp.set(1, 0).rotateRad(body.getAngle() + (90 - whiskerAngle) * MathUtils.degRad).scl(whiskerLength);
                shapeRenderer.line(pos.x, pos.y, pos.x + tmp.x, pos.y + tmp.y);
            }
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }
    }

    public static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
    @Override
    public void update(float delta) {
        position.set(body.getPosition());
        // note this is garbage
        boolean anyAccelerations = false;
        steering.calculateSteering(steeringOutput);
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
