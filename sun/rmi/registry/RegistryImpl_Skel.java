package sun.rmi.registry;

import java.rmi.MarshalException;
import java.io.IOException;
import sun.misc.SharedSecrets;
import java.io.ObjectInputStream;
import sun.rmi.transport.StreamRemoteCall;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class RegistryImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = 4905912898345647071L;
    
    @Override
    public Operation[] getOperations() {
        return RegistryImpl_Skel.operations.clone();
    }
    
    @Override
    public void dispatch(final Remote remote, final RemoteCall remoteCall, int n, final long n2) throws Exception {
        if (n < 0) {
            if (n2 == 7583982177005850366L) {
                n = 0;
            }
            else if (n2 == 2571371476350237748L) {
                n = 1;
            }
            else if (n2 == -7538657168040752697L) {
                n = 2;
            }
            else if (n2 == -8381844669958460146L) {
                n = 3;
            }
            else {
                if (n2 != 7305022919901907578L) {
                    throw new UnmarshalException("invalid method hash");
                }
                n = 4;
            }
        }
        else if (n2 != 4905912898345647071L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final RegistryImpl registryImpl = (RegistryImpl)remote;
        final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)remoteCall;
        switch (n) {
            case 0: {
                RegistryImpl.checkAccess("Registry.bind");
                String string;
                Remote remote2;
                try {
                    final ObjectInputStream objectInputStream = (ObjectInputStream)streamRemoteCall.getInputStream();
                    string = SharedSecrets.getJavaObjectInputStreamReadString().readString(objectInputStream);
                    remote2 = (Remote)objectInputStream.readObject();
                }
                catch (final ClassCastException | IOException | ClassNotFoundException ex) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                registryImpl.bind(string, remote2);
                try {
                    streamRemoteCall.getResultStream(true);
                    return;
                }
                catch (final IOException ex2) {
                    throw new MarshalException("error marshalling return", ex2);
                }
            }
            case 1: {
                streamRemoteCall.releaseInputStream();
                final String[] list = registryImpl.list();
                try {
                    streamRemoteCall.getResultStream(true).writeObject(list);
                    return;
                }
                catch (final IOException ex3) {
                    throw new MarshalException("error marshalling return", ex3);
                }
            }
            case 2: {
                String string2;
                try {
                    string2 = SharedSecrets.getJavaObjectInputStreamReadString().readString((ObjectInputStream)streamRemoteCall.getInputStream());
                }
                catch (final ClassCastException | IOException ex4) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex4);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                final Remote lookup = registryImpl.lookup(string2);
                try {
                    streamRemoteCall.getResultStream(true).writeObject(lookup);
                    return;
                }
                catch (final IOException ex5) {
                    throw new MarshalException("error marshalling return", ex5);
                }
            }
            case 3: {
                RegistryImpl.checkAccess("Registry.rebind");
                String string3;
                Remote remote3;
                try {
                    final ObjectInputStream objectInputStream2 = (ObjectInputStream)streamRemoteCall.getInputStream();
                    string3 = SharedSecrets.getJavaObjectInputStreamReadString().readString(objectInputStream2);
                    remote3 = (Remote)objectInputStream2.readObject();
                }
                catch (final ClassCastException | IOException | ClassNotFoundException ex6) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex6);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                registryImpl.rebind(string3, remote3);
                try {
                    streamRemoteCall.getResultStream(true);
                    return;
                }
                catch (final IOException ex7) {
                    throw new MarshalException("error marshalling return", ex7);
                }
            }
            case 4: {
                RegistryImpl.checkAccess("Registry.unbind");
                String string4;
                try {
                    string4 = SharedSecrets.getJavaObjectInputStreamReadString().readString((ObjectInputStream)streamRemoteCall.getInputStream());
                }
                catch (final ClassCastException | IOException ex8) {
                    streamRemoteCall.discardPendingRefs();
                    throw new UnmarshalException("error unmarshalling arguments", (Exception)ex8);
                }
                finally {
                    streamRemoteCall.releaseInputStream();
                }
                registryImpl.unbind(string4);
                try {
                    streamRemoteCall.getResultStream(true);
                    return;
                }
                catch (final IOException ex9) {
                    throw new MarshalException("error marshalling return", ex9);
                }
                break;
            }
        }
        throw new UnmarshalException("invalid method number");
    }
    
    static {
        operations = new Operation[] { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
    }
}
