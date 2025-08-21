package com.example.myapplication.utils;





import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.entity.BookEntry;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TxtReader.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +
                    BookEntry.TABLE+" (" +
                    BookEntry.ID+ " TXT PRIMARY KEY,"+
                    BookEntry.BOOK_NAME+" TEXT NOT NULL,"+
                   BookEntry.FILE_PATH+ " TEXT NOT NULL," +
                    BookEntry.CHAPTER_CURRENT+" INTEGER," +
                   BookEntry.CHAPTER_INDEX +" INTEGER," +
                   BookEntry.CHAPTER_TOTAL+ " INTEGER, " +
                  BookEntry.BOOK_NUM+  " INTEGER," +
                    BookEntry.UPDATE_TIME+ " INTEGER, " +
                  BookEntry.FILE_SIZE+  " INTEGER," +
                  BookEntry.REGEX_TYPE+ " INTEGER," +
                    BookEntry.CHAPTER_REGEX+" TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS book";
    public DbHelper(Context context) {
        super(context,  DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
