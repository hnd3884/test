package org.owasp.esapi.waf.rules;

import java.io.UnsupportedEncodingException;
import org.owasp.esapi.waf.actions.DefaultAction;
import java.io.IOException;
import org.owasp.esapi.waf.configuration.AppGuardianConfiguration;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class DetectOutboundContentRule extends Rule
{
    private Pattern contentType;
    private Pattern pattern;
    private Pattern uri;
    
    public DetectOutboundContentRule(final String id, final Pattern contentType, final Pattern pattern, final Pattern uri) {
        this.contentType = contentType;
        this.pattern = pattern;
        this.uri = uri;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        if (this.uri != null && !this.uri.matcher(request.getRequestURI()).matches()) {
            return new DoNothingAction();
        }
        String inboundContentType;
        String charEnc;
        if (response != null) {
            if (response.getContentType() == null) {
                response.setContentType(AppGuardianConfiguration.DEFAULT_CONTENT_TYPE);
            }
            inboundContentType = response.getContentType();
            charEnc = response.getCharacterEncoding();
        }
        else {
            if (httpResponse.getContentType() == null) {
                httpResponse.setContentType(AppGuardianConfiguration.DEFAULT_CONTENT_TYPE);
            }
            inboundContentType = httpResponse.getContentType();
            charEnc = httpResponse.getCharacterEncoding();
        }
        if (this.contentType.matcher(inboundContentType).matches()) {
            try {
                byte[] bytes = null;
                try {
                    bytes = response.getInterceptingServletOutputStream().getResponseBytes();
                }
                catch (final IOException ioe) {
                    this.log(request, "Error matching pattern '" + this.pattern.pattern() + "', IOException encountered (possibly too large?): " + ioe.getMessage() + " (in response to URL: '" + (Object)request.getRequestURL() + "')");
                    return new DoNothingAction();
                }
                final String s = new String(bytes, charEnc);
                if (this.pattern.matcher(s).matches()) {
                    this.log(request, "Content pattern '" + this.pattern.pattern() + "' was found in response to URL: '" + (Object)request.getRequestURL() + "'");
                    return new DefaultAction();
                }
            }
            catch (final UnsupportedEncodingException uee) {
                this.log(request, "Content pattern '" + this.pattern.pattern() + "' could not be found due to encoding error: " + uee.getMessage());
            }
        }
        return new DoNothingAction();
    }
}
