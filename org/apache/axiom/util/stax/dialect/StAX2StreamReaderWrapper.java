package org.apache.axiom.util.stax.dialect;

import org.codehaus.stax2.DTDInfo;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class StAX2StreamReaderWrapper extends XMLStreamReaderWrapper implements DTDReader
{
    public StAX2StreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }
    
    public String getRootName() {
        return ((DTDInfo)this.getParent()).getDTDRootName();
    }
    
    public String getPublicId() {
        return ((DTDInfo)this.getParent()).getDTDPublicId();
    }
    
    public String getSystemId() {
        return ((DTDInfo)this.getParent()).getDTDSystemId();
    }
}
