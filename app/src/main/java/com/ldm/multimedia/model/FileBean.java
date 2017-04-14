package com.ldm.multimedia.model;

import java.io.File;
import java.io.Serializable;

public class FileBean implements Serializable {
    //文件
    private File file;
    //文件时长
    private int fileLength;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }
}