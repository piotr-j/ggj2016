package com.mygdx.game.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.entities.Player;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerArrowsController extends InputAdapter {

    private Player player;

    public PlayerArrowsController(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.LEFT:
                player.getDirection().x = -1;
                return true;
            case Input.Keys.RIGHT:
                player.getDirection().x = 1;
                return true;
            case Input.Keys.UP:
                player.getDirection().y = 1;
                return true;
            case Input.Keys.DOWN:
                player.getDirection().y = -1;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                if(player.getDirection().x == -1) player.getDirection().x = 0;
                return true;
            case Input.Keys.RIGHT:
                if(player.getDirection().x == 1) player.getDirection().x = 0;
                return true;
            case Input.Keys.UP:
                if(player.getDirection().y == 1) player.getDirection().y = 0;
                return true;
            case Input.Keys.DOWN:
                if(player.getDirection().y == -1) player.getDirection().y = 0;
                return true;
        }
        return false;
    }
}
