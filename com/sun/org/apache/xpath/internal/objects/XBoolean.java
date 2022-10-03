package com.sun.org.apache.xpath.internal.objects;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;

public class XBoolean extends XObject
{
    static final long serialVersionUID = -2964933058866100881L;
    public static final XBoolean S_TRUE;
    public static final XBoolean S_FALSE;
    private final boolean m_val;
    
    public XBoolean(final boolean b) {
        this.m_val = b;
    }
    
    public XBoolean(final Boolean b) {
        this.m_val = b;
        this.setObject(b);
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    @Override
    public String getTypeString() {
        return "#BOOLEAN";
    }
    
    @Override
    public double num() {
        return this.m_val ? 1.0 : 0.0;
    }
    
    @Override
    public boolean bool() {
        return this.m_val;
    }
    
    @Override
    public String str() {
        return this.m_val ? "true" : "false";
    }
    
    @Override
    public Object object() {
        if (null == this.m_obj) {
            this.setObject(new Boolean(this.m_val));
        }
        return this.m_obj;
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        if (obj2.getType() == 4) {
            return obj2.equals(this);
        }
        try {
            return this.m_val == obj2.bool();
        }
        catch (final TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
    
    static {
        S_TRUE = new XBooleanStatic(true);
        S_FALSE = new XBooleanStatic(false);
    }
}
