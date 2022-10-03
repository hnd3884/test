package com.sun.org.apache.xml.internal.serialize;

import java.lang.reflect.Method;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.OutputStream;
import com.sun.org.apache.xerces.internal.util.EncodingMap;

public class EncodingInfo
{
    private Object[] fArgsForMethod;
    String ianaName;
    String javaName;
    int lastPrintable;
    Object fCharsetEncoder;
    Object fCharToByteConverter;
    boolean fHaveTriedCToB;
    boolean fHaveTriedCharsetEncoder;
    
    public EncodingInfo(final String ianaName, final String javaName, final int lastPrintable) {
        this.fArgsForMethod = null;
        this.fCharsetEncoder = null;
        this.fCharToByteConverter = null;
        this.fHaveTriedCToB = false;
        this.fHaveTriedCharsetEncoder = false;
        this.ianaName = ianaName;
        this.javaName = EncodingMap.getIANA2JavaMapping(ianaName);
        this.lastPrintable = lastPrintable;
    }
    
    public String getIANAName() {
        return this.ianaName;
    }
    
    public Writer getWriter(final OutputStream output) throws UnsupportedEncodingException {
        if (this.javaName != null) {
            return new OutputStreamWriter(output, this.javaName);
        }
        this.javaName = EncodingMap.getIANA2JavaMapping(this.ianaName);
        if (this.javaName == null) {
            return new OutputStreamWriter(output, "UTF8");
        }
        return new OutputStreamWriter(output, this.javaName);
    }
    
    public boolean isPrintable(final char ch) {
        return ch <= this.lastPrintable || this.isPrintable0(ch);
    }
    
    private boolean isPrintable0(final char ch) {
        if (this.fCharsetEncoder == null && CharsetMethods.fgNIOCharsetAvailable && !this.fHaveTriedCharsetEncoder) {
            if (this.fArgsForMethod == null) {
                this.fArgsForMethod = new Object[1];
            }
            try {
                this.fArgsForMethod[0] = this.javaName;
                final Object charset = CharsetMethods.fgCharsetForNameMethod.invoke(null, this.fArgsForMethod);
                if (CharsetMethods.fgCharsetCanEncodeMethod.invoke(charset, (Object[])null)) {
                    this.fCharsetEncoder = CharsetMethods.fgCharsetNewEncoderMethod.invoke(charset, (Object[])null);
                }
                else {
                    this.fHaveTriedCharsetEncoder = true;
                }
            }
            catch (final Exception e) {
                this.fHaveTriedCharsetEncoder = true;
            }
        }
        if (this.fCharsetEncoder != null) {
            try {
                this.fArgsForMethod[0] = new Character(ch);
                return (boolean)CharsetMethods.fgCharsetEncoderCanEncodeMethod.invoke(this.fCharsetEncoder, this.fArgsForMethod);
            }
            catch (final Exception e) {
                this.fCharsetEncoder = null;
                this.fHaveTriedCharsetEncoder = false;
            }
        }
        if (this.fCharToByteConverter == null) {
            if (this.fHaveTriedCToB || !CharToByteConverterMethods.fgConvertersAvailable) {
                return false;
            }
            if (this.fArgsForMethod == null) {
                this.fArgsForMethod = new Object[1];
            }
            try {
                this.fArgsForMethod[0] = this.javaName;
                this.fCharToByteConverter = CharToByteConverterMethods.fgGetConverterMethod.invoke(null, this.fArgsForMethod);
            }
            catch (final Exception e) {
                this.fHaveTriedCToB = true;
                return false;
            }
        }
        try {
            this.fArgsForMethod[0] = new Character(ch);
            return (boolean)CharToByteConverterMethods.fgCanConvertMethod.invoke(this.fCharToByteConverter, this.fArgsForMethod);
        }
        catch (final Exception e) {
            this.fCharToByteConverter = null;
            return this.fHaveTriedCToB = false;
        }
    }
    
    public static void testJavaEncodingName(final String name) throws UnsupportedEncodingException {
        final byte[] bTest = { 118, 97, 108, 105, 100 };
        final String s = new String(bTest, name);
    }
    
    static class CharsetMethods
    {
        private static Method fgCharsetForNameMethod;
        private static Method fgCharsetCanEncodeMethod;
        private static Method fgCharsetNewEncoderMethod;
        private static Method fgCharsetEncoderCanEncodeMethod;
        private static boolean fgNIOCharsetAvailable;
        
        private CharsetMethods() {
        }
        
        static {
            CharsetMethods.fgCharsetForNameMethod = null;
            CharsetMethods.fgCharsetCanEncodeMethod = null;
            CharsetMethods.fgCharsetNewEncoderMethod = null;
            CharsetMethods.fgCharsetEncoderCanEncodeMethod = null;
            CharsetMethods.fgNIOCharsetAvailable = false;
            try {
                final Class charsetClass = Class.forName("java.nio.charset.Charset");
                final Class charsetEncoderClass = Class.forName("java.nio.charset.CharsetEncoder");
                CharsetMethods.fgCharsetForNameMethod = charsetClass.getMethod("forName", String.class);
                CharsetMethods.fgCharsetCanEncodeMethod = charsetClass.getMethod("canEncode", (Class[])new Class[0]);
                CharsetMethods.fgCharsetNewEncoderMethod = charsetClass.getMethod("newEncoder", (Class[])new Class[0]);
                CharsetMethods.fgCharsetEncoderCanEncodeMethod = charsetEncoderClass.getMethod("canEncode", Character.TYPE);
                CharsetMethods.fgNIOCharsetAvailable = true;
            }
            catch (final Exception exc) {
                CharsetMethods.fgCharsetForNameMethod = null;
                CharsetMethods.fgCharsetCanEncodeMethod = null;
                CharsetMethods.fgCharsetEncoderCanEncodeMethod = null;
                CharsetMethods.fgCharsetNewEncoderMethod = null;
                CharsetMethods.fgNIOCharsetAvailable = false;
            }
        }
    }
    
    static class CharToByteConverterMethods
    {
        private static Method fgGetConverterMethod;
        private static Method fgCanConvertMethod;
        private static boolean fgConvertersAvailable;
        
        private CharToByteConverterMethods() {
        }
        
        static {
            CharToByteConverterMethods.fgGetConverterMethod = null;
            CharToByteConverterMethods.fgCanConvertMethod = null;
            CharToByteConverterMethods.fgConvertersAvailable = false;
            try {
                final Class clazz = Class.forName("sun.io.CharToByteConverter");
                CharToByteConverterMethods.fgGetConverterMethod = clazz.getMethod("getConverter", String.class);
                CharToByteConverterMethods.fgCanConvertMethod = clazz.getMethod("canConvert", Character.TYPE);
                CharToByteConverterMethods.fgConvertersAvailable = true;
            }
            catch (final Exception exc) {
                CharToByteConverterMethods.fgGetConverterMethod = null;
                CharToByteConverterMethods.fgCanConvertMethod = null;
                CharToByteConverterMethods.fgConvertersAvailable = false;
            }
        }
    }
}
