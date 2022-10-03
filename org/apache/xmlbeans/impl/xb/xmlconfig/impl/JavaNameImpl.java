package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaName;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class JavaNameImpl extends JavaStringHolderEx implements JavaName
{
    private static final long serialVersionUID = 1L;
    
    public JavaNameImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected JavaNameImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
