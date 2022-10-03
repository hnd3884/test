package com.sun.nio.zipfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.file.attribute.FileTime;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;

public class ZipFileAttributeView implements BasicFileAttributeView
{
    private final ZipPath path;
    private final boolean isZipView;
    
    private ZipFileAttributeView(final ZipPath path, final boolean isZipView) {
        this.path = path;
        this.isZipView = isZipView;
    }
    
    static <V extends FileAttributeView> V get(final ZipPath zipPath, final Class<V> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        if (clazz == BasicFileAttributeView.class) {
            return (V)new ZipFileAttributeView(zipPath, false);
        }
        if (clazz == ZipFileAttributeView.class) {
            return (V)new ZipFileAttributeView(zipPath, true);
        }
        return null;
    }
    
    static ZipFileAttributeView get(final ZipPath zipPath, final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("basic")) {
            return new ZipFileAttributeView(zipPath, false);
        }
        if (s.equals("zip")) {
            return new ZipFileAttributeView(zipPath, true);
        }
        return null;
    }
    
    @Override
    public String name() {
        return this.isZipView ? "zip" : "basic";
    }
    
    @Override
    public ZipFileAttributes readAttributes() throws IOException {
        return this.path.getAttributes();
    }
    
    @Override
    public void setTimes(final FileTime fileTime, final FileTime fileTime2, final FileTime fileTime3) throws IOException {
        this.path.setTimes(fileTime, fileTime2, fileTime3);
    }
    
    void setAttribute(final String s, final Object o) throws IOException {
        try {
            if (AttrID.valueOf(s) == AttrID.lastModifiedTime) {
                this.setTimes((FileTime)o, null, null);
            }
            if (AttrID.valueOf(s) == AttrID.lastAccessTime) {
                this.setTimes(null, (FileTime)o, null);
            }
            if (AttrID.valueOf(s) == AttrID.creationTime) {
                this.setTimes(null, null, (FileTime)o);
            }
        }
        catch (final IllegalArgumentException ex) {
            throw new UnsupportedOperationException("'" + s + "' is unknown or read-only attribute");
        }
    }
    
    Map<String, Object> readAttributes(final String s) throws IOException {
        final ZipFileAttributes attributes = this.readAttributes();
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        if ("*".equals(s)) {
            for (final AttrID attrID : AttrID.values()) {
                try {
                    linkedHashMap.put(attrID.name(), this.attribute(attrID, attributes));
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        else {
            for (final String s2 : s.split(",")) {
                try {
                    linkedHashMap.put(s2, this.attribute(AttrID.valueOf(s2), attributes));
                }
                catch (final IllegalArgumentException ex2) {}
            }
        }
        return linkedHashMap;
    }
    
    Object attribute(final AttrID attrID, final ZipFileAttributes zipFileAttributes) {
        switch (attrID) {
            case size: {
                return zipFileAttributes.size();
            }
            case creationTime: {
                return zipFileAttributes.creationTime();
            }
            case lastAccessTime: {
                return zipFileAttributes.lastAccessTime();
            }
            case lastModifiedTime: {
                return zipFileAttributes.lastModifiedTime();
            }
            case isDirectory: {
                return zipFileAttributes.isDirectory();
            }
            case isRegularFile: {
                return zipFileAttributes.isRegularFile();
            }
            case isSymbolicLink: {
                return zipFileAttributes.isSymbolicLink();
            }
            case isOther: {
                return zipFileAttributes.isOther();
            }
            case fileKey: {
                return zipFileAttributes.fileKey();
            }
            case compressedSize: {
                if (this.isZipView) {
                    return zipFileAttributes.compressedSize();
                }
                break;
            }
            case crc: {
                if (this.isZipView) {
                    return zipFileAttributes.crc();
                }
                break;
            }
            case method: {
                if (this.isZipView) {
                    return zipFileAttributes.method();
                }
                break;
            }
        }
        return null;
    }
    
    private enum AttrID
    {
        size, 
        creationTime, 
        lastAccessTime, 
        lastModifiedTime, 
        isDirectory, 
        isRegularFile, 
        isSymbolicLink, 
        isOther, 
        fileKey, 
        compressedSize, 
        crc, 
        method;
    }
}
