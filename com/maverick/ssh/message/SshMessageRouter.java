package com.maverick.ssh.message;

import com.maverick.events.EventLog;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.SshException;
import java.util.Vector;

public abstract class SshMessageRouter
{
    private SshAbstractChannel[] nc;
    SshMessageReader oc;
    SshMessageStore hc;
    ThreadSynchronizer qc;
    private int pc;
    boolean kc;
    _b ic;
    boolean rc;
    Vector mc;
    Vector jc;
    boolean lc;
    
    public SshMessageRouter(final SshMessageReader oc, final int n, final boolean kc) {
        this.pc = 0;
        this.rc = false;
        this.mc = new Vector();
        this.jc = new Vector();
        this.lc = Boolean.valueOf(System.getProperty("maverick.verbose", "false"));
        this.oc = oc;
        this.kc = kc;
        this.nc = new SshAbstractChannel[n];
        this.hc = new SshMessageStore(this, null, new MessageObserver() {
            public boolean wantsNotification(final Message message) {
                return false;
            }
        });
        this.qc = new ThreadSynchronizer(kc);
        if (kc) {
            this.ic = new _b();
            this.qc.c = this.ic;
            this.ic.setDaemon(true);
        }
    }
    
    public void start() {
        if (this.ic != null && !this.ic.b()) {
            String substring = "";
            final String name = Thread.currentThread().getName();
            if (name.indexOf(45) > -1) {
                substring = name.substring(0, 1 + name.indexOf(45));
            }
            this.ic.setName(substring + "MessagePump_" + this.ic.getName());
            this.ic.start();
        }
    }
    
    public void addShutdownHook(final Runnable runnable) {
        if (runnable != null) {
            this.jc.addElement(runnable);
        }
    }
    
    public boolean isBuffered() {
        return this.kc;
    }
    
    public void stop() {
        this.signalClosingState();
        if (this.ic != null) {
            this.ic.c();
        }
        for (int i = 0; i < this.jc.size(); ++i) {
            try {
                ((Runnable)this.jc.elementAt(i)).run();
            }
            catch (final Throwable t) {}
        }
    }
    
    public void signalClosingState() {
        if (this.kc && this.ic != null) {
            synchronized (this.ic) {
                this.rc = true;
            }
        }
    }
    
    protected SshMessageStore getGlobalMessages() {
        return this.hc;
    }
    
    public int getMaxChannels() {
        return this.nc.length;
    }
    
    protected int allocateChannel(final SshAbstractChannel sshAbstractChannel) {
        synchronized (this.nc) {
            for (int i = 0; i < this.nc.length; ++i) {
                if (this.nc[i] == null) {
                    this.nc[i] = sshAbstractChannel;
                    this.mc.addElement(sshAbstractChannel);
                    ++this.pc;
                    return i;
                }
            }
            return -1;
        }
    }
    
    protected void freeChannel(final SshAbstractChannel sshAbstractChannel) {
        synchronized (this.nc) {
            if (this.nc[sshAbstractChannel.getChannelId()] != null && sshAbstractChannel.equals(this.nc[sshAbstractChannel.getChannelId()])) {
                this.nc[sshAbstractChannel.getChannelId()] = null;
                this.mc.removeElement(sshAbstractChannel);
                --this.pc;
            }
        }
    }
    
    protected SshAbstractChannel[] getActiveChannels() {
        return this.mc.toArray(new SshAbstractChannel[0]);
    }
    
    protected int maximumChannels() {
        return this.nc.length;
    }
    
    public int getChannelCount() {
        return this.pc;
    }
    
    protected SshMessage nextMessage(final SshAbstractChannel sshAbstractChannel, final MessageObserver messageObserver, final long n) throws SshException, InterruptedException {
        final long currentTimeMillis = System.currentTimeMillis();
        final SshMessageStore sshMessageStore = (sshAbstractChannel == null) ? this.hc : sshAbstractChannel.getMessageStore();
        final MessageHolder messageHolder = new MessageHolder();
        while (messageHolder.msg == null && (n == 0L || System.currentTimeMillis() - currentTimeMillis < n)) {
            if (this.kc && this.ic != null) {
                synchronized (this.ic) {
                    if (!this.rc && this.ic.c != null) {
                        final Throwable c = this.ic.c;
                        this.ic.c = null;
                        if (c instanceof SshException) {
                            throw (SshException)c;
                        }
                        if (c instanceof SshIOException) {
                            throw ((SshIOException)c).getRealException();
                        }
                        throw new SshException(c);
                    }
                }
            }
            if (this.qc.requestBlock(sshMessageStore, messageObserver, messageHolder)) {
                try {
                    this.d();
                }
                finally {
                    this.qc.releaseBlock();
                }
            }
        }
        if (messageHolder.msg == null) {
            throw new SshException("The message was not received before the specified timeout period timeout=" + n, 21);
        }
        return (SshMessage)messageHolder.msg;
    }
    
    public boolean isBlockingThread(final Thread thread) {
        return this.qc.isBlockOwner(thread);
    }
    
    private void d() throws SshException {
        final SshMessage message = this.createMessage(this.oc.nextMessage());
        SshAbstractChannel sshAbstractChannel = null;
        if (message instanceof SshChannelMessage) {
            sshAbstractChannel = this.nc[((SshChannelMessage)message).b()];
        }
        if (!((sshAbstractChannel == null) ? this.processGlobalMessage(message) : sshAbstractChannel.processChannelMessage((SshChannelMessage)message))) {
            ((sshAbstractChannel == null) ? this.hc : sshAbstractChannel.getMessageStore()).c(message);
        }
    }
    
    protected abstract void onThreadExit();
    
    protected abstract SshMessage createMessage(final byte[] p0) throws SshException;
    
    protected abstract boolean processGlobalMessage(final SshMessage p0) throws SshException;
    
    class _b extends Thread
    {
        Throwable c;
        boolean b;
        
        _b() {
            this.b = false;
        }
        
        public void run() {
            try {
                this.b = true;
                while (this.b) {
                    try {
                        SshMessageRouter.this.d();
                        SshMessageRouter.this.qc.releaseWaiting();
                    }
                    catch (final Throwable c) {
                        synchronized (this) {
                            if (!SshMessageRouter.this.rc) {
                                EventLog.LogEvent(this, "Message pump caught exception: " + c.getMessage());
                                this.c = c;
                            }
                            this.c();
                        }
                    }
                }
                SshMessageRouter.this.qc.releaseBlock();
            }
            finally {
                SshMessageRouter.this.onThreadExit();
            }
        }
        
        public void c() {
            this.b = false;
            if (!Thread.currentThread().equals(this)) {
                this.interrupt();
            }
        }
        
        public boolean b() {
            return this.b;
        }
    }
}
