package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class ListServers implements CommandHandler
{
    static final int illegalServerId = -1;
    
    @Override
    public String getCommandName() {
        return "list";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.list"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.list1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        int intValue = -1;
        final boolean b = array.length != 0;
        if (array.length == 2 && array[0].equals("-serverid")) {
            intValue = Integer.valueOf(array[1]);
        }
        if (intValue == -1 && b) {
            return true;
        }
        try {
            final Repository narrow = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository"));
            if (b) {
                try {
                    final ServerDef server = narrow.getServer(intValue);
                    printStream.println();
                    printServerDef(server, intValue, printStream);
                    printStream.println();
                }
                catch (final ServerNotRegistered serverNotRegistered) {
                    printStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
                }
            }
            else {
                final int[] listRegisteredServers = narrow.listRegisteredServers();
                printStream.println(CorbaResourceUtil.getText("servertool.list2"));
                sortServers(listRegisteredServers);
                for (int i = 0; i < listRegisteredServers.length; ++i) {
                    try {
                        final ServerDef server2 = narrow.getServer(listRegisteredServers[i]);
                        printStream.println("\t   " + listRegisteredServers[i] + "\t\t" + server2.serverName + "\t\t" + server2.applicationName);
                    }
                    catch (final ServerNotRegistered serverNotRegistered2) {}
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    static void printServerDef(final ServerDef serverDef, final int n, final PrintStream printStream) {
        printStream.println(CorbaResourceUtil.getText("servertool.appname", serverDef.applicationName));
        printStream.println(CorbaResourceUtil.getText("servertool.name", serverDef.serverName));
        printStream.println(CorbaResourceUtil.getText("servertool.classpath", serverDef.serverClassPath));
        printStream.println(CorbaResourceUtil.getText("servertool.args", serverDef.serverArgs));
        printStream.println(CorbaResourceUtil.getText("servertool.vmargs", serverDef.serverVmArgs));
        printStream.println(CorbaResourceUtil.getText("servertool.serverid", n));
    }
    
    static void sortServers(final int[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            int n = i;
            for (int j = i + 1; j < length; ++j) {
                if (array[j] < array[n]) {
                    n = j;
                }
            }
            if (n != i) {
                final int n2 = array[i];
                array[i] = array[n];
                array[n] = n2;
            }
        }
    }
}
