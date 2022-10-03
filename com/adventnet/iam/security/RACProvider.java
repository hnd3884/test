package com.adventnet.iam.security;

import org.w3c.dom.Element;
import javax.servlet.http.HttpServletRequest;

public interface RACProvider
{
    RequestEntities getRACRequestEntities();
    
    void setCurrentRequest(final HttpServletRequest p0);
    
    ChildActionRule getChildActionRule(final Element p0, final ActionRule p1);
    
    void setThreadLocalActionRule(final ChildActionRule p0);
}
