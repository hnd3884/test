package org.apache.catalina.tribes.membership;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.io.ChannelData;
import java.util.Arrays;
import java.net.SocketTimeoutException;
import org.apache.catalina.tribes.io.XByteBuffer;
import java.net.BindException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.catalina.tribes.Member;
import java.io.IOException;
import org.apache.catalina.tribes.util.ExecutorFactory;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.tribes.Channel;
import java.util.concurrent.ExecutorService;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.tribes.MembershipListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;

public class McastServiceImpl
{
    private static final Log log;
    protected static final int MAX_PACKET_SIZE = 65535;
    protected static final StringManager sm;
    protected volatile boolean doRunSender;
    protected volatile boolean doRunReceiver;
    protected volatile int startLevel;
    protected MulticastSocket socket;
    protected final MemberImpl member;
    protected final InetAddress address;
    protected final int port;
    protected final long timeToExpiration;
    protected final long sendFrequency;
    protected DatagramPacket sendPacket;
    protected DatagramPacket receivePacket;
    protected Membership membership;
    protected final MembershipListener service;
    protected final MessageListener msgservice;
    protected ReceiverThread receiver;
    protected SenderThread sender;
    protected final int mcastTTL;
    protected int mcastSoTimeout;
    protected final InetAddress mcastBindAddress;
    protected int recoveryCounter;
    protected long recoverySleepTime;
    protected boolean recoveryEnabled;
    protected final ExecutorService executor;
    protected final boolean localLoopbackDisabled;
    private Channel channel;
    protected final Object expiredMutex;
    private final Object sendLock;
    
    public McastServiceImpl(final MemberImpl member, final long sendFrequency, final long expireTime, final int port, final InetAddress bind, final InetAddress mcastAddress, final int ttl, final int soTimeout, final MembershipListener service, final MessageListener msgservice, final boolean localLoopbackDisabled) throws IOException {
        this.doRunSender = false;
        this.doRunReceiver = false;
        this.startLevel = 0;
        this.mcastSoTimeout = -1;
        this.recoveryCounter = 10;
        this.recoverySleepTime = 5000L;
        this.recoveryEnabled = true;
        this.executor = ExecutorFactory.newThreadPool(0, 2, 2L, TimeUnit.SECONDS);
        this.expiredMutex = new Object();
        this.sendLock = new Object();
        this.member = member;
        this.address = mcastAddress;
        this.port = port;
        this.mcastSoTimeout = soTimeout;
        this.mcastTTL = ttl;
        this.mcastBindAddress = bind;
        this.timeToExpiration = expireTime;
        this.service = service;
        this.msgservice = msgservice;
        this.sendFrequency = sendFrequency;
        this.localLoopbackDisabled = localLoopbackDisabled;
        this.init();
    }
    
    public void init() throws IOException {
        this.setupSocket();
        (this.sendPacket = new DatagramPacket(new byte[65535], 65535)).setAddress(this.address);
        this.sendPacket.setPort(this.port);
        (this.receivePacket = new DatagramPacket(new byte[65535], 65535)).setAddress(this.address);
        this.receivePacket.setPort(this.port);
        this.member.setCommand(new byte[0]);
        if (this.membership == null) {
            this.membership = new Membership(this.member);
        }
    }
    
    protected void setupSocket() throws IOException {
        if (this.mcastBindAddress != null) {
            try {
                McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.bind", this.address, Integer.toString(this.port)));
                this.socket = new MulticastSocket(new InetSocketAddress(this.address, this.port));
            }
            catch (final BindException e) {
                McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.bind.failed"));
                this.socket = new MulticastSocket(this.port);
            }
        }
        else {
            this.socket = new MulticastSocket(this.port);
        }
        this.socket.setLoopbackMode(this.localLoopbackDisabled);
        if (this.mcastBindAddress != null) {
            if (McastServiceImpl.log.isInfoEnabled()) {
                McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.setInterface", this.mcastBindAddress));
            }
            this.socket.setInterface(this.mcastBindAddress);
        }
        if (this.mcastSoTimeout <= 0) {
            this.mcastSoTimeout = (int)this.sendFrequency;
        }
        if (McastServiceImpl.log.isInfoEnabled()) {
            McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.setSoTimeout", Integer.toString(this.mcastSoTimeout)));
        }
        this.socket.setSoTimeout(this.mcastSoTimeout);
        if (this.mcastTTL >= 0) {
            if (McastServiceImpl.log.isInfoEnabled()) {
                McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.setTTL", Integer.toString(this.mcastTTL)));
            }
            this.socket.setTimeToLive(this.mcastTTL);
        }
    }
    
