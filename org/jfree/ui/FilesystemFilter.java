package org.jfree.ui;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class FilesystemFilter extends FileFilter implements FilenameFilter
{
    private String[] fileext;
    private String descr;
    private boolean accDirs;
    
    public FilesystemFilter(final String fileext, final String descr) {
        this(fileext, descr, true);
    }
    
    public FilesystemFilter(final String fileext, final String descr, final boolean accDirs) {
        this(new String[] { fileext }, descr, accDirs);
    }
    
    public FilesystemFilter(final String[] fileext, final String descr, final boolean accDirs) {
        this.fileext = fileext.clone();
        this.descr = descr;
        this.accDirs = accDirs;
    }
    
    public boolean accept(final File dir) {
        if (dir.isDirectory() && this.acceptsDirectories()) {
            return true;
        }
        for (int i = 0; i < this.fileext.length; ++i) {
            if (dir.getName().endsWith(this.fileext[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean accept(final File dir, final String name) {
        final File f = new File(dir, name);
        if (f.isDirectory() && this.acceptsDirectories()) {
            return true;
        }
        for (int i = 0; i < this.fileext.length; ++i) {
            if (name.endsWith(this.fileext[i])) {
                return true;
            }
        }
        return false;
    }
    
    public void acceptDirectories(final boolean b) {
        this.accDirs = b;
    }
    
    public boolean acceptsDirectories() {
        return this.accDirs;
    }
    
    public String getDescription() {
        return this.descr;
    }
}
