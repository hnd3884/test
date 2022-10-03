package org.tukaani.xz;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;

public class SeekableFileInputStream extends SeekableInputStream
{
    protected RandomAccessFile randomAccessFile;
    
    public SeekableFileInputStream(final File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "r");
    }
    
    public SeekableFileInputStream(final String s) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(s, "r");
    }
    
    public SeekableFileInputStream(final RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }
    
    @Override
    public int read() throws IOException {
        return this.randomAccessFile.read();
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.randomAccessFile.read(array);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.randomAccessFile.read(array, n, n2);
    }
    
    @Override
    public void close() throws IOException {
        this.randomAccessFile.close();
    }
    
    @Override
    public long length() throws IOException {
        return this.randomAccessFile.length();
    }
    
    @Override
    public long position() throws IOException {
        return this.randomAccessFile.getFilePointer();
    }
    
    @Override
    public void seek(final long n) throws IOException {
        this.randomAccessFile.seek(n);
    }
}
