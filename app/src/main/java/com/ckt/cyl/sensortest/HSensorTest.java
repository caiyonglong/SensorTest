package com.ckt.cyl.sensortest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;

public class HSensorTest extends AppCompatActivity {

    Button button, button2, button3;

    private boolean isManualTest = false;
    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter intentFilter;
    int status = 0;
    PowerManager.WakeLock wakeLock, wakeLock1;
    PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hsensor);


        intentFilter = new IntentFilter();
        intentFilter.addAction("factory_hallsensor_test");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);


        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        mBroadcastReceiver = new HallSensorReceiver();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank");
        wakeLock1 = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Gank");

        registerReceiver(mBroadcastReceiver, intentFilter);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    long startonTime, startoffTime;

    public void onLidstatusChanged(int lidstatus) {
        status = lidstatus;
        if (status == 0) {
            button.setText("开盖=status：" + lidstatus);
            startonTime = System.currentTimeMillis();
        } else {
            button.setText("合盖=status：" + lidstatus);
            startoffTime = new Date().getTime();
        }
    }

    private void startjishi() {
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SystemProperties.set("persist.sys.hallsensor.config", "0");
        onLidstatusChanged(status);
    }

    @Override
    protected void onStop() {
//        SystemProperties.set("persist.sys.hallsensor.config", "1");
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


    class HallSensorReceiver extends BroadcastReceiver {
        public int lidstatus;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                Log.e("XXX", pm.isInteractive() + "--");
                if (status == 0) {
                    button2.setText("开盖->亮屏的响应时间：" + ( System.currentTimeMillis() - startonTime));
                }
                Log.e("XXX", "亮屏啦，，" + System.currentTimeMillis());
                Log.e("XXX", "响应时间" + ( System.currentTimeMillis() - startonTime));
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.e("XXX", pm.isInteractive() + "--");
                if (status == 1) {
                    button2.setText("合盖->灭屏的响应时间：" + ( System.currentTimeMillis() - startoffTime));
                }
                Log.e("XXX", "灭屏啦，，" +  System.currentTimeMillis());
                Log.e("XXX", "响应时间" + ( System.currentTimeMillis()- startoffTime));
            }
            if (intent.getAction() != "factory_hallsensor_test") {
                return;
            } else {
                lidstatus = intent.getIntExtra("hallsensor", -1);
                status = lidstatus;
                if (HSensorTest.this != null) {
                    HSensorTest.this.onLidstatusChanged(lidstatus);
                    android.util.Log.i("XXX", "lidstatus=" + lidstatus + "--" + System.currentTimeMillis());
                }


            }
        }
    }

}
