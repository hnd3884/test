package org.apache.xerces.impl.xs.alternative;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSTypeAlternative;

public class XSTypeAlternativeImpl implements XSTypeAlternative
{
    protected final String fElementName;
    protected Test fTestExpr;
    protected XSTypeDefinition fTypeDefinition;
    protected XSObjectList fAnnotations;
    protected String fXPathDefaultNamespace;
    protected NamespaceSupport fNamespaceContext;
    protected String fBaseURI;
    
    public XSTypeAlternativeImpl(final String fElementName, final XSTypeDefinition fTypeDefinition, final XSObjectList list) {
        this.fTestExpr = null;
        this.fAnnotations = null;
        this.fXPathDefaultNamespace = null;
        this.fNamespaceContext = null;
        this.fBaseURI = null;
        this.fElementName = fElementName;
        this.fTypeDefinition = fTypeDefinition;
    }
    
    public void setTest(final Test fTestExpr) {
        this.fTestExpr = fTestExpr;
    }
    
    public void setAnnotations(final XSObjectList fAnnotations) {
        this.fAnnotations = fAnnotations;
    }
    
    public void setXPathDefauleNamespace(final String fxPathDefaultNamespace) {
        this.fXPathDefaultNamespace = fxPathDefaultNamespace;
    }
    
    public void setNamespaceContext(final NamespaceSupport fNamespaceContext) {
        this.fNamespaceContext = fNamespaceContext;
    }
    
    public void setBaseURI(final String fBaseURI) {
        this.fBaseURI = fBaseURI;
    }
    
    public NamespaceSupport getNamespaceContext() {
        NamespaceSupport namespaceSupport = this.fNamespaceContext;
        if (namespaceSupport == null && this.fTestExpr != null) {
            namespaceSupport = this.fTestExpr.getNamespaceContext();
        }
        return namespaceSupport;
    }
    
    public String getBaseURI() {
        return this.fBaseURI;
    }
    
    public String getElementName() {
        return this.fElementName;
    }
    
    public String getXPathDefaultNamespace() {
        return this.fXPathDefaultNamespace;
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
    
    public boolean equals(final XSTypeAlternativeImpl xsTypeAlternativeImpl) {
        return false;
    }
    
    public XSObjectList getAnnotations() {
        return this.fAnnotations;
    }
    
    public String getTestStr() {
        return (this.fTestExpr != null) ? this.fTestExpr.toString() : null;
    }
    
    public Test getTest() {
        return this.fTestExpr;
    }
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fTypeDefinition;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    public short getType() {
        return 15;
    }
}
