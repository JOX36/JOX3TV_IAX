package com.jox3.tv;

import android.app.Application;
import android.util.Log;

import com.jox3.tv.util.AppPrefs;

public class JoxTvApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Thread.UncaughtExceptionHandler defaultHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                String trace = Log.getStackTraceString(throwable);
                new AppPrefs(getApplicationContext()).saveCrashLog(trace);
            } catch (Exception ignored) {
            }
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            } else {
                System.exit(1);
            }
        });
    }
}
