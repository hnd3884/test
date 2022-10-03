package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.impl.dv.SchemaDVFactory;

public class BaseDVFactory extends SchemaDVFactory
{
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fBaseTypes;
    
    public XSSimpleType getBuiltInType(final String s) {
        return (XSSimpleType)BaseDVFactory.fBaseTypes.get(s);
    }
    
    public SymbolHash getBuiltInTypes() {
        return BaseDVFactory.fBaseTypes.makeClone();
    }
    
    public XSSimpleType createTypeRestriction(final String s, final String s2, final short n, final XSSimpleType xsSimpleType, final XSObjectList list) {
        return new XSSimpleTypeDecl((XSSimpleTypeDecl)xsSimpleType, s, s2, n, false, list);
    }
    
    public XSSimpleType createTypeList(final String s, final String s2, final short n, final XSSimpleType xsSimpleType, final XSObjectList list) {
        return new XSSimpleTypeDecl(s, s2, n, (XSSimpleTypeDecl)xsSimpleType, false, list);
    }
    
    public XSSimpleType createTypeUnion(final String s, final String s2, final short n, final XSSimpleType[] array, final XSObjectList list) {
        final int length = array.length;
        final XSSimpleTypeDecl[] array2 = new XSSimpleTypeDecl[length];
        System.arraycopy(array, 0, array2, 0, length);
        return new XSSimpleTypeDecl(s, s2, n, array2, list);
    }
    
