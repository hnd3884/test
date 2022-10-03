package org.owasp.esapi.waf.rules;

import java.util.Enumeration;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletRequest;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class GeneralAttackSignatureRule extends Rule
{
    private Pattern signature;
    
    public GeneralAttackSignatureRule(final String id, final Pattern signature) {
        this.signature = signature;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest req, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final InterceptingHTTPServletRequest request = (InterceptingHTTPServletRequest)req;
        final Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            final String param = e.nextElement();
            if (this.signature.matcher(request.getDictionaryParameter(param)).matches()) {
                this.log((HttpServletRequest)request, "General attack signature detected in parameter '" + param + "' value '" + request.getDictionaryParameter(param) + "'");
                return new DefaultAction();
            }
        }
        return new DoNothingAction();
    }
}
