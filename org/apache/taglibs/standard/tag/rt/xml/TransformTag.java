package org.apache.taglibs.standard.tag.rt.xml;

import javax.xml.transform.Result;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.xml.TransformSupport;

public class TransformTag extends TransformSupport
{
    public void setXml(final Object xml) throws JspTagException {
        this.setDoc(xml);
    }
    
    public void setDoc(final Object xml) throws JspTagException {
        this.xml = xml;
        this.xmlSpecified = true;
    }
    
    public void setXmlSystemId(final String xmlSystemId) throws JspTagException {
        this.xmlSystemId = xmlSystemId;
    }
    
    public void setDocSystemId(final String xmlSystemId) throws JspTagException {
        this.xmlSystemId = xmlSystemId;
    }
    
    public void setXslt(final Object xslt) throws JspTagException {
        this.xslt = xslt;
    }
    
    public void setXsltSystemId(final String xsltSystemId) throws JspTagException {
        this.xsltSystemId = xsltSystemId;
    }
    
    public void setResult(final Result result) throws JspTagException {
        this.result = result;
    }
}
