package sun.net.www.protocol.https;

import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

public final class DefaultHostnameVerifier implements HostnameVerifier
{
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return false;
    }
}
