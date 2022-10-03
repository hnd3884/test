package org.tanukisoftware.wrapper;

import java.net.Socket;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.net.ServerSocket;
import java.net.InetAddress;

public class WrapperActionServer implements Runnable
{
    public static final byte COMMAND_SHUTDOWN = 83;
    public static final byte COMMAND_HALT_EXPECTED = 72;
    public static final byte COMMAND_RESTART = 82;
    public static final byte COMMAND_DUMP = 68;
    public static final byte COMMAND_HALT_UNEXPECTED = 85;
    public static final byte COMMAND_ACCESS_VIOLATION = 86;
    public static final byte COMMAND_APPEAR_HUNG = 71;
    private InetAddress m_bindAddr;
    private int m_port;
    private Thread m_runner;
    private boolean m_runnerStop;
    private ServerSocket m_serverSocket;
    private Hashtable m_actions;
    private static WrapperPrintStream m_out;
    
    public WrapperActionServer(final int port, final InetAddress bindAddress) {
        this.m_runnerStop = false;
        this.m_actions = new Hashtable();
        this.m_port = port;
        this.m_bindAddr = bindAddress;
        boolean streamSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperActionServer.m_out = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperActionServer: ");
                    streamSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println(WrapperManager.getRes().getString("Failed to set the encoding '{0}' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.", sunStdoutEncoding));
                }
            }
        }
        if (!streamSet) {
            WrapperActionServer.m_out = new WrapperPrintStream(System.out, "WrapperActionServer: ");
        }
    }
    
    public WrapperActionServer(final int port) {
        this(port, null);
    }
    
    public void run() {
        if (Thread.currentThread() != this.m_runner) {
            throw new IllegalStateException(WrapperManager.getRes().getString("Private method."));
        }
        try {
            while (!this.m_runnerStop) {
                try {
                    final Socket socket = this.m_serverSocket.accept();
                    int command;
                    try {
                        socket.setSoTimeout(15000);
                        command = socket.getInputStream().read();
                    }
                    finally {
                        socket.close();
                    }
                    if (command < 0) {
                        continue;
                    }
                    final Runnable action;
                    synchronized (this.m_actions) {
                        action = this.m_actions.get(new Integer(command));
                    }
                    if (action == null) {
                        continue;
                    }
                    try {
                        action.run();
                    }
                    catch (final Throwable t) {
                        WrapperActionServer.m_out.println(WrapperManager.getRes().getString("Error processing action."));
                        t.printStackTrace(WrapperActionServer.m_out);
                    }
                }
                catch (final Throwable t2) {
                    if (this.m_runnerStop) {
                        if (t2 instanceof InterruptedException || t2 instanceof SocketException || t2 instanceof InterruptedIOException) {
                            continue;
                        }
                        if (t2 instanceof IOException && t2.getMessage() != null && t2.getMessage().indexOf("Bad file descriptor") >= 0) {
                            continue;
                        }
                    }
                    WrapperActionServer.m_out.println(WrapperManager.getRes().getString("Unexpected error."));
                    t2.printStackTrace(WrapperActionServer.m_out);
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        }
        finally {
            synchronized (this) {
                this.m_runner = null;
                this.notify();
            }
        }
    }
    
    public void start() throws IOException {
        this.m_serverSocket = new ServerSocket(this.m_port, 5, this.m_bindAddr);
        (this.m_runner = new Thread(this, "WrapperActionServer_runner")).setDaemon(true);
        this.m_runner.start();
    }
    
    public void stop() throws Exception {
        final Thread runner = this.m_runner;
        this.m_runnerStop = true;
        runner.interrupt();
        final ServerSocket serverSocket = this.m_serverSocket;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (final IOException ex) {}
        }
        synchronized (this) {
            while (this.m_runner != null) {
                try {
                    this.wait();
                }
                catch (final InterruptedException e) {}
            }
        }
    }
    
    public void registerAction(final byte command, final Runnable action) {
        synchronized (this.m_actions) {
            this.m_actions.put(new Integer(command), action);
        }
    }
    
    public void unregisterAction(final byte command) {
        synchronized (this.m_actions) {
            this.m_actions.remove(new Integer(command));
        }
    }
    
    public void enableShutdownAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)83, new Runnable() {
                public void run() {
                    WrapperManager.stopAndReturn(0);
                }
            });
        }
        else {
            this.unregisterAction((byte)83);
        }
    }
    
    public void enableHaltExpectedAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)72, new Runnable() {
                public void run() {
                    WrapperManager.stopImmediate(0);
                }
            });
        }
        else {
            this.unregisterAction((byte)72);
        }
    }
    
    public void enableRestartAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)82, new Runnable() {
                public void run() {
                    WrapperManager.restartAndReturn();
                }
            });
        }
        else {
            this.unregisterAction((byte)82);
        }
    }
    
    public void enableThreadDumpAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)68, new Runnable() {
                public void run() {
                    WrapperManager.requestThreadDump();
                }
            });
        }
        else {
            this.unregisterAction((byte)68);
        }
    }
    
    public void enableHaltUnexpectedAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)85, new Runnable() {
                public void run() {
                    Runtime.getRuntime().halt(0);
                }
            });
        }
        else {
            this.unregisterAction((byte)85);
        }
    }
    
    public void enableAccessViolationAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)86, new Runnable() {
                public void run() {
                    WrapperManager.accessViolationNative();
                }
            });
        }
        else {
            this.unregisterAction((byte)86);
        }
    }
    
    public void enableAppearHungAction(final boolean enable) {
        if (enable) {
            this.registerAction((byte)71, new Runnable() {
                public void run() {
                    WrapperManager.appearHung();
                }
            });
        }
        else {
            this.unregisterAction((byte)71);
        }
    }
}
