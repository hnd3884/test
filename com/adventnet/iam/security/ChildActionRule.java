package com.adventnet.iam.security;

import org.w3c.dom.Element;

public class ChildActionRule
{
    private ActionRule actionRule;
    private String path;
    
    public ChildActionRule(final Element element, final ActionRule actionRule) {
        this.actionRule = null;
        this.path = null;
        this.actionRule = actionRule;
    }
    
    public ChildActionRule(final String path) {
        this.actionRule = null;
        this.path = null;
        this.path = path;
    }
    
    public ActionRule getActionRule() {
        return this.actionRule;
    }
}
