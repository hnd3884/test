package org.apache.taglibs.standard.tag.el.core;

import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;

public class ImportTag extends ImportSupport
{
    private String context_;
    private String charEncoding_;
    private String url_;
    
    public ImportTag() {
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
    
    public void setUrl(final String url_) {
        this.url_ = url_;
    }
    
    public void setContext(final String context_) {
        this.context_ = context_;
    }
    
    public void setCharEncoding(final String charEncoding_) {
        this.charEncoding_ = charEncoding_;
    }
    
    private void init() {
        final String url_ = null;
        this.charEncoding_ = url_;
        this.context_ = url_;
        this.url_ = url_;
    }
    
    private void evaluateExpressions() throws JspException {
        this.url = (String)ExpressionUtil.evalNotNull("import", "url", this.url_, String.class, (Tag)this, this.pageContext);
        if (this.url == null || this.url.equals("")) {
            throw new NullAttributeException("import", "url");
        }
        this.context = (String)ExpressionUtil.evalNotNull("import", "context", this.context_, String.class, (Tag)this, this.pageContext);
        this.charEncoding = (String)ExpressionUtil.evalNotNull("import", "charEncoding", this.charEncoding_, String.class, (Tag)this, this.pageContext);
    }
}
