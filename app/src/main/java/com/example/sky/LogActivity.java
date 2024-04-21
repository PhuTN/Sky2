package com.example.sky;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LogActivity extends AppCompatActivity {
    ImageView deleteBtn,backBtn;
    Dialog dialog;


    SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setContentView(R.layout.activity_log);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        backBtn = findViewById(R.id.backBtn);



        deleteBtn = findViewById(R.id.deleteBtn);
        searchView = findViewById(R.id.searchView);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LogDatabaseHelper dbHelper = new LogDatabaseHelper(this);
        List<LogItem> logList = dbHelper.getAllLogs(); // Lấy danh sách các mục nhật ký từ cơ sở dữ liệu

        LogAdapter logAdapter = new LogAdapter(this, logList,getIntent());
        recyclerView.setAdapter(logAdapter);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                logList.clear();
                LogAdapter logAdapter = new LogAdapter(context, logList,getIntent());
                recyclerView.setAdapter(logAdapter);
                dbHelper.setAllLogs(logList);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();


            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn nút tìm kiếm (ví dụ: gọi hàm tìm kiếm dữ liệu)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<LogItem> logList = dbHelper.getAllLogs(); // Lấy danh sách các mục nhật ký từ cơ sở dữ liệu

                LogAdapter logAdapter = new LogAdapter(context, logList,getIntent());
                logAdapter.setFilter(newText);
                recyclerView.setAdapter(logAdapter);
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }



}
