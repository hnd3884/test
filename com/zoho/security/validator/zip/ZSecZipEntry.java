package com.zoho.security.validator.zip;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import java.util.zip.ZipEntry;

class ZSecZipEntry
{
    private ZipEntry zipEntry;
    private SevenZArchiveEntry sevenZEntry;
    private boolean is7ZipEntry;
    
    ZSecZipEntry(final Object entry, final boolean is7ZipFile) {
        if (is7ZipFile) {
            this.sevenZEntry = (SevenZArchiveEntry)entry;
            this.is7ZipEntry = true;
        }
        else {
            this.zipEntry = (ZipEntry)entry;
        }
    }
    
    String getName() {
        return this.is7ZipEntry ? this.sevenZEntry.getName() : this.zipEntry.getName();
    }
    
    long getSize() {
        return this.is7ZipEntry ? this.sevenZEntry.getSize() : this.zipEntry.getSize();
    }
    
    ZipEntry getZipEntry() {
        return this.zipEntry;
    }
    
    SevenZArchiveEntry getSevenZipEntry() {
        return this.sevenZEntry;
    }
    
    boolean isDirectory() {
        return this.is7ZipEntry ? this.sevenZEntry.isDirectory() : this.zipEntry.isDirectory();
    }
}