    public synchronized void start(final int level) throws IOException {
        boolean valid = false;
        if ((level & 0x4) == 0x4) {
            if (this.receiver != null) {
                throw new IllegalStateException(McastServiceImpl.sm.getString("mcastServiceImpl.receive.running"));
            }
            try {
                if (this.sender == null) {
                    this.socket.joinGroup(this.address);
                }
            }
            catch (final IOException iox) {
                McastServiceImpl.log.error((Object)McastServiceImpl.sm.getString("mcastServiceImpl.unable.join"));
                throw iox;
            }
            this.doRunReceiver = true;
            (this.receiver = new ReceiverThread()).setDaemon(true);
            this.receiver.start();
            valid = true;
        }
        if ((level & 0x8) == 0x8) {
            if (this.sender != null) {
                throw new IllegalStateException(McastServiceImpl.sm.getString("mcastServiceImpl.send.running"));
            }
            if (this.receiver == null) {
                this.socket.joinGroup(this.address);
            }
            this.send(false);
            this.doRunSender = true;
            (this.sender = new SenderThread(this.sendFrequency)).setDaemon(true);
            this.sender.start();
            valid = true;
        }
        if (!valid) {
            throw new IllegalArgumentException(McastServiceImpl.sm.getString("mcastServiceImpl.invalid.startLevel"));
        }
        this.waitForMembers(level);
        this.startLevel |= level;
    }
    
    private void waitForMembers(final int level) {
        final long memberwait = this.sendFrequency * 2L;
        if (McastServiceImpl.log.isInfoEnabled()) {
            McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.waitForMembers.start", Long.toString(memberwait), Integer.toString(level)));
        }
        try {
            Thread.sleep(memberwait);
        }
        catch (final InterruptedException ex) {}
        if (McastServiceImpl.log.isInfoEnabled()) {
            McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.waitForMembers.done", Integer.toString(level)));
        }
    }
    
    public synchronized boolean stop(final int level) throws IOException {
        boolean valid = false;
        if ((level & 0x4) == 0x4) {
            valid = true;
            this.doRunReceiver = false;
            if (this.receiver != null) {
                this.receiver.interrupt();
            }
            this.receiver = null;
        }
        if ((level & 0x8) == 0x8) {
            valid = true;
            this.doRunSender = false;
            if (this.sender != null) {
                this.sender.interrupt();
            }
            this.sender = null;
        }
        if (!valid) {
            throw new IllegalArgumentException(McastServiceImpl.sm.getString("mcastServiceImpl.invalid.stopLevel"));
        }
        this.startLevel &= ~level;
        if (this.startLevel == 0) {
            this.member.setCommand(Member.SHUTDOWN_PAYLOAD);
            this.send(false);
            try {
                this.socket.leaveGroup(this.address);
            }
            catch (final Exception ex) {}
            try {
                this.socket.close();
            }
            catch (final Exception ex2) {}
            this.member.setServiceStartTime(-1L);
        }
        return this.startLevel == 0;
    }
    
