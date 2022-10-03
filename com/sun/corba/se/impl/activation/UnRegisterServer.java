package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class UnRegisterServer implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "unregister";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.unregister"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.unregister1"));
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
            try {
                ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator")).uninstall(n);
            }
            catch (final ServerHeldDown serverHeldDown) {}
            RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository")).unregisterServer(n);
            printStream.println(CorbaResourceUtil.getText("servertool.unregister2"));
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
