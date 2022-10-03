package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.SelectionKey;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.net.InetAddress;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

final class SocketFinder
{
    private static final ThreadPoolExecutor threadPoolExecutor;
    private static final int minTimeoutForParallelConnections = 1500;
    private final Object socketFinderlock;
    private final Object parentThreadLock;
    private volatile Result result;
    private int noOfSpawnedThreads;
    private int noOfThreadsThatNotified;
    private volatile Socket selectedSocket;
    private volatile IOException selectedException;
    private static final Logger logger;
    private final String traceID;
    private static final int ipAddressLimit = 64;
    private final SQLServerConnection conn;
    
    SocketFinder(final String callerTraceID, final SQLServerConnection sqlServerConnection) {
        this.socketFinderlock = new Object();
        this.parentThreadLock = new Object();
        this.result = Result.UNKNOWN;
        this.noOfSpawnedThreads = 0;
        this.noOfThreadsThatNotified = 0;
        this.selectedSocket = null;
        this.selectedException = null;
        this.traceID = "SocketFinder(" + callerTraceID + ")";
        this.conn = sqlServerConnection;
    }
    
    Socket findSocket(final String hostName, final int portNumber, int timeoutInMilliSeconds, final boolean useParallel, boolean useTnir, final boolean isTnirFirstAttempt, final int timeoutInMilliSecondsForFullTimeout) throws SQLServerException {
        assert timeoutInMilliSeconds != 0 : "The driver does not allow a time out of 0";
        try {
            InetAddress[] inetAddrs = null;
            if (useParallel || useTnir) {
                inetAddrs = InetAddress.getAllByName(hostName);
                if (useTnir && inetAddrs.length > 64) {
                    useTnir = false;
                    timeoutInMilliSeconds = timeoutInMilliSecondsForFullTimeout;
                }
            }
            if (!useParallel) {
                if (useTnir && isTnirFirstAttempt) {
                    return this.getDefaultSocket(hostName, portNumber, 500);
                }
                if (!useTnir) {
                    return this.getDefaultSocket(hostName, portNumber, timeoutInMilliSeconds);
                }
            }
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                final StringBuilder loggingString = new StringBuilder(this.toString());
                loggingString.append(" Total no of InetAddresses: ");
                loggingString.append(inetAddrs.length);
                loggingString.append(". They are: ");
                for (final InetAddress inetAddr : inetAddrs) {
                    loggingString.append(inetAddr.toString()).append(";");
                }
                SocketFinder.logger.finer(loggingString.toString());
            }
            if (inetAddrs.length > 64) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ipAddressLimitWithMultiSubnetFailover"));
                final Object[] msgArgs = { Integer.toString(64) };
                final String errorStr = form.format(msgArgs);
                this.conn.terminate(6, errorStr);
            }
            if (inetAddrs.length == 1) {
                return this.getConnectedSocket(inetAddrs[0], portNumber, timeoutInMilliSeconds);
            }
            timeoutInMilliSeconds = Math.max(timeoutInMilliSeconds, 1500);
            if (Util.isIBM()) {
                if (SocketFinder.logger.isLoggable(Level.FINER)) {
                    SocketFinder.logger.finer(this.toString() + "Using Java NIO with timeout:" + timeoutInMilliSeconds);
                }
                this.findSocketUsingJavaNIO(inetAddrs, portNumber, timeoutInMilliSeconds);
            }
            else {
                if (SocketFinder.logger.isLoggable(Level.FINER)) {
                    SocketFinder.logger.finer(this.toString() + "Using Threading with timeout:" + timeoutInMilliSeconds);
                }
                this.findSocketUsingThreading(inetAddrs, portNumber, timeoutInMilliSeconds);
            }
            if (this.result.equals(Result.UNKNOWN)) {
                synchronized (this.socketFinderlock) {
                    if (this.result.equals(Result.UNKNOWN)) {
                        this.result = Result.FAILURE;
                        if (SocketFinder.logger.isLoggable(Level.FINER)) {
                            SocketFinder.logger.finer(this.toString() + " The parent thread updated the result to failure");
                        }
                    }
                }
            }
            if (this.result.equals(Result.FAILURE)) {
                if (this.selectedException == null) {
                    if (SocketFinder.logger.isLoggable(Level.FINER)) {
                        SocketFinder.logger.finer(this.toString() + " There is no selectedException. The wait calls timed out before any connect call returned or timed out.");
                    }
                    final String message = SQLServerException.getErrString("R_connectionTimedOut");
                    this.selectedException = new IOException(message);
                }
                throw this.selectedException;
            }
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            this.close(this.selectedSocket);
            SQLServerException.ConvertConnectExceptionToSQLServerException(hostName, portNumber, this.conn, ex);
        }
        catch (final IOException ex2) {
            this.close(this.selectedSocket);
            SQLServerException.ConvertConnectExceptionToSQLServerException(hostName, portNumber, this.conn, ex2);
        }
        assert this.result.equals(Result.SUCCESS);
        assert this.selectedSocket != null : "Bug in code. Selected Socket cannot be null here.";
        return this.selectedSocket;
    }
    
    private void findSocketUsingJavaNIO(final InetAddress[] inetAddrs, final int portNumber, final int timeoutInMilliSeconds) throws IOException {
        assert timeoutInMilliSeconds != 0 : "The timeout cannot be zero";
        assert inetAddrs.length != 0 : "Number of inetAddresses should not be zero in this function";
        Selector selector = null;
        final LinkedList<SocketChannel> socketChannels = new LinkedList<SocketChannel>();
        SocketChannel selectedChannel = null;
        try {
            selector = Selector.open();
            for (final InetAddress inetAddr : inetAddrs) {
                final SocketChannel sChannel = SocketChannel.open();
                socketChannels.add(sChannel);
                sChannel.configureBlocking(false);
                final int ops = 8;
                final SelectionKey key = sChannel.register(selector, ops);
                sChannel.connect(new InetSocketAddress(inetAddr, portNumber));
                if (SocketFinder.logger.isLoggable(Level.FINER)) {
                    SocketFinder.logger.finer(this.toString() + " initiated connection to address: " + inetAddr + ", portNumber: " + portNumber);
                }
            }
            long timerNow = System.currentTimeMillis();
            final long timerExpire = timerNow + timeoutInMilliSeconds;
            int noOfOutstandingChannels = inetAddrs.length;
            while (true) {
                final long timeRemaining = timerExpire - timerNow;
                if (timeRemaining <= 0L || selectedChannel != null || noOfOutstandingChannels <= 0) {
                    break;
                }
                final int readyChannels = selector.select(timeRemaining);
                if (SocketFinder.logger.isLoggable(Level.FINER)) {
                    SocketFinder.logger.finer(this.toString() + " no of channels ready: " + readyChannels);
                }
                if (readyChannels != 0) {
                    final Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        final SelectionKey key2 = keyIterator.next();
                        final SocketChannel ch = (SocketChannel)key2.channel();
                        if (SocketFinder.logger.isLoggable(Level.FINER)) {
                            SocketFinder.logger.finer(this.toString() + " processing the channel :" + ch);
                        }
                        boolean connected = false;
                        try {
                            connected = ch.finishConnect();
                            assert connected : "finishConnect on channel:" + ch + " cannot be false";
                            selectedChannel = ch;
                            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                                SocketFinder.logger.finer(this.toString() + " selected the channel :" + selectedChannel);
                            }
                        }
                        catch (final IOException ex) {
                            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                                SocketFinder.logger.finer(this.toString() + " the exception: " + ex.getClass() + " with message: " + ex.getMessage() + " occurred while processing the channel: " + ch);
                            }
                            this.updateSelectedException(ex, this.toString());
                            ch.close();
                            key2.cancel();
                            keyIterator.remove();
                            --noOfOutstandingChannels;
                            continue;
                        }
                        break;
                    }
                }
                timerNow = System.currentTimeMillis();
            }
        }
        catch (final IOException ex2) {
            this.close(selectedChannel);
            throw ex2;
        }
        finally {
            this.close(selector);
            for (final SocketChannel s : socketChannels) {
                if (s != selectedChannel) {
                    this.close(s);
                }
            }
        }
        if (selectedChannel != null) {
            selectedChannel.configureBlocking(true);
            this.selectedSocket = selectedChannel.socket();
            this.result = Result.SUCCESS;
        }
    }
    
    private Socket getDefaultSocket(final String hostName, final int portNumber, final int timeoutInMilliSeconds) throws IOException {
        final InetSocketAddress addr = new InetSocketAddress(hostName, portNumber);
        return this.getConnectedSocket(addr, timeoutInMilliSeconds);
    }
    
    private Socket getConnectedSocket(final InetAddress inetAddr, final int portNumber, final int timeoutInMilliSeconds) throws IOException {
        final InetSocketAddress addr = new InetSocketAddress(inetAddr, portNumber);
        return this.getConnectedSocket(addr, timeoutInMilliSeconds);
    }
    
    private Socket getConnectedSocket(final InetSocketAddress addr, final int timeoutInMilliSeconds) throws IOException {
        assert timeoutInMilliSeconds != 0 : "timeout cannot be zero";
        if (addr.isUnresolved()) {
            throw new UnknownHostException();
        }
        (this.selectedSocket = new Socket()).connect(addr, timeoutInMilliSeconds);
        return this.selectedSocket;
    }
    
    private void findSocketUsingThreading(final InetAddress[] inetAddrs, final int portNumber, final int timeoutInMilliSeconds) throws IOException, InterruptedException {
        assert timeoutInMilliSeconds != 0 : "The timeout cannot be zero";
        assert inetAddrs.length != 0 : "Number of inetAddresses should not be zero in this function";
        final LinkedList<Socket> sockets = new LinkedList<Socket>();
        final LinkedList<SocketConnector> socketConnectors = new LinkedList<SocketConnector>();
        try {
            this.noOfSpawnedThreads = inetAddrs.length;
            for (final InetAddress inetAddress : inetAddrs) {
                final Socket s = new Socket();
                sockets.add(s);
                final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, portNumber);
                final SocketConnector socketConnector = new SocketConnector(s, inetSocketAddress, timeoutInMilliSeconds, this);
                socketConnectors.add(socketConnector);
            }
            synchronized (this.parentThreadLock) {
                for (final SocketConnector sc : socketConnectors) {
                    SocketFinder.threadPoolExecutor.execute(sc);
                }
                long timerNow = System.currentTimeMillis();
                final long timerExpire = timerNow + timeoutInMilliSeconds;
                while (true) {
                    final long timeRemaining = timerExpire - timerNow;
                    if (SocketFinder.logger.isLoggable(Level.FINER)) {
                        SocketFinder.logger.finer(this.toString() + " TimeRemaining:" + timeRemaining + "; Result:" + this.result + "; Max. open thread count: " + SocketFinder.threadPoolExecutor.getLargestPoolSize() + "; Current open thread count:" + SocketFinder.threadPoolExecutor.getActiveCount());
                    }
                    if (timeRemaining <= 0L || !this.result.equals(Result.UNKNOWN)) {
                        break;
                    }
                    this.parentThreadLock.wait(timeRemaining);
                    if (SocketFinder.logger.isLoggable(Level.FINER)) {
                        SocketFinder.logger.finer(this.toString() + " The parent thread wokeup.");
                    }
                    timerNow = System.currentTimeMillis();
                }
            }
        }
        finally {
            for (final Socket s2 : sockets) {
                if (s2 != this.selectedSocket) {
                    this.close(s2);
                }
            }
        }
        if (this.selectedSocket != null) {
            this.result = Result.SUCCESS;
        }
    }
    
    Result getResult() {
        return this.result;
    }
    
    void close(final Selector selector) {
        if (null != selector) {
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                SocketFinder.logger.finer(this.toString() + ": Closing Selector");
            }
            try {
                selector.close();
            }
            catch (final IOException e) {
                if (SocketFinder.logger.isLoggable(Level.FINE)) {
                    SocketFinder.logger.log(Level.FINE, this.toString() + ": Ignored the following error while closing Selector", e);
                }
            }
        }
    }
    
    void close(final Socket socket) {
        if (null != socket) {
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                SocketFinder.logger.finer(this.toString() + ": Closing TCP socket:" + socket);
            }
            try {
                socket.close();
            }
            catch (final IOException e) {
                if (SocketFinder.logger.isLoggable(Level.FINE)) {
                    SocketFinder.logger.log(Level.FINE, this.toString() + ": Ignored the following error while closing socket", e);
                }
            }
        }
    }
    
    void close(final SocketChannel socketChannel) {
        if (null != socketChannel) {
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                SocketFinder.logger.finer(this.toString() + ": Closing TCP socket channel:" + socketChannel);
            }
            try {
                socketChannel.close();
            }
            catch (final IOException e) {
                if (SocketFinder.logger.isLoggable(Level.FINE)) {
                    SocketFinder.logger.log(Level.FINE, this.toString() + "Ignored the following error while closing socketChannel", e);
                }
            }
        }
    }
    
    void updateResult(final Socket socket, final IOException exception, final String threadId) {
        if (this.result.equals(Result.UNKNOWN)) {
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                SocketFinder.logger.finer("The following child thread is waiting for socketFinderLock:" + threadId);
            }
            synchronized (this.socketFinderlock) {
                if (SocketFinder.logger.isLoggable(Level.FINER)) {
                    SocketFinder.logger.finer("The following child thread acquired socketFinderLock:" + threadId);
                }
                if (this.result.equals(Result.UNKNOWN)) {
                    if (exception == null && this.selectedSocket == null) {
                        this.selectedSocket = socket;
                        this.result = Result.SUCCESS;
                        if (SocketFinder.logger.isLoggable(Level.FINER)) {
                            SocketFinder.logger.finer("The socket of the following thread has been chosen:" + threadId);
                        }
                    }
                    if (exception != null) {
                        this.updateSelectedException(exception, threadId);
                    }
                }
                ++this.noOfThreadsThatNotified;
                if (this.noOfThreadsThatNotified >= this.noOfSpawnedThreads && this.result.equals(Result.UNKNOWN)) {
                    this.result = Result.FAILURE;
                }
                if (!this.result.equals(Result.UNKNOWN)) {
                    if (SocketFinder.logger.isLoggable(Level.FINER)) {
                        SocketFinder.logger.finer("The following child thread is waiting for parentThreadLock:" + threadId);
                    }
                    synchronized (this.parentThreadLock) {
                        if (SocketFinder.logger.isLoggable(Level.FINER)) {
                            SocketFinder.logger.finer("The following child thread acquired parentThreadLock:" + threadId);
                        }
                        this.parentThreadLock.notifyAll();
                    }
                    if (SocketFinder.logger.isLoggable(Level.FINER)) {
                        SocketFinder.logger.finer("The following child thread released parentThreadLock and notified the parent thread:" + threadId);
                    }
                }
            }
            if (SocketFinder.logger.isLoggable(Level.FINER)) {
                SocketFinder.logger.finer("The following child thread released socketFinderLock:" + threadId);
            }
        }
    }
    
    public void updateSelectedException(final IOException ex, final String traceId) {
        boolean updatedException = false;
        if (this.selectedException == null || (!(ex instanceof SocketTimeoutException) && this.selectedException instanceof SocketTimeoutException)) {
            this.selectedException = ex;
            updatedException = true;
        }
        if (updatedException && SocketFinder.logger.isLoggable(Level.FINER)) {
            SocketFinder.logger.finer("The selected exception is updated to the following: ExceptionType:" + ex.getClass() + "; ExceptionMessage:" + ex.getMessage() + "; by the following thread:" + traceId);
        }
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    static {
        threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SocketFinder");
    }
    
    enum Result
    {
        UNKNOWN, 
        SUCCESS, 
        FAILURE;
    }
}
