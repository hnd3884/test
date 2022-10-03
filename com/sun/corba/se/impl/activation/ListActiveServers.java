package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class ListActiveServers implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "listactive";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.listactive"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.listactive1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        try {
            final Repository narrow = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository"));
            final int[] activeServers = ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator")).getActiveServers();
            printStream.println(CorbaResourceUtil.getText("servertool.list2"));
            ListServers.sortServers(activeServers);
            for (int i = 0; i < activeServers.length; ++i) {
                try {
                    final ServerDef server = narrow.getServer(activeServers[i]);
                    printStream.println("\t   " + activeServers[i] + "\t\t" + server.serverName + "\t\t" + server.applicationName);
                }
                catch (final ServerNotRegistered serverNotRegistered) {}
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
