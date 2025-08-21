package com.example.myapplication.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Chapter {
    //章节标题
    private  String title="";
    //章节内容
    private  String[] content=new String[]{};

    public Chapter(String title, String[] content) {
        this.title = title;
        this.content = content;
    }

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

    public JSONObject toJson() throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("title",title);
        JSONArray arr=new JSONArray();
        int i=0;
        for(String c:content){
            arr.put(i,c);
            i++;
        }

        obj.put("content",arr);
        return  obj;

    }
}
