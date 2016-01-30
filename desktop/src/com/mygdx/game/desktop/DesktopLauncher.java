package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.game.Ggj16;

public class DesktopLauncher {
	public static void main (String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        // strip so we dont have to deal with this in a jam
        settings.stripWhitespaceX = false;
        settings.stripWhitespaceY = false;
        TexturePacker.processIfModified(settings, "raw", "pack", "entities");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new Ggj16(), config);
	}
}
