package org.apache.axiom.ext.stax.datahandler;

import javax.xml.stream.XMLStreamException;
import javax.activation.DataHandler;

public interface DataHandlerReader
{
    public static final String PROPERTY = DataHandlerReader.class.getName();
    
    boolean isBinary();
    
    boolean isOptimized();
    
    boolean isDeferred();
    
    String getContentID();
    
    DataHandler getDataHandler() throws XMLStreamException;
    
    DataHandlerProvider getDataHandlerProvider();
}
