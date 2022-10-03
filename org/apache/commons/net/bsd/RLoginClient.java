package org.apache.commons.net.bsd;

import java.io.IOException;

public class RLoginClient extends RCommandClient
{
    public static final int DEFAULT_PORT = 513;
    
    public RLoginClient() {
        this.setDefaultPort(513);
    }
    
    public void rlogin(final String localUsername, final String remoteUsername, final String terminalType, final int terminalSpeed) throws IOException {
        this.rexec(localUsername, remoteUsername, terminalType + "/" + terminalSpeed, false);
    }
    
    public void rlogin(final String localUsername, final String remoteUsername, final String terminalType) throws IOException {
        this.rexec(localUsername, remoteUsername, terminalType, false);
    }
}
