package com.ckt.cyl.sensortest.bean;

/**
 * Created by D22434 on 2017/8/22.
 */

public class HSensor {
    int status;
    long interval;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "状态=" + status + " 响应时间=" + interval + "\n";
    }
}
