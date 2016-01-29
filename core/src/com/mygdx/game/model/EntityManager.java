package com.mygdx.game.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    private Array<Entity> returnedEntites = new Array<Entity>();

    /**
     * Returns same array instance!
     * @param type
     * @return
     */
    public Array<Entity> getEntitiesClass(Class type) {
        returnedEntites.clear();

        for(int i = 0; i < entities.size; i++) {
            if(entities.get(i).getClass() == type) {
                returnedEntites.add(entities.get(i));
            }
        }

        return returnedEntites;
    }

    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
    }

    public void drawDebug (ShapeRenderer shapeRenderer) {
        for (Entity entity : entities) {
            entity.drawDebug(shapeRenderer);
        }
    }
}
