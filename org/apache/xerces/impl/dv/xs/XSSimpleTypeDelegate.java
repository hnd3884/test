package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.XSSimpleType;

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
    
    public XSObjectList getAnnotations() {
        return this.type.getAnnotations();
    }
    
    public boolean getBounded() {
        return this.type.getBounded();
    }
    
    public short getBuiltInKind() {
        return this.type.getBuiltInKind();
    }
    
    public short getDefinedFacets() {
        return this.type.getDefinedFacets();
    }
    
    public XSObjectList getFacets() {
        return this.type.getFacets();
    }
    
    public XSObject getFacet(final int n) {
        return this.type.getFacet(n);
    }
    
    public boolean getFinite() {
        return this.type.getFinite();
    }
    
    public short getFixedFacets() {
        return this.type.getFixedFacets();
    }
    
    public XSSimpleTypeDefinition getItemType() {
        return this.type.getItemType();
    }
    
    public StringList getLexicalEnumeration() {
        return this.type.getLexicalEnumeration();
    }
    
    public String getLexicalFacetValue(final short n) {
        return this.type.getLexicalFacetValue(n);
    }
    
    public StringList getLexicalPattern() {
        return this.type.getLexicalPattern();
    }
    
    public XSObjectList getMemberTypes() {
        return this.type.getMemberTypes();
    }
    
    public XSObjectList getMultiValueFacets() {
        return this.type.getMultiValueFacets();
    }
    
    public boolean getNumeric() {
        return this.type.getNumeric();
    }
    
    public short getOrdered() {
        return this.type.getOrdered();
    }
    
    public XSSimpleTypeDefinition getPrimitiveType() {
        return this.type.getPrimitiveType();
    }
    
    public short getVariety() {
        return this.type.getVariety();
    }
    
    public boolean isDefinedFacet(final short n) {
        return this.type.isDefinedFacet(n);
    }
    
    public boolean isFixedFacet(final short n) {
        return this.type.isFixedFacet(n);
    }
    
    public boolean derivedFrom(final String s, final String s2, final short n) {
        return this.type.derivedFrom(s, s2, n);
    }
    
    public boolean derivedFromType(final XSTypeDefinition xsTypeDefinition, final short n) {
        return this.type.derivedFromType(xsTypeDefinition, n);
    }
    
    public boolean getAnonymous() {
        return this.type.getAnonymous();
    }
    
    public XSTypeDefinition getBaseType() {
        return this.type.getBaseType();
    }
    
    public short getFinal() {
        return this.type.getFinal();
    }
    
    public short getTypeCategory() {
        return this.type.getTypeCategory();
    }
    
    public boolean isFinal(final short n) {
        return this.type.isFinal(n);
    }
    
    public String getName() {
        return this.type.getName();
    }
    
    public String getNamespace() {
        return this.type.getNamespace();
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return this.type.getNamespaceItem();
    }
    
    public short getType() {
        return this.type.getType();
    }
    
    public void applyFacets(final XSFacets xsFacets, final int n, final int n2, final ValidationContext validationContext) throws InvalidDatatypeFacetException {
        this.type.applyFacets(xsFacets, n, n2, validationContext);
    }
    
    public short getPrimitiveKind() {
        return this.type.getPrimitiveKind();
    }
    
    public short getWhitespace() throws DatatypeException {
        return this.type.getWhitespace();
    }
    
    public boolean isEqual(final Object o, final Object o2) {
        return this.type.isEqual(o, o2);
    }
    
    public boolean isIDType() {
        return this.type.isIDType();
    }
    
    public void validate(final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        this.type.validate(validationContext, validatedInfo);
    }
    
    public Object validate(final String s, final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(s, validationContext, validatedInfo);
    }
    
    public Object validate(final Object o, final ValidationContext validationContext, final ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.type.validate(o, validationContext, validatedInfo);
    }
    
    public XSObject getContext() {
        return this.type.getContext();
    }
    
    public String toString() {
        return this.type.toString();
    }
}
