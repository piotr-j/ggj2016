package com.mygdx.game.entities;

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
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Player extends Entity implements PhysicsObject {

    // Config
    private final float SPEED = 0.7f;
    private final float SACRIFICE_SLOWDOWN = 0.8f;
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
                .angularDamping(10f)
                .linearDamping(10f)
                .position(x, y)
                .type(BodyDef.BodyType.DynamicBody)
                .userData(this)
                .build();
        mass = body.getMass();
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
        Vector2 direction = controller.getDirection();
        if(!direction.isZero()) {
            tempVec2.set(direction).limit2(1).scl(mass).scl(SPEED);
            if (sacrifice != null) tempVec2.scl(SACRIFICE_SLOWDOWN);
            body.applyLinearImpulse(tempVec2, body.getWorldCenter(), true);
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
