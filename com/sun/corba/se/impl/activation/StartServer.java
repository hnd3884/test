package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class StartServer implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "startup";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.startserver"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.startserver1"));
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
            ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator")).activate(n);
            printStream.println(CorbaResourceUtil.getText("servertool.startserver2"));
        }
        catch (final ServerNotRegistered serverNotRegistered) {
            printStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        }
        catch (final ServerAlreadyActive serverAlreadyActive) {
            printStream.println(CorbaResourceUtil.getText("servertool.serverup"));
        }
        catch (final ServerHeldDown serverHeldDown) {
            printStream.println(CorbaResourceUtil.getText("servertool.helddown"));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
