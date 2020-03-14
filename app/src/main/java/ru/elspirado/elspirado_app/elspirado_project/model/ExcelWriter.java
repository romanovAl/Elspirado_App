package ru.elspirado.elspirado_app.elspirado_project.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWriter {

    private boolean success = false;

    public interface Callback {
        void excelWriterCallingBack(boolean success, String filePath);
    }

    private Callback callback;

    public void saveExcelFileInRange(final Context context, final String fileName,
        final long fromTime, final long toTime,final Callback callback ){

        this.callback = callback;

        new DBRecorder().getTimeRange(fromTime, toTime, new DBRecorder.CallbackSortedArrayListener() {
            @Override
            public void DBRecorderCallingBackSortedArray(ArrayList<Recorder> arrayList) {
                saveFile(arrayList,context,fileName);

            }
        });

    }

    public void saveExcelFile(final Context context, final String fileName, final Callback callback) {

        this.callback = callback;

        new DBRecorder().selectAll(new DBRecorder.CallbackSelectAllListener() {
            @Override
            public void DBRecorderCallingBackSelectAll(ArrayList<Recorder> arrayList) {
                saveFile(arrayList,context,fileName);
            }
        });

    }

    public void saveExcelFile(final Context context, final String fileName, ArrayList<Recorder> arrayList, final Callback callback) {

        this.callback = callback;

        saveFile(arrayList,context,fileName);
    }

    private void saveFile(ArrayList<Recorder> arrayList, Context context, String fileName){
        Workbook wb = new HSSFWorkbook();

        Cell c;
        Sheet sheet = wb.createSheet("Отчет");

        // Generate column headings
        Row row = sheet.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Дата");

        c = row.createCell(1);
        c.setCellValue("Время");

        c = row.createCell(2);
        c.setCellValue("Сила выдоха");

        c = row.createCell(3);
        c.setCellValue("Лекарство");

        c = row.createCell(4);
        c.setCellValue("Заметка");

        String[] dateStrings = new String[arrayList.size()];
        String[] timeStrings = new String[arrayList.size()];
        String[] isMedicineStrings = new String[arrayList.size()];
        String[] noteStrings = new String[arrayList.size()];

        int[] values = new int[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date date = new Date();
            date.setTime(arrayList.get(i).getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String timeString = timeformat.format(calendar.getTime());
            String dateString = dateFormat.format(calendar.getTime());
            int isMedicine = arrayList.get(i).getIsMedicine();

            if (isMedicine == 0)
                isMedicineStrings[i] = "Нет";
            else
                isMedicineStrings[i] = "Да";

            noteStrings[i] = arrayList.get(i).getNote();
            dateStrings[i] = dateString;
            timeStrings[i] = timeString;
            values[i] = arrayList.get(i).getValue();
        }

        for (int i = 1; i < arrayList.size() + 1; i++) {
            Row row1 = sheet.createRow(i);

            c = row1.createCell(0);
            c.setCellValue(dateStrings[i - 1]);

            c = row1.createCell(1);
            c.setCellValue(timeStrings[i - 1]);

            c = row1.createCell(2);
            c.setCellValue(values[i - 1]);

            c = row1.createCell(3);
            c.setCellValue(isMedicineStrings[i - 1]);

            c = row1.createCell(4);
            c.setCellValue(noteStrings[i - 1]);
        }

        sheet.setColumnWidth(0, (15 * 200));
        sheet.setColumnWidth(1, (15 * 100));
        sheet.setColumnWidth(2, (15 * 200));
        sheet.setColumnWidth(3, (15 * 200));
        sheet.setColumnWidth(4, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                fileName);

        try (FileOutputStream os = new FileOutputStream(file)) {
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        }

        callback.excelWriterCallingBack(success, file.getAbsolutePath());
    }

}