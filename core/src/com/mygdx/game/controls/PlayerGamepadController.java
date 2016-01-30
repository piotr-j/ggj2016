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

    private int down;
    @Override public boolean buttonDown (Controller controller, int buttonIndex) {
        switch (buttonIndex) {
        case Xbox360.BUTTON_A:
        case Xbox360.BUTTON_B:
        case Xbox360.BUTTON_X:
        case Xbox360.BUTTON_Y:
            down++;
        }
        if (down > 0) {
            playerController.shootPressed = true;
        }
        return super.buttonDown(controller, buttonIndex);
    }

    @Override public boolean buttonUp (Controller controller, int buttonIndex) {
        switch (buttonIndex) {
        case Xbox360.BUTTON_A:
        case Xbox360.BUTTON_B:
        case Xbox360.BUTTON_X:
        case Xbox360.BUTTON_Y:
            down--;
        }
        if (down == 0) {
            playerController.shootPressed = false;
        }
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
