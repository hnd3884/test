package com.sshtools.util;

import com.maverick.ssh.SshChannel;
import com.maverick.ssh.ChannelAdapter;
import com.maverick.ssh.SshException;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshSession;

public class SessionOutputReader
{
    BufferedSession c;
    int e;
    int d;
    String b;
    
    public SessionOutputReader(final SshSession sshSession) throws SshException {
        this.e = 0;
        this.d = 0;
        this.b = "";
        this.c = new BufferedSession(sshSession);
        sshSession.addChannelEventListener(new _b());
    }
    
    public String getOutput() {
        return this.b;
    }
    
    public int getPosition() {
        return this.e;
    }
    
    public void markPosition(final int d) {
        this.d = d;
    }
    
    public void markCurrentPosition() {
        this.d = this.e;
    }
    
    public String getMarkedOutput() {
        return this.b.substring(this.d, this.e);
    }
    
    public synchronized boolean waitForString(final String s, final SessionOutputEcho sessionOutputEcho) throws InterruptedException {
        return this.waitForString(s, 0, sessionOutputEcho);
    }
    
    public synchronized boolean waitForString(final String s) throws InterruptedException {
        return this.waitForString(s, 0, null);
    }
    
    public synchronized boolean waitForString(final String s, final int n) throws InterruptedException {
        return this.waitForString(s, n, null);
    }
    
    public synchronized boolean waitForString(final String s, final int n, final SessionOutputEcho sessionOutputEcho) throws InterruptedException {
        final long currentTimeMillis = System.currentTimeMillis();
        while (this.b.indexOf(s, this.e) == -1 && (System.currentTimeMillis() - currentTimeMillis < n || n == 0)) {
            final int length = this.b.length();
            this.wait((n > 0) ? (n - (System.currentTimeMillis() - currentTimeMillis)) : 0L);
            if (this.b.length() > length && sessionOutputEcho != null) {
                sessionOutputEcho.echo(this.b.substring(length, this.b.length()));
            }
        }
        if (this.b.indexOf(s, this.e) > -1) {
            this.e = this.b.indexOf(s, this.e) + s.length();
            return true;
        }
        return false;
    }
    
    public synchronized void echoLineByLineToClose(final SessionOutputEcho sessionOutputEcho) throws InterruptedException {
        while (!this.c.isClosed()) {
            this.waitForString("\n", 1000, sessionOutputEcho);
        }
    }
    
    private synchronized void b() {
        this.notifyAll();
    }
    
    class _b extends ChannelAdapter
    {
        public void channelClosed(final SshChannel sshChannel) {
            SessionOutputReader.this.b();
        }
        
        public void dataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
            final StringBuffer sb = new StringBuffer();
            final SessionOutputReader this$0 = SessionOutputReader.this;
            this$0.b = sb.append(this$0.b).append(new String(array)).toString();
            SessionOutputReader.this.b();
        }
    }
}
