package com.sun.org.apache.xml.internal.security.transforms.params;

import org.w3c.dom.Text;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;

public class XPathFilterCHGPContainer extends ElementProxy implements TransformParam
{
    public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
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
            this.setLocalAttribute("IncludeSlashPolicy", "true");
        }
        else {
            this.setLocalAttribute("IncludeSlashPolicy", "false");
        }
        if (s != null && s.trim().length() > 0) {
            final Element elementForFamily = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "IncludeButSearch");
            elementForFamily.appendChild(this.createText(indentXPathText(s)));
            this.addReturnToSelf();
            this.appendSelf(elementForFamily);
        }
        if (s2 != null && s2.trim().length() > 0) {
            final Element elementForFamily2 = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "ExcludeButSearch");
            elementForFamily2.appendChild(this.createText(indentXPathText(s2)));
            this.addReturnToSelf();
            this.appendSelf(elementForFamily2);
        }
        if (s3 != null && s3.trim().length() > 0) {
            final Element elementForFamily3 = ElementProxy.createElementForFamily(document, this.getBaseNamespace(), "Exclude");
            elementForFamily3.appendChild(this.createText(indentXPathText(s3)));
            this.addReturnToSelf();
            this.appendSelf(elementForFamily3);
        }
        this.addReturnToSelf();
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
        return XMLUtils.getFullTextChildrenFromNode(XMLUtils.selectNode(this.getElement().getFirstChild(), this.getBaseNamespace(), s, 0));
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
        return this.getLocalAttribute("IncludeSlashPolicy").equals("true");
    }
    
    private Node getHereContextNode(final String s) {
        if (this.length(this.getBaseNamespace(), s) != 1) {
            return null;
        }
        return selectNodeText(this.getFirstChild(), this.getBaseNamespace(), s, 0);
    }
    
    private static Text selectNodeText(final Node node, final String s, final String s2, final int n) {
        final Element selectNode = XMLUtils.selectNode(node, s, s2, n);
        if (selectNode == null) {
            return null;
        }
        Node node2;
        for (node2 = selectNode.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
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
    
    @Override
    public final String getBaseLocalName() {
        return "XPathAlternative";
    }
    
    @Override
    public final String getBaseNamespace() {
        return "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
    }
}
