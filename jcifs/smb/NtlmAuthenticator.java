package jcifs.smb;

public abstract class NtlmAuthenticator
{
    private static NtlmAuthenticator auth;
    private String url;
    private SmbAuthException sae;
    
    private void reset() {
        this.url = null;
        this.sae = null;
    }
    
    public static synchronized void setDefault(final NtlmAuthenticator a) {
        if (NtlmAuthenticator.auth != null) {
            return;
        }
        NtlmAuthenticator.auth = a;
    }
    
    protected final String getRequestingURL() {
        return this.url;
    }
    
    protected final SmbAuthException getRequestingException() {
        return this.sae;
    }
    
    public static NtlmPasswordAuthentication requestNtlmPasswordAuthentication(final String url, final SmbAuthException sae) {
        if (NtlmAuthenticator.auth == null) {
            return null;
        }
        synchronized (NtlmAuthenticator.auth) {
            NtlmAuthenticator.auth.url = url;
            NtlmAuthenticator.auth.sae = sae;
            return NtlmAuthenticator.auth.getNtlmPasswordAuthentication();
        }
    }
    
    protected NtlmPasswordAuthentication getNtlmPasswordAuthentication() {
        return null;
    }
}
