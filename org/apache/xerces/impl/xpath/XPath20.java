package org.apache.xerces.impl.xpath;

import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import java.io.Reader;
import java.io.StringReader;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.NamespaceContext;

public class XPath20
{
    protected final String fExpression;
    protected final NamespaceContext fNsContext;
    private XPathSyntaxTreeNode fRootNode;
    
    public XPath20(final String fExpression, final SymbolTable symbolTable, final NamespaceContext fNsContext) throws XPathException {
        this.fExpression = fExpression;
        this.fNsContext = fNsContext;
        this.fRootNode = new XPath20Parser(new StringReader(this.fExpression + "\n"), fNsContext).parseExpression();
    }
    
    public boolean evaluateTest(final QName qName, final XMLAttributes xmlAttributes) {
        try {
            return this.fRootNode.evaluate(qName, xmlAttributes, this.fNsContext);
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public String getXPathStrValue() {
        return this.fExpression;
    }
}
