package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;

public abstract class BaseSchemaDVFactory extends SchemaDVFactory
{
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    protected XSDeclarationPool fDeclPool;
    
    public BaseSchemaDVFactory() {
        this.fDeclPool = null;
    }
    
    protected static void createBuiltInTypes(final SymbolHash builtInTypes, final XSSimpleTypeDecl baseAtomicType) {
        final String ANYSIMPLETYPE = "anySimpleType";
        final String ANYURI = "anyURI";
        final String BASE64BINARY = "base64Binary";
        final String BOOLEAN = "boolean";
        final String BYTE = "byte";
        final String DATE = "date";
        final String DATETIME = "dateTime";
        final String DAY = "gDay";
        final String DECIMAL = "decimal";
        final String DOUBLE = "double";
        final String DURATION = "duration";
        final String ENTITY = "ENTITY";
        final String ENTITIES = "ENTITIES";
        final String FLOAT = "float";
        final String HEXBINARY = "hexBinary";
        final String ID = "ID";
        final String IDREF = "IDREF";
        final String IDREFS = "IDREFS";
        final String INT = "int";
        final String INTEGER = "integer";
        final String LONG = "long";
        final String NAME = "Name";
        final String NEGATIVEINTEGER = "negativeInteger";
        final String MONTH = "gMonth";
        final String MONTHDAY = "gMonthDay";
        final String NCNAME = "NCName";
        final String NMTOKEN = "NMTOKEN";
        final String NMTOKENS = "NMTOKENS";
        final String LANGUAGE = "language";
        final String NONNEGATIVEINTEGER = "nonNegativeInteger";
        final String NONPOSITIVEINTEGER = "nonPositiveInteger";
        final String NORMALIZEDSTRING = "normalizedString";
        final String NOTATION = "NOTATION";
        final String POSITIVEINTEGER = "positiveInteger";
        final String QNAME = "QName";
        final String SHORT = "short";
        final String STRING = "string";
        final String TIME = "time";
        final String TOKEN = "token";
        final String UNSIGNEDBYTE = "unsignedByte";
        final String UNSIGNEDINT = "unsignedInt";
        final String UNSIGNEDLONG = "unsignedLong";
        final String UNSIGNEDSHORT = "unsignedShort";
        final String YEAR = "gYear";
        final String YEARMONTH = "gYearMonth";
        final XSFacets facets = new XSFacets();
        builtInTypes.put("anySimpleType", XSSimpleTypeDecl.fAnySimpleType);
        final XSSimpleTypeDecl stringDV = new XSSimpleTypeDecl(baseAtomicType, "string", (short)1, (short)0, false, false, false, true, (short)2);
        builtInTypes.put("string", stringDV);
        builtInTypes.put("boolean", new XSSimpleTypeDecl(baseAtomicType, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
        final XSSimpleTypeDecl decimalDV = new XSSimpleTypeDecl(baseAtomicType, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
        builtInTypes.put("decimal", decimalDV);
        builtInTypes.put("anyURI", new XSSimpleTypeDecl(baseAtomicType, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
        builtInTypes.put("base64Binary", new XSSimpleTypeDecl(baseAtomicType, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
        final XSSimpleTypeDecl durationDV = new XSSimpleTypeDecl(baseAtomicType, "duration", (short)6, (short)1, false, false, false, true, (short)7);
        builtInTypes.put("duration", durationDV);
        builtInTypes.put("dateTime", new XSSimpleTypeDecl(baseAtomicType, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
        builtInTypes.put("time", new XSSimpleTypeDecl(baseAtomicType, "time", (short)8, (short)1, false, false, false, true, (short)9));
        builtInTypes.put("date", new XSSimpleTypeDecl(baseAtomicType, "date", (short)9, (short)1, false, false, false, true, (short)10));
        builtInTypes.put("gYearMonth", new XSSimpleTypeDecl(baseAtomicType, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
        builtInTypes.put("gYear", new XSSimpleTypeDecl(baseAtomicType, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
        builtInTypes.put("gMonthDay", new XSSimpleTypeDecl(baseAtomicType, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
        builtInTypes.put("gDay", new XSSimpleTypeDecl(baseAtomicType, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
        builtInTypes.put("gMonth", new XSSimpleTypeDecl(baseAtomicType, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
        final XSSimpleTypeDecl integerDV = new XSSimpleTypeDecl(decimalDV, "integer", (short)24, (short)2, false, false, true, true, (short)30);
        builtInTypes.put("integer", integerDV);
        facets.maxInclusive = "0";
        final XSSimpleTypeDecl nonPositiveDV = new XSSimpleTypeDecl(integerDV, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)31);
        nonPositiveDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("nonPositiveInteger", nonPositiveDV);
        facets.maxInclusive = "-1";
        final XSSimpleTypeDecl negativeDV = new XSSimpleTypeDecl(nonPositiveDV, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)32);
        negativeDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("negativeInteger", negativeDV);
        facets.maxInclusive = "9223372036854775807";
        facets.minInclusive = "-9223372036854775808";
        final XSSimpleTypeDecl longDV = new XSSimpleTypeDecl(integerDV, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)33);
        longDV.applyFacets1(facets, (short)288, (short)0);
        builtInTypes.put("long", longDV);
        facets.maxInclusive = "2147483647";
        facets.minInclusive = "-2147483648";
        final XSSimpleTypeDecl intDV = new XSSimpleTypeDecl(longDV, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)34);
        intDV.applyFacets1(facets, (short)288, (short)0);
        builtInTypes.put("int", intDV);
        facets.maxInclusive = "32767";
        facets.minInclusive = "-32768";
        final XSSimpleTypeDecl shortDV = new XSSimpleTypeDecl(intDV, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)35);
        shortDV.applyFacets1(facets, (short)288, (short)0);
        builtInTypes.put("short", shortDV);
        facets.maxInclusive = "127";
        facets.minInclusive = "-128";
        final XSSimpleTypeDecl byteDV = new XSSimpleTypeDecl(shortDV, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)36);
        byteDV.applyFacets1(facets, (short)288, (short)0);
        builtInTypes.put("byte", byteDV);
        facets.minInclusive = "0";
        final XSSimpleTypeDecl nonNegativeDV = new XSSimpleTypeDecl(integerDV, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)37);
        nonNegativeDV.applyFacets1(facets, (short)256, (short)0);
        builtInTypes.put("nonNegativeInteger", nonNegativeDV);
        facets.maxInclusive = "18446744073709551615";
        final XSSimpleTypeDecl unsignedLongDV = new XSSimpleTypeDecl(nonNegativeDV, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)38);
        unsignedLongDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("unsignedLong", unsignedLongDV);
        facets.maxInclusive = "4294967295";
        final XSSimpleTypeDecl unsignedIntDV = new XSSimpleTypeDecl(unsignedLongDV, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)39);
        unsignedIntDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("unsignedInt", unsignedIntDV);
        facets.maxInclusive = "65535";
        final XSSimpleTypeDecl unsignedShortDV = new XSSimpleTypeDecl(unsignedIntDV, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)40);
        unsignedShortDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("unsignedShort", unsignedShortDV);
        facets.maxInclusive = "255";
        final XSSimpleTypeDecl unsignedByteDV = new XSSimpleTypeDecl(unsignedShortDV, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)41);
        unsignedByteDV.applyFacets1(facets, (short)32, (short)0);
        builtInTypes.put("unsignedByte", unsignedByteDV);
        facets.minInclusive = "1";
        final XSSimpleTypeDecl positiveIntegerDV = new XSSimpleTypeDecl(nonNegativeDV, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)42);
        positiveIntegerDV.applyFacets1(facets, (short)256, (short)0);
        builtInTypes.put("positiveInteger", positiveIntegerDV);
        builtInTypes.put("float", new XSSimpleTypeDecl(baseAtomicType, "float", (short)4, (short)1, true, true, true, true, (short)5));
        builtInTypes.put("double", new XSSimpleTypeDecl(baseAtomicType, "double", (short)5, (short)1, true, true, true, true, (short)6));
        builtInTypes.put("hexBinary", new XSSimpleTypeDecl(baseAtomicType, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
        builtInTypes.put("NOTATION", new XSSimpleTypeDecl(baseAtomicType, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
        facets.whiteSpace = 1;
        final XSSimpleTypeDecl normalizedDV = new XSSimpleTypeDecl(stringDV, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
        normalizedDV.applyFacets1(facets, (short)16, (short)0);
        builtInTypes.put("normalizedString", normalizedDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl tokenDV = new XSSimpleTypeDecl(normalizedDV, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
        tokenDV.applyFacets1(facets, (short)16, (short)0);
        builtInTypes.put("token", tokenDV);
        facets.whiteSpace = 2;
        facets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
        final XSSimpleTypeDecl languageDV = new XSSimpleTypeDecl(tokenDV, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
        languageDV.applyFacets1(facets, (short)24, (short)0);
        builtInTypes.put("language", languageDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl nameDV = new XSSimpleTypeDecl(tokenDV, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
        nameDV.applyFacets1(facets, (short)16, (short)0, (short)2);
        builtInTypes.put("Name", nameDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl ncnameDV = new XSSimpleTypeDecl(nameDV, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
        ncnameDV.applyFacets1(facets, (short)16, (short)0, (short)3);
        builtInTypes.put("NCName", ncnameDV);
        builtInTypes.put("QName", new XSSimpleTypeDecl(baseAtomicType, "QName", (short)18, (short)0, false, false, false, true, (short)19));
        builtInTypes.put("ID", new XSSimpleTypeDecl(ncnameDV, "ID", (short)21, (short)0, false, false, false, true, (short)27));
        final XSSimpleTypeDecl idrefDV = new XSSimpleTypeDecl(ncnameDV, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
        builtInTypes.put("IDREF", idrefDV);
        facets.minLength = 1;
        XSSimpleTypeDecl tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, idrefDV, true, null);
        final XSSimpleTypeDecl idrefsDV = new XSSimpleTypeDecl(tempDV, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        idrefsDV.applyFacets1(facets, (short)2, (short)0);
        builtInTypes.put("IDREFS", idrefsDV);
        final XSSimpleTypeDecl entityDV = new XSSimpleTypeDecl(ncnameDV, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
        builtInTypes.put("ENTITY", entityDV);
        facets.minLength = 1;
        tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, entityDV, true, null);
        final XSSimpleTypeDecl entitiesDV = new XSSimpleTypeDecl(tempDV, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        entitiesDV.applyFacets1(facets, (short)2, (short)0);
        builtInTypes.put("ENTITIES", entitiesDV);
        facets.whiteSpace = 2;
        final XSSimpleTypeDecl nmtokenDV = new XSSimpleTypeDecl(tokenDV, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
        nmtokenDV.applyFacets1(facets, (short)16, (short)0, (short)1);
        builtInTypes.put("NMTOKEN", nmtokenDV);
        facets.minLength = 1;
        tempDV = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, nmtokenDV, true, null);
        final XSSimpleTypeDecl nmtokensDV = new XSSimpleTypeDecl(tempDV, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
        nmtokensDV.applyFacets1(facets, (short)2, (short)0);
        builtInTypes.put("NMTOKENS", nmtokensDV);
    }
    
    @Override
    public XSSimpleType createTypeRestriction(final String name, final String targetNamespace, final short finalSet, final XSSimpleType base, final XSObjectList annotations) {
        if (this.fDeclPool != null) {
            final XSSimpleTypeDecl st = this.fDeclPool.getSimpleTypeDecl();
            return st.setRestrictionValues((XSSimpleTypeDecl)base, name, targetNamespace, finalSet, annotations);
        }
        return new XSSimpleTypeDecl((XSSimpleTypeDecl)base, name, targetNamespace, finalSet, false, annotations);
    }
    
    @Override
    public XSSimpleType createTypeList(final String name, final String targetNamespace, final short finalSet, final XSSimpleType itemType, final XSObjectList annotations) {
        if (this.fDeclPool != null) {
            final XSSimpleTypeDecl st = this.fDeclPool.getSimpleTypeDecl();
            return st.setListValues(name, targetNamespace, finalSet, (XSSimpleTypeDecl)itemType, annotations);
        }
        return new XSSimpleTypeDecl(name, targetNamespace, finalSet, (XSSimpleTypeDecl)itemType, false, annotations);
    }
    
    @Override
    public XSSimpleType createTypeUnion(final String name, final String targetNamespace, final short finalSet, final XSSimpleType[] memberTypes, final XSObjectList annotations) {
        final int typeNum = memberTypes.length;
        final XSSimpleTypeDecl[] mtypes = new XSSimpleTypeDecl[typeNum];
        System.arraycopy(memberTypes, 0, mtypes, 0, typeNum);
        if (this.fDeclPool != null) {
            final XSSimpleTypeDecl st = this.fDeclPool.getSimpleTypeDecl();
            return st.setUnionValues(name, targetNamespace, finalSet, mtypes, annotations);
        }
        return new XSSimpleTypeDecl(name, targetNamespace, finalSet, mtypes, annotations);
    }
    
    public void setDeclPool(final XSDeclarationPool declPool) {
        this.fDeclPool = declPool;
    }
    
    public XSSimpleTypeDecl newXSSimpleTypeDecl() {
        return new XSSimpleTypeDecl();
    }
}
