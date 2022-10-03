package org.apache.taglibs.standard.tag.rt.fmt;

import java.util.Locale;
import org.apache.taglibs.standard.tag.common.fmt.LocaleUtil;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.fmt.ParseNumberSupport;

public class ParseNumberTag extends ParseNumberSupport
{
    public void setValue(final String value) throws JspTagException {
        this.value = value;
        this.valueSpecified = true;
    }
    
    public void setType(final String type) throws JspTagException {
        this.type = type;
    }
    
    public void setPattern(final String pattern) throws JspTagException {
        this.pattern = pattern;
    }
    
    public void setParseLocale(final Object loc) throws JspTagException {
        final Locale locale = LocaleUtil.parseLocaleAttributeValue(loc);
        if (loc != null) {
            this.parseLocale = locale;
        }
    }
    
    public void setIntegerOnly(final boolean isIntegerOnly) throws JspTagException {
        this.isIntegerOnly = isIntegerOnly;
        this.integerOnlySpecified = true;
    }
}
