package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import java.util.Iterator;
import java.util.List;
import org.apache.xerces.xs.XSMultiValueFacet;
import java.util.Vector;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidatorHelper;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public class XS11TypeHelper
{
    private XS11TypeHelper() {
    }
    
    public static boolean isSchemaTypesIdentical(final XSTypeDefinition xsTypeDefinition, final XSTypeDefinition xsTypeDefinition2) {
        boolean b = false;
        final String name = xsTypeDefinition.getName();
        final String name2 = xsTypeDefinition2.getName();
        if (("anyType".equals(name) && "anyType".equals(name2)) || ("anySimpleType".equals(name) && "anySimpleType".equals(name2))) {
            b = true;
        }
        if (!b && isURIEqual(xsTypeDefinition.getNamespace(), xsTypeDefinition2.getNamespace()) && ((name == null && name2 == null) || (name != null && name.equals(name2) && isSchemaTypesIdentical(xsTypeDefinition.getBaseType(), xsTypeDefinition2.getBaseType())))) {
            b = true;
        }
        return b;
    }
    
    public static boolean isURIEqual(final String s, final String s2) {
        return s == s2 || (s != null && s.equals(s2));
    }
    
    public static boolean isAtomicStrValueValidForSTUnion(final XSObjectList list, final String s, final ValidatedInfo validatedInfo, final short n) {
        boolean b = false;
        for (int i = 0; i < list.getLength(); ++i) {
            final XSSimpleType memberType = (XSSimpleType)list.item(i);
            if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(memberType.getNamespace()) && isStrValueValidForASimpleType(s, memberType, n)) {
                b = true;
                validatedInfo.memberType = memberType;
                break;
            }
        }
        return b;
    }
    
    public static boolean isStrValueValidForASimpleType(final String s, final XSSimpleType xsSimpleType, final short n) {
        boolean b = true;
        try {
            final ValidatedInfo validatedInfo = new ValidatedInfo();
            final ValidationState validationState = new ValidationState();
            validationState.setTypeValidatorHelper(TypeValidatorHelper.getInstance(n));
            xsSimpleType.validate(s, validationState, validatedInfo);
        }
        catch (final InvalidDatatypeValueException ex) {
            b = false;
        }
        return b;
    }
    
    public static void validateQNameValue(final String s, final NamespaceContext namespaceContext, final XMLErrorReporter xmlErrorReporter) {
        final String[] qnameString = parseQnameString(s);
        final String s2 = qnameString[0];
        final String s3 = qnameString[1];
        if ((s2.length() > 0 && !XMLChar.isValidNCName(s2)) || !XMLChar.isValidNCName(s3)) {
            xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-datatype-valid.1.2.1", new Object[] { s, "QName" }, (short)1);
        }
        final String uri = namespaceContext.getURI(s2.intern());
        if (s2.length() > 0 && uri == null) {
            xmlErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "UndeclaredPrefix", new Object[] { s, s2 }, (short)1);
        }
    }
    
    private static String[] parseQnameString(final String s) {
        final String[] array = new String[2];
        final int index = s.indexOf(58);
        String s2;
        String substring;
        if (index > 0) {
            s2 = s.substring(0, index);
            substring = s.substring(index + 1);
        }
        else {
            s2 = SchemaSymbols.EMPTY_STRING;
            substring = s;
        }
        array[0] = s2;
        array[1] = substring;
        return array;
    }
    
    public static Vector getAssertsFromSimpleType(final XSSimpleTypeDefinition xsSimpleTypeDefinition) {
        Vector asserts = new Vector();
        final XSObjectListImpl xsObjectListImpl = (XSObjectListImpl)xsSimpleTypeDefinition.getMultiValueFacets();
        for (int i = 0; i < xsObjectListImpl.getLength(); ++i) {
            final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)xsObjectListImpl.item(i);
            if (xsMultiValueFacet.getFacetKind() == 16384) {
                asserts = xsMultiValueFacet.getAsserts();
            }
        }
        return asserts;
    }
    
    public static boolean simpleTypeHasAsserts(final XSSimpleTypeDefinition xsSimpleTypeDefinition) {
        boolean b = false;
        final XSObjectList multiValueFacets = xsSimpleTypeDefinition.getMultiValueFacets();
        for (int length = multiValueFacets.getLength(), i = 0; i < length; ++i) {
            final XSMultiValueFacet xsMultiValueFacet = (XSMultiValueFacet)multiValueFacets.item(i);
            if (xsMultiValueFacet.getFacetKind() == 16384 && xsMultiValueFacet.getAsserts().size() > 0) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public static boolean isListContainsType(final List list, final XSTypeDefinition xsTypeDefinition) {
        boolean b = false;
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (isSchemaTypesIdentical((XSTypeDefinition)iterator.next(), xsTypeDefinition)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public static boolean isComplexTypeDerivedFromSTList(final XSComplexTypeDefinition xsComplexTypeDefinition, final short n) {
        final XSTypeDefinition baseType = xsComplexTypeDefinition.getBaseType();
        return xsComplexTypeDefinition.getDerivationMethod() == n && baseType instanceof XSSimpleTypeDefinition && ((XSSimpleTypeDefinition)baseType).getVariety() == 2;
    }
    
    public static String getSchemaTypeName(final XSTypeDefinition xsTypeDefinition) {
        String s;
        if (xsTypeDefinition instanceof XSSimpleTypeDefinition) {
            s = ((XSSimpleTypeDecl)xsTypeDefinition).getTypeName();
        }
        else {
            s = ((XSComplexTypeDecl)xsTypeDefinition).getTypeName();
        }
        return s;
    }
    
    public static boolean isSpecialSimpleType(final XSSimpleType xsSimpleType) {
        boolean b = false;
        final String name = xsSimpleType.getName();
        if (Constants.NS_XMLSCHEMA.equals(xsSimpleType.getNamespace()) && ("anyAtomicType".equals(name) || "anySimpleType".equals(name))) {
            b = true;
        }
        return b;
    }
    
    public static ResultSequence getXPath2ResultSequence(final List list) {
        final ResultSequence create_new = ResultSequenceFactory.create_new();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            create_new.add((AnyType)iterator.next());
        }
        return create_new;
    }
    
    public static boolean isTypeTablesComparable(final XSTypeAlternativeImpl[] array, final XSTypeAlternativeImpl[] array2) {
        boolean b = true;
        if (array == null && array2 == null) {
            b = false;
        }
        return b;
    }
}
