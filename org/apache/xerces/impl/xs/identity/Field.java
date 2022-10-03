package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.xpath.XPath;

public class Field
{
    protected final XPath fXPath;
    protected final IdentityConstraint fIdentityConstraint;
    protected String fXpathDefaultNamespace;
    
    public Field(final XPath fxPath, final IdentityConstraint fIdentityConstraint, final String fXpathDefaultNamespace) {
        this.fXPath = fxPath;
        this.fIdentityConstraint = fIdentityConstraint;
        this.fXpathDefaultNamespace = fXpathDefaultNamespace;
    }
    
    public org.apache.xerces.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }
    
    public IdentityConstraint getIdentityConstraint() {
        return this.fIdentityConstraint;
    }
    
    public XPathMatcher createMatcher(final ValueStore valueStore) {
        return new Matcher(this.fXPath, valueStore);
    }
    
    public String toString() {
        return this.fXPath.toString();
    }
    
    public String getXPathDefaultNamespace() {
        return this.fXpathDefaultNamespace;
    }
    
    protected class Matcher extends XPathMatcher
    {
        protected final ValueStore fStore;
        protected boolean fMayMatch;
        
        public Matcher(final XPath xPath, final ValueStore fStore) {
            super(xPath);
            this.fMayMatch = true;
            this.fStore = fStore;
        }
        
        protected void matched(final Object o, final short n, final ShortList list, final boolean b) {
            super.matched(o, n, list, b);
            if (b && Field.this.fIdentityConstraint.getCategory() == 1) {
                this.fStore.reportError("KeyMatchesNillable", new Object[] { this.fStore.getElementName(), Field.this.fIdentityConstraint.getIdentityConstraintName() });
            }
            this.fStore.addValue(Field.this, this.fMayMatch, o, this.convertToPrimitiveKind(n), this.convertToPrimitiveKind(list));
            this.fMayMatch = false;
        }
        
        private short convertToPrimitiveKind(final short n) {
            if (n <= 20) {
                return n;
            }
            if (n <= 29) {
                return 2;
            }
            if (n <= 42) {
                return 4;
            }
            return n;
        }
        
        private ShortList convertToPrimitiveKind(final ShortList list) {
            if (list != null) {
                int length;
                int i;
                for (length = list.getLength(), i = 0; i < length; ++i) {
                    final short item = list.item(i);
                    if (item != this.convertToPrimitiveKind(item)) {
                        break;
                    }
                }
                if (i != length) {
                    final short[] array = new short[length];
                    for (int j = 0; j < i; ++j) {
                        array[j] = list.item(j);
                    }
                    while (i < length) {
                        array[i] = this.convertToPrimitiveKind(list.item(i));
                        ++i;
                    }
                    return new ShortListImpl(array, array.length);
                }
            }
            return list;
        }
        
        protected void handleContent(final XSTypeDefinition xsTypeDefinition, final boolean b, final Object fMatchedString, final short n, final ShortList list) {
            if (xsTypeDefinition == null || (xsTypeDefinition.getTypeCategory() == 15 && ((XSComplexTypeDefinition)xsTypeDefinition).getContentType() != 1)) {
                this.fStore.reportError("cvc-id.3", new Object[] { Field.this.fIdentityConstraint.getName(), this.fStore.getElementName() });
            }
            this.matched(this.fMatchedString = fMatchedString, n, list, b);
        }
    }
    
    public static class XPath extends org.apache.xerces.impl.xpath.XPath
    {
        public XPath(final String s, final SymbolTable symbolTable, final NamespaceContext namespaceContext) throws XPathException {
            super(fixupXPath(s), symbolTable, namespaceContext);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                for (int j = 0; j < this.fLocationPaths[i].steps.length; ++j) {
                    if (this.fLocationPaths[i].steps[j].axis.type == 2 && j < this.fLocationPaths[i].steps.length - 1) {
                        throw new XPathException("c-fields-xpaths");
                    }
                }
            }
        }
        
        private static String fixupXPath(final String s) {
            final int length = s.length();
            int i = 0;
            int n = 1;
            while (i < length) {
                final char char1 = s.charAt(i);
                if (n != 0) {
                    if (!XMLChar.isSpace(char1)) {
                        if (char1 == '.' || char1 == '/') {
                            n = 0;
                        }
                        else if (char1 != '|') {
                            return fixupXPath2(s, i, length);
                        }
                    }
                }
                else if (char1 == '|') {
                    n = 1;
                }
                ++i;
            }
            return s;
        }
        
        private static String fixupXPath2(final String s, int i, final int n) {
            final StringBuffer sb = new StringBuffer(n + 2);
            for (int j = 0; j < i; ++j) {
                sb.append(s.charAt(j));
            }
            sb.append("./");
            int n2 = 0;
            while (i < n) {
                final char char1 = s.charAt(i);
                if (n2 != 0) {
                    if (!XMLChar.isSpace(char1)) {
                        if (char1 == '.' || char1 == '/') {
                            n2 = 0;
                        }
                        else if (char1 != '|') {
                            sb.append("./");
                            n2 = 0;
                        }
                    }
                }
                else if (char1 == '|') {
                    n2 = 1;
                }
                sb.append(char1);
                ++i;
            }
            return sb.toString();
        }
    }
}
