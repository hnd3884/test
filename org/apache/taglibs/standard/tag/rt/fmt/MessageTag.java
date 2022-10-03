package org.apache.taglibs.standard.tag.rt.fmt;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;

public class MessageTag extends MessageSupport
{
    public void setKey(final String key) throws JspTagException {
        this.keyAttrValue = key;
        this.keySpecified = true;
    }
    
    public void setBundle(final LocalizationContext locCtxt) throws JspTagException {
        this.bundleAttrValue = locCtxt;
        this.bundleSpecified = true;
    }
}
