package eu.medsea.util;

import java.util.Collection;
import java.util.Vector;
import java.io.UnsupportedEncodingException;

public class StringUtil
{
    static final byte[] HEX_CHAR_TABLE;
    
    static {
        HEX_CHAR_TABLE = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
    }
    
    public static String getHexString(final byte[] raw) throws UnsupportedEncodingException {
        if (raw == null) {
            return "";
        }
        final byte[] hex = new byte[2 * raw.length];
        int index = 0;
        for (int i = 0; i < raw.length; ++i) {
            final byte b = raw[i];
            final int v = b & 0xFF;
            hex[index++] = StringUtil.HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = StringUtil.HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }
    
    public static boolean contains(final String target, final String content) {
        return target.indexOf(content) != -1;
    }
    
    public static String toStringArrayToString(final String[] array) {
        final Collection c = new Vector();
        for (int i = 0; i < array.length; ++i) {
            c.add(array[i]);
        }
        return c.toString();
    }
}
