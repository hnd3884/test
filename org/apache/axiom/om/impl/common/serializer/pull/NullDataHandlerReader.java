package org.apache.axiom.om.impl.common.serializer.pull;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.xml.stream.XMLStreamException;
import javax.activation.DataHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;

final class NullDataHandlerReader implements DataHandlerReader
{
    static final NullDataHandlerReader INSTANCE;
    
    static {
        INSTANCE = new NullDataHandlerReader();
    }
    
    private NullDataHandlerReader() {
    }
    
    public boolean isBinary() {
        return false;
    }
    
    public boolean isOptimized() {
        throw new IllegalStateException();
    }
    
    public boolean isDeferred() {
        throw new IllegalStateException();
    }
    
    public String getContentID() {
        throw new IllegalStateException();
    }
    
    public DataHandler getDataHandler() throws XMLStreamException {
        throw new IllegalStateException();
    }
    
    public DataHandlerProvider getDataHandlerProvider() {
        throw new IllegalStateException();
    }
}
