package sun.rmi.server;

import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Collections;
import java.util.WeakHashMap;
import sun.security.action.GetBooleanAction;
import java.rmi.server.RemoteRef;
import java.io.PrintStream;
import java.util.Date;
import java.rmi.server.Operation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.rmi.ServerException;
import java.rmi.ServerError;
import java.rmi.MarshalException;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.rmi.AccessException;
import sun.rmi.transport.StreamRemoteCall;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.server.SkeletonNotFoundException;
import java.rmi.server.ServerNotActiveException;
import sun.rmi.transport.tcp.TCPTransport;
import sun.rmi.transport.Target;
import java.rmi.server.ExportException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.rmi.Remote;
import sun.rmi.transport.LiveRef;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Method;
import java.util.Map;
import sun.misc.ObjectInputFilter;
import java.rmi.server.Skeleton;
import sun.rmi.runtime.Log;
import java.rmi.server.ServerRef;

public class UnicastServerRef extends UnicastRef implements ServerRef, Dispatcher
{
    public static final boolean logCalls;
    public static final Log callLog;
    private static final long serialVersionUID = -7384275867073752268L;
    private static final boolean wantExceptionLog;
    private boolean forceStubUse;
    private static final boolean suppressStackTraces;
    private transient Skeleton skel;
    private final transient ObjectInputFilter filter;
    private transient Map<Long, Method> hashToMethod_Map;
    private static final WeakClassHashMap<Map<Long, Method>> hashToMethod_Maps;
    private static final Map<Class<?>, ?> withoutSkeletons;
    private final AtomicInteger methodCallIDCount;
    
    public UnicastServerRef() {
        this.forceStubUse = false;
        this.hashToMethod_Map = null;
        this.methodCallIDCount = new AtomicInteger(0);
        this.filter = null;
    }
    
    public UnicastServerRef(final LiveRef liveRef) {
        super(liveRef);
        this.forceStubUse = false;
        this.hashToMethod_Map = null;
        this.methodCallIDCount = new AtomicInteger(0);
        this.filter = null;
    }
    
    public UnicastServerRef(final LiveRef liveRef, final ObjectInputFilter filter) {
        super(liveRef);
        this.forceStubUse = false;
        this.hashToMethod_Map = null;
        this.methodCallIDCount = new AtomicInteger(0);
        this.filter = filter;
    }
    
    public UnicastServerRef(final int n) {
        super(new LiveRef(n));
        this.forceStubUse = false;
        this.hashToMethod_Map = null;
        this.methodCallIDCount = new AtomicInteger(0);
        this.filter = null;
    }
    
    public UnicastServerRef(final boolean forceStubUse) {
        this(0);
        this.forceStubUse = forceStubUse;
    }
    
    @Override
    public RemoteStub exportObject(final Remote remote, final Object o) throws RemoteException {
        this.forceStubUse = true;
        return (RemoteStub)this.exportObject(remote, o, false);
    }
    
    public Remote exportObject(final Remote skeleton, final Object o, final boolean b) throws RemoteException {
        final Class<? extends Remote> class1 = skeleton.getClass();
        Remote proxy;
        try {
            proxy = Util.createProxy(class1, this.getClientRef(), this.forceStubUse);
        }
        catch (final IllegalArgumentException ex) {
            throw new ExportException("remote object implements illegal remote interface", ex);
        }
        if (proxy instanceof RemoteStub) {
            this.setSkeleton(skeleton);
        }
        this.ref.exportObject(new Target(skeleton, this, proxy, this.ref.getObjID(), b));
        this.hashToMethod_Map = UnicastServerRef.hashToMethod_Maps.get(class1);
        return proxy;
    }
    
    @Override
    public String getClientHost() throws ServerNotActiveException {
        return TCPTransport.getClientHost();
    }
    
    public void setSkeleton(final Remote remote) throws RemoteException {
        if (!UnicastServerRef.withoutSkeletons.containsKey(remote.getClass())) {
            try {
                this.skel = Util.createSkeleton(remote);
            }
            catch (final SkeletonNotFoundException ex) {
                UnicastServerRef.withoutSkeletons.put(remote.getClass(), null);
            }
        }
    }
    
