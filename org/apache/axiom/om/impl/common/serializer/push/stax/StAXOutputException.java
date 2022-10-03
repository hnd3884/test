package org.apache.axiom.om.impl.common.serializer.push.stax;

import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;

public class StAXOutputException extends OutputException
{
    private static final long serialVersionUID = -8641924272865997771L;
    
    public StAXOutputException(final XMLStreamException cause) {
        super(cause);
    }
}
