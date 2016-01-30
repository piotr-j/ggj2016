package com.mygdx.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.G;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Sacrifice;
import com.mygdx.game.entities.Walker;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-30.
 */
public class GodsWillManager {

    public GameWorld gameWorld;

    public boolean needSacrifice = false;

    public GodsWillManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void update(float delta) {

        // Nominate new sacrifice
        if(needSacrifice) {
            Array<Entity> walkers = gameWorld.getEntityManager().getEntitiesClass(Walker.class);

            // Choose random one
            Entity walker = walkers.random();

            // Replace with sacrifice
            Entity newSacrifice = new Sacrifice(walker.getPosition().x, walker.getPosition().y, 15 * G.INV_SCALE, gameWorld, Color.GREEN);
            gameWorld.getEntityManager().addEntity(newSacrifice);
            gameWorld.getEntityManager().removeEntity(walker);

            needSacrifice = false;
        }


        // Check if sacrifice alive
        if(gameWorld.getEntityManager().getEntitiesClass(Sacrifice.class).size <= 0) {

            System.out.println("need one!");
            needSacrifice = true;
        }

    }

    public void draw(SpriteBatch batch) {


    }
}
