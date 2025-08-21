package com.example.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;

import android.util.Log;
import android.view.KeyEvent;

import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.databinding.ActivityMainBinding;

import com.example.myapplication.entity.TxtInfo;
import com.example.myapplication.utils.BookUtil;
import com.example.myapplication.utils.DbHelper;

import com.example.myapplication.utils.WebAppInterface;

import org.json.JSONException;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final Integer TXT_CODE = 1111;

    private ActivityMainBinding binding;
    private WebView myWebView;
    private DbHelper dbHelper;

    public void openFile() {//判断权限是否开启
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("chooseFile", "permission");

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, TXT_CODE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }

    }
    public  void readTxt(String id) throws FileNotFoundException, JSONException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            BookUtil.readTxt(this, dbHelper, id);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }
    }

    public void getList() throws JSONException {
        BookUtil.getList(dbHelper, this);
    }

    public void delTxt(String[] ids, String type) throws JSONException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            BookUtil.delTxt(dbHelper, this, ids, type);
            BookUtil.getList(dbHelper, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (TXT_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {//多选文件
                int count = data.getClipData().getItemCount();
                Log.d("chooseFile", "count=" + count);
                int currentItem = 0;
                List<TxtInfo> list = new ArrayList<TxtInfo>();
                while (currentItem < count) {
                    Uri uri = data.getClipData().getItemAt(currentItem).getUri();
                    TxtInfo txt = getTxtInfo(uri);

                    list.add(txt);

                    currentItem++;
                }

                BookUtil.openTxt(dbHelper, list);
                try {
                    BookUtil.getList(dbHelper, this);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else if (data.getData() != null) {//单选文件
                Log.d("chooseFile", "count=" + 1);
                List<TxtInfo> list = new ArrayList<TxtInfo>();
                Uri uri = data.getData();
                TxtInfo txt = getTxtInfo(uri);

                list.add(txt);


                BookUtil.openTxt(dbHelper, list);
                try {
                    BookUtil.getList(dbHelper, this);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected TxtInfo getTxtInfo(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();

        String path = uri.getPath();

        int size = cursor.getInt(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        name = name.substring(0, name.lastIndexOf("."));
        TxtInfo txt = new TxtInfo(name, size, path);
        Log.d("chooseFile", "path=" + path + ",name=" + name + ",size=" + size);

        return txt;

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String filePath = "file:///android_asset/index.html";


        myWebView = findViewById(R.id.the_webview);


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);// 设置支持javascript
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webSettings.setUseWideViewPort(true);//将图片调整到适合webView大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        webSettings.setDomStorageEnabled(true);//设置DOM缓存，当H5网页使用localstorage时一定要设置
        webSettings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);// 设置去缓存，防止加载的是上一次数据
        webSettings.setDatabaseEnabled(true);

        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置允许JS弹窗
        // 解决加载本地内存中报错 err_access_denied
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //页面加载完毕，获取书籍记录列表
                try {
                    getList();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        myWebView.loadUrl(filePath);


        dbHelper = new DbHelper(this);
        checkPermission();

    }

    public void doJs(String eventName, String result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myWebView.evaluateJavascript("AndroidResult('" + eventName + "'," + result + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check whether the key event is the Back button and if there's history.
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.doJs("backList", "");
            return true;
        }
        // If it isn't the Back button or there's no web page history, bubble up to
        // the default system behavior. Probably exit the activity.
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }
}