package org.owasp.esapi.waf.actions;

public abstract class Action
{
    protected boolean failed;
    protected boolean actionNecessary;
    
    public Action() {
        this.failed = true;
        this.actionNecessary = true;
    }
    
    public void setFailed(final boolean didFail) {
        this.failed = didFail;
    }
    
    public boolean failedRule() {
        return this.failed;
    }
    
    public boolean isActionNecessary() {
        return this.actionNecessary;
    }
    
    public void setActionNecessary(final boolean b) {
        this.actionNecessary = b;
    }
}
