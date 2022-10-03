package org.apache.xerces.impl.xpath;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

class ConjunctionNode extends XPathSyntaxTreeNode
{
    private int conjunction;
    private XPathSyntaxTreeNode child1;
    private XPathSyntaxTreeNode child2;
    public static final int OR = 0;
    public static final int AND = 1;
    
    public ConjunctionNode(final int conjunction, final XPathSyntaxTreeNode child1, final XPathSyntaxTreeNode child2) {
        this.conjunction = conjunction;
        this.child1 = child1;
        this.child2 = child2;
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final boolean evaluate = this.child1.evaluate(qName, xmlAttributes, namespaceContext);
        final boolean evaluate2 = this.child2.evaluate(qName, xmlAttributes, namespaceContext);
        if (this.conjunction == 0) {
            return evaluate || evaluate2;
        }
        return evaluate && evaluate2;
    }
}
