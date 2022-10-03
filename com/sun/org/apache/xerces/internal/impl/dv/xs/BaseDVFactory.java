package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;

public class BaseDVFactory extends SchemaDVFactory
{
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fBaseTypes;
    
    @Override
    public XSSimpleType getBuiltInType(final String name) {
        return (XSSimpleType)BaseDVFactory.fBaseTypes.get(name);
    }
    
    @Override
    public SymbolHash getBuiltInTypes() {
        return BaseDVFactory.fBaseTypes.makeClone();
    }
    
    @Override
    public XSSimpleType createTypeRestriction(final String name, final String targetNamespace, final short finalSet, final XSSimpleType base, final XSObjectList annotations) {
        return new XSSimpleTypeDecl((XSSimpleTypeDecl)base, name, targetNamespace, finalSet, false, annotations);
    }
    
    @Override
    public XSSimpleType createTypeList(final String name, final String targetNamespace, final short finalSet, final XSSimpleType itemType, final XSObjectList annotations) {
        return new XSSimpleTypeDecl(name, targetNamespace, finalSet, (XSSimpleTypeDecl)itemType, false, annotations);
    }
    
    @Override
    public XSSimpleType createTypeUnion(final String name, final String targetNamespace, final short finalSet, final XSSimpleType[] memberTypes, final XSObjectList annotations) {
        final int typeNum = memberTypes.length;
        final XSSimpleTypeDecl[] mtypes = new XSSimpleTypeDecl[typeNum];
        System.arraycopy(memberTypes, 0, mtypes, 0, typeNum);
        return new XSSimpleTypeDecl(name, targetNamespace, finalSet, mtypes, annotations);
    }
    
