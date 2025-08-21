package com.example.myapplication.entity;

import android.provider.BaseColumns;

public final class BookEntry implements BaseColumns {

    public  static  final  String TABLE="book";

    public static final String ID = "id";
    public static final String FILE_PATH = "file_path";
    public static final String BOOK_NAME = "book_name";

    public static final String CHAPTER_CURRENT = "chapter_current";

    public static final String CHAPTER_TOTAL = "chapter_total";

    public static final String CHAPTER_INDEX = "chapter_index";

    public static final String BOOK_NUM = "book_num";

    public static final String FILE_SIZE = "file_size";

    public static final String UPDATE_TIME = "update_time";

    public static final String REGEX_TYPE = "regex_type";

    public static final String CHAPTER_REGEX = "chapter_regex";
}
