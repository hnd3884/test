package com.sun.corba.se.impl.activation;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Properties;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import java.util.Vector;
import org.omg.CORBA.ORB;

public class ServerTool
{
    static final String helpCommand = "help";
    static final String toolName = "servertool";
    static final String commandArg = "-cmd";
    private static final boolean debug = false;
    ORB orb;
    static Vector handlers;
    static int maxNameLen;
    
    public ServerTool() {
        this.orb = null;
    }
    
    static int getServerIdForAlias(final ORB orb, final String s) throws ServerNotRegistered {
        try {
            final Repository narrow = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository"));
            narrow.getServerID(s);
            return narrow.getServerID(s);
        }
        catch (final Exception ex) {
            throw new ServerNotRegistered();
        }
    }
    
    void run(final String[] array) {
        String[] array2 = null;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-cmd")) {
                final int n = array.length - i - 1;
                array2 = new String[n];
                for (int j = 0; j < n; ++j) {
                    array2[j] = array[++i];
                }
                break;
            }
        }
        try {
            final Properties properties = System.getProperties();
            ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
            this.orb = ORB.init(array, properties);
            if (array2 != null) {
                this.executeCommand(array2);
            }
            else {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(CorbaResourceUtil.getText("servertool.banner"));
                while (true) {
                    final String[] command = this.readCommand(bufferedReader);
                    if (command != null) {
                        this.executeCommand(command);
                    }
                    else {
                        this.printAvailableCommands();
                    }
                }
            }
        }
        catch (final Exception ex) {
            System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
            System.out.println();
            ex.printStackTrace();
        }
    }
    
    public static void main(final String[] array) {
        new ServerTool().run(array);
    }
    
    String[] readCommand(final BufferedReader bufferedReader) {
        System.out.print("servertool > ");
        try {
            int n = 0;
            String[] array = null;
            final String line = bufferedReader.readLine();
            if (line != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (stringTokenizer.countTokens() != 0) {
                    array = new String[stringTokenizer.countTokens()];
                    while (stringTokenizer.hasMoreTokens()) {
                        array[n++] = stringTokenizer.nextToken();
                    }
                }
            }
            return array;
        }
        catch (final Exception ex) {
            System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
            System.out.println();
            ex.printStackTrace();
            return null;
        }
    }
    
    void printAvailableCommands() {
        System.out.println(CorbaResourceUtil.getText("servertool.shorthelp"));
        for (int i = 0; i < ServerTool.handlers.size(); ++i) {
            final CommandHandler commandHandler = ServerTool.handlers.elementAt(i);
            System.out.print("\t" + commandHandler.getCommandName());
            for (int j = commandHandler.getCommandName().length(); j < ServerTool.maxNameLen; ++j) {
                System.out.print(" ");
            }
            System.out.print(" - ");
            commandHandler.printCommandHelp(System.out, true);
        }
        System.out.println();
    }
    
    void executeCommand(final String[] array) {
        if (array[0].equals("help")) {
            if (array.length == 1) {
                this.printAvailableCommands();
            }
            else {
                for (int i = 0; i < ServerTool.handlers.size(); ++i) {
                    final CommandHandler commandHandler = ServerTool.handlers.elementAt(i);
                    if (commandHandler.getCommandName().equals(array[1])) {
                        commandHandler.printCommandHelp(System.out, false);
                    }
                }
            }
            return;
        }
        for (int j = 0; j < ServerTool.handlers.size(); ++j) {
            final CommandHandler commandHandler2 = ServerTool.handlers.elementAt(j);
            if (commandHandler2.getCommandName().equals(array[0])) {
                final String[] array2 = new String[array.length - 1];
                for (int k = 0; k < array2.length; ++k) {
                    array2[k] = array[k + 1];
                }
                try {
                    System.out.println();
                    if (commandHandler2.processCommand(array2, this.orb, System.out)) {
                        commandHandler2.printCommandHelp(System.out, false);
                    }
                    System.out.println();
                }
                catch (final Exception ex) {}
                return;
            }
        }
        this.printAvailableCommands();
    }
    
    static {
        (ServerTool.handlers = new Vector()).addElement(new RegisterServer());
        ServerTool.handlers.addElement(new UnRegisterServer());
        ServerTool.handlers.addElement(new GetServerID());
        ServerTool.handlers.addElement(new ListServers());
        ServerTool.handlers.addElement(new ListAliases());
        ServerTool.handlers.addElement(new ListActiveServers());
        ServerTool.handlers.addElement(new LocateServer());
        ServerTool.handlers.addElement(new LocateServerForORB());
        ServerTool.handlers.addElement(new ListORBs());
        ServerTool.handlers.addElement(new ShutdownServer());
        ServerTool.handlers.addElement(new StartServer());
        ServerTool.handlers.addElement(new Help());
        ServerTool.handlers.addElement(new Quit());
        ServerTool.maxNameLen = 0;
        for (int i = 0; i < ServerTool.handlers.size(); ++i) {
            final int length = ServerTool.handlers.elementAt(i).getCommandName().length();
            if (length > ServerTool.maxNameLen) {
                ServerTool.maxNameLen = length;
            }
        }
    }
}
