package com.adventnet.iam.security;

import org.w3c.dom.Element;
import javax.servlet.http.HttpServletRequest;

public class RACProviderImpl implements RACProvider
{
    @Override
    public RequestEntities getRACRequestEntities() {
        return new RequestEntities();
    }
    
    @Override
    public void setCurrentRequest(final HttpServletRequest request) {
    }
    
    @Override
    public ChildActionRule getChildActionRule(final Element elem, final ActionRule actionRule) {
        return new ChildActionRule(elem, actionRule);
    }
    
    @Override
    public void setThreadLocalActionRule(final ChildActionRule ch) {
    }
}
