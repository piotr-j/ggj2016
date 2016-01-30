package com.mygdx.game.controls;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class PlayerController {

    // Controls
    private Vector2 direction;

    public PlayerController() {
        this.direction = new Vector2();
    }

    public Vector2 getDirection() {
        return direction;
    }

    protected boolean shootPressed;

    public boolean isShootPressed () {
        return shootPressed;
    }
}
