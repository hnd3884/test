package org.apache.catalina.tribes.transport.nio;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.tribes.ChannelMessage;
import java.net.SocketAddress;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.RemoteProcessException;
import java.sql.Timestamp;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.util.Logs;
import org.apache.catalina.tribes.transport.Constants;
import java.nio.channels.WritableByteChannel;
import org.apache.catalina.tribes.io.ChannelData;
import java.nio.channels.SocketChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import org.apache.catalina.tribes.io.ObjectReader;
import java.nio.channels.DatagramChannel;
import org.apache.catalina.tribes.io.ListenCallback;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.transport.AbstractRxTask;

public class NioReplicationTask extends AbstractRxTask
{
    private static final Log log;
    protected static final StringManager sm;
    private ByteBuffer buffer;
    private SelectionKey key;
    private int rxBufSize;
    private final NioReceiver receiver;
    
    public NioReplicationTask(final ListenCallback callback, final NioReceiver receiver) {
        super(callback);
        this.buffer = null;
        this.receiver = receiver;
    }
    
    @Override
    public synchronized void run() {
        if (this.buffer == null) {
            int size = this.getRxBufSize();
            if (this.key.channel() instanceof DatagramChannel) {
                size = 65535;
            }
            if ((this.getOptions() & 0x4) == 0x4) {
                this.buffer = ByteBuffer.allocateDirect(size);
            }
            else {
                this.buffer = ByteBuffer.allocate(size);
            }
        }
        else {
            this.buffer.clear();
        }
        if (this.key == null) {
            return;
        }
        if (NioReplicationTask.log.isTraceEnabled()) {
            NioReplicationTask.log.trace((Object)("Servicing key:" + this.key));
        }
        try {
            final ObjectReader reader = (ObjectReader)this.key.attachment();
            if (reader == null) {
                if (NioReplicationTask.log.isTraceEnabled()) {
                    NioReplicationTask.log.trace((Object)("No object reader, cancelling:" + this.key));
                }
                this.cancelKey(this.key);
            }
            else {
                if (NioReplicationTask.log.isTraceEnabled()) {
                    NioReplicationTask.log.trace((Object)("Draining channel:" + this.key));
                }
                this.drainChannel(this.key, reader);
            }
        }
        catch (final Exception e) {
            if (!(e instanceof CancelledKeyException)) {
                if (e instanceof IOException) {
                    if (NioReplicationTask.log.isDebugEnabled()) {
                        NioReplicationTask.log.debug((Object)("IOException in replication worker, unable to drain channel. Probable cause: Keep alive socket closed[" + e.getMessage() + "]."), (Throwable)e);
                    }
                    else {
                        NioReplicationTask.log.warn((Object)NioReplicationTask.sm.getString("nioReplicationTask.unable.drainChannel.ioe", e.getMessage()));
                    }
                }
                else if (NioReplicationTask.log.isErrorEnabled()) {
                    NioReplicationTask.log.error((Object)NioReplicationTask.sm.getString("nioReplicationTask.exception.drainChannel"), (Throwable)e);
                }
            }
            this.cancelKey(this.key);
        }
        this.key = null;
        this.getTaskPool().returnWorker(this);
    }
    
    public synchronized void serviceChannel(final SelectionKey key) {
        if (NioReplicationTask.log.isTraceEnabled()) {
            NioReplicationTask.log.trace((Object)("About to service key:" + key));
        }
        final ObjectReader reader = (ObjectReader)key.attachment();
        if (reader != null) {
            reader.setLastAccess(System.currentTimeMillis());
        }
        (this.key = key).interestOps(key.interestOps() & 0xFFFFFFFE);
        key.interestOps(key.interestOps() & 0xFFFFFFFB);
    }
    
