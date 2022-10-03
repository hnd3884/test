package sun.rmi.transport;

import java.util.Arrays;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.rmi.Remote;
import java.io.DataOutput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import sun.rmi.transport.tcp.TCPEndpoint;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.ObjID;

public class LiveRef implements Cloneable
{
    private final Endpoint ep;
    private final ObjID id;
    private transient Channel ch;
    private final boolean isLocal;
    
    public LiveRef(final ObjID id, final Endpoint ep, final boolean isLocal) {
        this.ep = ep;
        this.id = id;
        this.isLocal = isLocal;
    }
    
    public LiveRef(final int n) {
        this(new ObjID(), n);
    }
    
    public LiveRef(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        this(new ObjID(), n, rmiClientSocketFactory, rmiServerSocketFactory);
    }
    
    public LiveRef(final ObjID objID, final int n) {
        this(objID, TCPEndpoint.getLocalEndpoint(n), true);
    }
    
    public LiveRef(final ObjID objID, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) {
        this(objID, TCPEndpoint.getLocalEndpoint(n, rmiClientSocketFactory, rmiServerSocketFactory), true);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex.toString(), ex);
        }
    }
    
    public int getPort() {
        return ((TCPEndpoint)this.ep).getPort();
    }
    
    public RMIClientSocketFactory getClientSocketFactory() {
        return ((TCPEndpoint)this.ep).getClientSocketFactory();
    }
    
    public RMIServerSocketFactory getServerSocketFactory() {
        return ((TCPEndpoint)this.ep).getServerSocketFactory();
    }
    
    public void exportObject(final Target target) throws RemoteException {
        this.ep.exportObject(target);
    }
    
    public Channel getChannel() throws RemoteException {
        if (this.ch == null) {
            this.ch = this.ep.getChannel();
        }
        return this.ch;
    }
    
    public ObjID getObjID() {
        return this.id;
    }
    
    Endpoint getEndpoint() {
        return this.ep;
    }
    
    @Override
    public String toString() {
        String s;
        if (this.isLocal) {
            s = "local";
        }
        else {
            s = "remote";
        }
        return "[endpoint:" + this.ep + "(" + s + "),objID:" + this.id + "]";
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof LiveRef) {
            final LiveRef liveRef = (LiveRef)o;
            return this.ep.equals(liveRef.ep) && this.id.equals(liveRef.id) && this.isLocal == liveRef.isLocal;
        }
        return false;
    }
    
    public boolean remoteEquals(final Object o) {
        if (o != null && o instanceof LiveRef) {
            final LiveRef liveRef = (LiveRef)o;
            final TCPEndpoint tcpEndpoint = (TCPEndpoint)this.ep;
            final TCPEndpoint tcpEndpoint2 = (TCPEndpoint)liveRef.ep;
            final RMIClientSocketFactory clientSocketFactory = tcpEndpoint.getClientSocketFactory();
            final RMIClientSocketFactory clientSocketFactory2 = tcpEndpoint2.getClientSocketFactory();
            return tcpEndpoint.getPort() == tcpEndpoint2.getPort() && tcpEndpoint.getHost().equals(tcpEndpoint2.getHost()) && !(clientSocketFactory == null ^ clientSocketFactory2 == null) && (clientSocketFactory == null || (clientSocketFactory.getClass() == clientSocketFactory2.getClass() && clientSocketFactory.equals(clientSocketFactory2))) && this.id.equals(liveRef.id);
        }
        return false;
    }
    
    public void write(final ObjectOutput objectOutput, final boolean b) throws IOException {
        boolean resultStream = false;
        if (objectOutput instanceof ConnectionOutputStream) {
            final ConnectionOutputStream connectionOutputStream = (ConnectionOutputStream)objectOutput;
            resultStream = connectionOutputStream.isResultStream();
            if (this.isLocal) {
                final Target target = ObjectTable.getTarget(new ObjectEndpoint(this.id, this.ep.getInboundTransport()));
                if (target != null) {
                    final Remote impl = target.getImpl();
                    if (impl != null) {
                        connectionOutputStream.saveObject(impl);
                    }
                }
            }
            else {
                connectionOutputStream.saveObject(this);
            }
        }
        if (b) {
            ((TCPEndpoint)this.ep).write(objectOutput);
        }
        else {
            ((TCPEndpoint)this.ep).writeHostPortFormat(objectOutput);
        }
        this.id.write(objectOutput);
        objectOutput.writeBoolean(resultStream);
    }
    
    public static LiveRef read(final ObjectInput objectInput, final boolean b) throws IOException, ClassNotFoundException {
        TCPEndpoint tcpEndpoint;
        if (b) {
            tcpEndpoint = TCPEndpoint.read(objectInput);
        }
        else {
            tcpEndpoint = TCPEndpoint.readHostPortFormat(objectInput);
        }
        final ObjID read = ObjID.read(objectInput);
        final boolean boolean1 = objectInput.readBoolean();
        final LiveRef liveRef = new LiveRef(read, tcpEndpoint, false);
        if (objectInput instanceof ConnectionInputStream) {
            final ConnectionInputStream connectionInputStream = (ConnectionInputStream)objectInput;
            connectionInputStream.saveRef(liveRef);
            if (boolean1) {
                connectionInputStream.setAckNeeded();
            }
        }
        else {
            DGCClient.registerRefs(tcpEndpoint, Arrays.asList(liveRef));
        }
        return liveRef;
    }
}
