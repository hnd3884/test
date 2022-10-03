package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.ServerNotActive;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.activation.Repository;
import java.util.HashMap;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.spi.activation._ServerManagerImplBase;

public class ServerManagerImpl extends _ServerManagerImplBase implements BadServerIdHandler
{
    HashMap serverTable;
    Repository repository;
    CorbaTransportManager transportManager;
    int initialPort;
    ORB orb;
    ActivationSystemException wrapper;
    String dbDirName;
    boolean debug;
    private int serverStartupDelay;
    
    ServerManagerImpl(final ORB orb, final CorbaTransportManager transportManager, final Repository repository, final String dbDirName, final boolean debug) {
        this.debug = false;
        this.orb = orb;
        this.wrapper = ActivationSystemException.get(orb, "orbd.activator");
        this.transportManager = transportManager;
        this.repository = repository;
        this.dbDirName = dbDirName;
        this.debug = debug;
        this.initialPort = ((SocketOrChannelAcceptor)orb.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING")).getServerSocket().getLocalPort();
        this.serverTable = new HashMap(256);
        this.serverStartupDelay = 1000;
        final String property = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
        if (property != null) {
            try {
                this.serverStartupDelay = Integer.parseInt(property);
            }
            catch (final Exception ex) {}
        }
        if (orb.getORBData().getBadServerIdHandler() == null) {
            orb.setBadServerIdHandler(this);
        }
        else {
            orb.initBadServerIdHandler();
        }
        orb.connect(this);
        ProcessMonitorThread.start(this.serverTable);
    }
    
    @Override
    public void activate(final int n) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
        final Integer n2 = new Integer(n);
        final ServerTableEntry serverTableEntry;
        synchronized (this.serverTable) {
            serverTableEntry = this.serverTable.get(n2);
        }
        if (serverTableEntry != null && serverTableEntry.isActive()) {
            if (this.debug) {
                System.out.println("ServerManagerImpl: activate for server Id " + n + " failed because server is already active. entry = " + serverTableEntry);
            }
            throw new ServerAlreadyActive(n);
        }
        try {
            final ServerTableEntry entry = this.getEntry(n);
            if (this.debug) {
                System.out.println("ServerManagerImpl: locateServer called with  serverId=" + n + " endpointType=" + "IIOP_CLEAR_TEXT" + " block=false");
            }
            final ServerLocation locateServer = this.locateServer(entry, "IIOP_CLEAR_TEXT", false);
            if (this.debug) {
                System.out.println("ServerManagerImpl: activate for server Id " + n + " found location " + locateServer.hostname + " and activated it");
            }
        }
        catch (final NoSuchEndPoint noSuchEndPoint) {
            if (this.debug) {
                System.out.println("ServerManagerImpl: activate for server Id  threw NoSuchEndpoint exception, which was ignored");
            }
        }
    }
    
