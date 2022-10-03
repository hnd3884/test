package org.apache.axiom.om.impl.common.serializer.push;

public abstract class OutputException extends Exception
{
    private static final long serialVersionUID = 7173617216602466028L;
    
    public OutputException(final Throwable cause) {
        super(cause);
    }
}
