package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.util.SymbolHash;

public class FullDVFactory extends BaseDVFactory
{
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fFullTypes;
    
    public XSSimpleType getBuiltInType(final String s) {
        return (XSSimpleType)FullDVFactory.fFullTypes.get(s);
    }
    
    public SymbolHash getBuiltInTypes() {
        return FullDVFactory.fFullTypes.makeClone();
    }
    
    static void createBuiltInTypes(final SymbolHash symbolHash) {
        BaseDVFactory.createBuiltInTypes(symbolHash);
        final XSFacets xsFacets = new XSFacets();
        final XSSimpleTypeDecl fAnySimpleType = XSSimpleTypeDecl.fAnySimpleType;
        final XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)symbolHash.get("string");
        symbolHash.put("float", new XSSimpleTypeDecl(fAnySimpleType, "float", (short)4, (short)1, true, true, true, true, (short)5));
        symbolHash.put("double", new XSSimpleTypeDecl(fAnySimpleType, "double", (short)5, (short)1, true, true, true, true, (short)6));
        symbolHash.put("duration", new XSSimpleTypeDecl(fAnySimpleType, "duration", (short)6, (short)1, false, false, false, true, (short)7));
        symbolHash.put("hexBinary", new XSSimpleTypeDecl(fAnySimpleType, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
        symbolHash.put("QName", new XSSimpleTypeDecl(fAnySimpleType, "QName", (short)18, (short)0, false, false, false, true, (short)19));
        symbolHash.put("NOTATION", new XSSimpleTypeDecl(fAnySimpleType, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
        xsFacets.whiteSpace = 1;
        final XSSimpleTypeDecl xsSimpleTypeDecl2 = new XSSimpleTypeDecl(xsSimpleTypeDecl, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
        xsSimpleTypeDecl2.applyFacets1(xsFacets, 16, 0);
        symbolHash.put("normalizedString", xsSimpleTypeDecl2);
        xsFacets.whiteSpace = 2;
        final XSSimpleTypeDecl xsSimpleTypeDecl3 = new XSSimpleTypeDecl(xsSimpleTypeDecl2, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
        xsSimpleTypeDecl3.applyFacets1(xsFacets, 16, 0);
        symbolHash.put("token", xsSimpleTypeDecl3);
        xsFacets.whiteSpace = 2;
        xsFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
        final XSSimpleTypeDecl xsSimpleTypeDecl4 = new XSSimpleTypeDecl(xsSimpleTypeDecl3, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
        xsSimpleTypeDecl4.applyFacets1(xsFacets, 24, 0);
        symbolHash.put("language", xsSimpleTypeDecl4);
        xsFacets.whiteSpace = 2;
        final XSSimpleTypeDecl xsSimpleTypeDecl5 = new XSSimpleTypeDecl(xsSimpleTypeDecl3, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
        xsSimpleTypeDecl5.applyFacets1(xsFacets, 16, 0, (short)2);
        symbolHash.put("Name", xsSimpleTypeDecl5);
        xsFacets.whiteSpace = 2;
        final XSSimpleTypeDecl xsSimpleTypeDecl6 = new XSSimpleTypeDecl(xsSimpleTypeDecl5, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
        xsSimpleTypeDecl6.applyFacets1(xsFacets, 16, 0, (short)3);
        symbolHash.put("NCName", xsSimpleTypeDecl6);
        symbolHash.put("ID", new XSSimpleTypeDecl(xsSimpleTypeDecl6, "ID", (short)21, (short)0, false, false, false, true, (short)27));
        final XSSimpleTypeDecl xsSimpleTypeDecl7 = new XSSimpleTypeDecl(xsSimpleTypeDecl6, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
        symbolHash.put("IDREF", xsSimpleTypeDecl7);
        xsFacets.minLength = 1;
        final XSSimpleTypeDecl xsSimpleTypeDecl8 = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xsSimpleTypeDecl7, true, null), "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl8.applyFacets1(xsFacets, 2, 0);
        symbolHash.put("IDREFS", xsSimpleTypeDecl8);
        final XSSimpleTypeDecl xsSimpleTypeDecl9 = new XSSimpleTypeDecl(xsSimpleTypeDecl6, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
        symbolHash.put("ENTITY", xsSimpleTypeDecl9);
        xsFacets.minLength = 1;
        final XSSimpleTypeDecl xsSimpleTypeDecl10 = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xsSimpleTypeDecl9, true, null), "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl10.applyFacets1(xsFacets, 2, 0);
        symbolHash.put("ENTITIES", xsSimpleTypeDecl10);
        xsFacets.whiteSpace = 2;
        final XSSimpleTypeDecl xsSimpleTypeDecl11 = new XSSimpleTypeDecl(xsSimpleTypeDecl3, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
        xsSimpleTypeDecl11.applyFacets1(xsFacets, 16, 0, (short)1);
        symbolHash.put("NMTOKEN", xsSimpleTypeDecl11);
        xsFacets.minLength = 1;
        final XSSimpleTypeDecl xsSimpleTypeDecl12 = new XSSimpleTypeDecl(new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, xsSimpleTypeDecl11, true, null), "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        xsSimpleTypeDecl12.applyFacets1(xsFacets, 2, 0);
        symbolHash.put("NMTOKENS", xsSimpleTypeDecl12);
    }
    
    static {
        createBuiltInTypes(FullDVFactory.fFullTypes = new SymbolHash(89));
    }
}
