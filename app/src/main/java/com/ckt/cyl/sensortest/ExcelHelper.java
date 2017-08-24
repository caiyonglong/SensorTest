package com.ckt.cyl.sensortest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ckt.cyl.sensortest.bean.MHSensor;
import com.ckt.cyl.sensortest.db.SensorLab;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 使用POI创建Excel示例
 */
public class ExcelHelper {

    private static String[] test = new String[]{"参考角度", "状态"};
    private static String[] value = new String[]{"偏差角度", "响应时间"};

    /**
     * 表格数据和SQLite中的要对应
     */
    public static String createExcel(Context context, int type, String filename) throws Exception {
        // 创建文档
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet(filename);

        List<MHSensor> sensors = SensorLab.get(context).getRecords(type);

        Object[] v = {"ID", test[0], value[0], "测试结果"};
        insertRow(sheet, (short) 0, v, null);
        int size = sensors.size();
        Log.d("xxx", size + "---");
        if (size > 0) {
            for (short i = 1; i <= size; i++) {
                MHSensor sensor = sensors.get(i - 1);
                Object[] values = {i,sensor.getField(), sensor.getValue(), sensor.isResult() ? "pass" : "fail"};
                insertRow(sheet, i, values, null);
            }
        }

        String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/SensorTest";
        // 保存文档
        new File(ROOT_PATH).mkdirs();
        File file = new File(ROOT_PATH, filename + ".xls");
        FileOutputStream fos;
        if (!file.exists()) {
            file.createNewFile();
        }
        fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
        return file.getAbsolutePath();
    }

    public static void deleteExcel() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sensor_test ";
        File file = new File(dirPath, "sensor_test.xls");
        file.delete();
    }

    /**
     * 插入一行数据
     *
     * @param sheet        插入数据行的表单
     * @param rowIndex     插入的行的索引
     * @param columnValues 要插入一行中的数据，数组表示
     * @param cellStyle    该格中数据的显示样式
     */

    private static void insertRow(HSSFSheet sheet, short rowIndex,
                                  Object[] columnValues, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(rowIndex);
        int column = columnValues.length;
        for (short i = 0; i < column; i++) {
            createCell(row, i, columnValues[i], cellStyle);
        }
    }

    /**
     * 在一行中插入一个单元值
     *
     * @param row         要插入的数据的行
     * @param columnIndex 插入的列的索引
     * @param cellValue   该cell的值：如果是Calendar或者Date类型，就先对其格式化
     * @param cellStyle   该格中数据的显示样式
     */
    private static void createCell(HSSFRow row, short columnIndex, Object cellValue,
                                   HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(columnIndex);
        // 如果是Calender或者Date类型的数据，就格式化成字符串
        if (cellValue instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(cellValue);
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else if (cellValue instanceof Calendar) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(((Calendar) cellValue).getTime());
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else {
            HSSFRichTextString richTextString = new HSSFRichTextString(cellValue.toString());
            cell.setCellValue(richTextString);
        }
        cell.setCellStyle(cellStyle);
    }

}
