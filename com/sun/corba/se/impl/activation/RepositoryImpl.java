package com.sun.corba.se.impl.activation;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.util.Enumeration;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import org.omg.CORBA.Object;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import com.sun.corba.se.spi.activation._RepositoryImplBase;

public class RepositoryImpl extends _RepositoryImplBase implements Serializable
{
    private static final long serialVersionUID = 8458417785209341858L;
    private transient boolean debug;
    static final int illegalServerId = -1;
    private transient RepositoryDB db;
    transient ORB orb;
    transient ActivationSystemException wrapper;
    
    RepositoryImpl(final ORB orb, final File file, final boolean debug) {
        this.debug = false;
        this.db = null;
        this.orb = null;
        this.debug = debug;
        this.orb = orb;
        this.wrapper = ActivationSystemException.get(orb, "orbd.repository");
        final File file2 = new File(file, "servers.db");
        if (!file2.exists()) {
            (this.db = new RepositoryDB(file2)).flush();
        }
        else {
            try {
                final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file2));
                this.db = (RepositoryDB)objectInputStream.readObject();
                objectInputStream.close();
            }
            catch (final Exception ex) {
                throw this.wrapper.cannotReadRepositoryDb(ex);
            }
        }
        orb.connect(this);
    }
    
    private String printServerDef(final ServerDef serverDef) {
        return "ServerDef[applicationName=" + serverDef.applicationName + " serverName=" + serverDef.serverName + " serverClassPath=" + serverDef.serverClassPath + " serverArgs=" + serverDef.serverArgs + " serverVmArgs=" + serverDef.serverVmArgs + "]";
    }
    
    public int registerServer(final ServerDef serverDef, final int n) throws ServerAlreadyRegistered {
        synchronized (this.db) {
            final Enumeration elements = this.db.serverTable.elements();
            while (elements.hasMoreElements()) {
                final DBServerDef dbServerDef = (DBServerDef)elements.nextElement();
                if (serverDef.applicationName.equals(dbServerDef.applicationName)) {
                    if (this.debug) {
                        System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(serverDef) + " with " + ((n == -1) ? "a new server Id" : ("server Id " + n)) + " FAILED because it is already registered.");
                    }
                    throw new ServerAlreadyRegistered(dbServerDef.id);
                }
            }
            int incrementServerIdCounter;
            if (n == -1) {
                incrementServerIdCounter = this.db.incrementServerIdCounter();
            }
            else {
                incrementServerIdCounter = n;
            }
            this.db.serverTable.put(new Integer(incrementServerIdCounter), new DBServerDef(serverDef, incrementServerIdCounter));
            this.db.flush();
            if (this.debug) {
                if (n == -1) {
                    System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(serverDef) + " with new serverId " + incrementServerIdCounter);
                }
                else {
                    System.out.println("RepositoryImpl: registerServer called to register ServerDef " + this.printServerDef(serverDef) + " with assigned serverId " + incrementServerIdCounter);
                }
            }
            return incrementServerIdCounter;
        }
    }
    
    @Override
    public int registerServer(final ServerDef serverDef) throws ServerAlreadyRegistered, BadServerDefinition {
        switch (new ServerTableEntry(this.wrapper, -1, serverDef, ((SocketOrChannelAcceptor)this.orb.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING")).getServerSocket().getLocalPort(), "", true, this.debug).verify()) {
            case 0: {
                return this.registerServer(serverDef, -1);
            }
            case 1: {
                throw new BadServerDefinition("main class not found.");
            }
            case 2: {
                throw new BadServerDefinition("no main method found.");
            }
            case 3: {
                throw new BadServerDefinition("server application error.");
            }
            default: {
                throw new BadServerDefinition("unknown Exception.");
            }
        }
    }
    
    @Override
    public void unregisterServer(final int n) throws ServerNotRegistered {
        final Integer n2 = new Integer(n);
        synchronized (this.db) {
            if (this.db.serverTable.get(n2) == null) {
                if (this.debug) {
                    System.out.println("RepositoryImpl: unregisterServer for serverId " + n + " called: server not registered");
                }
                throw new ServerNotRegistered();
            }
            this.db.serverTable.remove(n2);
            this.db.flush();
        }
        if (this.debug) {
            System.out.println("RepositoryImpl: unregisterServer for serverId " + n + " called");
        }
    }
    
    private DBServerDef getDBServerDef(final int n) throws ServerNotRegistered {
        final DBServerDef dbServerDef = this.db.serverTable.get(new Integer(n));
        if (dbServerDef == null) {
            throw new ServerNotRegistered(n);
        }
        return dbServerDef;
    }
    
    @Override
    public ServerDef getServer(final int n) throws ServerNotRegistered {
        final DBServerDef dbServerDef = this.getDBServerDef(n);
        final ServerDef serverDef = new ServerDef(dbServerDef.applicationName, dbServerDef.name, dbServerDef.classPath, dbServerDef.args, dbServerDef.vmArgs);
        if (this.debug) {
            System.out.println("RepositoryImpl: getServer for serverId " + n + " returns " + this.printServerDef(serverDef));
        }
        return serverDef;
    }
    
    @Override
    public boolean isInstalled(final int n) throws ServerNotRegistered {
        return this.getDBServerDef(n).isInstalled;
    }
    
    @Override
    public void install(final int n) throws ServerNotRegistered, ServerAlreadyInstalled {
        final DBServerDef dbServerDef = this.getDBServerDef(n);
        if (dbServerDef.isInstalled) {
            throw new ServerAlreadyInstalled(n);
        }
        dbServerDef.isInstalled = true;
        this.db.flush();
    }
    
    @Override
    public void uninstall(final int n) throws ServerNotRegistered, ServerAlreadyUninstalled {
        final DBServerDef dbServerDef = this.getDBServerDef(n);
        if (!dbServerDef.isInstalled) {
            throw new ServerAlreadyUninstalled(n);
        }
        dbServerDef.isInstalled = false;
        this.db.flush();
    }
    
    @Override
    public int[] listRegisteredServers() {
        synchronized (this.db) {
            int n = 0;
            final int[] array = new int[this.db.serverTable.size()];
            final Enumeration elements = this.db.serverTable.elements();
            while (elements.hasMoreElements()) {
                array[n++] = ((DBServerDef)elements.nextElement()).id;
            }
            if (this.debug) {
                final StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(' ');
                    sb.append(array[i]);
                }
                System.out.println("RepositoryImpl: listRegisteredServers returns" + sb.toString());
            }
            return array;
        }
    }
    
    @Override
    public int getServerID(final String s) throws ServerNotRegistered {
        synchronized (this.db) {
            int intValue = -1;
            final Enumeration keys = this.db.serverTable.keys();
            while (keys.hasMoreElements()) {
                final Integer n = (Integer)keys.nextElement();
                if (((DBServerDef)this.db.serverTable.get(n)).applicationName.equals(s)) {
                    intValue = n;
                    break;
                }
            }
            if (this.debug) {
                System.out.println("RepositoryImpl: getServerID for " + s + " is " + intValue);
            }
            if (intValue == -1) {
                throw new ServerNotRegistered();
            }
            return intValue;
        }
    }
    
    @Override
    public String[] getApplicationNames() {
        synchronized (this.db) {
            final Vector vector = new Vector();
            final Enumeration keys = this.db.serverTable.keys();
            while (keys.hasMoreElements()) {
                final DBServerDef dbServerDef = this.db.serverTable.get(keys.nextElement());
                if (!dbServerDef.applicationName.equals("")) {
                    vector.addElement(dbServerDef.applicationName);
                }
            }
            final String[] array = new String[vector.size()];
            for (int i = 0; i < vector.size(); ++i) {
                array[i] = (String)vector.elementAt(i);
            }
            if (this.debug) {
                final StringBuffer sb = new StringBuffer();
                for (int j = 0; j < array.length; ++j) {
                    sb.append(' ');
                    sb.append(array[j]);
                }
                System.out.println("RepositoryImpl: getApplicationNames returns " + sb.toString());
            }
            return array;
        }
    }
    
    public static void main(final String[] array) {
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-debug")) {
                b = true;
            }
        }
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
            final ORB orb = (ORB)org.omg.CORBA.ORB.init(array, properties);
            final RepositoryImpl repositoryImpl = new RepositoryImpl(orb, new File(System.getProperty("com.sun.CORBA.activation.db", "db")), b);
            orb.run();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    class RepositoryDB implements Serializable
    {
        File db;
        Hashtable serverTable;
        Integer serverIdCounter;
        
        RepositoryDB(final File db) {
            this.db = db;
            this.serverTable = new Hashtable(255);
            this.serverIdCounter = new Integer(256);
        }
        
        int incrementServerIdCounter() {
            int intValue = this.serverIdCounter;
            this.serverIdCounter = new Integer(++intValue);
            return intValue;
        }
        
        void flush() {
            try {
                this.db.delete();
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.db));
                objectOutputStream.writeObject(this);
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            catch (final Exception ex) {
                throw RepositoryImpl.this.wrapper.cannotWriteRepositoryDb(ex);
            }
        }
    }
    
    class DBServerDef implements Serializable
    {
        String applicationName;
        String name;
        String classPath;
        String args;
        String vmArgs;
        boolean isInstalled;
        int id;
        
        @Override
        public String toString() {
            return "DBServerDef(applicationName=" + this.applicationName + ", name=" + this.name + ", classPath=" + this.classPath + ", args=" + this.args + ", vmArgs=" + this.vmArgs + ", id=" + this.id + ", isInstalled=" + this.isInstalled + ")";
        }
        
        DBServerDef(final ServerDef serverDef, final int id) {
            this.applicationName = serverDef.applicationName;
            this.name = serverDef.serverName;
            this.classPath = serverDef.serverClassPath;
            this.args = serverDef.serverArgs;
            this.vmArgs = serverDef.serverVmArgs;
            this.id = id;
            this.isInstalled = false;
        }
    }
}