    @Override
    public void active(final int n, final Server server) throws ServerNotRegistered {
        final Integer n2 = new Integer(n);
        synchronized (this.serverTable) {
            final ServerTableEntry serverTableEntry = this.serverTable.get(n2);
            if (serverTableEntry == null) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: active for server Id " + n + " called, but no such server is registered.");
                }
                throw this.wrapper.serverNotExpectedToRegister();
            }
            if (this.debug) {
                System.out.println("ServerManagerImpl: active for server Id " + n + " called.  This server is now active.");
            }
            serverTableEntry.register(server);
        }
    }
    
    @Override
    public void registerEndpoints(final int n, final String s, final EndPointInfo[] array) throws NoSuchEndPoint, ServerNotRegistered, ORBAlreadyRegistered {
        final Integer n2 = new Integer(n);
        synchronized (this.serverTable) {
            final ServerTableEntry serverTableEntry = this.serverTable.get(n2);
            if (serverTableEntry == null) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: registerEndpoint for server Id " + n + " called, but no such server is registered.");
                }
                throw this.wrapper.serverNotExpectedToRegister();
            }
            if (this.debug) {
                System.out.println("ServerManagerImpl: registerEndpoints for server Id " + n + " called.  This server is now active.");
            }
            serverTableEntry.registerPorts(s, array);
        }
    }
    
    @Override
    public int[] getActiveServers() {
        int[] array = null;
        synchronized (this.serverTable) {
            final ArrayList list = new ArrayList(0);
            final Iterator iterator = this.serverTable.keySet().iterator();
            try {
                while (iterator.hasNext()) {
                    final ServerTableEntry serverTableEntry = this.serverTable.get(iterator.next());
                    if (serverTableEntry.isValid() && serverTableEntry.isActive()) {
                        list.add(serverTableEntry);
                    }
                }
            }
            catch (final NoSuchElementException ex) {}
            array = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((ServerTableEntry)list.get(i)).getServerId();
            }
        }
        if (this.debug) {
            final StringBuffer sb = new StringBuffer();
            for (int j = 0; j < array.length; ++j) {
                sb.append(' ');
                sb.append(array[j]);
            }
            System.out.println("ServerManagerImpl: getActiveServers returns" + sb.toString());
        }
        return array;
    }
    
    @Override
    public void shutdown(final int n) throws ServerNotActive {
        final Integer n2 = new Integer(n);
        synchronized (this.serverTable) {
            final ServerTableEntry serverTableEntry = this.serverTable.remove(n2);
            if (serverTableEntry == null) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: shutdown for server Id " + n + " throws ServerNotActive.");
                }
                throw new ServerNotActive(n);
            }
            try {
                serverTableEntry.destroy();
                if (this.debug) {
                    System.out.println("ServerManagerImpl: shutdown for server Id " + n + " completed.");
                }
            }
            catch (final Exception ex) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: shutdown for server Id " + n + " threw exception " + ex);
                }
            }
        }
    }
    
    private ServerTableEntry getEntry(final int n) throws ServerNotRegistered {
        final Integer n2 = new Integer(n);
        ServerTableEntry serverTableEntry = null;
        synchronized (this.serverTable) {
            serverTableEntry = this.serverTable.get(n2);
            if (this.debug) {
                if (serverTableEntry == null) {
                    System.out.println("ServerManagerImpl: getEntry: no active server found.");
                }
                else {
                    System.out.println("ServerManagerImpl: getEntry:  active server found " + serverTableEntry + ".");
                }
            }
            if (serverTableEntry != null && !serverTableEntry.isValid()) {
                this.serverTable.remove(n2);
                serverTableEntry = null;
            }
            if (serverTableEntry == null) {
                serverTableEntry = new ServerTableEntry(this.wrapper, n, this.repository.getServer(n), this.initialPort, this.dbDirName, false, this.debug);
                this.serverTable.put(n2, serverTableEntry);
                serverTableEntry.activate();
            }
        }
        return serverTableEntry;
    }
    
    private ServerLocation locateServer(final ServerTableEntry serverTableEntry, final String s, final boolean b) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
        final ServerLocation serverLocation = new ServerLocation();
        if (b) {
            ORBPortInfo[] lookup;
            try {
                lookup = serverTableEntry.lookup(s);
            }
            catch (final Exception ex) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: locateServer: server held down");
                }
                throw new ServerHeldDown(serverTableEntry.getServerId());
            }
            serverLocation.hostname = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
            int length;
            if (lookup != null) {
                length = lookup.length;
            }
            else {
                length = 0;
            }
            serverLocation.ports = new ORBPortInfo[length];
            for (int i = 0; i < length; ++i) {
                serverLocation.ports[i] = new ORBPortInfo(lookup[i].orbId, lookup[i].port);
                if (this.debug) {
                    System.out.println("ServerManagerImpl: locateServer: server located at location " + serverLocation.hostname + " ORBid  " + lookup[i].orbId + " Port " + lookup[i].port);
                }
            }
        }
        return serverLocation;
    }
    
    private ServerLocationPerORB locateServerForORB(final ServerTableEntry serverTableEntry, final String s, final boolean b) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
        final ServerLocationPerORB serverLocationPerORB = new ServerLocationPerORB();
        if (b) {
            EndPointInfo[] lookupForORB;
            try {
                lookupForORB = serverTableEntry.lookupForORB(s);
            }
            catch (final InvalidORBid invalidORBid) {
                throw invalidORBid;
            }
            catch (final Exception ex) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: locateServerForORB: server held down");
                }
                throw new ServerHeldDown(serverTableEntry.getServerId());
            }
            serverLocationPerORB.hostname = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
            int length;
            if (lookupForORB != null) {
                length = lookupForORB.length;
            }
            else {
                length = 0;
            }
            serverLocationPerORB.ports = new EndPointInfo[length];
            for (int i = 0; i < length; ++i) {
                serverLocationPerORB.ports[i] = new EndPointInfo(lookupForORB[i].endpointType, lookupForORB[i].port);
                if (this.debug) {
                    System.out.println("ServerManagerImpl: locateServer: server located at location " + serverLocationPerORB.hostname + " endpointType  " + lookupForORB[i].endpointType + " Port " + lookupForORB[i].port);
                }
            }
        }
        return serverLocationPerORB;
    }
    
    @Override
    public String[] getORBNames(final int n) throws ServerNotRegistered {
        try {
            return this.getEntry(n).getORBList();
        }
        catch (final Exception ex) {
            throw new ServerNotRegistered(n);
        }
    }
    
    private ServerTableEntry getRunningEntry(final int n) throws ServerNotRegistered {
        final ServerTableEntry entry = this.getEntry(n);
        try {
            entry.lookup("IIOP_CLEAR_TEXT");
        }
        catch (final Exception ex) {
            return null;
        }
        return entry;
    }
    
    @Override
    public void install(final int n) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled {
        final ServerTableEntry runningEntry = this.getRunningEntry(n);
        if (runningEntry != null) {
            this.repository.install(n);
            runningEntry.install();
        }
    }
    
    @Override
    public void uninstall(final int n) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled {
        if (this.serverTable.get(new Integer(n)) != null) {
            final ServerTableEntry serverTableEntry = this.serverTable.remove(new Integer(n));
            if (serverTableEntry == null) {
                if (this.debug) {
                    System.out.println("ServerManagerImpl: shutdown for server Id " + n + " throws ServerNotActive.");
                }
                throw new ServerHeldDown(n);
            }
            serverTableEntry.uninstall();
        }
    }
    
    @Override
    public ServerLocation locateServer(final int n, final String s) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
        final ServerTableEntry entry = this.getEntry(n);
        if (this.debug) {
            System.out.println("ServerManagerImpl: locateServer called with  serverId=" + n + " endpointType=" + s + " block=true");
        }
        return this.locateServer(entry, s, true);
    }
    
    @Override
    public ServerLocationPerORB locateServerForORB(final int n, final String s) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
        final ServerTableEntry entry = this.getEntry(n);
        if (this.debug) {
            System.out.println("ServerManagerImpl: locateServerForORB called with  serverId=" + n + " orbId=" + s + " block=true");
        }
        return this.locateServerForORB(entry, s, true);
    }
    
    @Override
    public void handle(final ObjectKey objectKey) {
        final ObjectKeyTemplate template = objectKey.getTemplate();
        final int serverId = template.getServerId();
        final String orbId = template.getORBId();
        IOR ior;
        try {
            final ServerLocationPerORB locateServerForORB = this.locateServerForORB(this.getEntry(serverId), orbId, true);
            if (this.debug) {
                System.out.println("ServerManagerImpl: handle called for server id" + serverId + "  orbid  " + orbId);
            }
            int port = 0;
            final EndPointInfo[] ports = locateServerForORB.ports;
            for (int i = 0; i < ports.length; ++i) {
                if (ports[i].endpointType.equals("IIOP_CLEAR_TEXT")) {
                    port = ports[i].port;
                    break;
                }
            }
            final IIOPProfileTemplate iiopProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, GIOPVersion.V1_2, IIOPFactories.makeIIOPAddress(this.orb, locateServerForORB.hostname, port));
            if (GIOPVersion.V1_2.supportsIORIIOPProfileComponents()) {
                iiopProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
                iiopProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
            }
            final IORTemplate iorTemplate = IORFactories.makeIORTemplate(template);
            iorTemplate.add(iiopProfileTemplate);
            ior = iorTemplate.makeIOR(this.orb, "IDL:org/omg/CORBA/Object:1.0", objectKey.getId());
        }
        catch (final Exception ex) {
            throw this.wrapper.errorInBadServerIdHandler(ex);
        }
        if (this.debug) {
            System.out.println("ServerManagerImpl: handle throws ForwardException");
        }
        try {
            Thread.sleep(this.serverStartupDelay);
        }
        catch (final Exception ex2) {
            System.out.println("Exception = " + ex2);
            ex2.printStackTrace();
        }
        throw new ForwardException(this.orb, ior);
    }
    
    @Override
    public int getEndpoint(final String s) throws NoSuchEndPoint {
        return this.orb.getLegacyServerSocketManager().legacyGetTransientServerPort(s);
    }
    
    @Override
    public int getServerPortForType(final ServerLocationPerORB serverLocationPerORB, final String s) throws NoSuchEndPoint {
        final EndPointInfo[] ports = serverLocationPerORB.ports;
        for (int i = 0; i < ports.length; ++i) {
            if (ports[i].endpointType.equals(s)) {
                return ports[i].port;
            }
        }
        throw new NoSuchEndPoint();
    }
}
