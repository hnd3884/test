package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.LocatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class LocateServer implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "locate";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.locate"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.locate1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        int n = -1;
        String s = "IIOP_CLEAR_TEXT";
        try {
            int i = 0;
            while (i < array.length) {
                final String s2 = array[i++];
                if (s2.equals("-serverid")) {
                    if (i >= array.length) {
                        return true;
                    }
                    n = Integer.valueOf(array[i++]);
                }
                else if (s2.equals("-applicationName")) {
                    if (i >= array.length) {
                        return true;
                    }
                    n = ServerTool.getServerIdForAlias(orb, array[i++]);
                }
                else {
                    if (!s2.equals("-endpointType") || i >= array.length) {
                        continue;
                    }
                    s = array[i++];
                }
            }
            if (n == -1) {
                return true;
            }
            final ServerLocation locateServer = LocatorHelper.narrow(orb.resolve_initial_references("ServerLocator")).locateServer(n, s);
            printStream.println(CorbaResourceUtil.getText("servertool.locate2", locateServer.hostname));
            for (int length = locateServer.ports.length, j = 0; j < length; ++j) {
                final ORBPortInfo orbPortInfo = locateServer.ports[j];
                printStream.println("\t\t" + orbPortInfo.port + "\t\t" + s + "\t\t" + orbPortInfo.orbId);
            }
        }
        catch (final NoSuchEndPoint noSuchEndPoint) {}
        catch (final ServerHeldDown serverHeldDown) {
            printStream.println(CorbaResourceUtil.getText("servertool.helddown"));
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
