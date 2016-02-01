package com.mygdx.game.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerArrowsController extends InputAdapter {

    private PlayerController playerController;

    public PlayerArrowsController(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.LEFT:
                playerController.getDirection().x = -1;
                return true;
            case Input.Keys.RIGHT:
                playerController.getDirection().x = 1;
                return true;
            case Input.Keys.UP:
                playerController.getDirection().y = 1;
                return true;
            case Input.Keys.DOWN:
                playerController.getDirection().y = -1;
                return true;
            case Input.Keys.ALT_RIGHT:
            case Input.Keys.SLASH:
            case Input.Keys.SHIFT_RIGHT:
            case Input.Keys.ENTER:
            case Input.Keys.M:
            case Input.Keys.L:
                playerController.shootPressed = true;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                if(playerController.getDirection().x == -1) playerController.getDirection().x = 0;
                return true;
            case Input.Keys.RIGHT:
                if(playerController.getDirection().x == 1) playerController.getDirection().x = 0;
                return true;
            case Input.Keys.UP:
                if(playerController.getDirection().y == 1) playerController.getDirection().y = 0;
                return true;
            case Input.Keys.DOWN:
                if(playerController.getDirection().y == -1) playerController.getDirection().y = 0;
                return true;
            case Input.Keys.ALT_RIGHT:
            case Input.Keys.SHIFT_RIGHT:
            case Input.Keys.SLASH:
            case Input.Keys.ENTER:
            case Input.Keys.M:
            case Input.Keys.L:
                playerController.shootPressed = false;
                return true;
        }
        return false;
    }
}
