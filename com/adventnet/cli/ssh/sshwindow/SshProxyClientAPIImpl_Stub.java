package com.adventnet.cli.ssh.sshwindow;

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
import com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI;
import java.rmi.server.RemoteStub;

public final class SshProxyClientAPIImpl_Stub extends RemoteStub implements TelnetProxyClientAPI, Remote
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
            ((SshProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef != null) ? SshProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef : (SshProxyClientAPIImpl_Stub.class$java$rmi$server$RemoteRef = class$("java.rmi.server.RemoteRef"))).getMethod("invoke", (SshProxyClientAPIImpl_Stub.class$java$rmi$Remote != null) ? SshProxyClientAPIImpl_Stub.class$java$rmi$Remote : (SshProxyClientAPIImpl_Stub.class$java$rmi$Remote = class$("java.rmi.Remote")), (SshProxyClientAPIImpl_Stub.class$java$lang$reflect$Method != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$reflect$Method : (SshProxyClientAPIImpl_Stub.class$java$lang$reflect$Method = class$("java.lang.reflect.Method")), (SshProxyClientAPIImpl_Stub.array$Ljava$lang$Object != null) ? SshProxyClientAPIImpl_Stub.array$Ljava$lang$Object : (SshProxyClientAPIImpl_Stub.array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), Long.TYPE);
            SshProxyClientAPIImpl_Stub.useNewInvoke = true;
            SshProxyClientAPIImpl_Stub.$method_connect_0 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("connect", (SshProxyClientAPIImpl_Stub.class$java$lang$String != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$String : (SshProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), Integer.TYPE);
            SshProxyClientAPIImpl_Stub.$method_connect_1 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("connect", (SshProxyClientAPIImpl_Stub.class$java$lang$String != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$String : (SshProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), Integer.TYPE, (SshProxyClientAPIImpl_Stub.class$java$lang$String != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$String : (SshProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            SshProxyClientAPIImpl_Stub.$method_disconnect_2 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("disconnect", Integer.TYPE);
            SshProxyClientAPIImpl_Stub.$method_login_3 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("login", Integer.TYPE, (SshProxyClientAPIImpl_Stub.class$java$lang$String != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$String : (SshProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")), (SshProxyClientAPIImpl_Stub.class$java$lang$String != null) ? SshProxyClientAPIImpl_Stub.class$java$lang$String : (SshProxyClientAPIImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            SshProxyClientAPIImpl_Stub.$method_read_4 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("read", Integer.TYPE, (SshProxyClientAPIImpl_Stub.array$B != null) ? SshProxyClientAPIImpl_Stub.array$B : (SshProxyClientAPIImpl_Stub.array$B = class$("[B")));
            SshProxyClientAPIImpl_Stub.$method_readAsByteArray_5 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("readAsByteArray", Integer.TYPE);
            SshProxyClientAPIImpl_Stub.$method_setSocketTimeout_6 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("setSocketTimeout", Integer.TYPE, Integer.TYPE);
            SshProxyClientAPIImpl_Stub.$method_write_7 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("write", Integer.TYPE, Byte.TYPE);
            SshProxyClientAPIImpl_Stub.$method_write_8 = ((SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI != null) ? SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI : (SshProxyClientAPIImpl_Stub.class$com$adventnet$telnet$telnetwindow$TelnetProxyClientAPI = class$("com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI"))).getMethod("write", Integer.TYPE, (SshProxyClientAPIImpl_Stub.array$B != null) ? SshProxyClientAPIImpl_Stub.array$B : (SshProxyClientAPIImpl_Stub.array$B = class$("[B")));
        }
        catch (final NoSuchMethodException ex) {
            SshProxyClientAPIImpl_Stub.useNewInvoke = false;
        }
    }
    
    public SshProxyClientAPIImpl_Stub() {
    }
    
    public SshProxyClientAPIImpl_Stub(final RemoteRef remoteRef) {
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_connect_0, new Object[] { s, new Integer(n) }, 48749161004939641L);
            }
            final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 0, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_connect_1, new Object[] { s, new Integer(n), s2 }, 7403871753764022759L);
            }
            final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 1, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_disconnect_2, new Object[] { new Integer(n) }, 3674334102525678583L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 2, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_login_3, new Object[] { new Integer(n), s, s2 }, -5233700984916094130L);
            }
            final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 3, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_read_4, new Object[] { new Integer(n), array }, 1097993668938678719L);
            }
            final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 4, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                return (byte[])super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_readAsByteArray_5, new Object[] { new Integer(n) }, -4467217384959979745L);
            }
            final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 5, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_setSocketTimeout_6, new Object[] { new Integer(n), new Integer(n2) }, -6726805466994499671L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 6, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_write_7, new Object[] { new Integer(n), new Byte(b) }, 3309430870527555919L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 7, 8499256766154638547L);
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
            if (SshProxyClientAPIImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, SshProxyClientAPIImpl_Stub.$method_write_8, new Object[] { new Integer(n), array }, -6047237704507632803L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, SshProxyClientAPIImpl_Stub.operations, 8, 8499256766154638547L);
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
