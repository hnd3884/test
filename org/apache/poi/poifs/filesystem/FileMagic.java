package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.common.POIFSConstants;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import java.io.FileInputStream;
import java.io.File;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.LittleEndian;

public enum FileMagic
{
    OLE2(-2226271756974174256L), 
    OOXML(new byte[][] { POIFSConstants.OOXML_FILE_HEADER }), 
    XML(new byte[][] { POIFSConstants.RAW_XML_FILE_HEADER }), 
    BIFF2(new byte[][] { { 9, 0, 4, 0, 0, 0, 63, 0 } }), 
    BIFF3(new byte[][] { { 9, 2, 6, 0, 0, 0, 63, 0 } }), 
    BIFF4(new byte[][] { { 9, 4, 6, 0, 0, 0, 63, 0 }, { 9, 4, 6, 0, 0, 0, 0, 1 } }), 
    MSWRITE(new byte[][] { { 49, -66, 0, 0 }, { 50, -66, 0, 0 } }), 
    RTF(new String[] { "{\\rtf" }), 
    PDF(new String[] { "%PDF" }), 
    HTML(new String[] { "<!DOCTYP", "<html", "\n\r<html", "\r\n<html", "\r<html", "\n<html", "<HTML", "\r\n<HTML", "\n\r<HTML", "\r<HTML", "\n<HTML" }), 
    WORD2(new byte[][] { { -37, -91, 45, 0 } }), 
    JPEG(new byte[][] { { -1, -40, -1, -37 }, { -1, -40, -1, -32, 63, 63, 74, 70, 73, 70, 0, 1 }, { -1, -40, -1, -18 }, { -1, -40, -1, -31, 63, 63, 69, 120, 105, 102, 0, 0 } }), 
    GIF(new String[] { "GIF87a", "GIF89a" }), 
    PNG(new byte[][] { { -119, 80, 78, 71, 13, 10, 26, 10 } }), 
    TIFF(new String[] { "II*\u0000", "MM\u0000*" }), 
    WMF(new byte[][] { { -41, -51, -58, -102 } }), 
    EMF(new byte[][] { { 1, 0, 0, 0, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 32, 69, 77, 70 } }), 
    BMP(new byte[][] { { 66, 77 } }), 
    UNKNOWN(new byte[][] { new byte[0] });
    
    static final int MAX_PATTERN_LENGTH = 44;
    final byte[][] magic;
    
    private FileMagic(final long magic) {
        this.magic = new byte[1][8];
        LittleEndian.putLong(this.magic[0], 0, magic);
    }
    
    private FileMagic(final byte[][] magic) {
        this.magic = magic;
    }
    
    private FileMagic(final String[] magic) {
        this.magic = new byte[magic.length][];
        int i = 0;
        for (final String s : magic) {
            this.magic[i++] = s.getBytes(LocaleUtil.CHARSET_1252);
        }
    }
    
    public static FileMagic valueOf(final byte[] magic) {
        for (final FileMagic fm : values()) {
            for (final byte[] ma : fm.magic) {
                if (magic.length >= ma.length) {
                    if (findMagic(ma, magic)) {
                        return fm;
                    }
                }
            }
        }
        return FileMagic.UNKNOWN;
    }
    
    private static boolean findMagic(final byte[] expected, final byte[] actual) {
        int i = 0;
        for (final byte expectedByte : expected) {
            if (actual[i++] != expectedByte && expectedByte != 63) {
                return false;
            }
        }
        return true;
    }
    
    public static FileMagic valueOf(final File inp) throws IOException {
        try (final FileInputStream fis = new FileInputStream(inp)) {
            byte[] data = new byte[44];
            final int read = IOUtils.readFully(fis, data, 0, 44);
            if (read == -1) {
                return FileMagic.UNKNOWN;
            }
            data = Arrays.copyOf(data, read);
            return valueOf(data);
        }
    }
    
    public static FileMagic valueOf(final InputStream inp) throws IOException {
        if (!inp.markSupported()) {
            throw new IOException("getFileMagic() only operates on streams which support mark(int)");
        }
        final byte[] data = IOUtils.peekFirstNBytes(inp, 44);
        return valueOf(data);
    }
    
    public static InputStream prepareToCheckMagic(final InputStream stream) {
        if (stream.markSupported()) {
            return stream;
        }
        return new BufferedInputStream(stream);
    }
}
