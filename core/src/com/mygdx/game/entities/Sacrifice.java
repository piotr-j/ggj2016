package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;
import com.mygdx.game.view.WorldRenderer;

/**
 * Should run from player
 */
public class Sacrifice extends Entity implements PhysicsObject {

    // Config
    private final float SPEED = .025f;
    private final float RUN_RADIUS = 150 * G.INV_SCALE;

    // Physics
    private Body body;
    private float mass;
    private boolean flagForDelete = false;
    private Vector2 velocity = new Vector2();

    private GameWorld gameWorld;
    private final Color color;
    public int team;

    // Temp
    private Vector2 tempVec2 = new Vector2();
    public Player owner;

    // Animation
    private float animTime;
    private Animation animation;
    private TextureRegion animFrame;

    public Sacrifice (float x, float y, float radius, GameWorld gameWorld, Color color) {
        super(x, y, radius * 2, radius * 2);
        this.gameWorld = gameWorld;
        this.color = color;

        // Animation
        this.animation = new Animation(0.033f, G.assets.getAtlas(G.A.ATLAS).findRegions(G.A.SACRIFICE));
        this.animation.setPlayMode(Animation.PlayMode.LOOP);
        this.animFrame = animation.getKeyFrame(animTime);

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2)
                        .density(.1f)
                        .friction(0.2f)
                        .restitution(0.5f)
//                        .maskBits(Box2DWorld.WALKER_MASK)
//                        .categoryBits(Box2DWorld.CATEGORY.ENEMY)
                        .build())
//                .fixedRotation()
                .position(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();
        mass = body.getMass();
        resetBox2d();
    }

    public void resetBox2d() {
        body.setAngularDamping(10f);
        body.setLinearDamping(30f);
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

    private Vector2 tmp = new Vector2();
    public float captureCoolDown;
    public float shootTimer;
    @Override
    public void update(float delta) {
        position.set(body.getPosition());
        velocity.set(body.getLinearVelocity());
        rotation = body.getAngle() * MathUtils.radDeg;

        float animSpeed = MathUtils.clamp(velocity.len(), 0, 1);
        animTime += delta * animSpeed;

        captureCoolDown -= delta;
        shootTimer -= delta;
        if (owner != null) {
            // position set via weld joint with owner
        } else if (shootTimer <= 0){
            resetBox2d();

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

            runSpeed *= SPEED;
            velocity.nor().scl(runSpeed);

            if (!velocity.isZero()) {
                body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
            }
        }

        Vector2 velocity = body.getLinearVelocity();
        if (!velocity.isZero(0.001f)) {
            float desiredAngle = velocity.angleRad();
            float nextAngle = body.getAngle() + body.getAngularVelocity() / 60.0f;
            float totalRotation = desiredAngle - nextAngle;
            while (totalRotation < -180 * MathUtils.degRad)
                totalRotation += 360 * MathUtils.degRad;
            while (totalRotation > 180 * MathUtils.degRad)
                totalRotation -= 360 * MathUtils.degRad;
            float desiredAngularVelocity = totalRotation * 60;
            float change = 90f * MathUtils.degRad; //allow 1 degree rotation per time step
            desiredAngularVelocity = Math.min(change, Math.max(-change, desiredAngularVelocity));
            float impulse = body.getInertia() * desiredAngularVelocity;
//            Gdx.app.log("", "impulse " + impulse);
            body.applyAngularImpulse(impulse, true);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        if (psycho2 instanceof Flame) {
            if (owner != null)
                owner.sacrifice = null;
            Flame flame = (Flame)psycho2;
            if (flame.team != team) {
                world.teamScored(team);
            } else {
                world.teamScored(flame.team);
            }
            owner = null;


            WorldRenderer.SHAKE_TIME += 0.3f;

            switch (MathUtils.random(2)) {
                case 0:
                    System.out.println(1);
                    G.assets.get(G.A.SOUND_CHEER1, Sound.class).play(0.8f, 1 + MathUtils.random(-0.05f, 0.05f), 0);
                    break;
                case 1:
                    System.out.println(2);
                    G.assets.get(G.A.SOUND_CHEER2, Sound.class).play(0.8f, 1 + MathUtils.random(-0.05f, 0.05f), 0);
                    break;
                case 2:
                    System.out.println(3);
                    G.assets.get(G.A.SOUND_CHEER3, Sound.class).play(0.8f, 1 + MathUtils.random(-0.05f, 0.05f), 0);
                    break;
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
