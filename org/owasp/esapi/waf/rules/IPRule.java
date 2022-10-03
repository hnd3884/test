package org.owasp.esapi.waf.rules;

import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class IPRule extends Rule
{
    private Pattern allowedIP;
    private String exactPath;
    private Pattern path;
    private boolean useExactPath;
    private String ipHeader;
    
    public IPRule(final String id, final Pattern allowedIP, final Pattern path, final String ipHeader) {
        this.useExactPath = false;
        this.allowedIP = allowedIP;
        this.path = path;
        this.useExactPath = false;
        this.ipHeader = ipHeader;
        this.setId(id);
    }
    
    public IPRule(final String id, final Pattern allowedIP, final String exactPath) {
        this.useExactPath = false;
        this.path = null;
        this.exactPath = exactPath;
        this.useExactPath = true;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final String uri = request.getRequestURI();
        if ((!this.useExactPath && this.path.matcher(uri).matches()) || (this.useExactPath && this.exactPath.equals(uri))) {
            String sourceIP = request.getRemoteAddr() + "";
            if (this.ipHeader != null) {
                sourceIP = request.getHeader(this.ipHeader);
            }
            if (!this.allowedIP.matcher(sourceIP).matches()) {
                this.log(request, "IP not allowed to access URI '" + uri + "'");
                return new DefaultAction();
            }
        }
        return new DoNothingAction();
    }
}
