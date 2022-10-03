package com.dd.plist;

import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import org.xml.sax.SAXException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PropertyListParser
{
    private static final int TYPE_XML = 0;
    private static final int TYPE_BINARY = 1;
    private static final int TYPE_ASCII = 2;
    private static final int TYPE_ERROR_BLANK = 10;
    private static final int TYPE_ERROR_UNKNOWN = 11;
    private static final int READ_BUFFER_LENGTH = 2048;
    
    protected PropertyListParser() {
    }
    
    private static int determineType(String dataBeginning) {
        dataBeginning = dataBeginning.trim();
        if (dataBeginning.length() == 0) {
            return 10;
        }
        if (dataBeginning.startsWith("bplist")) {
            return 1;
        }
        if (dataBeginning.startsWith("(") || dataBeginning.startsWith("{") || dataBeginning.startsWith("/")) {
            return 2;
        }
        if (dataBeginning.startsWith("<")) {
            return 0;
        }
        return 11;
    }
    
    private static int determineType(final byte[] bytes) {
        int offset = 0;
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
            offset += 3;
        }
        while (offset < bytes.length && (bytes[offset] == 32 || bytes[offset] == 9 || bytes[offset] == 13 || bytes[offset] == 10 || bytes[offset] == 12)) {
            ++offset;
        }
        return determineType(new String(bytes, offset, Math.min(8, bytes.length - offset)));
    }
    
    private static int determineType(final InputStream is, final int offset) throws IOException {
        int index = offset;
        final int readLimit = index + 1024;
        if (is.markSupported()) {
            is.mark(readLimit);
        }
        is.skip(offset);
        boolean bom = false;
        while (++index <= readLimit) {
            final int b = is.read();
            bom = (index < 3 && ((index == 0 && b == 239) || (bom && ((index == 1 && b == 187) || (index == 2 && b == 191)))));
            if (b == -1 || (b != 32 && b != 9 && b != 13 && b != 10 && b != 12 && !bom)) {
                if (b == -1) {
                    return 10;
                }
                final byte[] magicBytes = new byte[8];
                magicBytes[0] = (byte)b;
                final int read = is.read(magicBytes, 1, 7);
                final int type = determineType(new String(magicBytes, 0, read));
                if (is.markSupported()) {
                    is.reset();
                }
                return type;
            }
        }
        is.reset();
        return determineType(is, readLimit);
    }
    
    protected static byte[] readAll(final InputStream in) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final byte[] buf = new byte[2048];
        int read;
        while ((read = in.read(buf, 0, 2048)) != -1) {
            outputStream.write(buf, 0, read);
        }
        return outputStream.toByteArray();
    }
    
    public static NSObject parse(final String filePath) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        return parse(new File(filePath));
    }
    
    public static NSObject parse(final File f) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        final FileInputStream fis = new FileInputStream(f);
        try {
            return parse(fis);
        }
        finally {
            try {
                fis.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static NSObject parse(final byte[] bytes) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        return parse(new ByteArrayInputStream(bytes));
    }
    
    public static NSObject parse(InputStream is) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        switch (determineType(is, 0)) {
            case 1: {
                return BinaryPropertyListParser.parse(is);
            }
            case 0: {
                return XMLPropertyListParser.parse(is);
            }
            case 2: {
                return ASCIIPropertyListParser.parse(is);
            }
            case 10: {
                return null;
            }
            default: {
                throw new PropertyListFormatException("The given data is not a property list of a supported format.");
            }
        }
    }
    
    public static void saveAsXML(final NSObject root, final File out) throws IOException {
        final File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }
        final FileOutputStream fous = new FileOutputStream(out);
        saveAsXML(root, fous);
        fous.close();
    }
    
    public static void saveAsXML(final NSObject root, final OutputStream out) throws IOException {
        final OutputStreamWriter w = new OutputStreamWriter(out, "UTF-8");
        w.write(root.toXMLPropertyList());
        w.close();
    }
    
    public static void convertToXml(final File in, final File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        final NSObject root = parse(in);
        saveAsXML(root, out);
    }
    
    public static void saveAsBinary(final NSObject root, final File out) throws IOException {
        final File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }
        BinaryPropertyListWriter.write(out, root);
    }
    
    public static void saveAsBinary(final NSObject root, final OutputStream out) throws IOException {
        BinaryPropertyListWriter.write(out, root);
    }
    
    public static void convertToBinary(final File in, final File out) throws IOException, ParserConfigurationException, ParseException, SAXException, PropertyListFormatException {
        final NSObject root = parse(in);
        saveAsBinary(root, out);
    }
    
    public static void saveAsASCII(final NSDictionary root, final File out) throws IOException {
        final File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }
        final OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "ASCII");
        w.write(root.toASCIIPropertyList());
        w.close();
    }
    
    public static void saveAsASCII(final NSArray root, final File out) throws IOException {
        final OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "ASCII");
        w.write(root.toASCIIPropertyList());
        w.close();
    }
    
    public static void convertToASCII(final File in, final File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        final NSObject root = parse(in);
        if (root instanceof NSDictionary) {
            saveAsASCII((NSDictionary)root, out);
        }
        else {
            if (!(root instanceof NSArray)) {
                throw new PropertyListFormatException("The root of the given input property list is neither a Dictionary nor an Array!");
            }
            saveAsASCII((NSArray)root, out);
        }
    }
    
    public static void saveAsGnuStepASCII(final NSDictionary root, final File out) throws IOException {
        final File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }
        final OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "ASCII");
        w.write(root.toGnuStepASCIIPropertyList());
        w.close();
    }
    
    public static void saveAsGnuStepASCII(final NSArray root, final File out) throws IOException {
        final File parent = out.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("The output directory does not exist and could not be created.");
        }
        final OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "ASCII");
        w.write(root.toGnuStepASCIIPropertyList());
        w.close();
    }
    
    public static void convertToGnuStepASCII(final File in, final File out) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        final NSObject root = parse(in);
        if (root instanceof NSDictionary) {
            saveAsGnuStepASCII((NSDictionary)root, out);
        }
        else {
            if (!(root instanceof NSArray)) {
                throw new PropertyListFormatException("The root of the given input property list is neither a Dictionary nor an Array!");
            }
            saveAsGnuStepASCII((NSArray)root, out);
        }
    }
}
