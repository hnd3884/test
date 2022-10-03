package org.apache.http.impl.client;

import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.Header;
import java.util.Queue;
import org.apache.http.HttpResponse;
import java.util.Map;
import org.apache.http.protocol.HttpContext;
import org.apache.http.auth.AuthScheme;
import org.apache.http.HttpHost;
import java.util.Collection;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final ProxyAuthenticationStrategy INSTANCE;
    
    public ProxyAuthenticationStrategy() {
        super(407, "Proxy-Authenticate");
    }
    
    @Override
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getProxyPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new ProxyAuthenticationStrategy();
    }
}
