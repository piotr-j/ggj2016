package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.utils.Assets;

public class G {
    public static boolean DEBUG = true;
    public static boolean DEBUG_BOX2D = true;

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

    public static final class A {
        public static final String ATLAS = "pack/entities.atlas";
        public static final String ARENA = "arena";
        public static final String TRIBUNES_TOP = "trybuny_gora";
        public static final String TRIBUNES_BOT = "trybuny_dol";
        public static final String TRIBUNES_LEFT = "trybuny_lewo";
        public static final String TRIBUNES_RIGHT = "trybuny_prawo";
        public static final String TEAMA = "teamA";
        public static final String TEAMB = "teamB";
        public static final String TEAMN = "TeamNeutral";
        public static final String VOLCANO = "volcano-empty";
    }
}