    protected void drainChannel(final SelectionKey key, final ObjectReader reader) throws Exception {
        reader.access();
        final ReadableByteChannel channel = (ReadableByteChannel)key.channel();
        int count = -1;
        this.buffer.clear();
        SocketAddress saddr = null;
        if (channel instanceof SocketChannel) {
            while ((count = channel.read(this.buffer)) > 0) {
                this.buffer.flip();
                if (this.buffer.hasArray()) {
                    reader.append(this.buffer.array(), 0, count, false);
                }
                else {
                    reader.append(this.buffer, count, false);
                }
                this.buffer.clear();
                if (reader.hasPackage()) {
                    break;
                }
            }
        }
        else if (channel instanceof DatagramChannel) {
            final DatagramChannel dchannel = (DatagramChannel)channel;
            saddr = dchannel.receive(this.buffer);
            this.buffer.flip();
            if (this.buffer.hasArray()) {
                reader.append(this.buffer.array(), 0, this.buffer.limit() - this.buffer.position(), false);
            }
            else {
                reader.append(this.buffer, this.buffer.limit() - this.buffer.position(), false);
            }
            this.buffer.clear();
            count = (reader.hasPackage() ? 1 : -1);
        }
        final int pkgcnt = reader.count();
        if (count < 0 && pkgcnt == 0) {
            this.remoteEof(key);
            return;
        }
        final ChannelMessage[] msgs = (pkgcnt == 0) ? ChannelData.EMPTY_DATA_ARRAY : reader.execute();
        this.registerForRead(key, reader);
        for (final ChannelMessage msg : msgs) {
            if (ChannelData.sendAckAsync(msg.getOptions())) {
                this.sendAck(key, (WritableByteChannel)channel, Constants.ACK_COMMAND, saddr);
            }
            try {
                if (Logs.MESSAGES.isTraceEnabled()) {
                    try {
                        Logs.MESSAGES.trace((Object)("NioReplicationThread - Received msg:" + new UniqueId(msg.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis())));
                    }
                    catch (final Throwable t) {}
                }
                this.getCallback().messageDataReceived(msg);
                if (ChannelData.sendAckSync(msg.getOptions())) {
                    this.sendAck(key, (WritableByteChannel)channel, Constants.ACK_COMMAND, saddr);
                }
            }
            catch (final RemoteProcessException e) {
                if (NioReplicationTask.log.isDebugEnabled()) {
                    NioReplicationTask.log.error((Object)NioReplicationTask.sm.getString("nioReplicationTask.process.clusterMsg.failed"), (Throwable)e);
                }
                if (ChannelData.sendAckSync(msg.getOptions())) {
                    this.sendAck(key, (WritableByteChannel)channel, Constants.FAIL_ACK_COMMAND, saddr);
                }
            }
            catch (final Exception e2) {
                NioReplicationTask.log.error((Object)NioReplicationTask.sm.getString("nioReplicationTask.process.clusterMsg.failed"), (Throwable)e2);
                if (ChannelData.sendAckSync(msg.getOptions())) {
                    this.sendAck(key, (WritableByteChannel)channel, Constants.FAIL_ACK_COMMAND, saddr);
                }
            }
            if (this.getUseBufferPool()) {
                BufferPool.getBufferPool().returnBuffer(msg.getMessage());
                msg.setMessage(null);
            }
        }
        if (count < 0) {
            this.remoteEof(key);
        }
    }
    
    private void remoteEof(final SelectionKey key) {
        if (NioReplicationTask.log.isDebugEnabled()) {
            NioReplicationTask.log.debug((Object)"Channel closed on the remote end, disconnecting");
        }
        this.cancelKey(key);
    }
    
    protected void registerForRead(final SelectionKey key, final ObjectReader reader) {
        if (NioReplicationTask.log.isTraceEnabled()) {
            NioReplicationTask.log.trace((Object)("Adding key for read event:" + key));
        }
        reader.finish();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    if (key.isValid()) {
                        key.selector().wakeup();
                        final int resumeOps = key.interestOps() | 0x1;
                        key.interestOps(resumeOps);
                        if (NioReplicationTask.log.isTraceEnabled()) {
                            NioReplicationTask.log.trace((Object)("Registering key for read:" + key));
                        }
                    }
                }
                catch (final CancelledKeyException ckx) {
                    NioReceiver.cancelledKey(key);
                    if (NioReplicationTask.log.isTraceEnabled()) {
                        NioReplicationTask.log.trace((Object)("CKX Cancelling key:" + key));
                    }
                }
                catch (final Exception x) {
                    NioReplicationTask.log.error((Object)NioReplicationTask.sm.getString("nioReplicationTask.error.register.key", key), (Throwable)x);
                }
            }
        };
        this.receiver.addEvent(r);
    }
    
    private void cancelKey(final SelectionKey key) {
        if (NioReplicationTask.log.isTraceEnabled()) {
            NioReplicationTask.log.trace((Object)("Adding key for cancel event:" + key));
        }
        final ObjectReader reader = (ObjectReader)key.attachment();
        if (reader != null) {
            reader.setCancelled(true);
            reader.finish();
        }
        final Runnable cx = new Runnable() {
            @Override
            public void run() {
                if (NioReplicationTask.log.isTraceEnabled()) {
                    NioReplicationTask.log.trace((Object)("Cancelling key:" + key));
                }
                NioReceiver.cancelledKey(key);
            }
        };
        this.receiver.addEvent(cx);
    }
    
    protected void sendAck(final SelectionKey key, final WritableByteChannel channel, final byte[] command, final SocketAddress udpaddr) {
        try {
            final ByteBuffer buf = ByteBuffer.wrap(command);
            int total = 0;
            if (channel instanceof DatagramChannel) {
                for (DatagramChannel dchannel = (DatagramChannel)channel; total < command.length; total += dchannel.send(buf, udpaddr)) {}
            }
            else {
                while (total < command.length) {
                    total += channel.write(buf);
                }
            }
            if (NioReplicationTask.log.isTraceEnabled()) {
                NioReplicationTask.log.trace((Object)("ACK sent to " + ((channel instanceof SocketChannel) ? ((SocketChannel)channel).socket().getInetAddress() : ((DatagramChannel)channel).socket().getInetAddress())));
            }
        }
        catch (final IOException x) {
            NioReplicationTask.log.warn((Object)NioReplicationTask.sm.getString("nioReplicationTask.unable.ack", x.getMessage()));
        }
    }
    
    public void setRxBufSize(final int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }
    
    public int getRxBufSize() {
        return this.rxBufSize;
    }
    
    static {
        log = LogFactory.getLog((Class)NioReplicationTask.class);
        sm = StringManager.getManager(NioReplicationTask.class);
    }
}