    public void receive() throws IOException {
        final boolean checkexpired = true;
        try {
            this.socket.receive(this.receivePacket);
            if (this.receivePacket.getLength() > 65535) {
                McastServiceImpl.log.error((Object)McastServiceImpl.sm.getString("mcastServiceImpl.packet.tooLong", Integer.toString(this.receivePacket.getLength())));
            }
            else {
                final byte[] data = new byte[this.receivePacket.getLength()];
                System.arraycopy(this.receivePacket.getData(), this.receivePacket.getOffset(), data, 0, data.length);
                if (XByteBuffer.firstIndexOf(data, 0, MemberImpl.TRIBES_MBR_BEGIN) == 0) {
                    this.memberDataReceived(data);
                }
                else {
                    this.memberBroadcastsReceived(data);
                }
            }
        }
        catch (final SocketTimeoutException ex) {}
        if (checkexpired) {
            this.checkExpired();
        }
    }
    
    private void memberDataReceived(final byte[] data) {
        final Member m = MemberImpl.getMember(data);
        if (McastServiceImpl.log.isTraceEnabled()) {
            McastServiceImpl.log.trace((Object)("Mcast receive ping from member " + m));
        }
        Runnable t = null;
        if (Arrays.equals(m.getCommand(), Member.SHUTDOWN_PAYLOAD)) {
            if (McastServiceImpl.log.isDebugEnabled()) {
                McastServiceImpl.log.debug((Object)("Member has shutdown:" + m));
            }
            this.membership.removeMember(m);
            t = new Runnable() {
                @Override
                public void run() {
                    final String name = Thread.currentThread().getName();
                    try {
                        Thread.currentThread().setName("Membership-MemberDisappeared.");
                        McastServiceImpl.this.service.memberDisappeared(m);
                    }
                    finally {
                        Thread.currentThread().setName(name);
                    }
                }
            };
        }
        else if (this.membership.memberAlive(m)) {
            if (McastServiceImpl.log.isDebugEnabled()) {
                McastServiceImpl.log.debug((Object)("Mcast add member " + m));
            }
            t = new Runnable() {
                @Override
                public void run() {
                    final String name = Thread.currentThread().getName();
                    try {
                        Thread.currentThread().setName("Membership-MemberAdded.");
                        McastServiceImpl.this.service.memberAdded(m);
                    }
                    finally {
                        Thread.currentThread().setName(name);
                    }
                }
            };
        }
        if (t != null) {
            this.executor.execute(t);
        }
    }
    
    private void memberBroadcastsReceived(final byte[] b) {
        if (McastServiceImpl.log.isTraceEnabled()) {
            McastServiceImpl.log.trace((Object)"Mcast received broadcasts.");
        }
        final XByteBuffer buffer = new XByteBuffer(b, true);
        if (buffer.countPackages(true) > 0) {
            final int count = buffer.countPackages();
            final ChannelData[] data = new ChannelData[count];
            for (int i = 0; i < count; ++i) {
                try {
                    data[i] = buffer.extractPackage(true);
                }
                catch (final IllegalStateException ise) {
                    McastServiceImpl.log.debug((Object)"Unable to decode message.", (Throwable)ise);
                }
            }
            final Runnable t = new Runnable() {
                @Override
                public void run() {
                    final String name = Thread.currentThread().getName();
                    try {
                        Thread.currentThread().setName("Membership-MemberAdded.");
                        for (final ChannelData datum : data) {
                            try {
                                if (datum != null && !McastServiceImpl.this.member.equals(datum.getAddress())) {
                                    McastServiceImpl.this.msgservice.messageReceived(datum);
                                }
                            }
                            catch (final Throwable t) {
                                if (t instanceof ThreadDeath) {
                                    throw (ThreadDeath)t;
                                }
                                if (t instanceof VirtualMachineError) {
                                    throw (VirtualMachineError)t;
                                }
                                McastServiceImpl.log.error((Object)McastServiceImpl.sm.getString("mcastServiceImpl.unableReceive.broadcastMessage"), t);
                            }
                        }
                    }
                    finally {
                        Thread.currentThread().setName(name);
                    }
                }
            };
            this.executor.execute(t);
        }
    }
    
