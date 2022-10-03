package com.zoho.security.validator.zip;

import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import java.util.zip.ZipOutputStream;

class ZSecZipOutputFile
{
    private ZipOutputStream zipOutputStream;
    private SevenZOutputFile sevenZipOutputFile;
    private boolean is7Zip;
    
    ZSecZipOutputFile(final File destination, final boolean is7ZipFile) throws IOException {
        if (is7ZipFile) {
            this.sevenZipOutputFile = new SevenZOutputFile(destination);
            this.is7Zip = true;
        }
        else {
            this.zipOutputStream = new ZipOutputStream(new FileOutputStream(destination));
        }
    }
    
    void putEntry(final String zipEntry) throws IOException {
        if (this.is7Zip) {
            ZSec7ZipOutputFile.putEntry(this.sevenZipOutputFile, zipEntry);
        }
        else {
            this.zipOutputStream.putNextEntry(new ZipEntry(zipEntry));
        }
    }
    
    void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.is7Zip) {
            this.sevenZipOutputFile.write(b, off, len);
        }
        else {
            this.zipOutputStream.write(b, off, len);
        }
    }
    
    void closeEntry() throws IOException {
        if (this.is7Zip) {
            this.sevenZipOutputFile.closeArchiveEntry();
        }
        else {
            this.zipOutputStream.closeEntry();
        }
    }
    
    void close() throws IOException {
        if (this.is7Zip) {
            this.sevenZipOutputFile.close();
        }
        else {
            this.zipOutputStream.close();
        }
    }
}
