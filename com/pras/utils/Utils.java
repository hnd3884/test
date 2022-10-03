package com.pras.utils;

import java.io.ByteArrayInputStream;

public class Utils
{
    private static final String[] hexStr;
    public static boolean isProduction;
    
    static {
        hexStr = new String[] { "A", "B", "C", "D", "E", "F" };
        Utils.isProduction = false;
    }
    
    public static String toHex(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String hex = "";
        for (int i = 0; i < bytes.length; ++i) {
            final int num = 0xFF & bytes[i];
            int div = num / 16;
            int rem = num % 16;
            if (div > 9) {
                div -= 10;
                hex = String.valueOf(hex) + " 0x" + Utils.hexStr[div];
            }
            else {
                hex = String.valueOf(hex) + " 0x" + div;
            }
            if (rem > 9) {
                rem -= 10;
                hex = String.valueOf(hex) + Utils.hexStr[rem];
            }
            else {
                hex = String.valueOf(hex) + rem;
            }
        }
        return hex;
    }
    
    public static int toInt(final byte[] bytes, final boolean isBigEndian) {
        int x = 0;
        int numOfBytes = bytes.length;
        if (numOfBytes > 4) {
            numOfBytes = 4;
        }
        for (int i = 0; i < numOfBytes; ++i) {
            if (i == 0) {
                if (isBigEndian) {
                    x = (0xFF & bytes[i]);
                }
                else {
                    x = (0xFF & bytes[numOfBytes - 1]);
                }
            }
            else if (isBigEndian) {
                x = (x << 8 | (0xFF & bytes[i]));
            }
            else {
                x = (x << 8 | (0xFF & bytes[numOfBytes - 1 - i]));
            }
        }
        return x;
    }
    
    public static long toLong(final byte[] bytes, final boolean isBigEndian) {
        long x = 0L;
        int numOfBytes = bytes.length;
        if (numOfBytes > 8) {
            numOfBytes = 8;
        }
        for (int i = 0; i < numOfBytes; ++i) {
            if (i == 0) {
                if (isBigEndian) {
                    x = (0xFF & bytes[i]);
                }
                else {
                    x = (0xFF & bytes[numOfBytes - 1]);
                }
            }
            else if (isBigEndian) {
                x = (x << 8 | (long)(0xFF & bytes[i]));
            }
            else {
                x = (x << 8 | (long)(0xFF & bytes[numOfBytes - 1 - i]));
            }
        }
        return x;
    }
    
    public static String toString(final byte[] charBuf, final boolean isBigEndian) throws Exception {
        final StringBuffer strBuf = new StringBuffer();
        final byte[] buf_2 = new byte[2];
        final ByteArrayInputStream in = new ByteArrayInputStream(charBuf);
        while (in.read(buf_2) != -1) {
            final int code = toInt(buf_2, isBigEndian);
            if (code == 0) {
                break;
            }
            strBuf.append((char)code);
        }
        return strBuf.toString();
    }
}
