package org.apache.taglibs.standard.tag.el.core;

import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.jstl.core.LoopTag;
import org.apache.taglibs.standard.tag.common.core.ForTokensSupport;

public class ForTokensTag extends ForTokensSupport implements LoopTag, IterationTag
{
    private String begin_;
    private String end_;
    private String step_;
    private String items_;
    private String delims_;
    
    public ForTokensTag() {
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
    
    public void setBegin(final String begin_) {
        this.begin_ = begin_;
        this.beginSpecified = true;
    }
    
    public void setEnd(final String end_) {
        this.end_ = end_;
        this.endSpecified = true;
    }
    
    public void setStep(final String step_) {
        this.step_ = step_;
        this.stepSpecified = true;
    }
    
    public void setItems(final String items_) {
        this.items_ = items_;
    }
    
    public void setDelims(final String delims_) {
        this.delims_ = delims_;
    }
    
    private void init() {
        this.begin_ = null;
        this.end_ = null;
        this.step_ = null;
        this.items_ = null;
        this.delims_ = null;
    }
    
    private void evaluateExpressions() throws JspException {
        if (this.begin_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("begin", this.begin_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forTokens", "begin");
            }
            this.begin = (int)r;
            this.validateBegin();
        }
        if (this.end_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("end", this.end_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forTokens", "end");
            }
            this.end = (int)r;
            this.validateEnd();
        }
        if (this.step_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("step", this.step_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forTokens", "step");
            }
            this.step = (int)r;
            this.validateStep();
        }
        if (this.items_ != null) {
            this.items = ExpressionEvaluatorManager.evaluate("items", this.items_, String.class, (Tag)this, this.pageContext);
            if (this.items == null) {
                this.items = "";
            }
        }
        if (this.delims_ != null) {
            this.delims = (String)ExpressionEvaluatorManager.evaluate("delims", this.delims_, String.class, (Tag)this, this.pageContext);
            if (this.delims == null) {
                this.delims = "";
            }
        }
    }
}
