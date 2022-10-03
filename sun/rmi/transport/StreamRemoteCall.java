package sun.rmi.transport;

import sun.rmi.transport.tcp.TCPEndpoint;
import sun.rmi.server.UnicastRef;
import java.rmi.UnmarshalException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.StreamCorruptedException;
import java.security.AccessController;
import java.io.ObjectInputStream;
import java.io.ObjectInput;
import java.rmi.RemoteException;
import java.io.IOException;
import java.rmi.MarshalException;
import java.io.ObjectOutput;
import sun.rmi.runtime.Log;
import java.rmi.server.ObjID;
import sun.misc.ObjectInputFilter;
import java.rmi.server.RemoteCall;

public class StreamRemoteCall implements RemoteCall
{
    private ConnectionInputStream in;
    private ConnectionOutputStream out;
    private Connection conn;
    private ObjectInputFilter filter;
    private boolean resultStarted;
    private Exception serverException;
    
    public StreamRemoteCall(final Connection conn) {
        this.in = null;
        this.out = null;
        this.filter = null;
        this.resultStarted = false;
        this.serverException = null;
        this.conn = conn;
    }
    
    public StreamRemoteCall(final Connection conn, final ObjID objID, final int n, final long n2) throws RemoteException {
        this.in = null;
        this.out = null;
        this.filter = null;
        this.resultStarted = false;
        this.serverException = null;
        try {
            this.conn = conn;
            Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
            this.conn.getOutputStream().write(80);
            this.getOutputStream();
            objID.write(this.out);
            this.out.writeInt(n);
            this.out.writeLong(n2);
        }
        catch (final IOException ex) {
            throw new MarshalException("Error marshaling call header", ex);
        }
    }
    
    public Connection getConnection() {
        return this.conn;
    }
    
    @Override
    public ObjectOutput getOutputStream() throws IOException {
        return this.getOutputStream(false);
    }
    
    private ObjectOutput getOutputStream(final boolean b) throws IOException {
        if (this.out == null) {
            Transport.transportLog.log(Log.VERBOSE, "getting output stream");
            this.out = new ConnectionOutputStream(this.conn, b);
        }
        return this.out;
    }
    
    @Override
    public void releaseOutputStream() throws IOException {
        try {
            if (this.out != null) {
                try {
                    this.out.flush();
                }
                finally {
                    this.out.done();
                }
            }
            this.conn.releaseOutputStream();
        }
        finally {
            this.out = null;
        }
    }
    
    public void setObjectInputFilter(final ObjectInputFilter filter) {
        if (this.in != null) {
            throw new IllegalStateException("set filter must occur before calling getInputStream");
        }
        this.filter = filter;
    }
    
    @Override
    public ObjectInput getInputStream() throws IOException {
        if (this.in == null) {
            Transport.transportLog.log(Log.VERBOSE, "getting input stream");
            this.in = new ConnectionInputStream(this.conn.getInputStream());
            if (this.filter != null) {
                AccessController.doPrivileged(() -> {
                    ObjectInputFilter.Config.setObjectInputFilter(this.in, this.filter);
                    return null;
                });
            }
        }
        return this.in;
    }
    
    @Override
    public void releaseInputStream() throws IOException {
        try {
            if (this.in != null) {
                try {
                    this.in.done();
                }
                catch (final RuntimeException ex) {}
                this.in.registerRefs();
                this.in.done(this.conn);
            }
            this.conn.releaseInputStream();
        }
        finally {
            this.in = null;
        }
    }
    
    public void discardPendingRefs() {
        this.in.discardRefs();
    }
    
    @Override
    public ObjectOutput getResultStream(final boolean b) throws IOException {
        if (this.resultStarted) {
            throw new StreamCorruptedException("result already in progress");
        }
        this.resultStarted = true;
        new DataOutputStream(this.conn.getOutputStream()).writeByte(81);
        this.getOutputStream(true);
        if (b) {
            this.out.writeByte(1);
        }
        else {
            this.out.writeByte(2);
        }
        this.out.writeID();
        return this.out;
    }
    
    @Override
    public void executeCall() throws Exception {
        DGCAckHandler dgcAckHandler = null;
        byte byte2;
        try {
            if (this.out != null) {
                dgcAckHandler = this.out.getDGCAckHandler();
            }
            this.releaseOutputStream();
            final byte byte1 = new DataInputStream(this.conn.getInputStream()).readByte();
            if (byte1 != 81) {
                if (Transport.transportLog.isLoggable(Log.BRIEF)) {
                    Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + byte1);
                }
                throw new UnmarshalException("Transport return code invalid");
            }
            this.getInputStream();
            byte2 = this.in.readByte();
            this.in.readID();
        }
        catch (final UnmarshalException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw new UnmarshalException("Error unmarshaling return header", ex2);
        }
        finally {
            if (dgcAckHandler != null) {
                dgcAckHandler.release();
            }
        }
        switch (byte2) {
            case 1: {
                return;
            }
            case 2: {
                Object object;
                try {
                    object = this.in.readObject();
                }
                catch (final Exception ex3) {
                    this.discardPendingRefs();
                    throw new UnmarshalException("Error unmarshaling return", ex3);
                }
                if (object instanceof Exception) {
                    this.exceptionReceivedFromServer((Exception)object);
                    break;
                }
                this.discardPendingRefs();
                throw new UnmarshalException("Return type not Exception");
            }
        }
        if (Transport.transportLog.isLoggable(Log.BRIEF)) {
            Transport.transportLog.log(Log.BRIEF, "return code invalid: " + byte2);
        }
        throw new UnmarshalException("Return code invalid");
    }
    
    protected void exceptionReceivedFromServer(final Exception serverException) throws Exception {
        this.serverException = serverException;
        final StackTraceElement[] stackTrace = serverException.getStackTrace();
        final StackTraceElement[] stackTrace2 = new Throwable().getStackTrace();
        final StackTraceElement[] stackTrace3 = new StackTraceElement[stackTrace.length + stackTrace2.length];
        System.arraycopy(stackTrace, 0, stackTrace3, 0, stackTrace.length);
        System.arraycopy(stackTrace2, 0, stackTrace3, stackTrace.length, stackTrace2.length);
        serverException.setStackTrace(stackTrace3);
        if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF)) {
            final TCPEndpoint tcpEndpoint = (TCPEndpoint)this.conn.getChannel().getEndpoint();
            UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + tcpEndpoint.getHost() + ":" + tcpEndpoint.getPort() + "] exception: ", serverException);
        }
        throw serverException;
    }
    
    public Exception getServerException() {
        return this.serverException;
    }
    
    @Override
    public void done() throws IOException {
        this.releaseInputStream();
    }
}
