package com.mygdx.game;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.screens.SplashScreen;
import com.mygdx.game.utils.Assets;
import com.mygdx.game.utils.EntityTween;
import com.mygdx.game.utils.SpriteTween;

public class Ggj16 extends Game {

	private FPSLogger log;
	private Assets assets;

	@Override
	public void create () {
		G.game = this;

		log = new FPSLogger();
		assets = new Assets();
		assets.queueLoad("pack/ritualball-logo.png", Texture.class);
		assets.queueLoad("pack/entities.atlas", TextureAtlas.class);
        // TODO make it work with atlas
		ParticleEffectParameter parameter = new ParticleEffectParameter();
		parameter.atlasFile = "pack/entities.atlas";
		assets.queueLoad("pack/eruption.p", ParticleEffect.class, parameter);
		assets.queueLoad("pack/spawn.p", ParticleEffect.class, parameter);
		assets.queueLoad("pack/trail.p", ParticleEffect.class, parameter);
		assets.queueLoad("pack/konfetti.p", ParticleEffect.class, parameter);

		// Sfx
		assets.queueLoad("sfx/childCheer1.ogg", Sound.class);
		assets.queueLoad("sfx/childCheer2.ogg", Sound.class);
		assets.queueLoad("sfx/childCheer3.ogg", Sound.class);

		// Font
		assets.queueLoad("universidad.fnt", BitmapFont.class);
		assets.queueLoad("collegier2.fnt", BitmapFont.class);

		assets.queueLoad("pack/uiskin.json", Skin.class);
		G.assets = assets;

		Tween.registerAccessor(Sprite.class, new SpriteTween());
		Tween.registerAccessor(Entity.class, new EntityTween());

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
