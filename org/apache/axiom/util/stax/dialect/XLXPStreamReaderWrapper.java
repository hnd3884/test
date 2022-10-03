package org.apache.axiom.util.stax.dialect;

import org.apache.axiom.ext.stax.DTDReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class XLXPStreamReaderWrapper extends XMLStreamReaderWrapper implements DelegatingXMLStreamReader
{
    public XLXPStreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
    }
    
    @Override
    public Object getProperty(final String name) {
        if (DTDReader.PROPERTY.equals(name)) {
            return new AbstractDTDReader(this.getParent()) {
                @Override
                protected String getDocumentTypeDeclaration(final XMLStreamReader reader) {
                    return (String)reader.getProperty("javax.xml.stream.dtd.declaration");
                }
            };
        }
        return super.getProperty(name);
    }
    
    @Override
    public boolean isCharacters() {
        return this.getEventType() == 4;
    }
    
    public XMLStreamReader getParent() {
        return super.getParent();
    }
}
