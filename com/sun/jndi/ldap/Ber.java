package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import sun.misc.HexDumpEncoder;
import java.io.OutputStream;

public abstract class Ber
{
    protected byte[] buf;
    protected int offset;
    protected int bufsize;
    public static final int ASN_BOOLEAN = 1;
    public static final int ASN_INTEGER = 2;
    public static final int ASN_BIT_STRING = 3;
    public static final int ASN_SIMPLE_STRING = 4;
    public static final int ASN_OCTET_STR = 4;
    public static final int ASN_NULL = 5;
    public static final int ASN_OBJECT_ID = 6;
    public static final int ASN_SEQUENCE = 16;
    public static final int ASN_SET = 17;
    public static final int ASN_PRIMITIVE = 0;
    public static final int ASN_UNIVERSAL = 0;
    public static final int ASN_CONSTRUCTOR = 32;
    public static final int ASN_APPLICATION = 64;
    public static final int ASN_CONTEXT = 128;
    public static final int ASN_PRIVATE = 192;
    public static final int ASN_ENUMERATED = 10;
    
    protected Ber() {
    }
    
    public static void dumpBER(final OutputStream outputStream, final String s, final byte[] array, final int n, final int n2) {
        try {
            outputStream.write(10);
            outputStream.write(s.getBytes("UTF8"));
            new HexDumpEncoder().encodeBuffer(new ByteArrayInputStream(array, n, n2), outputStream);
            outputStream.write(10);
        }
        catch (final IOException ex) {
            try {
                outputStream.write("Ber.dumpBER(): error encountered\n".getBytes("UTF8"));
            }
            catch (final IOException ex2) {}
        }
    }
    
    static final class EncodeException extends IOException
    {
        private static final long serialVersionUID = -5247359637775781768L;
        
        EncodeException(final String s) {
            super(s);
        }
    }
    
    static final class DecodeException extends IOException
    {
        private static final long serialVersionUID = 8735036969244425583L;
        
        DecodeException(final String s) {
            super(s);
        }
    }
}
