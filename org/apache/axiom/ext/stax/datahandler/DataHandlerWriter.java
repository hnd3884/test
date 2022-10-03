package org.apache.axiom.ext.stax.datahandler;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.activation.DataHandler;

public interface DataHandlerWriter
{
    public static final String PROPERTY = DataHandlerWriter.class.getName();
    
    void writeDataHandler(final DataHandler p0, final String p1, final boolean p2) throws IOException, XMLStreamException;
    
    void writeDataHandler(final DataHandlerProvider p0, final String p1, final boolean p2) throws IOException, XMLStreamException;
}
