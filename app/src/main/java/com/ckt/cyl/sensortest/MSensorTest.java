package com.ckt.cyl.sensortest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.cyl.sensortest.bean.MSensor;

import java.util.ArrayList;
import java.util.List;

public class MSensorTest extends AppCompatActivity implements View.OnClickListener {
    TextView mCompassTV;
    TextView show;
    TextView record;
    ImageView mCompassIV;
    FrameLayout mViewGuide;
    Button btn_clear, standard_x;

    Sensor aSensor;
    Sensor mSensor;

    SensorManager sm;


    List<MSensor> sensorBeen = new ArrayList<>();

    int[] angles = new int[]{0, 45, 90, 135, 180, 225, 270, 315};


    private static final int MAX_ACCURATE_COUNT = 20;
    private static final int MAX_INACCURATE_COUNT = 20;

    private volatile int mAccurateCount;
    private volatile int mInaccurateCount;

    private volatile boolean mCalibration;

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

    private void switchMode(boolean calibration) {
        mCalibration = calibration;
        if (calibration) {
            mViewGuide.setVisibility(View.VISIBLE);
            resetAccurateCount();
        } else {
            mViewGuide.setVisibility(View.GONE);
            Toast.makeText(this, R.string.calibrate_success, Toast.LENGTH_SHORT).show();
            resetInaccurateCount();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msensor);
        initView();
        startSensorTest();
    }

    private void initView() {
        record = (TextView) findViewById(R.id.record);
        show = (TextView) findViewById(R.id.show);
        mCompassTV = (TextView) findViewById(R.id.compass_num);
        mViewGuide = (FrameLayout) findViewById(R.id.view_guide);

        mCompassIV = (ImageView) findViewById(R.id.compass);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        standard_x = (Button) findViewById(R.id.standard_x);
        btn_clear.setOnClickListener(this);
        standard_x.setOnClickListener(this);
    }

    private void startSensorTest() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_to_excel) {

            verifyStoragePermissions(this);
            try {
                ExcelHelper.createExcel(sensorBeen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_GAME);
    }

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
        //精准度
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
                double data = Math.sqrt(Math.pow(magneticFieldValues[0], 2) + Math.pow(magneticFieldValues[1], 2)
                        + Math.pow(magneticFieldValues[2], 2));
                show.setText("x = " + event.values[0]
                        + "\ny = " + event.values[1]
                        + "\nz=" + event.values[2]
                        + "\ndata =" + data
                        + "\n data out of range 30-60: " + (data >= 30 && data <= 60));
                Log.d("Compass", "data = " + data);
                calculateTargetDirection();
                updateDirection();

            }
//            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //当精准度改变时
        }
    };

    /***
     * 在onDestroy中注销传感器的监听事件
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(listener);
    }


    int t = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                record.setText("");
                sensorBeen.clear();
                t = 0;
                break;
            case R.id.standard_x:
                float t = Float.parseFloat(standard_x.getText().toString());
                saveAngle(t);
                break;
        }
    }

    /**
     * 保存测试记录并显示
     *
     * @param t 参考值
     * @return
     */
    private void saveAngle(float t) {
        //测量值
        float x = Float.parseFloat(mCompassTV.getText().toString());
        float z = 0;
        if (x >= 315 && t == 0)
            z = 360 - 315;
        else
            z = t - x;
        z = Math.abs(z);

        MSensor bean =
                new MSensor(Sensor.TYPE_MAGNETIC_FIELD, t + "", z);
        sensorBeen.add(0, bean);
        record.setText(sensorBeen.toString());
    }


    private float currentDegree1 = 0.0f;

    public void updateCompassM(float degree) {
        float abs_val = currentDegree1 - degree;

        //if(abs_val>2 || abs_val < -2){
        RotateAnimation ra = new RotateAnimation(currentDegree1, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(100);
        //ra.setRepeatMode(RotateAnimation.REVERSE);
        ra.setFillAfter(true);
        currentDegree1 = -degree;
        mCompassIV.startAnimation(ra);
        //}

    }

    private int mMagneticFieldAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private int angle = 0;

    // 计算方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);

        angle = 0;
        for (int i = 0; i < angles.length; i++) {
            if (Math.abs(angles[i] - (values[0] + 180)) <= 22.5) {
                angle = angles[i];
            }
        }
        standard_x.setText(angle + "");

        mCompassTV.setText(values[0] + 180 + "");
        updateCompassM(values[0]);

    }

    private void updateDirection() {
        float direction = normalizeDegree(mTargetDirection * -1.0f);

        Log.e("XXX", direction + "");
        angle = 0;
        for (int i = 0; i < angles.length; i++) {
            if (Math.abs(angles[i] - direction) <= 22.5) {
                angle = angles[i];
            }
        }
        standard_x.setText(angle + "");

        mCompassTV.setText(direction + "");
        updateCompassM(direction);

    }

    private float mDirection;
    private float mTargetDirection;
    private static final int MATRIX_SIZE = 9;

    private void calculateTargetDirection() {
        synchronized (this) {
            double data = Math.sqrt(Math.pow(magneticFieldValues[0], 2)
                    + Math.pow(magneticFieldValues[1], 2)
                    + Math.pow(magneticFieldValues[2], 2));

            Log.d("Compass", "data = " + data);

            if (mCalibration) {
                if (mMagneticFieldAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE && (data >= 25 && data <= 65)) {
                    increaseAccurateCount();
                } else {
                    resetAccurateCount();
                }

                Log.d("Compass", "accurate count = " + mAccurateCount);

                if (mAccurateCount >= MAX_ACCURATE_COUNT) {
                    switchMode(false);
                }

            } else {
                if (mMagneticFieldAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE || (data < 25 || data > 65)) {
                    increaseInaccurateCount();
                } else {
                    resetInaccurateCount();
                }

                Log.d("Compass", "inaccurate count = " + mInaccurateCount);

                if (mInaccurateCount >= MAX_INACCURATE_COUNT) {
                    switchMode(true);
                }
            }

            if (magneticFieldValues != null && accelerometerValues != null) {
                float[] R = new float[MATRIX_SIZE];
                if (SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float direction = (float) Math.toDegrees(orientation[0]) * -1.0f;
                    mTargetDirection = normalizeDegree(direction);
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
