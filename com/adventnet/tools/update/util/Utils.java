package com.adventnet.tools.update.util;

import java.util.Collection;
import java.util.ArrayList;
import java.io.File;

public class Utils
{
    public static final String zip = "zip";
    public static final String ppm = "ppm";
    
    public static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf(46);
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
    
    public static String getUnixFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.replace('\\', '/');
        }
        return fileName;
    }
    
    public static ArrayList recurseAndGetFiles(final File directory) {
        final ArrayList listToReturn = new ArrayList();
        if (directory.isDirectory()) {
            final File[] fileList = directory.listFiles();
            for (int i = 0; i < fileList.length; ++i) {
                final File tempFile = fileList[i];
                if (tempFile.isDirectory()) {
                    listToReturn.addAll(recurseAndGetFiles(tempFile));
                }
                else {
                    listToReturn.add(getUnixFileName(tempFile.getPath()));
                }
            }
        }
        else {
            listToReturn.add(getUnixFileName(directory.getPath()));
        }
        return listToReturn;
    }
    
    public static String getRelativeFileName(String home, String absFileName) {
        home = getUnixFileName(home);
        if (!home.endsWith("/")) {
            home += "/";
        }
        absFileName = getUnixFileName(absFileName);
        if (absFileName.startsWith(home)) {
            return absFileName.substring(home.length());
        }
        return absFileName;
    }
}
