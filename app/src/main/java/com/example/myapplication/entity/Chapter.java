package com.example.myapplication.entity;

import java.util.Arrays;

public class Chapter {
    //章节标题
    private  String title;
    //章节内容
    private  String[] content;
    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
