package com.octo.captcha.module.taglib;

import com.octo.captcha.service.CaptchaService;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public abstract class BaseCaptchaTag implements Tag
{
    protected PageContext pageContext;
    protected Tag parent;
    
    public void setPageContext(final PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    public void setParent(final Tag parent) {
        this.parent = parent;
    }
    
    public Tag getParent() {
        return this.parent;
    }
    
    public int doStartTag() throws JspException {
        return 0;
    }
    
    public void release() {
    }
    
    public abstract int doEndTag() throws JspException;
    
    protected abstract CaptchaService getService();
}
