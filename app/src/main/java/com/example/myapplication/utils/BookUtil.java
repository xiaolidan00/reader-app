package com.example.myapplication.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.entity.Book;
import com.example.myapplication.entity.BookEntry;
import com.example.myapplication.entity.TxtInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BookUtil {

    public static void openTxt(DbHelper dbHelper, List<TxtInfo> fileList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (TxtInfo file : fileList) {

            String id = file.getFilePath().replaceAll("[\\.|\\/:]", "_");
            String name = file.getFileName();


            ContentValues values = new ContentValues();
            values.put(BookEntry.ID, id);
            values.put(BookEntry.BOOK_NAME, name);
            values.put(BookEntry.FILE_PATH, file.getFilePath());
            values.put(BookEntry.FILE_SIZE, file.getFileSize());
            values.put(BookEntry.BOOK_NUM, 0);
            values.put(BookEntry.CHAPTER_CURRENT, 0);
            values.put(BookEntry.CHAPTER_INDEX, 0);
            values.put(BookEntry.CHAPTER_TOTAL, 0);
            values.put(BookEntry.REGEX_TYPE, -1);
            values.put(BookEntry.CHAPTER_REGEX, "");


            values.put(BookEntry.UPDATE_TIME, System.currentTimeMillis());
            try {
                db.insert(BookEntry.TABLE, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    public static final String[] chapterRegex = {
            "\\s*第\\s*[0-9]+\\s*章",
            "\\s*第\\s*[一二三四五六七八九十零百千万]+\\s*章",
            "\\s*第\\s*[0-9]+\\s*节",
            "\\s*第\\s*[一二三四五六七八九十零百千万]+\\s*节",
            "\\s*第\\s*[0-9]+\\s*回",
            "\\s*第\\s*[一二三四五六七八九十零百千万]+\\s*回",
            "\\s*[0-9]+\\.",
            "\\s*[一二三四五六七八九十零百千万]+\\.",
            "\\s*[0-9]+、",
            "\\s*[一二三四五六七八九十零百千万]+、",
            "\\s*[0-9]+",
            "\\s*[一二三四五六七八九十零百千万]+",
            "\\s*[\\(|（][0-9]+[\\)|）]",
            "\\s*[\\(|（][一二三四五六七八九十零百千万]+[\\)|）]",
            "^[^\"“”。,，\\！？—\\-\\+=\\?\\*…_、\\:：]+$"
    };

    //获取txt内容
    public static Book getTxt(DbHelper dbHelper,  String id)  {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry.ID,
                BookEntry.BOOK_NAME,
                BookEntry.FILE_PATH,
                BookEntry.CHAPTER_CURRENT,
                BookEntry.CHAPTER_INDEX,
                BookEntry.CHAPTER_TOTAL,
                BookEntry.BOOK_NUM,
                BookEntry.FILE_SIZE,
                BookEntry.UPDATE_TIME,
                BookEntry.REGEX_TYPE,
                BookEntry.CHAPTER_REGEX
        };

        String selection = BookEntry.ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(
                BookEntry.TABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        cursor.moveToFirst();

        Book book = new Book();
        book.setId(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.ID)));
        book.setName(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.BOOK_NAME)));
        book.setPath(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.FILE_PATH)));
        book.setNum(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.BOOK_NUM)));
        book.setChapter(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_CURRENT)));
        book.setIndex(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_INDEX)));
        book.setTotal(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_TOTAL)));
        book.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(BookEntry.UPDATE_TIME)));
        book.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.FILE_SIZE)));
        book.setRegexType(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.REGEX_TYPE)));
        book.setRegex(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_REGEX)));
return  book;

    }
    public static  void delTxt(DbHelper dbHelper,MainActivity context,String[] ids,String type)   {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(String id:ids){
            if(type=="file"){
        Book book=getTxt(dbHelper,id);
        File file=new File(book.getPath());
        if(file.exists()&&file.isFile()){
            if(file.delete()){
                Log.d("delTxt",book.getPath()+"删除成功");
            }else{
                Log.d("delTxt",book.getPath()+"删除失败");
            }
        }
            }
            try{
                db.delete(BookEntry.TABLE,BookEntry.ID + " = ?",new String[]{id});
                Log.d("delTxt",id+"删除记录成功");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void getList(DbHelper dbHelper, MainActivity context) throws JSONException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry.ID,
                BookEntry.BOOK_NAME,
                BookEntry.FILE_PATH,
                BookEntry.CHAPTER_CURRENT,
                BookEntry.CHAPTER_INDEX,
                BookEntry.CHAPTER_TOTAL,
                BookEntry.BOOK_NUM,
                BookEntry.FILE_SIZE,
                BookEntry.UPDATE_TIME,
                BookEntry.REGEX_TYPE,
                BookEntry.CHAPTER_REGEX
        };
        String sortOrder =
                BookEntry.UPDATE_TIME + " DESC";
        Cursor cursor = db.query(
                BookEntry.TABLE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        JSONArray list = new JSONArray();
        int idx = 0;
        while (cursor.moveToNext()) {

            Book book = new Book();
            book.setId(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.ID)));
            book.setName(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.BOOK_NAME)));
            book.setPath(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.FILE_PATH)));
            book.setNum(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.BOOK_NUM)));
            book.setChapter(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_CURRENT)));
            book.setIndex(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_INDEX)));
            book.setTotal(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_TOTAL)));
            book.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(BookEntry.UPDATE_TIME)));
            book.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.FILE_SIZE)));
            book.setRegexType(cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.REGEX_TYPE)));
            book.setRegex(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.CHAPTER_REGEX)));
            list.put(idx, book.toJson());
            idx++;
        }
        Log.d("getList", list.length() + "");

        cursor.close();
        context.doJs("listTxt", list.toString());

    }


}
