package sun.rmi.server;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.rmi.server.RemoteCall;
import java.rmi.server.Operation;
import java.rmi.server.RemoteObject;
import sun.misc.SharedSecrets;
import java.io.ObjectInputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import sun.rmi.transport.Connection;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.io.IOException;
import java.rmi.MarshalException;
import sun.rmi.transport.StreamRemoteCall;
import java.lang.reflect.Method;
import java.rmi.Remote;
import sun.rmi.transport.LiveRef;
import sun.rmi.runtime.Log;
import java.rmi.server.RemoteRef;

public class UnicastRef implements RemoteRef
{
    public static final Log clientRefLog;
    public static final Log clientCallLog;
    private static final long serialVersionUID = 8258372400816541186L;
    protected LiveRef ref;
    
    public UnicastRef() {
    }
    
    public UnicastRef(final LiveRef ref) {
        this.ref = ref;
    }
    
    public LiveRef getLiveRef() {
        return this.ref;
    }
    
    @Override
    public Object invoke(final Remote remote, final Method method, final Object[] array, final long n) throws Exception {
        if (UnicastRef.clientRefLog.isLoggable(Log.VERBOSE)) {
            UnicastRef.clientRefLog.log(Log.VERBOSE, "method: " + method);
        }
        if (UnicastRef.clientCallLog.isLoggable(Log.VERBOSE)) {
            this.logClientCall(remote, method);
        }
        final Connection connection = this.ref.getChannel().newConnection();
        StreamRemoteCall streamRemoteCall = null;
        boolean b = true;
        boolean b2 = false;
        try {
            if (UnicastRef.clientRefLog.isLoggable(Log.VERBOSE)) {
                UnicastRef.clientRefLog.log(Log.VERBOSE, "opnum = " + n);
            }
            streamRemoteCall = new StreamRemoteCall(connection, this.ref.getObjID(), -1, n);
            try {
                final ObjectOutput outputStream = streamRemoteCall.getOutputStream();
                this.marshalCustomCallData(outputStream);
                final Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; ++i) {
                    marshalValue(parameterTypes[i], array[i], outputStream);
                }
            }
            catch (final IOException ex) {
                UnicastRef.clientRefLog.log(Log.BRIEF, "IOException marshalling arguments: ", ex);
                throw new MarshalException("error marshalling arguments", ex);
            }
            streamRemoteCall.executeCall();
            try {
                final Class<?> returnType = method.getReturnType();
                if (returnType == Void.TYPE) {
                    return null;
                }
                final Object unmarshalValue = unmarshalValue(returnType, streamRemoteCall.getInputStream());
                b2 = true;
                UnicastRef.clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
                this.ref.getChannel().free(connection, true);
                return unmarshalValue;
            }
            catch (final IOException | ClassNotFoundException ex2) {
                streamRemoteCall.discardPendingRefs();
                UnicastRef.clientRefLog.log(Log.BRIEF, ex2.getClass().getName() + " unmarshalling return: ", (Throwable)ex2);
                throw new UnmarshalException("error unmarshalling return", (Exception)ex2);
            }
            finally {
                try {
                    streamRemoteCall.done();
                }
                catch (final IOException ex3) {
                    b = false;
                }
            }
        }
        catch (final RuntimeException ex4) {
            if (streamRemoteCall == null || streamRemoteCall.getServerException() != ex4) {
                b = false;
            }
            throw ex4;
        }
        catch (final RemoteException ex5) {
            b = false;
            throw ex5;
        }
        catch (final Error error) {
            b = false;
            throw error;
        }
        finally {
            if (!b2) {
                if (UnicastRef.clientRefLog.isLoggable(Log.BRIEF)) {
                    UnicastRef.clientRefLog.log(Log.BRIEF, "free connection (reuse = " + b + ")");
                }
                this.ref.getChannel().free(connection, b);
            }
        }
    }
    
    protected void marshalCustomCallData(final ObjectOutput objectOutput) throws IOException {
    }
    
    protected static void marshalValue(final Class<?> clazz, final Object o, final ObjectOutput objectOutput) throws IOException {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                objectOutput.writeInt((int)o);
            }
            else if (clazz == Boolean.TYPE) {
                objectOutput.writeBoolean((boolean)o);
            }
            else if (clazz == Byte.TYPE) {
                objectOutput.writeByte((byte)o);
            }
            else if (clazz == Character.TYPE) {
                objectOutput.writeChar((char)o);
            }
            else if (clazz == Short.TYPE) {
                objectOutput.writeShort((short)o);
            }
            else if (clazz == Long.TYPE) {
                objectOutput.writeLong((long)o);
            }
            else if (clazz == Float.TYPE) {
                objectOutput.writeFloat((float)o);
            }
            else {
                if (clazz != Double.TYPE) {
                    throw new Error("Unrecognized primitive type: " + clazz);
                }
                objectOutput.writeDouble((double)o);
            }
        }
        else {
            objectOutput.writeObject(o);
        }
    }
    
    protected static Object unmarshalValue(final Class<?> clazz, final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return objectInput.readInt();
            }
            if (clazz == Boolean.TYPE) {
                return objectInput.readBoolean();
            }
            if (clazz == Byte.TYPE) {
                return objectInput.readByte();
            }
            if (clazz == Character.TYPE) {
                return objectInput.readChar();
            }
            if (clazz == Short.TYPE) {
                return objectInput.readShort();
            }
            if (clazz == Long.TYPE) {
                return objectInput.readLong();
            }
            if (clazz == Float.TYPE) {
                return objectInput.readFloat();
            }
            if (clazz == Double.TYPE) {
                return objectInput.readDouble();
            }
            throw new Error("Unrecognized primitive type: " + clazz);
        }
        else {
            if (clazz == String.class && objectInput instanceof ObjectInputStream) {
                return SharedSecrets.getJavaObjectInputStreamReadString().readString((ObjectInputStream)objectInput);
            }
            return objectInput.readObject();
        }
    }
    
    @Override
    public RemoteCall newCall(final RemoteObject remoteObject, final Operation[] array, final int n, final long n2) throws RemoteException {
        UnicastRef.clientRefLog.log(Log.BRIEF, "get connection");
        final Connection connection = this.ref.getChannel().newConnection();
        try {
            UnicastRef.clientRefLog.log(Log.VERBOSE, "create call context");
            if (UnicastRef.clientCallLog.isLoggable(Log.VERBOSE)) {
                this.logClientCall(remoteObject, array[n]);
            }
            final StreamRemoteCall streamRemoteCall = new StreamRemoteCall(connection, this.ref.getObjID(), n, n2);
            try {
                this.marshalCustomCallData(streamRemoteCall.getOutputStream());
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshaling custom call data");
            }
            return streamRemoteCall;
        }
        catch (final RemoteException ex2) {
            this.ref.getChannel().free(connection, false);
            throw ex2;
        }
    }
    
    @Override
    public void invoke(final RemoteCall remoteCall) throws Exception {
        try {
            UnicastRef.clientRefLog.log(Log.VERBOSE, "execute call");
            remoteCall.executeCall();
        }
        catch (final RemoteException ex) {
            UnicastRef.clientRefLog.log(Log.BRIEF, "exception: ", ex);
            this.free(remoteCall, false);
            throw ex;
        }
        catch (final Error error) {
            UnicastRef.clientRefLog.log(Log.BRIEF, "error: ", error);
            this.free(remoteCall, false);
            throw error;
        }
        catch (final RuntimeException ex2) {
            UnicastRef.clientRefLog.log(Log.BRIEF, "exception: ", ex2);
            this.free(remoteCall, false);
            throw ex2;
        }
        catch (final Exception ex3) {
            UnicastRef.clientRefLog.log(Log.BRIEF, "exception: ", ex3);
            this.free(remoteCall, true);
            throw ex3;
        }
    }
    
    private void free(final RemoteCall remoteCall, final boolean b) throws RemoteException {
        this.ref.getChannel().free(((StreamRemoteCall)remoteCall).getConnection(), b);
    }
    
    @Override
    public void done(final RemoteCall remoteCall) throws RemoteException {
        UnicastRef.clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
        this.free(remoteCall, true);
        try {
            remoteCall.done();
        }
        catch (final IOException ex) {}
    }
    
    void logClientCall(final Object o, final Object o2) {
        UnicastRef.clientCallLog.log(Log.VERBOSE, "outbound call: " + this.ref + " : " + o.getClass().getName() + this.ref.getObjID().toString() + ": " + o2);
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "UnicastRef";
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.ref.write(objectOutput, false);
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.ref = LiveRef.read(objectInput, false);
    }
    
    @Override
    public String remoteToString() {
        return Util.getUnqualifiedName(this.getClass()) + " [liveRef: " + this.ref + "]";
    }
    
    @Override
    public int remoteHashCode() {
        return this.ref.hashCode();
    }
    
    @Override
    public boolean remoteEquals(final RemoteRef remoteRef) {
        return remoteRef instanceof UnicastRef && this.ref.remoteEquals(((UnicastRef)remoteRef).ref);
    }
    
    static {
        clientRefLog = Log.getLog("sun.rmi.client.ref", "transport", Util.logLevel);
        clientCallLog = Log.getLog("sun.rmi.client.call", "RMI", AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.client.logCalls")));
    }
}
