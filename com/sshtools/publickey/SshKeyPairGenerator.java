package com.sshtools.publickey;

import com.maverick.ssh.SshException;
import com.maverick.ssh.components.ComponentManager;
import java.io.IOException;
import com.maverick.ssh.components.SshKeyPair;

public class SshKeyPairGenerator
{
    public static final String SSH1_RSA = "rsa1";
    public static final String SSH2_RSA = "ssh-rsa";
    public static final String SSH2_DSA = "ssh-dss";
    
    public static SshKeyPair generateKeyPair(final String s, final int n) throws IOException, SshException {
        if (!"rsa1".equalsIgnoreCase(s) && !"ssh-rsa".equalsIgnoreCase(s) && !"ssh-dss".equalsIgnoreCase(s)) {
            throw new IOException(s + " is not a supported key algorithm!");
        }
        final SshKeyPair sshKeyPair = new SshKeyPair();
        SshKeyPair sshKeyPair2;
        if ("rsa1".equalsIgnoreCase(s)) {
            sshKeyPair2 = ComponentManager.getInstance().generateRsaKeyPair(n, 1);
        }
        else if ("ssh-rsa".equalsIgnoreCase(s)) {
            sshKeyPair2 = ComponentManager.getInstance().generateRsaKeyPair(n, 2);
        }
        else {
            sshKeyPair2 = ComponentManager.getInstance().generateDsaKeyPair(n);
        }
        return sshKeyPair2;
    }
}
