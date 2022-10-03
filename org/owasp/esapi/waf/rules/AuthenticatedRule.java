package org.owasp.esapi.waf.rules;

import java.util.Iterator;
import javax.servlet.http.HttpSession;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

public class AuthenticatedRule extends Rule
{
    private String sessionAttribute;
    private Pattern path;
    private List<Object> exceptions;
    
    public AuthenticatedRule(final String id, final String sessionAttribute, final Pattern path, final List<Object> exceptions) {
        this.sessionAttribute = sessionAttribute;
        this.path = path;
        this.exceptions = exceptions;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final HttpSession session = request.getSession();
        final String uri = request.getRequestURI();
        if (this.path != null && !this.path.matcher(uri).matches()) {
            return new DoNothingAction();
        }
        if (session != null && session.getAttribute(this.sessionAttribute) != null) {
            return new DoNothingAction();
        }
        for (final Object o : this.exceptions) {
            if (o instanceof Pattern) {
                final Pattern p = (Pattern)o;
                if (p.matcher(uri).matches()) {
                    return new DoNothingAction();
                }
                continue;
            }
            else {
                if (o instanceof String && uri.equals(o)) {
                    return new DoNothingAction();
                }
                continue;
            }
        }
        this.log(request, "User requested unauthenticated access to URI '" + request.getRequestURI() + "' [querystring=" + request.getQueryString() + "]");
        return new DefaultAction();
    }
}
