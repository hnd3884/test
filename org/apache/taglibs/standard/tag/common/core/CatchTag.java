package org.apache.taglibs.standard.tag.common.core;

import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.TagSupport;

public class CatchTag extends TagSupport implements TryCatchFinally
{
    private String var;
    private boolean caught;
    
    public CatchTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    private void init() {
        this.var = null;
    }
    
    public int doStartTag() {
        this.caught = false;
        return 1;
    }
    
    public void doCatch(final Throwable t) {
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)t, 1);
        }
        this.caught = true;
    }
    
    public void doFinally() {
        if (this.var != null && !this.caught) {
            this.pageContext.removeAttribute(this.var, 1);
        }
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
}
