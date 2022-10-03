package org.apache.taglibs.standard.tag.common.xml;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParamSupport extends BodyTagSupport
{
    protected String name;
    protected Object value;
    
    public ParamSupport() {
        this.init();
    }
    
    private void init() {
        this.name = null;
        this.value = null;
    }
    
    public int doEndTag() throws JspException {
        final Tag t = findAncestorWithClass((Tag)this, (Class)TransformSupport.class);
        if (t == null) {
            throw new JspTagException(Resources.getMessage("PARAM_OUTSIDE_TRANSFORM"));
        }
        final TransformSupport parent = (TransformSupport)t;
        Object value = this.value;
        if (value == null) {
            if (this.bodyContent == null || this.bodyContent.getString() == null) {
                value = "";
            }
            else {
                value = this.bodyContent.getString().trim();
            }
        }
        parent.addParameter(this.name, value);
        return 6;
    }
    
    public void release() {
        this.init();
    }
}
