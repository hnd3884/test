package org.apache.taglibs.standard.tag.el.xml;

import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import org.xml.sax.XMLFilter;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.xml.ParseSupport;

public class ParseTag extends ParseSupport
{
    private String xml_;
    private String systemId_;
    private String filter_;
    
    public ParseTag() {
        this.init();
    }
    
    public int doStartTag() throws JspException {
        this.evaluateExpressions();
        return super.doStartTag();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public void setFilter(final String filter_) {
        this.filter_ = filter_;
    }
    
    public void setXml(final String xml_) {
        this.xml_ = xml_;
    }
    
    public void setSystemId(final String systemId_) {
        this.systemId_ = systemId_;
    }
    
    private void init() {
        final String filter_ = null;
        this.systemId_ = filter_;
        this.xml_ = filter_;
        this.filter_ = filter_;
    }
    
    private void evaluateExpressions() throws JspException {
        this.xml = ExpressionUtil.evalNotNull("parse", "xml", this.xml_, Object.class, (Tag)this, this.pageContext);
        this.systemId = (String)ExpressionUtil.evalNotNull("parse", "systemId", this.systemId_, String.class, (Tag)this, this.pageContext);
        try {
            this.filter = (XMLFilter)ExpressionUtil.evalNotNull("parse", "filter", this.filter_, XMLFilter.class, (Tag)this, this.pageContext);
        }
        catch (final NullAttributeException ex) {
            this.filter = null;
        }
    }
}
