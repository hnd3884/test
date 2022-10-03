package org.owasp.esapi.waf.rules;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.Logger;

public abstract class Rule
{
    protected String id;
    protected static Logger logger;
    
    public Rule() {
        this.id = "(no rule ID)";
    }
    
    public abstract Action check(final HttpServletRequest p0, final InterceptingHTTPServletResponse p1, final HttpServletResponse p2);
    
    public void log(final HttpServletRequest request, final String message) {
        Rule.logger.warning(Logger.SECURITY_FAILURE, "[IP=" + request.getRemoteAddr() + ",Rule=" + this.getClass().getSimpleName() + ",ID=" + this.id + "] " + message);
    }
    
    protected void setId(final String id) {
        if (id == null || "".equals(id)) {
            return;
        }
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "Rule:" + this.getClass().getName();
    }
    
    static {
        Rule.logger = ESAPI.getLogger(Rule.class);
    }
}
