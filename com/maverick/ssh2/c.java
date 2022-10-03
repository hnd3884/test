package com.maverick.ssh2;

import com.maverick.ssh.message.SshChannelMessage;
import java.io.IOException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.SshTunnel;

class c extends Ssh2Channel implements SshTunnel
{
    SshTransport le;
    String ve;
    int ke;
    String ne;
    int qe;
    String pe;
    int se;
    byte[] je;
    boolean te;
    int ue;
    int me;
    int oe;
    int re;
    
    public c(final String s, final int n, final int n2, final String ve, final int ke, final String ne, final int qe, final String pe, final int se, final SshTransport le) {
        super(s, n, n2);
        this.je = new byte[1024];
        this.te = false;
        this.ue = 0;
        this.me = 12;
        this.le = le;
        this.ve = ve;
        this.ke = ke;
        this.ne = ne;
        this.qe = qe;
        this.pe = pe;
        this.se = se;
    }
    
    public String getHost() {
        return this.ve;
    }
    
    public int getPort() {
        return this.ke;
    }
    
    public String getOriginatingHost() {
        return this.pe;
    }
    
    public int getOriginatingPort() {
        return this.se;
    }
    
    public String getListeningAddress() {
        return this.ne;
    }
    
    public int getListeningPort() {
        return this.qe;
    }
    
    public boolean isLocal() {
        return this.getName().equals("direct-tcpip");
    }
    
    public boolean isX11() {
        return this.getName().equals("x11");
    }
    
    public SshTransport getTransport() {
        return this.le;
    }
    
    public boolean isLocalEOF() {
        return super.sb;
    }
    
    public boolean isRemoteEOF() {
        return super.xb;
    }
    
    public SshTransport duplicate() throws IOException {
        throw new SshIOException(new SshException("SSH tunnels cannot be duplicated!", 4));
    }
    
    public void close() {
        super.close();
    }
    
    protected void processStandardData(int n, final SshChannelMessage sshChannelMessage) throws SshException {
        if (this.getName().equals("x11") && !this.te) {
            if (this.ue < 12) {
                n -= this.b(sshChannelMessage);
                if (this.me == 0) {
                    if (this.je[0] == 66) {
                        this.oe = ((this.je[6] & 0xFF) << 8 | (this.je[7] & 0xFF));
                        this.re = ((this.je[8] & 0xFF) << 8 | (this.je[9] & 0xFF));
                    }
                    else {
                        if (this.je[0] != 108) {
                            this.close();
                            throw new SshException("Corrupt X11 authentication packet", 6);
                        }
                        this.oe = ((this.je[7] & 0xFF) << 8 | (this.je[6] & 0xFF));
                        this.re = ((this.je[9] & 0xFF) << 8 | (this.je[8] & 0xFF));
                    }
                    this.me = (this.oe + 3 & 0xFFFFFFFC);
                    this.me += (this.re + 3 & 0xFFFFFFFC);
                    if (this.me + this.ue > this.je.length) {
                        this.close();
                        throw new SshException("Corrupt X11 authentication packet", 6);
                    }
                    if (this.me == 0) {
                        this.close();
                        throw new SshException("X11 authentication cookie not found", 6);
                    }
                }
            }
            if (n > 0) {
                n -= this.b(sshChannelMessage);
                if (this.me == 0) {
                    final byte[] x11AuthenticationCookie = super.mb.e().getX11AuthenticationCookie();
                    final String s = new String(this.je, 12, this.oe);
                    final byte[] array = new byte[x11AuthenticationCookie.length];
                    this.oe = (this.oe + 3 & 0xFFFFFFFC);
                    System.arraycopy(this.je, 12 + this.oe, array, 0, x11AuthenticationCookie.length);
                    if (!"MIT-MAGIC-COOKIE-1".equals(s) || !this.b(x11AuthenticationCookie, array, x11AuthenticationCookie.length)) {
                        this.close();
                        throw new SshException("Incorrect X11 cookie", 6);
                    }
                    final byte[] x11RealCookie = super.mb.e().getX11RealCookie();
                    if (x11RealCookie.length != this.re) {
                        throw new SshException("Invalid X11 cookie", 6);
                    }
                    System.arraycopy(x11RealCookie, 0, this.je, 12 + this.oe, x11RealCookie.length);
                    this.te = true;
                    super.processStandardData(n, sshChannelMessage);
                    this.je = null;
                }
            }
            if (!this.te || n == 0) {
                return;
            }
        }
        super.processStandardData(n, sshChannelMessage);
    }
    
    private boolean b(final byte[] array, final byte[] array2, final int n) {
        int n2;
        for (n2 = 0; n2 < n && array[n2] == array2[n2]; ++n2) {}
        return n2 == n;
    }
    
    private int b(final SshChannelMessage sshChannelMessage) {
        int n = sshChannelMessage.available();
        if (n > this.me) {
            sshChannelMessage.read(this.je, this.ue, this.me);
            this.ue += this.me;
            n = this.me;
            this.me = 0;
        }
        else {
            sshChannelMessage.read(this.je, this.ue, n);
            this.ue += n;
            this.me -= n;
        }
        return n;
    }
}
