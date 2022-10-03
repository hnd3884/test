package org.apache.taglibs.standard.tag.rt.xml;

import org.xml.sax.XMLFilter;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.xml.ParseSupport;

public class ParseTag extends ParseSupport
{
    public void setXml(final Object xml) throws JspTagException {
        this.xml = xml;
    }
    
    public void setDoc(final Object xml) throws JspTagException {
        this.xml = xml;
    }
    
    public void setSystemId(final String systemId) throws JspTagException {
        this.systemId = systemId;
    }
    
    public void setFilter(final XMLFilter filter) throws JspTagException {
        this.filter = filter;
    }
}
