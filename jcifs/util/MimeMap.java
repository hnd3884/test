package jcifs.util;

import java.io.InputStream;
import java.io.IOException;

public class MimeMap
{
    private static final int IN_SIZE = 7000;
    private static final int ST_START = 1;
    private static final int ST_COMM = 2;
    private static final int ST_TYPE = 3;
    private static final int ST_GAP = 4;
    private static final int ST_EXT = 5;
    private byte[] in;
    private int inLen;
    
    public MimeMap() throws IOException {
        this.in = new byte[7000];
        final InputStream is = this.getClass().getClassLoader().getResourceAsStream("jcifs/util/mime.map");
        this.inLen = 0;
        int n;
        while ((n = is.read(this.in, this.inLen, 7000 - this.inLen)) != -1) {
            this.inLen += n;
        }
        if (this.inLen < 100 || this.inLen == 7000) {
            throw new IOException("Error reading jcifs/util/mime.map resource");
        }
        is.close();
    }
    
    public String getMimeType(final String extension) throws IOException {
        return this.getMimeType(extension, "application/octet-stream");
    }
    
    public String getMimeType(final String extension, final String def) throws IOException {
        final byte[] type = new byte[128];
        final byte[] buf = new byte[16];
        final byte[] ext = extension.toLowerCase().getBytes("ASCII");
        int state = 1;
        int i;
        int t;
        int x = t = (i = 0);
        for (int off = 0; off < this.inLen; ++off) {
            final byte ch = this.in[off];
            switch (state) {
                case 1: {
                    if (ch == 32) {
                        break;
                    }
                    if (ch == 9) {
                        break;
                    }
                    if (ch == 35) {
                        state = 2;
                        break;
                    }
                    state = 3;
                }
                case 3: {
                    if (ch == 32 || ch == 9) {
                        state = 4;
                        break;
                    }
                    type[t++] = ch;
                    break;
                }
                case 2: {
                    if (ch == 10) {
                        x = (t = (i = 0));
                        state = 1;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (ch == 32) {
                        break;
                    }
                    if (ch == 9) {
                        break;
                    }
                    state = 5;
                }
                case 5: {
                    switch (ch) {
                        case 9:
                        case 10:
                        case 32:
                        case 35: {
                            for (i = 0; i < x && x == ext.length && buf[i] == ext[i]; ++i) {}
                            if (i == ext.length) {
                                return new String(type, 0, t, "ASCII");
                            }
                            if (ch == 35) {
                                state = 2;
                            }
                            else if (ch == 10) {
                                x = (t = (i = 0));
                                state = 1;
                            }
                            x = 0;
                            continue;
                        }
                        default: {
                            buf[x++] = ch;
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        return def;
    }
}
