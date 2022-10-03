package com.sshtools.publickey;

import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshPublicKey;
import java.io.IOException;

public class SECSHPublicKeyFile extends Base64EncodedFileFormat implements SshPublicKeyFile
{
    private static String q;
    private static String s;
    String p;
    byte[] r;
    
    SECSHPublicKeyFile(final byte[] array) throws IOException {
        super(SECSHPublicKeyFile.q, SECSHPublicKeyFile.s);
        this.r = this.getKeyBlob(array);
        this.toPublicKey();
    }
    
    SECSHPublicKeyFile(final SshPublicKey sshPublicKey, final String comment) throws IOException {
        super(SECSHPublicKeyFile.q, SECSHPublicKeyFile.s);
        try {
            this.p = sshPublicKey.getAlgorithm();
            this.r = sshPublicKey.getEncoded();
            this.setComment(comment);
            this.toPublicKey();
        }
        catch (final SshException ex) {
            throw new IOException("Failed to encode public key");
        }
    }
    
    public String getComment() {
        return this.getHeaderValue("Comment");
    }
    
    public SshPublicKey toPublicKey() throws IOException {
        return SshPublicKeyFileFactory.decodeSSH2PublicKey(this.r);
    }
    
    public byte[] getFormattedKey() throws IOException {
        return this.formatKey(this.r);
    }
    
    public void setComment(final String s) {
        this.setHeaderValue("Comment", (s.trim().startsWith("\"") ? "" : "\"") + s.trim() + (s.trim().endsWith("\"") ? "" : "\""));
    }
    
    public String toString() {
        try {
            return new String(this.getFormattedKey());
        }
        catch (final IOException ex) {
            return "Invalid encoding!";
        }
    }
    
    static {
        SECSHPublicKeyFile.q = "---- BEGIN SSH2 PUBLIC KEY ----";
        SECSHPublicKeyFile.s = "---- END SSH2 PUBLIC KEY ----";
    }
}
