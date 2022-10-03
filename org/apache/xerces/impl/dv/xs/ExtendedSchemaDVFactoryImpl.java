package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.util.SymbolHash;

public class ExtendedSchemaDVFactoryImpl extends BaseSchemaDVFactory
{
    static SymbolHash fBuiltInTypes;
    
    static void createBuiltInTypes() {
        BaseSchemaDVFactory.createBuiltInTypes(ExtendedSchemaDVFactoryImpl.fBuiltInTypes, XSSimpleTypeDecl.fAnyAtomicType);
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("anyAtomicType", XSSimpleTypeDecl.fAnyAtomicType);
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("error", XSSimpleTypeDecl.fError);
        final XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)ExtendedSchemaDVFactoryImpl.fBuiltInTypes.get("duration");
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("yearMonthDuration", new XSSimpleTypeDecl(xsSimpleTypeDecl, "yearMonthDuration", (short)27, (short)1, false, false, false, true, (short)46));
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("dayTimeDuration", new XSSimpleTypeDecl(xsSimpleTypeDecl, "dayTimeDuration", (short)28, (short)1, false, false, false, true, (short)47));
    }
    
    public XSSimpleType getBuiltInType(final String s) {
        return (XSSimpleType)ExtendedSchemaDVFactoryImpl.fBuiltInTypes.get(s);
    }
    
    public SymbolHash getBuiltInTypes() {
        return ExtendedSchemaDVFactoryImpl.fBuiltInTypes.makeClone();
    }
    
    static {
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes = new SymbolHash();
        createBuiltInTypes();
    }
}
