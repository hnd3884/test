package org.apache.jasper.compiler;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;

class EncodingDetector
{
    private static final XMLInputFactory XML_INPUT_FACTORY;
    private final String encoding;
    private final int skip;
    private final boolean encodingSpecifiedInProlog;
    
    EncodingDetector(final InputStream is) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(is, 4);
        bis.mark(4);
        final BomResult bomResult = this.processBom(bis);
        bis.reset();
        for (int i = 0; i < bomResult.skip; ++i) {
            bis.read();
        }
        final String prologEncoding = this.getPrologEncoding(bis);
        if (prologEncoding == null) {
            this.encodingSpecifiedInProlog = false;
            this.encoding = bomResult.encoding;
        }
        else {
            this.encodingSpecifiedInProlog = true;
            this.encoding = prologEncoding;
        }
        this.skip = bomResult.skip;
    }
    
    String getEncoding() {
        return this.encoding;
    }
    
    int getSkip() {
        return this.skip;
    }
    
    boolean isEncodingSpecifiedInProlog() {
        return this.encodingSpecifiedInProlog;
    }
    
    private String getPrologEncoding(final InputStream stream) {
        String encoding = null;
        try {
            final XMLStreamReader xmlStreamReader = EncodingDetector.XML_INPUT_FACTORY.createXMLStreamReader(stream);
            encoding = xmlStreamReader.getCharacterEncodingScheme();
        }
        catch (final XMLStreamException ex) {}
        return encoding;
    }
    
    private BomResult processBom(final InputStream stream) {
        try {
            final byte[] b4 = new byte[4];
            int count;
            for (count = 0; count < 4; ++count) {
                final int singleByteRead = stream.read();
                if (singleByteRead == -1) {
                    break;
                }
                b4[count] = (byte)singleByteRead;
            }
            return this.parseBom(b4, count);
        }
        catch (final IOException ioe) {
            return new BomResult("UTF-8", 0);
        }
    }
    
    private BomResult parseBom(final byte[] b4, final int count) {
        if (count < 2) {
            return new BomResult("UTF-8", 0);
        }
        final int b5 = b4[0] & 0xFF;
        final int b6 = b4[1] & 0xFF;
        if (b5 == 254 && b6 == 255) {
            return new BomResult("UTF-16BE", 2);
        }
        if (b5 == 255 && b6 == 254) {
            return new BomResult("UTF-16LE", 2);
        }
        if (count < 3) {
            return new BomResult("UTF-8", 0);
        }
        final int b7 = b4[2] & 0xFF;
        if (b5 == 239 && b6 == 187 && b7 == 191) {
            return new BomResult("UTF-8", 3);
        }
        if (count < 4) {
            return new BomResult("UTF-8", 0);
        }
        final int b8 = b4[3] & 0xFF;
        if (b5 == 0 && b6 == 0 && b7 == 0 && b8 == 60) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b5 == 60 && b6 == 0 && b7 == 0 && b8 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b5 == 0 && b6 == 0 && b7 == 60 && b8 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 63) {
            return new BomResult("UTF-16BE", 0);
        }
        if (b5 == 60 && b6 == 0 && b7 == 63 && b8 == 0) {
            return new BomResult("UTF-16LE", 0);
        }
        if (b5 == 76 && b6 == 111 && b7 == 167 && b8 == 148) {
            return new BomResult("CP037", 0);
        }
        return new BomResult("UTF-8", 0);
    }
    
    static {
        XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    }
    
    private static class BomResult
    {
        public final String encoding;
        public final int skip;
        
        public BomResult(final String encoding, final int skip) {
            this.encoding = encoding;
            this.skip = skip;
        }
    }
}
