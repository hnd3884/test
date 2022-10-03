package com.sun.corba.se.impl.naming.cosnaming;

import java.util.Hashtable;
import java.util.Properties;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.logging.NamingSystemException;

public class TransientNameServer
{
    private static boolean debug;
    static NamingSystemException wrapper;
    
    public static void trace(final String s) {
        if (TransientNameServer.debug) {
            System.out.println(s);
        }
    }
    
    public static void initDebug(final String[] array) {
        if (TransientNameServer.debug) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equalsIgnoreCase("-debug")) {
                TransientNameServer.debug = true;
                return;
            }
        }
        TransientNameServer.debug = false;
    }
    
    private static org.omg.CORBA.Object initializeRootNamingContext(final ORB orb) {
        try {
            return new TransientNameService((com.sun.corba.se.spi.orb.ORB)orb).initialNamingContext();
        }
        catch (final SystemException ex) {
            throw TransientNameServer.wrapper.transNsCannotCreateInitialNcSys(ex);
        }
        catch (final Exception ex2) {
            throw TransientNameServer.wrapper.transNsCannotCreateInitialNc(ex2);
        }
    }
    
    public static void main(final String[] array) {
        initDebug(array);
        boolean b = false;
        boolean b2 = false;
        int n = 0;
        try {
            trace("Transient name server started with args " + array);
            final Properties properties = System.getProperties();
            ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBServerId", "1000000");
            ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
            try {
                final String property = System.getProperty("org.omg.CORBA.ORBInitialPort");
                if (property != null && property.length() > 0) {
                    n = Integer.parseInt(property);
                    if (n == 0) {
                        b2 = true;
                        throw TransientNameServer.wrapper.transientNameServerBadPort();
                    }
                }
                if (System.getProperty("org.omg.CORBA.ORBInitialHost") != null) {
                    b = true;
                    throw TransientNameServer.wrapper.transientNameServerBadHost();
                }
            }
            catch (final NumberFormatException ex) {}
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals("-ORBInitialPort") && i < array.length - 1) {
                    n = Integer.parseInt(array[i + 1]);
                    if (n == 0) {
                        b2 = true;
                        throw TransientNameServer.wrapper.transientNameServerBadPort();
                    }
                }
                if (array[i].equals("-ORBInitialHost")) {
                    b = true;
                    throw TransientNameServer.wrapper.transientNameServerBadHost();
                }
            }
            if (n == 0) {
                n = 900;
                ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBInitialPort", Integer.toString(n));
            }
            ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBPersistentServerPort", Integer.toString(n));
            final ORB init = ORB.init(array, properties);
            trace("ORB object returned from init: " + init);
            final org.omg.CORBA.Object initializeRootNamingContext = initializeRootNamingContext(init);
            ((com.sun.corba.se.org.omg.CORBA.ORB)init).register_initial_reference("NamingService", initializeRootNamingContext);
            String object_to_string = null;
            if (initializeRootNamingContext != null) {
                object_to_string = init.object_to_string(initializeRootNamingContext);
            }
            else {
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", n));
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
                System.exit(1);
            }
            trace("name service created");
            System.out.println(CorbaResourceUtil.getText("tnameserv.hs1", object_to_string));
            System.out.println(CorbaResourceUtil.getText("tnameserv.hs2", n));
            System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));
            final Object o = new Object();
            synchronized (o) {
                o.wait();
            }
        }
        catch (final Exception ex2) {
            if (b) {
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.invalidhostoption"));
            }
            else if (b2) {
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.orbinitialport0"));
            }
            else {
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", n));
                NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
            }
            ex2.printStackTrace();
        }
    }
    
    private TransientNameServer() {
    }
    
    static {
        TransientNameServer.debug = false;
        TransientNameServer.wrapper = NamingSystemException.get("naming");
    }
}
