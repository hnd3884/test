package com.sshtools.publickey;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.maverick.ssh.SshKeyFingerprint;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.SshException;

public class ConsoleKnownHostsKeyVerification extends AbstractKnownHostsKeyVerification
{
    public ConsoleKnownHostsKeyVerification() throws SshException {
    }
    
    public ConsoleKnownHostsKeyVerification(final String s) throws SshException {
        super(s);
    }
    
    public void onHostKeyMismatch(final String s, final SshPublicKey sshPublicKey, final SshPublicKey sshPublicKey2) {
        try {
            System.out.println("The host key supplied by " + s + "(" + sshPublicKey.getAlgorithm() + ")" + " is: " + sshPublicKey2.getFingerprint());
            System.out.println("The current allowed key for " + s + " is: " + sshPublicKey.getFingerprint());
            this.d(s, sshPublicKey2);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void onUnknownHost(final String s, final SshPublicKey sshPublicKey) {
        try {
            System.out.println("The host " + s + " is currently unknown to the system");
            System.out.println("The MD5 host key (" + sshPublicKey.getAlgorithm() + ") fingerprint is: " + sshPublicKey.getFingerprint());
            System.out.println("The SHA1 host key (" + sshPublicKey.getAlgorithm() + ") fingerprint is: " + SshKeyFingerprint.getFingerprint(sshPublicKey.getEncoded(), "SHA-1"));
            try {
                System.out.println("The SHA256 host key (" + sshPublicKey.getAlgorithm() + ") fingerprint is: " + SshKeyFingerprint.getFingerprint(sshPublicKey.getEncoded(), "SHA-256"));
            }
            catch (final Exception ex) {}
            this.d(s, sshPublicKey);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    protected void onInvalidHostEntry(final String s) throws SshException {
        System.out.println("Invalid host entry in " + this.getKnownHostsFile().getAbsolutePath());
        System.out.println(s);
    }
    
    private void d(final String s, final SshPublicKey sshPublicKey) throws SshException {
        String line = "";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (!line.equalsIgnoreCase("YES") && !line.equalsIgnoreCase("NO") && (!line.equalsIgnoreCase("ALWAYS") || !this.isHostFileWriteable())) {
            final String s2 = this.isHostFileWriteable() ? "Yes|No|Always" : "Yes|No";
            if (!this.isHostFileWriteable()) {
                System.out.println("Always option disabled, host file is not writeable");
            }
            System.out.print("Do you want to allow this host key? [" + s2 + "]: ");
            try {
                line = bufferedReader.readLine();
            }
            catch (final IOException ex) {
                throw new SshException("Failed to read response", 5);
            }
        }
        if (line.equalsIgnoreCase("YES")) {
            this.allowHost(s, sshPublicKey, false);
        }
        if (line.equalsIgnoreCase("ALWAYS") && this.isHostFileWriteable()) {
            this.allowHost(s, sshPublicKey, true);
        }
    }
}
