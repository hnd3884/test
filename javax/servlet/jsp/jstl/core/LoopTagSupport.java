package javax.servlet.jsp.jstl.core;

import javax.el.VariableMapper;
import javax.el.ELContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.el.ValueExpression;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class LoopTagSupport extends TagSupport implements LoopTag, IterationTag, TryCatchFinally
{
    protected int begin;
    protected int end;
    protected int step;
    protected boolean beginSpecified;
    protected boolean endSpecified;
    protected boolean stepSpecified;
    protected String itemId;
    protected String statusId;
    protected ValueExpression deferredExpression;
    private LoopTagStatus status;
    private Object item;
    private int index;
    private int count;
    private boolean last;
    
    public LoopTagSupport() {
        this.init();
    }
    
    protected abstract Object next() throws JspTagException;
    
    protected abstract boolean hasNext() throws JspTagException;
    
    protected abstract void prepare() throws JspTagException;
    
    public void release() {
        super.release();
        this.init();
    }
    
    public int doStartTag() throws JspException {
        if (this.end != -1 && this.begin > this.end) {
            return 0;
        }
        this.index = 0;
        this.count = 1;
        this.last = false;
        this.prepare();
        this.discardIgnoreSubset(this.begin);
        if (this.hasNext()) {
            this.item = this.next();
            this.discard(this.step - 1);
            this.exposeVariables();
            this.calibrateLast();
            return 1;
        }
        return 0;
    }
    
    public int doAfterBody() throws JspException {
        this.index += this.step - 1;
        ++this.count;
        if (this.hasNext() && !this.atEnd()) {
            ++this.index;
            this.item = this.next();
            this.discard(this.step - 1);
            this.exposeVariables();
            this.calibrateLast();
            return 2;
        }
        return 0;
    }
    
    public void doFinally() {
        this.unExposeVariables();
    }
    
    public void doCatch(final Throwable t) throws Throwable {
        throw t;
    }
    
    public Object getCurrent() {
        return this.item;
    }
    
    public LoopTagStatus getLoopStatus() {
        if (this.status == null) {
            class Status implements LoopTagStatus
            {
                public Object getCurrent() {
                    return LoopTagSupport.this.getCurrent();
                }
                
                public int getIndex() {
                    return LoopTagSupport.this.index + LoopTagSupport.this.begin;
                }
                
                public int getCount() {
                    return LoopTagSupport.this.count;
                }
                
                public boolean isFirst() {
                    return LoopTagSupport.this.index == 0;
                }
                
                public boolean isLast() {
                    return LoopTagSupport.this.last;
                }
                
                public Integer getBegin() {
                    if (LoopTagSupport.this.beginSpecified) {
                        return new Integer(LoopTagSupport.this.begin);
                    }
                    return null;
                }
                
                public Integer getEnd() {
                    if (LoopTagSupport.this.endSpecified) {
                        return new Integer(LoopTagSupport.this.end);
                    }
                    return null;
                }
                
                public Integer getStep() {
                    if (LoopTagSupport.this.stepSpecified) {
                        return new Integer(LoopTagSupport.this.step);
                    }
                    return null;
                }
            }
            this.status = new Status();
        }
        return this.status;
    }
    
    public void setVar(final String id) {
        this.itemId = id;
    }
    
    public void setVarStatus(final String statusId) {
        this.statusId = statusId;
    }
    
    protected void validateBegin() throws JspTagException {
        if (this.begin < 0) {
            throw new JspTagException("'begin' < 0");
        }
    }
    
    protected void validateEnd() throws JspTagException {
        if (this.end < 0) {
            throw new JspTagException("'end' < 0");
        }
    }
    
    protected void validateStep() throws JspTagException {
        if (this.step < 1) {
            throw new JspTagException("'step' <= 0");
        }
    }
    
    private void init() {
        this.index = 0;
        this.count = 1;
        this.status = null;
        this.item = null;
        this.last = false;
        this.beginSpecified = false;
        this.endSpecified = false;
        this.stepSpecified = false;
        this.deferredExpression = null;
        this.begin = 0;
        this.end = -1;
        this.step = 1;
        this.itemId = null;
        this.statusId = null;
    }
    
    private void calibrateLast() throws JspTagException {
        this.last = (!this.hasNext() || this.atEnd() || (this.end != -1 && this.begin + this.index + this.step > this.end));
    }
    
    private void exposeVariables() throws JspTagException {
        if (this.deferredExpression == null) {
            if (this.itemId != null) {
                if (this.getCurrent() == null) {
                    this.pageContext.removeAttribute(this.itemId, 1);
                }
                else {
                    this.pageContext.setAttribute(this.itemId, this.getCurrent());
                }
            }
        }
        else {
            final ELContext myELContext = this.pageContext.getELContext();
            final VariableMapper vm = myELContext.getVariableMapper();
            vm.setVariable(this.itemId, (ValueExpression)this.getCurrent());
        }
        if (this.statusId != null) {
            if (this.getLoopStatus() == null) {
                this.pageContext.removeAttribute(this.statusId, 1);
            }
            else {
                this.pageContext.setAttribute(this.statusId, (Object)this.getLoopStatus());
            }
        }
    }
    
    private void unExposeVariables() {
        if (this.deferredExpression == null) {
            if (this.itemId != null) {
                this.pageContext.removeAttribute(this.itemId, 1);
            }
        }
        else {
            final ELContext myELContext = this.pageContext.getELContext();
            final VariableMapper vm = myELContext.getVariableMapper();
            vm.setVariable(this.itemId, (ValueExpression)null);
        }
        if (this.statusId != null) {
            this.pageContext.removeAttribute(this.statusId, 1);
        }
    }
    
    private void discard(int n) throws JspTagException {
        final int oldIndex = this.index;
        while (n-- > 0 && !this.atEnd() && this.hasNext()) {
            ++this.index;
            this.next();
        }
        this.index = oldIndex;
    }
    
    private void discardIgnoreSubset(int n) throws JspTagException {
        while (n-- > 0 && this.hasNext()) {
            this.next();
        }
    }
    
    private boolean atEnd() {
        return this.end != -1 && this.begin + this.index >= this.end;
    }
    
    protected String getDelims() {
        return ",";
    }
}
