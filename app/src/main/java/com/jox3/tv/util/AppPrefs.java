package com.jox3.tv.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jox3.tv.model.MediaItem;
import com.jox3.tv.model.PlaylistConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppPrefs {

    private static final String PREFS_NAME = "jox3tv_prefs";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_PLAYLIST_CONFIG = "playlist_config";
    private static final String KEY_CONTINUE_WATCHING = "continue_watching";
    private static final int MAX_CONTINUE_WATCHING = 12;
    private static final String PREFIX_POS = "pos_";
    private static final String PREFIX_DUR = "dur_";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public AppPrefs(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFav(String favKey) {
        return getFavorites().contains(favKey);
    }

    public void toggleFav(String favKey) {
        Set<String> favs = new HashSet<>(getFavorites());
        if (favs.contains(favKey)) favs.remove(favKey);
        else favs.add(favKey);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public Set<String> getFavorites() {
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }

    public void saveProgress(String itemId, long positionMs, long durationMs) {
        prefs.edit()
                .putLong(PREFIX_POS + itemId, positionMs)
                .putLong(PREFIX_DUR + itemId, durationMs)
                .apply();
    }

    public long getPos(String itemId) {
        return prefs.getLong(PREFIX_POS + itemId, 0);
    }

    public long getDur(String itemId) {
        return prefs.getLong(PREFIX_DUR + itemId, 0);
    }

    public void addRecentlyWatched(MediaItem item) {
        if (item == null) return;
        List<MediaItem> list = getRecentlyWatched();

        list.removeIf(existing -> existing.favKey().equals(item.favKey()));
        list.add(0, item);

        while (list.size() > MAX_CONTINUE_WATCHING) {
            list.remove(list.size() - 1);
        }

        prefs.edit().putString(KEY_CONTINUE_WATCHING, gson.toJson(list)).apply();
    }

    public List<MediaItem> getRecentlyWatched() {
        String json = prefs.getString(KEY_CONTINUE_WATCHING, null);
        if (json == null) return new ArrayList<>();
        try {
            Type listType = new TypeToken<ArrayList<MediaItem>>(){}.getType();
            List<MediaItem> list = gson.fromJson(json, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void removeFromContinueWatching(String favKey) {
        List<MediaItem> list = getRecentlyWatched();
        list.removeIf(existing -> existing.favKey().equals(favKey));
        prefs.edit().putString(KEY_CONTINUE_WATCHING, gson.toJson(list)).apply();
    }

    public void savePlaylistConfig(PlaylistConfig config) {
        prefs.edit().putString(KEY_PLAYLIST_CONFIG, gson.toJson(config)).apply();
    }

    public PlaylistConfig getPlaylistConfig() {
        String json = prefs.getString(KEY_PLAYLIST_CONFIG, null);
        if (json == null) return null;
        try {
            return gson.fromJson(json, PlaylistConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearPlaylistConfig() {
        prefs.edit().remove(KEY_PLAYLIST_CONFIG).apply();
    }

    private static final String KEY_CRASH_LOG = "last_crash_log";

    public void saveCrashLog(String stackTrace) {
        prefs.edit().putString(KEY_CRASH_LOG, stackTrace).apply();
    }

    public String getCrashLog() {
        return prefs.getString(KEY_CRASH_LOG, null);
    }

    public void clearCrashLog() {
        prefs.edit().remove(KEY_CRASH_LOG).apply();
    }
}
