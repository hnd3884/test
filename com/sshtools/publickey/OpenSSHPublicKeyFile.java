package com.sshtools.publickey;

import com.maverick.ssh.SshException;
import com.maverick.util.Base64;
import com.maverick.ssh.components.SshPublicKey;
import java.io.IOException;

public class OpenSSHPublicKeyFile implements SshPublicKeyFile
{
    byte[] t;
    String u;
    
    OpenSSHPublicKeyFile(final byte[] t) throws IOException {
        this.t = t;
        this.toPublicKey();
    }
    
    OpenSSHPublicKeyFile(final SshPublicKey sshPublicKey, final String s) throws IOException {
        try {
            String s2 = sshPublicKey.getAlgorithm() + " " + Base64.encodeBytes(sshPublicKey.getEncoded(), true);
            if (s != null) {
                s2 = s2 + " " + s;
            }
            this.t = s2.getBytes();
        }
        catch (final SshException ex) {
            throw new IOException("Failed to encode public key");
        }
    }
    
    public String toString() {
        return new String(this.t);
    }
    
    public byte[] getFormattedKey() {
        return this.t;
    }
    
    public SshPublicKey toPublicKey() throws IOException {
        final String s = new String(this.t);
        final int index = s.indexOf(" ");
        if (index <= 0) {
            throw new IOException("Key format not supported!");
        }
        final String substring = s.substring(0, index);
        final int index2 = s.indexOf(" ", index + 1);
        if (index2 != -1) {
            final String substring2 = s.substring(index + 1, index2);
            if (s.length() > index2) {
                this.u = s.substring(index2 + 1).trim();
            }
            return SshPublicKeyFileFactory.decodeSSH2PublicKey(substring, Base64.decode(substring2));
        }
        return SshPublicKeyFileFactory.decodeSSH2PublicKey(substring, Base64.decode(s.substring(index + 1)));
    }
    
    public String getComment() {
        return this.u;
    }
}
