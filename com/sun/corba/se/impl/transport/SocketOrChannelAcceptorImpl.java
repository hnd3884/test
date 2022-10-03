package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.nio.channels.SelectableChannel;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import java.util.Iterator;
import com.sun.corba.se.spi.extension.RequestPartitioningPolicy;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.pept.transport.Selector;
import java.net.Socket;
import java.io.IOException;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.Acceptor;
import java.net.InetSocketAddress;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import com.sun.corba.se.spi.transport.CorbaAcceptor;

public class SocketOrChannelAcceptorImpl extends EventHandlerBase implements CorbaAcceptor, SocketOrChannelAcceptor, Work, SocketInfo, LegacyServerSocketEndPointInfo
{
    protected ServerSocketChannel serverSocketChannel;
    protected ServerSocket serverSocket;
    protected int port;
    protected long enqueueTime;
    protected boolean initialized;
    protected ORBUtilSystemException wrapper;
    protected InboundConnectionCache connectionCache;
    protected String type;
    protected String name;
    protected String hostname;
    protected int locatorPort;
    
    public SocketOrChannelAcceptorImpl(final ORB orb) {
        this.type = "";
        this.name = "";
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
        this.setWork(this);
        this.initialized = false;
        this.hostname = orb.getORBData().getORBServerHost();
        this.name = "NO_NAME";
        this.locatorPort = -1;
    }
    
    public SocketOrChannelAcceptorImpl(final ORB orb, final int port) {
        this(orb);
        this.port = port;
    }
    
    public SocketOrChannelAcceptorImpl(final ORB orb, final int n, final String name, final String type) {
        this(orb, n);
        this.name = name;
        this.type = type;
    }
    
    @Override
    public boolean initialize() {
        if (this.initialized) {
            return false;
        }
        if (this.orb.transportDebugFlag) {
            this.dprint(".initialize: " + this);
        }
        try {
            InetSocketAddress inetSocketAddress;
            if (this.orb.getORBData().getListenOnAllInterfaces().equals("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces")) {
                inetSocketAddress = new InetSocketAddress(this.port);
            }
            else {
                inetSocketAddress = new InetSocketAddress(this.orb.getORBData().getORBServerHost(), this.port);
            }
            this.serverSocket = this.orb.getORBData().getSocketFactory().createServerSocket(this.type, inetSocketAddress);
            this.internalInitialize();
        }
        catch (final Throwable t) {
            throw this.wrapper.createListenerFailed(t, Integer.toString(this.port));
        }
        return this.initialized = true;
    }
    
