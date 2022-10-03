package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class SchemaDVFactoryImpl extends BaseSchemaDVFactory
{
    static final SymbolHash fBuiltInTypes;
    
    static void createBuiltInTypes() {
        BaseSchemaDVFactory.createBuiltInTypes(SchemaDVFactoryImpl.fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
    }
    
    @Override
    public XSSimpleType getBuiltInType(final String name) {
        return (XSSimpleType)SchemaDVFactoryImpl.fBuiltInTypes.get(name);
    }
    
    @Override
    public SymbolHash getBuiltInTypes() {
        return SchemaDVFactoryImpl.fBuiltInTypes.makeClone();
    }
    
    static {
        fBuiltInTypes = new SymbolHash();
        createBuiltInTypes();
    }
}
