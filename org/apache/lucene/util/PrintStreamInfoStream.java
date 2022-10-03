package org.apache.lucene.util;

import java.nio.file.attribute.FileTime;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintStreamInfoStream extends InfoStream
{
    private static final AtomicInteger MESSAGE_ID;
    protected final int messageID;
    protected final PrintStream stream;
    
    public PrintStreamInfoStream(final PrintStream stream) {
        this(stream, PrintStreamInfoStream.MESSAGE_ID.getAndIncrement());
    }
    
    public PrintStreamInfoStream(final PrintStream stream, final int messageID) {
        this.stream = stream;
        this.messageID = messageID;
    }
    
    @Override
    public void message(final String component, final String message) {
        this.stream.println(component + " " + this.messageID + " [" + this.getTimestamp() + "; " + Thread.currentThread().getName() + "]: " + message);
    }
    
    @Override
    public boolean isEnabled(final String component) {
        return true;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.isSystemStream()) {
            this.stream.close();
        }
    }
    
    @SuppressForbidden(reason = "System.out/err detection")
    public boolean isSystemStream() {
        return this.stream == System.out || this.stream == System.err;
    }
    
    protected String getTimestamp() {
        return FileTime.fromMillis(System.currentTimeMillis()).toString();
    }
    
    static {
        MESSAGE_ID = new AtomicInteger();
    }
}
