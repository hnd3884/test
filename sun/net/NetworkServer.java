package sun.net;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer implements Runnable, Cloneable
{
    public Socket clientSocket;
    private Thread serverInstance;
    private ServerSocket serverSocket;
    public PrintStream clientOutput;
    public InputStream clientInput;
    
    public void close() throws IOException {
        this.clientSocket.close();
        this.clientSocket = null;
        this.clientInput = null;
        this.clientOutput = null;
    }
    
    public boolean clientIsOpen() {
        return this.clientSocket != null;
    }
    
    @Override
    public final void run() {
        if (this.serverSocket != null) {
            Thread.currentThread().setPriority(10);
            try {
                while (true) {
                    final Socket accept = this.serverSocket.accept();
                    final NetworkServer networkServer = (NetworkServer)this.clone();
                    networkServer.serverSocket = null;
                    networkServer.clientSocket = accept;
                    new Thread(networkServer).start();
                }
            }
            catch (final Exception ex) {
                System.out.print("Server failure\n");
                ex.printStackTrace();
                try {
                    this.serverSocket.close();
                }
                catch (final IOException ex2) {}
                System.out.print("cs=" + this.serverSocket + "\n");
                return;
            }
        }
        try {
            this.clientOutput = new PrintStream(new BufferedOutputStream(this.clientSocket.getOutputStream()), false, "ISO8859_1");
            this.clientInput = new BufferedInputStream(this.clientSocket.getInputStream());
            this.serviceRequest();
        }
        catch (final Exception ex3) {}
        try {
            this.close();
        }
        catch (final IOException ex4) {}
    }
    
    public final void startServer(final int n) throws IOException {
        this.serverSocket = new ServerSocket(n, 50);
        (this.serverInstance = new Thread(this)).start();
    }
    
    public void serviceRequest() throws IOException {
        final byte[] array = new byte[300];
        this.clientOutput.print("Echo server " + this.getClass().getName() + "\n");
        this.clientOutput.flush();
        int read;
        while ((read = this.clientInput.read(array, 0, array.length)) >= 0) {
            this.clientOutput.write(array, 0, read);
        }
    }
    
    public static void main(final String[] array) {
        try {
            new NetworkServer().startServer(8888);
        }
        catch (final IOException ex) {
            System.out.print("Server failed: " + ex + "\n");
        }
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public NetworkServer() {
        this.clientSocket = null;
    }
}