    protected void checkExpired() {
        synchronized (this.expiredMutex) {
            final Member[] arr$;
            final Member[] expired = arr$ = this.membership.expire(this.timeToExpiration);
            for (final Member member : arr$) {
                if (McastServiceImpl.log.isDebugEnabled()) {
                    McastServiceImpl.log.debug((Object)("Mcast expire  member " + member));
                }
                try {
                    final Runnable t = new Runnable() {
                        @Override
                        public void run() {
                            final String name = Thread.currentThread().getName();
                            try {
                                Thread.currentThread().setName("Membership-MemberExpired.");
                                McastServiceImpl.this.service.memberDisappeared(member);
                            }
                            finally {
                                Thread.currentThread().setName(name);
                            }
                        }
                    };
                    this.executor.execute(t);
                }
                catch (final Exception x) {
                    McastServiceImpl.log.error((Object)McastServiceImpl.sm.getString("mcastServiceImpl.memberDisappeared.failed"), (Throwable)x);
                }
            }
        }
    }
    
    public void send(final boolean checkexpired) throws IOException {
        this.send(checkexpired, null);
    }
    
    public void send(boolean checkexpired, DatagramPacket packet) throws IOException {
        checkexpired = (checkexpired && packet == null);
        if (packet == null) {
            this.member.inc();
            if (McastServiceImpl.log.isTraceEnabled()) {
                McastServiceImpl.log.trace((Object)("Mcast send ping from member " + this.member));
            }
            final byte[] data = this.member.getData();
            packet = new DatagramPacket(data, data.length);
        }
        else if (McastServiceImpl.log.isTraceEnabled()) {
            McastServiceImpl.log.trace((Object)("Sending message broadcast " + packet.getLength() + " bytes from " + this.member));
        }
        packet.setAddress(this.address);
        packet.setPort(this.port);
        synchronized (this.sendLock) {
            this.socket.send(packet);
        }
        if (checkexpired) {
            this.checkExpired();
        }
    }
    
    public long getServiceStartTime() {
        return (this.member != null) ? this.member.getServiceStartTime() : -1L;
    }
    
    public int getRecoveryCounter() {
        return this.recoveryCounter;
    }
    
    public boolean isRecoveryEnabled() {
        return this.recoveryEnabled;
    }
    
    public long getRecoverySleepTime() {
        return this.recoverySleepTime;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }
    
    public void setRecoveryCounter(final int recoveryCounter) {
        this.recoveryCounter = recoveryCounter;
    }
    
    public void setRecoveryEnabled(final boolean recoveryEnabled) {
        this.recoveryEnabled = recoveryEnabled;
    }
    
    public void setRecoverySleepTime(final long recoverySleepTime) {
        this.recoverySleepTime = recoverySleepTime;
    }
    
    static {
        log = LogFactory.getLog((Class)McastService.class);
        sm = StringManager.getManager("org.apache.catalina.tribes.membership");
    }
    
    public class ReceiverThread extends Thread
    {
        int errorCounter;
        
        public ReceiverThread() {
            this.errorCounter = 0;
            String channelName = "";
            if (McastServiceImpl.this.channel.getName() != null) {
                channelName = "[" + McastServiceImpl.this.channel.getName() + "]";
            }
            this.setName("Tribes-MembershipReceiver" + channelName);
        }
        
        @Override
        public void run() {
            while (McastServiceImpl.this.doRunReceiver) {
                try {
                    McastServiceImpl.this.receive();
                    this.errorCounter = 0;
                }
                catch (final ArrayIndexOutOfBoundsException ax) {
                    if (!McastServiceImpl.log.isDebugEnabled()) {
                        continue;
                    }
                    McastServiceImpl.log.debug((Object)"Invalid member mcast package.", (Throwable)ax);
                }
                catch (final Exception x) {
                    if (this.errorCounter == 0 && McastServiceImpl.this.doRunReceiver) {
                        McastServiceImpl.log.warn((Object)McastServiceImpl.sm.getString("mcastServiceImpl.error.receiving"), (Throwable)x);
                    }
                    else if (McastServiceImpl.log.isDebugEnabled()) {
                        McastServiceImpl.log.debug((Object)("Error receiving mcast package" + (McastServiceImpl.this.doRunReceiver ? ". Sleeping 500ms" : ".")), (Throwable)x);
                    }
                    if (!McastServiceImpl.this.doRunReceiver) {
                        continue;
                    }
                    try {
                        Thread.sleep(500L);
                    }
                    catch (final Exception ex) {}
                    if (++this.errorCounter < McastServiceImpl.this.recoveryCounter) {
                        continue;
                    }
                    this.errorCounter = 0;
                    RecoveryThread.recover(McastServiceImpl.this);
                }
            }
        }
    }
    
