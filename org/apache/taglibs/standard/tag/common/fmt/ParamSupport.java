package org.apache.taglibs.standard.tag.common.fmt;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParamSupport extends BodyTagSupport
{
    protected Object value;
    protected boolean valueSpecified;
    
    public ParamSupport() {
        this.init();
    }
    
    private void init() {
        this.value = null;
        this.valueSpecified = false;
    }
    
    public int doEndTag() throws JspException {
        final Tag t = findAncestorWithClass((Tag)this, (Class)MessageSupport.class);
        if (t == null) {
            throw new JspTagException(Resources.getMessage("PARAM_OUTSIDE_MESSAGE"));
        }
        final MessageSupport parent = (MessageSupport)t;
        Object input = null;
        if (this.valueSpecified) {
            input = this.value;
        }
        else {
            input = this.bodyContent.getString().trim();
        }
        parent.addParam(input);
        return 6;
    }
    
    public void release() {
        this.init();
    }
}
