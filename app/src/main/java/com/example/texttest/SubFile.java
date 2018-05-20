package com.example.texttest;

import java.io.File;

public class SubFile {
    private File file = null;
    public SubFile(File file){
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public String toString() {
        String str = null;
        if (file.isDirectory()){
            str = "➔\t\t" + file.getName();
        } else {
            if (file.getName().lastIndexOf(".txt") > -1){
                str = "\t\t\uD83D\uDCD6\t\t" + file.getName();
            }
        }
        return str;
    }
}
