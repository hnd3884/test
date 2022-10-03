package org.apache.lucene.util;

import java.io.Closeable;

public abstract class InfoStream implements Closeable
{
    public static final InfoStream NO_OUTPUT;
    private static InfoStream defaultInfoStream;
    
    public abstract void message(final String p0, final String p1);
    
    public abstract boolean isEnabled(final String p0);
    
    public static synchronized InfoStream getDefault() {
        return InfoStream.defaultInfoStream;
    }
    
    public static synchronized void setDefault(final InfoStream infoStream) {
        if (infoStream == null) {
            throw new IllegalArgumentException("Cannot set InfoStream default implementation to null. To disable logging use InfoStream.NO_OUTPUT");
        }
        InfoStream.defaultInfoStream = infoStream;
    }
    
    static {
        NO_OUTPUT = new NoOutput();
        InfoStream.defaultInfoStream = InfoStream.NO_OUTPUT;
    }
    
    private static final class NoOutput extends InfoStream
    {
        @Override
        public void message(final String component, final String message) {
            assert false : "message() should not be called when isEnabled returns false";
        }
        
        @Override
        public boolean isEnabled(final String component) {
            return false;
        }
        
        @Override
        public void close() {
        }
    }
}
