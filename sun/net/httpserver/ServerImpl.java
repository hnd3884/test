package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import javax.net.ssl.SSLEngine;
import java.net.URISyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;
import java.net.URI;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import java.util.Iterator;
import java.net.BindException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.Collections;
import java.util.HashSet;
import java.net.SocketAddress;
import java.util.logging.Logger;
import java.util.Timer;
import com.sun.net.httpserver.HttpServer;
import java.util.List;
import java.util.Set;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import com.sun.net.httpserver.HttpsConfigurator;
import java.util.concurrent.Executor;

class ServerImpl implements TimeSource
{
    private String protocol;
    private boolean https;
    private Executor executor;
    private HttpsConfigurator httpsConfig;
    private SSLContext sslContext;
    private ContextList contexts;
    private InetSocketAddress address;
    private ServerSocketChannel schan;
    private Selector selector;
    private SelectionKey listenerKey;
    private Set<HttpConnection> idleConnections;
    private Set<HttpConnection> allConnections;
    private Set<HttpConnection> reqConnections;
    private Set<HttpConnection> rspConnections;
    private List<Event> events;
    private Object lolock;
    private volatile boolean finished;
    private volatile boolean terminating;
    private boolean bound;
    private boolean started;
    private volatile long time;
    private volatile long subticks;
    private volatile long ticks;
    private HttpServer wrapper;
    static final int CLOCK_TICK;
    static final long IDLE_INTERVAL;
    static final int MAX_IDLE_CONNECTIONS;
    static final long TIMER_MILLIS;
    static final long MAX_REQ_TIME;
    static final long MAX_RSP_TIME;
    static final boolean timer1Enabled;
    private Timer timer;
    private Timer timer1;
    private Logger logger;
    Dispatcher dispatcher;
    static boolean debug;
    private int exchangeCount;
    
    ServerImpl(final HttpServer wrapper, final String protocol, final InetSocketAddress address, final int n) throws IOException {
        this.lolock = new Object();
        this.finished = false;
        this.terminating = false;
        this.bound = false;
        this.started = false;
        this.subticks = 0L;
        this.exchangeCount = 0;
        this.protocol = protocol;
        this.wrapper = wrapper;
        ServerConfig.checkLegacyProperties(this.logger = Logger.getLogger("com.sun.net.httpserver"));
        this.https = protocol.equalsIgnoreCase("https");
        this.address = address;
        this.contexts = new ContextList();
        this.schan = ServerSocketChannel.open();
        if (address != null) {
            this.schan.socket().bind(address, n);
            this.bound = true;
        }
        this.selector = Selector.open();
        this.schan.configureBlocking(false);
        this.listenerKey = this.schan.register(this.selector, 16);
        this.dispatcher = new Dispatcher();
        this.idleConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.allConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.reqConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.rspConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.time = System.currentTimeMillis();
        (this.timer = new Timer("server-timer", true)).schedule(new ServerTimerTask(), ServerImpl.CLOCK_TICK, ServerImpl.CLOCK_TICK);
        if (ServerImpl.timer1Enabled) {
            (this.timer1 = new Timer("server-timer1", true)).schedule(new ServerTimerTask1(), ServerImpl.TIMER_MILLIS, ServerImpl.TIMER_MILLIS);
            this.logger.config("HttpServer timer1 enabled period in ms:  " + ServerImpl.TIMER_MILLIS);
            this.logger.config("MAX_REQ_TIME:  " + ServerImpl.MAX_REQ_TIME);
            this.logger.config("MAX_RSP_TIME:  " + ServerImpl.MAX_RSP_TIME);
        }
        this.events = new LinkedList<Event>();
        this.logger.config("HttpServer created " + protocol + " " + address);
    }
    
    public void bind(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        if (this.bound) {
            throw new BindException("HttpServer already bound");
        }
        if (inetSocketAddress == null) {
            throw new NullPointerException("null address");
        }
        this.schan.socket().bind(inetSocketAddress, n);
        this.bound = true;
    }
    
    public void start() {
        if (!this.bound || this.started || this.finished) {
            throw new IllegalStateException("server in wrong state");
        }
        if (this.executor == null) {
            this.executor = new DefaultExecutor();
        }
        final Thread thread = new Thread(this.dispatcher);
        this.started = true;
        thread.start();
    }
    