    @Override
    public void dispatch(final Remote remote, final RemoteCall remoteCall) throws IOException {
        try {
            ObjectInput inputStream;
            int int1;
            try {
                inputStream = remoteCall.getInputStream();
                int1 = inputStream.readInt();
            }
            catch (final Exception ex) {
                throw new UnmarshalException("error unmarshalling call header", ex);
            }
            if (this.skel != null) {
                this.oldDispatch(remote, remoteCall, int1);
                return;
            }
            if (int1 >= 0) {
                throw new UnmarshalException("skeleton class not found but required for client version");
            }
            long long1;
            try {
                long1 = inputStream.readLong();
            }
            catch (final Exception ex2) {
                throw new UnmarshalException("error unmarshalling call header", ex2);
            }
            final MarshalInputStream marshalInputStream = (MarshalInputStream)inputStream;
            marshalInputStream.skipDefaultResolveClass();
            final Method method = this.hashToMethod_Map.get(long1);
            if (method == null) {
                throw new UnmarshalException("unrecognized method hash: method not supported by remote object");
            }
            this.logCall(remote, method);
            Object[] unmarshalParameters = null;
            try {
                this.unmarshalCustomCallData(inputStream);
                unmarshalParameters = this.unmarshalParameters(remote, method, marshalInputStream);
            }
            catch (final AccessException ex3) {
                ((StreamRemoteCall)remoteCall).discardPendingRefs();
                throw ex3;
            }
            catch (final IOException | ClassNotFoundException ex4) {
                ((StreamRemoteCall)remoteCall).discardPendingRefs();
                throw new UnmarshalException("error unmarshalling arguments", (Exception)ex4);
            }
            finally {
                remoteCall.releaseInputStream();
            }
            Object invoke;
            try {
                invoke = method.invoke(remote, unmarshalParameters);
            }
            catch (final InvocationTargetException ex5) {
                throw ex5.getTargetException();
            }
            try {
                final ObjectOutput resultStream = remoteCall.getResultStream(true);
                final Class<?> returnType = method.getReturnType();
                if (returnType != Void.TYPE) {
                    UnicastRef.marshalValue(returnType, invoke, resultStream);
                }
            }
            catch (final IOException ex6) {
                throw new MarshalException("error marshalling return", ex6);
            }
        }
        catch (final Throwable ex7) {
            final RemoteException ex8 = ex7;
            this.logCallException(ex7);
            final ObjectOutput resultStream2 = remoteCall.getResultStream(false);
            if (ex7 instanceof Error) {
                ex7 = new ServerError("Error occurred in server thread", (Error)ex7);
            }
            else if (ex7 instanceof RemoteException) {
                ex7 = new ServerException("RemoteException occurred in server thread", ex7);
            }
            if (UnicastServerRef.suppressStackTraces) {
                clearStackTraces(ex7);
            }
            resultStream2.writeObject(ex7);
            if (ex8 instanceof AccessException) {
                throw new IOException("Connection is not reusable", ex8);
            }
        }
        finally {
            remoteCall.releaseInputStream();
            remoteCall.releaseOutputStream();
        }
    }
    
