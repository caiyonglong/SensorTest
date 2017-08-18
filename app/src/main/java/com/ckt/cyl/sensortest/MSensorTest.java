package com.ckt.cyl.sensortest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MSensorTest extends AppCompatActivity implements View.OnClickListener {
    TextView mCompassTV;
    TextView mCompassTV1;
    TextView record;
    ImageView mCompassIV;
    ImageView mCompassIV1;

    Sensor aSensor;
    Sensor mSensor;

    //方向传感器
    Sensor oSensor;

    SensorManager sm;

    Button[] button = new Button[8];
    int[] btn = new int[]{
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
    };

    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msensor);

        record = (TextView) findViewById(R.id.record);
        mCompassTV = (TextView) findViewById(R.id.compass_num);
        mCompassIV = (ImageView) findViewById(R.id.compass);


        for (int i = 0; i < 8; i++) {
            button[i] = (Button) findViewById(btn[i]);
            button[i].setOnClickListener(this);
        }

        startSensorTest();

    }


    private void startSensorTest() {
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        oSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);


//        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(listener, oSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sm.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sm.registerListener(listener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    mCompassTV.setText("" + event.values[0]);
                    updateCompassOrient(event.values[0]);
                    break;
            }

//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                accelerometerValues = event.values;
//            }
//            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                magneticFieldValues = event.values;
//            }
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

    private float currentDegree = 0.0f;

    /**
     * d更新视图
     *
     * @param degree
     */
    public void updateCompassOrient(float degree) {
        float abs_val = currentDegree - degree;

        //if(abs_val>2 || abs_val < -2){
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(100);
        //ra.setRepeatMode(RotateAnimation.REVERSE);
        ra.setFillAfter(true);
        currentDegree = -degree;
        mCompassIV.startAnimation(ra);
        //}

    }

    int x = 0;

    @Override
    public void onClick(View view) {
        for (int i = 0; i < 8; i++) {
            if (view.getId() == btn[i]) {
                float x = Float.parseFloat(mCompassTV.getText().toString());
                float z = 45 * i - x;
                //350
                if (x == 7) {
                    z = -360 + 45 * i;
                }

                stringBuilder.insert(0, "第" + ++x + "次误差 = " + z + "\n");

                record.setText(stringBuilder.toString());
            }
        }
    }


//    private float currentDegree1 = 0.0f;

//    public void updateCompassM(float degree) {
//        float abs_val = currentDegree1 - degree;
//
//        //if(abs_val>2 || abs_val < -2){
//        RotateAnimation ra = new RotateAnimation(currentDegree1, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        ra.setDuration(100);
//        //ra.setRepeatMode(RotateAnimation.REVERSE);
//        ra.setFillAfter(true);
//        currentDegree1 = -degree;
//        mCompassIV1.startAnimation(ra);
//        //}
//
//    }

//    private float[] accelerometerValues = new float[3];
//    private float[] magneticFieldValues = new float[3];
//
//    // 计算方向
//    private void calculateOrientation() {
//        float[] values = new float[3];
//        float[] R = new float[9];
//        SensorManager.getRotationMatrix(R, null, accelerometerValues,
//                magneticFieldValues);
//        SensorManager.getOrientation(R, values);
//        values[0] = (float) Math.toDegrees(values[0]);
//        if (values[0] > 0) {
//            mCompassTV1.setText("角度：" + (360 - values[0]));
//            updateCompassM(360 - values[0]);
//        } else {
//            mCompassTV1.setText("角度：" + (360 + values[0]));
//            updateCompassM(360 + values[0]);
//        }
//    }

}
