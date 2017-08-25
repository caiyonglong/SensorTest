package com.ckt.test.sensortest;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.test.sensortest.adapter.MyAdapter;
import com.ckt.test.sensortest.bean.MHSensor;
import com.ckt.test.sensortest.db.SensorLab;
import com.ckt.test.sensortest.utils.ExcelHelper;
import com.ckt.test.sensortest.utils.PermissionUtils;
import com.ckt.test.sensortest.utils.SensorType;

import java.util.ArrayList;
import java.util.List;

public class CompassActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 控件
     */
    private TextView mTvCompass, mTvReference, mTvCalibration;
    private ImageView mIvCompass;
    private Button mBtnRecord, mBtnClear, mBtnExport;
    private RecyclerView mRvRecord;

    /**
     * 传感器
     */
    private Sensor aSensor;
    private Sensor mSensor;
    private SensorManager sm;

    /**
     * 数据
     */
    List<MHSensor> sensorBeans = new ArrayList<>();
    MyAdapter myAdapter;
    int[] angles = new int[]{0, 45, 90, 135, 180, 225, 270, 315};

    //参考值
    private int angle = 0;


    //精确值次数
    private static final int MAX_ACCURATE_COUNT = 20;
    private static final int MAX_INACCURATE_COUNT = 20;

    private volatile int mAccurateCount;
    private volatile int mInaccurateCount;

    private volatile boolean mCalibration;


    private float currentDegree = 0.0f;
    private float mTargetDirection;

    private static final int MATRIX_SIZE = 9;

    private int mMagneticFieldAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];


    private void resetAccurateCount() {
        mAccurateCount = 0;
    }

    private void increaseAccurateCount() {
        mAccurateCount++;
    }

    private void resetInaccurateCount() {
        mInaccurateCount = 0;
    }

    private void increaseInaccurateCount() {
        mInaccurateCount++;
    }

    /**
     * 校准选择
     *
     * @param calibration
     */
    private void switchMode(boolean calibration) {
        mCalibration = calibration;
        if (calibration) {
            mTvCalibration.setVisibility(View.VISIBLE);
            resetAccurateCount();
        } else {
            mTvCalibration.setVisibility(View.GONE);
            Toast.makeText(this, R.string.calibrate_success, Toast.LENGTH_SHORT).show();
            resetInaccurateCount();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msensor);
        initView();
        initData();
    }


    private void initView() {
        mTvCalibration = (TextView) findViewById(R.id.tv_calibration);
        mTvCompass = (TextView) findViewById(R.id.tv_compass);
        mTvReference = (TextView) findViewById(R.id.tv_refer);

        mIvCompass = (ImageView) findViewById(R.id.iv_compass);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnExport = (Button) findViewById(R.id.btn_export);
        mRvRecord = (RecyclerView) findViewById(R.id.rv_record);

        sensorBeans = SensorLab.get(this).getRecords(SensorType.TYPE_MSENSOR);
        myAdapter = new MyAdapter(this, sensorBeans);
        mRvRecord.setLayoutManager(new LinearLayoutManager(this));
        mRvRecord.setAdapter(myAdapter);

        mBtnClear.setOnClickListener(this);
        mBtnRecord.setOnClickListener(this);
        mBtnExport.setOnClickListener(this);
    }

    private void initData() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /***
     * 在onStop中注销传感器的监听事件
     */
    @Override
    protected void onStop() {
        super.onStop();
        sm.unregisterListener(listener);
    }

    /***
     * 在onStart中注册传感器的监听事件
     */
    @Override
    protected void onStart() {
        super.onStart();
        sm.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * 传感器的监听对象
     */
    SensorEventListener listener = new SensorEventListener() {
        //传感器改变时,一般是通过这个方法里面的参数确定传感器状态的改变
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
                mMagneticFieldAccuracy = event.accuracy;
                calculateTargetDirection();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //当精准度改变时
        }
    };


    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                sensorBeans.clear();
                SensorLab.get(this).delete(SensorType.TYPE_MSENSOR);
                myAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_record:
                float direction = normalizeDegree(mTargetDirection * -1.0f);
                //计算偏差角度
                float z = 0;
                if (direction >= 337.5)
                    z = 360 - direction;
                else
                    z = direction - angle;
                z = Math.abs(z);
                MHSensor bean =
                        new MHSensor(SensorType.TYPE_MSENSOR, angle + "", z + "", z < 5);
                sensorBeans.add(0, bean);
                SensorLab.get(this).addRecord(bean);
                myAdapter.notifyDataSetChanged();
                mRvRecord.scrollToPosition(0);
                break;
            case R.id.btn_export:
                new AlertDialog.Builder(CompassActivity.this)
                        .setMessage("导出Excel表格")
                        .setTitle("导出Excel表格")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (PermissionUtils.verifyStoragePermissions(CompassActivity.this)) {
                                    try {
                                        String xx = ExcelHelper.createExcel(
                                                CompassActivity.this, SensorType.TYPE_MSENSOR);
                                        Snackbar.make(view, "导出路径：" + xx, Snackbar.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Snackbar.make(view, "导出失败", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
        }

    }

    /**
     * 更新指南针
     *
     * @param degree
     */
    public void updateCompassM(float degree) {

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(100);
        ra.setFillAfter(true);
        currentDegree = -degree;
        mIvCompass.startAnimation(ra);
    }

    /**
     * 更新方向
     */
    private void updateDirection() {
        float direction = normalizeDegree(mTargetDirection * -1.0f);
        angle = 0;
        for (int i = 0; i < angles.length; i++) {
            if (Math.abs(angles[i] - direction) <= 22.5) {
                angle = angles[i];
                break;
            }
        }
        mTvReference.setText(angle + "");
        mTvCompass.setText(direction + "");
        updateCompassM(direction);
    }


    /**
     * 计算方向
     */

    private void calculateTargetDirection() {
        synchronized (this) {
            //磁通量
            double data = Math.sqrt(Math.pow(magneticFieldValues[0], 2)
                    + Math.pow(magneticFieldValues[1], 2)
                    + Math.pow(magneticFieldValues[2], 2));
            Log.d("Compass", "data = " + data);

            //取20组数据判断指南针是否校准成功
            if (mCalibration) {
                if (mMagneticFieldAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE
                        && (data >= 30 && data <= 60)) {
                    increaseAccurateCount();
                } else {
                    resetAccurateCount();
                }

                Log.d("Compass", "accurate count = " + mAccurateCount);

                if (mAccurateCount >= MAX_ACCURATE_COUNT) {
                    switchMode(false);
                }

            } else {
                if (mMagneticFieldAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE || (data < 30 || data > 60)) {
                    increaseInaccurateCount();
                } else {
                    resetInaccurateCount();
                }

                Log.d("Compass", "inaccurate count = " + mInaccurateCount);

                if (mInaccurateCount >= MAX_INACCURATE_COUNT) {
                    switchMode(true);
                }
            }
            //校准成功后获取数据，生成方向
            if (magneticFieldValues != null && accelerometerValues != null) {
                float[] R = new float[MATRIX_SIZE];
                if (SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);

                    Log.d("Compass", "orientation[0] = " + orientation[0]);
                    float direction = (float) Math.toDegrees(orientation[0]) * -1.0f;
                    Log.d("Compass", "direction = " + direction);
                    mTargetDirection = normalizeDegree(direction);
                    updateDirection();
                    Log.d("Compass", "mTargetDirection = " + mTargetDirection);
                } else {
                    Log.d("Compass", "Error: SensorManager.getRotationMatrix");
                }
            }
        }
    }

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }


}
