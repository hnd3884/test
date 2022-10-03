package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import javax.xml.namespace.QName;

public class OMChildrenWithSpecificAttributeIterator extends OMFilterIterator
{
    private QName attributeName;
    private String attributeValue;
    private boolean detach;
    private boolean doCaseSensitiveValueChecks;
    
    public OMChildrenWithSpecificAttributeIterator(final OMNode currentChild, final QName attributeName, final String attributeValue, final boolean detach) {
        super(new OMChildrenIterator(currentChild));
        this.doCaseSensitiveValueChecks = true;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.detach = detach;
    }
    
    public void setCaseInsensitiveValueChecks(final boolean val) {
        this.doCaseSensitiveValueChecks = val;
    }
    
    @Override
    protected boolean matches(final OMNode node) {
        if (node instanceof OMElement) {
            final OMAttribute attr = ((OMElement)node).getAttribute(this.attributeName);
            return attr != null && (this.doCaseSensitiveValueChecks ? attr.getAttributeValue().equals(this.attributeValue) : attr.getAttributeValue().equalsIgnoreCase(this.attributeValue));
        }
        return false;
    }
    
    @Override
    public Object next() {
        final Object result = super.next();
        if (this.detach) {
            this.remove();
        }
        return result;
    }
}
