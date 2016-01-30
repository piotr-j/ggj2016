package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * Should run from player
 */
public class Sacrifice extends Entity implements PhysicsObject {

    // Config
    private final float SPEED = 1f;
    private final float RUN_RADIUS = 150 * G.INV_SCALE;

    // Physics
    private Body body;
    private boolean flagForDelete = false;
    private Vector2 velocity = new Vector2();

    private GameWorld gameWorld;
    private final Color color;

    // Temp
    private Vector2 tempVec2 = new Vector2();
    public Player owner;

    public Sacrifice (float x, float y, float radius, GameWorld gameWorld, Color color) {
        super(x, y, radius * 2, radius * 2);
        this.gameWorld = gameWorld;
        this.color = color;

        System.out.println(x + " " + y);

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
//                        .maskBits(Box2DWorld.WALKER_MASK)
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

    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, bounds.width/2, 16);
    }

    private Vector2 tmp = new Vector2();
    @Override
    public void update(float delta) {
        if (owner != null) {
            tmp.set(1, 0).rotate(owner.getRotation()).scl(bounds.width/2 + owner.bounds.width/2 + 0.05f).add(owner.getPosition());
            body.setTransform(tmp.x, tmp.y, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            position.set(body.getPosition());
            body.setLinearVelocity(owner.getBody().getLinearVelocity());
        } else {
            position.set(body.getPosition());
            rotation = body.getAngle() * MathUtils.radDeg;

            // Set velocity
            velocity.set(0, 0);
            float runSpeed = 0;

            // Players
            for (Entity player : gameWorld.getEntityManager().getEntitiesClass(Player.class)) {
                if (player.getPosition().dst(position) < RUN_RADIUS) {

                    // Direction
                    tempVec2.set(position).sub(player.getPosition()).nor();

                    // Speed
                    float tempSpeed = RUN_RADIUS - player.getPosition().dst(position);
                    if (tempSpeed > runSpeed)
                        runSpeed = tempSpeed;

                    velocity.add(tempVec2);
                }
            }

            // Flames
            for (Entity flame : gameWorld.getEntityManager().getEntitiesClass(Flame.class)) {
                if (flame.getPosition().dst(position) < RUN_RADIUS) {

                    // Direction
                    tempVec2.set(position).sub(flame.getPosition()).nor();

                    // Speed
                    float tempSpeed = RUN_RADIUS - flame.getPosition().dst(position);
                    if (tempSpeed > runSpeed)
                        runSpeed = tempSpeed;

                    velocity.add(tempVec2);
                }
            }

            runSpeed *= SPEED * 10;
            velocity.nor().scl(runSpeed);

            if (!velocity.isZero()) {

                tempVec2.set(body.getLinearVelocity()).lerp(velocity, delta);

                body.setLinearVelocity(tempVec2);
                body.setTransform(position.x, position.y, velocity.angle() * MathUtils.degRad);
            }
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        if (psycho2 instanceof Flame) {
            if (owner != null) {
                // TODO score based on which flame it is
                owner.sacrifice = null;
                Flame flame = (Flame)psycho2;
                if (flame.team != owner.team) {
                    Gdx.app.log("", "Team " + owner.team + " scored !");
                } else {
                    Gdx.app.log("", "Team " + owner.team + " scored for another team " + flame.team + "!");
                }
                owner = null;
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
