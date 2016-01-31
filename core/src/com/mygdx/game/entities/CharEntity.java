package com.mygdx.game.entities;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.G;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-31.
 */
public class CharEntity extends Entity {

    private float alpha = 1f;
    private String message;
    public boolean isFlaggedForDelete = false;
    private float scale = 1f;

    GlyphLayout layout;

    private BitmapFont fnt;
    private Color tempFntColor;
    private float tempFntScale;

    private Color color;

    public CharEntity(String message, float x, float y, BitmapFont fnt, Color color) {
        super(x, y, 0, 0);
        this.color = color;
        this.message = message;
        this.fnt = fnt;
        fnt.setUseIntegerPositions(false);

        fnt.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        layout = new GlyphLayout();
        layout.setText(fnt, message);

        bounds.setWidth(layout.width);
        bounds.setHeight(layout.height);
    }

    public void draw(SpriteBatch batch) {
//        tempFntColor = fnt.getColor();
//        tempFntScale = fnt.getScaleX();

//        fnt.setColor(fnt.getColor().r, fnt.getColor().g, fnt.getColor().b, alpha);
//        fnt.getData().scale(scale);
//        fnt.draw(batch, message, position.x - (layout.width * scale / 2), position.y + (fnt.getLineHeight() / 2));
        fnt.setColor(color);
        fnt.draw(batch, message, position.x, position.y, layout.width, Align.topLeft, false);
        fnt.setColor(Color.WHITE);
//        fnt.setColor(Color.WHITE);
//        fnt.getData().scale(1);
    }

    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

    /**
     * @return the alpha
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
}
