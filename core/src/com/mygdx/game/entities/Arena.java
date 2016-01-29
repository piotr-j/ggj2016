package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;
import com.mygdx.game.utils.FixtureDefBuilder;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Arena extends Entity implements PhysicsObject {

    // Physics
    private Body body;
    private boolean flagForDelete = false;

    public Arena (float x, float y, float width, float height, GameWorld gameWorld) {
        super(x, y, width, height);
        FixtureDefBuilder builder = gameWorld.getBox2DWorld().getFixtureDefBuilder();
        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(builder
                        .boxShape(bounds.width / 2, bounds.height / 2)
                        .density(0)
                        .build())
                .angularDamping(1f)
           // center of the screen
                .position(x + bounds.width / 2, y + bounds.height / 2)
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
