package com.mygdx.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.mygdx.game.utils.Xbox360;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class PlayerGamepadController extends ControllerAdapter {

    private PlayerController playerController;

    public PlayerGamepadController(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        if(value > -0.1f && value < 0.1f) value = 0;

        if(axisIndex == Xbox360.AXIS_LEFT_X) {
            playerController.getDirection().x = value;
            return true;

        } else if(axisIndex == Xbox360.AXIS_LEFT_Y) {
            playerController.getDirection().y = -value;
            return true;

        }

        return false;
    }

    @Override public boolean buttonDown (Controller controller, int buttonIndex) {
        playerController.shootPressed = true;
        return super.buttonDown(controller, buttonIndex);
    }

    @Override public boolean buttonUp (Controller controller, int buttonIndex) {
        playerController.shootPressed = false;
        return super.buttonUp(controller, buttonIndex);
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
