package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.LocatorHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class LocateServerForORB implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "locateperorb";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.locateorb"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.locateorb1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        int n = -1;
        String s = "";
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
                    if (!s2.equals("-orbid") || i >= array.length) {
                        continue;
                    }
                    s = array[i++];
                }
            }
            if (n == -1) {
                return true;
            }
            final ServerLocationPerORB locateServerForORB = LocatorHelper.narrow(orb.resolve_initial_references("ServerLocator")).locateServerForORB(n, s);
            printStream.println(CorbaResourceUtil.getText("servertool.locateorb2", locateServerForORB.hostname));
            for (int length = locateServerForORB.ports.length, j = 0; j < length; ++j) {
                final EndPointInfo endPointInfo = locateServerForORB.ports[j];
                printStream.println("\t\t" + endPointInfo.port + "\t\t" + endPointInfo.endpointType + "\t\t" + s);
            }
        }
        catch (final InvalidORBid invalidORBid) {
            printStream.println(CorbaResourceUtil.getText("servertool.nosuchorb"));
        }
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
