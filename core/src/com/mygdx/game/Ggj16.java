package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.GameScreen;

public class Ggj16 extends Game {

	private FPSLogger log;

	@Override
	public void create () {
		G.game = this;

		log = new FPSLogger();

		// No assets to load so go straight to the game
		G.game.setScreen(new GameScreen());
	}

	@Override
	public void render() {
		super.render();
		log.log();
	}
}
