package sun.rmi.registry;

import java.rmi.NotBoundException;
import java.rmi.UnmarshalException;
import java.rmi.AccessException;
import java.io.ObjectOutput;
import java.rmi.UnexpectedException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteCall;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.RemoteObject;
import sun.rmi.transport.StreamRemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.Operation;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;

public final class RegistryImpl_Stub extends RemoteStub implements Registry, Remote
{
    private static final Operation[] operations;
    private static final long interfaceHash = 4905912898345647071L;
    
    public RegistryImpl_Stub() {
    }
    
    public RegistryImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    @Override
    public void bind(final String s, final Remote remote) throws AccessException, AlreadyBoundException, RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, RegistryImpl_Stub.operations, 0, 4905912898345647071L);
            try {
                final ObjectOutput outputStream = streamRemoteCall.getOutputStream();
                outputStream.writeObject(s);
                outputStream.writeObject(remote);
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
        catch (final AlreadyBoundException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    @Override
    public String[] list() throws AccessException, RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, RegistryImpl_Stub.operations, 1, 4905912898345647071L);
            this.ref.invoke(streamRemoteCall);
            String[] array;
            try {
                array = (String[])streamRemoteCall.getInputStream().readObject();
            }
            catch (final ClassCastException | IOException | ClassNotFoundException ex) {
                streamRemoteCall.discardPendingRefs();
                throw new UnmarshalException("error unmarshalling return", (Exception)ex);
            }
            finally {
                this.ref.done(streamRemoteCall);
            }
            return array;
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
    public Remote lookup(final String s) throws AccessException, NotBoundException, RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, RegistryImpl_Stub.operations, 2, 4905912898345647071L);
            try {
                final Object outputStream = streamRemoteCall.getOutputStream();
                ((ObjectOutput)outputStream).writeObject(s);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            this.ref.invoke(streamRemoteCall);
            Object outputStream;
            try {
                outputStream = streamRemoteCall.getInputStream().readObject();
            }
            catch (final ClassCastException | IOException | ClassNotFoundException ex2) {
                streamRemoteCall.discardPendingRefs();
                throw new UnmarshalException("error unmarshalling return", (Exception)ex2);
            }
            finally {
                this.ref.done(streamRemoteCall);
            }
            return (Remote)outputStream;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final NotBoundException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    @Override
    public void rebind(final String s, final Remote remote) throws AccessException, RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, RegistryImpl_Stub.operations, 3, 4905912898345647071L);
            try {
                final ObjectOutput outputStream = streamRemoteCall.getOutputStream();
                outputStream.writeObject(s);
                outputStream.writeObject(remote);
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
    public void unbind(final String s) throws AccessException, NotBoundException, RemoteException {
        try {
            final StreamRemoteCall streamRemoteCall = (StreamRemoteCall)this.ref.newCall(this, RegistryImpl_Stub.operations, 4, 4905912898345647071L);
            try {
                streamRemoteCall.getOutputStream().writeObject(s);
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
        catch (final NotBoundException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    static {
        operations = new Operation[] { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
    }
}
