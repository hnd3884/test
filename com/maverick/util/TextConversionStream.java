package com.maverick.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class TextConversionStream extends FilterOutputStream
{
    public static final int TEXT_SYSTEM = 0;
    public static final int TEXT_WINDOWS = 1;
    public static final int TEXT_DOS = 1;
    public static final int TEXT_CRLF = 1;
    public static final int TEXT_UNIX = 2;
    public static final int TEXT_LF = 2;
    public static final int TEXT_MAC = 3;
    public static final int TEXT_CR = 3;
    public static final int TEXT_ALL = 4;
    byte[] d;
    String h;
    boolean c;
    boolean f;
    boolean g;
    boolean e;
    boolean b;
    
    public TextConversionStream(final int n, final int n2, final OutputStream outputStream) {
        super(outputStream);
        this.h = System.getProperty("line.separator");
        this.e = false;
        this.b = false;
        switch (n) {
            case 1: {
                this.c = false;
                this.f = false;
                this.g = true;
                break;
            }
            case 3: {
                this.c = true;
                this.f = false;
                this.g = false;
                break;
            }
            case 2: {
                this.c = false;
                this.f = true;
                this.g = false;
                break;
            }
            case 4: {
                this.c = true;
                this.f = true;
                this.g = true;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown text style: " + n2);
            }
        }
        switch (n2) {
            case 0: {
                this.d = this.h.getBytes();
                break;
            }
            case 1: {
                this.d = new byte[] { 13, 10 };
                break;
            }
            case 3: {
                this.d = new byte[] { 13 };
                break;
            }
            case 2: {
                this.d = new byte[] { 10 };
                break;
            }
            case 4: {
                throw new IllegalArgumentException("TEXT_ALL cannot be used for an output style");
            }
            default: {
                throw new IllegalArgumentException("Unknown text style: " + n2);
            }
        }
    }
    
    public boolean hasBinary() {
        return this.e;
    }
    
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n });
    }
    
    public void close() throws IOException {
        if (this.b && !this.c) {
            super.out.write(13);
        }
        super.close();
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(array, n, n2), 32768);
        int read;
        while ((read = bufferedInputStream.read()) != -1) {
            if (read == 13) {
                if (this.g) {
                    bufferedInputStream.mark(1);
                    final int read2 = bufferedInputStream.read();
                    if (read2 == -1) {
                        this.b = true;
                        break;
                    }
                    if (read2 == 10) {
                        super.out.write(this.d);
                    }
                    else {
                        bufferedInputStream.reset();
                        if (this.c) {
                            super.out.write(this.d);
                        }
                        else {
                            super.out.write(read);
                        }
                    }
                }
                else if (this.c) {
                    super.out.write(this.d);
                }
                else {
                    super.out.write(read);
                }
            }
            else if (read == 10) {
                if (this.b) {
                    super.out.write(this.d);
                    this.b = false;
                }
                else if (this.f) {
                    super.out.write(this.d);
                }
                else {
                    super.out.write(read);
                }
            }
            else {
                if (this.b) {
                    if (this.c) {
                        super.out.write(this.d);
                    }
                    else {
                        super.out.write(read);
                    }
                }
                if (read != 116 && read != 12 && (read & 0xFF) < 32) {
                    this.e = true;
                }
                super.out.write(read);
            }
        }
    }
    
    public static void main(final String[] array) {
        try {
            final TextConversionStream textConversionStream = new TextConversionStream(1, 3, new FileOutputStream("C:\\TEXT.txt"));
            textConversionStream.write("1234567890\r".getBytes());
            textConversionStream.write("\n01234567890\r\n".getBytes());
            textConversionStream.write("\r\n12323445546657".getBytes());
            textConversionStream.write("21344356545656\r".getBytes());
            textConversionStream.close();
        }
        catch (final Exception ex) {
            System.out.println("RECIEVED IOException IN Ssh1Protocol.close:" + ex.getMessage());
        }
    }
}
