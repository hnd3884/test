package org.apache.taglibs.standard.tag.rt.core;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.core.UrlSupport;

public class UrlTag extends UrlSupport
{
    public void setValue(final String value) throws JspTagException {
        this.value = value;
    }
    
    public void setContext(final String context) throws JspTagException {
        this.context = context;
    }
}
