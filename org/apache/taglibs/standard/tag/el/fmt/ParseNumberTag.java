package org.apache.taglibs.standard.tag.el.fmt;

import java.util.Locale;
import org.apache.taglibs.standard.tag.common.fmt.LocaleUtil;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.ParseNumberSupport;

public class ParseNumberTag extends ParseNumberSupport
{
    private String value_;
    private String type_;
    private String pattern_;
    private String parseLocale_;
    private String integerOnly_;
    
    public ParseNumberTag() {
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
    
    public void setPattern(final String pattern_) {
        this.pattern_ = pattern_;
    }
    
    public void setParseLocale(final String parseLocale_) {
        this.parseLocale_ = parseLocale_;
    }
    
    public void setIntegerOnly(final String integerOnly_) {
        this.integerOnly_ = integerOnly_;
        this.integerOnlySpecified = true;
    }
    
    private void init() {
        final String value_ = null;
        this.integerOnly_ = value_;
        this.parseLocale_ = value_;
        this.pattern_ = value_;
        this.type_ = value_;
        this.value_ = value_;
    }
    
    private void evaluateExpressions() throws JspException {
        Object obj = null;
        if (this.value_ != null) {
            this.value = (String)ExpressionEvaluatorManager.evaluate("value", this.value_, String.class, (Tag)this, this.pageContext);
        }
        if (this.type_ != null) {
            this.type = (String)ExpressionEvaluatorManager.evaluate("type", this.type_, String.class, (Tag)this, this.pageContext);
        }
        if (this.pattern_ != null) {
            this.pattern = (String)ExpressionEvaluatorManager.evaluate("pattern", this.pattern_, String.class, (Tag)this, this.pageContext);
        }
        if (this.parseLocale_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("parseLocale", this.parseLocale_, Object.class, (Tag)this, this.pageContext);
            final Locale locale = LocaleUtil.parseLocaleAttributeValue(obj);
            if (locale != null) {
                this.parseLocale = locale;
            }
        }
        if (this.integerOnly_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("integerOnly", this.integerOnly_, Boolean.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.isIntegerOnly = (boolean)obj;
            }
        }
    }
}
