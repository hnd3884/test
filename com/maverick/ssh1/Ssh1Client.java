package com.maverick.ssh1;

import com.maverick.ssh.ForwardingRequestListener;
import com.maverick.ssh.SshTunnel;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshSession;
import java.math.BigInteger;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import com.maverick.ssh.message.SshMessage;
import java.io.IOException;
import com.maverick.ssh.PublicKeyAuthentication;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.PasswordAuthentication;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshContext;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.SshConnector;
import com.maverick.ssh.SshAuthentication;
import com.maverick.ssh.SshClient;

public class Ssh1Client implements SshClient
{
    String bc;
    boolean ac;
    boolean wb;
    b yb;
    f zb;
    String ub;
    SshAuthentication xb;
    SshConnector vb;
    
    public Ssh1Client() {
        this.ac = false;
    }
    
    public void connect(final SshTransport sshTransport, final SshContext sshContext, final SshConnector vb, final String bc, final String s, final String ub, final boolean wb) throws SshException {
        if (vb == null || !vb.isLicensed()) {
            throw new SshException("You cannot create Ssh1Client instances directly", 4);
        }
        this.zb = new f(sshTransport, sshContext);
        this.bc = bc;
        this.wb = wb;
        this.vb = vb;
        this.ub = ub;
        if (bc == null) {
            throw new SshException("You must supply a valid username!", 4);
        }
        this.zb.b();
        this.zb.g();
        this.ac = this.zb.b(bc);
    }
    
    public String getRemoteIdentification() {
        return this.ub;
    }
    
    public boolean isAuthenticated() {
        return this.ac;
    }
    
