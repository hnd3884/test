package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JClass;

public final class VoidClassImpl extends BuiltinClassImpl
{
    private static final String SIMPLE_NAME = "void";
    
    public static boolean isVoid(final String fd) {
        return fd.equals("void");
    }
    
    public VoidClassImpl(final ElementContext ctx) {
        super(ctx);
        super.reallySetSimpleName("void");
    }
    
    @Override
    public boolean isVoidType() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(final JClass c) {
        return false;
    }
}
