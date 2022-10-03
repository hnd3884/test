package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF8 extends Reader
{
    private InputStream is;
    
    public ReaderUTF8(final InputStream is) {
        this.is = is;
    }
    
    @Override
    public int read(final char[] array, int n, final int n2) throws IOException {
        int i;
        for (i = 0; i < n2; ++i) {
            final int read;
            if ((read = this.is.read()) < 0) {
                return (i != 0) ? i : -1;
            }
            switch (read & 0xF0) {
                case 192:
                case 208: {
                    array[n++] = (char)((read & 0x1F) << 6 | (this.is.read() & 0x3F));
                    break;
                }
                case 224: {
                    array[n++] = (char)((read & 0xF) << 12 | (this.is.read() & 0x3F) << 6 | (this.is.read() & 0x3F));
                    break;
                }
                case 240: {
                    throw new UnsupportedEncodingException("UTF-32 (or UCS-4) encoding not supported.");
                }
                default: {
                    array[n++] = (char)read;
                    break;
                }
            }
        }
        return i;
    }
    
    @Override
    public int read() throws IOException {
        int read;
        if ((read = this.is.read()) < 0) {
            return -1;
        }
        switch (read & 0xF0) {
            case 192:
            case 208: {
                read = ((read & 0x1F) << 6 | (this.is.read() & 0x3F));
                break;
            }
            case 224: {
                read = ((read & 0xF) << 12 | (this.is.read() & 0x3F) << 6 | (this.is.read() & 0x3F));
                break;
            }
            case 240: {
                throw new UnsupportedEncodingException();
            }
        }
        return read;
    }
    
    @Override
    public void close() throws IOException {
        this.is.close();
    }
}
