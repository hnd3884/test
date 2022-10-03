package com.adventnet.management.log;

import java.io.File;

public class NmsFileUtil
{
    private File dir;
    
    public NmsFileUtil(final File dir) {
        this.dir = dir;
    }
    
    public NmsFileUtil(final String s) {
        this.dir = new File(s);
    }
    
    public String getFileMax(final String s) {
        int fileIndex = -1;
        final String fileType = this.getFileType(s);
        final String lastPart = this.getLastPart(s);
        if (fileType.equalsIgnoreCase("INVALID_FORMAT") || lastPart.equalsIgnoreCase("INVAILD_FORMAT")) {
            return "INVALID_FORMAT";
        }
        String s2 = null;
        if (!this.dirExists()) {
            return null;
        }
        final String[] list = this.dir.list();
        for (int i = 0; i < list.length; ++i) {
            final String fileType2 = this.getFileType(list[i]);
            final String lastPart2 = this.getLastPart(list[i]);
            if (fileType2.equals(fileType) && lastPart2.equals(lastPart) && fileIndex < this.getFileIndex(list[i])) {
                fileIndex = this.getFileIndex(list[i]);
                s2 = list[i];
            }
        }
        if (fileIndex == -1) {
            return "NOT_PRESENT";
        }
        return s2;
    }
    
    public int getFileCount(final String s) {
        int n = 0;
        final String fileType = this.getFileType(s);
        if (fileType.equalsIgnoreCase("INVALID_FORMAT")) {
            return -2;
        }
        if (this.dirExists()) {
            final String[] list = this.dir.list();
            for (int i = 0; i < list.length; ++i) {
                if (list[i].startsWith(fileType)) {
                    ++n;
                }
            }
            return n;
        }
        return -1;
    }
    
    public String getFileType(final String s) {
        final String firstPart = this.getFirstPart(s);
        final String string = new Integer(this.getIntPart(s)).toString();
        if (string.equals("-2")) {
            return "INVALID_FORMAT";
        }
        if (string.equals("0")) {
            return firstPart;
        }
        return firstPart.substring(0, firstPart.lastIndexOf(string));
    }
    
    public int getFileIndex(final String s) {
        return this.getIntPart(s);
    }
    
    public String getFileNext(final String s) {
        int intPart = this.getIntPart(s);
        if (intPart == -2) {
            return "INVALID_FORMAT";
        }
        ++intPart;
        return this.getFileType(s) + new Integer(intPart).toString() + "." + this.getLastPart(s);
    }
    
    private boolean dirExists() {
        return this.dir.exists() && this.dir.isDirectory();
    }
    
    private String getFirstPart(final String s) {
        if (s.indexOf(".") <= -1) {
            return s;
        }
        if (s.indexOf(".") == 0) {
            return "INVALID_FORMAT";
        }
        return s.substring(0, s.lastIndexOf("."));
    }
    
    private String getLastPart(final String s) {
        if (s.indexOf(".") <= -1) {
            return "";
        }
        if (s.charAt(0) == '.') {
            return "INVALID_FORMAT";
        }
        if (s.lastIndexOf(".") == s.length() - 1) {
            return "";
        }
        return s.substring(s.lastIndexOf(".") + 1, s.length());
    }
    
    private int getIntPart(final String s) {
        final String firstPart = this.getFirstPart(s);
        if (firstPart.equals("INVALID_FORMAT")) {
            return -2;
        }
        if (firstPart == "") {
            return 0;
        }
        String string = "";
        try {
            for (int i = firstPart.length() - 1; i >= 0; --i) {
                final char char1 = firstPart.charAt(i);
                if (!Character.isDigit(char1)) {
                    break;
                }
                string += char1;
            }
            String string2 = "";
            if (string.length() > 0) {
                for (int j = string.length() - 1; j >= 0; --j) {
                    string2 += string.charAt(j);
                }
                return Integer.parseInt(string2);
            }
            return 0;
        }
        catch (final Exception ex) {
            System.out.println(ex.toString());
            return -2;
        }
    }
}
