package com.sun.corba.se.impl.activation;

import org.omg.CORBA.ORB;
import java.io.PrintStream;

public interface CommandHandler
{
    public static final boolean shortHelp = true;
    public static final boolean longHelp = false;
    public static final boolean parseError = true;
    public static final boolean commandDone = false;
    
    String getCommandName();
    
    void printCommandHelp(final PrintStream p0, final boolean p1);
    
    boolean processCommand(final String[] p0, final ORB p1, final PrintStream p2);
}
