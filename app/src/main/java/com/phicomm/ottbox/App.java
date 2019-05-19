package com.phicomm.ottbox;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class App extends Application {
    private static final String PREFNAME = "target";
    private static final String PREF_PKG = "package";
    private static final String PREF_CLS = "class";

    static ComponentName TARGET_COMPONENT = null;
    public static ComponentName getTargetComponent() {
        return TARGET_COMPONENT;
    }

    public static Intent createLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TARGET_COMPONENT = load();
    }

    static void save(Context context, ComponentName componentName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PREF_PKG, componentName.getPackageName()).putString(PREF_CLS, componentName.getClassName()).apply();
    }

    ComponentName load() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        String pkg = sharedPreferences.getString(PREF_PKG, null);
        String cls = sharedPreferences.getString(PREF_CLS, null);
        if (pkg != null && cls != null) {
            return new ComponentName(pkg, cls);
        }
        return null;
    }
}
