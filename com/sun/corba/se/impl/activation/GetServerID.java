package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class GetServerID implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "getserverid";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.getserverid"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.getserverid1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        if (array.length == 2 && array[0].equals("-applicationName")) {
            final String s = array[1];
            try {
                final Repository narrow = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository"));
                try {
                    final int serverID = narrow.getServerID(s);
                    printStream.println();
                    printStream.println(CorbaResourceUtil.getText("servertool.getserverid2", s, Integer.toString(serverID)));
                    printStream.println();
                }
                catch (final ServerNotRegistered serverNotRegistered) {
                    printStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }
}
