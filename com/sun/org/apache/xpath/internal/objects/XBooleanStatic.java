package com.sun.org.apache.xpath.internal.objects;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;

public class XBooleanStatic extends XBoolean
{
    static final long serialVersionUID = -8064147275772687409L;
    private final boolean m_val;
    
    public XBooleanStatic(final boolean b) {
        super(b);
        this.m_val = b;
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        try {
            return this.m_val == obj2.bool();
        }
        catch (final TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
}
