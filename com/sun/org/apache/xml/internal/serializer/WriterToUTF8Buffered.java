package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;

final class WriterToUTF8Buffered extends Writer implements WriterChain
{
    private static final int BYTES_MAX = 16384;
    private static final int CHARS_MAX = 5461;
    private final OutputStream m_os;
    private final byte[] m_outputBytes;
    private final char[] m_inputChars;
    private int count;
    
    public WriterToUTF8Buffered(final OutputStream out) throws UnsupportedEncodingException {
        this.m_os = out;
        this.m_outputBytes = new byte[16387];
        this.m_inputChars = new char[5463];
        this.count = 0;
    }
    
    @Override
    public void write(final int c) throws IOException {
        if (this.count >= 16384) {
            this.flushBuffer();
        }
        if (c < 128) {
            this.m_outputBytes[this.count++] = (byte)c;
        }
        else if (c < 2048) {
            this.m_outputBytes[this.count++] = (byte)(192 + (c >> 6));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        }
        else if (c < 65536) {
            this.m_outputBytes[this.count++] = (byte)(224 + (c >> 12));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        }
        else {
            this.m_outputBytes[this.count++] = (byte)(240 + (c >> 18));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 12 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c >> 6 & 0x3F));
            this.m_outputBytes[this.count++] = (byte)(128 + (c & 0x3F));
        }
    }
    
    @Override
    public void write(final char[] chars, final int start, final int length) throws IOException {
        final int lengthx3 = 3 * length;
        if (lengthx3 >= 16384 - this.count) {
            this.flushBuffer();
            if (lengthx3 > 16384) {
                final int split = length / 5461;
                int chunks;
                if (length % 5461 > 0) {
                    chunks = split + 1;
                }
                else {
                    chunks = split;
                }
                int end_chunk = start;
                for (int chunk = 1; chunk <= chunks; ++chunk) {
                    final int start_chunk = end_chunk;
                    end_chunk = start + (int)(length * (long)chunk / chunks);
                    final char c = chars[end_chunk - 1];
                    final int ic = chars[end_chunk - 1];
                    if (c >= '\ud800' && c <= '\udbff') {
                        if (end_chunk < start + length) {
                            ++end_chunk;
                        }
                        else {
                            --end_chunk;
                        }
                    }
                    final int len_chunk = end_chunk - start_chunk;
                    this.write(chars, start_chunk, len_chunk);
                }
                return;
            }
        }
        final int n = length + start;
        final byte[] buf_loc = this.m_outputBytes;
        int count_loc = this.count;
        int i;
        char c2;
        for (i = start; i < n && (c2 = chars[i]) < '\u0080'; ++i) {
            buf_loc[count_loc++] = (byte)c2;
        }
        while (i < n) {
            c2 = chars[i];
            if (c2 < '\u0080') {
                buf_loc[count_loc++] = (byte)c2;
            }
            else if (c2 < '\u0800') {
                buf_loc[count_loc++] = (byte)(192 + (c2 >> 6));
                buf_loc[count_loc++] = (byte)(128 + (c2 & '?'));
            }
            else if (c2 >= '\ud800' && c2 <= '\udbff') {
                final char high = c2;
                ++i;
                final char low = chars[i];
                buf_loc[count_loc++] = (byte)(0xF0 | (high + '@' >> 8 & 0xF0));
                buf_loc[count_loc++] = (byte)(0x80 | (high + '@' >> 2 & 0x3F));
                buf_loc[count_loc++] = (byte)(0x80 | (low >> 6 & 0xF) + (high << 4 & 0x30));
                buf_loc[count_loc++] = (byte)(0x80 | (low & '?'));
            }
            else {
                buf_loc[count_loc++] = (byte)(224 + (c2 >> 12));
                buf_loc[count_loc++] = (byte)(128 + (c2 >> 6 & 0x3F));
                buf_loc[count_loc++] = (byte)(128 + (c2 & '?'));
            }
            ++i;
        }
        this.count = count_loc;
    }
    
    @Override
    public void write(final String s) throws IOException {
        final int length = s.length();
        final int lengthx3 = 3 * length;
        if (lengthx3 >= 16384 - this.count) {
            this.flushBuffer();
            if (lengthx3 > 16384) {
                final int start = 0;
                final int split = length / 5461;
                int chunks;
                if (length % 5461 > 0) {
                    chunks = split + 1;
                }
                else {
                    chunks = split;
                }
                int end_chunk = 0;
                for (int chunk = 1; chunk <= chunks; ++chunk) {
                    final int start_chunk = end_chunk;
                    end_chunk = 0 + (int)(length * (long)chunk / chunks);
                    s.getChars(start_chunk, end_chunk, this.m_inputChars, 0);
                    int len_chunk = end_chunk - start_chunk;
                    final char c = this.m_inputChars[len_chunk - 1];
                    if (c >= '\ud800' && c <= '\udbff') {
                        --end_chunk;
                        --len_chunk;
                        if (chunk == chunks) {}
                    }
                    this.write(this.m_inputChars, 0, len_chunk);
                }
                return;
            }
        }
        s.getChars(0, length, this.m_inputChars, 0);
        final char[] chars = this.m_inputChars;
        final int n = length;
        final byte[] buf_loc = this.m_outputBytes;
        int count_loc = this.count;
        int i;
        char c2;
        for (i = 0; i < n && (c2 = chars[i]) < '\u0080'; ++i) {
            buf_loc[count_loc++] = (byte)c2;
        }
        while (i < n) {
            c2 = chars[i];
            if (c2 < '\u0080') {
                buf_loc[count_loc++] = (byte)c2;
            }
            else if (c2 < '\u0800') {
                buf_loc[count_loc++] = (byte)(192 + (c2 >> 6));
                buf_loc[count_loc++] = (byte)(128 + (c2 & '?'));
            }
            else if (c2 >= '\ud800' && c2 <= '\udbff') {
                final char high = c2;
                ++i;
                final char low = chars[i];
                buf_loc[count_loc++] = (byte)(0xF0 | (high + '@' >> 8 & 0xF0));
                buf_loc[count_loc++] = (byte)(0x80 | (high + '@' >> 2 & 0x3F));
                buf_loc[count_loc++] = (byte)(0x80 | (low >> 6 & 0xF) + (high << 4 & 0x30));
                buf_loc[count_loc++] = (byte)(0x80 | (low & '?'));
            }
            else {
                buf_loc[count_loc++] = (byte)(224 + (c2 >> 12));
                buf_loc[count_loc++] = (byte)(128 + (c2 >> 6 & 0x3F));
                buf_loc[count_loc++] = (byte)(128 + (c2 & '?'));
            }
            ++i;
        }
        this.count = count_loc;
    }
    
    public void flushBuffer() throws IOException {
        if (this.count > 0) {
            this.m_os.write(this.m_outputBytes, 0, this.count);
            this.count = 0;
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.flushBuffer();
        this.m_os.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.flushBuffer();
        this.m_os.close();
    }
    
    @Override
    public OutputStream getOutputStream() {
        return this.m_os;
    }
    
    @Override
    public Writer getWriter() {
        return null;
    }
}
