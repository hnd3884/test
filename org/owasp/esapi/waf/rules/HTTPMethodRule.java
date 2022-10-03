package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class HTTPMethodRule extends Rule
{
    private Pattern allowedMethods;
    private Pattern deniedMethods;
    private Pattern path;
    
    public HTTPMethodRule(final String id, final Pattern allowedMethods, final Pattern deniedMethods, final Pattern path) {
        this.allowedMethods = allowedMethods;
        this.deniedMethods = deniedMethods;
        this.path = path;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final String uri = request.getRequestURI();
        final String method = request.getMethod();
        if (this.path == null || this.path.matcher(uri).matches()) {
            if (this.allowedMethods != null && this.allowedMethods.matcher(method).matches()) {
                return new DoNothingAction();
            }
            if (this.allowedMethods != null) {
                this.log(request, "Disallowed HTTP method '" + request.getMethod() + "' found for URL: " + (Object)request.getRequestURL());
                return new DefaultAction();
            }
            if (this.deniedMethods != null && this.deniedMethods.matcher(method).matches()) {
                this.log(request, "Disallowed HTTP method '" + request.getMethod() + "' found for URL: " + (Object)request.getRequestURL());
                return new DefaultAction();
            }
        }
        return new DoNothingAction();
    }
}
