package com.sun.nio.zipfs;

import java.util.Formatter;
import java.util.Arrays;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributes;

public class ZipFileAttributes implements BasicFileAttributes
{
    private final ZipFileSystem.Entry e;
    
    ZipFileAttributes(final ZipFileSystem.Entry e) {
        this.e = e;
    }
    
    @Override
    public FileTime creationTime() {
        if (this.e.ctime != -1L) {
            return FileTime.fromMillis(this.e.ctime);
        }
        return null;
    }
    
    @Override
    public boolean isDirectory() {
        return this.e.isDir();
    }
    
    @Override
    public boolean isOther() {
        return false;
    }
    
    @Override
    public boolean isRegularFile() {
        return !this.e.isDir();
    }
    
    @Override
    public FileTime lastAccessTime() {
        if (this.e.atime != -1L) {
            return FileTime.fromMillis(this.e.atime);
        }
        return null;
    }
    
    @Override
    public FileTime lastModifiedTime() {
        return FileTime.fromMillis(this.e.mtime);
    }
    
    @Override
    public long size() {
        return this.e.size;
    }
    
    @Override
    public boolean isSymbolicLink() {
        return false;
    }
    
    @Override
    public Object fileKey() {
        return null;
    }
    
    public long compressedSize() {
        return this.e.csize;
    }
    
    public long crc() {
        return this.e.crc;
    }
    
    public int method() {
        return this.e.method;
    }
    
    public byte[] extra() {
        if (this.e.extra != null) {
            return Arrays.copyOf(this.e.extra, this.e.extra.length);
        }
        return null;
    }
    
    public byte[] comment() {
        if (this.e.comment != null) {
            return Arrays.copyOf(this.e.comment, this.e.comment.length);
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(1024);
        final Formatter formatter = new Formatter(sb);
        if (this.creationTime() != null) {
            formatter.format("    creationTime    : %tc%n", this.creationTime().toMillis());
        }
        else {
            formatter.format("    creationTime    : null%n", new Object[0]);
        }
        if (this.lastAccessTime() != null) {
            formatter.format("    lastAccessTime  : %tc%n", this.lastAccessTime().toMillis());
        }
        else {
            formatter.format("    lastAccessTime  : null%n", new Object[0]);
        }
        formatter.format("    lastModifiedTime: %tc%n", this.lastModifiedTime().toMillis());
        formatter.format("    isRegularFile   : %b%n", this.isRegularFile());
        formatter.format("    isDirectory     : %b%n", this.isDirectory());
        formatter.format("    isSymbolicLink  : %b%n", this.isSymbolicLink());
        formatter.format("    isOther         : %b%n", this.isOther());
        formatter.format("    fileKey         : %s%n", this.fileKey());
        formatter.format("    size            : %d%n", this.size());
        formatter.format("    compressedSize  : %d%n", this.compressedSize());
        formatter.format("    crc             : %x%n", this.crc());
        formatter.format("    method          : %d%n", this.method());
        formatter.close();
        return sb.toString();
    }
}
