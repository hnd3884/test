package org.apache.poi.util;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

public class HexRead
{
    public static byte[] readData(final String filename) throws IOException {
        final File file = new File(filename);
        try (final InputStream stream = new FileInputStream(file)) {
            return readData(stream, -1);
        }
    }
    
    public static byte[] readData(final InputStream stream, final String section) throws IOException {
        try {
            StringBuilder sectionText = new StringBuilder();
            boolean inSection = false;
            for (int c = stream.read(); c != -1; c = stream.read()) {
                switch (c) {
                    case 91: {
                        inSection = true;
                        break;
                    }
                    case 10:
                    case 13: {
                        inSection = false;
                        sectionText = new StringBuilder();
                        break;
                    }
                    case 93: {
                        inSection = false;
                        if (sectionText.toString().equals(section)) {
                            return readData(stream, 91);
                        }
                        sectionText = new StringBuilder();
                        break;
                    }
                    default: {
                        if (inSection) {
                            sectionText.append((char)c);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        finally {
            stream.close();
        }
        throw new IOException("Section '" + section + "' not found");
    }
    
    public static byte[] readData(final String filename, final String section) throws IOException {
        return readData(new FileInputStream(filename), section);
    }
    
    public static byte[] readData(final InputStream stream, final int eofChar) throws IOException {
        int characterCount = 0;
        byte b = 0;
        final List<Byte> bytes = new ArrayList<Byte>();
        final char a = 'W';
        final char A = '7';
        while (true) {
            final int count = stream.read();
            int digitValue = -1;
            if (48 <= count && count <= 57) {
                digitValue = count - 48;
            }
            else if (65 <= count && count <= 70) {
                digitValue = count - 55;
            }
            else if (97 <= count && count <= 102) {
                digitValue = count - 87;
            }
            else if (35 == count) {
                readToEOL(stream);
            }
            else if (-1 == count || eofChar == count) {
                break;
            }
            if (digitValue != -1) {
                b <<= 4;
                b += (byte)digitValue;
                if (++characterCount != 2) {
                    continue;
                }
                bytes.add(b);
                characterCount = 0;
                b = 0;
            }
        }
        final Byte[] polished = bytes.toArray(new Byte[0]);
        final byte[] rval = new byte[polished.length];
        for (int j = 0; j < polished.length; ++j) {
            rval[j] = polished[j];
        }
        return rval;
    }
    
    public static byte[] readFromString(final String data) {
        try {
            return readData(new ByteArrayInputStream(data.getBytes(StringUtil.UTF8)), -1);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void readToEOL(final InputStream stream) throws IOException {
        for (int c = stream.read(); c != -1 && c != 10 && c != 13; c = stream.read()) {}
    }
}