    protected void internalInitialize() throws Exception {
        this.port = this.serverSocket.getLocalPort();
        this.orb.getCorbaTransportManager().getInboundConnectionCache(this);
        this.serverSocketChannel = this.serverSocket.getChannel();
        if (this.serverSocketChannel != null) {
            this.setUseSelectThreadToWait(this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
            this.serverSocketChannel.configureBlocking(!this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
        }
        else {
            this.setUseSelectThreadToWait(false);
        }
        this.setUseWorkerThreadForEvent(this.orb.getORBData().acceptorSocketUseWorkerThreadForEvent());
    }
    
    @Override
    public boolean initialized() {
        return this.initialized;
    }
    
    @Override
    public String getConnectionCacheType() {
        return this.getClass().toString();
    }
    
    @Override
    public void setConnectionCache(final InboundConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }
    
    @Override
    public InboundConnectionCache getConnectionCache() {
        return this.connectionCache;
    }
    
    @Override
    public boolean shouldRegisterAcceptEvent() {
        return true;
    }
    
    @Override
    public void accept() {
        try {
            Socket socket;
            if (this.serverSocketChannel == null) {
                socket = this.serverSocket.accept();
            }
            else {
                socket = this.serverSocketChannel.accept().socket();
            }
            this.orb.getORBData().getSocketFactory().setAcceptedSocketOptions(this, this.serverSocket, socket);
            if (this.orb.transportDebugFlag) {
                this.dprint(".accept: " + ((this.serverSocketChannel == null) ? this.serverSocket.toString() : this.serverSocketChannel.toString()));
            }
            final SocketOrChannelConnectionImpl socketOrChannelConnectionImpl = new SocketOrChannelConnectionImpl(this.orb, this, socket);
            if (this.orb.transportDebugFlag) {
                this.dprint(".accept: new: " + socketOrChannelConnectionImpl);
            }
            this.getConnectionCache().stampTime(socketOrChannelConnectionImpl);
            this.getConnectionCache().put(this, socketOrChannelConnectionImpl);
            if (socketOrChannelConnectionImpl.shouldRegisterServerReadEvent()) {
                final Selector selector = this.orb.getTransportManager().getSelector(0);
                if (selector != null) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".accept: registerForEvent: " + socketOrChannelConnectionImpl);
                    }
                    selector.registerForEvent(socketOrChannelConnectionImpl.getEventHandler());
                }
            }
            this.getConnectionCache().reclaim();
        }
        catch (final IOException ex) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".accept:", ex);
            }
            final Selector selector2 = this.orb.getTransportManager().getSelector(0);
            if (selector2 != null) {
                selector2.unregisterForEvent(this);
                selector2.registerForEvent(this);
            }
        }
    }
    
    @Override
    public void close() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close->:");
            }
            final Selector selector = this.orb.getTransportManager().getSelector(0);
            if (selector != null) {
                selector.unregisterForEvent(this);
            }
            if (this.serverSocketChannel != null) {
                this.serverSocketChannel.close();
            }
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        }
        catch (final IOException ex) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close:", ex);
            }
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close<-:");
            }
        }
    }
    
    @Override
    public EventHandler getEventHandler() {
        return this;
    }
    
    @Override
    public String getObjectAdapterId() {
        return null;
    }
    
    @Override
    public String getObjectAdapterManagerId() {
        return null;
    }
    
    @Override
    public void addToIORTemplate(final IORTemplate iorTemplate, final Policies policies, final String s) {
        final Iterator iteratorById = iorTemplate.iteratorById(0);
        final String orbServerHost = this.orb.getORBData().getORBServerHost();
        if (iteratorById.hasNext()) {
            final AlternateIIOPAddressComponent alternateIIOPAddressComponent = IIOPFactories.makeAlternateIIOPAddressComponent(IIOPFactories.makeIIOPAddress(this.orb, orbServerHost, this.port));
            while (iteratorById.hasNext()) {
                iteratorById.next().add(alternateIIOPAddressComponent);
            }
        }
        else {
            final GIOPVersion giopVersion = this.orb.getORBData().getGIOPVersion();
            int n;
            if (policies.forceZeroPort()) {
                n = 0;
            }
            else if (policies.isTransient()) {
                n = this.port;
            }
            else {
                n = this.orb.getLegacyServerSocketManager().legacyGetPersistentServerPort("IIOP_CLEAR_TEXT");
            }
            final IIOPProfileTemplate iiopProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, giopVersion, IIOPFactories.makeIIOPAddress(this.orb, orbServerHost, n));
            if (giopVersion.supportsIORIIOPProfileComponents()) {
                iiopProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
                iiopProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
                final RequestPartitioningPolicy requestPartitioningPolicy = (RequestPartitioningPolicy)policies.get_effective_policy(1398079491);
                if (requestPartitioningPolicy != null) {
                    iiopProfileTemplate.add(IIOPFactories.makeRequestPartitioningComponent(requestPartitioningPolicy.getValue()));
                }
                if (s != null && s != "") {
                    iiopProfileTemplate.add(IIOPFactories.makeJavaCodebaseComponent(s));
                }
                if (this.orb.getORBData().isJavaSerializationEnabled()) {
                    iiopProfileTemplate.add(IIOPFactories.makeJavaSerializationComponent());
                }
            }
            iorTemplate.add(iiopProfileTemplate);
        }
    }
    
    @Override
    public String getMonitoringName() {
        return "AcceptedConnections";
    }
    
    @Override
    public SelectableChannel getChannel() {
        return this.serverSocketChannel;
    }
    
    @Override
    public int getInterestOps() {
        return 16;
    }
    
    @Override
    public Acceptor getAcceptor() {
        return this;
    }
    
    @Override
    public Connection getConnection() {
        throw new RuntimeException("Should not happen.");
    }
    
    @Override
    public void doWork() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork->: " + this);
            }
            if (this.selectionKey.isAcceptable()) {
                this.accept();
            }
            else if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: ! selectionKey.isAcceptable: " + this);
            }
        }
        catch (final SecurityException ex) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: ignoring SecurityException: " + ex + " " + this);
            }
            this.wrapper.securityExceptionInAccept(ex, ORBUtility.getClassSecurityInfo(this.getClass()));
        }
        catch (final Exception ex2) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: ignoring Exception: " + ex2 + " " + this);
            }
            this.wrapper.exceptionInAccept(ex2);
        }
        catch (final Throwable t) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: ignoring Throwable: " + t + " " + this);
            }
        }
        finally {
            final Selector selector = this.orb.getTransportManager().getSelector(0);
            if (selector != null) {
                selector.registerInterestOps(this);
            }
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork<-:" + this);
            }
        }
    }
    
    @Override
    public void setEnqueueTime(final long enqueueTime) {
        this.enqueueTime = enqueueTime;
    }
    
    @Override
    public long getEnqueueTime() {
        return this.enqueueTime;
    }
    
    @Override
    public MessageMediator createMessageMediator(final Broker broker, final Connection connection) {
        return new SocketOrChannelContactInfoImpl().createMessageMediator(broker, connection);
    }
    
    @Override
    public MessageMediator finishCreatingMessageMediator(final Broker broker, final Connection connection, final MessageMediator messageMediator) {
        return new SocketOrChannelContactInfoImpl().finishCreatingMessageMediator(broker, connection, messageMediator);
    }
    
    @Override
    public InputObject createInputObject(final Broker broker, final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        return new CDRInputObject((ORB)broker, (CorbaConnection)messageMediator.getConnection(), corbaMessageMediator.getDispatchBuffer(), corbaMessageMediator.getDispatchHeader());
    }
    
    @Override
    public OutputObject createOutputObject(final Broker broker, final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        return OutputStreamFactory.newCDROutputObject((ORB)broker, corbaMessageMediator, corbaMessageMediator.getReplyHeader(), corbaMessageMediator.getStreamFormatVersion());
    }
    
    @Override
    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }
    
    @Override
    public String toString() {
        String s;
        if (this.serverSocketChannel == null) {
            if (this.serverSocket == null) {
                s = "(not initialized)";
            }
            else {
                s = this.serverSocket.toString();
            }
        }
        else {
            s = this.serverSocketChannel.toString();
        }
        return this.toStringName() + "[" + s + " " + this.type + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + "]";
    }
    
    protected String toStringName() {
        return "SocketOrChannelAcceptorImpl";
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint(this.toStringName(), s);
    }
    
    protected void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public String getHostName() {
        return this.hostname;
    }
    
    @Override
    public String getHost() {
        return this.hostname;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public int getLocatorPort() {
        return this.locatorPort;
    }
    
    @Override
    public void setLocatorPort(final int locatorPort) {
        this.locatorPort = locatorPort;
    }
    
    @Override
    public String getName() {
        return this.name.equals("NO_NAME") ? this.toString() : this.name;
    }
}
