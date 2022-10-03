package org.apache.axiom.util.stax;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;

class TextFromElementReader extends Reader
{
    private final XMLStreamReader stream;
    private final boolean allowNonTextChildren;
    private boolean endOfStream;
    private int skipDepth;
    private int sourceStart;
    
    TextFromElementReader(final XMLStreamReader stream, final boolean allowNonTextChildren) {
        this.sourceStart = -1;
        this.stream = stream;
        this.allowNonTextChildren = allowNonTextChildren;
    }
    
    @Override
    public int read(final char[] cbuf, int off, int len) throws IOException {
        if (this.endOfStream) {
            return -1;
        }
        int read = 0;
        try {
            while (true) {
                Label_0158: {
                    if (this.sourceStart == -1) {
                        while (true) {
                            final int type = this.stream.next();
                            switch (type) {
                                case 4:
                                case 12: {
                                    if (this.skipDepth == 0) {
                                        this.sourceStart = 0;
                                        break Label_0158;
                                    }
                                    continue;
                                }
                                case 1: {
                                    if (this.allowNonTextChildren) {
                                        ++this.skipDepth;
                                        continue;
                                    }
                                    throw new IOException("Unexpected START_ELEMENT event");
                                }
                                case 2: {
                                    if (this.skipDepth == 0) {
                                        this.endOfStream = true;
                                        return (read == 0) ? -1 : read;
                                    }
                                    --this.skipDepth;
                                    continue;
                                }
                            }
                        }
                    }
                }
                final int c = this.stream.getTextCharacters(this.sourceStart, cbuf, off, len);
                this.sourceStart += c;
                off += c;
                len -= c;
                read += c;
                if (len <= 0) {
                    return read;
                }
                this.sourceStart = -1;
            }
        }
        catch (final XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }
    
    @Override
    public void close() throws IOException {
    }
}
