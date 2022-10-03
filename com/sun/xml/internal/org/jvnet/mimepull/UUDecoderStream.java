package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

final class UUDecoderStream extends FilterInputStream
{
    private String name;
    private int mode;
    private byte[] buffer;
    private int bufsize;
    private int index;
    private boolean gotPrefix;
    private boolean gotEnd;
    private LineInputStream lin;
    private boolean ignoreErrors;
    private boolean ignoreMissingBeginEnd;
    private String readAhead;
    
    public UUDecoderStream(final InputStream in) {
        super(in);
        this.buffer = new byte[45];
        this.bufsize = 0;
        this.index = 0;
        this.gotPrefix = false;
        this.gotEnd = false;
        this.lin = new LineInputStream(in);
        this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
        this.ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
    }
    
    public UUDecoderStream(final InputStream in, final boolean ignoreErrors, final boolean ignoreMissingBeginEnd) {
        super(in);
        this.buffer = new byte[45];
        this.bufsize = 0;
        this.index = 0;
        this.gotPrefix = false;
        this.gotEnd = false;
        this.lin = new LineInputStream(in);
        this.ignoreErrors = ignoreErrors;
        this.ignoreMissingBeginEnd = ignoreMissingBeginEnd;
    }
    
    @Override
    public int read() throws IOException {
        if (this.index >= this.bufsize) {
            this.readPrefix();
            if (!this.decode()) {
                return -1;
            }
            this.index = 0;
        }
        return this.buffer[this.index++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        int i = 0;
        while (i < len) {
            final int c;
            if ((c = this.read()) == -1) {
                if (i == 0) {
                    i = -1;
                    break;
                }
                break;
            }
            else {
                buf[off + i] = (byte)c;
                ++i;
            }
        }
        return i;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available() * 3 / 4 + (this.bufsize - this.index);
    }
    
    public String getName() throws IOException {
        this.readPrefix();
        return this.name;
    }
    
    public int getMode() throws IOException {
        this.readPrefix();
        return this.mode;
    }
    
    private void readPrefix() throws IOException {
        if (this.gotPrefix) {
            return;
        }
        this.mode = 438;
        this.name = "encoder.buf";
        while (true) {
            final String line = this.lin.readLine();
            if (line == null) {
                if (!this.ignoreMissingBeginEnd) {
                    throw new DecodingException("UUDecoder: Missing begin");
                }
                this.gotPrefix = true;
                this.gotEnd = true;
                break;
            }
            else {
                if (line.regionMatches(false, 0, "begin", 0, 5)) {
                    try {
                        this.mode = Integer.parseInt(line.substring(6, 9));
                    }
                    catch (final NumberFormatException ex) {
                        if (!this.ignoreErrors) {
                            throw new DecodingException("UUDecoder: Error in mode: " + ex.toString());
                        }
                    }
                    if (line.length() > 10) {
                        this.name = line.substring(10);
                    }
                    else if (!this.ignoreErrors) {
                        throw new DecodingException("UUDecoder: Missing name: " + line);
                    }
                    this.gotPrefix = true;
                    break;
                }
                if (!this.ignoreMissingBeginEnd || line.length() == 0) {
                    continue;
                }
                int count = line.charAt(0);
                count = (count - 32 & 0x3F);
                final int need = (count * 8 + 5) / 6;
                if (need == 0 || line.length() >= need + 1) {
                    this.readAhead = line;
                    this.gotPrefix = true;
                    break;
                }
                continue;
            }
        }
    }
    
    private boolean decode() throws IOException {
        if (this.gotEnd) {
            return false;
        }
        this.bufsize = 0;
        int count = 0;
        while (true) {
            String line;
            if (this.readAhead != null) {
                line = this.readAhead;
                this.readAhead = null;
            }
            else {
                line = this.lin.readLine();
            }
            if (line == null) {
                if (!this.ignoreMissingBeginEnd) {
                    throw new DecodingException("UUDecoder: Missing end at EOF");
                }
                this.gotEnd = true;
                return false;
            }
            else {
                if (line.equals("end")) {
                    this.gotEnd = true;
                    return false;
                }
                if (line.length() == 0) {
                    continue;
                }
                count = line.charAt(0);
                if (count < 32) {
                    if (!this.ignoreErrors) {
                        throw new DecodingException("UUDecoder: Buffer format error");
                    }
                    continue;
                }
                else {
                    count = (count - 32 & 0x3F);
                    if (count == 0) {
                        line = this.lin.readLine();
                        if ((line == null || !line.equals("end")) && !this.ignoreMissingBeginEnd) {
                            throw new DecodingException("UUDecoder: Missing End after count 0 line");
                        }
                        this.gotEnd = true;
                        return false;
                    }
                    else {
                        final int need = (count * 8 + 5) / 6;
                        if (line.length() >= need + 1) {
                            int i = 1;
                            while (this.bufsize < count) {
                                byte a = (byte)(line.charAt(i++) - ' ' & 0x3F);
                                byte b = (byte)(line.charAt(i++) - ' ' & 0x3F);
                                this.buffer[this.bufsize++] = (byte)((a << 2 & 0xFC) | (b >>> 4 & 0x3));
                                if (this.bufsize < count) {
                                    a = b;
                                    b = (byte)(line.charAt(i++) - ' ' & 0x3F);
                                    this.buffer[this.bufsize++] = (byte)((a << 4 & 0xF0) | (b >>> 2 & 0xF));
                                }
                                if (this.bufsize < count) {
                                    a = b;
                                    b = (byte)(line.charAt(i++) - ' ' & 0x3F);
                                    this.buffer[this.bufsize++] = (byte)((a << 6 & 0xC0) | (b & 0x3F));
                                }
                            }
                            return true;
                        }
                        if (!this.ignoreErrors) {
                            throw new DecodingException("UUDecoder: Short buffer error");
                        }
                        continue;
                    }
                }
            }
        }
    }
}
