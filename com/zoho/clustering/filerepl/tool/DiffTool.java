package com.zoho.clustering.filerepl.tool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import com.zoho.clustering.util.FileUtil;
import java.util.TreeSet;
import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

public class DiffTool
{
    private static Logger logger;
    private Set<String> firstOnly;
    private Set<String> secondOnly;
    private File dir1;
    private File dir2;
    
    public static void main(final String[] args) {
        final DiffTool diff = new DiffTool(new File(args[0]), new File(args[1]));
        diff.findDiff();
    }
    
    public DiffTool(final File dir1, final File dir2) {
        this.firstOnly = new TreeSet<String>();
        this.secondOnly = new TreeSet<String>();
        FileUtil.assertDir(dir1);
        FileUtil.assertDir(dir2);
        this.dir1 = dir1;
        this.dir2 = dir2;
    }
    
    public void showResult() {
        if (this.firstOnly.isEmpty() && this.secondOnly.isEmpty()) {
            DiffTool.logger.log(Level.INFO, "No Diff. Files are equal");
            return;
        }
        if (!this.firstOnly.isEmpty()) {
            DiffTool.logger.log(Level.INFO, "\nFiles only in " + this.dir1.getPath() + ":");
            for (final String file : this.firstOnly) {
                DiffTool.logger.log(Level.INFO, file);
            }
        }
        if (!this.secondOnly.isEmpty()) {
            DiffTool.logger.log(Level.INFO, "\nFiles only in " + this.dir2.getPath() + ":");
            for (final String file : this.secondOnly) {
                DiffTool.logger.log(Level.INFO, file);
            }
        }
    }
    
    private void findDiff() {
        final Set<String> list1 = new HashSet<String>();
        final Set<String> list2 = new HashSet<String>();
        getFilesList("", this.dir1, list1);
        getFilesList("", this.dir2, list2);
        final Iterator<String> it = list1.iterator();
        while (it.hasNext()) {
            final String file1 = it.next();
            if (list2.remove(file1)) {
                it.remove();
            }
        }
        for (final String file2 : list1) {
            this.firstOnly.add(file2);
        }
        for (final String file2 : list2) {
            this.secondOnly.add(file2);
        }
        this.showResult();
    }
    
    private static void getFilesList(final String prefix, final File dir, final Set<String> result) {
        final File[] listFiles;
        final File[] files = listFiles = dir.listFiles();
        for (final File file : listFiles) {
            if (file.isDirectory()) {
                getFilesList(prefix + "/" + file.getName(), file, result);
            }
            else {
                result.add(prefix + "/" + file.getName());
            }
        }
    }
    
    static {
        DiffTool.logger = Logger.getLogger(DiffTool.class.getName());
    }
}
