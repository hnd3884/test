package com.maverick.ssh2;

import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.components.SshKeyExchangeClient;
import com.maverick.ssh.message.SshAbstractChannel;
import com.maverick.ssh.ForwardingRequestListener;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.SshTunnel;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshSession;
import com.maverick.ssh.PublicKeyAuthentication;
import com.maverick.ssh.PasswordAuthentication;
import java.util.Vector;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshContext;
import com.maverick.ssh.SshConnector;
import com.maverick.ssh.SshAuthentication;
import java.util.Hashtable;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.SshClient;

public class Ssh2Client implements SshClient
{
    TransportProtocol kb;
    SshTransport pb;
    AuthenticationProtocol ob;
    b gb;
    String lb;
    String qb;
    String[] nb;
    String mb;
    Hashtable tb;
    Hashtable sb;
    _b rb;
    SshAuthentication hb;
    SshConnector jb;
    boolean fb;
    boolean ib;
    
    public Ssh2Client() {
        this.tb = new Hashtable();
        this.sb = new Hashtable();
        this.rb = new _b();
        this.fb = false;
    }
    
    public void connect(final SshTransport pb, final SshContext sshContext, final SshConnector jb, final String mb, final String lb, final String qb, final boolean ib) throws SshException {
        if (jb == null || !jb.isLicensed()) {
            throw new SshException("You cannot create Ssh2Client instances directly", 4);
        }
        this.pb = pb;
        this.lb = lb;
        this.qb = qb;
        this.mb = mb;
        this.ib = ib;
        this.jb = jb;
        if (mb == null) {
            try {
                pb.close();
            }
            catch (final IOException ex) {}
            throw new SshException("You must supply a valid username!", 4);
        }
        if (!(sshContext instanceof Ssh2Context)) {
            try {
                pb.close();
            }
            catch (final IOException ex2) {}
            throw new SshException("Ssh2Context required!", 4);
        }
        (this.kb = new TransportProtocol()).startTransportProtocol(pb, (Ssh2Context)sshContext, lb, qb, this);
        (this.ob = new AuthenticationProtocol(this.kb)).setBannerDisplay(((Ssh2Context)sshContext).getBannerDisplay());
        (this.gb = new b(this.kb, sshContext, ib)).b(this.rb);
        this.getAuthenticationMethods(mb);
    }
    
    public String[] getAuthenticationMethods(final String s) throws SshException {
        this.b(false);
        if (this.nb == null) {
            String s2 = this.ob.getAuthenticationMethods(s, "ssh-connection");
            final Vector vector = new Vector();
            while (s2 != null) {
                final int index = s2.indexOf(44);
                if (index > -1) {
                    vector.addElement(s2.substring(0, index));
                    s2 = s2.substring(index + 1);
                }
                else {
                    vector.addElement(s2);
                    s2 = null;
                }
            }
            vector.copyInto(this.nb = new String[vector.size()]);
            if (this.isAuthenticated()) {
                this.gb.start();
            }
        }
        return this.nb;
    }
    
    private SshAuthentication b(final SshAuthentication sshAuthentication) {
        boolean b = false;
        for (int i = 0; i < this.nb.length; ++i) {
            if (this.nb[i].equals("password")) {
                return sshAuthentication;
            }
            if (this.nb[i].equals("keyboard-interactive")) {
                b = true;
            }
        }
        if (b) {
            final KBIAuthentication kbiAuthentication = new KBIAuthentication();
            kbiAuthentication.setUsername(((PasswordAuthentication)sshAuthentication).getUsername());
            kbiAuthentication.setKBIRequestHandler(new _c((PasswordAuthentication)sshAuthentication));
            return kbiAuthentication;
        }
        return sshAuthentication;
    }
    
