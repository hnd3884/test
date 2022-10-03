package org.apache.axiom.attachments;

import java.io.IOException;
import org.apache.commons.logging.Log;
import java.io.InputStream;

final class DebugInputStream extends InputStream
{
    private final InputStream parent;
    private final Log log;
    private long read;
    private int chunks;
    private boolean logged;
    
    DebugInputStream(final InputStream parent, final Log log) {
        this.parent = parent;
        this.log = log;
    }
    
    private void log(final IOException ex) {
        if (!this.logged) {
            this.log.debug((Object)("IOException occurred after reading " + this.read + " bytes in " + this.chunks + " chunks"), (Throwable)ex);
            this.logged = true;
        }
    }
    
    private void logEOF() {
        if (!this.logged) {
            this.log.debug((Object)("EOF reached after reading " + this.read + " bytes in " + this.chunks + " chunks"));
            this.logged = true;
        }
    }
    
    @Override
    public int available() throws IOException {
        try {
            return this.parent.available();
        }
        catch (final IOException ex) {
            this.log(ex);
            throw ex;
        }
    }
    
    @Override
    public boolean markSupported() {
        return this.parent.markSupported();
    }
    
    @Override
    public void mark(final int readlimit) {
        this.parent.mark(readlimit);
    }
    
    @Override
    public void reset() throws IOException {
        try {
            this.parent.reset();
        }
        catch (final IOException ex) {
            this.log(ex);
            throw ex;
        }
    }
    
    @Override
    public int read() throws IOException {
        int result;
        try {
            result = this.parent.read();
        }
        catch (final IOException ex) {
            this.log(ex);
            throw ex;
        }
        if (result == -1) {
            this.logEOF();
        }
        else {
            ++this.read;
            ++this.chunks;
        }
        return result;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int c;
        try {
            c = this.parent.read(b, off, len);
        }
        catch (final IOException ex) {
            this.log(ex);
            throw ex;
        }
        if (c == -1) {
            this.logEOF();
        }
        else {
            this.read += c;
            ++this.chunks;
        }
        return c;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        try {
            return this.parent.skip(n);
        }
        catch (final IOException ex) {
            this.log(ex);
            throw ex;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.logged) {
            this.log.debug((Object)("Closing stream after reading " + this.read + " bytes in " + this.chunks + " chunks"));
            this.logged = true;
        }
        this.parent.close();
    }
}
