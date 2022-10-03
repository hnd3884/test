package org.apache.xml.security.transforms.params;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XPathContainer extends SignatureElementProxy implements TransformParam
{
    public XPathContainer(final Document document) {
        super(document);
    }
    
    public void setXPath(final String s) {
        if (super._constructionElement.getChildNodes() != null) {
            final NodeList childNodes = super._constructionElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                super._constructionElement.removeChild(childNodes.item(i));
            }
        }
        super._constructionElement.appendChild(super._doc.createTextNode(s));
    }
    
    public String getXPath() {
        return this.getTextFromTextChild();
    }
    
    public String getBaseLocalName() {
        return "XPath";
    }
}
