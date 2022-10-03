package com.sun.xml.internal.ws.util;

import java.util.logging.Level;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import com.sun.istack.internal.NotNull;
import java.io.InputStream;

public class ReadAllStream extends InputStream
{
    @NotNull
    private final MemoryStream memStream;
    @NotNull
    private final FileStream fileStream;
    private boolean readAll;
    private boolean closed;
    private static final Logger LOGGER;
    
    public ReadAllStream() {
        this.memStream = new MemoryStream();
        this.fileStream = new FileStream();
    }
    
    public void readAll(final InputStream in, final long inMemory) throws IOException {
        assert !this.readAll;
        this.readAll = true;
        final boolean eof = this.memStream.readAll(in, inMemory);
        if (!eof) {
            this.fileStream.readAll(in);
        }
    }
    
    @Override
    public int read() throws IOException {
        int ch = this.memStream.read();
        if (ch == -1) {
            ch = this.fileStream.read();
        }
        return ch;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int sz) throws IOException {
        int len = this.memStream.read(b, off, sz);
        if (len == -1) {
            len = this.fileStream.read(b, off, sz);
        }
        return len;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.memStream.close();
            this.fileStream.close();
            this.closed = true;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ReadAllStream.class.getName());
    }
    
    private static class FileStream extends InputStream
    {
        @Nullable
        private File tempFile;
        @Nullable
        private FileInputStream fin;
        
        void readAll(final InputStream in) throws IOException {
            this.tempFile = File.createTempFile("jaxws", ".bin");
            final FileOutputStream fileOut = new FileOutputStream(this.tempFile);
            try {
                final byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    fileOut.write(buf, 0, len);
                }
            }
            finally {
                fileOut.close();
            }
            this.fin = new FileInputStream(this.tempFile);
        }
        
        @Override
        public int read() throws IOException {
            return (this.fin != null) ? this.fin.read() : -1;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int sz) throws IOException {
            return (this.fin != null) ? this.fin.read(b, off, sz) : -1;
        }
        
        @Override
        public void close() throws IOException {
            if (this.fin != null) {
                this.fin.close();
            }
            if (this.tempFile != null) {
                final boolean success = this.tempFile.delete();
                if (!success) {
                    ReadAllStream.LOGGER.log(Level.INFO, "File {0} could not be deleted", this.tempFile);
                }
            }
        }
    }
    
    private static class MemoryStream extends InputStream
    {
        private Chunk head;
        private Chunk tail;
        private int curOff;
        
        private void add(final byte[] buf, final int len) {
            if (this.tail != null) {
                this.tail = this.tail.createNext(buf, 0, len);
            }
            else {
                final Chunk chunk = new Chunk(buf, 0, len);
                this.tail = chunk;
                this.head = chunk;
            }
        }
        
        boolean readAll(final InputStream in, final long inMemory) throws IOException {
            long total = 0L;
            while (true) {
                final byte[] buf = new byte[8192];
                final int read = this.fill(in, buf);
                total += read;
                if (read != 0) {
                    this.add(buf, read);
                }
                if (read != buf.length) {
                    return true;
                }
                if (total > inMemory) {
                    return false;
                }
            }
        }
        
        private int fill(final InputStream in, final byte[] buf) throws IOException {
            int total;
            int read;
            for (total = 0; total < buf.length && (read = in.read(buf, total, buf.length - total)) != -1; total += read) {}
            return total;
        }
        
        @Override
        public int read() throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            return this.head.buf[this.curOff++] & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, final int off, int sz) throws IOException {
            if (!this.fetch()) {
                return -1;
            }
            sz = Math.min(sz, this.head.len - (this.curOff - this.head.off));
            System.arraycopy(this.head.buf, this.curOff, b, off, sz);
            this.curOff += sz;
            return sz;
        }
        
        private boolean fetch() {
            if (this.head == null) {
                return false;
            }
            if (this.curOff == this.head.off + this.head.len) {
                this.head = this.head.next;
                if (this.head == null) {
                    return false;
                }
                this.curOff = this.head.off;
            }
            return true;
        }
        
        private static final class Chunk
        {
            Chunk next;
            final byte[] buf;
            final int off;
            final int len;
            
            public Chunk(final byte[] buf, final int off, final int len) {
                this.buf = buf;
                this.off = off;
                this.len = len;
            }
            
            public Chunk createNext(final byte[] buf, final int off, final int len) {
                return this.next = new Chunk(buf, off, len);
            }
        }
    }
}
