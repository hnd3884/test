package org.apache.taglibs.standard.tag.rt.fmt;

import javax.servlet.jsp.JspTagException;
import java.util.Date;
import org.apache.taglibs.standard.tag.common.fmt.FormatDateSupport;

public class FormatDateTag extends FormatDateSupport
{
    public void setValue(final Date value) throws JspTagException {
        this.value = value;
    }
    
    public void setType(final String type) throws JspTagException {
        this.type = type;
    }
    
    public void setDateStyle(final String dateStyle) throws JspTagException {
        this.dateStyle = dateStyle;
    }
    
    public void setTimeStyle(final String timeStyle) throws JspTagException {
        this.timeStyle = timeStyle;
    }
    
    public void setPattern(final String pattern) throws JspTagException {
        this.pattern = pattern;
    }
    
    public void setTimeZone(final Object timeZone) throws JspTagException {
        this.timeZone = timeZone;
    }
}
