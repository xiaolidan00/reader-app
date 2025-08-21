package com.example.myapplication.entity;

public class TxtInfo {
    private String fileName="";
    private Integer fileSize=0;

    private    String filePath="";



    public TxtInfo(String fileName, Integer fileSize, String filePath) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }
}
