package com.maverick.ssh;

import java.util.StringTokenizer;
import java.io.IOException;
import com.maverick.ssh2.Ssh2Session;
import com.maverick.events.EventLog;
import java.io.OutputStream;

public class Shell implements Client
{
    SshSession m;
    SshClient r;
    OutputStream y;
    ShellEnvironment ab;
    String s;
    ShellProcess t;
    String q;
    String eb;
    String w;
    String cb;
    boolean bb;
    boolean u;
    final int db = 1;
    final int v = 2;
    final int z = 3;
    int p;
    int x;
    int n;
    ShellProcess o;
    
    public Shell(final SshClient r) {
        this.s = "[PROMPT]#";
        this.q = null;
        this.eb = null;
        this.w = "exit";
        this.cb = null;
        this.bb = true;
        this.u = true;
        this.p = 1;
        this.x = 5000;
        this.n = 10000;
        this.r = r;
    }
    
    public ShellProcess getSessionInitProcess() {
        return this.o;
    }
    
    public void setEOL(final String q) {
        this.q = q;
    }
    
    public void setPrompt(final String eb) {
        this.eb = eb;
    }
    
    public void setExitCommand(final String w) {
        this.w = w;
    }
    
    public void setShellInitTimePeriod(final int x) {
        if (x <= 0) {
            throw new IllegalArgumentException("Period must be > 0");
        }
        this.x = x;
    }
    
