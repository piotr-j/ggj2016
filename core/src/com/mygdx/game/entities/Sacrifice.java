package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * Should run from player
 */
public class Sacrifice extends Entity implements PhysicsObject {

    // Config
    private final float SPEED = 1.3f;
    private final float RUN_RADIUS = 150;

    // Physics
    private Body body;
    private boolean flagForDelete = false;
    private Vector2 velocity = new Vector2();

    private GameWorld gameWorld;

    // Temp
    private Vector2 tempVec2 = new Vector2();

    public Sacrifice(float x, float y, float radius, GameWorld gameWorld) {
        super(x, y, radius * 2, radius * 2);
        this.gameWorld = gameWorld;

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2 * Box2DWorld.WORLD_TO_BOX)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
//                        .maskBits(Box2DWorld.WALKER_MASK)
//                        .categoryBits(Box2DWorld.CATEGORY.ENEMY)
                        .build())
//                .fixedRotation()
                .angularDamping(3f)
                .linearDamping(10f)
                .position(x * Box2DWorld.WORLD_TO_BOX, y * Box2DWorld.WORLD_TO_BOX)
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();
    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {
        position.set(body.getPosition().x * Box2DWorld.BOX_TO_WORLD, body.getPosition().y * Box2DWorld.BOX_TO_WORLD);
        rotation = body.getAngle() * MathUtils.radDeg;

        // Set velocity
        velocity.set(0, 0);
        float runSpeed = 0;

        // Players
        for(Entity player : gameWorld.getEntityManager().getEntitiesClass(Player.class)) {
            if(player.getPosition().dst(position) < RUN_RADIUS) {

                // Direction
                tempVec2.set(position).sub(player.getPosition()).nor();

                // Speed
                float tempSpeed = RUN_RADIUS - player.getPosition().dst(position);
                if(tempSpeed > runSpeed) runSpeed = tempSpeed;

                velocity.add(tempVec2);
            }
        }

        // Flames
        for(Entity flame : gameWorld.getEntityManager().getEntitiesClass(Flame.class)) {
            if(flame.getPosition().dst(position) < RUN_RADIUS) {

                // Direction
                tempVec2.set(position).sub(flame.getPosition()).nor();

                // Speed
                float tempSpeed = RUN_RADIUS - flame.getPosition().dst(position);
                if(tempSpeed > runSpeed) runSpeed = tempSpeed;

                velocity.add(tempVec2);
            }
        }


        runSpeed *= 0.1f * SPEED;
        velocity.nor().scl(runSpeed);

        if(!velocity.isZero()) {

            tempVec2.set(body.getLinearVelocity()).lerp(velocity, delta);

            body.setLinearVelocity(tempVec2);
            body.setTransform(position.x * Box2DWorld.WORLD_TO_BOX, position.y * Box2DWorld.WORLD_TO_BOX,
                    velocity.angle() * MathUtils.degRad);
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
