package com.azul.crs.client.buffer;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferManager
{
    private Buffer[] buffers;
    private int bufferSize;
    
    private static class Buffer
    {
        private byte[] data;
        private AtomicInteger pos;
    }
    
    private static class BufferWriter extends Writer
    {
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
