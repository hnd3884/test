package org.apache.taglibs.standard.tag.rt.core;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.core.ParamSupport;

public class ParamTag extends ParamSupport
{
    public void setName(final String name) throws JspTagException {
        this.name = name;
    }
    
    public void setValue(final String value) throws JspTagException {
        this.value = value;
    }
}
