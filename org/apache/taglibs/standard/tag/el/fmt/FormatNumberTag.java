package org.apache.taglibs.standard.tag.el.fmt;

import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.FormatNumberSupport;

public class FormatNumberTag extends FormatNumberSupport
{
    private String value_;
    private String type_;
    private String pattern_;
    private String currencyCode_;
    private String currencySymbol_;
    private String groupingUsed_;
    private String maxIntegerDigits_;
    private String minIntegerDigits_;
    private String maxFractionDigits_;
    private String minFractionDigits_;
    
    public FormatNumberTag() {
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
    
    public void setCurrencyCode(final String currencyCode_) {
        this.currencyCode_ = currencyCode_;
    }
    
    public void setCurrencySymbol(final String currencySymbol_) {
        this.currencySymbol_ = currencySymbol_;
    }
    
    public void setGroupingUsed(final String groupingUsed_) {
        this.groupingUsed_ = groupingUsed_;
        this.groupingUsedSpecified = true;
    }
    
    public void setMaxIntegerDigits(final String maxIntegerDigits_) {
        this.maxIntegerDigits_ = maxIntegerDigits_;
        this.maxIntegerDigitsSpecified = true;
    }
    
    public void setMinIntegerDigits(final String minIntegerDigits_) {
        this.minIntegerDigits_ = minIntegerDigits_;
        this.minIntegerDigitsSpecified = true;
    }
    
    public void setMaxFractionDigits(final String maxFractionDigits_) {
        this.maxFractionDigits_ = maxFractionDigits_;
        this.maxFractionDigitsSpecified = true;
    }
    
    public void setMinFractionDigits(final String minFractionDigits_) {
        this.minFractionDigits_ = minFractionDigits_;
        this.minFractionDigitsSpecified = true;
    }
    
    private void init() {
        final String value_ = null;
        this.pattern_ = value_;
        this.type_ = value_;
        this.value_ = value_;
        final String s = null;
        this.currencySymbol_ = s;
        this.currencyCode_ = s;
        this.groupingUsed_ = null;
        final String s2 = null;
        this.minIntegerDigits_ = s2;
        this.maxIntegerDigits_ = s2;
        final String s3 = null;
        this.minFractionDigits_ = s3;
        this.maxFractionDigits_ = s3;
    }
    
    private void evaluateExpressions() throws JspException {
        Object obj = null;
        if (this.value_ != null) {
            this.value = ExpressionEvaluatorManager.evaluate("value", this.value_, Object.class, (Tag)this, this.pageContext);
        }
        if (this.type_ != null) {
            this.type = (String)ExpressionEvaluatorManager.evaluate("type", this.type_, String.class, (Tag)this, this.pageContext);
        }
        if (this.pattern_ != null) {
            this.pattern = (String)ExpressionEvaluatorManager.evaluate("pattern", this.pattern_, String.class, (Tag)this, this.pageContext);
        }
        if (this.currencyCode_ != null) {
            this.currencyCode = (String)ExpressionEvaluatorManager.evaluate("currencyCode", this.currencyCode_, String.class, (Tag)this, this.pageContext);
        }
        if (this.currencySymbol_ != null) {
            this.currencySymbol = (String)ExpressionEvaluatorManager.evaluate("currencySymbol", this.currencySymbol_, String.class, (Tag)this, this.pageContext);
        }
        if (this.groupingUsed_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("groupingUsed", this.groupingUsed_, Boolean.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.isGroupingUsed = (boolean)obj;
            }
        }
        if (this.maxIntegerDigits_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("maxIntegerDigits", this.maxIntegerDigits_, Integer.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.maxIntegerDigits = (int)obj;
            }
        }
        if (this.minIntegerDigits_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("minIntegerDigits", this.minIntegerDigits_, Integer.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.minIntegerDigits = (int)obj;
            }
        }
        if (this.maxFractionDigits_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("maxFractionDigits", this.maxFractionDigits_, Integer.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.maxFractionDigits = (int)obj;
            }
        }
        if (this.minFractionDigits_ != null) {
            obj = ExpressionEvaluatorManager.evaluate("minFractionDigits", this.minFractionDigits_, Integer.class, (Tag)this, this.pageContext);
            if (obj != null) {
                this.minFractionDigits = (int)obj;
            }
        }
    }
}
