package com.ckt.cyl.sensortest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ckt.cyl.sensortest.bean.MSensor;
import com.ckt.cyl.sensortest.db.SensorDbSchema.MSensorTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D22434 on 2017/8/23.
 */

public class SensorLab {
    private static SensorLab sSensorLab;

    private Context mContext;
    private SQLiteDatabase mDataBase;

    public static SensorLab get(Context context) {
        if (sSensorLab == null) {
            sSensorLab = new SensorLab(context);
        }
        return sSensorLab;
    }

    private SensorLab(Context context) {
        mContext = context.getApplicationContext();
        mDataBase = DBHelper.getInstance(mContext).getWritableDatabase();
    }

    /**
     * 插入记录
     */
    public void addMSensor(MSensor mSensor) {
        ContentValues values = new ContentValues();
        values.put("angle", mSensor.getAngle());
        values.put("deviation", mSensor.getDeviation());
        mDataBase.insert(MSensorTable.NAME, null, values);
    }

}
