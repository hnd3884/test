package com.adventnet.tools.update;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import java.util.Vector;

public class FileGroup
{
    private String relativeFilePath;
    private Vector jarNames;
    private Vector fileNames;
    private Set<String> deletedFileNames;
    
    public FileGroup() {
        this.relativeFilePath = "";
        this.jarNames = null;
        this.fileNames = null;
        this.deletedFileNames = null;
        this.jarNames = new Vector();
        this.fileNames = new Vector();
        this.deletedFileNames = new TreeSet<String>();
    }
    
    public Vector getFileNameVector() {
        return this.fileNames;
    }
    
    public Vector getJarNameVector() {
        return this.jarNames;
    }
    
    public void setRelativeFilePath(final String st) {
        this.relativeFilePath = st;
    }
    
    public void addDeletedFileEntry(final String fileName) {
        this.deletedFileNames.add(fileName);
    }
    
    @Deprecated
    public List getDeletedFilesList() {
        return new ArrayList(this.deletedFileNames);
    }
    
    public Set<String> getDeletedFiles() {
        return this.deletedFileNames;
    }
}
