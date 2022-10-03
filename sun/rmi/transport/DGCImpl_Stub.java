package sun.rmi.transport;

import java.io.DataOutput;
import java.util.ArrayList;
import java.rmi.server.UID;
import sun.misc.ObjectInputFilter;
import java.rmi.UnmarshalException;
import sun.rmi.transport.tcp.TCPConnection;
import java.rmi.dgc.Lease;
import java.io.ObjectOutput;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteCall;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteRef;
import java.rmi.server.Operation;
import java.rmi.dgc.DGC;
import java.rmi.server.RemoteStub;

public final class DGCImpl_Stub extends RemoteStub implements DGC
{
    private static final Operation[] operations;
    private static final long interfaceHash = -669196253586618813L;
    private static int DGCCLIENT_MAX_DEPTH;
    private static int DGCCLIENT_MAX_ARRAY_SIZE;
    
    public DGCImpl_Stub() {
    }
    
    public DGCImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    @Override
    public void clean(final ObjID[] array, final long n, final VMID vmid, final boolean b) throws RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, DGCImpl_Stub.operations, 0, -669196253586618813L);
            streamRemoteCall.setObjectInputFilter(DGCImpl_Stub::leaseFilter);
            try {
                final ObjectOutput outputStream = streamRemoteCall.getOutputStream();
                outputStream.writeObject(array);
                outputStream.writeLong(n);
                outputStream.writeObject(vmid);
                outputStream.writeBoolean(b);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            this.ref.invoke(streamRemoteCall);
            this.ref.done(streamRemoteCall);
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    @Override
    public Lease dirty(final ObjID[] array, final long n, final Lease lease) throws RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, DGCImpl_Stub.operations, 1, -669196253586618813L);
            streamRemoteCall.setObjectInputFilter(DGCImpl_Stub::leaseFilter);
            try {
                final Object outputStream = streamRemoteCall.getOutputStream();
                ((ObjectOutput)outputStream).writeObject(array);
                ((DataOutput)outputStream).writeLong(n);
                ((ObjectOutput)outputStream).writeObject(lease);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            this.ref.invoke(streamRemoteCall);
            final Connection connection = streamRemoteCall.getConnection();
            Object outputStream;
            try {
                outputStream = streamRemoteCall.getInputStream().readObject();
            }
            catch (final ClassCastException | IOException | ClassNotFoundException ex2) {
                if (connection instanceof TCPConnection) {
                    ((TCPConnection)connection).getChannel().free(connection, false);
                }
                streamRemoteCall.discardPendingRefs();
                throw new UnmarshalException("error unmarshalling return", (Exception)ex2);
            }
            finally {
                this.ref.done(streamRemoteCall);
            }
            return (Lease)outputStream;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    private static ObjectInputFilter.Status leaseFilter(final ObjectInputFilter.FilterInfo filterInfo) {
        if (filterInfo.depth() > DGCImpl_Stub.DGCCLIENT_MAX_DEPTH) {
            return ObjectInputFilter.Status.REJECTED;
        }
        Class<?> clazz = filterInfo.serialClass();
        if (clazz == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        while (clazz.isArray()) {
            if (filterInfo.arrayLength() >= 0L && filterInfo.arrayLength() > DGCImpl_Stub.DGCCLIENT_MAX_ARRAY_SIZE) {
                return ObjectInputFilter.Status.REJECTED;
            }
            clazz = clazz.getComponentType();
        }
        if (clazz.isPrimitive()) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return (clazz == UID.class || clazz == VMID.class || clazz == Lease.class || (Throwable.class.isAssignableFrom(clazz) && clazz.getClassLoader() == Object.class.getClassLoader()) || clazz == StackTraceElement.class || clazz == ArrayList.class || clazz == Object.class || clazz.getName().equals("java.util.Collections$UnmodifiableList") || clazz.getName().equals("java.util.Collections$UnmodifiableCollection") || clazz.getName().equals("java.util.Collections$UnmodifiableRandomAccessList")) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
    }
    
    static {
        operations = new Operation[] { new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)") };
        DGCImpl_Stub.DGCCLIENT_MAX_DEPTH = 6;
        DGCImpl_Stub.DGCCLIENT_MAX_ARRAY_SIZE = 10000;
    }
}
