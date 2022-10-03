package com.zoho.clustering.filerepl.tool;

import java.io.IOException;
import java.io.Closeable;
import java.io.FileOutputStream;
import com.zoho.clustering.util.FileUtil;
import java.io.File;

public class FileList
{
    public static void main(final String[] args) throws IOException {
        final File dirObj = new File(args[0]);
        FileUtil.assertDir(dirObj);
        final FileOutputStream fout = new FileOutputStream(new File(args[1]));
        try {
            getFilesList("", dirObj, fout);
        }
        finally {
            FileUtil.Close(fout);
        }
    }
    
    private static void getFilesList(final String prefix, final File dirObj, final FileOutputStream fout) throws IOException {
        final File[] listFiles;
        final File[] files = listFiles = dirObj.listFiles();
        for (final File file : listFiles) {
            if (file.isDirectory()) {
                getFilesList(prefix + "/" + file.getName(), file, fout);
            }
            else {
                fout.write((prefix + "/" + file.getName() + "\n").getBytes());
            }
        }
    }
}
