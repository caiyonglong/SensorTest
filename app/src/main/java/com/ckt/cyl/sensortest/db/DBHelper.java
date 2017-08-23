package com.ckt.cyl.sensortest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ckt.cyl.sensortest.db.SensorDbSchema.HSensorTable;
import static com.ckt.cyl.sensortest.db.SensorDbSchema.MSensorTable;

/**
 * Created by D22434 on 2017/8/22.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "sensor.db";
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + MSensorTable.NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MSensorTable.Cols.ANGLE + " , "
                + MSensorTable.Cols.DEVIATION + " )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + HSensorTable.NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HSensorTable.Cols.STATUS + " , "
                + HSensorTable.Cols.INTERVAL + " interval)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

