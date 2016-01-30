package com.mygdx.game.controls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerStageController {

    private PlayerController playerController;
    private final static float size = 150;
    public PlayerStageController (final PlayerController controller, final Table container, boolean flip) {
        playerController = controller;
        final Touchpad touchpad = new Touchpad(0.1f, VisUI.getSkin());
        touchpad.getStyle().background = null;
        touchpad.addListener(new ChangeListener() {
            @Override public void changed (ChangeEvent event, Actor actor) {
                Vector2 direction = controller.getDirection();
                direction.x = touchpad.getKnobPercentX();
                direction.y = touchpad.getKnobPercentY();
            }
        });
        final VisTextButton shoot = new VisTextButton("");
        shoot.addListener(new InputListener() {
            @Override public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                controller.shootPressed = true;
                return true;
            }

            @Override public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                controller.shootPressed = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        if (flip) {
            container.add(touchpad).size(size).expand().top().right().pad(size/2);
            container.row();
            container.add(shoot).size(size).expand().bottom().right().pad(size/2);
        } else {
            container.add(shoot).size(size).expand().top().left().pad(size/2);
            container.row();
            container.add(touchpad).size(size).expand().bottom().left().pad(size/2);
        }
    }
}
