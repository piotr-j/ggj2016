package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.utils.Assets;

public class G {
    public static boolean DEBUG = false;
    public static boolean DEBUG_BOX2D = false;

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
        public static final String VOLCANO_FIRE = "volcano-fire";
        public static final String SACRIFICE = "ofiara";
        public static final String RAY = "ray";
        public static final String RAY_CIRCLE = "ray-circle";
        public static final String PIXEL = "pixel";

        public static final String SPECT_DANCE= "CHAR_C2";
        public static final String SPECT_CLAP= "CHAR_C3";
        public static final String SPECT_JUMP= "CHAR_C4";

        public static final String SOUND_CHEER1= "sfx/childCheer1.ogg";
        public static final String SOUND_CHEER2= "sfx/childCheer2.ogg";
        public static final String SOUND_CHEER3= "sfx/childCheer3.ogg";

        // Fonts
        public static final String FONT_UNIVERSIDAD = "universidad.fnt";
        public static final String FONT_COLLEGIER = "collegier2.fnt";
    }
}
