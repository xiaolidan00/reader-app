package com.example.myapplication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.KeyEvent;

import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utils.BookUtil;
import com.example.myapplication.utils.WebAppInterface;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private  WebView myWebView;
    public void openFile(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(data.getClipData() != null) {//多选文件
                int count = data.getClipData().getItemCount();
                int currentItem = 0;
                List<String> list = new ArrayList<String>();
                while(currentItem < count) {
                    Uri uri = data.getClipData().getItemAt(currentItem).getUri();
                    String path = BookUtil.getFilePath(uri);
                    list.add(path);
                    currentItem++;
                }
                String[] arr = list.toArray(new String[list.size()]);
                BookUtil.openTxt(arr);
            }else if(data.getData()!=null){//单选文件

                Uri uri = data.getData();

                String path =BookUtil.getFilePath(uri);

                BookUtil.openTxt( new String[]{path});
            }
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        String filePath = "file:///android_asset/index.html";


        myWebView =  findViewById(R.id.the_webview);



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

        myWebView.loadUrl(filePath);

//        myWebView.loadDataWithBaseURL( "file:///android_asset/","index.html","text/html","UTF-8",null);

     }
     public  void doJs(String eventName,String result){
         myWebView.evaluateJavascript("AndroidResult('"+eventName+"','"+result+"')", new ValueCallback<String>() {
             @Override
             public void onReceiveValue(String s) {

             }
         });
     }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check whether the key event is the Back button and if there's history.
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it isn't the Back button or there's no web page history, bubble up to
        // the default system behavior. Probably exit the activity.
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onSupportNavigateUp() {
      return  false;
    }
}