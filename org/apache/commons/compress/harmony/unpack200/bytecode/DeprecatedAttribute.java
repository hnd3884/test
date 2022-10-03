package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class DeprecatedAttribute extends Attribute
{
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        DeprecatedAttribute.attributeName = cpUTF8Value;
    }
    
    public DeprecatedAttribute() {
        super(DeprecatedAttribute.attributeName);
    }
    
    @Override
    protected int getLength() {
        return 0;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
    }
    
    @Override
    public String toString() {
        return "Deprecated Attribute";
    }
}
