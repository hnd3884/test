package com.sun.corba.se.impl.activation;

import java.util.Hashtable;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.Repository;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.COMM_FAILURE;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.util.Properties;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.Locator;
import java.io.File;

public class ORBD
{
    private int initSvcPort;
    protected File dbDir;
    private String dbDirName;
    protected Locator locator;
    protected Activator activator;
    protected RepositoryImpl repository;
    private static String[][] orbServers;
    
    protected void initializeBootNaming(final ORB orb) {
        this.initSvcPort = orb.getORBData().getORBInitialPort();
        SocketOrChannelAcceptorImpl socketOrChannelAcceptorImpl;
        if (orb.getORBData().getLegacySocketFactory() == null) {
            socketOrChannelAcceptorImpl = new SocketOrChannelAcceptorImpl(orb, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
        }
        else {
            socketOrChannelAcceptorImpl = new SocketFactoryAcceptorImpl(orb, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
        }
        orb.getCorbaTransportManager().registerAcceptor(socketOrChannelAcceptorImpl);
    }
    
    protected ORB createORB(final String[] array) {
        final Properties properties = System.getProperties();
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBServerId", "1000");
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBPersistentServerPort", properties.getProperty("com.sun.CORBA.activation.Port", Integer.toString(1049)));
        ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
        return (ORB)org.omg.CORBA.ORB.init(array, properties);
    }
    
    private void run(final String[] array) {
        try {
            this.processArgs(array);
            final ORB orb = this.createORB(array);
            if (orb.orbdDebugFlag) {
                System.out.println("ORBD begins initialization.");
            }
            final boolean systemDirs = this.createSystemDirs("orb.db");
            this.startActivationObjects(orb);
            if (systemDirs) {
                this.installOrbServers(this.getRepository(), this.getActivator());
            }
            if (orb.orbdDebugFlag) {
                System.out.println("ORBD is ready.");
                System.out.println("ORBD serverid: " + System.getProperty("com.sun.CORBA.POA.ORBServerId"));
                System.out.println("activation dbdir: " + System.getProperty("com.sun.CORBA.activation.DbDir"));
                System.out.println("activation port: " + System.getProperty("com.sun.CORBA.activation.Port"));
                String s = System.getProperty("com.sun.CORBA.activation.ServerPollingTime");
                if (s == null) {
                    s = Integer.toString(1000);
                }
                System.out.println("activation Server Polling Time: " + s + " milli-seconds ");
                String s2 = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
                if (s2 == null) {
                    s2 = Integer.toString(1000);
                }
                System.out.println("activation Server Startup Delay: " + s2 + " milli-seconds ");
            }
            new NameServiceStartThread(orb, this.dbDir).start();
            orb.run();
        }
        catch (final COMM_FAILURE comm_FAILURE) {
            System.out.println(CorbaResourceUtil.getText("orbd.commfailure"));
            System.out.println(comm_FAILURE);
            comm_FAILURE.printStackTrace();
        }
        catch (final INTERNAL internal) {
            System.out.println(CorbaResourceUtil.getText("orbd.internalexception"));
            System.out.println(internal);
            internal.printStackTrace();
        }
        catch (final Exception ex) {
            System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
    
    private void processArgs(final String[] array) {
        final Properties properties = System.getProperties();
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-port")) {
                if (i + 1 < array.length) {
                    ((Hashtable<String, String>)properties).put("com.sun.CORBA.activation.Port", array[++i]);
                }
                else {
                    System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
                }
            }
            else if (array[i].equals("-defaultdb")) {
                if (i + 1 < array.length) {
                    ((Hashtable<String, String>)properties).put("com.sun.CORBA.activation.DbDir", array[++i]);
                }
                else {
                    System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
                }
            }
            else if (array[i].equals("-serverid")) {
                if (i + 1 < array.length) {
                    ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBServerId", array[++i]);
                }
                else {
                    System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
                }
            }
            else if (array[i].equals("-serverPollingTime")) {
                if (i + 1 < array.length) {
                    ((Hashtable<String, String>)properties).put("com.sun.CORBA.activation.ServerPollingTime", array[++i]);
                }
                else {
                    System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
                }
            }
            else if (array[i].equals("-serverStartupDelay")) {
                if (i + 1 < array.length) {
                    ((Hashtable<String, String>)properties).put("com.sun.CORBA.activation.ServerStartupDelay", array[++i]);
                }
                else {
                    System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
                }
            }
        }
    }
    
    protected boolean createSystemDirs(final String s) {
        boolean b = false;
        final Properties properties = System.getProperties();
        this.dbDir = new File(properties.getProperty("com.sun.CORBA.activation.DbDir", properties.getProperty("user.dir") + properties.getProperty("file.separator") + s));
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.activation.DbDir", this.dbDirName = this.dbDir.getAbsolutePath());
        if (!this.dbDir.exists()) {
            this.dbDir.mkdir();
            b = true;
        }
        final File file = new File(this.dbDir, "logs");
        if (!file.exists()) {
            file.mkdir();
        }
        return b;
    }
    
    protected File getDbDir() {
        return this.dbDir;
    }
    
    protected String getDbDirName() {
        return this.dbDirName;
    }
    
    protected void startActivationObjects(final ORB orb) throws Exception {
        this.initializeBootNaming(orb);
        orb.register_initial_reference("ServerRepository", this.repository = new RepositoryImpl(orb, this.dbDir, orb.orbdDebugFlag));
        final ServerManagerImpl serverManagerImpl = new ServerManagerImpl(orb, orb.getCorbaTransportManager(), this.repository, this.getDbDirName(), orb.orbdDebugFlag);
        orb.register_initial_reference("ServerLocator", this.locator = LocatorHelper.narrow(serverManagerImpl));
        orb.register_initial_reference("ServerActivator", this.activator = ActivatorHelper.narrow(serverManagerImpl));
        final TransientNameService transientNameService = new TransientNameService(orb, "TNameService");
    }
    
    protected Locator getLocator() {
        return this.locator;
    }
    
    protected Activator getActivator() {
        return this.activator;
    }
    
    protected RepositoryImpl getRepository() {
        return this.repository;
    }
    
    protected void installOrbServers(final RepositoryImpl repositoryImpl, final Activator activator) {
        for (int i = 0; i < ORBD.orbServers.length; ++i) {
            try {
                final String[] array = ORBD.orbServers[i];
                final ServerDef serverDef = new ServerDef(array[1], array[2], array[3], array[4], array[5]);
                final int intValue = Integer.valueOf(ORBD.orbServers[i][0]);
                repositoryImpl.registerServer(serverDef, intValue);
                activator.activate(intValue);
            }
            catch (final Exception ex) {}
        }
    }
    
    public static void main(final String[] array) {
        new ORBD().run(array);
    }
    
    static {
        ORBD.orbServers = new String[][] { { "" } };
    }
}
