package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.PushbackInputStream;
import java.io.InputStream;

class EncodingDetectionHelper
{
    private final InputStream stream;
    private final boolean useMark;
    
    public EncodingDetectionHelper(final InputStream stream) {
        this.useMark = stream.markSupported();
        if (this.useMark) {
            this.stream = stream;
        }
        else {
            this.stream = new PushbackInputStream(stream, 4);
        }
    }
    
    public InputStream getInputStream() {
        return this.stream;
    }
    
    public String detectEncoding() throws XMLStreamException {
        final byte[] startBytes = new byte[4];
        try {
            if (this.useMark) {
                this.stream.mark(4);
            }
            int read = 0;
            do {
                final int c = this.stream.read(startBytes, read, 4 - read);
                if (c == -1) {
                    throw new XMLStreamException("Unexpected end of stream");
                }
                read += c;
            } while (read < 4);
            if (this.useMark) {
                this.stream.reset();
            }
            else {
                ((PushbackInputStream)this.stream).unread(startBytes);
            }
        }
        catch (final IOException ex) {
            throw new XMLStreamException("Unable to read start bytes", ex);
        }
        final int marker = ((startBytes[0] & 0xFF) << 24) + ((startBytes[1] & 0xFF) << 16) + ((startBytes[2] & 0xFF) << 8) + (startBytes[3] & 0xFF);
        switch (marker) {
            case -16842752:
            case -131072:
            case 60:
            case 15360:
            case 65279:
            case 65534:
            case 3932160:
            case 1006632960: {
                return "UCS-4";
            }
            case 3932223: {
                return "UTF-16BE";
            }
            case 1006649088: {
                return "UTF-16LE";
            }
            case 1010792557: {
                return "UTF-8";
            }
            default: {
                if ((marker & 0xFFFF0000) == 0xFEFF0000) {
                    return "UTF-16BE";
                }
                if ((marker & 0xFFFF0000) == 0xFFFE0000) {
                    return "UTF-16LE";
                }
                return "UTF-8";
            }
        }
    }
}
