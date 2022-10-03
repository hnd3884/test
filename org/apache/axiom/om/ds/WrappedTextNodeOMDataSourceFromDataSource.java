package org.apache.axiom.om.ds;

import org.apache.axiom.om.OMDataSourceExt;
import java.io.InputStream;
import java.io.Reader;
import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;
import java.nio.charset.Charset;
import javax.activation.DataSource;

public class WrappedTextNodeOMDataSourceFromDataSource extends WrappedTextNodeOMDataSource
{
    private final DataSource binaryData;
    private final Charset charset;
    
    public WrappedTextNodeOMDataSourceFromDataSource(final QName wrapperElementName, final DataSource binaryData, final Charset charset) {
        super(wrapperElementName);
        this.binaryData = binaryData;
        this.charset = charset;
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        InputStream is;
        try {
            is = this.binaryData.getInputStream();
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
        return new WrappedTextNodeStreamReader(this.wrapperElementName, new InputStreamReader(is, this.charset));
    }
    
    @Override
    public Object getObject() {
        return this.binaryData;
    }
    
    public boolean isDestructiveRead() {
        return false;
    }
    
    @Override
    public OMDataSourceExt copy() {
        return new WrappedTextNodeOMDataSourceFromDataSource(this.wrapperElementName, this.binaryData, this.charset);
    }
}
