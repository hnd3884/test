package com.sun.corba.se.impl.naming.pcosnaming;

import java.util.Hashtable;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.activation.InitialNameServiceHelper;
import java.util.Properties;
import java.io.File;
import com.sun.corba.se.spi.orb.ORB;

public class NameServer
{
    private ORB orb;
    private File dbDir;
    private static final String dbName = "names.db";
    
    public static void main(final String[] array) {
        new NameServer(array).run();
    }
    
    protected NameServer(final String[] array) {
        final Properties properties = System.getProperties();
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBServerId", "1000");
        ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
        this.orb = (ORB)org.omg.CORBA.ORB.init(array, properties);
        this.dbDir = new File(properties.getProperty("com.sun.CORBA.activation.DbDir") + properties.getProperty("file.separator") + "names.db" + properties.getProperty("file.separator"));
        if (!this.dbDir.exists()) {
            this.dbDir.mkdir();
        }
    }
    
    protected void run() {
        try {
            InitialNameServiceHelper.narrow(this.orb.resolve_initial_references("InitialNameService")).bind("NameService", new NameService(this.orb, this.dbDir).initialNamingContext(), true);
            System.out.println(CorbaResourceUtil.getText("pnameserv.success"));
            this.orb.run();
        }
        catch (final Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
