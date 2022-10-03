package sun.net.www.protocol.http.ntlm;

import java.util.Base64;
import java.io.IOException;

public class NTLMAuthSequence
{
    private String username;
    private String password;
    private String ntdomain;
    private int state;
    private long crdHandle;
    private long ctxHandle;
    Status status;
    
    NTLMAuthSequence(final String username, final String password, final String ntdomain) throws IOException {
        this.username = username;
        this.password = password;
        this.ntdomain = ntdomain;
        this.status = new Status();
        this.state = 0;
        this.crdHandle = this.getCredentialsHandle(username, ntdomain, password);
        if (this.crdHandle == 0L) {
            throw new IOException("could not get credentials handle");
        }
    }
    
    public String getAuthHeader(final String s) throws IOException {
        byte[] decode = null;
        assert !this.status.sequenceComplete;
        if (s != null) {
            decode = Base64.getDecoder().decode(s);
        }
        final byte[] nextToken = this.getNextToken(this.crdHandle, decode, this.status);
        if (nextToken == null) {
            throw new IOException("Internal authentication error");
        }
        return Base64.getEncoder().encodeToString(nextToken);
    }
    
    public boolean isComplete() {
        return this.status.sequenceComplete;
    }
    
    private static native void initFirst(final Class<Status> p0);
    
    private native long getCredentialsHandle(final String p0, final String p1, final String p2);
    
    private native byte[] getNextToken(final long p0, final byte[] p1, final Status p2);
    
    static {
        initFirst(Status.class);
    }
    
    class Status
    {
        boolean sequenceComplete;
    }
}
