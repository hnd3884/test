package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.BlockAction;
import org.owasp.esapi.waf.configuration.AppGuardianConfiguration;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class RestrictUserAgentRule extends Rule
{
    private static final String USER_AGENT_HEADER = "User-Agent";
    private Pattern allow;
    private Pattern deny;
    
    public RestrictUserAgentRule(final String id, final Pattern allow, final Pattern deny) {
        this.allow = allow;
        this.deny = deny;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "";
        }
        if (this.allow != null) {
            if (this.allow.matcher(userAgent).matches()) {
                return new DoNothingAction();
            }
        }
        else if (this.deny != null && !this.deny.matcher(userAgent).matches()) {
            return new DoNothingAction();
        }
        this.log(request, "Disallowed user agent pattern '" + this.deny.pattern() + "' found in user agent '" + request.getHeader("User-Agent") + "'");
        if (AppGuardianConfiguration.DEFAULT_FAIL_ACTION == 1) {
            return new BlockAction();
        }
        return new DefaultAction();
    }
}