    protected void unmarshalCustomCallData(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if (this.filter != null && objectInput instanceof ObjectInputStream) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                final /* synthetic */ ObjectInputStream val$ois = (ObjectInputStream)objectInput;
                
                @Override
                public Void run() {
                    ObjectInputFilter.Config.setObjectInputFilter(this.val$ois, UnicastServerRef.this.filter);
                    return null;
                }
            });
        }
    }
    
    private void oldDispatch(final Remote remote, final RemoteCall remoteCall, final int n) throws Exception {
        final ObjectInput inputStream = remoteCall.getInputStream();
        try {
            if (Class.forName("sun.rmi.transport.DGCImpl_Skel").isAssignableFrom(this.skel.getClass())) {
                ((MarshalInputStream)inputStream).useCodebaseOnly();
            }
        }
        catch (final ClassNotFoundException ex) {}
        long long1;
        try {
            long1 = inputStream.readLong();
        }
        catch (final Exception ex2) {
            throw new UnmarshalException("error unmarshalling call header", ex2);
        }
        final Operation[] operations = this.skel.getOperations();
        this.logCall(remote, (n >= 0 && n < operations.length) ? operations[n] : ("op: " + n));
        this.unmarshalCustomCallData(inputStream);
        this.skel.dispatch(remote, remoteCall, n, long1);
    }
    
    public static void clearStackTraces(Throwable cause) {
        final StackTraceElement[] stackTrace = new StackTraceElement[0];
        while (cause != null) {
            cause.setStackTrace(stackTrace);
            cause = cause.getCause();
        }
    }
    
    private void logCall(final Remote remote, final Object o) {
        if (UnicastServerRef.callLog.isLoggable(Log.VERBOSE)) {
            String clientHost;
            try {
                clientHost = this.getClientHost();
            }
            catch (final ServerNotActiveException ex) {
                clientHost = "(local)";
            }
            UnicastServerRef.callLog.log(Log.VERBOSE, "[" + clientHost + ": " + remote.getClass().getName() + this.ref.getObjID().toString() + ": " + o + "]");
        }
    }
    
    private void logCallException(final Throwable t) {
        if (UnicastServerRef.callLog.isLoggable(Log.BRIEF)) {
            String string = "";
            try {
                string = "[" + this.getClientHost() + "] ";
            }
            catch (final ServerNotActiveException ex) {}
            UnicastServerRef.callLog.log(Log.BRIEF, string + "exception: ", t);
        }
        if (UnicastServerRef.wantExceptionLog) {
            final PrintStream err = System.err;
            synchronized (err) {
                err.println();
                err.println("Exception dispatching call to " + this.ref.getObjID() + " in thread \"" + Thread.currentThread().getName() + "\" at " + new Date() + ":");
                t.printStackTrace(err);
            }
        }
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "UnicastServerRef";
    }
    
    protected RemoteRef getClientRef() {
        return new UnicastRef(this.ref);
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.ref = null;
        this.skel = null;
    }
    
    private Object[] unmarshalParameters(final Object o, final Method method, final MarshalInputStream marshalInputStream) throws IOException, ClassNotFoundException {
        return (o instanceof DeserializationChecker) ? this.unmarshalParametersChecked((DeserializationChecker)o, method, marshalInputStream) : this.unmarshalParametersUnchecked(method, marshalInputStream);
    }
    
    private Object[] unmarshalParametersUnchecked(final Method method, final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] array = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            array[i] = UnicastRef.unmarshalValue(parameterTypes[i], objectInput);
        }
        return array;
    }
    
    private Object[] unmarshalParametersChecked(final DeserializationChecker deserializationChecker, final Method method, final MarshalInputStream marshalInputStream) throws IOException, ClassNotFoundException {
        final int andIncrement = this.methodCallIDCount.getAndIncrement();
        final MyChecker streamChecker = new MyChecker(deserializationChecker, method, andIncrement);
        marshalInputStream.setStreamChecker(streamChecker);
        try {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Object[] array = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                streamChecker.setIndex(i);
                array[i] = UnicastRef.unmarshalValue(parameterTypes[i], marshalInputStream);
            }
            streamChecker.end(andIncrement);
            return array;
        }
        finally {
            marshalInputStream.setStreamChecker(null);
        }
    }
    
    static {
        logCalls = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("java.rmi.server.logCalls"));
        callLog = Log.getLog("sun.rmi.server.call", "RMI", UnicastServerRef.logCalls);
        wantExceptionLog = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.server.exceptionTrace"));
        suppressStackTraces = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.server.suppressStackTraces"));
        hashToMethod_Maps = new HashToMethod_Maps();
        withoutSkeletons = Collections.synchronizedMap((Map<Class<?>, ?>)new WeakHashMap<Class<?>, Object>());
    }
    
    private static class HashToMethod_Maps extends WeakClassHashMap<Map<Long, Method>>
    {
        HashToMethod_Maps() {
        }
        
        @Override
        protected Map<Long, Method> computeValue(final Class<?> clazz) {
            final HashMap hashMap = new HashMap();
            for (Class<?> superclass = clazz; superclass != null; superclass = superclass.getSuperclass()) {
                for (final Class clazz2 : superclass.getInterfaces()) {
                    if (Remote.class.isAssignableFrom(clazz2)) {
                        for (final Method method : clazz2.getMethods()) {
                            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                                @Override
                                public Void run() {
                                    method.setAccessible(true);
                                    return null;
                                }
                            });
                            hashMap.put(Util.computeMethodHash(method), method);
                        }
                    }
                }
            }
            return hashMap;
        }
    }
    
    private static class MyChecker implements MarshalInputStream.StreamChecker
    {
        private final DeserializationChecker descriptorCheck;
        private final Method method;
        private final int callID;
        private int parameterIndex;
        
        MyChecker(final DeserializationChecker descriptorCheck, final Method method, final int callID) {
            this.descriptorCheck = descriptorCheck;
            this.method = method;
            this.callID = callID;
        }
        
        @Override
        public void validateDescriptor(final ObjectStreamClass objectStreamClass) {
            this.descriptorCheck.check(this.method, objectStreamClass, this.parameterIndex, this.callID);
        }
        
        @Override
        public void checkProxyInterfaceNames(final String[] array) {
            this.descriptorCheck.checkProxyClass(this.method, array, this.parameterIndex, this.callID);
        }
        
        void setIndex(final int parameterIndex) {
            this.parameterIndex = parameterIndex;
        }
        
        void end(final int n) {
            this.descriptorCheck.end(n);
        }
    }
}
