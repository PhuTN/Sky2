package com.example.sky;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "_browser_logs.db";
    private static final int DATABASE_VERSION = 1;

    public LogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng để lưu trữ nhật ký
        db.execSQL("CREATE TABLE browser_logs (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, event TEXT, date TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu (nếu cần)
    }

    public List<LogItem> getAllLogs() {
        List<LogItem> logList = new ArrayList<>();

        // Xây dựng truy vấn SELECT để lấy dữ liệu từ bảng nhật ký
        String selectQuery = "SELECT * FROM browser_logs";

        // Mở kết nối đọc của cơ sở dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String oldDate = "";
        // Lặp qua các hàng của kết quả truy vấn và thêm chúng vào danh sách
        if (cursor.moveToLast()) { // Bắt đầu từ cuối danh sách
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                @SuppressLint("Range") String event = cursor.getString(cursor.getColumnIndex("event"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                if(oldDate.equals("") || !oldDate.equals(date)) {
                    oldDate = date;
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1); // Giảm một ngày từ ngày hiện tại để lấy ngày hôm qua


                    String yesterdayDate = sdf.format(calendar.getTime());
                    if(date.equals(sdf.format(new Date()))) {
                        LogItem logItem = new LogItem("", "Hôm nay - " +date,"" );
                        logList.add(logItem);
                    }
                    else if(date.equals(yesterdayDate)){
                        LogItem logItem = new LogItem("", "Hôm qua - " +date, "");
                        logList.add(logItem);
                    }
                    else{
                        LogItem logItem = new LogItem("",date,"");
                        logList.add(logItem);
                    }
                }

                // Tạo một đối tượng LogItem từ dữ liệu và thêm vào danh sách

                LogItem logItem = new LogItem(url, event,date);
                logList.add(logItem);
            } while (cursor.moveToPrevious()); // Đọc từ cuối về đầu
        }


        // Đóng cursor và đóng kết nối đến cơ sở dữ liệu
        cursor.close();
        db.close();

        return logList;
    }


    public void setAllLogs(List<LogItem> logList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa tất cả các mục hiện có trong bảng nhật ký
            db.delete("browser_logs", null, null);

            // Chèn các mục mới vào bảng nhật ký
            ContentValues values = new ContentValues();
            for (int i = logList.size() - 1; i >= 0; i--) {
                LogItem logItem = logList.get(i);
                if(!logItem.getUrl().equals("") && !logItem.getEvent().equals("")) {
                    values.put("url", logItem.getUrl());
                    values.put("event", logItem.getEvent());
                    values.put("date", logItem.getDate());
                    db.insert("browser_logs", null, values);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while inserting logs into database", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}