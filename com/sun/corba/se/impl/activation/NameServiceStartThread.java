package com.sun.corba.se.impl.activation;

import org.omg.CORBA.Object;
import com.sun.corba.se.impl.naming.pcosnaming.NameService;
import java.io.File;
import com.sun.corba.se.spi.orb.ORB;

public class NameServiceStartThread extends Thread
{
    private ORB orb;
    private File dbDir;
    
    public NameServiceStartThread(final ORB orb, final File dbDir) {
        this.orb = orb;
        this.dbDir = dbDir;
    }
    
    @Override
    public void run() {
        try {
            this.orb.register_initial_reference("NameService", new NameService(this.orb, this.dbDir).initialNamingContext());
        }
        catch (final Exception ex) {
            System.err.println("NameService did not start successfully");
            ex.printStackTrace();
        }
    }
}