    public void setExecutor(final Executor executor) {
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        this.executor = executor;
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public void setHttpsConfigurator(final HttpsConfigurator httpsConfig) {
        if (httpsConfig == null) {
            throw new NullPointerException("null HttpsConfigurator");
        }
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        this.httpsConfig = httpsConfig;
        this.sslContext = httpsConfig.getSSLContext();
    }
    
    public HttpsConfigurator getHttpsConfigurator() {
        return this.httpsConfig;
    }
    
    public void stop(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("negative delay parameter");
        }
        this.terminating = true;
        try {
            this.schan.close();
        }
        catch (final IOException ex) {}
        this.selector.wakeup();
        while (System.currentTimeMillis() < System.currentTimeMillis() + n * 1000) {
            this.delay();
            if (this.finished) {
                break;
            }
        }
        this.finished = true;
        this.selector.wakeup();
        synchronized (this.allConnections) {
            final Iterator<HttpConnection> iterator = this.allConnections.iterator();
            while (iterator.hasNext()) {
                iterator.next().close();
            }
        }
        this.allConnections.clear();
        this.idleConnections.clear();
        this.timer.cancel();
        if (ServerImpl.timer1Enabled) {
            this.timer1.cancel();
        }
    }
    
    public synchronized HttpContextImpl createContext(final String s, final HttpHandler httpHandler) {
        if (httpHandler == null || s == null) {
            throw new NullPointerException("null handler, or path parameter");
        }
        final HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, s, httpHandler, this);
        this.contexts.add(httpContextImpl);
        this.logger.config("context created: " + s);
        return httpContextImpl;
    }
    
    public synchronized HttpContextImpl createContext(final String s) {
        if (s == null) {
            throw new NullPointerException("null path parameter");
        }
        final HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, s, null, this);
        this.contexts.add(httpContextImpl);
        this.logger.config("context created: " + s);
        return httpContextImpl;
    }
    
    public synchronized void removeContext(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new NullPointerException("null path parameter");
        }
        this.contexts.remove(this.protocol, s);
        this.logger.config("context removed: " + s);
    }
    
    public synchronized void removeContext(final HttpContext httpContext) throws IllegalArgumentException {
        if (!(httpContext instanceof HttpContextImpl)) {
            throw new IllegalArgumentException("wrong HttpContext type");
        }
        this.contexts.remove((HttpContextImpl)httpContext);
        this.logger.config("context removed: " + httpContext.getPath());
    }
    
    public InetSocketAddress getAddress() {
        return AccessController.doPrivileged((PrivilegedAction<InetSocketAddress>)new PrivilegedAction<InetSocketAddress>() {
            @Override
            public InetSocketAddress run() {
                return (InetSocketAddress)ServerImpl.this.schan.socket().getLocalSocketAddress();
            }
        });
    }
    
    Selector getSelector() {
        return this.selector;
    }
    
    void addEvent(final Event event) {
        synchronized (this.lolock) {
            this.events.add(event);
            this.selector.wakeup();
        }
    }
    
    static synchronized void dprint(final String s) {
        if (ServerImpl.debug) {
            System.out.println(s);
        }
    }
    
    static synchronized void dprint(final Exception ex) {
        if (ServerImpl.debug) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    Logger getLogger() {
        return this.logger;
    }
    
    private void closeConnection(final HttpConnection httpConnection) {
        httpConnection.close();
        this.allConnections.remove(httpConnection);
        switch (httpConnection.getState()) {
            case REQUEST: {
                this.reqConnections.remove(httpConnection);
                break;
            }
            case RESPONSE: {
                this.rspConnections.remove(httpConnection);
                break;
            }
            case IDLE: {
                this.idleConnections.remove(httpConnection);
                break;
            }
        }
        assert !this.reqConnections.remove(httpConnection);
        assert !this.rspConnections.remove(httpConnection);
        assert !this.idleConnections.remove(httpConnection);
    }
    
    void logReply(final int n, final String s, String s2) {
        if (!this.logger.isLoggable(Level.FINE)) {
            return;
        }
        if (s2 == null) {
            s2 = "";
        }
        String string;
        if (s.length() > 80) {
            string = s.substring(0, 80) + "<TRUNCATED>";
        }
        else {
            string = s;
        }
        this.logger.fine(string + " [" + n + " " + Code.msg(n) + "] (" + s2 + ")");
    }
    
    long getTicks() {
        return this.ticks;
    }
    
    @Override
    public long getTime() {
        return this.time;
    }
    
    void delay() {
        Thread.yield();
        try {
            Thread.sleep(200L);
        }
        catch (final InterruptedException ex) {}
    }
    
    synchronized void startExchange() {
        ++this.exchangeCount;
    }
    
    synchronized int endExchange() {
        --this.exchangeCount;
        assert this.exchangeCount >= 0;
        return this.exchangeCount;
    }
    
    HttpServer getWrapper() {
        return this.wrapper;
    }
    
    void requestStarted(final HttpConnection httpConnection) {
        httpConnection.creationTime = this.getTime();
        httpConnection.setState(HttpConnection.State.REQUEST);
        this.reqConnections.add(httpConnection);
    }
    
    void requestCompleted(final HttpConnection httpConnection) {
        assert httpConnection.getState() == HttpConnection.State.REQUEST;
        this.reqConnections.remove(httpConnection);
        httpConnection.rspStartedTime = this.getTime();
        this.rspConnections.add(httpConnection);
        httpConnection.setState(HttpConnection.State.RESPONSE);
    }
    
    void responseCompleted(final HttpConnection httpConnection) {
        assert httpConnection.getState() == HttpConnection.State.RESPONSE;
        this.rspConnections.remove(httpConnection);
        httpConnection.setState(HttpConnection.State.IDLE);
    }
    
    void logStackTrace(final String s) {
        this.logger.finest(s);
        final StringBuilder sb = new StringBuilder();
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; ++i) {
            sb.append(stackTrace[i].toString()).append("\n");
        }
        this.logger.finest(sb.toString());
    }
    
    static long getTimeMillis(final long n) {
        if (n == -1L) {
            return -1L;
        }
        return n * 1000L;
    }
    
    static {
        CLOCK_TICK = ServerConfig.getClockTick();
        IDLE_INTERVAL = ServerConfig.getIdleInterval();
        MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
        TIMER_MILLIS = ServerConfig.getTimerMillis();
        MAX_REQ_TIME = getTimeMillis(ServerConfig.getMaxReqTime());
        MAX_RSP_TIME = getTimeMillis(ServerConfig.getMaxRspTime());
        timer1Enabled = (ServerImpl.MAX_REQ_TIME != -1L || ServerImpl.MAX_RSP_TIME != -1L);
        ServerImpl.debug = ServerConfig.debugEnabled();
    }
    
    private static class DefaultExecutor implements Executor
    {
        @Override
        public void execute(final Runnable runnable) {
            runnable.run();
        }
    }
    
    class Dispatcher implements Runnable
    {
        final LinkedList<HttpConnection> connsToRegister;
        
        Dispatcher() {
            this.connsToRegister = new LinkedList<HttpConnection>();
        }
        
        private void handleEvent(final Event event) {
            final ExchangeImpl exchange = event.exchange;
            final HttpConnection connection = exchange.getConnection();
            try {
                if (event instanceof WriteFinishedEvent) {
                    final int endExchange = ServerImpl.this.endExchange();
                    if (ServerImpl.this.terminating && endExchange == 0) {
                        ServerImpl.this.finished = true;
                    }
                    ServerImpl.this.responseCompleted(connection);
                    final LeftOverInputStream originalInputStream = exchange.getOriginalInputStream();
                    if (!originalInputStream.isEOF()) {
                        exchange.close = true;
                    }
                    if (exchange.close || ServerImpl.this.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS) {
                        connection.close();
                        ServerImpl.this.allConnections.remove(connection);
                    }
                    else if (originalInputStream.isDataBuffered()) {
                        ServerImpl.this.requestStarted(connection);
                        this.handle(connection.getChannel(), connection);
                    }
                    else {
                        this.connsToRegister.add(connection);
                    }
                }
            }
            catch (final IOException ex) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", ex);
                connection.close();
            }
        }
        
        void reRegister(final HttpConnection httpConnection) {
            try {
                final SocketChannel channel = httpConnection.getChannel();
                channel.configureBlocking(false);
                final SelectionKey register = channel.register(ServerImpl.this.selector, 1);
                register.attach(httpConnection);
                httpConnection.selectionKey = register;
                httpConnection.time = ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL;
                ServerImpl.this.idleConnections.add(httpConnection);
            }
            catch (final IOException ex) {
                ServerImpl.dprint(ex);
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher(8)", ex);
                httpConnection.close();
            }
        }
        
        @Override
        public void run() {
            while (!ServerImpl.this.finished) {
                try {
                    List access$900 = null;
                    synchronized (ServerImpl.this.lolock) {
                        if (ServerImpl.this.events.size() > 0) {
                            access$900 = ServerImpl.this.events;
                            ServerImpl.this.events = (List<Event>)new LinkedList();
                        }
                    }
                    if (access$900 != null) {
                        final Iterator iterator = access$900.iterator();
                        while (iterator.hasNext()) {
                            this.handleEvent((Event)iterator.next());
                        }
                    }
                    final Iterator<Object> iterator2 = this.connsToRegister.iterator();
                    while (iterator2.hasNext()) {
                        this.reRegister(iterator2.next());
                    }
                    this.connsToRegister.clear();
                    ServerImpl.this.selector.select(1000L);
                    final Iterator<SelectionKey> iterator3 = ServerImpl.this.selector.selectedKeys().iterator();
                    while (iterator3.hasNext()) {
                        final SelectionKey selectionKey = iterator3.next();
                        iterator3.remove();
                        if (selectionKey.equals(ServerImpl.this.listenerKey)) {
                            if (ServerImpl.this.terminating) {
                                continue;
                            }
                            final SocketChannel accept = ServerImpl.this.schan.accept();
                            if (ServerConfig.noDelay()) {
                                accept.socket().setTcpNoDelay(true);
                            }
                            if (accept == null) {
                                continue;
                            }
                            accept.configureBlocking(false);
                            final SelectionKey register = accept.register(ServerImpl.this.selector, 1);
                            final HttpConnection httpConnection = new HttpConnection();
                            httpConnection.selectionKey = register;
                            httpConnection.setChannel(accept);
                            register.attach(httpConnection);
                            ServerImpl.this.requestStarted(httpConnection);
                            ServerImpl.this.allConnections.add(httpConnection);
                        }
                        else {
                            try {
                                if (selectionKey.isReadable()) {
                                    final SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                                    final HttpConnection httpConnection2 = (HttpConnection)selectionKey.attachment();
                                    selectionKey.cancel();
                                    socketChannel.configureBlocking(true);
                                    if (ServerImpl.this.idleConnections.remove(httpConnection2)) {
                                        ServerImpl.this.requestStarted(httpConnection2);
                                    }
                                    this.handle(socketChannel, httpConnection2);
                                }
                                else {
                                    assert false;
                                    continue;
                                }
                            }
                            catch (final CancelledKeyException ex) {
                                this.handleException(selectionKey, null);
                            }
                            catch (final IOException ex2) {
                                this.handleException(selectionKey, ex2);
                            }
                        }
                    }
                    ServerImpl.this.selector.selectNow();
                }
                catch (final IOException ex3) {
                    ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", ex3);
                }
                catch (final Exception ex4) {
                    ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", ex4);
                }
            }
            try {
                ServerImpl.this.selector.close();
            }
            catch (final Exception ex5) {}
        }
        
        private void handleException(final SelectionKey selectionKey, final Exception ex) {
            final HttpConnection httpConnection = (HttpConnection)selectionKey.attachment();
            if (ex != null) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", ex);
            }
            ServerImpl.this.closeConnection(httpConnection);
        }
        
        public void handle(final SocketChannel socketChannel, final HttpConnection httpConnection) throws IOException {
            try {
                ServerImpl.this.executor.execute(new Exchange(socketChannel, ServerImpl.this.protocol, httpConnection));
            }
            catch (final HttpError httpError) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", httpError);
                ServerImpl.this.closeConnection(httpConnection);
            }
            catch (final IOException ex) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", ex);
                ServerImpl.this.closeConnection(httpConnection);
            }
        }
    }
    
    class Exchange implements Runnable
    {
        SocketChannel chan;
        HttpConnection connection;
        HttpContextImpl context;
        InputStream rawin;
        OutputStream rawout;
        String protocol;
        ExchangeImpl tx;
        HttpContextImpl ctx;
        boolean rejected;
        
        Exchange(final SocketChannel chan, final String protocol, final HttpConnection connection) throws IOException {
            this.rejected = false;
            this.chan = chan;
            this.connection = connection;
            this.protocol = protocol;
        }
        
        @Override
        public void run() {
            this.context = this.connection.getHttpContext();
            SSLEngine sslEngine = null;
            String requestLine = null;
            SSLStreams sslStreams = null;
            try {
                boolean b;
                if (this.context != null) {
                    this.rawin = this.connection.getInputStream();
                    this.rawout = this.connection.getRawOutputStream();
                    b = false;
                }
                else {
                    b = true;
                    if (ServerImpl.this.https) {
                        if (ServerImpl.this.sslContext == null) {
                            ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
                            throw new HttpError("No SSL context established");
                        }
                        sslStreams = new SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
                        this.rawin = sslStreams.getInputStream();
                        this.rawout = sslStreams.getOutputStream();
                        sslEngine = sslStreams.getSSLEngine();
                        this.connection.sslStreams = sslStreams;
                    }
                    else {
                        this.rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, this.chan));
                        this.rawout = new Request.WriteStream(ServerImpl.this, this.chan);
                    }
                    this.connection.raw = this.rawin;
                    this.connection.rawout = this.rawout;
                }
                final Request request = new Request(this.rawin, this.rawout);
                requestLine = request.requestLine();
                if (requestLine == null) {
                    ServerImpl.this.closeConnection(this.connection);
                    return;
                }
                final int index = requestLine.indexOf(32);
                if (index == -1) {
                    this.reject(400, requestLine, "Bad request line");
                    return;
                }
                final String substring = requestLine.substring(0, index);
                final int n = index + 1;
                final int index2 = requestLine.indexOf(32, n);
                if (index2 == -1) {
                    this.reject(400, requestLine, "Bad request line");
                    return;
                }
                final URI uri = new URI(requestLine.substring(n, index2));
                final String substring2 = requestLine.substring(index2 + 1);
                final Headers headers = request.headers();
                final String first = headers.getFirst("Transfer-encoding");
                long long1 = 0L;
                if (first != null && first.equalsIgnoreCase("chunked")) {
                    long1 = -1L;
                }
                else {
                    final String first2 = headers.getFirst("Content-Length");
                    if (first2 != null) {
                        long1 = Long.parseLong(first2);
                    }
                    if (long1 == 0L) {
                        ServerImpl.this.requestCompleted(this.connection);
                    }
                }
                this.ctx = ServerImpl.this.contexts.findContext(this.protocol, uri.getPath());
                if (this.ctx == null) {
                    this.reject(404, requestLine, "No context found for request");
                    return;
                }
                this.connection.setContext(this.ctx);
                if (this.ctx.getHandler() == null) {
                    this.reject(500, requestLine, "No handler for context");
                    return;
                }
                this.tx = new ExchangeImpl(substring, uri, request, long1, this.connection);
                final String first3 = headers.getFirst("Connection");
                final Headers responseHeaders = this.tx.getResponseHeaders();
                if (first3 != null && first3.equalsIgnoreCase("close")) {
                    this.tx.close = true;
                }
                if (substring2.equalsIgnoreCase("http/1.0")) {
                    this.tx.http10 = true;
                    if (first3 == null) {
                        this.tx.close = true;
                        responseHeaders.set("Connection", "close");
                    }
                    else if (first3.equalsIgnoreCase("keep-alive")) {
                        responseHeaders.set("Connection", "keep-alive");
                        responseHeaders.set("Keep-Alive", "timeout=" + (int)(ServerConfig.getIdleInterval() / 1000L) + ", max=" + ServerConfig.getMaxIdleConnections());
                    }
                }
                if (b) {
                    this.connection.setParameters(this.rawin, this.rawout, this.chan, sslEngine, sslStreams, ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin);
                }
                final String first4 = headers.getFirst("Expect");
                if (first4 != null && first4.equalsIgnoreCase("100-continue")) {
                    ServerImpl.this.logReply(100, requestLine, null);
                    this.sendReply(100, false, null);
                }
                final Filter.Chain chain = new Filter.Chain(this.ctx.getFilters(), new LinkHandler(new Filter.Chain(this.ctx.getSystemFilters(), this.ctx.getHandler())));
                this.tx.getRequestBody();
                this.tx.getResponseBody();
                if (ServerImpl.this.https) {
                    chain.doFilter(new HttpsExchangeImpl(this.tx));
                }
                else {
                    chain.doFilter(new HttpExchangeImpl(this.tx));
                }
            }
            catch (final IOException ex) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", ex);
                ServerImpl.this.closeConnection(this.connection);
            }
            catch (final NumberFormatException ex2) {
                this.reject(400, requestLine, "NumberFormatException thrown");
            }
            catch (final URISyntaxException ex3) {
                this.reject(400, requestLine, "URISyntaxException thrown");
            }
            catch (final Exception ex4) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", ex4);
                ServerImpl.this.closeConnection(this.connection);
            }
        }
        
        void reject(final int n, final String s, final String s2) {
            this.rejected = true;
            ServerImpl.this.logReply(n, s, s2);
            this.sendReply(n, false, "<h1>" + n + Code.msg(n) + "</h1>" + s2);
            ServerImpl.this.closeConnection(this.connection);
        }
        
        void sendReply(final int n, final boolean b, String s) {
            try {
                final StringBuilder sb = new StringBuilder(512);
                sb.append("HTTP/1.1 ").append(n).append(Code.msg(n)).append("\r\n");
                if (s != null && s.length() != 0) {
                    sb.append("Content-Length: ").append(s.length()).append("\r\n").append("Content-Type: text/html\r\n");
                }
                else {
                    sb.append("Content-Length: 0\r\n");
                    s = "";
                }
                if (b) {
                    sb.append("Connection: close\r\n");
                }
                sb.append("\r\n").append(s);
                this.rawout.write(sb.toString().getBytes("ISO8859_1"));
                this.rawout.flush();
                if (b) {
                    ServerImpl.this.closeConnection(this.connection);
                }
            }
            catch (final IOException ex) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", ex);
                ServerImpl.this.closeConnection(this.connection);
            }
        }
        
        class LinkHandler implements HttpHandler
        {
            Filter.Chain nextChain;
            
            LinkHandler(final Filter.Chain nextChain) {
                this.nextChain = nextChain;
            }
            
            @Override
            public void handle(final HttpExchange httpExchange) throws IOException {
                this.nextChain.doFilter(httpExchange);
            }
        }
    }
    
    class ServerTimerTask extends TimerTask
    {
        @Override
        public void run() {
            final LinkedList list = new LinkedList();
            ServerImpl.this.time = System.currentTimeMillis();
            ServerImpl.this.ticks++;
            synchronized (ServerImpl.this.idleConnections) {
                for (final HttpConnection httpConnection : ServerImpl.this.idleConnections) {
                    if (httpConnection.time <= ServerImpl.this.time) {
                        list.add(httpConnection);
                    }
                }
                for (final HttpConnection httpConnection2 : list) {
                    ServerImpl.this.idleConnections.remove(httpConnection2);
                    ServerImpl.this.allConnections.remove(httpConnection2);
                    httpConnection2.close();
                }
            }
        }
    }
    
    class ServerTimerTask1 extends TimerTask
    {
        @Override
        public void run() {
            final LinkedList list = new LinkedList();
            ServerImpl.this.time = System.currentTimeMillis();
            synchronized (ServerImpl.this.reqConnections) {
                if (ServerImpl.MAX_REQ_TIME != -1L) {
                    for (final HttpConnection httpConnection : ServerImpl.this.reqConnections) {
                        if (httpConnection.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time) {
                            list.add(httpConnection);
                        }
                    }
                    for (final HttpConnection httpConnection2 : list) {
                        ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + httpConnection2);
                        ServerImpl.this.reqConnections.remove(httpConnection2);
                        ServerImpl.this.allConnections.remove(httpConnection2);
                        httpConnection2.close();
                    }
                }
            }
            final LinkedList list2 = new LinkedList();
            synchronized (ServerImpl.this.rspConnections) {
                if (ServerImpl.MAX_RSP_TIME != -1L) {
                    for (final HttpConnection httpConnection3 : ServerImpl.this.rspConnections) {
                        if (httpConnection3.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time) {
                            list2.add(httpConnection3);
                        }
                    }
                    for (final HttpConnection httpConnection4 : list2) {
                        ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + httpConnection4);
                        ServerImpl.this.rspConnections.remove(httpConnection4);
                        ServerImpl.this.allConnections.remove(httpConnection4);
                        httpConnection4.close();
                    }
                }
            }
        }
    }
}
