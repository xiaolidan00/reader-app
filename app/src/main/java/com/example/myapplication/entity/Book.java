package com.example.myapplication.entity;


import org.json.JSONException;
import org.json.JSONObject;

public class Book   {
    //id

    private String id= "";
 //file_path
    //文件路径
    private  String path= "";
    //书名 文件名
//file_name
    private  String name= "";


    //阅读当前章节
//chapter_current
   private Integer chapter=0;


//chapter_total
    private Integer total=0;

   //阅读章节当前页
//chapter_index
    private Integer index=0;

    //字数
//book_num
    private Integer num=0;
    //文件大小
//file_size
    private Integer size=0;
    //更新时间戳
//update_time
    private Long updateTime= 0L;
    //章节匹配正则类型
//regex_type
    private Integer regexType=-1;

    //章节匹配正则表达式
   //chapter_regex
    private String regex="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChapter() {
        return chapter;
    }

    public void setChapter(Integer chapter) {
        this.chapter = chapter;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRegexType() {
        return regexType;
    }

    public void setRegexType(Integer regexType) {
        this.regexType = regexType;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
//    public String toJSON(){
//
//        return "{" +
//                "\"id\":\""+this.id+"\","+
//                "\"path\":\""+this.path+"\","+
//                "\"name\":\""+this.name+"\","+
//                "\"chapter\":"+this.chapter+","+
//                "\"index\":"+this.index+","+
//                "\"total\":"+this.total+","+
//                "\"size\":"+this.size+","+
//                "\"num\":"+this.num+","+
//                "\"regexType\":"+this.regexType+","+
//                "\"regex\":\""+this.regex+"\","+
//                "\"updateTime\":"+this.updateTime+
//                "}" ;
//    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",id);
        jsonObject.put("path",path);
        jsonObject.put("name",name);
        jsonObject.put("chapter", chapter);
        jsonObject.put("index", index);
        jsonObject.put("total", total);
        jsonObject.put("size", size);
        jsonObject.put("num", num);
        jsonObject.put("regexType",regexType);
        jsonObject.put("regex",regex);
        jsonObject.put("updateTime", updateTime);
        return  jsonObject;
    }

}
