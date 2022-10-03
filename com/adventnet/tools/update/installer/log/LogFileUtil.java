package com.adventnet.tools.update.installer.log;

import java.io.File;

public class LogFileUtil
{
    private File dir;
    
    public LogFileUtil(final File dir) {
        this.dir = dir;
    }
    
    public LogFileUtil(final String dir) {
        this.dir = new File(dir);
    }
    
    public String getFileMax(final String fileName) {
        int max_index = -1;
        final String filetype = this.getFileType(fileName);
        final String lastpart = this.getLastPart(fileName);
        if (filetype.equalsIgnoreCase("INVALID_FORMAT") || lastpart.equalsIgnoreCase("INVAILD_FORMAT")) {
            return "INVALID_FORMAT";
        }
        String max_file = null;
        if (!this.dirExists()) {
            return null;
        }
        final String[] files = this.dir.list();
        for (int i = 0; i < files.length; ++i) {
            final String files_i_firstPart = this.getFileType(files[i]);
            final String files_i_lastPart = this.getLastPart(files[i]);
            if (files_i_firstPart.equals(filetype) && files_i_lastPart.equals(lastpart) && max_index < this.getFileIndex(files[i])) {
                max_index = this.getFileIndex(files[i]);
                max_file = files[i];
            }
        }
        if (max_index == -1) {
            return "NOT_PRESENT";
        }
        return max_file;
    }
    
    public int getFileCount(final String fileName) {
        int count = 0;
        final String filetype = this.getFileType(fileName);
        if (filetype.equalsIgnoreCase("INVALID_FORMAT")) {
            return -2;
        }
        if (this.dirExists()) {
            final String[] files = this.dir.list();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].startsWith(filetype)) {
                    ++count;
                }
            }
            return count;
        }
        return -1;
    }
    
    public String getFileType(final String fileName) {
        final String fp = this.getFirstPart(fileName);
        final String np = new Integer(this.getIntPart(fileName)).toString();
        if (np.equals("-2")) {
            return "INVALID_FORMAT";
        }
        if (np.equals("0")) {
            return fp;
        }
        return fp.substring(0, fp.lastIndexOf(np));
    }
    
    public int getFileIndex(final String fileName) {
        return this.getIntPart(fileName);
    }
    
    public String getFileNext(final String fileName) {
        int np = this.getIntPart(fileName);
        if (np == -2) {
            return "INVALID_FORMAT";
        }
        ++np;
        return this.getFileType(fileName) + new Integer(np).toString() + "." + this.getLastPart(fileName);
    }
    
    private boolean dirExists() {
        return this.dir.exists() && this.dir.isDirectory();
    }
    
    private String getFirstPart(final String fileName) {
        if (fileName.indexOf(".") <= -1) {
            return fileName;
        }
        if (fileName.indexOf(".") == 0) {
            return "INVALID_FORMAT";
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
    
    private String getLastPart(final String fileName) {
        if (fileName.indexOf(".") <= -1) {
            return "";
        }
        if (fileName.charAt(0) == '.') {
            return "INVALID_FORMAT";
        }
        if (fileName.lastIndexOf(".") == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }
    
    private int getIntPart(final String fileName) {
        final String firstPart = this.getFirstPart(fileName);
        if (firstPart.equals("INVALID_FORMAT")) {
            return -2;
        }
        if (firstPart == "") {
            return 0;
        }
        String s = "";
        try {
            for (int i = firstPart.length() - 1; i >= 0; --i) {
                final char c = firstPart.charAt(i);
                if (!Character.isDigit(c)) {
                    break;
                }
                s += c;
            }
            String stonum = "";
            if (s.length() > 0) {
                for (int j = s.length() - 1; j >= 0; --j) {
                    stonum += s.charAt(j);
                }
                return Integer.parseInt(stonum);
            }
            return 0;
        }
        catch (final Exception e) {
            System.out.println(e.toString());
            return -2;
        }
    }
}
