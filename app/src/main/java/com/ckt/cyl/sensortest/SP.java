package com.ckt.cyl.sensortest;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by D22434 on 2017/8/17.
 */

public class SP {

    public static void put(Context context, String Key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(Key, value)
                .apply();
    }

    public static int get(Context context, String Key, int value) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Key, value);
    }

}
