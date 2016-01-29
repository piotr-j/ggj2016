package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Base entity class with basic position, rotation and size
 */
public abstract class Entity {

    protected Vector2 position;
    protected float rotation = 0;

    protected Rectangle bounds;

    public Entity(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);
    }

    public abstract void draw(SpriteBatch batch);
    public abstract void update(float delta);
    public abstract void dispose();

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void drawDebug (ShapeRenderer shapeRenderer) {

    }
}
