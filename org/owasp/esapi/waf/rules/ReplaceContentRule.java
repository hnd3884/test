package org.owasp.esapi.waf.rules;

import java.util.regex.Matcher;
import java.io.UnsupportedEncodingException;
import org.owasp.esapi.Logger;
import java.io.IOException;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class ReplaceContentRule extends Rule
{
    private Pattern pattern;
    private String replacement;
    private Pattern contentType;
    private Pattern path;
    
    public ReplaceContentRule(final String id, final Pattern pattern, final String replacement, final Pattern contentType, final Pattern path) {
        this.pattern = pattern;
        this.replacement = replacement;
        this.path = path;
        this.contentType = contentType;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final String uri = request.getRequestURI();
        if (this.path != null && !this.path.matcher(uri).matches()) {
            return new DoNothingAction();
        }
        if (this.contentType != null && response.getContentType() != null && !this.contentType.matcher(response.getContentType()).matches()) {
            return new DoNothingAction();
        }
        byte[] bytes = null;
        try {
            bytes = response.getInterceptingServletOutputStream().getResponseBytes();
        }
        catch (final IOException ioe) {
            this.log(request, "Error matching pattern '" + this.pattern.pattern() + "', IOException encountered (possibly too large?): " + ioe.getMessage() + " (in response to URL: '" + (Object)request.getRequestURL() + "')");
            return new DoNothingAction();
        }
        try {
            final String s = new String(bytes, response.getCharacterEncoding());
            final Matcher m = this.pattern.matcher(s);
            final String canary = m.replaceAll(this.replacement);
            try {
                if (!s.equals(canary)) {
                    response.getInterceptingServletOutputStream().setResponseBytes(canary.getBytes(response.getCharacterEncoding()));
                    ReplaceContentRule.logger.debug(Logger.SECURITY_SUCCESS, "Successfully replaced pattern '" + this.pattern.pattern() + "' on response to URL '" + (Object)request.getRequestURL() + "'");
                }
            }
            catch (final IOException ioe2) {
                ReplaceContentRule.logger.error(Logger.SECURITY_FAILURE, "Failed to replace pattern '" + this.pattern.pattern() + "' on response to URL '" + (Object)request.getRequestURL() + "' due to [" + ioe2.getMessage() + "]");
            }
        }
        catch (final UnsupportedEncodingException uee) {
            ReplaceContentRule.logger.error(Logger.SECURITY_FAILURE, "Failed to replace pattern '" + this.pattern.pattern() + "' on response to URL '" + (Object)request.getRequestURL() + "' due to [" + uee.getMessage() + "]");
        }
        return new DoNothingAction();
    }
}