    static void createBuiltInTypes(final SymbolHash symbolHash) {
        final XSFacets xsFacets = new XSFacets();
        final XSSimpleTypeDecl fAnySimpleType = XSSimpleTypeDecl.fAnySimpleType;
        symbolHash.put("anySimpleType", fAnySimpleType);
        symbolHash.put("string", new XSSimpleTypeDecl(fAnySimpleType, "string", (short)1, (short)0, false, false, false, true, (short)2));
        symbolHash.put("boolean", new XSSimpleTypeDecl(fAnySimpleType, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
        final XSSimpleTypeDecl xsSimpleTypeDecl = new XSSimpleTypeDecl(fAnySimpleType, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
        symbolHash.put("decimal", xsSimpleTypeDecl);
        symbolHash.put("anyURI", new XSSimpleTypeDecl(fAnySimpleType, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
        symbolHash.put("base64Binary", new XSSimpleTypeDecl(fAnySimpleType, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
        symbolHash.put("dateTime", new XSSimpleTypeDecl(fAnySimpleType, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
        symbolHash.put("time", new XSSimpleTypeDecl(fAnySimpleType, "time", (short)8, (short)1, false, false, false, true, (short)9));
        symbolHash.put("date", new XSSimpleTypeDecl(fAnySimpleType, "date", (short)9, (short)1, false, false, false, true, (short)10));
        symbolHash.put("gYearMonth", new XSSimpleTypeDecl(fAnySimpleType, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
        symbolHash.put("gYear", new XSSimpleTypeDecl(fAnySimpleType, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
        symbolHash.put("gMonthDay", new XSSimpleTypeDecl(fAnySimpleType, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
        symbolHash.put("gDay", new XSSimpleTypeDecl(fAnySimpleType, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
        symbolHash.put("gMonth", new XSSimpleTypeDecl(fAnySimpleType, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
        final XSSimpleTypeDecl xsSimpleTypeDecl2 = new XSSimpleTypeDecl(xsSimpleTypeDecl, "integer", (short)24, (short)2, false, false, true, true, (short)30);
        symbolHash.put("integer", xsSimpleTypeDecl2);
        xsFacets.maxInclusive = "0";
        final XSSimpleTypeDecl xsSimpleTypeDecl3 = new XSSimpleTypeDecl(xsSimpleTypeDecl2, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)31);
        xsSimpleTypeDecl3.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("nonPositiveInteger", xsSimpleTypeDecl3);
        xsFacets.maxInclusive = "-1";
        final XSSimpleTypeDecl xsSimpleTypeDecl4 = new XSSimpleTypeDecl(xsSimpleTypeDecl3, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)32);
        xsSimpleTypeDecl4.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("negativeInteger", xsSimpleTypeDecl4);
        xsFacets.maxInclusive = "9223372036854775807";
        xsFacets.minInclusive = "-9223372036854775808";
        final XSSimpleTypeDecl xsSimpleTypeDecl5 = new XSSimpleTypeDecl(xsSimpleTypeDecl2, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)33);
        xsSimpleTypeDecl5.applyFacets1(xsFacets, 288, 0);
        symbolHash.put("long", xsSimpleTypeDecl5);
        xsFacets.maxInclusive = "2147483647";
        xsFacets.minInclusive = "-2147483648";
        final XSSimpleTypeDecl xsSimpleTypeDecl6 = new XSSimpleTypeDecl(xsSimpleTypeDecl5, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)34);
        xsSimpleTypeDecl6.applyFacets1(xsFacets, 288, 0);
        symbolHash.put("int", xsSimpleTypeDecl6);
        xsFacets.maxInclusive = "32767";
        xsFacets.minInclusive = "-32768";
        final XSSimpleTypeDecl xsSimpleTypeDecl7 = new XSSimpleTypeDecl(xsSimpleTypeDecl6, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)35);
        xsSimpleTypeDecl7.applyFacets1(xsFacets, 288, 0);
        symbolHash.put("short", xsSimpleTypeDecl7);
        xsFacets.maxInclusive = "127";
        xsFacets.minInclusive = "-128";
        final XSSimpleTypeDecl xsSimpleTypeDecl8 = new XSSimpleTypeDecl(xsSimpleTypeDecl7, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)36);
        xsSimpleTypeDecl8.applyFacets1(xsFacets, 288, 0);
        symbolHash.put("byte", xsSimpleTypeDecl8);
        xsFacets.minInclusive = "0";
        final XSSimpleTypeDecl xsSimpleTypeDecl9 = new XSSimpleTypeDecl(xsSimpleTypeDecl2, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)37);
        xsSimpleTypeDecl9.applyFacets1(xsFacets, 256, 0);
        symbolHash.put("nonNegativeInteger", xsSimpleTypeDecl9);
        xsFacets.maxInclusive = "18446744073709551615";
        final XSSimpleTypeDecl xsSimpleTypeDecl10 = new XSSimpleTypeDecl(xsSimpleTypeDecl9, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)38);
        xsSimpleTypeDecl10.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("unsignedLong", xsSimpleTypeDecl10);
        xsFacets.maxInclusive = "4294967295";
        final XSSimpleTypeDecl xsSimpleTypeDecl11 = new XSSimpleTypeDecl(xsSimpleTypeDecl10, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)39);
        xsSimpleTypeDecl11.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("unsignedInt", xsSimpleTypeDecl11);
        xsFacets.maxInclusive = "65535";
        final XSSimpleTypeDecl xsSimpleTypeDecl12 = new XSSimpleTypeDecl(xsSimpleTypeDecl11, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)40);
        xsSimpleTypeDecl12.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("unsignedShort", xsSimpleTypeDecl12);
        xsFacets.maxInclusive = "255";
        final XSSimpleTypeDecl xsSimpleTypeDecl13 = new XSSimpleTypeDecl(xsSimpleTypeDecl12, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)41);
        xsSimpleTypeDecl13.applyFacets1(xsFacets, 32, 0);
        symbolHash.put("unsignedByte", xsSimpleTypeDecl13);
        xsFacets.minInclusive = "1";
        final XSSimpleTypeDecl xsSimpleTypeDecl14 = new XSSimpleTypeDecl(xsSimpleTypeDecl9, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)42);
        xsSimpleTypeDecl14.applyFacets1(xsFacets, 256, 0);
        symbolHash.put("positiveInteger", xsSimpleTypeDecl14);
    }
    
    static {
        createBuiltInTypes(BaseDVFactory.fBaseTypes = new SymbolHash(53));
    }
}
