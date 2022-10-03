package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class ShutdownServer implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "shutdown";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.shutdown"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.shutdown1"));
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
            ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator")).shutdown(n);
            printStream.println(CorbaResourceUtil.getText("servertool.shutdown2"));
        }
        catch (final ServerNotActive serverNotActive) {
            printStream.println(CorbaResourceUtil.getText("servertool.servernotrunning"));
        }
        catch (final ServerNotRegistered serverNotRegistered) {
            printStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
