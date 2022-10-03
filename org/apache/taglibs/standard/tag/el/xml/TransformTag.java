package org.apache.taglibs.standard.tag.el.xml;

import javax.xml.transform.Result;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.xml.TransformSupport;

public class TransformTag extends TransformSupport
{
    private String xml_;
    private String xmlSystemId_;
    private String xslt_;
    private String xsltSystemId_;
    private String result_;
    
    public TransformTag() {
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
    
    public void setXml(final String xml_) {
        this.xml_ = xml_;
        this.xmlSpecified = true;
    }
    
    public void setXmlSystemId(final String xmlSystemId_) {
        this.xmlSystemId_ = xmlSystemId_;
    }
    
    public void setXslt(final String xslt_) {
        this.xslt_ = xslt_;
    }
    
    public void setXsltSystemId(final String xsltSystemId_) {
        this.xsltSystemId_ = xsltSystemId_;
    }
    
    public void setResult(final String result_) {
        this.result_ = result_;
    }
    
    private void init() {
        final String xml_ = null;
        this.result_ = xml_;
        this.xsltSystemId_ = xml_;
        this.xslt_ = xml_;
        this.xmlSystemId = xml_;
        this.xml_ = xml_;
    }
    
    private void evaluateExpressions() throws JspException {
        this.xml = ExpressionUtil.evalNotNull("transform", "xml", this.xml_, Object.class, (Tag)this, this.pageContext);
        this.xmlSystemId = (String)ExpressionUtil.evalNotNull("transform", "xmlSystemId", this.xmlSystemId_, String.class, (Tag)this, this.pageContext);
        this.xslt = ExpressionUtil.evalNotNull("transform", "xslt", this.xslt_, Object.class, (Tag)this, this.pageContext);
        this.xsltSystemId = (String)ExpressionUtil.evalNotNull("transform", "xsltSystemId", this.xsltSystemId_, String.class, (Tag)this, this.pageContext);
        this.result = (Result)ExpressionUtil.evalNotNull("transform", "result", this.result_, Result.class, (Tag)this, this.pageContext);
    }
}
