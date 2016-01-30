package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-29.
 */
public class Arena extends Entity {

    private boolean flagForDelete = false;
    private TextureAtlas.AtlasRegion region;
    public Arena (float x, float y, float width, float height, GameWorld gameWorld) {
        super(x, y, width, height);
        region = G.assets.getAtlasRegion(G.A.ARENA, G.A.ATLAS);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }
}
