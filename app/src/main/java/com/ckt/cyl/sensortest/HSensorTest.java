package com.ckt.cyl.sensortest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HSensorTest extends AppCompatActivity {

    Button btn_sure;
    EditText et_deviation;
    TextView tv_record, tv_status, tv_deviation;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter intentFilter;
    int status = 0;

    long deviation = 0;


    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hsensor);


        intentFilter = new IntentFilter();
        intentFilter.addAction("factory_hallsensor_test");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        tv_record = (TextView) findViewById(R.id.tv_record);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_deviation = (TextView) findViewById(R.id.tv_deviation);
        et_deviation = (EditText) findViewById(R.id.et_deviation);
        btn_sure = (Button) findViewById(R.id.btn_sure);

        tv_deviation.setText("无最大误差值");
        mBroadcastReceiver = new HallSensorReceiver();

        registerReceiver(mBroadcastReceiver, intentFilter);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_deviation.getText().toString().equals("")) {
                    deviation = Long.parseLong(et_deviation.getText().toString());
                    tv_deviation.setText("最大误差值：" + deviation + " ms");
                } else {
                    Snackbar.make(view, "请输入最大误差值", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    long startonTime, startOffTime;

    public void onLidstatusChanged(int lidstatus) {
        status = lidstatus;
        if (status == 0) {
            startonTime = System.currentTimeMillis();
            tv_status.setText("开盖：status：" + lidstatus + "时间：" + startonTime);
        } else {
            startOffTime = System.currentTimeMillis();
            tv_status.setText("合盖：status：" + lidstatus + "时间：" + startOffTime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (status == 0) {
            long time = System.currentTimeMillis() - startonTime;
            showRecord(time, status);
        }
        Log.e("XXX", "亮屏..." + System.currentTimeMillis());
    }

    @Override
    protected void onResume() {
        super.onResume();
        onLidstatusChanged(status);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (status == 1) {
            long time = System.currentTimeMillis() - startOffTime;
            showRecord(time, status);
        }
        Log.e("XXX", "灭屏..." + System.currentTimeMillis());
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }


    int k = 0;

    class HallSensorReceiver extends BroadcastReceiver {
        public int lidstatus;

        @Override
        public void onReceive(Context context, Intent intent) {

//            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
//                if (status == 0) {
//                    long time = System.currentTimeMillis() - startonTime;
//                    showRecord(time, status);
//                }
//                Log.e("XXX", "亮屏..." + System.currentTimeMillis());
//
//            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
//                if (status == 1) {
//                    long time = System.currentTimeMillis() - startOffTime;
//                    showRecord(time, status);
//                }
//                Log.e("XXX", "灭屏..." + System.currentTimeMillis());
//            }
            if (intent.getAction() != "factory_hallsensor_test") {
                return;
            } else {
                lidstatus = intent.getIntExtra("hallsensor", -1);
                status = lidstatus;
                if (HSensorTest.this != null) {
                    HSensorTest.this.onLidstatusChanged(lidstatus);
                }
            }
        }
    }

    String[] tt = new String[]{"开盖 -> 亮屏：", "合盖 -> 灭屏："};

    private void showRecord(long time, int status) {
        if (deviation > 0)
            stringBuilder.insert(0, "test " + ++k + tt[status] + time + "\t 测试结果：" + (deviation > time) + "\n");
        else {
            stringBuilder.insert(0, "test " + ++k + tt[status] + time + "\n");
        }
        tv_record.setText(stringBuilder.toString());
    }

}
