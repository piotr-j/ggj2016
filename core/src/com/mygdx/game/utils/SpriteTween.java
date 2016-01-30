package com.mygdx.game.utils;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class SpriteTween implements TweenAccessor<Sprite> {

    public static final int ALPHA = 1;
    public static final int POSITION_X = 2;
    public static final int POSITION_Y = 3;
    public static final int POSITION_XY = 4;
    public static final int SCALEX = 5;
    public static final int SCALEY = 6;
    public static final int COLOR = 7;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {

        switch(tweenType){
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case POSITION_X: returnValues[0] = target.getX(); return 1;
            case POSITION_Y: returnValues[0] = target.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            //default:
            //	return 0;
            case SCALEX: returnValues[0] = target.getScaleX(); return 1;
            case SCALEY: returnValues[0] = target.getScaleY(); return 1;
            case COLOR:
                returnValues[0] = target.getColor().r;
                returnValues[1] = target.getColor().g;
                returnValues[2] = target.getColor().b;
                returnValues[3] = target.getColor().a;
                return 3;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {

        switch(tweenType){
            case ALPHA:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
                break;
            case POSITION_X: target.setX(newValues[0]); break;
            case POSITION_Y: target.setY(newValues[0]); break;
            case POSITION_XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            case SCALEX: target.setScale(newValues[0], target.getScaleY()); break;
            case SCALEY: target.setScale(target.getScaleX(), newValues[0]); break;
            case COLOR:
                target.setColor(newValues[0], newValues[1], newValues[2], newValues[3]);
                break;
            default: assert false; break;
        }
    }


}
