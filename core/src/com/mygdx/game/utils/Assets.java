package com.mygdx.game.utils;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by PiotrJ on 30/01/16.
 */
public class Assets {
    private AssetManager manager;
    private ObjectMap<Class, Array<String>> typeToName;

    public Assets() {
        manager = new AssetManager();
        typeToName = new ObjectMap<Class, Array<String>>();
    }

    public <T> Assets queueLoad (String fileName, Class<T> type) {
        manager.load(fileName, type);
        Array<String> names = typeToName.get(type);
        if (names == null) {
            names = new Array<String>();
            typeToName.put(type, names);
        }
        names.add(fileName);
        return this;
    }

    public <T> void queueLoad (String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        manager.load(fileName, type, parameter);
        Array<String> names = typeToName.get(type);
        if (names == null) {
            names = new Array<String>();
            typeToName.put(type, names);
        }
        names.add(fileName);
    }

    public <T, P extends AssetLoaderParameters<T>> void setLoader (Class<T> type, AssetLoader<T, P> loader) {
        manager.setLoader(type, loader);
    }

    private boolean loaded;
    public boolean update() {
        if (!loaded && manager.update()) {
            loaded = true;
            finishLoading();
        }
        return loaded;
    }

    private void finishLoading () {
        // do we need to do anything?
    }

    public <T> T get (String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }

    public Texture getTexture(String name) {
        return manager.get(name, Texture.class);
    }

    public Sound getSound(String name) {
        return manager.get(name, Sound.class);
    }

    public Music getMusic(String name) {
        return manager.get(name, Music.class);
    }

    public TextureAtlas getAtlas(String name) {
        return manager.get(name, TextureAtlas.class);
    }

    private ObjectMap<String, TextureAtlasCache> nameToCache = new ObjectMap<String, TextureAtlasCache>();
    public TextureAtlasCache getAtlasCache(String name) {
        TextureAtlasCache cache = nameToCache.get(name, null);
        if (cache == null) {
            TextureAtlas atlas = getAtlas(name);
            if (atlas == null) return null;
            cache = new TextureAtlasCache(atlas);
            nameToCache.put(name, cache);
        }
        return cache;
    }

    public TextureAtlas.AtlasRegion getAtlasRegion (String name, String atlas) {
        TextureAtlasCache cache = getAtlasCache(atlas);
        if (cache == null) return null;
        return cache.getAtlasRegion(name);
    }

    public void dispose () {
        manager.dispose();
    }

    public AssetManager getManager () {
        return manager;
    }

    public static class TextureAtlasCache {
        private ObjectMap<String, TextureAtlas.AtlasRegion> nameToRegion = new ObjectMap<String, TextureAtlas.AtlasRegion>();
        private ObjectMap<String, Array<TextureAtlas.AtlasRegion>> nameToRegions = new ObjectMap<String, Array<TextureAtlas.AtlasRegion>>();
        private TextureAtlas atlas;

        public TextureAtlasCache (TextureAtlas atlas) {
            this.atlas = atlas;
        }

        public TextureAtlas.AtlasRegion getAtlasRegion (String name) {
            TextureAtlas.AtlasRegion region = nameToRegion.get(name, null);
            if (region == null) {
                region = atlas.findRegion(name);
                if (region == null) return null;
                nameToRegion.put(name, region);
            }
            return region;
        }

        public Array<TextureAtlas.AtlasRegion> getAtlasRegions (String name) {
            Array<TextureAtlas.AtlasRegion> regions = nameToRegions.get(name, null);
            if (regions == null) {
                regions = atlas.findRegions(name);
                nameToRegions.put(name, regions);
            }
            return regions;
        }

        public TextureAtlas getAtlas () {
            return atlas;
        }
    }
}
