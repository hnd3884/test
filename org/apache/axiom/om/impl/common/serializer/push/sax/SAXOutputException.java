package org.apache.axiom.om.impl.common.serializer.push.sax;

import org.xml.sax.SAXException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;

public class SAXOutputException extends OutputException
{
    private static final long serialVersionUID = -4299745257772383270L;
    
    public SAXOutputException(final SAXException cause) {
        super(cause);
    }
}
