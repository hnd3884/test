package com.sun.media.sound;

import java.io.DataInputStream;
import java.util.Iterator;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public final class ModelByteBuffer
{
    private ModelByteBuffer root;
    private File file;
    private long fileoffset;
    private byte[] buffer;
    private long offset;
    private final long len;
    
    private ModelByteBuffer(final ModelByteBuffer modelByteBuffer, long offset, long n, final boolean b) {
        this.root = this;
        this.root = modelByteBuffer.root;
        this.offset = 0L;
        final long len = modelByteBuffer.len;
        if (offset < 0L) {
            offset = 0L;
        }
        if (offset > len) {
            offset = len;
        }
        if (n < 0L) {
            n = 0L;
        }
        if (n > len) {
            n = len;
        }
        if (offset > n) {
            offset = n;
        }
        this.offset = offset;
        this.len = n - offset;
        if (b) {
            this.buffer = this.root.buffer;
            if (this.root.file != null) {
                this.file = this.root.file;
                this.fileoffset = this.root.fileoffset + this.arrayOffset();
                this.offset = 0L;
            }
            else {
                this.offset = this.arrayOffset();
            }
            this.root = this;
        }
    }
    
    public ModelByteBuffer(final byte[] buffer) {
        this.root = this;
        this.buffer = buffer;
        this.offset = 0L;
        this.len = buffer.length;
    }
    
    public ModelByteBuffer(final byte[] buffer, final int n, final int n2) {
        this.root = this;
        this.buffer = buffer;
        this.offset = n;
        this.len = n2;
    }
    
    public ModelByteBuffer(final File file) {
        this.root = this;
        this.file = file;
        this.fileoffset = 0L;
        this.len = file.length();
    }
    
    public ModelByteBuffer(final File file, final long fileoffset, final long len) {
        this.root = this;
        this.file = file;
        this.fileoffset = fileoffset;
        this.len = len;
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        if (this.root.file != null && this.root.buffer == null) {
            try (final InputStream inputStream = this.getInputStream()) {
                final byte[] array = new byte[1024];
                int read;
                while ((read = inputStream.read(array)) != -1) {
                    outputStream.write(array, 0, read);
                }
            }
        }
        else {
            outputStream.write(this.array(), (int)this.arrayOffset(), (int)this.capacity());
        }
    }
    
    public InputStream getInputStream() {
        if (this.root.file != null && this.root.buffer == null) {
            try {
                return new RandomFileInputStream();
            }
            catch (final IOException ex) {
                return null;
            }
        }
        return new ByteArrayInputStream(this.array(), (int)this.arrayOffset(), (int)this.capacity());
    }
    
    public ModelByteBuffer subbuffer(final long n) {
        return this.subbuffer(n, this.capacity());
    }
    
    public ModelByteBuffer subbuffer(final long n, final long n2) {
        return this.subbuffer(n, n2, false);
    }
    
    public ModelByteBuffer subbuffer(final long n, final long n2, final boolean b) {
        return new ModelByteBuffer(this, n, n2, b);
    }
    
    public byte[] array() {
        return this.root.buffer;
    }
    
    public long arrayOffset() {
        if (this.root != this) {
            return this.root.arrayOffset() + this.offset;
        }
        return this.offset;
    }
    
    public long capacity() {
        return this.len;
    }
    
    public ModelByteBuffer getRoot() {
        return this.root;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public long getFilePointer() {
        return this.fileoffset;
    }
    
    public static void loadAll(final Collection<ModelByteBuffer> collection) throws IOException {
        File file = null;
        RandomAccessFile randomAccessFile = null;
        try {
            final Iterator<ModelByteBuffer> iterator = collection.iterator();
            while (iterator.hasNext()) {
                final ModelByteBuffer root = iterator.next().root;
                if (root.file == null) {
                    continue;
                }
                if (root.buffer != null) {
                    continue;
                }
                if (file == null || !file.equals(root.file)) {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                        randomAccessFile = null;
                    }
                    file = root.file;
                    randomAccessFile = new RandomAccessFile(root.file, "r");
                }
                randomAccessFile.seek(root.fileoffset);
                final byte[] buffer = new byte[(int)root.capacity()];
                int i = 0;
                final int length = buffer.length;
                while (i != length) {
                    if (length - i > 65536) {
                        randomAccessFile.readFully(buffer, i, 65536);
                        i += 65536;
                    }
                    else {
                        randomAccessFile.readFully(buffer, i, length - i);
                        i = length;
                    }
                }
                root.buffer = buffer;
                root.offset = 0L;
            }
        }
        finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }
    
    public void load() throws IOException {
        if (this.root != this) {
            this.root.load();
            return;
        }
        if (this.buffer != null) {
            return;
        }
        if (this.file == null) {
            throw new IllegalStateException("No file associated with this ByteBuffer!");
        }
        final DataInputStream dataInputStream = new DataInputStream(this.getInputStream());
        this.buffer = new byte[(int)this.capacity()];
        this.offset = 0L;
        dataInputStream.readFully(this.buffer);
        dataInputStream.close();
    }
    
    public void unload() {
        if (this.root != this) {
            this.root.unload();
            return;
        }
        if (this.file == null) {
            throw new IllegalStateException("No file associated with this ByteBuffer!");
        }
        this.root.buffer = null;
    }
    
    private class RandomFileInputStream extends InputStream
    {
        private final RandomAccessFile raf;
        private long left;
        private long mark;
        private long markleft;
        
        RandomFileInputStream() throws IOException {
            this.mark = 0L;
            this.markleft = 0L;
            (this.raf = new RandomAccessFile(ModelByteBuffer.this.root.file, "r")).seek(ModelByteBuffer.this.root.fileoffset + ModelByteBuffer.this.arrayOffset());
            this.left = ModelByteBuffer.this.capacity();
        }
        
        @Override
        public int available() throws IOException {
            if (this.left > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int)this.left;
        }
        
        @Override
        public synchronized void mark(final int n) {
            try {
                this.mark = this.raf.getFilePointer();
                this.markleft = this.left;
            }
            catch (final IOException ex) {}
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public synchronized void reset() throws IOException {
            this.raf.seek(this.mark);
            this.left = this.markleft;
        }
        
        @Override
        public long skip(long left) throws IOException {
            if (left < 0L) {
                return 0L;
            }
            if (left > this.left) {
                left = this.left;
            }
            this.raf.seek(this.raf.getFilePointer() + left);
            this.left -= left;
            return left;
        }
        
        @Override
        public int read(final byte[] array, final int n, int read) throws IOException {
            if (read > this.left) {
                read = (int)this.left;
            }
            if (this.left == 0L) {
                return -1;
            }
            read = this.raf.read(array, n, read);
            if (read == -1) {
                return -1;
            }
            this.left -= read;
            return read;
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            int length = array.length;
            if (length > this.left) {
                length = (int)this.left;
            }
            if (this.left == 0L) {
                return -1;
            }
            final int read = this.raf.read(array, 0, length);
            if (read == -1) {
                return -1;
            }
            this.left -= read;
            return read;
        }
        
        @Override
        public int read() throws IOException {
            if (this.left == 0L) {
                return -1;
            }
            final int read = this.raf.read();
            if (read == -1) {
                return -1;
            }
            --this.left;
            return read;
        }
        
        @Override
        public void close() throws IOException {
            this.raf.close();
        }
    }
}
