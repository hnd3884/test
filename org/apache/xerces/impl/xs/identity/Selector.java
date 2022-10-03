package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xpath.XPath;

public class Selector
{
    protected final XPath fXPath;
    protected final IdentityConstraint fIdentityConstraint;
    protected IdentityConstraint fIDConstraint;
    protected String fXpathDefaultNamespace;
    
    public Selector(final XPath fxPath, final IdentityConstraint fIdentityConstraint, final String fXpathDefaultNamespace) {
        this.fXPath = fxPath;
        this.fIdentityConstraint = fIdentityConstraint;
        this.fXpathDefaultNamespace = fXpathDefaultNamespace;
    }
    
    public org.apache.xerces.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }
    
    public IdentityConstraint getIDConstraint() {
        return this.fIdentityConstraint;
    }
    
    public XPathMatcher createMatcher(final FieldActivator fieldActivator, final int n) {
        return new Matcher(this.fXPath, fieldActivator, n);
    }
    
    public String toString() {
        return this.fXPath.toString();
    }
    
    public String getXPathDefaultNamespace() {
        return this.fXpathDefaultNamespace;
    }
    
    public class Matcher extends XPathMatcher
    {
        protected final FieldActivator fFieldActivator;
        protected final int fInitialDepth;
        protected int fElementDepth;
        protected int fMatchedDepth;
        
        public Matcher(final XPath xPath, final FieldActivator fFieldActivator, final int fInitialDepth) {
            super(xPath);
            this.fFieldActivator = fFieldActivator;
            this.fInitialDepth = fInitialDepth;
        }
        
        public void startDocumentFragment() {
            super.startDocumentFragment();
            this.fElementDepth = 0;
            this.fMatchedDepth = -1;
        }
        
        public void startElement(final QName qName, final XMLAttributes xmlAttributes) {
            super.startElement(qName, xmlAttributes);
            ++this.fElementDepth;
            if (this.isMatched()) {
                this.fMatchedDepth = this.fElementDepth;
                this.fFieldActivator.startValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
                for (int fieldCount = Selector.this.fIdentityConstraint.getFieldCount(), i = 0; i < fieldCount; ++i) {
                    this.fFieldActivator.activateField(Selector.this.fIdentityConstraint.getFieldAt(i), this.fInitialDepth).startElement(qName, xmlAttributes);
                }
            }
        }
        
        public void endElement(final QName qName, final XSTypeDefinition xsTypeDefinition, final boolean b, final Object o, final short n, final ShortList list) {
            super.endElement(qName, xsTypeDefinition, b, o, n, list);
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
    
    public static class XPath extends org.apache.xerces.impl.xpath.XPath
    {
        public XPath(final String s, final SymbolTable symbolTable, final NamespaceContext namespaceContext) throws XPathException {
            super(normalize(s), symbolTable, namespaceContext);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                if (this.fLocationPaths[i].steps[this.fLocationPaths[i].steps.length - 1].axis.type == 2) {
                    throw new XPathException("c-selector-xpath");
                }
            }
        }
        
        private static String normalize(String substring) {
            final StringBuffer sb = new StringBuffer(substring.length() + 5);
            while (true) {
                if (!XMLChar.trim(substring).startsWith("/") && !XMLChar.trim(substring).startsWith(".")) {
                    sb.append("./");
                }
                final int index = substring.indexOf(124);
                if (index == -1) {
                    break;
                }
                sb.append(substring.substring(0, index + 1));
                substring = substring.substring(index + 1, substring.length());
            }
            sb.append(substring);
            return sb.toString();
        }
    }
}
