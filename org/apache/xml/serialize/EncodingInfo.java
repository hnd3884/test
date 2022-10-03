package org.apache.xml.serialize;

import java.lang.reflect.Method;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.OutputStream;
import org.apache.xerces.util.EncodingMap;

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
    
    public EncodingInfo(final String ianaName, final String s, final int lastPrintable) {
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
    
    public Writer getWriter(final OutputStream outputStream) throws UnsupportedEncodingException {
        if (this.javaName != null) {
            return new OutputStreamWriter(outputStream, this.javaName);
        }
        this.javaName = EncodingMap.getIANA2JavaMapping(this.ianaName);
        if (this.javaName == null) {
            return new OutputStreamWriter(outputStream, "UTF8");
        }
        return new OutputStreamWriter(outputStream, this.javaName);
    }
    
    public boolean isPrintable(final char c) {
        return c <= this.lastPrintable || this.isPrintable0(c);
    }
    
    private boolean isPrintable0(final char c) {
        if (this.fCharsetEncoder == null && CharsetMethods.fgNIOCharsetAvailable && !this.fHaveTriedCharsetEncoder) {
            if (this.fArgsForMethod == null) {
                this.fArgsForMethod = new Object[1];
            }
            try {
                this.fArgsForMethod[0] = this.javaName;
                final Object invoke = CharsetMethods.fgCharsetForNameMethod.invoke(null, this.fArgsForMethod);
                if (CharsetMethods.fgCharsetCanEncodeMethod.invoke(invoke, (Object[])null)) {
                    this.fCharsetEncoder = CharsetMethods.fgCharsetNewEncoderMethod.invoke(invoke, (Object[])null);
                }
                else {
                    this.fHaveTriedCharsetEncoder = true;
                }
            }
            catch (final Exception ex) {
                this.fHaveTriedCharsetEncoder = true;
            }
        }
        if (this.fCharsetEncoder != null) {
            try {
                this.fArgsForMethod[0] = new Character(c);
                return (boolean)CharsetMethods.fgCharsetEncoderCanEncodeMethod.invoke(this.fCharsetEncoder, this.fArgsForMethod);
            }
            catch (final Exception ex2) {
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
            catch (final Exception ex3) {
                this.fHaveTriedCToB = true;
                return false;
            }
        }
        try {
            this.fArgsForMethod[0] = new Character(c);
            return (boolean)CharToByteConverterMethods.fgCanConvertMethod.invoke(this.fCharToByteConverter, this.fArgsForMethod);
        }
        catch (final Exception ex4) {
            this.fCharToByteConverter = null;
            return this.fHaveTriedCToB = false;
        }
    }
    
    public static void testJavaEncodingName(final String s) throws UnsupportedEncodingException {
        final String s2 = new String(new byte[] { 118, 97, 108, 105, 100 }, s);
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
                final Class<?> forName = Class.forName("sun.io.CharToByteConverter");
                CharToByteConverterMethods.fgGetConverterMethod = forName.getMethod("getConverter", String.class);
                CharToByteConverterMethods.fgCanConvertMethod = forName.getMethod("canConvert", Character.TYPE);
                CharToByteConverterMethods.fgConvertersAvailable = true;
            }
            catch (final Exception ex) {
                CharToByteConverterMethods.fgGetConverterMethod = null;
                CharToByteConverterMethods.fgCanConvertMethod = null;
                CharToByteConverterMethods.fgConvertersAvailable = false;
            }
        }
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
                final Class<?> forName = Class.forName("java.nio.charset.Charset");
                final Class<?> forName2 = Class.forName("java.nio.charset.CharsetEncoder");
                CharsetMethods.fgCharsetForNameMethod = forName.getMethod("forName", String.class);
                CharsetMethods.fgCharsetCanEncodeMethod = forName.getMethod("canEncode", (Class[])new Class[0]);
                CharsetMethods.fgCharsetNewEncoderMethod = forName.getMethod("newEncoder", (Class[])new Class[0]);
                CharsetMethods.fgCharsetEncoderCanEncodeMethod = forName2.getMethod("canEncode", Character.TYPE);
                CharsetMethods.fgNIOCharsetAvailable = true;
            }
            catch (final Exception ex) {
                CharsetMethods.fgCharsetForNameMethod = null;
                CharsetMethods.fgCharsetCanEncodeMethod = null;
                CharsetMethods.fgCharsetEncoderCanEncodeMethod = null;
                CharsetMethods.fgCharsetNewEncoderMethod = null;
                CharsetMethods.fgNIOCharsetAvailable = false;
            }
        }
    }
}
