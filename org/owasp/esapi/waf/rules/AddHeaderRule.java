package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

public class AddHeaderRule extends Rule
{
    private String header;
    private String value;
    private Pattern path;
    private List<Object> exceptions;
    
    public AddHeaderRule(final String id, final String header, final String value, final Pattern path, final List<Object> exceptions) {
        this.setId(id);
        this.header = header;
        this.value = value;
        this.path = path;
        this.exceptions = exceptions;
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final DoNothingAction action = new DoNothingAction();
        if (this.path.matcher(request.getRequestURI()).matches()) {
            for (int i = 0; i < this.exceptions.size(); ++i) {
                final Object o = this.exceptions.get(i);
                if (o instanceof String) {
                    if (request.getRequestURI().equals(o)) {
                        action.setFailed(false);
                        action.setActionNecessary(false);
                        return action;
                    }
                }
                else if (o instanceof Pattern && ((Pattern)o).matcher(request.getRequestURI()).matches()) {
                    action.setFailed(false);
                    action.setActionNecessary(false);
                    return action;
                }
            }
            action.setFailed(true);
            action.setActionNecessary(false);
            if (response != null) {
                response.setHeader(this.header, this.value);
            }
            else {
                httpResponse.setHeader(this.header, this.value);
            }
        }
        return action;
    }
}
