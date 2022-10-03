package sun.rmi.transport.proxy;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public final class CGIHandler
{
    static int ContentLength;
    static String QueryString;
    static String RequestMethod;
    static String ServerName;
    static int ServerPort;
    private static CGICommandHandler[] commands;
    private static Hashtable<String, CGICommandHandler> commandLookup;
    
    private CGIHandler() {
    }
    
    public static void main(final String[] array) {
        try {
            final int index = CGIHandler.QueryString.indexOf("=");
            String s;
            String substring;
            if (index == -1) {
                s = CGIHandler.QueryString;
                substring = "";
            }
            else {
                s = CGIHandler.QueryString.substring(0, index);
                substring = CGIHandler.QueryString.substring(index + 1);
            }
            final CGICommandHandler cgiCommandHandler = CGIHandler.commandLookup.get(s);
            if (cgiCommandHandler != null) {
                try {
                    cgiCommandHandler.execute(substring);
                }
                catch (final CGIClientException ex) {
                    ex.printStackTrace();
                    returnClientError(ex.getMessage());
                }
                catch (final CGIServerException ex2) {
                    ex2.printStackTrace();
                    returnServerError(ex2.getMessage());
                }
            }
            else {
                returnClientError("invalid command.");
            }
        }
        catch (final Exception ex3) {
            ex3.printStackTrace();
            returnServerError("internal error: " + ex3.getMessage());
        }
        System.exit(0);
    }
    
    private static void returnClientError(final String s) {
        System.out.println("Status: 400 Bad Request: " + s);
        System.out.println("Content-type: text/html");
        System.out.println("");
        System.out.println("<HTML><HEAD><TITLE>Java RMI Client Error</TITLE></HEAD><BODY>");
        System.out.println("<H1>Java RMI Client Error</H1>");
        System.out.println("");
        System.out.println(s);
        System.out.println("</BODY></HTML>");
        System.exit(1);
    }
    
    private static void returnServerError(final String s) {
        System.out.println("Status: 500 Server Error: " + s);
        System.out.println("Content-type: text/html");
        System.out.println("");
        System.out.println("<HTML><HEAD><TITLE>Java RMI Server Error</TITLE></HEAD><BODY>");
        System.out.println("<H1>Java RMI Server Error</H1>");
        System.out.println("");
        System.out.println(s);
        System.out.println("</BODY></HTML>");
        System.exit(1);
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                CGIHandler.ContentLength = Integer.getInteger("CONTENT_LENGTH", 0);
                CGIHandler.QueryString = System.getProperty("QUERY_STRING", "");
                CGIHandler.RequestMethod = System.getProperty("REQUEST_METHOD", "");
                CGIHandler.ServerName = System.getProperty("SERVER_NAME", "");
                CGIHandler.ServerPort = Integer.getInteger("SERVER_PORT", 0);
                return null;
            }
        });
        CGIHandler.commands = new CGICommandHandler[] { new CGIForwardCommand(), new CGIGethostnameCommand(), new CGIPingCommand(), new CGITryHostnameCommand() };
        CGIHandler.commandLookup = new Hashtable<String, CGICommandHandler>();
        for (int i = 0; i < CGIHandler.commands.length; ++i) {
            CGIHandler.commandLookup.put(CGIHandler.commands[i].getName(), CGIHandler.commands[i]);
        }
    }
}
