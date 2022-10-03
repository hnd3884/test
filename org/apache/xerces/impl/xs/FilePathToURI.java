package org.apache.xerces.impl.xs;

import java.io.UnsupportedEncodingException;
import java.io.File;

final class FilePathToURI
{
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;
    
    private FilePathToURI() {
    }
    
    public static String filepath2URI(String replace) {
        if (replace == null) {
            return null;
        }
        replace = replace.replace(File.separatorChar, '/');
        final int length = replace.length();
        final StringBuffer sb = new StringBuffer(length * 3);
        sb.append("file://");
        if (length >= 2 && replace.charAt(1) == ':') {
            final char upperCase = Character.toUpperCase(replace.charAt(0));
            if (upperCase >= 'A' && upperCase <= 'Z') {
                sb.append('/');
            }
        }
        int i;
        for (i = 0; i < length; ++i) {
            final char char1 = replace.charAt(i);
            if (char1 >= '\u0080') {
                break;
            }
            if (FilePathToURI.gNeedEscaping[char1]) {
                sb.append('%');
                sb.append(FilePathToURI.gAfterEscaping1[char1]);
                sb.append(FilePathToURI.gAfterEscaping2[char1]);
            }
            else {
                sb.append(char1);
            }
        }
        if (i < length) {
            byte[] bytes;
            try {
                bytes = replace.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException ex) {
                return replace;
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int n = b + 256;
                    sb.append('%');
                    sb.append(FilePathToURI.gHexChs[n >> 4]);
                    sb.append(FilePathToURI.gHexChs[n & 0xF]);
                }
                else if (FilePathToURI.gNeedEscaping[b]) {
                    sb.append('%');
                    sb.append(FilePathToURI.gAfterEscaping1[b]);
                    sb.append(FilePathToURI.gAfterEscaping2[b]);
                }
                else {
                    sb.append((char)b);
                }
            }
        }
        return sb.toString();
    }
    
    static {
        FilePathToURI.gNeedEscaping = new boolean[128];
        FilePathToURI.gAfterEscaping1 = new char[128];
        FilePathToURI.gAfterEscaping2 = new char[128];
        FilePathToURI.gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (int i = 0; i <= 31; ++i) {
            FilePathToURI.gNeedEscaping[i] = true;
            FilePathToURI.gAfterEscaping1[i] = FilePathToURI.gHexChs[i >> 4];
            FilePathToURI.gAfterEscaping2[i] = FilePathToURI.gHexChs[i & 0xF];
        }
        FilePathToURI.gNeedEscaping[127] = true;
        FilePathToURI.gAfterEscaping1[127] = '7';
        FilePathToURI.gAfterEscaping2[127] = 'F';
        for (final char c : new char[] { ' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' }) {
            FilePathToURI.gNeedEscaping[c] = true;
            FilePathToURI.gAfterEscaping1[c] = FilePathToURI.gHexChs[c >> 4];
            FilePathToURI.gAfterEscaping2[c] = FilePathToURI.gHexChs[c & '\u000f'];
        }
    }
}
