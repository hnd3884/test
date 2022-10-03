package org.apache.axiom.om.util;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.util.stax.debug.XMLStreamReaderValidator;

public class OMXMLStreamReaderValidator extends XMLStreamReaderValidator implements OMXMLStreamReader
{
    public OMXMLStreamReaderValidator(final OMXMLStreamReader delegate, final boolean throwExceptions) {
        super(delegate, throwExceptions);
    }
    
    public DataHandler getDataHandler(final String blobcid) {
        return ((OMXMLStreamReader)this.getParent()).getDataHandler(blobcid);
    }
    
    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)this.getParent()).isInlineMTOM();
    }
    
    public void setInlineMTOM(final boolean value) {
        ((OMXMLStreamReader)this.getParent()).setInlineMTOM(value);
    }
}
