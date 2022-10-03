package org.owasp.esapi.waf.rules;

import java.util.Iterator;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.RedirectAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

public class EnforceHTTPSRule extends Rule
{
    private Pattern path;
    private List<Object> exceptions;
    private String action;
    
    public EnforceHTTPSRule(final String id, final Pattern path, final List<Object> exceptions, final String action) {
        this.path = path;
        this.exceptions = exceptions;
        this.action = action;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        if (request.isSecure() || !this.path.matcher(request.getRequestURI()).matches()) {
            return new DoNothingAction();
        }
        for (final Object o : this.exceptions) {
            if (o instanceof String) {
                if (((String)o).equalsIgnoreCase(request.getRequestURI())) {
                    return new DoNothingAction();
                }
                continue;
            }
            else {
                if (o instanceof Pattern && ((Pattern)o).matcher(request.getRequestURI()).matches()) {
                    return new DoNothingAction();
                }
                continue;
            }
        }
        this.log(request, "Insecure request to resource detected in URL: '" + (Object)request.getRequestURL() + "'");
        if ("redirect".equals(this.action)) {
            final RedirectAction ra = new RedirectAction();
            ra.setRedirectURL(request.getRequestURL().toString().replaceFirst("http", "https"));
            return ra;
        }
        return new DefaultAction();
    }
}
