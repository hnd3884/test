package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.EOFException;
import sun.rmi.runtime.Log;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.FilterInputStream;

class HttpInputStream extends FilterInputStream
{
    protected int bytesLeft;
    protected int bytesLeftAtMark;
    
    public HttpInputStream(final InputStream inputStream) throws IOException {
        super(inputStream);
        if (inputStream.markSupported()) {
            inputStream.mark(0);
        }
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final String lowerCase = "Content-length:".toLowerCase();
        int n = 0;
        String line;
        do {
            line = dataInputStream.readLine();
            if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
                RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received header line: \"" + line + "\"");
            }
            if (line == null) {
                throw new EOFException();
            }
            if (!line.toLowerCase().startsWith(lowerCase)) {
                continue;
            }
            if (n != 0) {
                throw new IOException("Multiple Content-length entries found.");
            }
            this.bytesLeft = Integer.parseInt(line.substring(lowerCase.length()).trim());
            n = 1;
        } while (line.length() != 0 && line.charAt(0) != '\r' && line.charAt(0) != '\n');
        if (n == 0 || this.bytesLeft < 0) {
            this.bytesLeft = Integer.MAX_VALUE;
        }
        this.bytesLeftAtMark = this.bytesLeft;
        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "content length: " + this.bytesLeft);
        }
    }
    
    @Override
    public int available() throws IOException {
        int n = this.in.available();
        if (n > this.bytesLeft) {
            n = this.bytesLeft;
        }
        return n;
    }
    
    @Override
    public int read() throws IOException {
        if (this.bytesLeft > 0) {
            final int read = this.in.read();
            if (read != -1) {
                --this.bytesLeft;
            }
            if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
                RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received byte: '" + (((read & 0x7F) < 32) ? " " : String.valueOf((char)read)) + "' " + read);
            }
            return read;
        }
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
        return -1;
    }
    
    @Override
    public int read(final byte[] array, final int n, int bytesLeft) throws IOException {
        if (this.bytesLeft == 0 && bytesLeft > 0) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
            return -1;
        }
        if (bytesLeft > this.bytesLeft) {
            bytesLeft = this.bytesLeft;
        }
        final int read = this.in.read(array, n, bytesLeft);
        this.bytesLeft -= read;
        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
            RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read " + read + " bytes, " + this.bytesLeft + " remaining");
        }
        return read;
    }
    
    @Override
    public void mark(final int n) {
        this.in.mark(n);
        if (this.in.markSupported()) {
            this.bytesLeftAtMark = this.bytesLeft;
        }
    }
    
    @Override
    public void reset() throws IOException {
        this.in.reset();
        this.bytesLeft = this.bytesLeftAtMark;
    }
    
    @Override
    public long skip(long n) throws IOException {
        if (n > this.bytesLeft) {
            n = this.bytesLeft;
        }
        final long skip = this.in.skip(n);
        this.bytesLeft -= (int)skip;
        return skip;
    }
}
