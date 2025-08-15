package com.example.myapplication.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.FileOutputStream;

public class BookUtil {

   public static void openTxt(String[] fileList){

    }
    public  static void  saveFile(Context context, String filename, String content){
        FileOutputStream outputStream=context.openFileOutput(filename);
    }

    public  static String  getFilePath(Uri uri){
        String doc = DocumentsContract.getDocumentId(uri);
        String path = doc;
        Log.d("getFilePath", "doc: " + doc);

        final String[] split = doc.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
            path = Environment.getExternalStorageDirectory() + "/" + split[1];

        }
        return  path;
    }

}
