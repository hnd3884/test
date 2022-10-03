package com.lowagie.text.pdf;

import java.util.HashMap;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;

public final class PdfEncryptor
{
    private PdfEncryptor() {
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final byte[] userPassword, final byte[] ownerPassword, final int permissions, final boolean strength128Bits) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.close();
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final byte[] userPassword, final byte[] ownerPassword, final int permissions, final boolean strength128Bits, final HashMap newInfo) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.setMoreInfo(newInfo);
        stamper.close();
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final boolean strength, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(strength, userPassword, ownerPassword, permissions);
        stamper.close();
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final boolean strength, final String userPassword, final String ownerPassword, final int permissions, final HashMap newInfo) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(strength, userPassword, ownerPassword, permissions);
        stamper.setMoreInfo(newInfo);
        stamper.close();
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final int type, final String userPassword, final String ownerPassword, final int permissions, final HashMap newInfo) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(type, userPassword, ownerPassword, permissions);
        stamper.setMoreInfo(newInfo);
        stamper.close();
    }
    
    public static void encrypt(final PdfReader reader, final OutputStream os, final int type, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException, IOException {
        final PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(type, userPassword, ownerPassword, permissions);
        stamper.close();
    }
    
    public static String getPermissionsVerbose(final int permissions) {
        final StringBuffer buf = new StringBuffer("Allowed:");
        if ((0x804 & permissions) == 0x804) {
            buf.append(" Printing");
        }
        if ((0x8 & permissions) == 0x8) {
            buf.append(" Modify contents");
        }
        if ((0x10 & permissions) == 0x10) {
            buf.append(" Copy");
        }
        if ((0x20 & permissions) == 0x20) {
            buf.append(" Modify annotations");
        }
        if ((0x100 & permissions) == 0x100) {
            buf.append(" Fill in");
        }
        if ((0x200 & permissions) == 0x200) {
            buf.append(" Screen readers");
        }
        if ((0x400 & permissions) == 0x400) {
            buf.append(" Assembly");
        }
        if ((0x4 & permissions) == 0x4) {
            buf.append(" Degraded printing");
        }
        return buf.toString();
    }
    
    public static boolean isPrintingAllowed(final int permissions) {
        return (0x804 & permissions) == 0x804;
    }
    
    public static boolean isModifyContentsAllowed(final int permissions) {
        return (0x8 & permissions) == 0x8;
    }
    
    public static boolean isCopyAllowed(final int permissions) {
        return (0x10 & permissions) == 0x10;
    }
    
    public static boolean isModifyAnnotationsAllowed(final int permissions) {
        return (0x20 & permissions) == 0x20;
    }
    
    public static boolean isFillInAllowed(final int permissions) {
        return (0x100 & permissions) == 0x100;
    }
    
    public static boolean isScreenReadersAllowed(final int permissions) {
        return (0x200 & permissions) == 0x200;
    }
    
    public static boolean isAssemblyAllowed(final int permissions) {
        return (0x400 & permissions) == 0x400;
    }
    
    public static boolean isDegradedPrintingAllowed(final int permissions) {
        return (0x4 & permissions) == 0x4;
    }
}
