package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class FullDVFactory extends BaseDVFactory
{
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fFullTypes;
    
    @Override
    public XSSimpleType getBuiltInType(final String name) {
        return (XSSimpleType)FullDVFactory.fFullTypes.get(name);
    }
    
    @Override
    public SymbolHash getBuiltInTypes() {
        return FullDVFactory.fFullTypes.makeClone();
    }
    
    static void createBuiltInTypes(final SymbolHash types) {
        BaseDVFactory.createBuiltInTypes(types);
        final String DOUBLE = "double";
        final String DURATION = "duration";
        final String ENTITY = "ENTITY";
        final String ENTITIES = "ENTITIES";
        final String FLOAT = "float";
        final String HEXBINARY = "hexBinary";
        final String ID = "ID";
        final String IDREF = "IDREF";
        final String IDREFS = "IDREFS";
        final String NAME = "Name";
        final String NCNAME = "NCName";
        final String NMTOKEN = "NMTOKEN";
        final String NMTOKENS = "NMTOKENS";
        final String LANGUAGE = "language";
        final String NORMALIZEDSTRING = "normalizedString";
        final String NOTATION = "NOTATION";
        final String QNAME = "QName";
        final String STRING = "string";
        final String TOKEN = "token";
        final XSFacets facets = new XSFacets();
        final XSSimpleTypeDecl anySimpleType = XSSimpleTypeDecl.fAnySimpleType;
        final XSSimpleTypeDecl stringDV = (XSSimpleTypeDecl)types.get("string");
        types.put("float", new XSSimpleTypeDecl(anySimpleType, "float", (short)4, (short)1, true, true, true, true, (short)5));
        types.put("double", new XSSimpleTypeDecl(anySimpleType, "double", (short)5, (short)1, true, true, true, true, (short)6));
        types.put("duration", new XSSimpleTypeDecl(anySimpleType, "duration", (short)6, (short)1, false, false, false, true, (short)7));
        types.put("hexBinary", new XSSimpleTypeDecl(anySimpleType, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
        types.put("QName", new XSSimpleTypeDecl(anySimpleType, "QName", (short)18, (short)0, false, false, false, true, (short)19));
        types.put("NOTATION", new XSSimpleTypeDecl(anySimpleType, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
        facets.whiteSpace = 1;
        final XSSimpleTypeDecl normalizedDV = new XSSimpleTypeDecl(stringDV, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
        normalizedDV.applyFacets1(facets, (short)16, (short)0);
        types.put("normalizedString", normalizedDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl tokenDV = new XSSimpleTypeDecl(normalizedDV, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
        tokenDV.applyFacets1(facets, (short)16, (short)0);
        types.put("token", tokenDV);
        facets.whiteSpace = 2;
        facets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
        final XSSimpleTypeDecl languageDV = new XSSimpleTypeDecl(tokenDV, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
        languageDV.applyFacets1(facets, (short)24, (short)0);
        types.put("language", languageDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl nameDV = new XSSimpleTypeDecl(tokenDV, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
        nameDV.applyFacets1(facets, (short)16, (short)0, (short)2);
        types.put("Name", nameDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl ncnameDV = new XSSimpleTypeDecl(nameDV, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
        ncnameDV.applyFacets1(facets, (short)16, (short)0, (short)3);
        types.put("NCName", ncnameDV);
        types.put("ID", new XSSimpleTypeDecl(ncnameDV, "ID", (short)21, (short)0, false, false, false, true, (short)27));
        final XSSimpleTypeDecl idrefDV = new XSSimpleTypeDecl(ncnameDV, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
        types.put("IDREF", idrefDV);
        facets.minLength = 1;
        XSSimpleTypeDecl tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, idrefDV, true, null);
        final XSSimpleTypeDecl idrefsDV = new XSSimpleTypeDecl(tempDV, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        idrefsDV.applyFacets1(facets, (short)2, (short)0);
        types.put("IDREFS", idrefsDV);
        final XSSimpleTypeDecl entityDV = new XSSimpleTypeDecl(ncnameDV, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
        types.put("ENTITY", entityDV);
        facets.minLength = 1;
        tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, entityDV, true, null);
        final XSSimpleTypeDecl entitiesDV = new XSSimpleTypeDecl(tempDV, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        entitiesDV.applyFacets1(facets, (short)2, (short)0);
        types.put("ENTITIES", entitiesDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl nmtokenDV = new XSSimpleTypeDecl(tokenDV, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
        nmtokenDV.applyFacets1(facets, (short)16, (short)0, (short)1);
        types.put("NMTOKEN", nmtokenDV);
        facets.minLength = 1;
        tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, nmtokenDV, true, null);
        final XSSimpleTypeDecl nmtokensDV = new XSSimpleTypeDecl(tempDV, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        nmtokensDV.applyFacets1(facets, (short)2, (short)0);
        types.put("NMTOKENS", nmtokensDV);
    }
    
    static {
        createBuiltInTypes(FullDVFactory.fFullTypes = new SymbolHash(89));
    }
}