    static void createBuiltInTypes(final SymbolHash types) {
        final String ANYSIMPLETYPE = "anySimpleType";
        final String ANYURI = "anyURI";
        final String BASE64BINARY = "base64Binary";
        final String BOOLEAN = "boolean";
        final String BYTE = "byte";
        final String DATE = "date";
        final String DATETIME = "dateTime";
        final String DAY = "gDay";
        final String DECIMAL = "decimal";
        final String INT = "int";
        final String INTEGER = "integer";
        final String LONG = "long";
        final String NEGATIVEINTEGER = "negativeInteger";
        final String MONTH = "gMonth";
        final String MONTHDAY = "gMonthDay";
        final String NONNEGATIVEINTEGER = "nonNegativeInteger";
        final String NONPOSITIVEINTEGER = "nonPositiveInteger";
        final String POSITIVEINTEGER = "positiveInteger";
        final String SHORT = "short";
        final String STRING = "string";
        final String TIME = "time";
        final String UNSIGNEDBYTE = "unsignedByte";
        final String UNSIGNEDINT = "unsignedInt";
        final String UNSIGNEDLONG = "unsignedLong";
        final String UNSIGNEDSHORT = "unsignedShort";
        final String YEAR = "gYear";
        final String YEARMONTH = "gYearMonth";
        final XSFacets facets = new XSFacets();
        final XSSimpleTypeDecl anySimpleType = XSSimpleTypeDecl.fAnySimpleType;
        types.put("anySimpleType", anySimpleType);
        final XSSimpleTypeDecl stringDV = new XSSimpleTypeDecl(anySimpleType, "string", (short)1, (short)0, false, false, false, true, (short)2);
        types.put("string", stringDV);
        types.put("boolean", new XSSimpleTypeDecl(anySimpleType, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
        final XSSimpleTypeDecl decimalDV = new XSSimpleTypeDecl(anySimpleType, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
        types.put("decimal", decimalDV);
        types.put("anyURI", new XSSimpleTypeDecl(anySimpleType, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
        types.put("base64Binary", new XSSimpleTypeDecl(anySimpleType, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
        types.put("dateTime", new XSSimpleTypeDecl(anySimpleType, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
        types.put("time", new XSSimpleTypeDecl(anySimpleType, "time", (short)8, (short)1, false, false, false, true, (short)9));
        types.put("date", new XSSimpleTypeDecl(anySimpleType, "date", (short)9, (short)1, false, false, false, true, (short)10));
        types.put("gYearMonth", new XSSimpleTypeDecl(anySimpleType, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
        types.put("gYear", new XSSimpleTypeDecl(anySimpleType, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
        types.put("gMonthDay", new XSSimpleTypeDecl(anySimpleType, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
        types.put("gDay", new XSSimpleTypeDecl(anySimpleType, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
        types.put("gMonth", new XSSimpleTypeDecl(anySimpleType, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
        final XSSimpleTypeDecl integerDV = new XSSimpleTypeDecl(decimalDV, "integer", (short)24, (short)2, false, false, true, true, (short)30);
        types.put("integer", integerDV);
        facets.maxInclusive = "0";
        final XSSimpleTypeDecl nonPositiveDV = new XSSimpleTypeDecl(integerDV, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)31);
        nonPositiveDV.applyFacets1(facets, (short)32, (short)0);
        types.put("nonPositiveInteger", nonPositiveDV);
        facets.maxInclusive = "-1";
        final XSSimpleTypeDecl negativeDV = new XSSimpleTypeDecl(nonPositiveDV, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)32);
        negativeDV.applyFacets1(facets, (short)32, (short)0);
        types.put("negativeInteger", negativeDV);
        facets.maxInclusive = "9223372036854775807";
        facets.minInclusive = "-9223372036854775808";
        final XSSimpleTypeDecl longDV = new XSSimpleTypeDecl(integerDV, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)33);
        longDV.applyFacets1(facets, (short)288, (short)0);
        types.put("long", longDV);
        facets.maxInclusive = "2147483647";
        facets.minInclusive = "-2147483648";
        final XSSimpleTypeDecl intDV = new XSSimpleTypeDecl(longDV, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)34);
        intDV.applyFacets1(facets, (short)288, (short)0);
        types.put("int", intDV);
        facets.maxInclusive = "32767";
        facets.minInclusive = "-32768";
        final XSSimpleTypeDecl shortDV = new XSSimpleTypeDecl(intDV, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)35);
        shortDV.applyFacets1(facets, (short)288, (short)0);
        types.put("short", shortDV);
        facets.maxInclusive = "127";
        facets.minInclusive = "-128";
        final XSSimpleTypeDecl byteDV = new XSSimpleTypeDecl(shortDV, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)36);
        byteDV.applyFacets1(facets, (short)288, (short)0);
        types.put("byte", byteDV);
        facets.minInclusive = "0";
        final XSSimpleTypeDecl nonNegativeDV = new XSSimpleTypeDecl(integerDV, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)37);
        nonNegativeDV.applyFacets1(facets, (short)256, (short)0);
        types.put("nonNegativeInteger", nonNegativeDV);
        facets.maxInclusive = "18446744073709551615";
        final XSSimpleTypeDecl unsignedLongDV = new XSSimpleTypeDecl(nonNegativeDV, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)38);
        unsignedLongDV.applyFacets1(facets, (short)32, (short)0);
        types.put("unsignedLong", unsignedLongDV);
        facets.maxInclusive = "4294967295";
        final XSSimpleTypeDecl unsignedIntDV = new XSSimpleTypeDecl(unsignedLongDV, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)39);
        unsignedIntDV.applyFacets1(facets, (short)32, (short)0);
        types.put("unsignedInt", unsignedIntDV);
        facets.maxInclusive = "65535";
        final XSSimpleTypeDecl unsignedShortDV = new XSSimpleTypeDecl(unsignedIntDV, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)40);
        unsignedShortDV.applyFacets1(facets, (short)32, (short)0);
        types.put("unsignedShort", unsignedShortDV);
        facets.maxInclusive = "255";
        final XSSimpleTypeDecl unsignedByteDV = new XSSimpleTypeDecl(unsignedShortDV, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)41);
        unsignedByteDV.applyFacets1(facets, (short)32, (short)0);
        types.put("unsignedByte", unsignedByteDV);
        facets.minInclusive = "1";
        final XSSimpleTypeDecl positiveIntegerDV = new XSSimpleTypeDecl(nonNegativeDV, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)42);
        positiveIntegerDV.applyFacets1(facets, (short)256, (short)0);
        types.put("positiveInteger", positiveIntegerDV);
    }
    
    static {
        createBuiltInTypes(BaseDVFactory.fBaseTypes = new SymbolHash(53));
    }
}
