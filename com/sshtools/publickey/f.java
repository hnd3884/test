package com.sshtools.publickey;

import java.math.BigInteger;
import com.maverick.ssh.components.ComponentManager;
import java.util.StringTokenizer;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.components.SshPublicKey;
import java.io.IOException;

class f implements SshPublicKeyFile
{
    String b;
    
    public f(final byte[] array) throws IOException {
        this.b = new String(array);
        this.toPublicKey();
    }
    
    public f(final SshPublicKey sshPublicKey) throws IOException {
        if (sshPublicKey instanceof SshRsaPublicKey) {
            this.b = String.valueOf(((SshRsaPublicKey)sshPublicKey).getModulus().bitLength()) + " " + ((SshRsaPublicKey)sshPublicKey).getPublicExponent() + " " + ((SshRsaPublicKey)sshPublicKey).getModulus();
            this.toPublicKey();
            return;
        }
        throw new IOException("SSH1 public keys must be rsa");
    }
    
    public byte[] getFormattedKey() {
        return this.b.getBytes();
    }
    
    public SshPublicKey toPublicKey() throws IOException {
        final StringTokenizer stringTokenizer = new StringTokenizer(this.b.trim(), " ");
        try {
            Integer.parseInt((String)stringTokenizer.nextElement());
            return ComponentManager.getInstance().createRsaPublicKey(new BigInteger((String)stringTokenizer.nextElement()), new BigInteger((String)stringTokenizer.nextElement()), 1);
        }
        catch (final Throwable t) {
            throw new IOException("Invalid SSH1 public key format");
        }
    }
    
    public String getComment() {
        return "";
    }
}
