package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;
import com.mygdx.game.model.PhysicsObject;

/**
 * Kills entities!
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Flame extends Entity implements PhysicsObject {

    private final Color color;

    // Physics
    private Body body;
    private boolean flagForDelete = false;
    public final int team;

    private Sprite sprite;

    public Flame (float x, float y, float radius, GameWorld gameWorld, Color color, int team) {
        super(x, y, radius * 2, radius * 2);
        this.color = color;
        this.team = team;

        this.sprite = new Sprite(G.assets.getAtlas(G.A.ATLAS).createSprite(G.A.VOLCANO));
        sprite.setOrigin(sprite.getRegionWidth() / 2 * G.INV_SCALE, sprite.getRegionHeight() / 2 * G.INV_SCALE);

        sprite.rotate(90);

        if(team == 1) {
            sprite.setFlip(false, false);
        } else {
            sprite.setFlip(false, true);
        }

        this.body = gameWorld.getBox2DWorld().getBodyBuilder()
                .fixture(gameWorld.getBox2DWorld().getFixtureDefBuilder()
                        .circleShape(getBounds().getWidth() / 2)
                        .density(1f)
                        .friction(0.2f)
                        .restitution(0.5f)
                        .sensor()
//                                .maskBits(Box2DWorld.WALKER_MASK)
//                        .categoryBits(Box2DWorld.CATEGORY.ENEMY)
                        .build())
//                .fixedRotation()
//                .angularDamping(1f)
                .position(x, y)
                .type(BodyDef.BodyType.StaticBody)
                .userData(this)
                .build();
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setSize(sprite.getRegionWidth() * G.INV_SCALE, sprite.getRegionHeight() * G.INV_SCALE);
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }

    @Override public void drawDebug (ShapeRenderer shapeRenderer) {
//        shapeRenderer.setColor(color);
//        shapeRenderer.circle(position.x, position.y, bounds.width/2, 32);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world) {
        if (psycho2 instanceof Walker) return;
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
