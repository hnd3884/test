package com.sun.corba.se.impl.activation;

import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class Quit implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "quit";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.quit"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.quit1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        System.exit(0);
        return false;
    }
}
