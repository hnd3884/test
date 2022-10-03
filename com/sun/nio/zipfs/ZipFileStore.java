package com.sun.nio.zipfs;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.io.IOException;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.FileStore;

public class ZipFileStore extends FileStore
{
    private final ZipFileSystem zfs;
    
    ZipFileStore(final ZipPath zipPath) {
        this.zfs = zipPath.getFileSystem();
    }
    
    @Override
    public String name() {
        return this.zfs.toString() + "/";
    }
    
    @Override
    public String type() {
        return "zipfs";
    }
    
    @Override
    public boolean isReadOnly() {
        return this.zfs.isReadOnly();
    }
    
    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> clazz) {
        return clazz == BasicFileAttributeView.class || clazz == ZipFileAttributeView.class;
    }
    
    @Override
    public boolean supportsFileAttributeView(final String s) {
        return s.equals("basic") || s.equals("zip");
    }
    
    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        return null;
    }
    
    @Override
    public long getTotalSpace() throws IOException {
        return new ZipFileStoreAttributes(this).totalSpace();
    }
    
    @Override
    public long getUsableSpace() throws IOException {
        return new ZipFileStoreAttributes(this).usableSpace();
    }
    
    @Override
    public long getUnallocatedSpace() throws IOException {
        return new ZipFileStoreAttributes(this).unallocatedSpace();
    }
    
    @Override
    public Object getAttribute(final String s) throws IOException {
        if (s.equals("totalSpace")) {
            return this.getTotalSpace();
        }
        if (s.equals("usableSpace")) {
            return this.getUsableSpace();
        }
        if (s.equals("unallocatedSpace")) {
            return this.getUnallocatedSpace();
        }
        throw new UnsupportedOperationException("does not support the given attribute");
    }
    
    private static class ZipFileStoreAttributes
    {
        final FileStore fstore;
        final long size;
        
        public ZipFileStoreAttributes(final ZipFileStore zipFileStore) throws IOException {
            final Path path = FileSystems.getDefault().getPath(zipFileStore.name(), new String[0]);
            this.size = Files.size(path);
            this.fstore = Files.getFileStore(path);
        }
        
        public long totalSpace() {
            return this.size;
        }
        
        public long usableSpace() throws IOException {
            if (!this.fstore.isReadOnly()) {
                return this.fstore.getUsableSpace();
            }
            return 0L;
        }
        
        public long unallocatedSpace() throws IOException {
            if (!this.fstore.isReadOnly()) {
                return this.fstore.getUnallocatedSpace();
            }
            return 0L;
        }
    }
}
