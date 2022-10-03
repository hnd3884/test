package org.apache.catalina.tribes.transport.nio;

import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.LogFactory;
import java.util.Map;
import java.net.UnknownHostException;
import java.util.Iterator;
import org.apache.catalina.tribes.transport.SenderState;
import java.sql.Timestamp;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.util.Logs;
import java.nio.channels.SelectionKey;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.ChannelMessage;
import java.io.IOException;
import org.apache.catalina.tribes.Member;
import java.util.HashMap;
import java.nio.channels.Selector;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.AbstractSender;

public class ParallelNioSender extends AbstractSender implements MultiPointSender
{
    private static final Log log;
    protected static final StringManager sm;
    protected final long selectTimeout = 5000L;
    protected final Selector selector;
    protected final HashMap<Member, NioSender> nioSenders;
    
    public ParallelNioSender() throws IOException {
        this.nioSenders = new HashMap<Member, NioSender>();
        this.selector = Selector.open();
        this.setConnected(true);
    }
    
    @Override
    public synchronized void sendMessage(final Member[] destination, final ChannelMessage msg) throws ChannelException {
        final long start = System.currentTimeMillis();
        this.setUdpBased((msg.getOptions() & 0x20) == 0x20);
        final byte[] data = XByteBuffer.createDataPackage((ChannelData)msg);
        final NioSender[] senders = this.setupForSend(destination);
        this.connect(senders);
        this.setData(senders, data);
        int remaining = senders.length;
        ChannelException cx = null;
        try {
            long delta = System.currentTimeMillis() - start;
            final boolean waitForAck = (0x2 & msg.getOptions()) == 0x2;
            while (remaining > 0 && delta < this.getTimeout()) {
                try {
                    final SendResult result = this.doLoop(5000L, this.getMaxRetryAttempts(), waitForAck, msg);
                    remaining -= result.getCompleted();
                    if (result.getFailed() != null) {
                        remaining -= result.getFailed().getFaultyMembers().length;
                        if (cx == null) {
                            cx = result.getFailed();
                        }
                        else {
                            cx.addFaultyMember(result.getFailed().getFaultyMembers());
                        }
                    }
                }
                catch (final Exception x) {
                    if (ParallelNioSender.log.isTraceEnabled()) {
                        ParallelNioSender.log.trace((Object)"Error sending message", (Throwable)x);
                    }
                    if (cx == null) {
                        if (x instanceof ChannelException) {
                            cx = (ChannelException)x;
                        }
                        else {
                            cx = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.send.failed"), x);
                        }
                    }
                    for (final NioSender sender : senders) {
                        if (!sender.isComplete()) {
                            cx.addFaultyMember(sender.getDestination(), x);
                        }
                    }
                    throw cx;
                }
                delta = System.currentTimeMillis() - start;
            }
            if (remaining > 0) {
                final ChannelException cxtimeout = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.operation.timedout", Long.toString(this.getTimeout())));
                if (cx == null) {
                    cx = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.operation.timedout", Long.toString(this.getTimeout())));
                }
                for (final NioSender sender : senders) {
                    if (!sender.isComplete()) {
                        cx.addFaultyMember(sender.getDestination(), cxtimeout);
                    }
                }
                throw cx;
            }
            if (cx != null) {
                throw cx;
            }
        }
        catch (final Exception x2) {
            try {
                this.disconnect();
            }
            catch (final Exception ex) {}
            if (x2 instanceof ChannelException) {
                throw (ChannelException)x2;
            }
            throw new ChannelException(x2);
        }
    }
    
