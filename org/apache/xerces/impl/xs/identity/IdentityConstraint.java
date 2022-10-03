package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.xs.XSIDCDefinition;

public abstract class IdentityConstraint implements XSIDCDefinition
{
    protected short type;
    protected final String fNamespace;
    protected final String fIdentityConstraintName;
    protected Selector fSelector;
    protected int fFieldCount;
    protected Field[] fFields;
    protected XSAnnotationImpl[] fAnnotations;
    protected int fNumAnnotations;
    
    protected IdentityConstraint(final String fNamespace, final String fIdentityConstraintName) {
        this.fAnnotations = null;
        this.fNamespace = fNamespace;
        this.fIdentityConstraintName = fIdentityConstraintName;
    }
    
    public String getIdentityConstraintName() {
        return this.fIdentityConstraintName;
    }
    
    public void setSelector(final Selector fSelector) {
        this.fSelector = fSelector;
    }
    
    public Selector getSelector() {
        return this.fSelector;
    }
    
    public void addField(final Field field) {
        if (this.fFields == null) {
            this.fFields = new Field[4];
        }
        else if (this.fFieldCount == this.fFields.length) {
            this.fFields = resize(this.fFields, this.fFieldCount * 2);
        }
        this.fFields[this.fFieldCount++] = field;
    }
    
    public int getFieldCount() {
        return this.fFieldCount;
    }
    
    public Field getFieldAt(final int n) {
        return this.fFields[n];
    }
    
    public String toString() {
        final String string = super.toString();
        final int lastIndex = string.lastIndexOf(36);
        if (lastIndex != -1) {
            return string.substring(lastIndex + 1);
        }
        final int lastIndex2 = string.lastIndexOf(46);
        if (lastIndex2 != -1) {
            return string.substring(lastIndex2 + 1);
        }
        return string;
    }
    
    public boolean equals(final IdentityConstraint identityConstraint) {
        if (!this.fIdentityConstraintName.equals(identityConstraint.fIdentityConstraintName)) {
            return false;
        }
        if (!this.fSelector.toString().equals(identityConstraint.fSelector.toString())) {
            return false;
        }
        if (this.fFieldCount != identityConstraint.fFieldCount) {
            return false;
        }
        for (int i = 0; i < this.fFieldCount; ++i) {
            if (!this.fFields[i].toString().equals(identityConstraint.fFields[i].toString())) {
                return false;
            }
        }
        return true;
    }
    
    static final Field[] resize(final Field[] array, final int n) {
        final Field[] array2 = new Field[n];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public short getType() {
        return 10;
    }
    
    public String getName() {
        return this.fIdentityConstraintName;
    }
    
    public String getNamespace() {
        return this.fNamespace;
    }
    
    public short getCategory() {
        return this.type;
    }
    
    public String getSelectorStr() {
        return (this.fSelector != null) ? this.fSelector.toString() : null;
    }
    
    public StringList getFieldStrs() {
        final String[] array = new String[this.fFieldCount];
        for (int i = 0; i < this.fFieldCount; ++i) {
            array[i] = this.fFields[i].toString();
        }
        return new StringListImpl(array, this.fFieldCount);
    }
    
    public XSIDCDefinition getRefKey() {
        return null;
    }
    
    public XSObjectList getAnnotations() {
        return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    public void addAnnotation(final XSAnnotationImpl xsAnnotationImpl) {
        if (xsAnnotationImpl == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
        }
        else if (this.fNumAnnotations == this.fAnnotations.length) {
            final XSAnnotationImpl[] fAnnotations = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, fAnnotations, 0, this.fNumAnnotations);
            this.fAnnotations = fAnnotations;
        }
        this.fAnnotations[this.fNumAnnotations++] = xsAnnotationImpl;
    }
}
