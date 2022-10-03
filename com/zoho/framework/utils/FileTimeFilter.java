package com.zoho.framework.utils;

import java.io.File;
import java.util.Arrays;
import java.io.FileFilter;

public class FileTimeFilter implements FileFilter
{
    private COMPARATOR comparator;
    private long[] timeArray;
    
    public FileTimeFilter(final COMPARATOR c, final long... lastModifiedTime) {
        this.comparator = COMPARATOR.EQUAL;
        this.timeArray = null;
        this.comparator = c;
        switch (this.comparator.intValue) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                if (lastModifiedTime.length != 1) {
                    throw new IllegalArgumentException("One lastModifiedTime should be passed for the comparators LESS_THAN/LESS_EQUAL/GREATER_THAN/GREATER_EQUAL/EQUAL/NOT_EQUAL");
                }
                break;
            }
            case 6:
            case 7: {
                if (lastModifiedTime.length != 0) {
                    throw new IllegalArgumentException("lastModifiedTime values should be passed for the comparators IN/NOT_IN");
                }
                break;
            }
            case 8:
            case 9: {
                if (lastModifiedTime.length != 2) {
                    throw new IllegalArgumentException("Two lastModifiedTime values should be passed for the comparators BETWEEN/NOT_BETWEEN");
                }
                break;
            }
        }
        System.arraycopy(lastModifiedTime, 0, this.timeArray = new long[lastModifiedTime.length], 0, lastModifiedTime.length);
        Arrays.sort(this.timeArray);
    }
    
    @Override
    public boolean accept(final File filePath) {
        switch (this.comparator.intValue) {
            case 0: {
                return filePath.lastModified() < this.timeArray[0];
            }
            case 1: {
                return filePath.lastModified() <= this.timeArray[0];
            }
            case 2: {
                return filePath.lastModified() > this.timeArray[0];
            }
            case 3: {
                return filePath.lastModified() >= this.timeArray[0];
            }
            case 4: {
                return filePath.lastModified() == this.timeArray[0];
            }
            case 5: {
                return filePath.lastModified() != this.timeArray[0];
            }
            case 6: {
                return Arrays.binarySearch(this.timeArray, filePath.lastModified()) >= 0;
            }
            case 7: {
                return Arrays.binarySearch(this.timeArray, filePath.lastModified()) < 0;
            }
            case 8: {
                return filePath.lastModified() >= this.timeArray[0] && filePath.lastModified() <= this.timeArray[1];
            }
            case 9: {
                return filePath.lastModified() >= this.timeArray[0] && filePath.lastModified() <= this.timeArray[1];
            }
            default: {
                return false;
            }
        }
    }
    
    public enum COMPARATOR
    {
        LESS_THAN(0), 
        LESS_EQUAL(1), 
        GREATER_THAN(2), 
        GREATER_EQUAL(3), 
        EQUAL(4), 
        NOT_EQUAL(5), 
        IN(6), 
        NOT_IN(7), 
        BETWEEN(8), 
        NOT_BETWEEN(9);
        
        int intValue;
        
        private COMPARATOR(final int i) {
            this.intValue = i;
        }
    }
}
