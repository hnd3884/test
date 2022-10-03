package com.adventnet.tools.update;

import java.util.ArrayList;

public class ZipFileGroup
{
    private String zipName;
    private ArrayList fileList;
    
    public ZipFileGroup() {
        this.zipName = null;
        this.fileList = null;
        this.fileList = new ArrayList();
    }
    
    public void setZipName(final String name) {
        this.zipName = name;
    }
    
    public String getZipName() {
        return this.zipName;
    }
    
    public ArrayList getFilesList() {
        return this.fileList;
    }
}
