package com.adventnet.telnet.telnetwindow;

import java.io.DataOutput;
import java.rmi.RemoteException;
import java.io.ObjectOutput;
import java.rmi.server.RemoteCall;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.Operation;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class TelnetProxyClientAPIImpl_Stub extends RemoteStub implements TelnetProxyClientAPI, Remote
{
    private static final Operation[] operations;
    private static final long interfaceHash = 8499256766154638547L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_connect_0;
    private static Method $method_connect_1;
    private static Method $method_disconnect_2;
    private static Method $method_login_3;
    private static Method $method_read_4;
    private static Method $method_readAsByteArray_5;
    private static Method $method_setSocketTimeout_6;
    private static Method $method_write_7;
    private static Method $method_write_8;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class array$B;
    
    static {
        operations = new Operation[] { new Operation("int connect(java.lang.String, int)"), new Operation("int connect(java.lang.String, int, java.lang.String)"), new Operation("void disconnect(int)"), new Operation("java.lang.String login(int, java.lang.String, java.lang.String)"), new Operation("int read(int, byte[])"), new Operation("byte readAsByteArray(int)[]"), new Operation("void setSocketTimeout(int, int)"), new Operation("void write(int, byte)"), new Operation("void write(int, byte[])") };
        try {
            ((TelnetProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef != null) ? TelnetProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef : (TelnetProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef = class$("java.rmi.server.RemoteRef"))).getMethod("invoke", (TelnetProxyClientAPIImpl_Stub.class$java$rmi$Remote != null) ? TelnetProxyClientAPIImpl_Stub.class$java$rmi$Remote : (TelnetProxyClientAPIImpl_Stub.class$java$rmi$Remote = class$("java.rmi.Remote")), (TelnetProxyClientAPIImpl_Stub.class$java$lang$reflect$Method != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$reflect$Method : (TelnetProxyClientAPIImpl_Stub.class$java$lang$reflect$Method = class$("java.lang.reflect.Method")), (TelnetProxyClientAPIImpl_Stub.array$Ljava$lang$Object != null) ? TelnetProxyClientAPIImpl_Stub.array$Ljava$lang$Object : (TelnetProxyClientAPIImpl_Stub.array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), Long.TYPE);
            TelnetProxyClientAPIImpl_Stub.useNewInvoke = true;
            TelnetProxyClientAPIImpl_Stub.$method_connect_0 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("connect", (TelnetProxyClientAPIImpl_Stub.class$java$lang$String != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$String : (TelnetProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), Integer.TYPE);
            TelnetProxyClientAPIImpl_Stub.$method_connect_1 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("connect", (TelnetProxyClientAPIImpl_Stub.class$java$lang$String != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$String : (TelnetProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), Integer.TYPE, (TelnetProxyClientAPIImpl_Stub.class$java$lang$String != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$String : (TelnetProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            TelnetProxyClientAPIImpl_Stub.$method_disconnect_2 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("disconnect", Integer.TYPE);
            TelnetProxyClientAPIImpl_Stub.$method_login_3 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("login", Integer.TYPE, (TelnetProxyClientAPIImpl_Stub.class$java$lang$String != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$String : (TelnetProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), (TelnetProxyClientAPIImpl_Stub.class$java$lang$String != null) ? TelnetProxyClientAPIImpl_Stub.class$java$lang$String : (TelnetProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            TelnetProxyClientAPIImpl_Stub.$method_read_4 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("read", Integer.TYPE, (TelnetProxyClientAPIImpl_Stub.array$B != null) ? TelnetProxyClientAPIImpl_Stub.array$B : (TelnetProxyClientAPIImpl_Stub.array$B = class$("[B")));
            TelnetProxyClientAPIImpl_Stub.$method_readAsByteArray_5 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("readAsByteArray", Integer.TYPE);
            TelnetProxyClientAPIImpl_Stub.$method_setSocketTimeout_6 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("setSocketTimeout", Integer.TYPE, Integer.TYPE);
            TelnetProxyClientAPIImpl_Stub.$method_write_7 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("write", Integer.TYPE, Byte.TYPE);
            TelnetProxyClientAPIImpl_Stub.$method_write_8 = ((TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (TelnetProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("write", Integer.TYPE, (TelnetProxyClientAPIImpl_Stub.array$B != null) ? TelnetProxyClientAPIImpl_Stub.array$B : (TelnetProxyClientAPIImpl_Stub.array$B = class$("[B")));
        }
        catch (final NoSuchMethodException ex) {
            TelnetProxyClientAPIImpl_Stub.useNewInvoke = false;
        }
    }
    
    public TelnetProxyClientAPIImpl_Stub() {
    }
    
    public TelnetProxyClientAPIImpl_Stub(final RemoteRef remoteRef) {
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
    
    public int connect(final String s, final int n) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_connect_0, new Object[] { s, new Integer(n) }, 48749161004939641L);
            }
            final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 0, 8499256766154638547L);
            try {
                final ObjectOutput outputStream = call.getOutputStream();
                outputStream.writeObject(s);
                outputStream.writeInt(n);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final IOException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public int connect(final String s, final int n, final String s2) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_connect_1, new Object[] { s, new Integer(n), s2 }, 7403871753764022759L);
            }
            final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 1, 8499256766154638547L);
            try {
                final ObjectOutput outputStream = call.getOutputStream();
                outputStream.writeObject(s);
                outputStream.writeInt(n);
                outputStream.writeObject(s2);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final IOException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void disconnect(final int n) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_disconnect_2, new Object[] { new Integer(n) }, 3674334102525678583L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 2, 8499256766154638547L);
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
        catch (final IOException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public String login(final int n, final String s, final String s2) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_login_3, new Object[] { new Integer(n), s, s2 }, -5233700984916094130L);
            }
            final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 3, 8499256766154638547L);
            try {
                final Object outputStream = call.getOutputStream();
                ((DataOutput)outputStream).writeInt(n);
                ((ObjectOutput)outputStream).writeObject(s);
                ((ObjectOutput)outputStream).writeObject(s2);
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
            return (String)outputStream;
        }
        catch (final RuntimeException ex4) {
            throw ex4;
        }
        catch (final IOException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    public int read(final int n, final byte[] array) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_read_4, new Object[] { new Integer(n), array }, 1097993668938678719L);
            }
            final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 4, 8499256766154638547L);
            try {
                final ObjectOutput outputStream = call.getOutputStream();
                outputStream.writeInt(n);
                outputStream.writeObject(array);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final IOException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public byte[] readAsByteArray(final int n) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                return (byte[])super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_readAsByteArray_5, new Object[] { new Integer(n) }, -4467217384959979745L);
            }
            final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 5, 8499256766154638547L);
            try {
                final Object outputStream = call.getOutputStream();
                ((DataOutput)outputStream).writeInt(n);
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
            return (byte[])outputStream;
        }
        catch (final RuntimeException ex4) {
            throw ex4;
        }
        catch (final IOException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    public void setSocketTimeout(final int n, final int n2) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_setSocketTimeout_6, new Object[] { new Integer(n), new Integer(n2) }, -6726805466994499671L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 6, 8499256766154638547L);
                try {
                    final ObjectOutput outputStream = call.getOutputStream();
                    outputStream.writeInt(n);
                    outputStream.writeInt(n2);
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
        catch (final IOException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void write(final int n, final byte b) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_write_7, new Object[] { new Integer(n), new Byte(b) }, 3309430870527555919L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 7, 8499256766154638547L);
                try {
                    final ObjectOutput outputStream = call.getOutputStream();
                    outputStream.writeInt(n);
                    outputStream.writeByte(b);
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
        catch (final IOException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void write(final int n, final byte[] array) throws IOException, RemoteException {
        try {
            if (TelnetProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, TelnetProxyClientAPIImpl_Stub.$method_write_8, new Object[] { new Integer(n), array }, -6047237704507632803L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, TelnetProxyClientAPIImpl_Stub.operations, 8, 8499256766154638547L);
                try {
                    final ObjectOutput outputStream = call.getOutputStream();
                    outputStream.writeInt(n);
                    outputStream.writeObject(array);
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
        catch (final IOException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
}
