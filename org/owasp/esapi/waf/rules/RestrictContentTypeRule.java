package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class RestrictContentTypeRule extends Rule
{
    private Pattern allow;
    private Pattern deny;
    
    public RestrictContentTypeRule(final String id, final Pattern allow, final Pattern deny) {
        this.allow = allow;
        this.deny = deny;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        if (request.getContentType() == null) {
            return new DoNothingAction();
        }
        if (this.allow != null) {
            if (this.allow.matcher(request.getContentType()).matches()) {
                return new DoNothingAction();
            }
            this.log(request, "Disallowed content type based on allow pattern '" + this.allow.pattern() + "' found on URI '" + request.getRequestURI() + "' (value was '" + request.getContentType() + "')");
        }
        else if (this.deny != null) {
            if (!this.deny.matcher(request.getContentType()).matches()) {
                return new DoNothingAction();
            }
            this.log(request, "Disallowed content type based on deny pattern '" + this.deny.pattern() + "' found on URI '" + request.getRequestURI() + "' (value was '" + request.getContentType() + ")'");
        }
        return new DefaultAction();
    }
}
