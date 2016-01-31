package com.mygdx.game.utils;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.entities.CharEntity;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.model.GameWorld;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-31.
 */
public class FancyTextSpawner {

    private static Vector2 upVector2 = new Vector2();
    private static Vector2 downVector2 = new Vector2();

    private static Vector2 currVector2 = new Vector2();

    private static Vector2 position = new Vector2();
    private static Vector2 enterPosition = new Vector2();
    private static Vector2 exitPosition = new Vector2();

    public static void spawnText(CharSequence text,float posX, float posY, final GameWorld gameWorld, Color color, float delayAfter) {
        upVector2.setZero();
        downVector2.setZero();
        currVector2.setZero();
        position.setZero();
        enterPosition.setZero();
        exitPosition.setZero();

        upVector2.set(1, 1).nor().setAngle(78);
        downVector2.set(1, 1).nor().setAngle(78 + 180);

        float currentOffsetX = 0;
        float currentOffsetY = 0;

        BitmapFont fnt = G.assets.get(G.A.FONT_COLLEGIER, BitmapFont.class);
        fnt.getData().setScale(G.INV_SCALE, G.INV_SCALE);

        GlyphLayout layout = new GlyphLayout();
        layout.setText(fnt, text);

        currentOffsetX = -layout.width / 2;
        currentOffsetY = layout.height / 2f;

        final Array<Entity> charEntites = new Array<Entity>();

        for (int i = 0; i < text.length(); i++) {
            char charachter = text.charAt(i);

            if(charachter == ' ') {
                currentOffsetX += layout.width / text.length() / 2f;
                continue;
            }

            position.set(currentOffsetX + posX, currentOffsetY + posY);

            if(i % 2 == 0) {
                enterPosition.set(upVector2).scl(G.VP_HEIGHT / 2 * 1.5f).add(position);
                exitPosition.set(downVector2).scl(G.VP_HEIGHT / 2 * 1.5f).add(position);

                currVector2.set(upVector2);
            } else {
                enterPosition.set(downVector2).scl(G.VP_HEIGHT / 2 * 1.5f).add(position);
                exitPosition.set(upVector2).scl(G.VP_HEIGHT / 2 * 1.5f).add(position);

                currVector2.scl(downVector2);
            }



            final CharEntity charEnt = new CharEntity(String.valueOf(charachter), enterPosition.x, enterPosition.y, fnt, color);
            currentOffsetX += charEnt.getBounds().width;

            Timeline.createSequence()
//                    .push(Tween.to(charEnt, EntityTween.POSITION_XY, 1.0f).target(position.x + currVector2.x, position.y + currVector2.y).ease(TweenEquations.easeOutQuart))
//                    .push(Tween.to(charEnt, EntityTween.POSITION_XY, 0.4f).target(position.x - currVector2.x, position.y - currVector2.y).ease(TweenEquations.easeInOutQuint))
//                    .push(Tween.to(charEnt, EntityTween.POSITION_XY, 1.0f).target(exitPosition.x, exitPosition.y).ease(TweenEquations.easeInQuart))
                    .push(Tween.to(charEnt, EntityTween.POSITION_XY, 1.0f).target(position.x, position.y).ease(TweenEquations.easeOutQuart))
                    .pushPause(delayAfter)
                    .push(Tween.to(charEnt, EntityTween.POSITION_XY, 1.0f).target(exitPosition.x, exitPosition.y).ease(TweenEquations.easeInQuart))
                    .delay(0.1f * i)
                    .pushPause(0.1f * text.length())
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            for(Entity charEntity : charEntites) {
                                gameWorld.getMessageEntManager().removeEntity(charEntity);
                            }
                        }
                    })
                    .start(gameWorld.getTweenManager());

            gameWorld.getMessageEntManager().addEntity(charEnt);
            charEntites.add(charEnt);
        }



    }
}
