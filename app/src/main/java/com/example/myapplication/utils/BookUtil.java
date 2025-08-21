package com.example.myapplication.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.entity.Book;
import com.example.myapplication.entity.BookEntry;
import com.example.myapplication.entity.Chapter;
import com.example.myapplication.entity.TxtInfo;

import org.intellij.lang.annotations.RegExp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getRegex(String str) {
        for (String r : chapterRegex) {
            Pattern p = Pattern.compile(r);
            Matcher m = p.matcher(str);
            if (m.matches()) {
                return r;
            }
        }
        return chapterRegex[chapterRegex.length - 1];
    }

    public static final int LineNum = 24;
    public static final int PageNum = 21;

    public static void sliceContent(String it, List<String> content) {
        it = it.trim();

        if (TextUtils.isEmpty(it)) return;

        if (it.length() + 3 <= LineNum && !TextUtils.isEmpty(it)) {
            content.add("\\t" + it + "\\n");
        } else {
            int count = 3;
            String ss = "\\t";
            for (int i = 0; i < it.length(); i++) {
                String s = String.valueOf(it.charAt(i));
                count++;
                ss += s;
                if (count == LineNum || i == it.length() - 1) {
                    content.add(ss);
                    count = 0;
                    ss = "";
                }
            }
            int last = content.size() - 1;
            content.set(last, content.get(last) + "\\n");
        }
    }

    public static void readTxt(MainActivity context, DbHelper db, String id) throws FileNotFoundException, JSONException {
        Book book = getTxt(db, id);


        String txt = "";
        try {
            Log.d("readTxt",book.getPath());
            File file=new File(book.getPath());
            FileInputStream fis=new FileInputStream(file);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            char[] input=new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            txt = new String(input);
            Log.d("readTxt","txt size="+txt.length());

        } catch (Exception e) {
            e.printStackTrace();
            context.doJs("backList","文件不存在");
            return;
        }
        if (!TextUtils.isEmpty(txt)) {
            String first3000 = txt.substring(0, 3000);

            String[] lines = TextUtils.split(txt.replaceAll("\\r|\\t", ""), "\\n");
            String zhangjie = book.getRegexType() >= 0 ? getRegex(first3000) : (!TextUtils.isEmpty(book.getRegex()) ? book.getRegex() : chapterRegex[chapterRegex.length - 1]);
            List<String> content = new ArrayList<>();
            JSONArray list = new JSONArray();
            String newTitle = "";
            String title = book.getName();
            Pattern p = Pattern.compile(zhangjie);
            int idx = 0;
            for (String it : lines) {
                boolean tag = true;
                String r = it.trim();
                Matcher m = p.matcher(r);
                if (m.matches()) {

                    newTitle = r;
                    tag = false;
                }

                if (!TextUtils.isEmpty(newTitle) && !title.equals(newTitle)) {
                    Chapter c = new Chapter(title, content.toArray(new String[content.size()]));
                    list.put(idx, c.toJson());
                    idx++;
                    title = newTitle;
                    content.clear();
                } else if (tag) {
                    sliceContent(it, content);
                }

            }
            if (content.size() > 0) {
                Chapter c = new Chapter(title, content.toArray(new String[content.size()]));
                list.put(idx, c.toJson());
                idx++;
            }
            book.setTotal(list.length());
            book.setNum(txt.length());
            book.setRegex(zhangjie);
            saveTxt(db, book);

            context.doJs("readTxt", list.toString());
        } else {
            context.doJs("backList", "");
        }


    }

    public static void saveTxt(DbHelper dbHelper, Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(BookEntry.ID, book.getId());
        values.put(BookEntry.BOOK_NAME, book.getName());
        values.put(BookEntry.FILE_PATH, book.getPath());
        values.put(BookEntry.FILE_SIZE, book.getSize());
        values.put(BookEntry.BOOK_NUM, book.getNum());
        values.put(BookEntry.CHAPTER_CURRENT, book.getChapter());
        values.put(BookEntry.CHAPTER_INDEX, book.getIndex());
        values.put(BookEntry.CHAPTER_TOTAL, book.getTotal());
        values.put(BookEntry.REGEX_TYPE, book.getRegexType());
        values.put(BookEntry.CHAPTER_REGEX, book.getRegex());

        values.put(BookEntry.UPDATE_TIME, System.currentTimeMillis());
        try {
            String selection = BookEntry.ID + " = ?";
            String[] selectionArgs = {book.getId()};
            db.update(BookEntry.TABLE, values, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取txt内容
    public static Book getTxt(DbHelper dbHelper, String id) {
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
        cursor.close();
        db.close();
        return book;

    }

    public static void delTxt(DbHelper dbHelper, MainActivity context, String[] ids, String type) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (String id : ids) {
            if (type == "file") {
                Book book = getTxt(dbHelper, id);
                File file = new File(book.getPath());
                if (file.exists() && file.isFile()) {
                    if (file.delete()) {
                        Log.d("delTxt", book.getPath() + "删除成功");
                    } else {
                        Log.d("delTxt", book.getPath() + "删除失败");
                    }
                }
            }
            try {
                db.delete(BookEntry.TABLE, BookEntry.ID + " = ?", new String[]{id});
                Log.d("delTxt", id + "删除记录成功");
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