    public int authenticate(SshAuthentication b) throws SshException {
        this.b(false);
        if (this.isAuthenticated()) {
            throw new SshException("User is already authenticated! Did you check isAuthenticated?", 4);
        }
        if (b.getUsername() == null) {
            b.setUsername(this.mb);
        }
        if (b instanceof PasswordAuthentication || b instanceof Ssh2PasswordAuthentication) {
            b = this.b(b);
        }
        int n;
        if (b instanceof PasswordAuthentication && !(b instanceof Ssh2PasswordAuthentication)) {
            final Ssh2PasswordAuthentication ssh2PasswordAuthentication = new Ssh2PasswordAuthentication();
            ssh2PasswordAuthentication.setUsername(((PasswordAuthentication)b).getUsername());
            ssh2PasswordAuthentication.setPassword(((PasswordAuthentication)b).getPassword());
            n = this.ob.authenticate(ssh2PasswordAuthentication, "ssh-connection");
            if (ssh2PasswordAuthentication.requiresPasswordChange()) {
                this.disconnect();
                throw new SshException("Password change required!", 8);
            }
        }
        else if (b instanceof PublicKeyAuthentication && !(b instanceof Ssh2PublicKeyAuthentication)) {
            final Ssh2PublicKeyAuthentication ssh2PublicKeyAuthentication = new Ssh2PublicKeyAuthentication();
            ssh2PublicKeyAuthentication.setUsername(((PublicKeyAuthentication)b).getUsername());
            ssh2PublicKeyAuthentication.setPublicKey(((PublicKeyAuthentication)b).getPublicKey());
            ssh2PublicKeyAuthentication.setPrivateKey(((PublicKeyAuthentication)b).getPrivateKey());
            n = this.ob.authenticate(ssh2PublicKeyAuthentication, "ssh-connection");
        }
        else {
            if (!(b instanceof AuthenticationClient)) {
                throw new SshException("Invalid authentication client", 4);
            }
            n = this.ob.authenticate((AuthenticationClient)b, "ssh-connection");
        }
        if (n == 1) {
            this.hb = b;
            this.gb.start();
        }
        return n;
    }
    
    public boolean isAuthenticated() {
        return this.ob.isAuthenticated();
    }
    
    public void disconnect() {
        try {
            this.gb.signalClosingState();
            this.kb.disconnect(11, "The user disconnected the application");
        }
        catch (final Throwable t) {}
    }
    
    public void exit() {
        try {
            this.gb.signalClosingState();
            this.kb.disconnect(11, "The user disconnected the application");
        }
        catch (final Throwable t) {}
    }
    
    public boolean isConnected() {
        return this.kb.isConnected();
    }
    
    public void forceKeyExchange() throws SshException {
        this.kb.b(false);
    }
    
    public SshSession openSessionChannel() throws SshException, ChannelOpenException {
        return this.openSessionChannel(32768, 32768, null);
    }
    
    public SshSession openSessionChannel(final long n) throws SshException, ChannelOpenException {
        return this.openSessionChannel(32768, 32768, null, n);
    }
    
    public SshSession openSessionChannel(final ChannelEventListener channelEventListener, final long n) throws SshException, ChannelOpenException {
        return this.openSessionChannel(32768, 32768, channelEventListener, n);
    }
    
    public SshSession openSessionChannel(final ChannelEventListener channelEventListener) throws SshException, ChannelOpenException {
        return this.openSessionChannel(32768, 32768, channelEventListener);
    }
    
    public Ssh2Session openSessionChannel(final int n, final int n2, final ChannelEventListener channelEventListener) throws ChannelOpenException, SshException {
        return this.openSessionChannel(n, n2, channelEventListener, 0L);
    }
    
