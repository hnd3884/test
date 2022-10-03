package org.apache.tomcat.websocket;

import java.nio.charset.Charset;
import org.apache.tomcat.util.codec.binary.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BasicAuthenticator extends Authenticator
{
    public static final String schemeName = "basic";
    public static final String charsetparam = "charset";
    
    @Override
    public String getAuthorization(final String requestUri, final String WWWAuthenticate, final Map<String, Object> userProperties) throws AuthenticationException {
        final String userName = userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_USER_NAME");
        final String password = userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_PASSWORD");
        if (userName == null || password == null) {
            throw new AuthenticationException("Failed to perform Basic authentication due to  missing user/password");
        }
        final Map<String, String> wwwAuthenticate = this.parseWWWAuthenticateHeader(WWWAuthenticate);
        final String userPass = userName + ":" + password;
        Charset charset;
        if (wwwAuthenticate.get("charset") != null && wwwAuthenticate.get("charset").equalsIgnoreCase("UTF-8")) {
            charset = StandardCharsets.UTF_8;
        }
        else {
            charset = StandardCharsets.ISO_8859_1;
        }
        final String base64 = Base64.encodeBase64String(userPass.getBytes(charset));
        return " Basic " + base64;
    }
    
    @Override
    public String getSchemeName() {
        return "basic";
    }
}
