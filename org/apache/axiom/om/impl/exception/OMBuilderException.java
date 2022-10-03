package org.apache.axiom.om.impl.exception;

import org.apache.axiom.om.OMException;

public class OMBuilderException extends OMException
{
    private static final long serialVersionUID = -7447667411291193889L;
    
    public OMBuilderException(final String s) {
        super(s);
    }
    
    public OMBuilderException(final Throwable cause) {
        super(cause);
    }
}
