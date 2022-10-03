package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class ListORBs implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "orblist";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.orbidmap"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.orbidmap1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        int n = -1;
        try {
            if (array.length == 2) {
                if (array[0].equals("-serverid")) {
                    n = Integer.valueOf(array[1]);
                }
                else if (array[0].equals("-applicationName")) {
                    n = ServerTool.getServerIdForAlias(orb, array[1]);
                }
            }
            if (n == -1) {
                return true;
            }
            final String[] orbNames = ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator")).getORBNames(n);
            printStream.println(CorbaResourceUtil.getText("servertool.orbidmap2"));
            for (int i = 0; i < orbNames.length; ++i) {
                printStream.println("\t " + orbNames[i]);
            }
        }
        catch (final ServerNotRegistered serverNotRegistered) {
            printStream.println("\tno such server found.");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
