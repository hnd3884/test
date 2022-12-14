package org.apache.taglibs.standard.tag.rt.core;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.core.RedirectSupport;

public class RedirectTag extends RedirectSupport
{
    public void setUrl(final String url) throws JspTagException {
        this.url = url;
    }
    
    public void setContext(final String context) throws JspTagException {
        this.context = context;
    }
}
