package com.mygdx.game.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class PlayerStageController {

    private final static float size = 200;
    public PlayerStageController (final PlayerController controller, final Table container, Skin skin, boolean flip) {
        final Touchpad touchpad = new Touchpad(0.1f, skin);
        touchpad.addListener(new ChangeListener() {
            @Override public void changed (ChangeEvent event, Actor actor) {
                Vector2 direction = controller.getDirection();
                direction.x = touchpad.getKnobPercentX();
                direction.y = touchpad.getKnobPercentY();
            }
        });
        final Button shoot = new Button(skin);
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

        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle(touchpad.getStyle());
        style.background = null;
        Drawable knob = style.knob;
        knob.setMinWidth(knob.getMinWidth() * 2f);
        knob.setMinHeight(knob.getMinHeight() * 2f);
        touchpad.setStyle(style);
        if (flip) {
            container.add(touchpad).size(size * 1.5f).expand().top().right().pad(30);
            container.row();
            container.add(shoot).size(size * 2/3f).expand().bottom().right().pad(size/3);
            shoot.setColor(Color.RED);
            touchpad.setColor(Color.RED);
        } else {
            container.add(shoot).size(size * 2/3f).expand().top().left().pad(size/3);
            container.row();
            container.add(touchpad).size(size * 1.5f).expand().bottom().left().pad(30);
            shoot.setColor(Color.BLUE);
            touchpad.setColor(Color.BLUE);
        }
    }
}
