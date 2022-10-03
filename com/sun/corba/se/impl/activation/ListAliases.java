package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.RepositoryHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class ListAliases implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "listappnames";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.listappnames"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.listappnames1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        try {
            final String[] applicationNames = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository")).getApplicationNames();
            printStream.println(CorbaResourceUtil.getText("servertool.listappnames2"));
            printStream.println();
            for (int i = 0; i < applicationNames.length; ++i) {
                printStream.println("\t" + applicationNames[i]);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
