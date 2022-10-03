package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;

public class XSSimpleTypeDelegate implements XSSimpleType
{
    protected final XSSimpleType type;
    
    public XSSimpleTypeDelegate(final XSSimpleType type) {
        if (type == null) {
            throw new NullPointerException();
        }
        this.type = type;
    }
    
    public XSSimpleType getWrappedXSSimpleType() {
        return this.type;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return this.type.getAnnotations();
    }
    
    @Override
    public boolean getBounded() {
        return this.type.getBounded();
    }
    
    @Override
    public short getBuiltInKind() {
        return this.type.getBuiltInKind();
    }
    
    @Override
    public short getDefinedFacets() {
        return this.type.getDefinedFacets();
    }
    
    @Override
    public XSObjectList getFacets() {
        return this.type.getFacets();
    }
    
    @Override
    public boolean getFinite() {
        return this.type.getFinite();
    }
    
    @Override
    public short getFixedFacets() {
        return this.type.getFixedFacets();
    }
    
    @Override
    public XSSimpleTypeDefinition getItemType() {
        return this.type.getItemType();
    }
    
    @Override
    public StringList getLexicalEnumeration() {
        return this.type.getLexicalEnumeration();
    }
    
    @Override
    public String getLexicalFacetValue(final short facetName) {
        return this.type.getLexicalFacetValue(facetName);
    }
    
    @Override
    public StringList getLexicalPattern() {
        return this.type.getLexicalPattern();
    }
    
    @Override
    public XSObjectList getMemberTypes() {
        return this.type.getMemberTypes();
    }
    
    @Override
    public XSObjectList getMultiValueFacets() {
        return this.type.getMultiValueFacets();
    }
    
    @Override
    public boolean getNumeric() {
        return this.type.getNumeric();
    }
    
    @Override
    public short getOrdered() {
        return this.type.getOrdered();
    }
    
    @Override
    public XSSimpleTypeDefinition getPrimitiveType() {
        return this.type.getPrimitiveType();
    }
    
    @Override
    public short getVariety() {
        return this.type.getVariety();
    }
    
    @Override
    public boolean isDefinedFacet(final short facetName) {
        return this.type.isDefinedFacet(facetName);
    }
    
    @Override
    public boolean isFixedFacet(final short facetName) {
        return this.type.isFixedFacet(facetName);
    }
    
    @Override
    public boolean derivedFrom(final String namespace, final String name, final short derivationMethod) {
        return this.type.derivedFrom(namespace, name, derivationMethod);
    }
    
    @Override
    public boolean derivedFromType(final XSTypeDefinition ancestorType, final short derivationMethod) {
        return this.type.derivedFromType(ancestorType, derivationMethod);
    }
    
    @Override
    public boolean getAnonymous() {
        return this.type.getAnonymous();
    }
    
    @Override
    public XSTypeDefinition getBaseType() {
        return this.type.getBaseType();
    }
    
    @Override
    public short getFinal() {
        return this.type.getFinal();
    }
    
    @Override
    public short getTypeCategory() {
        return this.type.getTypeCategory();
    }
    
    @Override
    public boolean isFinal(final short restriction) {
        return this.type.isFinal(restriction);
    }
    
    @Override
    public String getName() {
        return this.type.getName();
    }
    
    @Override
    public String getNamespace() {
        return this.type.getNamespace();
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.type.getNamespaceItem();
    }
    
    @Override
    public short getType() {
        return this.type.getType();
    }
    
    @Override
    public void applyFacets(final XSFacets facets, final short presentFacet, final short fixedFacet, final ValidationContext context) throws InvalidDatatypeFacetException {
        this.type.applyFacets(facets, presentFacet, fixedFacet, context);
    }
    
    @Override
    public short getPrimitiveKind() {
        return this.type.getPrimitiveKind();
    }
    
    @Override
    public short getWhitespace() throws DatatypeException {
        return this.type.getWhitespace();
    }
    
    @Override
    public boolean isEqual(final Object value1, final Object value2) {
        return this.type.isEqual(value1, value2);
    }
    
    @Override
    public boolean isIDType() {
        return this.type.isIDType();
    }
    
    @Override
    public void validate(final ValidationContext context, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        this.type.validate(context, validatedInfo);
    }
    
    @Override
    public Object validate(final String content, final ValidationContext context, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(content, context, validatedInfo);
    }
    
    @Override
    public Object validate(final Object content, final ValidationContext context, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(content, context, validatedInfo);
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
}
