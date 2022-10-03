package org.apache.axiom.om.ds;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;
import java.io.Reader;

public class WrappedTextNodeOMDataSourceFromReader extends WrappedTextNodeOMDataSource
{
    private final Reader reader;
    private boolean isAccessed;
    
    public WrappedTextNodeOMDataSourceFromReader(final QName wrapperElementName, final Reader reader) {
        super(wrapperElementName);
        this.reader = reader;
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        this.isAccessed = true;
        return new WrappedTextNodeStreamReader(this.wrapperElementName, this.reader);
    }
    
    @Override
    public Object getObject() {
        return this.isAccessed ? null : this.reader;
    }
    
    public boolean isDestructiveRead() {
        return true;
    }
    
    @Override
    public void close() {
        try {
            this.reader.close();
        }
        catch (final IOException ex) {}
    }
}
