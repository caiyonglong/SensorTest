package com.ckt.cyl.sensortest;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by D22434 on 2017/8/18.
 */

public class WakeAndLock {
    Context context;
    PowerManager pm;
    PowerManager.WakeLock wakeLock;

    public WakeAndLock(Context context) {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeAndLock");
    }

    /**
     * 唤醒屏幕
     */
    public void screenOn() {
        wakeLock.acquire();
        android.util.Log.i("cxq", "screenOn");

    }

    /**
     * 熄灭屏幕
     */
    public void screenOff() {
//        pm.goToSleep(SystemClock.uptimeMillis());
        android.util.Log.i("cxq", "screenOff");

    }
}