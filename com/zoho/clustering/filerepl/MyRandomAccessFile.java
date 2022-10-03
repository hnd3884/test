package com.zoho.clustering.filerepl;

import java.io.EOFException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.Closeable;

public class MyRandomAccessFile implements Closeable
{
    private RandomAccessFile raf;
    private File file;
    
    public MyRandomAccessFile(final File file, final String mode) {
        this.file = file;
        try {
            this.raf = new RandomAccessFile(file, mode);
        }
        catch (final FileNotFoundException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public MyRandomAccessFile(final String filePath, final String mode) {
        this(new File(filePath), mode);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public long length() {
        try {
            return this.raf.length();
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public long getFilePointer() {
        try {
            return this.raf.getFilePointer();
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public void seek(final long pos) {
        try {
            this.raf.seek(pos);
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public String readLine() {
        try {
            return this.raf.readLine();
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public String readUTF() throws EOFException {
        try {
            return this.raf.readUTF();
        }
        catch (final EOFException exp) {
            throw exp;
        }
        catch (final IOException exp2) {
            throw new RuntimeException(exp2);
        }
    }
    
    public void writeBytes(final String str) {
        try {
            this.raf.writeBytes(str);
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public void writeUTF(final String str) {
        try {
            this.raf.writeUTF(str);
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    @Override
    public void close() {
        try {
            this.raf.close();
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
}
