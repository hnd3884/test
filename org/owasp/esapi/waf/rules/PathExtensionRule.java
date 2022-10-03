package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class PathExtensionRule extends Rule
{
    private Pattern allow;
    private Pattern deny;
    
    public PathExtensionRule(final String id, final Pattern allow, final Pattern deny) {
        this.allow = allow;
        this.deny = deny;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        if (this.allow != null && this.allow.matcher(request.getRequestURI()).matches()) {
            return new DoNothingAction();
        }
        if (this.deny != null && this.deny.matcher(request.getRequestURI()).matches()) {
            this.log(request, "Disallowed extension pattern '" + this.deny.pattern() + "' found on URI '" + request.getRequestURI() + "'");
            return new DefaultAction();
        }
        return new DoNothingAction();
    }
}
