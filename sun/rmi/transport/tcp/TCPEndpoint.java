package sun.rmi.transport.tcp;

import sun.rmi.runtime.NewThreadAction;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.ConnectIOException;
import java.rmi.ConnectException;
import java.rmi.UnknownHostException;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;
import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Proxy;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import sun.rmi.transport.Channel;
import java.rmi.RemoteException;
import sun.rmi.transport.Target;
import java.util.HashSet;
import java.util.Collection;
import sun.rmi.transport.Transport;
import java.util.Iterator;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;
import sun.security.action.GetBooleanAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.LinkedList;
import java.util.Map;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import sun.rmi.transport.Endpoint;

public class TCPEndpoint implements Endpoint
{
    private String host;
    private int port;
    private final RMIClientSocketFactory csf;
    private final RMIServerSocketFactory ssf;
    private int listenPort;
    private TCPTransport transport;
    private static String localHost;
    private static boolean localHostKnown;
    private static final Map<TCPEndpoint, LinkedList<TCPEndpoint>> localEndpoints;
    private static final int FORMAT_HOST_PORT = 0;
    private static final int FORMAT_HOST_PORT_FACTORY = 1;
    
    private static int getInt(final String s, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction(s, n));
    }
    
    private static boolean getBoolean(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction(s));
    }
    
    private static String getHostnameProperty() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.hostname"));
    }
    
    public TCPEndpoint(final String s, final int n) {
        this(s, n, null, null);
    }
    
    public TCPEndpoint(String host, final int port, final RMIClientSocketFactory csf, final RMIServerSocketFactory ssf) {
        this.listenPort = -1;
        this.transport = null;
        if (host == null) {
            host = "";
        }
        this.host = host;
        this.port = port;
        this.csf = csf;
        this.ssf = ssf;
    }
    
    public static TCPEndpoint getLocalEndpoint(final int n) {
        return getLocalEndpoint(n, null, null);
    }
    
    public static TCPEndpoint getLocalEndpoint(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        TCPEndpoint tcpEndpoint = null;
        synchronized (TCPEndpoint.localEndpoints) {
            final TCPEndpoint tcpEndpoint2 = new TCPEndpoint(null, n, rmiClientSocketFactory, rmiServerSocketFactory);
            final LinkedList list = TCPEndpoint.localEndpoints.get(tcpEndpoint2);
            final String resampleLocalHost = resampleLocalHost();
            if (list == null) {
                tcpEndpoint = new TCPEndpoint(resampleLocalHost, n, rmiClientSocketFactory, rmiServerSocketFactory);
                final LinkedList list2 = new LinkedList();
                list2.add(tcpEndpoint);
                tcpEndpoint.listenPort = n;
                tcpEndpoint.transport = new TCPTransport(list2);
                TCPEndpoint.localEndpoints.put(tcpEndpoint2, list2);
                if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                    TCPTransport.tcpLog.log(Log.BRIEF, "created local endpoint for socket factory " + rmiServerSocketFactory + " on port " + n);
                }
            }
            else {
                synchronized (list) {
                    tcpEndpoint = (TCPEndpoint)list.getLast();
                    final String host = tcpEndpoint.host;
                    final int port = tcpEndpoint.port;
                    final TCPTransport transport = tcpEndpoint.transport;
                    if (resampleLocalHost != null && !resampleLocalHost.equals(host)) {
                        if (port != 0) {
                            list.clear();
                        }
                        tcpEndpoint = new TCPEndpoint(resampleLocalHost, port, rmiClientSocketFactory, rmiServerSocketFactory);
                        tcpEndpoint.listenPort = n;
                        tcpEndpoint.transport = transport;
                        list.add(tcpEndpoint);
                    }
                }
            }
        }
        return tcpEndpoint;
    }
    
    private static String resampleLocalHost() {
        final String hostnameProperty = getHostnameProperty();
        synchronized (TCPEndpoint.localEndpoints) {
            if (hostnameProperty != null) {
                if (!TCPEndpoint.localHostKnown) {
                    setLocalHost(hostnameProperty);
                }
                else if (!hostnameProperty.equals(TCPEndpoint.localHost)) {
                    TCPEndpoint.localHost = hostnameProperty;
                    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                        TCPTransport.tcpLog.log(Log.BRIEF, "updated local hostname to: " + TCPEndpoint.localHost);
                    }
                }
            }
            return TCPEndpoint.localHost;
        }
    }
    
    static void setLocalHost(final String s) {
        synchronized (TCPEndpoint.localEndpoints) {
            if (!TCPEndpoint.localHostKnown) {
                TCPEndpoint.localHost = s;
                TCPEndpoint.localHostKnown = true;
                if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                    TCPTransport.tcpLog.log(Log.BRIEF, "local host set to " + s);
                }
                for (final LinkedList list : TCPEndpoint.localEndpoints.values()) {
                    synchronized (list) {
                        final Iterator iterator2 = list.iterator();
                        while (iterator2.hasNext()) {
                            ((TCPEndpoint)iterator2.next()).host = s;
                        }
                    }
                }
            }
        }
    }
    
    static void setDefaultPort(final int port, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        final TCPEndpoint tcpEndpoint = new TCPEndpoint(null, 0, rmiClientSocketFactory, rmiServerSocketFactory);
        synchronized (TCPEndpoint.localEndpoints) {
            final LinkedList list = TCPEndpoint.localEndpoints.get(tcpEndpoint);
            synchronized (list) {
                final int size = list.size();
                final TCPEndpoint tcpEndpoint2 = (TCPEndpoint)list.getLast();
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ((TCPEndpoint)iterator.next()).port = port;
                }
                if (size > 1) {
                    list.clear();
                    list.add(tcpEndpoint2);
                }
            }
            TCPEndpoint.localEndpoints.put(new TCPEndpoint(null, port, rmiClientSocketFactory, rmiServerSocketFactory), list);
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                TCPTransport.tcpLog.log(Log.BRIEF, "default port for server socket factory " + rmiServerSocketFactory + " and client socket factory " + rmiClientSocketFactory + " set to " + port);
            }
        }
    }
    
    @Override
    public Transport getOutboundTransport() {
        return getLocalEndpoint(0, null, null).transport;
    }
    
    private static Collection<TCPTransport> allKnownTransports() {
        final HashSet set;
        synchronized (TCPEndpoint.localEndpoints) {
            set = new HashSet(TCPEndpoint.localEndpoints.size());
            final Iterator<LinkedList<TCPEndpoint>> iterator = TCPEndpoint.localEndpoints.values().iterator();
            while (iterator.hasNext()) {
                set.add(iterator.next().getFirst().transport);
            }
        }
        return set;
    }
    
    public static void shedConnectionCaches() {
        final Iterator<TCPTransport> iterator = allKnownTransports().iterator();
        while (iterator.hasNext()) {
            iterator.next().shedConnectionCaches();
        }
    }
    
    @Override
    public void exportObject(final Target target) throws RemoteException {
        this.transport.exportObject(target);
    }
    
    @Override
    public Channel getChannel() {
        return this.getOutboundTransport().getChannel(this);
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getListenPort() {
        return this.listenPort;
    }
    
    @Override
    public Transport getInboundTransport() {
        return this.transport;
    }
    
    public RMIClientSocketFactory getClientSocketFactory() {
        return this.csf;
    }
    
    public RMIServerSocketFactory getServerSocketFactory() {
        return this.ssf;
    }
    
    @Override
    public String toString() {
        return "[" + this.host + ":" + this.port + ((this.ssf != null) ? ("," + this.ssf) : "") + ((this.csf != null) ? ("," + this.csf) : "") + "]";
    }
    
    @Override
    public int hashCode() {
        return this.port;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof TCPEndpoint) {
            final TCPEndpoint tcpEndpoint = (TCPEndpoint)o;
            return this.port == tcpEndpoint.port && this.host.equals(tcpEndpoint.host) && !(this.csf == null ^ tcpEndpoint.csf == null) && !(this.ssf == null ^ tcpEndpoint.ssf == null) && (this.csf == null || (this.csf.getClass() == tcpEndpoint.csf.getClass() && this.csf.equals(tcpEndpoint.csf))) && (this.ssf == null || (this.ssf.getClass() == tcpEndpoint.ssf.getClass() && this.ssf.equals(tcpEndpoint.ssf)));
        }
        return false;
    }
    
    public void write(final ObjectOutput objectOutput) throws IOException {
        if (this.csf == null) {
            objectOutput.writeByte(0);
            objectOutput.writeUTF(this.host);
            objectOutput.writeInt(this.port);
        }
        else {
            objectOutput.writeByte(1);
            objectOutput.writeUTF(this.host);
            objectOutput.writeInt(this.port);
            objectOutput.writeObject(this.csf);
        }
    }
    
    public static TCPEndpoint read(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        RMIClientSocketFactory rmiClientSocketFactory = null;
        String s = null;
        int n = 0;
        switch (objectInput.readByte()) {
            case 0: {
                s = objectInput.readUTF();
                n = objectInput.readInt();
                break;
            }
            case 1: {
                s = objectInput.readUTF();
                n = objectInput.readInt();
                rmiClientSocketFactory = (RMIClientSocketFactory)objectInput.readObject();
                if (rmiClientSocketFactory != null && Proxy.isProxyClass(rmiClientSocketFactory.getClass())) {
                    throw new IOException("Invalid SocketFactory");
                }
                break;
            }
            default: {
                throw new IOException("invalid endpoint format");
            }
        }
        return new TCPEndpoint(s, n, rmiClientSocketFactory, null);
    }
    
    public void writeHostPortFormat(final DataOutput dataOutput) throws IOException {
        if (this.csf != null) {
            throw new InternalError("TCPEndpoint.writeHostPortFormat: called for endpoint with non-null socket factory");
        }
        dataOutput.writeUTF(this.host);
        dataOutput.writeInt(this.port);
    }
    
    public static TCPEndpoint readHostPortFormat(final DataInput dataInput) throws IOException {
        return new TCPEndpoint(dataInput.readUTF(), dataInput.readInt());
    }
    
    private static RMISocketFactory chooseFactory() {
        RMISocketFactory rmiSocketFactory = RMISocketFactory.getSocketFactory();
        if (rmiSocketFactory == null) {
            rmiSocketFactory = TCPTransport.defaultSocketFactory;
        }
        return rmiSocketFactory;
    }
    
    Socket newSocket() throws RemoteException {
        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "opening socket to " + this);
        }
        Socket socket;
        try {
            RMIClientSocketFactory rmiClientSocketFactory = this.csf;
            if (rmiClientSocketFactory == null) {
                rmiClientSocketFactory = chooseFactory();
            }
            socket = rmiClientSocketFactory.createSocket(this.host, this.port);
        }
        catch (final java.net.UnknownHostException ex) {
            throw new UnknownHostException("Unknown host: " + this.host, ex);
        }
        catch (final java.net.ConnectException ex2) {
            throw new ConnectException("Connection refused to host: " + this.host, ex2);
        }
        catch (final IOException ex3) {
            try {
                shedConnectionCaches();
            }
            catch (final OutOfMemoryError | Exception ex4) {}
            throw new ConnectIOException("Exception creating connection to: " + this.host, ex3);
        }
        try {
            socket.setTcpNoDelay(true);
        }
        catch (final Exception ex5) {}
        try {
            socket.setKeepAlive(true);
        }
        catch (final Exception ex6) {}
        return socket;
    }
    
    ServerSocket newServerSocket() throws IOException {
        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "creating server socket on " + this);
        }
        RMIServerSocketFactory rmiServerSocketFactory = this.ssf;
        if (rmiServerSocketFactory == null) {
            rmiServerSocketFactory = chooseFactory();
        }
        final ServerSocket serverSocket = rmiServerSocketFactory.createServerSocket(this.listenPort);
        if (this.listenPort == 0) {
            setDefaultPort(serverSocket.getLocalPort(), this.csf, this.ssf);
        }
        return serverSocket;
    }
    
    static {
        TCPEndpoint.localHostKnown = true;
        TCPEndpoint.localHost = getHostnameProperty();
        if (TCPEndpoint.localHost == null) {
            try {
                final InetAddress localHost = InetAddress.getLocalHost();
                final byte[] address = localHost.getAddress();
                if (address[0] == 127 && address[1] == 0 && address[2] == 0 && address[3] == 1) {
                    TCPEndpoint.localHostKnown = false;
                }
                if (getBoolean("java.rmi.server.useLocalHostName")) {
                    TCPEndpoint.localHost = FQDN.attemptFQDN(localHost);
                }
                else {
                    TCPEndpoint.localHost = localHost.getHostAddress();
                }
            }
            catch (final Exception ex) {
                TCPEndpoint.localHostKnown = false;
                TCPEndpoint.localHost = null;
            }
        }
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, "localHostKnown = " + TCPEndpoint.localHostKnown + ", localHost = " + TCPEndpoint.localHost);
        }
        localEndpoints = new HashMap<TCPEndpoint, LinkedList<TCPEndpoint>>();
    }
    
    private static class FQDN implements Runnable
    {
        private String reverseLookup;
        private String hostAddress;
        
        private FQDN(final String hostAddress) {
            this.hostAddress = hostAddress;
        }
        
        static String attemptFQDN(final InetAddress inetAddress) throws java.net.UnknownHostException {
            String s = inetAddress.getHostName();
            if (s.indexOf(46) < 0) {
                final String hostAddress = inetAddress.getHostAddress();
                final FQDN fqdn = new FQDN(hostAddress);
                final int access$000 = getInt("sun.rmi.transport.tcp.localHostNameTimeOut", 10000);
                try {
                    synchronized (fqdn) {
                        fqdn.getFQDN();
                        fqdn.wait(access$000);
                    }
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                s = fqdn.getHost();
                if (s == null || s.equals("") || s.indexOf(46) < 0) {
                    s = hostAddress;
                }
            }
            return s;
        }
        
        private void getFQDN() {
            AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(this, "FQDN Finder", true)).start();
        }
        
        private synchronized String getHost() {
            return this.reverseLookup;
        }
        
        @Override
        public void run() {
            String hostName = null;
            try {
                hostName = InetAddress.getByName(this.hostAddress).getHostName();
            }
            catch (final java.net.UnknownHostException ex) {
                synchronized (this) {
                    this.reverseLookup = hostName;
                    this.notify();
                }
            }
            finally {
                synchronized (this) {
                    this.reverseLookup = hostName;
                    this.notify();
                }
            }
        }
    }
}