    private SendResult doLoop(final long selectTimeOut, final int maxAttempts, final boolean waitForAck, final ChannelMessage msg) throws ChannelException {
        final SendResult result = new SendResult();
        int selectedKeys;
        try {
            selectedKeys = this.selector.select(selectTimeOut);
        }
        catch (final IOException ioe) {
            throw new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.send.failed"), ioe);
        }
        if (selectedKeys == 0) {
            return result;
        }
        final Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
        while (it.hasNext()) {
            final SelectionKey sk = it.next();
            it.remove();
            final int readyOps = sk.readyOps();
            sk.interestOps(sk.interestOps() & ~readyOps);
            final NioSender sender = (NioSender)sk.attachment();
            try {
                if (!sender.process(sk, waitForAck)) {
                    continue;
                }
                sender.setComplete(true);
                result.complete(sender);
                if (Logs.MESSAGES.isTraceEnabled()) {
                    Logs.MESSAGES.trace((Object)("ParallelNioSender - Sent msg:" + new UniqueId(msg.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis()) + " to " + sender.getDestination().getName()));
                }
                SenderState.getSenderState(sender.getDestination()).setReady();
            }
            catch (final Exception x) {
                if (ParallelNioSender.log.isTraceEnabled()) {
                    ParallelNioSender.log.trace((Object)("Error while processing send to " + sender.getDestination().getName()), (Throwable)x);
                }
                final SenderState state = SenderState.getSenderState(sender.getDestination());
                final int attempt = sender.getAttempt() + 1;
                final boolean retry = attempt <= maxAttempts && maxAttempts > 0;
                synchronized (state) {
                    if (state.isSuspect()) {
                        state.setFailing();
                    }
                    if (state.isReady()) {
                        state.setSuspect();
                        if (retry) {
                            ParallelNioSender.log.warn((Object)ParallelNioSender.sm.getString("parallelNioSender.send.fail.retrying", sender.getDestination().getName()));
                        }
                        else {
                            ParallelNioSender.log.warn((Object)ParallelNioSender.sm.getString("parallelNioSender.send.fail", sender.getDestination().getName()), (Throwable)x);
                        }
                    }
                }
                if (!this.isConnected()) {
                    ParallelNioSender.log.warn((Object)ParallelNioSender.sm.getString("parallelNioSender.sender.disconnected.notRetry", sender.getDestination().getName()));
                    final ChannelException cx = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.sender.disconnected.sendFailed"), x);
                    cx.addFaultyMember(sender.getDestination(), x);
                    result.failed(cx);
                    break;
                }
                final byte[] data = sender.getMessage();
                if (retry) {
                    try {
                        sender.disconnect();
                        sender.connect();
                        sender.setAttempt(attempt);
                        sender.setMessage(data);
                    }
                    catch (final Exception ignore) {
                        state.setFailing();
                    }
                }
                else {
                    final ChannelException cx2 = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.sendFailed.attempt", Integer.toString(sender.getAttempt()), Integer.toString(maxAttempts)), x);
                    cx2.addFaultyMember(sender.getDestination(), x);
                    result.failed(cx2);
                }
            }
        }
        return result;
    }
    
    private void connect(final NioSender[] senders) throws ChannelException {
        ChannelException x = null;
        for (final NioSender sender : senders) {
            try {
                sender.connect();
            }
            catch (final IOException io) {
                if (x == null) {
                    x = new ChannelException(io);
                }
                x.addFaultyMember(sender.getDestination(), io);
            }
        }
        if (x != null) {
            throw x;
        }
    }
    
    private void setData(final NioSender[] senders, final byte[] data) throws ChannelException {
        ChannelException x = null;
        for (final NioSender sender : senders) {
            try {
                sender.setMessage(data);
            }
            catch (final IOException io) {
                if (x == null) {
                    x = new ChannelException(io);
                }
                x.addFaultyMember(sender.getDestination(), io);
            }
        }
        if (x != null) {
            throw x;
        }
    }
    
