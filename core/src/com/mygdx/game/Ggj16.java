package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.screens.SplashScreen;
import com.mygdx.game.utils.Assets;

public class Ggj16 extends Game {

	private FPSLogger log;
	private Assets assets;

	@Override
	public void create () {
		G.game = this;

		log = new FPSLogger();
		assets = new Assets();
		assets.queueLoad("pack/entities.atlas", TextureAtlas.class);
        // TODO make it work with atlas
		assets.queueLoad("pack/eruption.p", ParticleEffect.class);
		assets.queueLoad("pack/spawn.p", ParticleEffect.class);
		G.assets = assets;

		// No assets to load so go straight to the game
		G.game.setScreen(new SplashScreen());

	}

	@Override
	public void render() {
		super.render();
		log.log();
	}

	@Override public void dispose () {
		super.dispose();
		assets.dispose();
	}
}
