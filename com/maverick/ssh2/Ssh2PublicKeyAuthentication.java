package com.maverick.ssh2;

import java.io.IOException;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshException;
import com.maverick.ssh.PublicKeyAuthentication;

public class Ssh2PublicKeyAuthentication extends PublicKeyAuthentication implements AuthenticationClient
{
    SignatureGenerator u;
    
    public void authenticate(final AuthenticationProtocol authenticationProtocol, final String s) throws SshException, AuthenticationResult {
        try {
            if (this.getPublicKey() == null) {
                throw new SshException("Public key not set!", 4);
            }
            if (this.getPrivateKey() == null && this.u == null && this.isAuthenticating()) {
                throw new SshException("Private key or signature generator not set!", 4);
            }
            if (this.getUsername() == null) {
                throw new SshException("Username not set!", 4);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeBinaryString(authenticationProtocol.getSessionIdentifier());
            byteArrayWriter.write(50);
            byteArrayWriter.writeString(this.getUsername());
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeString("publickey");
            byteArrayWriter.writeBoolean(this.isAuthenticating());
            byte[] array;
            try {
                if (this.getPublicKey() instanceof SshRsaPublicKey && ((SshRsaPublicKey)this.getPublicKey()).getVersion() == 1) {
                    final SshRsaPublicKey sshRsaPublicKey = (SshRsaPublicKey)this.getPublicKey();
                    byteArrayWriter.writeString("ssh-rsa");
                    final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
                    byteArrayWriter2.writeString("ssh-rsa");
                    byteArrayWriter2.writeBigInteger(sshRsaPublicKey.getPublicExponent());
                    byteArrayWriter2.writeBigInteger(sshRsaPublicKey.getModulus());
                    byteArrayWriter.writeBinaryString(array = byteArrayWriter2.toByteArray());
                }
                else {
                    byteArrayWriter.writeString(this.getPublicKey().getAlgorithm());
                    byteArrayWriter.writeBinaryString(array = this.getPublicKey().getEncoded());
                }
            }
            catch (final Throwable t) {
                throw new SshException("Unsupported public key type " + this.getPublicKey().getAlgorithm(), 4);
            }
            final ByteArrayWriter byteArrayWriter3 = new ByteArrayWriter();
            byteArrayWriter3.writeBoolean(this.isAuthenticating());
            byteArrayWriter3.writeString(this.getPublicKey().getAlgorithm());
            byteArrayWriter3.writeBinaryString(array);
            if (this.isAuthenticating()) {
                byte[] array2;
                if (this.u != null) {
                    array2 = this.u.sign(this.getPublicKey(), byteArrayWriter.toByteArray());
                }
                else {
                    array2 = this.getPrivateKey().sign(byteArrayWriter.toByteArray());
                }
                final ByteArrayWriter byteArrayWriter4 = new ByteArrayWriter();
                byteArrayWriter4.writeString(this.getPublicKey().getAlgorithm());
                byteArrayWriter4.writeBinaryString(array2);
                byteArrayWriter3.writeBinaryString(byteArrayWriter4.toByteArray());
            }
            authenticationProtocol.sendRequest(this.getUsername(), s, "publickey", byteArrayWriter3.toByteArray());
            final byte[] message = authenticationProtocol.readMessage();
            if (message[0] == 60) {
                throw new AuthenticationResult(5);
            }
            authenticationProtocol.d.disconnect(2, "Unexpected message " + message[0] + " received");
            throw new SshException("Unexpected message " + message[0] + " received", 3);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public void setSignatureGenerator(final SignatureGenerator u) {
        this.u = u;
    }
}
