package com.adventnet.cli.rmi;

import java.io.DataOutput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.server.RemoteCall;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.io.IOException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.Operation;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class CLIFactoryImpl_Stub extends RemoteStub implements CLIFactory, Remote
{
    private static final Operation[] operations;
    private static final long interfaceHash = 4102910071669140523L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_createCLIResourceManager_0;
    private static Method $method_createCLISession_1;
    private static Method $method_createCLISession_2;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$adventnet$cli$rmi$CLIFactory;
    static /* synthetic */ Class class$com$adventnet$cli$transport$CLIProtocolOptions;
    
    static {
        operations = new Operation[] { new Operation("com.adventnet.cli.rmi.CLIResourceManager createCLIResourceManager()"), new Operation("com.adventnet.cli.rmi.CLISession createCLISession(com.adventnet.cli.transport.CLIProtocolOptions)"), new Operation("com.adventnet.cli.rmi.CLISession createCLISession(com.adventnet.cli.transport.CLIProtocolOptions, boolean)") };
        try {
            ((CLIFactoryImpl_Stub.class$java$rmi$server$RemoteRef != null) ? CLIFactoryImpl_Stub.class$java$rmi$server$RemoteRef : (CLIFactoryImpl_Stub.class$java$rmi$server$RemoteRef = class$("java.rmi.server.RemoteRef"))).getMethod("invoke", (CLIFactoryImpl_Stub.class$java$rmi$Remote != null) ? CLIFactoryImpl_Stub.class$java$rmi$Remote : (CLIFactoryImpl_Stub.class$java$rmi$Remote = class$("java.rmi.Remote")), (CLIFactoryImpl_Stub.class$java$lang$reflect$Method != null) ? CLIFactoryImpl_Stub.class$java$lang$reflect$Method : (CLIFactoryImpl_Stub.class$java$lang$reflect$Method = class$("java.lang.reflect.Method")), (CLIFactoryImpl_Stub.array$Ljava$lang$Object != null) ? CLIFactoryImpl_Stub.array$Ljava$lang$Object : (CLIFactoryImpl_Stub.array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), Long.TYPE);
            CLIFactoryImpl_Stub.useNewInvoke = true;
            CLIFactoryImpl_Stub.$method_createCLIResourceManager_0 = ((CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory != null) ? CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory : (CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory = class$("com.adventnet.cli.rmi.CLIFactory"))).getMethod("createCLIResourceManager", (Class[])new Class[0]);
            CLIFactoryImpl_Stub.$method_createCLISession_1 = ((CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory != null) ? CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory : (CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory = class$("com.adventnet.cli.rmi.CLIFactory"))).getMethod("createCLISession", (CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions != null) ? CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions : (CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions = class$("com.adventnet.cli.transport.CLIProtocolOptions")));
            CLIFactoryImpl_Stub.$method_createCLISession_2 = ((CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory != null) ? CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory : (CLIFactoryImpl_Stub.class$com$adventnet$cli$rmi$CLIFactory = class$("com.adventnet.cli.rmi.CLIFactory"))).getMethod("createCLISession", (CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions != null) ? CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions : (CLIFactoryImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions = class$("com.adventnet.cli.transport.CLIProtocolOptions")), Boolean.TYPE);
        }
        catch (final NoSuchMethodException ex) {
            CLIFactoryImpl_Stub.useNewInvoke = false;
        }
    }
    
    public CLIFactoryImpl_Stub() {
    }
    
    public CLIFactoryImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public CLIResourceManager createCLIResourceManager() throws RemoteException {
        try {
            if (CLIFactoryImpl_Stub.useNewInvoke) {
                return (CLIResourceManager)super.ref.invoke(this, CLIFactoryImpl_Stub.$method_createCLIResourceManager_0, null, 88892228454991705L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIFactoryImpl_Stub.operations, 0, 4102910071669140523L);
            super.ref.invoke(call);
            CLIResourceManager cliResourceManager;
            try {
                cliResourceManager = (CLIResourceManager)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return cliResourceManager;
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
    
    public CLISession createCLISession(final CLIProtocolOptions cliProtocolOptions) throws RemoteException {
        try {
            if (CLIFactoryImpl_Stub.useNewInvoke) {
                return (CLISession)super.ref.invoke(this, CLIFactoryImpl_Stub.$method_createCLISession_1, new Object[] { cliProtocolOptions }, 3822035896352519788L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIFactoryImpl_Stub.operations, 1, 4102910071669140523L);
            try {
                final Object outputStream = call.getOutputStream();
                ((ObjectOutput)outputStream).writeObject(cliProtocolOptions);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            Object outputStream;
            try {
                outputStream = call.getInputStream().readObject();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            catch (final ClassNotFoundException ex3) {
                throw new UnmarshalException("error unmarshalling return", ex3);
            }
            finally {
                super.ref.done(call);
            }
            return (CLISession)outputStream;
        }
        catch (final RuntimeException ex4) {
            throw ex4;
        }
        catch (final RemoteException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    public CLISession createCLISession(final CLIProtocolOptions cliProtocolOptions, final boolean b) throws RemoteException {
        try {
            if (CLIFactoryImpl_Stub.useNewInvoke) {
                return (CLISession)super.ref.invoke(this, CLIFactoryImpl_Stub.$method_createCLISession_2, new Object[] { cliProtocolOptions, new Boolean(b) }, -7388077998818797900L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIFactoryImpl_Stub.operations, 2, 4102910071669140523L);
            try {
                final Object outputStream = call.getOutputStream();
                ((ObjectOutput)outputStream).writeObject(cliProtocolOptions);
                ((DataOutput)outputStream).writeBoolean(b);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            Object outputStream;
            try {
                outputStream = call.getInputStream().readObject();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            catch (final ClassNotFoundException ex3) {
                throw new UnmarshalException("error unmarshalling return", ex3);
            }
            finally {
                super.ref.done(call);
            }
            return (CLISession)outputStream;
        }
        catch (final RuntimeException ex4) {
            throw ex4;
        }
        catch (final RemoteException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
}