    public synchronized void setPromptTimeoutPeriod(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Period must be >= 0");
        }
        this.n = n;
    }
    
    public void setFailOnUknownOS(final boolean bb) {
        this.bb = bb;
    }
    
    public void setPromptCommand(final String cb) {
        this.cb = cb;
    }
    
    public void createSession() throws SshException, ChannelOpenException, ShellTimeoutException {
        this.createSession("vt100", 80, 24);
    }
    
    public void initializeSession(final String s, final int n, final int n2) throws SshException, ChannelOpenException, ShellTimeoutException {
        if (!this.r.isBuffered()) {
            throw new SshException("The Shell class requires a buffered SshClient instance", 4);
        }
        EventLog.LogEvent(this, "Creating shell with term " + s + " " + n + "x" + n2);
        try {
            if (this.ab == null) {
                this.ab = new ShellEnvironment(this.r, this);
                if (this.ab.getOSType() == 99 && this.bb) {
                    throw new SshException("Shell could not determine the remote operating system", 15);
                }
            }
            this.m = this.r.openSessionChannel();
            EventLog.LogEvent(this, "session channel opened");
            if (this.m instanceof Ssh2Session) {
                ((Ssh2Session)this.m).setSendKeepAliveOnIdle(true);
            }
            this.m.setAutoConsumeInput(true);
            this.m.addChannelEventListener(new _b());
            if (this.isAllocatePseudoTerminal()) {
                final PseudoTerminalModes pseudoTerminalModes = new PseudoTerminalModes(this.r);
                pseudoTerminalModes.setTerminalMode(53, false);
                if (!this.m.requestPseudoTerminal(s, n, n2, 0, 0, pseudoTerminalModes)) {
                    throw new SshException("Failed to allocate pty for scripted session", 15);
                }
            }
            if (!this.m.startShell()) {
                throw new SshException("Failed to open a shell for scripted session", 15);
            }
            this.y = this.m.getOutputStream();
            final ShellProcess shellProcess = new ShellProcess(this, "");
            this.t = shellProcess;
            this.o = shellProcess;
        }
        catch (final IOException ex) {
            throw new SshException(ex);
        }
    }
    
    public void createSession(final String s, final int n, final int n2) throws SshException, ChannelOpenException, ShellTimeoutException {
        this.initializeSession(s, n, n2);
        this.initializePrompt();
    }
    
    public void initializePrompt() throws SshException, ChannelOpenException, ShellTimeoutException {
        try {
            final ShellProcess shellProcess = null;
            this.t = shellProcess;
            this.o = shellProcess;
            if (this.eb != null) {
                this.s = this.eb;
            }
            EventLog.LogEvent(this, "os type is " + this.ab.getOSType());
            String s = null;
            switch (this.ab.getOSType()) {
                case 1: {
                    s = "PROMPT=" + this.s;
                    break;
                }
                default: {
                    s = "PS1=" + this.s;
                    break;
                }
            }
            if (this.cb != null) {
                s = this.cb;
            }
            if (s != null) {
                try {
                    Thread.sleep(this.x);
                }
                catch (final InterruptedException ex) {}
                this.type(s);
                this.carriageReturn();
            }
            EventLog.LogEvent(this, "wait for prompt");
            this.b();
        }
        catch (final IOException ex2) {
            throw new SshException(ex2);
        }
    }
    
    public boolean isClosed() {
        return this.m.isClosed();
    }
    
    private synchronized void b() throws ShellTimeoutException {
        if (this.m.isClosed() || !this.r.isConnected()) {
            throw new ShellTimeoutException("The session or client has disconnected!");
        }
        final long currentTimeMillis = System.currentTimeMillis();
        while (this.p != 2 && System.currentTimeMillis() - currentTimeMillis < this.n) {
            try {
                this.wait(100L);
            }
            catch (final InterruptedException ex) {}
        }
        if (this.p != 2) {
            throw new ShellTimeoutException("The shell did not return to the prompt in the given timeout period " + this.n + "ms");
        }
    }
    
    public synchronized ShellProcess execute(final String s) throws IOException, ShellTimeoutException {
        this.b();
        this.t = new ShellProcess(this, s);
        this.p = 3;
        this.type(s);
        this.carriageReturn();
        return this.t;
    }
    
    public void exit() throws ShellTimeoutException, IOException {
        try {
            while (this.execute(this.w).readLine() != null) {}
        }
        finally {
            try {
                if (this.m != null) {
                    this.m.close();
                }
            }
            catch (final Throwable t) {}
        }
    }
    
    public void type(final String s) throws IOException {
        this.y.write(s.getBytes());
    }
    
    public void write(final byte[] array) throws IOException {
        this.y.write(array);
    }
    
    public void type(final int n) throws IOException {
        this.y.write(n);
    }
    
    public void carriageReturn() throws IOException {
        if (this.q == null && this.ab != null) {
            this.y.write(this.ab.getEOL().getBytes());
        }
        else {
            this.y.write(this.q.getBytes());
        }
    }
    
    void b(final String s) throws IOException {
        if (this.q == null && this.ab != null) {
            this.y.write((s + this.ab.getEOL()).getBytes());
        }
        else {
            this.y.write((s + this.q).getBytes());
        }
    }
    
    public ShellEnvironment getEnvironment() {
        return this.ab;
    }
    
    public void setEnvironment(final ShellEnvironment ab) {
        this.ab = ab;
    }
    
    public static void setDefaultEOL(final String g) {
        ShellEnvironment.g = g;
    }
    
    public boolean isAllocatePseudoTerminal() {
        return this.u;
    }
    
    public void setAllocatePseudoTerminal(final boolean u) {
        this.u = u;
    }
    
    class _b extends ChannelAdapter
    {
        boolean b;
        String c;
        String d;
        String e;
        
        _b() {
            this.b = false;
            this.c = "\n";
            this.d = "\r";
        }
        
        public void dataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
            synchronized (Shell.this) {
                final StringTokenizer stringTokenizer = new StringTokenizer(new String(array, n, n2), "\r\n", true);
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    EventLog.LogEvent(this, "Processing: " + nextToken);
                    if (nextToken.startsWith(Shell.this.s)) {
                        EventLog.LogEvent(this, "Found prompt " + Shell.this.s);
                        Shell.this.p = 2;
                        if (Shell.this.t != null) {
                            Shell.this.t.close();
                            Shell.this.t = null;
                        }
                        this.e = null;
                        Shell.this.notifyAll();
                    }
                    else if ((nextToken.equals(this.c) || nextToken.equals(this.d)) && this.b) {
                        if (!nextToken.equals(this.c)) {
                            continue;
                        }
                        this.b = false;
                    }
                    else {
                        if (Shell.this.p == 3) {
                            if (this.e == null) {
                                this.e = Shell.this.t.getCommandLine();
                            }
                            if (this.e.startsWith(nextToken.trim())) {
                                this.e = this.e.substring(nextToken.trim().length()).trim();
                                this.b = true;
                                continue;
                            }
                            Shell.this.p = 1;
                        }
                        if (Shell.this.t != null && !nextToken.equals(this.c) && !nextToken.equals(this.d)) {
                            Shell.this.t.b(nextToken);
                        }
                        else {
                            if (Shell.this.t == null || !nextToken.equals(this.c)) {
                                continue;
                            }
                            Shell.this.t.b();
                        }
                    }
                }
            }
        }
    }
}
