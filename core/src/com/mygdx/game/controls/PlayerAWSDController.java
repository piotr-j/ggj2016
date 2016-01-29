package com.mygdx.game.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.entities.Player;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerAWSDController extends InputAdapter {

    private Player player;

    public PlayerAWSDController(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A:
                player.getDirection().x = -1;
                return true;
            case Input.Keys.D:
                player.getDirection().x = 1;
                return true;
            case Input.Keys.W:
                player.getDirection().y = 1;
                return true;
            case Input.Keys.S:
                player.getDirection().y = -1;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
                if(player.getDirection().x == -1) player.getDirection().x = 0;
                return true;
            case Input.Keys.D:
                if(player.getDirection().x == 1) player.getDirection().x = 0;
                return true;
            case Input.Keys.W:
                if(player.getDirection().y == 1) player.getDirection().y = 0;
                return true;
            case Input.Keys.S:
                if(player.getDirection().y == -1) player.getDirection().y = 0;
                return true;
        }
        return false;
    }
}
