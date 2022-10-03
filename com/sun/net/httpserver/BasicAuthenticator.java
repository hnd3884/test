package com.sun.net.httpserver;

import java.util.Base64;
import jdk.Exported;

@Exported
public abstract class BasicAuthenticator extends Authenticator
{
    protected String realm;
    
    public BasicAuthenticator(final String realm) {
        this.realm = realm;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    @Override
    public Result authenticate(final HttpExchange httpExchange) {
        final String first = httpExchange.getRequestHeaders().getFirst("Authorization");
        if (first == null) {
            httpExchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
            return new Retry(401);
        }
        final int index = first.indexOf(32);
        if (index == -1 || !first.substring(0, index).equals("Basic")) {
            return new Failure(401);
        }
        final String s = new String(Base64.getDecoder().decode(first.substring(index + 1)));
        final int index2 = s.indexOf(58);
        final String substring = s.substring(0, index2);
        if (this.checkCredentials(substring, s.substring(index2 + 1))) {
            return new Success(new HttpPrincipal(substring, this.realm));
        }
        httpExchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
        return new Failure(401);
    }
    
    public abstract boolean checkCredentials(final String p0, final String p1);
}
