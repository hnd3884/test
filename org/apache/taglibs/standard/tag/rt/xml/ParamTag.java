package org.apache.taglibs.standard.tag.rt.xml;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.xml.ParamSupport;

public class ParamTag extends ParamSupport
{
    public void setName(final String name) throws JspTagException {
        this.name = name;
    }
    
    public void setValue(final Object value) throws JspTagException {
        this.value = value;
    }
}
