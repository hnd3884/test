package org.apache.catalina.authenticator;

import org.apache.coyote.ActionCode;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;

public class SSLAuthenticator extends AuthenticatorBase
{
    @Override
    protected boolean doAuthenticate(final Request request, final HttpServletResponse response) throws IOException {
        if (this.checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)" Looking up certificates");
        }
        final X509Certificate[] certs = this.getRequestCertificates(request);
        if (certs == null || certs.length < 1) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"  No certificates included with this request");
            }
            response.sendError(401, SSLAuthenticator.sm.getString("authenticator.certificates"));
            return false;
        }
        final Principal principal = this.context.getRealm().authenticate(certs);
        if (principal == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"  Realm.authenticate() returned false");
            }
            response.sendError(401, SSLAuthenticator.sm.getString("authenticator.unauthorized"));
            return false;
        }
        this.register(request, response, principal, "CLIENT_CERT", null, null);
        return true;
    }
    
    @Override
    protected String getAuthMethod() {
        return "CLIENT_CERT";
    }
    
    @Override
    protected boolean isPreemptiveAuthPossible(final Request request) {
        final X509Certificate[] certs = this.getRequestCertificates(request);
        return certs != null && certs.length > 0;
    }
    
    protected X509Certificate[] getRequestCertificates(final Request request) throws IllegalStateException {
        X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null) {
            if (certs.length >= 1) {
                return certs;
            }
        }
        try {
            request.getCoyoteRequest().action(ActionCode.REQ_SSL_CERTIFICATE, (Object)null);
            certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        }
        catch (final IllegalStateException ex) {}
        return certs;
    }
}