    private NioSender[] setupForSend(final Member[] destination) throws ChannelException {
        ChannelException cx = null;
        final NioSender[] result = new NioSender[destination.length];
        for (int i = 0; i < destination.length; ++i) {
            NioSender sender = this.nioSenders.get(destination[i]);
            try {
                if (sender == null) {
                    sender = new NioSender();
                    AbstractSender.transferProperties(this, sender);
                    this.nioSenders.put(destination[i], sender);
                }
                sender.reset();
                sender.setDestination(destination[i]);
                sender.setSelector(this.selector);
                sender.setUdpBased(this.isUdpBased());
                result[i] = sender;
            }
            catch (final UnknownHostException x) {
                if (cx == null) {
                    cx = new ChannelException(ParallelNioSender.sm.getString("parallelNioSender.unable.setup.NioSender"), x);
                }
                cx.addFaultyMember(destination[i], x);
            }
        }
        if (cx != null) {
            throw cx;
        }
        return result;
    }
    
    @Override
    public void connect() {
        this.setConnected(true);
    }
    
    private synchronized void close() throws ChannelException {
        ChannelException x = null;
        final Object[] arr$;
        final Object[] members = arr$ = this.nioSenders.keySet().toArray();
        for (final Object member : arr$) {
            final Member mbr = (Member)member;
            try {
                final NioSender sender = this.nioSenders.get(mbr);
                sender.disconnect();
            }
            catch (final Exception e) {
                if (x == null) {
                    x = new ChannelException(e);
                }
                x.addFaultyMember(mbr, e);
            }
            this.nioSenders.remove(mbr);
        }
        if (x != null) {
            throw x;
        }
    }
    
    @Override
    public void add(final Member member) {
    }
    
    @Override
    public void remove(final Member member) {
        final NioSender sender = this.nioSenders.remove(member);
        if (sender != null) {
            sender.disconnect();
        }
    }
    
    @Override
    public synchronized void disconnect() {
        this.setConnected(false);
        try {
            this.close();
        }
        catch (final Exception ex) {}
    }
    
    public void finalize() throws Throwable {
        try {
            this.disconnect();
        }
        catch (final Exception ex) {}
        try {
            this.selector.close();
        }
        catch (final Exception e) {
            if (ParallelNioSender.log.isDebugEnabled()) {
                ParallelNioSender.log.debug((Object)"Failed to close selector", (Throwable)e);
            }
        }
        super.finalize();
    }
    
    @Override
    public boolean keepalive() {
        boolean result = false;
        final Iterator<Map.Entry<Member, NioSender>> i = this.nioSenders.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry<Member, NioSender> entry = i.next();
            final NioSender sender = entry.getValue();
            if (sender.keepalive()) {
                i.remove();
                result = true;
            }
            else {
                try {
                    sender.read();
                }
                catch (final IOException x) {
                    sender.disconnect();
                    sender.reset();
                    i.remove();
                    result = true;
                }
                catch (final Exception x2) {
                    ParallelNioSender.log.warn((Object)ParallelNioSender.sm.getString("parallelNioSender.error.keepalive", sender), (Throwable)x2);
                }
            }
        }
        if (result) {
            try {
                this.selector.selectNow();
            }
            catch (final Exception ex) {}
        }
        return result;
    }
    
    static {
        log = LogFactory.getLog((Class)ParallelNioSender.class);
        sm = StringManager.getManager(ParallelNioSender.class);
    }
    
    private static class SendResult
    {
        private List<NioSender> completeSenders;
        private ChannelException exception;
        
        private SendResult() {
            this.completeSenders = new ArrayList<NioSender>();
            this.exception = null;
        }
        
        private void complete(final NioSender sender) {
            if (!this.completeSenders.contains(sender)) {
                this.completeSenders.add(sender);
            }
        }
        
        private int getCompleted() {
            return this.completeSenders.size();
        }
        
        private void failed(final ChannelException cx) {
            if (this.exception == null) {
                this.exception = cx;
            }
            this.exception.addFaultyMember(cx.getFaultyMembers());
        }
        
        private ChannelException getFailed() {
            return this.exception;
        }
    }
}
