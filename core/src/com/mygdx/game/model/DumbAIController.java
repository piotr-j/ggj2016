package com.mygdx.game.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.controls.PlayerController;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Flame;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.Sacrifice;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-02-02.
 */
public class DumbAIController {

    /**
     * Team controlled by AI
     */
    private int team;

    private PlayerController playerController;

    private GameWorld gameWorld;

    private Array<Entity> players = new Array<Entity>();
    private Entity targetVolcano;

    private Vector2 direction = new Vector2();

    private float lagTime = 0;

    public DumbAIController(int team, PlayerController playerController, GameWorld gameWorld) {
        this.team = team;
        this.playerController = playerController;
        this.gameWorld = gameWorld;
    }

    public void update(float delta) {
        setUp();

        if(lagTime > 0) {
            lagTime -= delta;
            return;
        }

        lagTime = MathUtils.random(0.3f, 1f);

        Entity sacrifice = null;

        // Check if sacrifice is alive
        Array<Entity> sacrifices = gameWorld.getEntityManager().getEntitiesClass(Sacrifice.class);
        if(sacrifices.size > 0) {
            sacrifice = sacrifices.first();
        } else {
            direction.set(0, 0);
            playerController.getDirection().set(direction);
            // Dead, nothing to do
            return;
        }

        // Find out if someone owns sacrifice
        Entity sacrificeOwner = getSacrificeOwner(players);

        // Get the sacrifice
        if(sacrificeOwner == null) {
            Entity nearestEntity = getEntityNearestSacrifice(players, sacrifice);

            direction.set(sacrifice.getPosition()).sub(nearestEntity.getPosition()).nor();
            playerController.getDirection().set(direction);
        }

        // Go to the volcano
        if(sacrificeOwner != null) {
            direction.set(targetVolcano.getPosition()).sub(sacrificeOwner.getPosition()).nor();
            playerController.getDirection().set(direction);
        }
    }

    private void setUp() {
        // Get the team!
        if(players.size == 0) {
            Array<Entity> allPlayers = gameWorld.getEntityManager().getEntitiesClass(Player.class);
            for(Entity player : allPlayers) {
                if(((Player)player).getTeam() == team) players.add(player);
            }
        }

        // Get the volcano
        if(targetVolcano == null) {
            Array<Entity> volcanos = gameWorld.getEntityManager().getEntitiesClass(Flame.class);
            for(Entity volcano : volcanos) {
                if(((Flame)volcano).team != team) targetVolcano = volcano;
            }
        }
    }

    private Entity getSacrificeOwner(Array<Entity> players) {
        for(Entity player : players) {
            if (((Player)player).sacrifice != null) return player;
        }

        return null;
    }

    private Entity getEntityNearestSacrifice(Array<Entity> players, Entity sacrifice) {
        Entity nearestEnt = null;
        float dst = 0;

        for(Entity player : players) {
            float tempDst = player.getPosition().dst2(sacrifice.getPosition());

            if(nearestEnt == null || tempDst < dst) {
                // First one
                nearestEnt = player;
                dst = tempDst;
                continue;
            }
        }

        return nearestEnt;
    }

}
