package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;

public abstract class SchemaDVFactory
{
    private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl";
    
    public static final synchronized SchemaDVFactory getInstance() throws DVFactoryException {
        return getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl");
    }
    
    public static final synchronized SchemaDVFactory getInstance(final String factoryClass) throws DVFactoryException {
        try {
            return (SchemaDVFactory)ObjectFactory.newInstance(factoryClass, true);
        }
        catch (final ClassCastException e4) {
            throw new DVFactoryException("Schema factory class " + factoryClass + " does not extend from SchemaDVFactory.");
        }
    }
    
    protected SchemaDVFactory() {
    }
    
    public abstract XSSimpleType getBuiltInType(final String p0);
    
    public abstract SymbolHash getBuiltInTypes();
    
    public abstract XSSimpleType createTypeRestriction(final String p0, final String p1, final short p2, final XSSimpleType p3, final XSObjectList p4);
    
    public abstract XSSimpleType createTypeList(final String p0, final String p1, final short p2, final XSSimpleType p3, final XSObjectList p4);
    
    public abstract XSSimpleType createTypeUnion(final String p0, final String p1, final short p2, final XSSimpleType[] p3, final XSObjectList p4);
}
