package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.util.SymbolHash;

public class SchemaDVFactoryImpl extends BaseSchemaDVFactory
{
    static final SymbolHash fBuiltInTypes;
    
    static void createBuiltInTypes() {
        BaseSchemaDVFactory.createBuiltInTypes(SchemaDVFactoryImpl.fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
        final XSFacets xsFacets = new XSFacets();
        xsFacets.minLength = 1;
        final XSSimpleTypeDecl xsSimpleTypeDecl = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)SchemaDVFactoryImpl.fBuiltInTypes.get("ENTITY"), true, null), "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl.applyFacets1(xsFacets, 2, 0);
        SchemaDVFactoryImpl.fBuiltInTypes.put("ENTITIES", xsSimpleTypeDecl);
        final XSSimpleTypeDecl xsSimpleTypeDecl2 = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)SchemaDVFactoryImpl.fBuiltInTypes.get("NMTOKEN"), true, null), "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl2.applyFacets1(xsFacets, 2, 0);
        SchemaDVFactoryImpl.fBuiltInTypes.put("NMTOKENS", xsSimpleTypeDecl2);
        final XSSimpleTypeDecl xsSimpleTypeDecl3 = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, (XSSimpleTypeDecl)SchemaDVFactoryImpl.fBuiltInTypes.get("IDREF"), true, null), "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl3.applyFacets1(xsFacets, 2, 0);
        SchemaDVFactoryImpl.fBuiltInTypes.put("IDREFS", xsSimpleTypeDecl3);
    }
    
    public XSSimpleType getBuiltInType(final String s) {
        return (XSSimpleType)SchemaDVFactoryImpl.fBuiltInTypes.get(s);
    }
    
    public SymbolHash getBuiltInTypes() {
        return SchemaDVFactoryImpl.fBuiltInTypes.makeClone();
    }
    
    static {
        fBuiltInTypes = new SymbolHash();
        createBuiltInTypes();
    }
}
