package org.apache.catalina.tribes.transport.bio;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.tribes.io.ObjectReader;
import java.net.Socket;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import java.io.IOException;
import org.apache.catalina.tribes.transport.RxTaskPool;
import java.net.ServerSocket;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.transport.ReceiverBase;

public class BioReceiver extends ReceiverBase implements Runnable
{
    private static final Log log;
    protected static final StringManager sm;
    protected ServerSocket serverSocket;
    
    @Override
    public void start() throws IOException {
        super.start();
        try {
            this.setPool(new RxTaskPool(this.getMaxThreads(), this.getMinThreads(), this));
        }
        catch (final Exception x) {
            BioReceiver.log.fatal((Object)BioReceiver.sm.getString("bioReceiver.threadpool.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
        try {
            this.getBind();
            this.bind();
            String channelName = "";
            if (this.getChannel().getName() != null) {
                channelName = "[" + this.getChannel().getName() + "]";
            }
            final Thread t = new Thread(this, "BioReceiver" + channelName);
            t.setDaemon(true);
            t.start();
        }
        catch (final Exception x) {
            BioReceiver.log.fatal((Object)BioReceiver.sm.getString("bioReceiver.start.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
    }
    
    @Override
    public AbstractRxTask createRxTask() {
        return this.getReplicationThread();
    }
    
    protected BioReplicationTask getReplicationThread() {
        final BioReplicationTask result = new BioReplicationTask(this);
        result.setOptions(this.getWorkerThreadOptions());
        result.setUseBufferPool(this.getUseBufferPool());
        return result;
    }
    
    @Override
    public void stop() {
        this.setListen(false);
        try {
            this.serverSocket.close();
        }
        catch (final Exception x) {
            if (BioReceiver.log.isDebugEnabled()) {
                BioReceiver.log.debug((Object)BioReceiver.sm.getString("bioReceiver.socket.closeFailed"), (Throwable)x);
            }
        }
        super.stop();
    }
    
    protected void bind() throws IOException {
        this.bind(this.serverSocket = new ServerSocket(), this.getPort(), this.getAutoBind());
    }
    
    @Override
    public void run() {
        try {
            this.listen();
        }
        catch (final Exception x) {
            BioReceiver.log.error((Object)BioReceiver.sm.getString("bioReceiver.run.fail"), (Throwable)x);
        }
    }
    
    public void listen() throws Exception {
        if (this.doListen()) {
            BioReceiver.log.warn((Object)BioReceiver.sm.getString("bioReceiver.already.started"));
            return;
        }
        this.setListen(true);
        while (this.doListen()) {
            Socket socket = null;
            if (this.getTaskPool().available() < 1 && BioReceiver.log.isWarnEnabled()) {
                BioReceiver.log.warn((Object)BioReceiver.sm.getString("bioReceiver.threads.busy"));
            }
            final BioReplicationTask task = (BioReplicationTask)this.getTaskPool().getRxTask();
            if (task == null) {
                continue;
            }
            try {
                socket = this.serverSocket.accept();
            }
            catch (final Exception x) {
                if (this.doListen()) {
                    throw x;
                }
            }
            if (!this.doListen()) {
                task.setDoRun(false);
                task.serviceSocket(null, null);
                this.getExecutor().execute(task);
                break;
            }
            if (socket == null) {
                continue;
            }
            socket.setReceiveBufferSize(this.getRxBufSize());
            socket.setSendBufferSize(this.getTxBufSize());
            socket.setTcpNoDelay(this.getTcpNoDelay());
            socket.setKeepAlive(this.getSoKeepAlive());
            socket.setOOBInline(this.getOoBInline());
            socket.setReuseAddress(this.getSoReuseAddress());
            socket.setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
            socket.setSoTimeout(this.getTimeout());
            final ObjectReader reader = new ObjectReader(socket);
            task.serviceSocket(socket, reader);
            this.getExecutor().execute(task);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)BioReceiver.class);
        sm = StringManager.getManager(BioReceiver.class);
    }
}
