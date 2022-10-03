package org.owasp.esapi.waf.actions;

public class BlockAction extends Action
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
