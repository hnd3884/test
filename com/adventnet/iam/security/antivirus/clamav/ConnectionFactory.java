package com.adventnet.iam.security.antivirus.clamav;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

class ConnectionFactory
{
    private static final Logger LOGGER;
    private static final byte[] START_AV_SESSION;
    private static final byte[] END_AV_SESSION;
    private static ConnectionFactory factory;
    private static ExecutorService exe;
    private final ConcurrentLinkedQueue<SocketChannel> queue;
    private final int minLimit;
    private final String clamAVHost;
    private final int clamAVPort;
    
    protected ConnectionFactory(final CLAMAVConfiguration config) {
        this.clamAVHost = config.getHost();
        this.clamAVPort = config.getPort();
        this.minLimit = config.getPersistantConnections();
        this.queue = new ConcurrentLinkedQueue<SocketChannel>();
    }
    
    public static synchronized ConnectionFactory getFactory(final CLAMAVConfiguration config) {
        if (ConnectionFactory.factory == null) {
            ConnectionFactory.factory = new ConnectionFactory(config);
            ConnectionFactory.exe = Executors.newCachedThreadPool();
        }
        return ConnectionFactory.factory;
    }
    
    public static ConnectionFactory getFactory() {
        return ConnectionFactory.factory;
    }
    
    public ExecutorService getExecutor() {
        return ConnectionFactory.exe;
    }
    
    public SocketChannel getChannel() throws IOException {
        if (this.minLimit == 0) {
            return this.getNewChannel();
        }
        synchronized (this.queue) {
            if (this.queue.isEmpty()) {
                this.addNewChannels(this.minLimit);
            }
        }
        try {
            return this.queue.remove();
        }
        catch (final NoSuchElementException nsex) {
            return this.getNewChannel();
        }
    }
    
    public void returnConnectionToPool(final SocketChannel c) {
        try {
            if (c != null) {
                if (c.socket().isInputShutdown() || c.socket().isOutputShutdown()) {
                    c.close();
                    return;
                }
                if (this.queue.contains(c)) {
                    return;
                }
                if (this.queue.size() >= this.minLimit || this.minLimit == 0) {
                    c.write(ByteBuffer.wrap(ConnectionFactory.END_AV_SESSION));
                    c.close();
                    return;
                }
                this.queue.add(c);
            }
        }
        catch (final IOException ioex) {
            ConnectionFactory.LOGGER.log(Level.WARNING, "Exception occurred while closing channel.", ioex);
        }
    }
    
    private void addNewChannels(final int limit) throws IOException {
        for (int i = 0; i < limit; ++i) {
            final SocketChannel channel = this.getNewChannel();
            if (channel != null) {
                this.queue.add(channel);
            }
        }
    }
    
    private SocketChannel getNewChannel() throws IOException {
        final InetSocketAddress avServer = new InetSocketAddress(this.clamAVHost, this.clamAVPort);
        final SocketChannel channel = SocketChannel.open(avServer);
        channel.write(ByteBuffer.wrap(ConnectionFactory.START_AV_SESSION));
        return channel;
    }
    
    static {
        LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
        START_AV_SESSION = "zIDSESSION\u0000".getBytes();
        END_AV_SESSION = "zEND\u0000".getBytes();
    }
}
