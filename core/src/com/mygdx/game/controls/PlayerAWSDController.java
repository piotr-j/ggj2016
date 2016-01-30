package com.mygdx.game.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerAWSDController extends InputAdapter {

    private PlayerController playerController;

    public PlayerAWSDController(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A:
                playerController.getDirection().x = -1;
                return true;
            case Input.Keys.D:
                playerController.getDirection().x = 1;
                return true;
            case Input.Keys.W:
                playerController.getDirection().y = 1;
                return true;
            case Input.Keys.S:
                playerController.getDirection().y = -1;
                return true;
            case Input.Keys.Q:
                playerController.shootPressed = true;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
                if(playerController.getDirection().x == -1) playerController.getDirection().x = 0;
                return true;
            case Input.Keys.D:
                if(playerController.getDirection().x == 1) playerController.getDirection().x = 0;
                return true;
            case Input.Keys.W:
                if(playerController.getDirection().y == 1) playerController.getDirection().y = 0;
                return true;
            case Input.Keys.S:
                if(playerController.getDirection().y == -1) playerController.getDirection().y = 0;
                return true;
            case Input.Keys.Q:
                playerController.shootPressed = true;
                return true;
        }
        return false;
    }
}
