package org.apache.axiom.util.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;

public class XMLStreamWriterWriter extends Writer
{
    private final XMLStreamWriter writer;
    
    public XMLStreamWriterWriter(final XMLStreamWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        try {
            this.writer.writeCharacters(cbuf, off, len);
        }
        catch (final XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        this.write(str.substring(off, off + len));
    }
    
    @Override
    public void write(final String str) throws IOException {
        try {
            this.writer.writeCharacters(str);
        }
        catch (final XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.write(new char[] { (char)c });
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
    }
}
