package com.me.devicemanagement.framework.server.util;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

public class I18nPropertyFilesMerge
{
    public static void main(final String[] args) throws Exception {
        final String sourceFolder = args[0];
        final String otherFolder = args[1];
        mergeAllFiles(new File(sourceFolder), new File(otherFolder));
    }
    
    private static File isFileExists(final File sourceFile, final File otherFolder) {
        for (final File otherFile : otherFolder.listFiles()) {
            if (otherFile.isFile() && otherFile.getName().equals(sourceFile.getName())) {
                return otherFile;
            }
        }
        return null;
    }
    
    private static Properties loadProperties(final File propertyFile, final Properties p) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertyFile);
            p.load(fis);
            return p;
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    private static void storeProperties(final File propertyFile, final Properties p) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(propertyFile);
            p.store(fos, null);
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
    
    private static void mergeAllFiles(final File sourceFolder, final File otherFolder) throws Exception {
        for (final File sourceFile : sourceFolder.listFiles()) {
            if (sourceFile.isFile()) {
                final File otherFile = isFileExists(sourceFile, otherFolder);
                if (otherFile != null) {
                    Properties p = new Properties();
                    p = loadProperties(sourceFile, p);
                    p = loadProperties(otherFile, p);
                    storeProperties(sourceFile, p);
                }
            }
        }
    }
}
