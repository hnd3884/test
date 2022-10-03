package org.apache.taglibs.standard.tag.rt.fmt;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.fmt.FormatNumberSupport;

public class FormatNumberTag extends FormatNumberSupport
{
    public void setValue(final Object value) throws JspTagException {
        this.value = value;
        this.valueSpecified = true;
    }
    
    public void setType(final String type) throws JspTagException {
        this.type = type;
    }
    
    public void setPattern(final String pattern) throws JspTagException {
        this.pattern = pattern;
    }
    
    public void setCurrencyCode(final String currencyCode) throws JspTagException {
        this.currencyCode = currencyCode;
    }
    
    public void setCurrencySymbol(final String currencySymbol) throws JspTagException {
        this.currencySymbol = currencySymbol;
    }
    
    public void setGroupingUsed(final boolean isGroupingUsed) throws JspTagException {
        this.isGroupingUsed = isGroupingUsed;
        this.groupingUsedSpecified = true;
    }
    
    public void setMaxIntegerDigits(final int maxDigits) throws JspTagException {
        this.maxIntegerDigits = maxDigits;
        this.maxIntegerDigitsSpecified = true;
    }
    
    public void setMinIntegerDigits(final int minDigits) throws JspTagException {
        this.minIntegerDigits = minDigits;
        this.minIntegerDigitsSpecified = true;
    }
    
    public void setMaxFractionDigits(final int maxDigits) throws JspTagException {
        this.maxFractionDigits = maxDigits;
        this.maxFractionDigitsSpecified = true;
    }
    
    public void setMinFractionDigits(final int minDigits) throws JspTagException {
        this.minFractionDigits = minDigits;
        this.minFractionDigitsSpecified = true;
    }
}
