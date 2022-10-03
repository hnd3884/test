package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

public class Field
{
    protected XPath fXPath;
    protected IdentityConstraint fIdentityConstraint;
    
    public Field(final XPath xpath, final IdentityConstraint identityConstraint) {
        this.fXPath = xpath;
        this.fIdentityConstraint = identityConstraint;
    }
    
    public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }
    
    public IdentityConstraint getIdentityConstraint() {
        return this.fIdentityConstraint;
    }
    
    public XPathMatcher createMatcher(final FieldActivator activator, final ValueStore store) {
        return new Matcher(this.fXPath, activator, store);
    }
    
    @Override
    public String toString() {
        return this.fXPath.toString();
    }
    
    public static class XPath extends com.sun.org.apache.xerces.internal.impl.xpath.XPath
    {
        public XPath(final String xpath, final SymbolTable symbolTable, final NamespaceContext context) throws XPathException {
            super((xpath.trim().startsWith("/") || xpath.trim().startsWith(".")) ? xpath : ("./" + xpath), symbolTable, context);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                for (int j = 0; j < this.fLocationPaths[i].steps.length; ++j) {
                    final Axis axis = this.fLocationPaths[i].steps[j].axis;
                    if (axis.type == 2 && j < this.fLocationPaths[i].steps.length - 1) {
                        throw new XPathException("c-fields-xpaths");
                    }
                }
            }
        }
    }
    
    protected class Matcher extends XPathMatcher
    {
        protected FieldActivator fFieldActivator;
        protected ValueStore fStore;
        
        public Matcher(final XPath xpath, final FieldActivator activator, final ValueStore store) {
            super(xpath);
            this.fFieldActivator = activator;
            this.fStore = store;
        }
        
        @Override
        protected void matched(final Object actualValue, final short valueType, final ShortList itemValueType, final boolean isNil) {
            super.matched(actualValue, valueType, itemValueType, isNil);
            if (isNil && Field.this.fIdentityConstraint.getCategory() == 1) {
                final String code = "KeyMatchesNillable";
                this.fStore.reportError(code, new Object[] { Field.this.fIdentityConstraint.getElementName(), Field.this.fIdentityConstraint.getIdentityConstraintName() });
            }
            this.fStore.addValue(Field.this, actualValue, this.convertToPrimitiveKind(valueType), this.convertToPrimitiveKind(itemValueType));
            this.fFieldActivator.setMayMatch(Field.this, Boolean.FALSE);
        }
        
        private short convertToPrimitiveKind(final short valueType) {
            if (valueType <= 20) {
                return valueType;
            }
            if (valueType <= 29) {
                return 2;
            }
            if (valueType <= 42) {
                return 4;
            }
            return valueType;
        }
        
        private ShortList convertToPrimitiveKind(final ShortList itemValueType) {
            if (itemValueType != null) {
                int length;
                int i;
                for (length = itemValueType.getLength(), i = 0; i < length; ++i) {
                    final short type = itemValueType.item(i);
                    if (type != this.convertToPrimitiveKind(type)) {
                        break;
                    }
                }
                if (i != length) {
                    final short[] arr = new short[length];
                    for (int j = 0; j < i; ++j) {
                        arr[j] = itemValueType.item(j);
                    }
                    while (i < length) {
                        arr[i] = this.convertToPrimitiveKind(itemValueType.item(i));
                        ++i;
                    }
                    return new ShortListImpl(arr, arr.length);
                }
            }
            return itemValueType;
        }
        
        @Override
        protected void handleContent(final XSTypeDefinition type, final boolean nillable, final Object actualValue, final short valueType, final ShortList itemValueType) {
            if (type == null || (type.getTypeCategory() == 15 && ((XSComplexTypeDefinition)type).getContentType() != 1)) {
                this.fStore.reportError("cvc-id.3", new Object[] { Field.this.fIdentityConstraint.getName(), Field.this.fIdentityConstraint.getElementName() });
            }
            this.matched(this.fMatchedString = actualValue, valueType, itemValueType, nillable);
        }
    }
}
