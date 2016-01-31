package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Spectator;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class WaveManager {

    private float waveAngle;
    private boolean isWaving = false;

    private Array<Entity> alreadyJumped = new Array<Entity>();

    private GameWorld gameWorld;

    private Vector2 screenCenter = new Vector2(G.VP_WIDTH / 2, G.VP_HEIGHT / 2);
    private Vector2 tempVec2 = new Vector2();

    public WaveManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void update(float delta) {
        if(!isWaving) return;

        waveAngle += delta * 100;

        for(Entity spect : gameWorld.getEntityManager().getEntitiesClass(Spectator.class)) {
            if(alreadyJumped.contains(spect, true)) continue;

            float angle = tempVec2.set(spect.getPosition()).sub(screenCenter).angle();

            if(waveAngle > angle) {
                alreadyJumped.add(spect);
                ((Spectator)spect).jump();
            }
        }

        if(waveAngle >= 360) {
            isWaving = false;
            waveAngle = 0;
        }
    }

    public void makeWave() {
        waveAngle = 0;
        alreadyJumped.clear();
        isWaving = true;
    }

}
