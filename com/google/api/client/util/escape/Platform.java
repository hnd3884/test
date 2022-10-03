package com.google.api.client.util.escape;

final class Platform
{
    private static final ThreadLocal<char[]> DEST_TL;
    
    private Platform() {
    }
    
    static char[] charBufferFromThreadLocal() {
        return Platform.DEST_TL.get();
    }
    
    static {
        DEST_TL = new ThreadLocal<char[]>() {
            @Override
            protected char[] initialValue() {
                return new char[1024];
            }
        };
    }
}
