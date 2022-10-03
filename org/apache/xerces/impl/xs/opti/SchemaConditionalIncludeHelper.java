package org.apache.xerces.impl.xs.opti;

import java.util.Iterator;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.Constants;
import java.util.ArrayList;
import java.util.List;

public class SchemaConditionalIncludeHelper
{
    List typesSupported;
    List facetsSupported;
    
    public SchemaConditionalIncludeHelper() {
        this.typesSupported = null;
        this.facetsSupported = null;
        this.typesSupported = new ArrayList();
        this.facetsSupported = new ArrayList();
        this.initialize();
    }
    
    private void initialize() {
        this.initSupportedTypes();
        this.initSupportedFacets();
    }
    
    private void initSupportedTypes() {
        this.typesSupported.add(new QName(null, "anyType", "anyType", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "anySimpleType", "anySimpleType", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "anyAtomicType", "anyAtomicType", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "string", "string", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "boolean", "boolean", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "decimal", "decimal", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "precisionDecimal", "precisionDecimal", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "float", "float", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "double", "double", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "duration", "duration", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "dateTime", "dateTime", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "time", "time", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "date", "date", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "gYearMonth", "gYearMonth", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "gYear", "gYear", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "gMonthDay", "gMonthDay", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "gDay", "gDay", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "gMonth", "gMonth", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "hexBinary", "hexBinary", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "base64Binary", "base64Binary", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "anyURI", "anyURI", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "QName", "QName", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "NOTATION", "NOTATION", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "normalizedString", "normalizedString", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "token", "token", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "language", "language", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "NMTOKEN", "NMTOKEN", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "NMTOKENS", "NMTOKENS", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "Name", "Name", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "NCName", "NCName", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "ID", "ID", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "IDREF", "IDREF", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "IDREFS", "IDREFS", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "ENTITY", "ENTITY", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "ENTITIES", "ENTITIES", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "integer", "integer", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "nonPositiveInteger", "nonPositiveInteger", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "negativeInteger", "negativeInteger", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "long", "long", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "int", "int", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "short", "short", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "byte", "byte", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "nonNegativeInteger", "nonNegativeInteger", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "unsignedLong", "unsignedLong", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "unsignedInt", "unsignedInt", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "unsignedShort", "unsignedShort", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "unsignedByte", "unsignedByte", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "positiveInteger", "positiveInteger", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "yearMonthDuration", "yearMonthDuration", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "dayTimeDuration", "dayTimeDuration", Constants.NS_XMLSCHEMA));
        this.typesSupported.add(new QName(null, "dateTimeStamp", "dateTimeStamp", Constants.NS_XMLSCHEMA));
    }
    
    private void initSupportedFacets() {
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_LENGTH, SchemaSymbols.ELT_LENGTH, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MINLENGTH, SchemaSymbols.ELT_MINLENGTH, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MAXLENGTH, SchemaSymbols.ELT_MAXLENGTH, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_PATTERN, SchemaSymbols.ELT_PATTERN, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ELT_ENUMERATION, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_WHITESPACE, SchemaSymbols.ELT_WHITESPACE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MAXINCLUSIVE, SchemaSymbols.ELT_MAXINCLUSIVE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MAXEXCLUSIVE, SchemaSymbols.ELT_MAXEXCLUSIVE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MININCLUSIVE, SchemaSymbols.ELT_MININCLUSIVE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_TOTALDIGITS, SchemaSymbols.ELT_TOTALDIGITS, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MINEXCLUSIVE, SchemaSymbols.ELT_MINEXCLUSIVE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_FRACTIONDIGITS, SchemaSymbols.ELT_FRACTIONDIGITS, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MAXSCALE, SchemaSymbols.ELT_MAXSCALE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_MINSCALE, SchemaSymbols.ELT_MINSCALE, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_ASSERTION, SchemaSymbols.ELT_ASSERTION, Constants.NS_XMLSCHEMA));
        this.facetsSupported.add(new QName(null, SchemaSymbols.ELT_EXPLICITTIMEZONE, SchemaSymbols.ELT_EXPLICITTIMEZONE, Constants.NS_XMLSCHEMA));
    }
    
    public boolean isTypeSupported(final String s, final String s2) {
        boolean b = false;
        final Iterator iterator = this.typesSupported.iterator();
        while (iterator.hasNext()) {
            final QName qName = (QName)iterator.next();
            if (qName.localpart.equals(s) && qName.uri.equals(s2)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public boolean isFacetSupported(final String s, final String s2) {
        boolean b = false;
        final Iterator iterator = this.facetsSupported.iterator();
        while (iterator.hasNext()) {
            final QName qName = (QName)iterator.next();
            if (qName.localpart.equals(s) && qName.uri.equals(s2)) {
                b = true;
                break;
            }
        }
        return b;
    }
}
