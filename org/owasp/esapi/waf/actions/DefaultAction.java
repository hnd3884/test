package org.owasp.esapi.waf.actions;

public class DefaultAction extends Action
{
    @Override
    public boolean failedRule() {
        return true;
    }
    
    @Override
    public boolean isActionNecessary() {
        return true;
    }
}
