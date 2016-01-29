package com.mygdx.game.model;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicsObject {
    public void handleBeginContact(PhysicsObject psycho2, GameWorld world);

    public Body getBody();
    public boolean getFlagForDelete();
    public void setFlagForDelete(boolean flag);
}
