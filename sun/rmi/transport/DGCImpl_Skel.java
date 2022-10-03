package sun.rmi.transport;

import java.io.ObjectInput;
import java.rmi.dgc.Lease;
import java.rmi.MarshalException;
import java.io.IOException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class DGCImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = -669196253586618813L;
    
    @Override
    public Operation[] getOperations() {
        return DGCImpl_Skel.operations.clone();
    }
    
    @Override
    public void dispatch(final Remote remote, final RemoteCall remoteCall, final int n, final long n2) throws Exception {
        if (n2 != -669196253586618813L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final DGCImpl dgcImpl = (DGCImpl)remote;
        final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)remoteCall;
        switch (n) {
            case 0: {
                ObjID[] array;
                long long1;
                VMID vmid;
                boolean boolean1;
                try {
                    final ObjectInput inputStream = streamRemoteCall.getInputStream();
                    array = (ObjID[])inputStream.readObject();
                    long1 = inputStream.readLong();
                    vmid = (VMID)inputStream.readObject();
                    boolean1 = inputStream.readBoolean();
                }
                catch (final ClassCastException | IOException | ClassNotFoundException ex) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                dgcImpl.clean(array, long1, vmid, boolean1);
                try {
                    streamRemoteCall.getResultStream(true);
                    return;
                }
                catch (final IOException ex2) {
                    throw new MarshalException("error marshalling return", ex2);
                }
            }
            case 1: {
                ObjID[] array2;
                long long2;
                Lease lease;
                try {
                    final ObjectInput inputStream2 = streamRemoteCall.getInputStream();
                    array2 = (ObjID[])inputStream2.readObject();
                    long2 = inputStream2.readLong();
                    lease = (Lease)inputStream2.readObject();
                }
                catch (final ClassCastException | IOException | ClassNotFoundException ex3) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex3);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                final Lease dirty = dgcImpl.dirty(array2, long2, lease);
                try {
                    streamRemoteCall.getResultStream(true).writeObject(dirty);
                    return;
                }
                catch (final IOException ex4) {
                    throw new MarshalException("error marshalling return", ex4);
                }
                break;
            }
        }
        throw new UnmarshalException("invalid method number");
    }
    
    static {
        operations = new Operation[] { new Operation("void clean(java.rmi.server.ObjID[], long, java.rmi.dgc.VMID, boolean)"), new Operation("java.rmi.dgc.Lease dirty(java.rmi.server.ObjID[], long, java.rmi.dgc.Lease)") };
    }
}
