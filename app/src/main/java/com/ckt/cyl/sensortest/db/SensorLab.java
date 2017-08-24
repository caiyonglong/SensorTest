package com.ckt.cyl.sensortest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ckt.cyl.sensortest.bean.MHSensor;

import java.util.ArrayList;
import java.util.List;

import static com.ckt.cyl.sensortest.db.SensorDbSchema.SensorTable;

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
    public void addRecord(MHSensor sensor) {
        ContentValues values = new ContentValues();
        values.put(SensorTable.Cols.TYPE, sensor.getType() + "");
        values.put(SensorTable.Cols.FIELD, sensor.getField());
        values.put(SensorTable.Cols.VALUE, sensor.getValue());
        values.put(SensorTable.Cols.RESULT, sensor.isResult());
        mDataBase.insert(SensorTable.MHSENSOR, null, values);
    }

    /**
     * 获取
     */
    public List<MHSensor> getRecords(int type) {
        List<MHSensor> mhSensors = new ArrayList<>();
        SensorCursorWrapper cursor = querySensors(SensorTable.Cols.TYPE + " = ?",
                new String[]{type + ""});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mhSensors.add(cursor.getSensor());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mhSensors;
    }

    /**
     * 删除记录
     */
    public void delete(int type) {
        mDataBase.delete(SensorTable.MHSENSOR, SensorTable.Cols.TYPE + " = ?",
                new String[]{type + ""});
    }

    /**
     * 使用cursor封装方法
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private SensorCursorWrapper querySensors(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(
                SensorTable.MHSENSOR,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                "_id desc"
        );
        return new SensorCursorWrapper(cursor);
    }

}
