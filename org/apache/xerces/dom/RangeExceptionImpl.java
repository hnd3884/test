package org.apache.xerces.dom;

import org.w3c.dom.ranges.RangeException;

public class RangeExceptionImpl extends RangeException
{
    static final long serialVersionUID = -9058052627467240856L;
    
    public RangeExceptionImpl(final short n, final String s) {
        super(n, s);
    }
}
