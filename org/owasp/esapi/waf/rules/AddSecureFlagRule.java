package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import java.util.List;

public class AddSecureFlagRule extends Rule
{
    private List<Pattern> name;
    
    public AddSecureFlagRule(final String id, final List<Pattern> name) {
        this.name = name;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final DoNothingAction action = new DoNothingAction();
        return action;
    }
    
    public boolean doesCookieMatch(final String cookieName) {
        for (int i = 0; i < this.name.size(); ++i) {
            final Pattern p = this.name.get(i);
            if (p.matcher(cookieName).matches()) {
                return true;
            }
        }
        return false;
    }
}
