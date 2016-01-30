package com.mygdx.game;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.screens.SplashScreen;
import com.mygdx.game.utils.Assets;
import com.mygdx.game.utils.SpriteTween;

public class Ggj16 extends Game {

	private FPSLogger log;
	private Assets assets;

	@Override
	public void create () {
		G.game = this;

		log = new FPSLogger();
		assets = new Assets();
		assets.queueLoad("pack/entities.atlas", TextureAtlas.class);
		G.assets = assets;

		Tween.registerAccessor(Sprite.class, new SpriteTween());

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
