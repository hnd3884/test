package org.owasp.esapi.waf.rules;

import java.util.Enumeration;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletRequest;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class SimpleVirtualPatchRule extends Rule
{
    private static final String REQUEST_PARAMETERS = "request.parameters.";
    private static final String REQUEST_HEADERS = "request.headers.";
    private Pattern path;
    private String variable;
    private Pattern valid;
    private String message;
    
    public SimpleVirtualPatchRule(final String id, final Pattern path, final String variable, final Pattern valid, final String message) {
        this.setId(id);
        this.path = path;
        this.variable = variable;
        this.valid = valid;
        this.message = message;
    }
    
    @Override
    public Action check(final HttpServletRequest req, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final InterceptingHTTPServletRequest request = (InterceptingHTTPServletRequest)req;
        final String uri = request.getRequestURI();
        if (!this.path.matcher(uri).matches()) {
            return new DoNothingAction();
        }
        String target = null;
        Enumeration en = null;
        boolean parameter = true;
        if (this.variable.startsWith("request.parameters.")) {
            target = this.variable.substring("request.parameters.".length());
            en = request.getParameterNames();
        }
        else {
            if (!this.variable.startsWith("request.headers.")) {
                this.log((HttpServletRequest)request, "Patch failed (improperly configured variable '" + this.variable + "')");
                return new DefaultAction();
            }
            parameter = false;
            target = this.variable.substring("request.headers.".length());
            en = request.getHeaderNames();
        }
        if (target.contains("*") || target.contains("?")) {
            target = target.replaceAll("\\*", ".*");
            final Pattern p = Pattern.compile(target);
            while (en.hasMoreElements()) {
                final String s = en.nextElement();
                String value = null;
                if (p.matcher(s).matches()) {
                    if (parameter) {
                        value = request.getDictionaryParameter(s);
                    }
                    else {
                        value = request.getHeader(s);
                    }
                    if (value != null && !this.valid.matcher(value).matches()) {
                        this.log((HttpServletRequest)request, "Virtual patch tripped on variable '" + this.variable + "' (specifically '" + s + "'). User input was '" + value + "' and legal pattern was '" + this.valid.pattern() + "': " + this.message);
                        return new DefaultAction();
                    }
                    continue;
                }
            }
            return new DoNothingAction();
        }
        if (parameter) {
            final String value2 = request.getDictionaryParameter(target);
            if (value2 == null || this.valid.matcher(value2).matches()) {
                return new DoNothingAction();
            }
            this.log((HttpServletRequest)request, "Virtual patch tripped on parameter '" + target + "'. User input was '" + value2 + "' and legal pattern was '" + this.valid.pattern() + "': " + this.message);
            return new DefaultAction();
        }
        else {
            final String value2 = request.getHeader(target);
            if (value2 == null || this.valid.matcher(value2).matches()) {
                return new DoNothingAction();
            }
            this.log((HttpServletRequest)request, "Virtual patch tripped on header '" + target + "'. User input was '" + value2 + "' and legal pattern was '" + this.valid.pattern() + "': " + this.message);
            return new DefaultAction();
        }
    }
}
