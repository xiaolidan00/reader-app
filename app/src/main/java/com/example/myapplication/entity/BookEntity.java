package com.example.myapplication.entity;


public class BookEntity {
    //文件路径
    private  String path;

    //id
    private String id;
    //书名 文件名
    private  String name;


    //阅读当前章节
   private Integer chapter;

   //阅读章节当前页
    private Integer index;

    //字数
    private Integer num;
    //文件大小
    private Integer size;
    //更新时间戳
    private Long updateTime;
    //章节匹配正则类型
    private String regexType;

    //章节匹配正则表达式
    private String regex;

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

    public String getRegexType() {
        return regexType;
    }

    public void setRegexType(String regexType) {
        this.regexType = regexType;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "path='" + path + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", chapter=" + chapter +
                ", index=" + index +
                ", num=" + num +
                ", size=" + size +
                ", updateTime=" + updateTime +
                ", regexType='" + regexType + '\'' +
                ", regex='" + regex + '\'' +
                '}';
    }
}
