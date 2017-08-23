package com.ckt.cyl.sensortest.bean;

/**
 * Created by D22434 on 2017/8/22.
 */

public class MSensor {
    int angle;
    float deviation;

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public float getDeviation() {
        return deviation;
    }

    public void setDeviation(float deviation) {
        this.deviation = deviation;
    }

    @Override
    public String toString() {
        return "测量值=" + angle + " 偏差=" + deviation + "\n";
    }
}
