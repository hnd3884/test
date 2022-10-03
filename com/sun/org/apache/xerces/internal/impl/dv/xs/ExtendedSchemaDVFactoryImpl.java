package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class ExtendedSchemaDVFactoryImpl extends BaseSchemaDVFactory
{
    static SymbolHash fBuiltInTypes;
    
    static void createBuiltInTypes() {
        final String ANYATOMICTYPE = "anyAtomicType";
        final String DURATION = "duration";
        final String YEARMONTHDURATION = "yearMonthDuration";
        final String DAYTIMEDURATION = "dayTimeDuration";
        BaseSchemaDVFactory.createBuiltInTypes(ExtendedSchemaDVFactoryImpl.fBuiltInTypes, XSSimpleTypeDecl.fAnyAtomicType);
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("anyAtomicType", XSSimpleTypeDecl.fAnyAtomicType);
        final XSSimpleTypeDecl durationDV = (XSSimpleTypeDecl)ExtendedSchemaDVFactoryImpl.fBuiltInTypes.get("duration");
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("yearMonthDuration", new XSSimpleTypeDecl(durationDV, "yearMonthDuration", (short)27, (short)1, false, false, false, true, (short)46));
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes.put("dayTimeDuration", new XSSimpleTypeDecl(durationDV, "dayTimeDuration", (short)28, (short)1, false, false, false, true, (short)47));
    }
    
    @Override
    public XSSimpleType getBuiltInType(final String name) {
        return (XSSimpleType)ExtendedSchemaDVFactoryImpl.fBuiltInTypes.get(name);
    }
    
    @Override
    public SymbolHash getBuiltInTypes() {
        return ExtendedSchemaDVFactoryImpl.fBuiltInTypes.makeClone();
    }
    
    static {
        ExtendedSchemaDVFactoryImpl.fBuiltInTypes = new SymbolHash();
        createBuiltInTypes();
    }
}
