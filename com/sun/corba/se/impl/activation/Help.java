package com.sun.corba.se.impl.activation;

import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class Help implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "help";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.help"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.help1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        return false;
    }
}
