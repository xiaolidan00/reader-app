package com.example.myapplication.utils;

import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;

public class WebAppInterface {
    MainActivity mContext;

    /** Instantiate the interface and set the context. */
    public WebAppInterface(MainActivity c) {
        mContext = c;

    }

    /** Show a toast from the web page. */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void openTxt() {//打开txt文件
        Toast.makeText(mContext, "openTxt", Toast.LENGTH_SHORT).show();
        mContext.openFile();
    }

    @JavascriptInterface
    public void listTxt() {//获取最新的txt列表

    }
}