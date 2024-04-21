package com.example.sky;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private List<LogItem> logList;
    private List<LogItem> newLogList;
    private Context context;
    String filter;
    private Intent myIntent;

    ArrayList<Integer> a;

    public LogAdapter(Context context, List<LogItem> logList,Intent myIntent) {
        this.context = context;
        this.logList = logList;
        filter = "";
        a = new ArrayList<>();
        for(int i = 0;i< logList.size();i++){
            a.add(i);
        }
        newLogList = new ArrayList<>(logList);
        this.myIntent = myIntent;
    }

    public void setFilter(String filter){
        this.filter = filter;
        newLogList.clear();
        a.clear();
        for (int i = 0;i<logList.size();i++) {
            LogItem logItem = logList.get(i);
            if(logItem.getUrl().contains(filter) || logItem.getEvent().contains(filter)) {
                newLogList.add(logItem);
                a.add(i);
            }
        }
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, @SuppressLint("RecyclerView") int position) {


        LogItem logItem = newLogList.get(position);

            holder.textUrl.setText(logItem.getUrl());


            holder.textEvent.setText(logItem.getEvent());
            TextView textUrl, textEvent;
            ImageView imageViewIcon, imageViewCancel;
            textUrl = holder.itemView.findViewById(R.id.text_url);
            textEvent = holder.itemView.findViewById(R.id.text_event);
            imageViewIcon = holder.itemView.findViewById(R.id.imageViewIcon);
        imageViewCancel = holder.itemView.findViewById(R.id.imageViewCancel);



        if(logItem.getUrl().equals("")){
            textEvent.setEnabled(false);
            textUrl.setVisibility(View.GONE);
imageViewIcon.setVisibility(View.GONE);

            imageViewCancel.setVisibility(View.GONE);
        }
        else{
            textEvent.setEnabled(true);
            textUrl.setVisibility(View.VISIBLE);
            imageViewIcon.setVisibility(View.VISIBLE);
            imageViewCancel.setVisibility(View.VISIBLE);
        }



            new DownloadImageTask(holder.imageViewIcon).execute(logItem.getUrl());
            textUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("item_id", holder.textUrl.getText());
                    ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                    ((AppCompatActivity)context).finish();
                }
            });
            textEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("item_id", holder.textUrl.getText());
                    ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                    ((AppCompatActivity)context).finish();
                }
            });
            imageViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("item_id", holder.textUrl.getText());
                    ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                    ((AppCompatActivity)context).finish();
                }
            });
            imageViewCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    newLogList.remove(position);
                    int tempp = a.get(position);
                    logList.remove(tempp);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    LogDatabaseHelper dbHelper = new LogDatabaseHelper(context);
                    dbHelper.setAllLogs(logList);


                }
            });


    }

    @Override
    public int getItemCount() {
        return newLogList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textUrl, textEvent;
        ImageView imageViewIcon,imageViewCancel;




        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            textUrl = itemView.findViewById(R.id.text_url);
            textEvent = itemView.findViewById(R.id.text_event);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            imageViewCancel = itemView.findViewById(R.id.imageViewCancel);

        }


    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new URL("https://www.google.com/s2/favicons?domain=" + url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);

            }
        }
    }


}

