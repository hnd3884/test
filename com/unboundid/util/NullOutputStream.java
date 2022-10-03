package com.unboundid.util;

import java.io.PrintStream;
import java.io.OutputStream;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class NullOutputStream extends OutputStream
{
    private static final NullOutputStream INSTANCE;
    private static final PrintStream PRINT_STREAM;
    
    public static NullOutputStream getInstance() {
        return NullOutputStream.INSTANCE;
    }
    
    public static PrintStream getPrintStream() {
        return NullOutputStream.PRINT_STREAM;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void write(final byte[] b) {
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
    }
    
    @Override
    public void write(final int b) {
    }
    
    static {
        INSTANCE = new NullOutputStream();
        PRINT_STREAM = new PrintStream(NullOutputStream.INSTANCE);
    }
}
