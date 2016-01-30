package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.utils.Assets;

public class G {
    public static boolean DEBUG = true;

    // Virtual resolution - potato units
    public static float SCALE = 48;
    public static float INV_SCALE = 1f/SCALE;
    public static int TARGET_WIDTH = 1280;
    public static int TARGET_HEIGHT = 720;
    public static float VP_WIDTH = TARGET_WIDTH * INV_SCALE;
    public static float VP_HEIGHT = TARGET_HEIGHT * INV_SCALE;

    // Game instance used to move between screens
    public static Game game;
    public static Assets assets;
}
