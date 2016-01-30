package com.mygdx.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.mygdx.game.entities.Player;
import com.mygdx.game.utils.Xbox360;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class PlayerGamepadController extends ControllerAdapter {

    private Player player;

    public PlayerGamepadController(Player player) {
        this.player = player;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        if(value > -0.1f && value < 0.1f) value = 0;

        if(axisIndex == Xbox360.AXIS_LEFT_X) {
            player.getDirection().x = value;
            return true;

        } else if(axisIndex == Xbox360.AXIS_LEFT_Y) {
            player.getDirection().y = -value;
            return true;

        }

        return false;
    }

    @Override
    public void connected(Controller controller) {
        super.connected(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        super.disconnected(controller);
    }


}
