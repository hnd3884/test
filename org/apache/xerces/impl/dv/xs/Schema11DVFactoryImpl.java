package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.util.SymbolHash;

public class Schema11DVFactoryImpl extends BaseSchemaDVFactory
{
    static SymbolHash fBuiltInTypes;
    
    static void createBuiltInTypes() {
        BaseSchemaDVFactory.createBuiltInTypes(Schema11DVFactoryImpl.fBuiltInTypes, XSSimpleTypeDecl.fAnyAtomicType);
        Schema11DVFactoryImpl.fBuiltInTypes.put("anyAtomicType", XSSimpleTypeDecl.fAnyAtomicType);
        Schema11DVFactoryImpl.fBuiltInTypes.put("error", XSSimpleTypeDecl.fError);
        final XSFacets xsFacets = new XSFacets();
        xsFacets.minLength = 1;
        xsFacets.whiteSpace = 2;
        final XSSimpleTypeDecl xsSimpleTypeDecl = new XSSimpleTypeDecl("ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)Schema11DVFactoryImpl.fBuiltInTypes.get("ENTITY"), false, null);
        xsSimpleTypeDecl.applyFacets1(xsFacets, 18, 0);
        Schema11DVFactoryImpl.fBuiltInTypes.put("ENTITIES", xsSimpleTypeDecl);
        final XSSimpleTypeDecl xsSimpleTypeDecl2 = new XSSimpleTypeDecl("NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)Schema11DVFactoryImpl.fBuiltInTypes.get("NMTOKEN"), false, null);
        xsSimpleTypeDecl2.applyFacets1(xsFacets, 18, 0);
        Schema11DVFactoryImpl.fBuiltInTypes.put("NMTOKENS", xsSimpleTypeDecl2);
        final XSSimpleTypeDecl xsSimpleTypeDecl3 = new XSSimpleTypeDecl("IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)Schema11DVFactoryImpl.fBuiltInTypes.get("IDREF"), false, null);
        xsSimpleTypeDecl3.applyFacets1(xsFacets, 18, 0);
        Schema11DVFactoryImpl.fBuiltInTypes.put("IDREFS", xsSimpleTypeDecl3);
        final XSSimpleTypeDecl xsSimpleTypeDecl4 = (XSSimpleTypeDecl)Schema11DVFactoryImpl.fBuiltInTypes.get("duration");
        Schema11DVFactoryImpl.fBuiltInTypes.put("yearMonthDuration", new XSSimpleTypeDecl(xsSimpleTypeDecl4, "yearMonthDuration", (short)27, (short)1, false, false, false, true, (short)46));
        Schema11DVFactoryImpl.fBuiltInTypes.put("dayTimeDuration", new XSSimpleTypeDecl(xsSimpleTypeDecl4, "dayTimeDuration", (short)28, (short)1, false, false, false, true, (short)47));
        Schema11DVFactoryImpl.fBuiltInTypes.put("dateTimeStamp", new XSSimpleTypeDecl((XSSimpleTypeDecl)Schema11DVFactoryImpl.fBuiltInTypes.get("dateTime"), "dateTimeStamp", (short)31, (short)1, false, false, false, true, (short)51));
        Schema11DVFactoryImpl.fBuiltInTypes.put("precisionDecimal", new XSSimpleTypeDecl(XSSimpleTypeDecl.fAnyAtomicType, "precisionDecimal", (short)19, (short)1, false, false, true, true, (short)48));
    }
    
    public XSSimpleType getBuiltInType(final String s) {
        return (XSSimpleType)Schema11DVFactoryImpl.fBuiltInTypes.get(s);
    }
    
    public SymbolHash getBuiltInTypes() {
        return Schema11DVFactoryImpl.fBuiltInTypes.makeClone();
    }
    
    static {
        Schema11DVFactoryImpl.fBuiltInTypes = new SymbolHash();
        createBuiltInTypes();
    }
}
