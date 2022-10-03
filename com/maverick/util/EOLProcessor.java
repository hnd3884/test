package com.maverick.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

public class EOLProcessor
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
    String i;
    boolean c;
    boolean g;
    boolean h;
    boolean f;
    boolean b;
    OutputStream e;
    
    public EOLProcessor(final int n, final int n2, final OutputStream outputStream) throws IOException {
        this.i = System.getProperty("line.separator");
        this.c = false;
        this.g = false;
        this.h = false;
        this.f = false;
        this.b = false;
        this.e = new BufferedOutputStream(outputStream);
        switch (n) {
            case 1: {
                this.h = true;
                break;
            }
            case 3: {
                this.c = true;
                break;
            }
            case 2: {
                this.g = true;
                break;
            }
            case 4: {
                this.c = true;
                this.g = true;
                this.h = true;
                break;
            }
            case 0: {
                final byte[] bytes = this.i.getBytes();
                if (bytes.length == 2 && bytes[0] == 13 && bytes[1] == 10) {
                    this.h = true;
                    break;
                }
                if (bytes.length == 1 && bytes[0] == 13) {
                    this.c = true;
                    break;
                }
                if (bytes.length == 1 && bytes[0] == 10) {
                    this.g = true;
                    break;
                }
                throw new IOException("Unsupported system EOL mode");
            }
            default: {
                throw new IllegalArgumentException("Unknown text style: " + n2);
            }
        }
        switch (n2) {
            case 0: {
                this.d = this.i.getBytes();
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
        return this.f;
    }
    
    public void close() throws IOException {
        if (this.b && !this.c) {
            this.e.write(13);
        }
        this.e.close();
    }
    
    public void processBytes(final byte[] array, final int n, final int n2) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(array, n, n2), 32768);
        int read;
        while ((read = bufferedInputStream.read()) != -1) {
            if (read == 13) {
                if (this.h) {
                    bufferedInputStream.mark(1);
                    final int read2 = bufferedInputStream.read();
                    if (read2 == -1) {
                        this.b = true;
                        break;
                    }
                    if (read2 == 10) {
                        this.e.write(this.d);
                    }
                    else {
                        bufferedInputStream.reset();
                        if (this.c) {
                            this.e.write(this.d);
                        }
                        else {
                            this.e.write(read);
                        }
                    }
                }
                else if (this.c) {
                    this.e.write(this.d);
                }
                else {
                    this.e.write(read);
                }
            }
            else if (read == 10) {
                if (this.b) {
                    this.e.write(this.d);
                    this.b = false;
                }
                else if (this.g) {
                    this.e.write(this.d);
                }
                else {
                    this.e.write(read);
                }
            }
            else {
                if (this.b) {
                    if (this.c) {
                        this.e.write(this.d);
                    }
                    else {
                        this.e.write(read);
                    }
                }
                if (read != 116 && read != 12 && (read & 0xFF) < 32) {
                    this.f = true;
                }
                this.e.write(read);
            }
        }
        this.e.flush();
    }
    
    public static OutputStream createOutputStream(final int n, final int n2, final OutputStream outputStream) throws IOException {
        return new b(n, n2, outputStream);
    }
    
    public static InputStream createInputStream(final int n, final int n2, final InputStream inputStream) throws IOException {
        return new c(n, n2, inputStream);
    }
}
