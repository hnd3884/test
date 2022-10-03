package org.apache.taglibs.standard.tag.el.fmt;

import java.util.Locale;
import org.apache.taglibs.standard.tag.common.fmt.LocaleUtil;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.ParseDateSupport;

public class ParseDateTag extends ParseDateSupport
{
    private String value_;
    private String type_;
    private String dateStyle_;
    private String timeStyle_;
    private String pattern_;
    private String timeZone_;
    private String parseLocale_;
    
    public ParseDateTag() {
        this.init();
    }
    
    public int doStartTag() throws JspException {
        this.evaluateExpressions();
        return super.doStartTag();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public void setValue(final String value_) {
        this.value_ = value_;
        this.valueSpecified = true;
    }
    
    public void setType(final String type_) {
        this.type_ = type_;
    }
    
    public void setDateStyle(final String dateStyle_) {
        this.dateStyle_ = dateStyle_;
    }
    
    public void setTimeStyle(final String timeStyle_) {
        this.timeStyle_ = timeStyle_;
    }
    
    public void setPattern(final String pattern_) {
        this.pattern_ = pattern_;
    }
    
    public void setTimeZone(final String timeZone_) {
        this.timeZone_ = timeZone_;
    }
    
    public void setParseLocale(final String parseLocale_) {
        this.parseLocale_ = parseLocale_;
    }
    
    private void init() {
        final String s = null;
        this.timeZone_ = s;
        this.pattern_ = s;
        this.timeStyle_ = s;
        this.dateStyle_ = s;
        this.type_ = s;
        this.value_ = s;
        this.parseLocale_ = null;
    }
    
    private void evaluateExpressions() throws JspException {
        if (this.value_ != null) {
            this.value = (String)ExpressionEvaluatorManager.evaluate("value", this.value_, String.class, (Tag)this, this.pageContext);
        }
        if (this.type_ != null) {
            this.type = (String)ExpressionEvaluatorManager.evaluate("type", this.type_, String.class, (Tag)this, this.pageContext);
        }
        if (this.dateStyle_ != null) {
            this.dateStyle = (String)ExpressionEvaluatorManager.evaluate("dateStyle", this.dateStyle_, String.class, (Tag)this, this.pageContext);
        }
        if (this.timeStyle_ != null) {
            this.timeStyle = (String)ExpressionEvaluatorManager.evaluate("timeStyle", this.timeStyle_, String.class, (Tag)this, this.pageContext);
        }
        if (this.pattern_ != null) {
            this.pattern = (String)ExpressionEvaluatorManager.evaluate("pattern", this.pattern_, String.class, (Tag)this, this.pageContext);
        }
        if (this.timeZone_ != null) {
            this.timeZone = ExpressionEvaluatorManager.evaluate("timeZone", this.timeZone_, Object.class, (Tag)this, this.pageContext);
        }
        if (this.parseLocale_ != null) {
            final Object obj = ExpressionEvaluatorManager.evaluate("parseLocale", this.parseLocale_, Object.class, (Tag)this, this.pageContext);
            final Locale locale = LocaleUtil.parseLocaleAttributeValue(obj);
            if (locale != null) {
                this.parseLocale = locale;
            }
        }
    }
}
