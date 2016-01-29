package com.mygdx.game.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.Entity;

/**
 * Keeps all entities and handle their update and draw.
 */
public class EntityManager {

    private Array<Entity> entities;

    public EntityManager() {
        this.entities = new Array<Entity>();
    }

    public void update(float delta) {
        for (Entity entity : entities) {
            entity.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    public void reset() {
        entities.clear();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.removeValue(entity, true);

        if(entity instanceof PhysicsObject) {
            PhysicsObject physicsObject = (PhysicsObject)entity;
            physicsObject.setFlagForDelete(true);
        }
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
    }
}
