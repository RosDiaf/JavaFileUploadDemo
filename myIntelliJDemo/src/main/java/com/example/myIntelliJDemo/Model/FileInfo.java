package com.example.myIntelliJDemo.Model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileInfo {
    private List<MultipartFile> name;
    private String url;

    public FileInfo() {
    }

    public FileInfo(List<MultipartFile> name, String url) {
        this.name = name;
        this.url = url;
    }

    public List<MultipartFile> getName() {
        return this.name;
    }

    public void setName(List<MultipartFile> name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
