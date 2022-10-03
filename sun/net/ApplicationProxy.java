package sun.net;

import java.net.Proxy;

public final class ApplicationProxy extends Proxy
{
    private ApplicationProxy(final Proxy proxy) {
        super(proxy.type(), proxy.address());
    }
    
    public static ApplicationProxy create(final Proxy proxy) {
        return new ApplicationProxy(proxy);
    }
}
