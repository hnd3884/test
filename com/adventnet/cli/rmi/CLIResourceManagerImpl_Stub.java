package com.adventnet.cli.rmi;

import java.rmi.MarshalException;
import java.io.IOException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.Operation;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class CLIResourceManagerImpl_Stub extends RemoteStub implements CLIResourceManager, Remote
{
    private static final Operation[] operations;
    private static final long interfaceHash = 174905849328115593L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_closeAllConnections_0;
    private static Method $method_getKeepAliveTimeout_1;
    private static Method $method_getMaxConnections_2;
    private static Method $method_getSystemWideMaxConnections_3;
    private static Method $method_isSetPooling_4;
    private static Method $method_setKeepAliveTimeout_5;
    private static Method $method_setMaxConnections_6;
    private static Method $method_setPooling_7;
    private static Method $method_setSystemWideMaxConnections_8;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$adventnet$cli$rmi$CLIResourceManager;
    
    static {
        operations = new Operation[] { new Operation("void closeAllConnections()"), new Operation("int getKeepAliveTimeout()"), new Operation("int getMaxConnections()"), new Operation("int getSystemWideMaxConnections()"), new Operation("boolean isSetPooling()"), new Operation("void setKeepAliveTimeout(int)"), new Operation("void setMaxConnections(int)"), new Operation("void setPooling(boolean)"), new Operation("void setSystemWideMaxConnections(int)") };
        try {
            ((CLIResourceManagerImpl_Stub.class$java$rmi$server$RemoteRef != null) ? CLIResourceManagerImpl_Stub.class$java$rmi$server$RemoteRef : (CLIResourceManagerImpl_Stub.class$java$rmi$server$RemoteRef = class$("java.rmi.server.RemoteRef"))).getMethod("invoke", (CLIResourceManagerImpl_Stub.class$java$rmi$Remote != null) ? CLIResourceManagerImpl_Stub.class$java$rmi$Remote : (CLIResourceManagerImpl_Stub.class$java$rmi$Remote = class$("java.rmi.Remote")), (CLIResourceManagerImpl_Stub.class$java$lang$reflect$Method != null) ? CLIResourceManagerImpl_Stub.class$java$lang$reflect$Method : (CLIResourceManagerImpl_Stub.class$java$lang$reflect$Method = class$("java.lang.reflect.Method")), (CLIResourceManagerImpl_Stub.array$Ljava$lang$Object != null) ? CLIResourceManagerImpl_Stub.array$Ljava$lang$Object : (CLIResourceManagerImpl_Stub.array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), Long.TYPE);
            CLIResourceManagerImpl_Stub.useNewInvoke = true;
            CLIResourceManagerImpl_Stub.$method_closeAllConnections_0 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("closeAllConnections", (Class[])new Class[0]);
            CLIResourceManagerImpl_Stub.$method_getKeepAliveTimeout_1 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("getKeepAliveTimeout", (Class[])new Class[0]);
            CLIResourceManagerImpl_Stub.$method_getMaxConnections_2 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("getMaxConnections", (Class[])new Class[0]);
            CLIResourceManagerImpl_Stub.$method_getSystemWideMaxConnections_3 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("getSystemWideMaxConnections", (Class[])new Class[0]);
            CLIResourceManagerImpl_Stub.$method_isSetPooling_4 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("isSetPooling", (Class[])new Class[0]);
            CLIResourceManagerImpl_Stub.$method_setKeepAliveTimeout_5 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("setKeepAliveTimeout", Integer.TYPE);
            CLIResourceManagerImpl_Stub.$method_setMaxConnections_6 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("setMaxConnections", Integer.TYPE);
            CLIResourceManagerImpl_Stub.$method_setPooling_7 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("setPooling", Boolean.TYPE);
            CLIResourceManagerImpl_Stub.$method_setSystemWideMaxConnections_8 = ((CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager != null) ? CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager : (CLIResourceManagerImpl_Stub.class$com$adventnet$cli$rmi$CLIResourceManager = class$("com.adventnet.cli.rmi.CLIResourceManager"))).getMethod("setSystemWideMaxConnections", Integer.TYPE);
        }
        catch (final NoSuchMethodException ex) {
            CLIResourceManagerImpl_Stub.useNewInvoke = false;
        }
    }
    
    public CLIResourceManagerImpl_Stub() {
    }
    
    public CLIResourceManagerImpl_Stub(final RemoteRef remoteRef) {
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
    
    public void closeAllConnections() throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_closeAllConnections_0, null, 5714694664026780380L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 0, 174905849328115593L);
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public int getKeepAliveTimeout() throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_getKeepAliveTimeout_1, null, 9049373075317492944L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 1, 174905849328115593L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
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
    
    public int getMaxConnections() throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_getMaxConnections_2, null, -6614033420789451520L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 2, 174905849328115593L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
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
    
    public int getSystemWideMaxConnections() throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_getSystemWideMaxConnections_3, null, 4301691336213711973L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 3, 174905849328115593L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
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
    
    public boolean isSetPooling() throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                return (boolean)super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_isSetPooling_4, null, 8716287632483877530L);
            }
            final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 4, 174905849328115593L);
            super.ref.invoke(call);
            boolean boolean1;
            try {
                boolean1 = call.getInputStream().readBoolean();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return boolean1;
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
    
    public void setKeepAliveTimeout(final int n) throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_setKeepAliveTimeout_5, new Object[] { new Integer(n) }, 9212739374310597693L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 5, 174905849328115593L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
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
    
    public void setMaxConnections(final int n) throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_setMaxConnections_6, new Object[] { new Integer(n) }, -3216999682298717359L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 6, 174905849328115593L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
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
    
    public void setPooling(final boolean b) throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_setPooling_7, new Object[] { new Boolean(b) }, 3910746245484721574L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 7, 174905849328115593L);
                try {
                    call.getOutputStream().writeBoolean(b);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
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
    
    public void setSystemWideMaxConnections(final int n) throws RemoteException {
        try {
            if (CLIResourceManagerImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLIResourceManagerImpl_Stub.$method_setSystemWideMaxConnections_8, new Object[] { new Integer(n) }, -5549271102658825530L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLIResourceManagerImpl_Stub.operations, 8, 174905849328115593L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
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
}