    public int authenticate(final SshAuthentication sshAuthentication) throws SshException {
        try {
            if (this.ac) {
                throw new SshException("The connection has already been authenticated!", 4);
            }
            if (sshAuthentication.getUsername() == null) {
                sshAuthentication.setUsername(this.bc);
            }
            if (sshAuthentication instanceof PasswordAuthentication) {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(9);
                byteArrayWriter.writeString(((PasswordAuthentication)sshAuthentication).getPassword());
                this.zb.d(byteArrayWriter.toByteArray());
                this.ac = this.zb.f();
                if (this.ac) {
                    this.xb = sshAuthentication;
                    return 1;
                }
                return 2;
            }
            else if (sshAuthentication instanceof Ssh1RhostsRsaAuthentication) {
                final Ssh1RhostsRsaAuthentication ssh1RhostsRsaAuthentication = (Ssh1RhostsRsaAuthentication)sshAuthentication;
                if (!(ssh1RhostsRsaAuthentication.getPublicKey() instanceof SshRsaPublicKey) || !(ssh1RhostsRsaAuthentication.getPrivateKey() instanceof SshRsaPrivateCrtKey)) {
                    throw new SshException("Only SSH1 RSA keys are suitable for SSH1 hostbased authentication", 4);
                }
                final SshRsaPublicKey sshRsaPublicKey = (SshRsaPublicKey)ssh1RhostsRsaAuthentication.getPublicKey();
                final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
                byteArrayWriter2.write(35);
                byteArrayWriter2.writeString(ssh1RhostsRsaAuthentication.getClientUsername());
                byteArrayWriter2.writeInt(sshRsaPublicKey.getBitLength());
                byteArrayWriter2.writeMPINT(sshRsaPublicKey.getPublicExponent());
                byteArrayWriter2.writeMPINT(sshRsaPublicKey.getModulus());
                this.zb.d(byteArrayWriter2.toByteArray());
                this.ac = this.b(true, (SshRsaPrivateCrtKey)ssh1RhostsRsaAuthentication.getPrivateKey());
                if (this.ac) {
                    this.xb = sshAuthentication;
                    return 1;
                }
                return 2;
            }
            else if (sshAuthentication instanceof PublicKeyAuthentication) {
                final PublicKeyAuthentication publicKeyAuthentication = (PublicKeyAuthentication)sshAuthentication;
                if (!(publicKeyAuthentication.getPublicKey() instanceof SshRsaPublicKey)) {
                    throw new SshException("Only SSH1 RSA private keys are acceptable for SSH1 RSA Authentication", 4);
                }
                final SshRsaPublicKey sshRsaPublicKey2 = (SshRsaPublicKey)publicKeyAuthentication.getPublicKey();
                final ByteArrayWriter byteArrayWriter3 = new ByteArrayWriter();
                byteArrayWriter3.write(6);
                byteArrayWriter3.writeMPINT(sshRsaPublicKey2.getModulus());
                this.zb.d(byteArrayWriter3.toByteArray());
                this.ac = this.b(publicKeyAuthentication.isAuthenticating(), (SshRsaPrivateCrtKey)publicKeyAuthentication.getPrivateKey());
                if (!publicKeyAuthentication.isAuthenticating()) {
                    return 5;
                }
                if (this.ac) {
                    this.xb = sshAuthentication;
                    return 1;
                }
                return 2;
            }
            else {
                if (!(sshAuthentication instanceof Ssh1ChallengeResponseAuthentication)) {
                    throw new SshException("Unsupported SSH1 authentication type!", 4);
                }
                if (((Ssh1ChallengeResponseAuthentication)sshAuthentication).getPrompt() == null) {
                    throw new SshException("SSH1 challenge-response requires prompt!", 4);
                }
                final ByteArrayWriter byteArrayWriter4 = new ByteArrayWriter();
                byteArrayWriter4.write(39);
                this.zb.d(byteArrayWriter4.toByteArray());
                this.ac = this.b((Ssh1ChallengeResponseAuthentication)sshAuthentication);
                if (this.ac) {
                    this.xb = sshAuthentication;
                    return 1;
                }
                return 2;
            }
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Client.authenticate caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    private boolean b(final Ssh1ChallengeResponseAuthentication ssh1ChallengeResponseAuthentication) throws SshException {
        try {
            final SshMessage sshMessage = new SshMessage(this.zb.nextMessage());
            if (sshMessage.getMessageId() != 40) {
                return false;
            }
            final String response = ssh1ChallengeResponseAuthentication.getPrompt().getResponse(sshMessage.readString());
            if (response != null) {
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(41);
                byteArrayWriter.writeString(response);
                this.zb.d(byteArrayWriter.toByteArray());
                return this.zb.f();
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Client.performChallengeResponse() caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    private boolean b(final boolean b, final SshRsaPrivateCrtKey sshRsaPrivateCrtKey) throws SshException {
        try {
            final SshMessage sshMessage = new SshMessage(this.zb.nextMessage());
            if (sshMessage.getMessageId() == 7) {
                byte[] doFinal = new byte[16];
                if (b && sshRsaPrivateCrtKey != null) {
                    final byte[] byteArray = this.b(sshMessage.readMPINT().modPow(sshRsaPrivateCrtKey.getPrivateExponent(), sshRsaPrivateCrtKey.getModulus()), 2).toByteArray();
                    final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
                    if (byteArray[0] == 0) {
                        digest.putBytes(byteArray, 1, 32);
                    }
                    else {
                        digest.putBytes(byteArray, 0, 32);
                    }
                    digest.putBytes(this.zb.p);
                    doFinal = digest.doFinal();
                }
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
                byteArrayWriter.write(8);
                byteArrayWriter.write(doFinal);
                this.zb.d(byteArrayWriter.toByteArray());
                return this.zb.f();
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException("Ssh1Client.performRSAChallenge() caught an IOException: " + ex.getMessage(), 5);
        }
    }
    
    private BigInteger b(final BigInteger bigInteger, final int n) throws IllegalStateException {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] != n) {
            throw new IllegalStateException("PKCS1 padding type " + n + " is not valid");
        }
        int n2;
        for (n2 = 1; n2 < byteArray.length && byteArray[n2] != 0; ++n2) {
            if (n == 1 && byteArray[n2] != -1) {
                throw new IllegalStateException("Corrupt data found in expected PKSC1 padding");
            }
        }
        if (n2 == byteArray.length) {
            throw new IllegalStateException("Corrupt data found in expected PKSC1 padding");
        }
        final byte[] array = new byte[byteArray.length - n2];
        System.arraycopy(byteArray, n2, array, 0, array.length);
        return new BigInteger(1, array);
    }
    
    public SshSession openSessionChannel() throws SshException, ChannelOpenException {
        return this.openSessionChannel(null);
    }
    
    public SshSession openSessionChannel(final ChannelEventListener channelEventListener) throws SshException, ChannelOpenException {
        return this.openSessionChannel(channelEventListener, 0L);
    }
    
    public SshSession openSessionChannel(final long n) throws SshException, ChannelOpenException {
        return this.openSessionChannel(null, n);
    }
    
    public SshSession openSessionChannel(final ChannelEventListener channelEventListener, final long n) throws SshException, ChannelOpenException {
        if (!this.ac) {
            throw new SshException("The connection must be authenticated first!", 4);
        }
        if (this.yb == null) {
            this.yb = new b(this.zb, this, channelEventListener, this.wb);
            if (this.zb.b.getX11Display() != null && !this.yb.yc) {
                this.yb.b(this.zb.b.getX11Display(), this.zb.b.getX11RequestListener());
            }
            return this.yb;
        }
        return this.duplicate().openSessionChannel(channelEventListener);
    }
    
    public SshTunnel openForwardingChannel(final String s, final int n, final String s2, final int n2, final String s3, final int n3, final SshTransport sshTransport, final ChannelEventListener channelEventListener) throws SshException, ChannelOpenException {
        if (this.yb == null || !this.yb.kd) {
            throw new SshException("SSH1 forwarding channels can only be opened after the user's shell has been started!", 4);
        }
        return this.yb.b(s, n, s2, n2, s3, n3, sshTransport, channelEventListener);
    }
    
    public SshClient openRemoteClient(final String s, final int n, final String s2, final SshConnector sshConnector) throws SshException, ChannelOpenException {
        return sshConnector.connect(this.openForwardingChannel(s, n, "127.0.0.1", 22, "127.0.0.1", 22, null, null), s2);
    }
    
    public SshClient openRemoteClient(final String s, final int n, final String s2) throws SshException, ChannelOpenException {
        return this.openRemoteClient(s, n, s2, this.vb);
    }
    
    public boolean requestXForwarding(final String x11Display, final ForwardingRequestListener x11RequestListener) throws SshException {
        if (this.yb != null && this.yb.kd) {
            throw new SshException("SSH1 X forwarding requests must be made after opening the session but before starting the shell!", 4);
        }
        if (this.yb == null) {
            throw new SshException("SSH1 X forwarding requests must be made after opening the session but before starting the shell!", 4);
        }
        this.zb.b.setX11Display(x11Display);
        this.zb.b.setX11RequestListener(x11RequestListener);
        this.yb.b(x11Display, x11RequestListener);
        return this.yb.yc;
    }
    
    public boolean requestRemoteForwarding(final String s, final int n, final String s2, final int n2, final ForwardingRequestListener forwardingRequestListener) throws SshException {
        if (this.yb != null && this.yb.kd) {
            throw new SshException("SSH1 forwarding requests must be made after opening the session but before starting the shell!", 4);
        }
        if (this.yb == null) {
            throw new SshException("SSH1 forwarding requests must be made after opening the session but before starting the shell!", 4);
        }
        return this.yb.b(n, s2, n2, forwardingRequestListener);
    }
    
    public boolean cancelRemoteForwarding(final String s, final int n) throws SshException {
        return false;
    }
    
    public void disconnect() {
        try {
            if (this.yb != null) {
                this.yb.signalClosingState();
            }
            this.zb.c("The user disconnected the application");
        }
        catch (final Throwable t) {}
    }
    
    public void exit() {
        try {
            if (this.yb != null) {
                this.yb.signalClosingState();
            }
            this.zb.c("The user disconnected the application");
        }
        catch (final Throwable t) {}
    }
    
    public boolean isConnected() {
        return this.zb.e() == 2;
    }
    
    public String getUsername() {
        return this.bc;
    }
    
    public SshClient duplicate() throws SshException {
        if (this.bc == null || this.xb == null) {
            throw new SshException("Cannot duplicate! The existing connection does not have a set of credentials", 4);
        }
        try {
            final SshClient connect = this.vb.connect(this.zb.q.duplicate(), this.bc, this.wb, this.zb.b);
            if (!connect.isAuthenticated() && connect.authenticate(this.xb) != 1) {
                throw new SshException("Duplication attempt failed to authenicate user!", 5);
            }
            return connect;
        }
        catch (final IOException ex) {
            throw new SshException("Failed to duplicate SshClient", 10);
        }
    }
    
    public SshContext getContext() {
        return this.zb.b;
    }
    
    public int getChannelCount() {
        if (this.yb == null) {
            return 0;
        }
        return this.yb.getChannelCount();
    }
    
    public int getVersion() {
        return 1;
    }
    
    public boolean isBuffered() {
        return this.wb;
    }
    
    public String toString() {
        return "SSH1 " + this.zb.q.getHost() + ":" + this.zb.q.getPort() + "[" + "cipher=" + ((this.zb.s == null) ? "none" : this.zb.s.getAlgorithm()) + "]";
    }
}
