package com.example.texttest;

public class BookInfo {
    private int id;
    private String name;
    private String path;
    private float progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public BookInfo (int id, String name, String path, float progress){
        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.progress = progress;
    }
}
