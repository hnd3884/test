package org.apache.axiom.om.ds;

import org.apache.axiom.om.OMDataSourceExt;
import java.io.Reader;
import org.apache.axiom.om.util.StAXUtils;
import java.io.CharArrayReader;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMOutputFormat;
import java.io.Writer;

public class CharArrayDataSource extends OMDataSourceExtBase
{
    char[] chars;
    
    public CharArrayDataSource(final char[] chars) {
        this.chars = null;
        this.chars = chars;
    }
    
    @Override
    public void serialize(final Writer writer, final OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(this.chars);
        }
        catch (final UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (final IOException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        final CharArrayReader reader = new CharArrayReader(this.chars);
        return StAXUtils.createXMLStreamReader(reader);
    }
    
    public Object getObject() {
        return this.chars;
    }
    
    public boolean isDestructiveRead() {
        return false;
    }
    
    public boolean isDestructiveWrite() {
        return false;
    }
    
    public byte[] getXMLBytes(final String encoding) throws UnsupportedEncodingException {
        final String text = new String(this.chars);
        return text.getBytes(encoding);
    }
    
    public void close() {
        this.chars = null;
    }
    
    public OMDataSourceExt copy() {
        return new CharArrayDataSource(this.chars);
    }
}
