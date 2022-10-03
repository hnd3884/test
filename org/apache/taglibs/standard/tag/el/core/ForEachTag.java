package org.apache.taglibs.standard.tag.el.core;

import java.util.ArrayList;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.jstl.core.LoopTag;
import org.apache.taglibs.standard.tag.common.core.ForEachSupport;

public class ForEachTag extends ForEachSupport implements LoopTag, IterationTag
{
    private String begin_;
    private String end_;
    private String step_;
    private String items_;
    
    public ForEachTag() {
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
    
    private void init() {
        this.begin_ = null;
        this.end_ = null;
        this.step_ = null;
        this.items_ = null;
    }
    
    private void evaluateExpressions() throws JspException {
        if (this.begin_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("begin", this.begin_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forEach", "begin");
            }
            this.begin = (int)r;
            this.validateBegin();
        }
        if (this.end_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("end", this.end_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forEach", "end");
            }
            this.end = (int)r;
            this.validateEnd();
        }
        if (this.step_ != null) {
            final Object r = ExpressionEvaluatorManager.evaluate("step", this.step_, Integer.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("forEach", "step");
            }
            this.step = (int)r;
            this.validateStep();
        }
        if (this.items_ != null) {
            this.rawItems = ExpressionEvaluatorManager.evaluate("items", this.items_, Object.class, (Tag)this, this.pageContext);
            if (this.rawItems == null) {
                this.rawItems = new ArrayList();
            }
        }
    }
}
