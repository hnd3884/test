package com.me.mdm.agent.servlets.auth;

import java.io.IOException;
import sun.misc.BASE64Decoder;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public abstract class BasicAuthenticatedRequestServlet extends UserAuthenticatedRequestServlet
{
    public boolean authenticate(final HttpServletRequest request, final HttpServletResponse response, final JSONObject requestJSON) throws Exception {
        final String authorization = this.getAuthorization(request);
        if (authorization != null && !authorization.isEmpty() && authorization.trim().toLowerCase().startsWith("basic")) {
            final String username = this.parseUsername(authorization);
            final String password = this.parsePassword(authorization);
            final Boolean authenticated = this.authenticate(username, password, request, response, requestJSON);
            if (authenticated) {
                return true;
            }
        }
        response.setHeader("WWW-Authenticate", "Basic");
        response.setStatus(401);
        return false;
    }
    
    public abstract boolean authenticate(final String p0, final String p1, final HttpServletRequest p2, final HttpServletResponse p3, final JSONObject p4) throws Exception;
    
    protected String parseUsername(String authorization) throws IOException {
        if (authorization == null) {
            return null;
        }
        if (!authorization.toLowerCase().startsWith("basic")) {
            return null;
        }
        authorization = authorization.substring(6).trim();
        final BASE64Decoder decoder = new BASE64Decoder();
        final byte[] bytes = decoder.decodeBuffer(authorization);
        final String unencoded = new String(bytes);
        final int colon = unencoded.indexOf(58);
        if (colon < 0) {
            return null;
        }
        final String username = unencoded.substring(0, colon).trim();
        return username;
    }
    
    protected String parsePassword(String authorization) throws IOException {
        if (authorization == null) {
            return null;
        }
        if (!authorization.toLowerCase().startsWith("basic")) {
            return null;
        }
        authorization = authorization.substring(6).trim();
        final BASE64Decoder decoder = new BASE64Decoder();
        final byte[] bytes = decoder.decodeBuffer(authorization);
        final String unencoded = new String(bytes);
        final int colon = unencoded.indexOf(58);
        if (colon < 0) {
            return null;
        }
        final String password = unencoded.substring(colon + 1).trim();
        return password;
    }
    
    protected String getAuthorization(final HttpServletRequest request) {
        return request.getHeader("authorization");
    }
}
