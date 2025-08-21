package com.example.myapplication.utils;

import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;

import org.json.JSONException;

import java.io.FileNotFoundException;

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
        mContext.openFile();
    }
    @JavascriptInterface
    public void readTxt(String id) throws FileNotFoundException, JSONException {//打开txt文件

        mContext.readTxt(id);
    }
    @JavascriptInterface
    public void readedTxt()   {//打开txt文件

    }

    @JavascriptInterface
    public void getList() throws JSONException {//获取最新的txt列表
        mContext.getList();
    }
    @JavascriptInterface
    public void delTxt(String[] ids,String type) throws JSONException {//删除记录
        mContext.delTxt(ids,type);
    }

}