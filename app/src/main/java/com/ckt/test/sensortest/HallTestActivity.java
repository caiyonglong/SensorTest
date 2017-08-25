package com.ckt.test.sensortest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ckt.test.sensortest.adapter.MyAdapter;
import com.ckt.test.sensortest.bean.MHSensor;
import com.ckt.test.sensortest.db.SensorLab;
import com.ckt.test.sensortest.utils.ExcelHelper;
import com.ckt.test.sensortest.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ckt.test.sensortest.utils.SensorType.TYPE_HSENSOR;

public class HallTestActivity extends AppCompatActivity {

    Button mBtnSure, mBtnExport, mBtnClear;
    EditText mEtDeviation;
    TextView mTvDeviation;
    RecyclerView mRvRecord;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter intentFilter;

    int status = 0;
    long deviation = 0;
    long startOnTime, startOffTime;

    List<MHSensor> msensors = new ArrayList<>();
    MyAdapter myAdapter;

    String[] state = new String[]{"开盖到亮屏", "合盖到灭屏"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hsensor);
        initView();
        initData();
    }

    private void initView() {

        mRvRecord = (RecyclerView) findViewById(R.id.rv_record);
        mTvDeviation = (TextView) findViewById(R.id.tv_deviation);
        mEtDeviation = (EditText) findViewById(R.id.et_deviation);
        mBtnSure = (Button) findViewById(R.id.btn_sure);
        mBtnExport = (Button) findViewById(R.id.btn_export);
        mBtnClear = (Button) findViewById(R.id.btn_clear);

        mTvDeviation.setText("无最大误差值");
        msensors = SensorLab.get(this).getRecords(TYPE_HSENSOR);
        myAdapter = new MyAdapter(this, msensors);
        mRvRecord.setLayoutManager(new LinearLayoutManager(this));
        mRvRecord.setAdapter(myAdapter);


        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEtDeviation.getText().toString().equals("")) {
                    deviation = Long.parseLong(mEtDeviation.getText().toString());
                    mTvDeviation.setText("最大误差值：" + deviation + " ms");
                } else {
                    Snackbar.make(view, "请输入最大误差值", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        mBtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.verifyStoragePermissions(HallTestActivity.this)) {
                    try {
                        String xx = ExcelHelper.createExcel(HallTestActivity.this, TYPE_HSENSOR);
                        Snackbar.make(view, "导出路径：" + xx, Snackbar.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(view, "导出失败", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msensors.clear();
                myAdapter.notifyDataSetChanged();
                SensorLab.get(HallTestActivity.this).delete(TYPE_HSENSOR);
            }
        });
    }

    private void initData() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("factory_hallsensor_test");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mBroadcastReceiver = new HallSensorReceiver();

        registerReceiver(mBroadcastReceiver, intentFilter);
    }


    public void onLidstatusChanged(int lidstatus) {
        status = lidstatus;
        if (status == 0) {
            startOnTime = System.currentTimeMillis();
            Log.d("XXX", "开盖：status：" + lidstatus + "时间：" + startOnTime);
        } else {
            startOffTime = System.currentTimeMillis();
            Log.d("XXX", "合盖：status：" + lidstatus + "时间：" + startOffTime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (status == 0 && startOnTime != 0) {
            long time = System.currentTimeMillis() - startOnTime;
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
        if (status == 1 && startOffTime != 0) {
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


    class HallSensorReceiver extends BroadcastReceiver {
        public int lidstatus;

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
//                if (status == 0) {
//                    long time = System.currentTimeMillis() - startOnTime;
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
                if (HallTestActivity.this != null) {
                    HallTestActivity.this.onLidstatusChanged(lidstatus);
                }
            }
        }
    }

    private void showRecord(long time, int t) {
        MHSensor mhsensor;

        if (deviation == 0)
            mhsensor = new MHSensor(TYPE_HSENSOR, state[t], time + "", true);
        else {
            mhsensor = new MHSensor(TYPE_HSENSOR, state[t], time + "", time <= deviation);
        }
        msensors.add(0, mhsensor);
        SensorLab.get(this).addRecord(mhsensor);
        myAdapter.notifyDataSetChanged();
        mRvRecord.scrollToPosition(0);

    }

}
