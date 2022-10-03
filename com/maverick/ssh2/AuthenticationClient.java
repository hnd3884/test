package com.maverick.ssh2;

import com.maverick.ssh.SshException;
import com.maverick.ssh.SshAuthentication;

public interface AuthenticationClient extends SshAuthentication
{
    void authenticate(final AuthenticationProtocol p0, final String p1) throws SshException, AuthenticationResult;
}
