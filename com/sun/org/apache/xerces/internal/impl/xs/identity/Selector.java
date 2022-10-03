package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

public class Selector
{
    protected final XPath fXPath;
    protected final IdentityConstraint fIdentityConstraint;
    protected IdentityConstraint fIDConstraint;
    
    public Selector(final XPath xpath, final IdentityConstraint identityConstraint) {
        this.fXPath = xpath;
        this.fIdentityConstraint = identityConstraint;
    }
    
    public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }
    
    public IdentityConstraint getIDConstraint() {
        return this.fIdentityConstraint;
    }
    
    public XPathMatcher createMatcher(final FieldActivator activator, final int initialDepth) {
        return new Matcher(this.fXPath, activator, initialDepth);
    }
    
    @Override
    public String toString() {
        return this.fXPath.toString();
    }
    
    public static class XPath extends com.sun.org.apache.xerces.internal.impl.xpath.XPath
    {
        public XPath(final String xpath, final SymbolTable symbolTable, final NamespaceContext context) throws XPathException {
            super(normalize(xpath), symbolTable, context);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                final Axis axis = this.fLocationPaths[i].steps[this.fLocationPaths[i].steps.length - 1].axis;
                if (axis.type == 2) {
                    throw new XPathException("c-selector-xpath");
                }
            }
        }
        
        private static String normalize(String xpath) {
            final StringBuffer modifiedXPath = new StringBuffer(xpath.length() + 5);
            int unionIndex = -1;
            while (true) {
                if (!XMLChar.trim(xpath).startsWith("/") && !XMLChar.trim(xpath).startsWith(".")) {
                    modifiedXPath.append("./");
                }
                unionIndex = xpath.indexOf(124);
                if (unionIndex == -1) {
                    break;
                }
                modifiedXPath.append(xpath.substring(0, unionIndex + 1));
                xpath = xpath.substring(unionIndex + 1, xpath.length());
            }
            modifiedXPath.append(xpath);
            return modifiedXPath.toString();
        }
    }
    
    public class Matcher extends XPathMatcher
    {
        protected final FieldActivator fFieldActivator;
        protected final int fInitialDepth;
        protected int fElementDepth;
        protected int fMatchedDepth;
        
        public Matcher(final XPath xpath, final FieldActivator activator, final int initialDepth) {
            super(xpath);
            this.fFieldActivator = activator;
            this.fInitialDepth = initialDepth;
        }
        
        @Override
        public void startDocumentFragment() {
            super.startDocumentFragment();
            this.fElementDepth = 0;
            this.fMatchedDepth = -1;
        }
        
        @Override
        public void startElement(final QName element, final XMLAttributes attributes) {
            super.startElement(element, attributes);
            ++this.fElementDepth;
            if (this.isMatched()) {
                this.fMatchedDepth = this.fElementDepth;
                this.fFieldActivator.startValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
                for (int count = Selector.this.fIdentityConstraint.getFieldCount(), i = 0; i < count; ++i) {
                    final Field field = Selector.this.fIdentityConstraint.getFieldAt(i);
                    final XPathMatcher matcher = this.fFieldActivator.activateField(field, this.fInitialDepth);
                    matcher.startElement(element, attributes);
                }
            }
        }
        
        @Override
        public void endElement(final QName element, final XSTypeDefinition type, final boolean nillable, final Object actualValue, final short valueType, final ShortList itemValueType) {
            super.endElement(element, type, nillable, actualValue, valueType, itemValueType);
            if (this.fElementDepth-- == this.fMatchedDepth) {
                this.fMatchedDepth = -1;
                this.fFieldActivator.endValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
            }
        }
        
        public IdentityConstraint getIdentityConstraint() {
            return Selector.this.fIdentityConstraint;
        }
        
        public int getInitialDepth() {
            return this.fInitialDepth;
        }
    }
}
