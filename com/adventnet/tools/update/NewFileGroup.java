package com.adventnet.tools.update;

import java.util.ArrayList;

public class NewFileGroup
{
    private String versionName;
    private ArrayList fileList;
    
    public NewFileGroup() {
        this.versionName = null;
        this.fileList = null;
        this.fileList = new ArrayList();
    }
    
    public void setVersionName(final String name) {
        this.versionName = name;
    }
    
    public String getVersionName() {
        return this.versionName;
    }
    
    public ArrayList getFilesList() {
        return this.fileList;
    }
}
