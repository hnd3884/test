package org.owasp.esapi.waf.actions;

public class DoNothingAction extends Action
{
    @Override
    public boolean failedRule() {
        return this.failed;
    }
    
    @Override
    public boolean isActionNecessary() {
        return false;
    }
}
