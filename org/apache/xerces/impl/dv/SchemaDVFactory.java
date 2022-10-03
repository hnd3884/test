package org.apache.xerces.impl.dv;

import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.util.SymbolHash;

public abstract class SchemaDVFactory
{
    private static final String DEFAULT_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl";
    
    public static final SchemaDVFactory getInstance() throws DVFactoryException {
        return getInstance("org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl");
    }
    
    public static final SchemaDVFactory getInstance(final String s) throws DVFactoryException {
        try {
            return (SchemaDVFactory)ObjectFactory.newInstance(s, ObjectFactory.findClassLoader(), true);
        }
        catch (final ClassCastException ex) {
            throw new DVFactoryException("Schema factory class " + s + " does not extend from SchemaDVFactory.");
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
