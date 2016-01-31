package com.mygdx.game.utils;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.entities.Entity;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class EntityTween implements TweenAccessor<Entity> {

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;

    @Override
    public int getValues(Entity target, int tweenType, float[] returnValues) {

        switch(tweenType){
            case POSITION_X: returnValues[0] = target.getPosition().x; return 1;
            case POSITION_Y: returnValues[0] = target.getPosition().y; return 1;
            case POSITION_XY:
                returnValues[0] = target.getPosition().x;
                returnValues[1] = target.getPosition().y;
                return 2;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Entity target, int tweenType, float[] newValues) {

        switch(tweenType){
            case POSITION_X: target.getPosition().x = newValues[0]; break;
            case POSITION_Y: target.getPosition().y = newValues[0]; break;
            case POSITION_XY:
                target.getPosition().x = newValues[0];
                target.getPosition().y = newValues[1];
                break;
            default: assert false; break;
        }
    }


}
