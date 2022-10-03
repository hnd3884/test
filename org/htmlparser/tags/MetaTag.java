package org.htmlparser.tags;

import org.htmlparser.util.ParserException;
import org.htmlparser.Attribute;
import org.htmlparser.nodes.TagNode;

public class MetaTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return MetaTag.mIds;
    }
    
    public String getHttpEquiv() {
        return this.getAttribute("HTTP-EQUIV");
    }
    
    public String getMetaContent() {
        return this.getAttribute("CONTENT");
    }
    
    public String getMetaTagName() {
        return this.getAttribute("NAME");
    }
    
    public void setHttpEquiv(final String httpEquiv) {
        final Attribute equiv = this.getAttributeEx("HTTP-EQUIV");
        if (null != equiv) {
            equiv.setValue(httpEquiv);
        }
        else {
            this.getAttributesEx().add(new Attribute("HTTP-EQUIV", httpEquiv));
        }
    }
    
    public void setMetaTagContents(final String metaTagContents) {
        final Attribute content = this.getAttributeEx("CONTENT");
        if (null != content) {
            content.setValue(metaTagContents);
        }
        else {
            this.getAttributesEx().add(new Attribute("CONTENT", metaTagContents));
        }
    }
    
    public void setMetaTagName(final String metaTagName) {
        final Attribute name = this.getAttributeEx("NAME");
        if (null != name) {
            name.setValue(metaTagName);
        }
        else {
            this.getAttributesEx().add(new Attribute("NAME", metaTagName));
        }
    }
    
    public void doSemanticAction() throws ParserException {
        final String httpEquiv = this.getHttpEquiv();
        if ("Content-Type".equalsIgnoreCase(httpEquiv)) {
            final String charset = this.getPage().getCharset(this.getAttribute("CONTENT"));
            this.getPage().setEncoding(charset);
        }
    }
    
    static {
        mIds = new String[] { "META" };
    }
}
