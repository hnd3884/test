package com.sun.org.apache.xml.internal.security.transforms.params;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class XPathContainer extends SignatureElementProxy implements TransformParam
{
    public XPathContainer(final Document document) {
        super(document);
    }
    
    public void setXPath(final String s) {
        Node node = this.getElement().getFirstChild();
        while (node != null) {
            final Node node2 = node;
            node = node.getNextSibling();
            this.getElement().removeChild(node2);
        }
        this.appendSelf(this.createText(s));
    }
    
    public String getXPath() {
        return this.getTextFromTextChild();
    }
    
    @Override
    public String getBaseLocalName() {
        return "XPath";
    }
}
