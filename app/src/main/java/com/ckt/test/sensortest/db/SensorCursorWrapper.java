package com.ckt.test.sensortest.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ckt.test.sensortest.bean.MHSensor;

import static com.ckt.test.sensortest.db.SensorDbSchema.SensorTable;

/**
 * Created by D22434 on 2017/7/24.
 */

public class SensorCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public SensorCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public MHSensor getMHSensor() {
        int type = Integer.parseInt(getString(getColumnIndex(SensorTable.Cols.TYPE)));
        String field = getString(getColumnIndex(SensorTable.Cols.FIELD));
        String value = getString(getColumnIndex(SensorTable.Cols.VALUE));
        int result = getInt(getColumnIndex(SensorTable.Cols.RESULT));


        MHSensor mhSensor = new MHSensor();
        mhSensor.setType(type);
        mhSensor.setField(field);
        mhSensor.setValue(value);
        mhSensor.setResult(result == 1);

        return mhSensor;
    }
}
