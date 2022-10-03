package sun.rmi.transport.tcp;

import java.rmi.server.LogStream;
import java.util.Enumeration;
import sun.rmi.transport.Connection;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Hashtable;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import sun.rmi.runtime.Log;

final class ConnectionMultiplexer
{
    static int logLevel;
    static final Log multiplexLog;
    private static final int OPEN = 225;
    private static final int CLOSE = 226;
    private static final int CLOSEACK = 227;
    private static final int REQUEST = 228;
    private static final int TRANSMIT = 229;
    private TCPChannel channel;
    private InputStream in;
    private OutputStream out;
    private boolean orig;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Hashtable<Integer, MultiplexConnectionInfo> connectionTable;
    private int numConnections;
    private static final int maxConnections = 256;
    private int lastID;
    private boolean alive;
    
    private static String getLogLevel() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel"));
    }
    
    public ConnectionMultiplexer(final TCPChannel channel, final InputStream in, final OutputStream out, final boolean orig) {
        this.connectionTable = new Hashtable<Integer, MultiplexConnectionInfo>(7);
        this.numConnections = 0;
        this.lastID = 4097;
        this.alive = true;
        this.channel = channel;
        this.in = in;
        this.out = out;
        this.orig = orig;
        this.dataIn = new DataInputStream(in);
        this.dataOut = new DataOutputStream(out);
    }
    
    public void run() throws IOException {
        try {
            while (true) {
                final int unsignedByte = this.dataIn.readUnsignedByte();
                switch (unsignedByte) {
                    case 225: {
                        final int unsignedShort = this.dataIn.readUnsignedShort();
                        if (ConnectionMultiplexer.multiplexLog.isLoggable(Log.VERBOSE)) {
                            ConnectionMultiplexer.multiplexLog.log(Log.VERBOSE, "operation  OPEN " + unsignedShort);
                        }
                        if (this.connectionTable.get(unsignedShort) != null) {
                            throw new IOException("OPEN: Connection ID already exists");
                        }
                        final MultiplexConnectionInfo multiplexConnectionInfo = new MultiplexConnectionInfo(unsignedShort);
                        multiplexConnectionInfo.in = new MultiplexInputStream(this, multiplexConnectionInfo, 2048);
                        multiplexConnectionInfo.out = new MultiplexOutputStream(this, multiplexConnectionInfo, 2048);
                        synchronized (this.connectionTable) {
                            this.connectionTable.put(unsignedShort, multiplexConnectionInfo);
                            ++this.numConnections;
                        }
                        this.channel.acceptMultiplexConnection(new TCPConnection(this.channel, multiplexConnectionInfo.in, multiplexConnectionInfo.out));
                        continue;
                    }
                    case 226: {
                        final int unsignedShort2 = this.dataIn.readUnsignedShort();
                        if (ConnectionMultiplexer.multiplexLog.isLoggable(Log.VERBOSE)) {
                            ConnectionMultiplexer.multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + unsignedShort2);
                        }
                        final MultiplexConnectionInfo multiplexConnectionInfo2 = this.connectionTable.get(unsignedShort2);
                        if (multiplexConnectionInfo2 == null) {
                            throw new IOException("CLOSE: Invalid connection ID");
                        }
                        multiplexConnectionInfo2.in.disconnect();
                        multiplexConnectionInfo2.out.disconnect();
                        if (!multiplexConnectionInfo2.closed) {
                            this.sendCloseAck(multiplexConnectionInfo2);
                        }
                        synchronized (this.connectionTable) {
                            this.connectionTable.remove(unsignedShort2);
                            --this.numConnections;
                        }
                        continue;
                    }
                    case 227: {
                        final int unsignedShort3 = this.dataIn.readUnsignedShort();
                        if (ConnectionMultiplexer.multiplexLog.isLoggable(Log.VERBOSE)) {
                            ConnectionMultiplexer.multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + unsignedShort3);
                        }
                        final MultiplexConnectionInfo multiplexConnectionInfo3 = this.connectionTable.get(unsignedShort3);
                        if (multiplexConnectionInfo3 == null) {
                            throw new IOException("CLOSEACK: Invalid connection ID");
                        }
                        if (!multiplexConnectionInfo3.closed) {
                            throw new IOException("CLOSEACK: Connection not closed");
                        }
                        multiplexConnectionInfo3.in.disconnect();
                        multiplexConnectionInfo3.out.disconnect();
                        synchronized (this.connectionTable) {
                            this.connectionTable.remove(unsignedShort3);
                            --this.numConnections;
                        }
                        continue;
                    }
                    case 228: {
                        final int unsignedShort4 = this.dataIn.readUnsignedShort();
                        final MultiplexConnectionInfo multiplexConnectionInfo4 = this.connectionTable.get(unsignedShort4);
                        if (multiplexConnectionInfo4 == null) {
                            throw new IOException("REQUEST: Invalid connection ID");
                        }
                        final int int1 = this.dataIn.readInt();
                        if (ConnectionMultiplexer.multiplexLog.isLoggable(Log.VERBOSE)) {
                            ConnectionMultiplexer.multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + unsignedShort4 + ": " + int1);
                        }
                        multiplexConnectionInfo4.out.request(int1);
                        continue;
                    }
                    case 229: {
                        final int unsignedShort5 = this.dataIn.readUnsignedShort();
                        final MultiplexConnectionInfo multiplexConnectionInfo5 = this.connectionTable.get(unsignedShort5);
                        if (multiplexConnectionInfo5 == null) {
                            throw new IOException("SEND: Invalid connection ID");
                        }
                        final int int2 = this.dataIn.readInt();
                        if (ConnectionMultiplexer.multiplexLog.isLoggable(Log.VERBOSE)) {
                            ConnectionMultiplexer.multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + unsignedShort5 + ": " + int2);
                        }
                        multiplexConnectionInfo5.in.receive(int2, this.dataIn);
                        continue;
                    }
                    default: {
                        throw new IOException("Invalid operation: " + Integer.toHexString(unsignedByte));
                    }
                }
            }
        }
        finally {
            this.shutDown();
        }
    }
    
    public synchronized TCPConnection openConnection() throws IOException {
        int lastID;
        do {
            this.lastID = (++this.lastID & 0x7FFF);
            lastID = this.lastID;
            if (this.orig) {
                lastID |= 0x8000;
            }
        } while (this.connectionTable.get(lastID) != null);
        final MultiplexConnectionInfo multiplexConnectionInfo = new MultiplexConnectionInfo(lastID);
        multiplexConnectionInfo.in = new MultiplexInputStream(this, multiplexConnectionInfo, 2048);
        multiplexConnectionInfo.out = new MultiplexOutputStream(this, multiplexConnectionInfo, 2048);
        synchronized (this.connectionTable) {
            if (!this.alive) {
                throw new IOException("Multiplexer connection dead");
            }
            if (this.numConnections >= 256) {
                throw new IOException("Cannot exceed 256 simultaneous multiplexed connections");
            }
            this.connectionTable.put(lastID, multiplexConnectionInfo);
            ++this.numConnections;
        }
        synchronized (this.dataOut) {
            try {
                this.dataOut.writeByte(225);
                this.dataOut.writeShort(lastID);
                this.dataOut.flush();
            }
            catch (final IOException ex) {
                ConnectionMultiplexer.multiplexLog.log(Log.BRIEF, "exception: ", ex);
                this.shutDown();
                throw ex;
            }
        }
        return new TCPConnection(this.channel, multiplexConnectionInfo.in, multiplexConnectionInfo.out);
    }
    
    public void shutDown() {
        synchronized (this.connectionTable) {
            if (!this.alive) {
                return;
            }
            this.alive = false;
            final Enumeration<MultiplexConnectionInfo> elements = this.connectionTable.elements();
            while (elements.hasMoreElements()) {
                final MultiplexConnectionInfo multiplexConnectionInfo = elements.nextElement();
                multiplexConnectionInfo.in.disconnect();
                multiplexConnectionInfo.out.disconnect();
            }
            this.connectionTable.clear();
            this.numConnections = 0;
        }
        try {
            this.in.close();
        }
        catch (final IOException ex) {}
        try {
            this.out.close();
        }
        catch (final IOException ex2) {}
    }
    
    void sendRequest(final MultiplexConnectionInfo multiplexConnectionInfo, final int n) throws IOException {
        synchronized (this.dataOut) {
            if (this.alive && !multiplexConnectionInfo.closed) {
                try {
                    this.dataOut.writeByte(228);
                    this.dataOut.writeShort(multiplexConnectionInfo.id);
                    this.dataOut.writeInt(n);
                    this.dataOut.flush();
                }
                catch (final IOException ex) {
                    ConnectionMultiplexer.multiplexLog.log(Log.BRIEF, "exception: ", ex);
                    this.shutDown();
                    throw ex;
                }
            }
        }
    }
    
    void sendTransmit(final MultiplexConnectionInfo multiplexConnectionInfo, final byte[] array, final int n, final int n2) throws IOException {
        synchronized (this.dataOut) {
            if (this.alive && !multiplexConnectionInfo.closed) {
                try {
                    this.dataOut.writeByte(229);
                    this.dataOut.writeShort(multiplexConnectionInfo.id);
                    this.dataOut.writeInt(n2);
                    this.dataOut.write(array, n, n2);
                    this.dataOut.flush();
                }
                catch (final IOException ex) {
                    ConnectionMultiplexer.multiplexLog.log(Log.BRIEF, "exception: ", ex);
                    this.shutDown();
                    throw ex;
                }
            }
        }
    }
    
    void sendClose(final MultiplexConnectionInfo multiplexConnectionInfo) throws IOException {
        multiplexConnectionInfo.out.disconnect();
        synchronized (this.dataOut) {
            if (this.alive && !multiplexConnectionInfo.closed) {
                try {
                    this.dataOut.writeByte(226);
                    this.dataOut.writeShort(multiplexConnectionInfo.id);
                    this.dataOut.flush();
                    multiplexConnectionInfo.closed = true;
                }
                catch (final IOException ex) {
                    ConnectionMultiplexer.multiplexLog.log(Log.BRIEF, "exception: ", ex);
                    this.shutDown();
                    throw ex;
                }
            }
        }
    }
    
    void sendCloseAck(final MultiplexConnectionInfo multiplexConnectionInfo) throws IOException {
        synchronized (this.dataOut) {
            if (this.alive && !multiplexConnectionInfo.closed) {
                try {
                    this.dataOut.writeByte(227);
                    this.dataOut.writeShort(multiplexConnectionInfo.id);
                    this.dataOut.flush();
                    multiplexConnectionInfo.closed = true;
                }
                catch (final IOException ex) {
                    ConnectionMultiplexer.multiplexLog.log(Log.BRIEF, "exception: ", ex);
                    this.shutDown();
                    throw ex;
                }
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.shutDown();
    }
    
    static {
        ConnectionMultiplexer.logLevel = LogStream.parseLevel(getLogLevel());
        multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", ConnectionMultiplexer.logLevel);
    }
}
