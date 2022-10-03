package org.glassfish.jersey.client.authentication;

import javax.ws.rs.core.Response;
import javax.ws.rs.client.ClientResponseContext;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import javax.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.internal.util.Base64;

final class BasicAuthenticator
{
    private final HttpAuthenticationFilter.Credentials defaultCredentials;
    
    BasicAuthenticator(final HttpAuthenticationFilter.Credentials defaultCredentials) {
        this.defaultCredentials = defaultCredentials;
    }
    
    private String calculateAuthentication(final HttpAuthenticationFilter.Credentials credentials) {
        String username = credentials.getUsername();
        byte[] password = credentials.getPassword();
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = new byte[0];
        }
        final byte[] prefix = (username + ":").getBytes(HttpAuthenticationFilter.CHARACTER_SET);
        final byte[] usernamePassword = new byte[prefix.length + password.length];
        System.arraycopy(prefix, 0, usernamePassword, 0, prefix.length);
        System.arraycopy(password, 0, usernamePassword, prefix.length, password.length);
        return "Basic " + Base64.encodeAsString(usernamePassword);
    }
    
    public void filterRequest(final ClientRequestContext request) throws RequestAuthenticationException {
        final HttpAuthenticationFilter.Credentials credentials = HttpAuthenticationFilter.getCredentials(request, this.defaultCredentials, HttpAuthenticationFilter.Type.BASIC);
        if (credentials == null) {
            throw new RequestAuthenticationException(LocalizationMessages.AUTHENTICATION_CREDENTIALS_MISSING_BASIC());
        }
        request.getHeaders().add((Object)"Authorization", (Object)this.calculateAuthentication(credentials));
    }
    
    public boolean filterResponseAndAuthenticate(final ClientRequestContext request, final ClientResponseContext response) {
        final String authenticate = (String)response.getHeaders().getFirst((Object)"WWW-Authenticate");
        if (authenticate == null || !authenticate.trim().toUpperCase().startsWith("BASIC")) {
            return false;
        }
        final HttpAuthenticationFilter.Credentials credentials = HttpAuthenticationFilter.getCredentials(request, this.defaultCredentials, HttpAuthenticationFilter.Type.BASIC);
        if (credentials == null) {
            throw new ResponseAuthenticationException(null, LocalizationMessages.AUTHENTICATION_CREDENTIALS_MISSING_BASIC());
        }
        return HttpAuthenticationFilter.repeatRequest(request, response, this.calculateAuthentication(credentials));
    }
}
