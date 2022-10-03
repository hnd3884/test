package org.apache.xml.security.transforms.params;

import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;

public class XPathFilterCHGPContainer extends ElementProxy implements TransformParam
{
    private static final String _TAG_INCLUDE_BUT_SEARCH = "IncludeButSearch";
    private static final String _TAG_EXCLUDE_BUT_SEARCH = "ExcludeButSearch";
    private static final String _TAG_EXCLUDE = "Exclude";
    public static final String _TAG_XPATHCHGP = "XPathAlternative";
    public static final String _ATT_INCLUDESLASH = "IncludeSlashPolicy";
    public static final boolean IncludeSlash = true;
    public static final boolean ExcludeSlash = false;
    
    private XPathFilterCHGPContainer() {
    }
    
    private XPathFilterCHGPContainer(final Document document, final boolean b, final String s, final String s2, final String s3) {
        super(document);
        if (b) {
            super._constructionElement.setAttributeNS(null, "IncludeSlashPolicy", "true");
        }
        else {
            super._constructionElement.setAttributeNS(null, "IncludeSlashPolicy", "false");
        }
        if (s != null && s.trim().length() > 0) {
            final Element elementForFamily = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "IncludeButSearch");
            elementForFamily.appendChild(super._doc.createTextNode(indentXPathText(s)));
            super._constructionElement.appendChild(document.createTextNode("\n"));
            super._constructionElement.appendChild(elementForFamily);
        }
        if (s2 != null && s2.trim().length() > 0) {
            final Element elementForFamily2 = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "ExcludeButSearch");
            elementForFamily2.appendChild(super._doc.createTextNode(indentXPathText(s2)));
            super._constructionElement.appendChild(document.createTextNode("\n"));
            super._constructionElement.appendChild(elementForFamily2);
        }
        if (s3 != null && s3.trim().length() > 0) {
            final Element elementForFamily3 = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "Exclude");
            elementForFamily3.appendChild(super._doc.createTextNode(indentXPathText(s3)));
            super._constructionElement.appendChild(document.createTextNode("\n"));
            super._constructionElement.appendChild(elementForFamily3);
        }
        super._constructionElement.appendChild(document.createTextNode("\n"));
    }
    
    static String indentXPathText(final String s) {
        if (s.length() > 2 && !Character.isWhitespace(s.charAt(0))) {
            return "\n" + s + "\n";
        }
        return s;
    }
    
    private XPathFilterCHGPContainer(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public static XPathFilterCHGPContainer getInstance(final Document document, final boolean b, final String s, final String s2, final String s3) {
        return new XPathFilterCHGPContainer(document, b, s, s2, s3);
    }
    
    public static XPathFilterCHGPContainer getInstance(final Element element, final String s) throws XMLSecurityException {
        return new XPathFilterCHGPContainer(element, s);
    }
    
    private String getXStr(final String s) {
        if (this.length(this.getBaseNamespace(), s) != 1) {
            return "";
        }
        return XMLUtils.getFullTextChildrenFromElement(XMLUtils.selectNode(super._constructionElement.getFirstChild(), this.getBaseNamespace(), s, 0));
    }
    
    public String getIncludeButSearch() {
        return this.getXStr("IncludeButSearch");
    }
    
    public String getExcludeButSearch() {
        return this.getXStr("ExcludeButSearch");
    }
    
    public String getExclude() {
        return this.getXStr("Exclude");
    }
    
    public boolean getIncludeSlashPolicy() {
        return super._constructionElement.getAttributeNS(null, "IncludeSlashPolicy").equals("true");
    }
    
    private Node getHereContextNode(final String s) {
        if (this.length(this.getBaseNamespace(), s) != 1) {
            return null;
        }
        return XMLUtils.selectNodeText(super._constructionElement.getFirstChild(), this.getBaseNamespace(), s, 0);
    }
    
    public Node getHereContextNodeIncludeButSearch() {
        return this.getHereContextNode("IncludeButSearch");
    }
    
    public Node getHereContextNodeExcludeButSearch() {
        return this.getHereContextNode("ExcludeButSearch");
    }
    
    public Node getHereContextNodeExclude() {
        return this.getHereContextNode("Exclude");
    }
    
    public final String getBaseLocalName() {
        return "XPathAlternative";
    }
    
    public final String getBaseNamespace() {
        return "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
    }
}
