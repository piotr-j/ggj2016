package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.model.Box2DWorld;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * Kills entities!
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Flame extends Entity implements PhysicsObject {

    // Physics
    private Body body;
    private boolean flagForDelete = false;

    public Flame(float x, float y, float radius, GameWorld gameWorld) {
        super(x, y, radius * 2, radius * 2);

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2 * Box2DWorld.WORLD_TO_BOX)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
//                                .maskBits(Box2DWorld.WALKER_MASK)
//                        .categoryBits(Box2DWorld.CATEGORY.ENEMY)
                        .build())
//                .fixedRotation()
//                .angularDamping(1f)
                .position(x * Box2DWorld.WORLD_TO_BOX, y * Box2DWorld.WORLD_TO_BOX)
                .type(BodyDef.BodyType.StaticBody)
                .userData(this)
                .build();
    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        psycho2.setFlagForDelete(true);
        world.getEntityManager().removeEntity((Entity)psycho2);
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
