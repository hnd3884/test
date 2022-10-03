package org.apache.taglibs.standard.tag.rt.fmt;

import java.util.Locale;
import org.apache.taglibs.standard.tag.common.fmt.LocaleUtil;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.fmt.ParseDateSupport;

public class ParseDateTag extends ParseDateSupport
{
    public void setValue(final String value) throws JspTagException {
        this.value = value;
        this.valueSpecified = true;
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
    
    public void setParseLocale(final Object loc) throws JspTagException {
        final Locale locale = LocaleUtil.parseLocaleAttributeValue(loc);
        if (loc != null) {
            this.parseLocale = locale;
        }
    }
}
