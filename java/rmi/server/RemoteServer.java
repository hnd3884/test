package java.rmi.server;

import java.io.PrintStream;
import sun.rmi.server.UnicastServerRef;
import java.io.OutputStream;
import sun.rmi.transport.tcp.TCPTransport;

public abstract class RemoteServer extends RemoteObject
{
    private static final long serialVersionUID = -4100238210092549637L;
    private static boolean logNull;
    
    protected RemoteServer() {
    }
    
    protected RemoteServer(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    public static String getClientHost() throws ServerNotActiveException {
        return TCPTransport.getClientHost();
    }
    
    public static void setLog(final OutputStream outputStream) {
        RemoteServer.logNull = (outputStream == null);
        UnicastServerRef.callLog.setOutputStream(outputStream);
    }
    
    public static PrintStream getLog() {
        return RemoteServer.logNull ? null : UnicastServerRef.callLog.getPrintStream();
    }
    
    static {
        RemoteServer.logNull = !UnicastServerRef.logCalls;
    }
}
