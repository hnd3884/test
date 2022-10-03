package com.zoho.framework.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Collection;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import java.io.FilenameFilter;

public class FileNameFilter implements FilenameFilter
{
    private int patternListCount;
    private String startsWith;
    private String endsWith;
    private List<Pattern> inPatterns;
    private List<Pattern> exPatterns;
    private boolean isDirectory;
    
    public FileNameFilter(final List<Pattern> includePatterns, final List<Pattern> excludePatterns) {
        this.patternListCount = 0;
        this.startsWith = null;
        this.endsWith = null;
        this.inPatterns = null;
        this.exPatterns = null;
        this.isDirectory = false;
        this.inPatterns = ((includePatterns != null && includePatterns.size() > 0) ? new ArrayList<Pattern>(includePatterns) : null);
        this.exPatterns = ((excludePatterns != null && excludePatterns.size() > 0) ? new ArrayList<Pattern>(excludePatterns) : null);
        this.patternListCount += ((includePatterns != null) ? 1 : 0);
        this.patternListCount += ((excludePatterns == null) ? 0 : 2);
        if (this.patternListCount == 0) {
            throw new IllegalArgumentException("Both includePatterns and excludePatterns in the FileNameFilter cannot be null, if so ignore the FileNameFilter object itself");
        }
    }
    
    public FileNameFilter(final String startsWith, final String endsWith) {
        this.patternListCount = 0;
        this.startsWith = null;
        this.endsWith = null;
        this.inPatterns = null;
        this.exPatterns = null;
        this.isDirectory = false;
        this.startsWith = ((startsWith == null) ? startsWith : startsWith.toLowerCase(Locale.ENGLISH));
        this.endsWith = ((endsWith == null) ? endsWith : endsWith.toLowerCase(Locale.ENGLISH));
        if (this.startsWith == null && this.endsWith == null) {
            throw new IllegalArgumentException("Both startsWith and endsWith in the FileNameFilter cannot be null, if so ignore the FileNameFilter object itself");
        }
    }
    
    public FileNameFilter(final boolean isDir) {
        this.patternListCount = 0;
        this.startsWith = null;
        this.endsWith = null;
        this.inPatterns = null;
        this.exPatterns = null;
        this.isDirectory = false;
        this.isDirectory = isDir;
    }
    
    public static boolean matches(final List<Pattern> patterns, final String value) {
        if (patterns == null) {
            throw new IllegalArgumentException("patternList cannot be null");
        }
        for (final Pattern p : patterns) {
            if (p == null) {
                throw new IllegalArgumentException("pattern cannot be null");
            }
            if (p.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean accept(final File directory, final String fileName) {
        final String fileStr = directory + (directory.toString().endsWith("/") ? "" : "/") + fileName;
        if (this.patternListCount > 0) {
            switch (this.patternListCount) {
                case 1: {
                    return matches(this.inPatterns, fileStr);
                }
                case 2: {
                    return !matches(this.exPatterns, fileStr);
                }
                case 3: {
                    return matches(this.inPatterns, fileStr) && !matches(this.exPatterns, fileStr);
                }
            }
        }
        final String fileNameInLC = fileName.toLowerCase(Locale.ENGLISH);
        if (this.isDirectory) {
            return new File(fileStr).isDirectory();
        }
        return (this.startsWith == null || fileNameInLC.startsWith(this.startsWith)) && (this.endsWith == null || fileNameInLC.endsWith(this.endsWith));
    }
}
