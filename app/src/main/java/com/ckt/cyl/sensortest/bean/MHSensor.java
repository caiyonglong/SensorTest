package com.ckt.cyl.sensortest.bean;

import java.io.Serializable;

/**
 * Created by D22434 on 2017/8/22.
 */

public class MHSensor implements Serializable {
    int type;
    int field;
    String value;
    boolean result;


    public MHSensor() {
    }

    public MHSensor(int type, int field, String value, boolean result) {
        this.type = type;
        this.field = field;
        this.value = value;
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "状态=" + field + " 响应时间=" + value + "\t" + " 结果=" + result + "\n";
    }
}
