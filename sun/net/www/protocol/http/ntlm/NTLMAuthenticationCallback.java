package sun.net.www.protocol.http.ntlm;

import java.net.URL;

public abstract class NTLMAuthenticationCallback
{
    private static volatile NTLMAuthenticationCallback callback;
    
    public static void setNTLMAuthenticationCallback(final NTLMAuthenticationCallback callback) {
        NTLMAuthenticationCallback.callback = callback;
    }
    
    public static NTLMAuthenticationCallback getNTLMAuthenticationCallback() {
        return NTLMAuthenticationCallback.callback;
    }
    
    public abstract boolean isTrustedSite(final URL p0);
}
