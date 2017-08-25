package com.ckt.test.sensortest.utils;

import android.content.Context;
import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by D22434 on 2017/8/25.
 */

public class FileUtils {

    Context mContext;
    String path;

    public FileUtils(Context context) {
        mContext = context;
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorTest";

    }

    public String saveFile(HSSFWorkbook workbook, String filename) {

        File file = new File(path, filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "文件创建失败";
        }
        return file.getAbsolutePath();
    }
}
