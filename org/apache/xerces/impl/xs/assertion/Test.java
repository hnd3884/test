package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.xni.NamespaceContext;

public class Test
{
    protected final String fExpressionStr;
    protected final NamespaceContext fNsContext;
    protected final XSAssert fAssert;
    
    public Test(final String fExpressionStr, final NamespaceContext fNsContext, final XSAssert fAssert) {
        this.fExpressionStr = fExpressionStr;
        this.fNsContext = fNsContext;
        this.fAssert = fAssert;
    }
    
    public String getXPathStr() {
        return this.fExpressionStr;
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.fNsContext;
    }
    
    public XSAssert getAssertion() {
        return this.fAssert;
    }
}
