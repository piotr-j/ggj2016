package com.mygdx.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.game.G;
import com.mygdx.game.model.GameWorld;

/**
 * @author Lukasz Zmudziak, @lukz_dev on 2016-01-31.
 */
public class ScoreDisplay extends Table {

    private Label scoreLabel;

    private Image team1Color;
    private Image team2Color;

    public ScoreDisplay() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = G.assets.get(G.A.FONT_UNIVERSIDAD, BitmapFont.class);

        scoreLabel = new Label("00:00", style);
        scoreLabel.setAlignment(Align.center);

        team1Color = new Image(G.assets.getAtlas(G.A.ATLAS).findRegion(G.A.PIXEL));
        team1Color.setAlign(Align.center);
        team1Color.setColor(Color.valueOf("60a7fc"));

        team2Color = new Image(G.assets.getAtlas(G.A.ATLAS).findRegion(G.A.PIXEL));
        team2Color.setAlign(Align.center);
        team2Color.setColor(Color.valueOf("fc3552"));

        Stack stack = new Stack();

        Table colorTable = new Table();
        colorTable.add(team1Color).width(80).height(80);
        colorTable.add(team2Color).width(80).height(80);

        stack.add(colorTable);
        stack.add(scoreLabel);

        add(stack);
    }

    private StringBuilder sb = new StringBuilder();
    public void updateScore(GameWorld gameWorld) {
        sb.setLength(0);
        if (gameWorld.getTeam1Score() < 10) {
            sb.append(0);
        }
        sb.append(gameWorld.getTeam1Score());
        sb.append(":");
        if (gameWorld.getTeam2Score() < 10) {
            sb.append(0);
        }
        sb.append(gameWorld.getTeam2Score());
        scoreLabel.setText(sb);
    }
}
