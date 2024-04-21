package com.example.sky;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    String oldUrl ;
    private float offsetX;
    private float offsetY;
    private static final int CLICK_ACTION_THRESHOLD = 200;
    private long pressStartTime;

    EditText urlInput;

    Dialog dialog;
    WebView webView;

    RelativeLayout mainLayout;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                if (data.hasExtra("item_id")) {
                    loadMyUrl(String.valueOf(data.getStringExtra("item_id")));
                    getIntent().removeExtra("item_id");
                }
                else
                    loadMyUrl("");

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldUrl = "";
        Context context = this;
        LogDatabaseHelper dbHelper = new LogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        setContentView(R.layout.activity_main);

        FloatingActionButton movableButton = findViewById(R.id.movableButton);


        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.main_dialog_content);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        ImageButton history = dialog.findViewById(R.id.historyBtn);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(context, LogActivity.class);
                intent.putExtra("oldUrl", webView.getUrl());
                startActivityForResult(intent,1);

            }
        });

        ImageButton forwardBtn = dialog.findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(webView.canGoForward()) {
                    webView.goForward();
                }

            }
        });

        ImageButton homeBtn = dialog.findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                loadMyUrl("google.com");

            }
        });


        webView = findViewById(R.id.webView);
        urlInput = findViewById(R.id.urlInput);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true  );
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        webView.setWebViewClient(new MyWebViewClient(db));
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }
        });
        if (getIntent().hasExtra("item_id")) {
            loadMyUrl(String.valueOf(getIntent().getStringExtra("item_id")));
            getIntent().removeExtra("item_id");
        }
        else
        loadMyUrl("google.com");

        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(urlInput.getWindowToken(),0);
                    loadMyUrl(urlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });
        movableButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        offsetX = motionEvent.getRawX() - view.getX();
                        offsetY = motionEvent.getRawY() - view.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(motionEvent.getRawX() - offsetX)
                                .y(motionEvent.getRawY() - offsetY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(System.currentTimeMillis()-pressStartTime <CLICK_ACTION_THRESHOLD){
                            dialog.show();

                        }

                }
                return true;
            }
        });

        mainLayout = findViewById(R.id.mainLayout);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // Cuộn xuống, ẩn thanh URL

                    if(linearLayout.getVisibility() != View.INVISIBLE) {
                        TranslateAnimation slideDown = new TranslateAnimation(0, 0, 0, linearLayout.getHeight());
                        slideDown.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                        linearLayout.startAnimation(slideDown);
                        linearLayout.setVisibility(View.INVISIBLE);
                    }

                } else if (scrollY < oldScrollY) {
                    if(linearLayout.getVisibility() != View.VISIBLE) {
                        TranslateAnimation slideUp = new TranslateAnimation(0, 0, linearLayout.getHeight(), 0);
                        slideUp.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                        linearLayout.startAnimation(slideUp);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


    }

    private void loadMyUrl(String url) {

        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if(matchUrl){
            if(url.contains("https")){
                webView.loadUrl(url);
            }
            else
            webView.loadUrl("https://www."+url);
        }else{

            webView.loadUrl("https://www.google.com/search?q="+url);
        }
    }






    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
    /*
    public void changeLog(){
        Intent intent = new Intent(this,LogActivity.class);
        startActivity(intent);
    }

     */
    class MyWebViewClient extends WebViewClient {
        private SQLiteDatabase database;

        public MyWebViewClient(SQLiteDatabase db) {
            this.database = db;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                        return true; // Đã xử lý URL, không cần WebView xử lý nữa
                    }
                } catch (URISyntaxException | ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false; // Để WebView xử lý URL bình thường
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlInput.setText(webView.getUrl());


        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            urlInput.setText(webView.getUrl());

            if(!Objects.equals(oldUrl, url)) {
                ContentValues values = new ContentValues();
                values.put("url", url);

                values.put("event", webView.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                values.put("date",sdf.format(new Date()));




                database.insert("browser_logs", null, values);
                oldUrl = url;
            }
        }


    }



}