    public Ssh2Session openSessionChannel(final int n, final int n2, final ChannelEventListener channelEventListener, final long n3) throws ChannelOpenException, SshException {
        this.b(true);
        final Ssh2Session ssh2Session = new Ssh2Session(n, n2, this);
        if (channelEventListener != null) {
            ssh2Session.addChannelEventListener(channelEventListener);
        }
        this.gb.b(ssh2Session, null, n3);
        if (this.gb.e().getX11Display() != null) {
            String s = this.gb.e().getX11Display();
            final int index = s.indexOf(58);
            int int1 = 0;
            if (index != -1) {
                s = s.substring(index + 1);
            }
            final int index2 = s.indexOf(46);
            if (index2 > -1) {
                int1 = Integer.parseInt(s.substring(index2 + 1));
            }
            final byte[] x11AuthenticationCookie = this.gb.e().getX11AuthenticationCookie();
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 16; ++i) {
                String s2 = Integer.toHexString(x11AuthenticationCookie[i] & 0xFF);
                if (s2.length() == 1) {
                    s2 = "0" + s2;
                }
                sb.append(s2);
            }
            if (ssh2Session.b(false, "MIT-MAGIC-COOKIE-1", sb.toString(), int1)) {
                this.fb = true;
            }
        }
        return ssh2Session;
    }
    
    public SshClient openRemoteClient(final String s, final int n, final String s2, final SshConnector sshConnector) throws SshException, ChannelOpenException {
        return sshConnector.connect(this.openForwardingChannel(s, n, "127.0.0.1", 22, "127.0.0.1", 22, null, null), s2, this.ib);
    }
    
    public SshClient openRemoteClient(final String s, final int n, final String s2) throws SshException, ChannelOpenException {
        return this.openRemoteClient(s, n, s2, this.jb);
    }
    
    public SshTunnel openForwardingChannel(final String s, final int n, final String s2, final int n2, final String s3, final int n3, final SshTransport sshTransport, final ChannelEventListener channelEventListener) throws SshException, ChannelOpenException {
        try {
            final c c = new c("direct-tcpip", 32768, 2097152, s, n, s2, n2, s3, n3, sshTransport);
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n);
            byteArrayWriter.writeString(s3);
            byteArrayWriter.writeInt(n3);
            c.addChannelEventListener(channelEventListener);
            this.openChannel(c, byteArrayWriter.toByteArray());
            return c;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean requestRemoteForwarding(final String s, final int n, final String s2, final int n2, final ForwardingRequestListener forwardingRequestListener) throws SshException {
        try {
            if (forwardingRequestListener == null) {
                throw new SshException("You must specify a listener to receive connection requests", 4);
            }
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n);
            if (this.sendGlobalRequest(new GlobalRequest("tcpip-forward", byteArrayWriter.toByteArray()), true)) {
                this.tb.put(s + ":" + String.valueOf(n), forwardingRequestListener);
                this.sb.put(s + ":" + String.valueOf(n), s2 + ":" + String.valueOf(n2));
                return true;
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean cancelRemoteForwarding(final String s, final int n) throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(s);
            byteArrayWriter.writeInt(n);
            if (this.sendGlobalRequest(new GlobalRequest("cancel-tcpip-forward", byteArrayWriter.toByteArray()), true)) {
                this.tb.remove(s + ":" + String.valueOf(n));
                this.sb.remove(s + ":" + String.valueOf(n));
                return true;
            }
            return false;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public void openChannel(final Ssh2Channel ssh2Channel, final byte[] array) throws SshException, ChannelOpenException {
        this.b(true);
        this.gb.b(ssh2Channel, array);
    }
    
    public void openChannel(final SshAbstractChannel sshAbstractChannel) throws SshException, ChannelOpenException {
        this.b(true);
        if (sshAbstractChannel instanceof Ssh2Channel) {
            this.gb.b((Ssh2Channel)sshAbstractChannel, null);
            return;
        }
        throw new SshException("The channel is not an SSH2 channel!", 4);
    }
    
    public void addChannelFactory(final ChannelFactory channelFactory) throws SshException {
        this.gb.b(channelFactory);
    }
    
    public SshContext getContext() {
        return this.kb.qb;
    }
    
    public void addRequestHandler(final GlobalRequestHandler globalRequestHandler) throws SshException {
        this.gb.b(globalRequestHandler);
    }
    
    public boolean sendGlobalRequest(final GlobalRequest globalRequest, final boolean b) throws SshException {
        this.b(true);
        return this.gb.b(globalRequest, b);
    }
    
    public String getRemoteIdentification() {
        return this.qb;
    }
    
    void b(final boolean b) throws SshException {
        if (this.ob == null || this.kb == null || this.gb == null) {
            throw new SshException("Not connected!", 4);
        }
        if (!this.kb.isConnected()) {
            throw new SshException("The connection has been terminated!", 2);
        }
        if (!this.ob.isAuthenticated() && b) {
            throw new SshException("The connection is not authenticated!", 4);
        }
    }
    
    public String getUsername() {
        return this.mb;
    }
    
    public SshClient duplicate() throws SshException {
        if (this.mb == null || this.hb == null) {
            throw new SshException("Cannot duplicate! The existing connection does not have a set of credentials", 4);
        }
        try {
            final SshClient connect = this.jb.connect(this.pb.duplicate(), this.mb, this.ib, this.kb.qb);
            if (connect.authenticate(this.hb) != 1) {
                connect.disconnect();
                throw new SshException("Duplication attempt failed to authenicate user!", 5);
            }
            return connect;
        }
        catch (final IOException ex) {
            throw new SshException(ex, 10);
        }
    }
    
    public int getChannelCount() {
        return this.gb.getChannelCount();
    }
    
    public int getVersion() {
        return 2;
    }
    
    public boolean isBuffered() {
        return this.ib;
    }
    
    public String getKeyExchangeInUse() {
        return (this.kb.mc == null) ? "none" : this.kb.mc.getAlgorithm();
    }
    
    public SshKeyExchangeClient getKeyExchangeInstanceInUse() {
        return this.kb.mc;
    }
    
    public String getHostKeyInUse() {
        return (this.kb.z == null) ? "none" : this.kb.z.getAlgorithm();
    }
    
    public String getCipherInUseCS() {
        return (this.kb.cc == null) ? "none" : this.kb.cc.getAlgorithm();
    }
    
    public String getCipherInUseSC() {
        return (this.kb.xb == null) ? "none" : this.kb.xb.getAlgorithm();
    }
    
    public String getMacInUseCS() {
        return (this.kb.fb == null) ? "none" : this.kb.fb.getAlgorithm();
    }
    
    public String getMacInUseSC() {
        return (this.kb.tb == null) ? "none" : this.kb.tb.getAlgorithm();
    }
    
    public String getCompressionInUseCS() {
        return (this.kb.kc == null) ? "none" : this.kb.kc.getAlgorithm();
    }
    
    public String getCompressionInUseSC() {
        return (this.kb.nb == null) ? "none" : this.kb.nb.getAlgorithm();
    }
    
    public String toString() {
        return "SSH2 " + this.pb.getHost() + ":" + this.pb.getPort() + " [kex=" + ((this.kb.mc == null) ? "none" : this.kb.mc.getAlgorithm()) + " hostkey=" + ((this.kb.z == null) ? "none" : this.kb.z.getAlgorithm()) + " client->server=" + ((this.kb.cc == null) ? "none" : this.kb.cc.getAlgorithm()) + "," + ((this.kb.fb == null) ? "none" : this.kb.fb.getAlgorithm()) + "," + ((this.kb.kc == null) ? "none" : this.kb.kc.getAlgorithm()) + " server->client=" + ((this.kb.xb == null) ? "none" : this.kb.xb.getAlgorithm()) + "," + ((this.kb.tb == null) ? "none" : this.kb.tb.getAlgorithm()) + "," + ((this.kb.nb == null) ? "none" : this.kb.nb.getAlgorithm()) + "]";
    }
    
    class _b implements ChannelFactory
    {
        String[] b;
        
        _b() {
            this.b = new String[] { "forwarded-tcpip", "x11" };
        }
        
        public String[] supportedChannelTypes() {
            return this.b;
        }
        
        public Ssh2Channel createChannel(final String s, final byte[] array) throws SshException, ChannelOpenException {
            if (s.equals("forwarded-tcpip")) {
                try {
                    final ByteArrayReader byteArrayReader = new ByteArrayReader(array);
                    final String string = byteArrayReader.readString();
                    final int n = (int)byteArrayReader.readInt();
                    final String string2 = byteArrayReader.readString();
                    final int n2 = (int)byteArrayReader.readInt();
                    final String string3 = string + ":" + String.valueOf(n);
                    if (Ssh2Client.this.tb.containsKey(string3)) {
                        final ForwardingRequestListener forwardingRequestListener = Ssh2Client.this.tb.get(string3);
                        final String s2 = Ssh2Client.this.sb.get(string3);
                        final String substring = s2.substring(0, s2.indexOf(58));
                        final int int1 = Integer.parseInt(s2.substring(s2.indexOf(58) + 1));
                        final c c = new c("forwarded-tcpip", 32768, 2097152, substring, int1, string, n, string2, n2, forwardingRequestListener.createConnection(substring, int1));
                        forwardingRequestListener.initializeTunnel(c);
                        return c;
                    }
                    throw new ChannelOpenException("Forwarding had not previously been requested", 1);
                }
                catch (final IOException ex) {
                    throw new ChannelOpenException(ex.getMessage(), 4);
                }
                catch (final SshException ex2) {
                    throw new ChannelOpenException(ex2.getMessage(), 2);
                }
            }
            if (s.equals("x11")) {
                if (!Ssh2Client.this.fb) {
                    throw new ChannelOpenException("X Forwarding had not previously been requested", 1);
                }
                try {
                    final ByteArrayReader byteArrayReader2 = new ByteArrayReader(array);
                    final String string4 = byteArrayReader2.readString();
                    final int n3 = (int)byteArrayReader2.readInt();
                    final String x11Display = Ssh2Client.this.gb.e().getX11Display();
                    final int index = x11Display.indexOf(":");
                    int int2 = 0;
                    String substring2;
                    int n5;
                    if (index != -1) {
                        substring2 = x11Display.substring(0, index);
                        final String substring3 = x11Display.substring(index + 1);
                        final int index2 = substring3.indexOf(46);
                        int n4;
                        if (index2 > -1) {
                            n4 = Integer.parseInt(substring3.substring(0, index2));
                            int2 = Integer.parseInt(substring3.substring(index2 + 1));
                        }
                        else {
                            n4 = Integer.parseInt(substring3);
                        }
                        n5 = n4;
                    }
                    else {
                        substring2 = x11Display;
                        n5 = 6000;
                    }
                    if (n5 <= 10) {
                        n5 += 6000;
                    }
                    final ForwardingRequestListener x11RequestListener = Ssh2Client.this.gb.e().getX11RequestListener();
                    final c c2 = new c("x11", 32768, 32768, substring2, n5, substring2, int2, string4, n3, x11RequestListener.createConnection(substring2, n5));
                    x11RequestListener.initializeTunnel(c2);
                    return c2;
                }
                catch (final Throwable t) {
                    throw new ChannelOpenException(t.getMessage(), 2);
                }
            }
            throw new ChannelOpenException(s + " is not supported", 3);
        }
    }
    
    private static class _c implements KBIRequestHandler
    {
        private String b;
        
        public _c(final PasswordAuthentication passwordAuthentication) {
            this.b = passwordAuthentication.getPassword();
        }
        
        public boolean showPrompts(final String s, final String s2, final KBIPrompt[] array) {
            for (int i = 0; i < array.length; ++i) {
                array[i].setResponse(this.b);
            }
            return true;
        }
    }
}