    public class SenderThread extends Thread
    {
        final long time;
        int errorCounter;
        
        public SenderThread(final long time) {
            this.errorCounter = 0;
            this.time = time;
            String channelName = "";
            if (McastServiceImpl.this.channel.getName() != null) {
                channelName = "[" + McastServiceImpl.this.channel.getName() + "]";
            }
            this.setName("Tribes-MembershipSender" + channelName);
        }
        
        @Override
        public void run() {
            while (McastServiceImpl.this.doRunSender) {
                try {
                    McastServiceImpl.this.send(true);
                    this.errorCounter = 0;
                }
                catch (final Exception x) {
                    if (this.errorCounter == 0) {
                        McastServiceImpl.log.warn((Object)McastServiceImpl.sm.getString("mcastServiceImpl.send.failed"), (Throwable)x);
                    }
                    else {
                        McastServiceImpl.log.debug((Object)"Unable to send mcast message.", (Throwable)x);
                    }
                    if (++this.errorCounter >= McastServiceImpl.this.recoveryCounter) {
                        this.errorCounter = 0;
                        RecoveryThread.recover(McastServiceImpl.this);
                    }
                }
                try {
                    Thread.sleep(this.time);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    protected static class RecoveryThread extends Thread
    {
        private static final AtomicBoolean running;
        final McastServiceImpl parent;
        
        public static synchronized void recover(final McastServiceImpl parent) {
            if (!parent.isRecoveryEnabled()) {
                return;
            }
            if (!RecoveryThread.running.compareAndSet(false, true)) {
                return;
            }
            final Thread t = new RecoveryThread(parent);
            String channelName = "";
            if (parent.channel.getName() != null) {
                channelName = "[" + parent.channel.getName() + "]";
            }
            t.setName("Tribes-MembershipRecovery" + channelName);
            t.setDaemon(true);
            t.start();
        }
        
        public RecoveryThread(final McastServiceImpl parent) {
            this.parent = parent;
        }
        
        public boolean stopService() {
            try {
                this.parent.stop(12);
                return true;
            }
            catch (final Exception x) {
                McastServiceImpl.log.warn((Object)McastServiceImpl.sm.getString("mcastServiceImpl.recovery.stopFailed"), (Throwable)x);
                return false;
            }
        }
        
        public boolean startService() {
            try {
                this.parent.init();
                this.parent.start(12);
                return true;
            }
            catch (final Exception x) {
                McastServiceImpl.log.warn((Object)McastServiceImpl.sm.getString("mcastServiceImpl.recovery.startFailed"), (Throwable)x);
                return false;
            }
        }
        
        @Override
        public void run() {
            boolean success = false;
            int attempt = 0;
            try {
                while (!success) {
                    if (McastServiceImpl.log.isInfoEnabled()) {
                        McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.recovery"));
                    }
                    if (this.stopService() & this.startService()) {
                        success = true;
                        if (McastServiceImpl.log.isInfoEnabled()) {
                            McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.recovery.successful"));
                        }
                    }
                    try {
                        if (success) {
                            continue;
                        }
                        if (McastServiceImpl.log.isInfoEnabled()) {
                            McastServiceImpl.log.info((Object)McastServiceImpl.sm.getString("mcastServiceImpl.recovery.failed", Integer.toString(++attempt), Long.toString(this.parent.recoverySleepTime)));
                        }
                        Thread.sleep(this.parent.recoverySleepTime);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            finally {
                RecoveryThread.running.set(false);
            }
        }
        
        static {
            running = new AtomicBoolean(false);
        }
    }
}
