package org.apache.http.client.protocol;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.CookieStore;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;

@Deprecated
public class ClientContextConfigurer implements ClientContext
{
    private final HttpContext context;
    
    public ClientContextConfigurer(final HttpContext context) {
        Args.notNull((Object)context, "HTTP context");
        this.context = context;
    }
    
    public void setCookieSpecRegistry(final CookieSpecRegistry registry) {
        this.context.setAttribute("http.cookiespec-registry", (Object)registry);
    }
    
    public void setAuthSchemeRegistry(final AuthSchemeRegistry registry) {
        this.context.setAttribute("http.authscheme-registry", (Object)registry);
    }
    
    public void setCookieStore(final CookieStore store) {
        this.context.setAttribute("http.cookie-store", (Object)store);
    }
    
    public void setCredentialsProvider(final CredentialsProvider provider) {
        this.context.setAttribute("http.auth.credentials-provider", (Object)provider);
    }
}
