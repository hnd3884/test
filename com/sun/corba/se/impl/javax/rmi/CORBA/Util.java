package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
import java.util.EmptyStackException;
import java.rmi.UnexpectedException;
import com.sun.corba.se.pept.transport.ContactInfoList;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import javax.rmi.CORBA.Stub;
import com.sun.corba.se.impl.util.JDKBridge;
import java.rmi.server.RMIClassLoader;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.OBJ_ADAPTER;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.impl.corba.AnyImpl;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.io.Serializable;
import com.sun.corba.se.impl.util.Utility;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.INVALID_ACTIVITY;
import org.omg.CORBA.ACTIVITY_COMPLETED;
import sun.corba.SharedSecrets;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import java.io.NotSerializableException;
import org.omg.CORBA.BAD_PARAM;
import javax.transaction.InvalidTransactionException;
import org.omg.CORBA.INVALID_TRANSACTION;
import javax.transaction.TransactionRolledbackException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import javax.transaction.TransactionRequiredException;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.MARSHAL;
import java.rmi.AccessException;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.INV_OBJREF;
import java.rmi.MarshalException;
import org.omg.CORBA.COMM_FAILURE;
import java.rmi.ServerException;
import java.rmi.ServerError;
import org.omg.CORBA.portable.UnknownException;
import java.rmi.RemoteException;
import org.omg.CORBA.SystemException;
import java.util.Enumeration;
import org.omg.CORBA.BAD_OPERATION;
import java.rmi.NoSuchObjectException;
import javax.rmi.CORBA.Tie;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.io.ValueHandlerImpl;
import com.sun.corba.se.impl.util.IdentityHashtable;
import javax.rmi.CORBA.UtilDelegate;

public class Util implements UtilDelegate
{
    private static KeepAlive keepAlive;
    private static IdentityHashtable exportedServants;
    private static final ValueHandlerImpl valueHandlerSingleton;
    private UtilSystemException utilWrapper;
    private static Util instance;
    
    public Util() {
        this.utilWrapper = UtilSystemException.get("rpc.encoding");
        setInstance(this);
    }
    
    private static void setInstance(final Util instance) {
        assert Util.instance == null : "Instance already defined";
        Util.instance = instance;
    }
    
    public static Util getInstance() {
        return Util.instance;
    }
    
    public static boolean isInstanceDefined() {
        return Util.instance != null;
    }
    
    public void unregisterTargetsForORB(final ORB orb) {
        final Enumeration keys = Util.exportedServants.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            final Remote remote = (nextElement instanceof Tie) ? ((Tie)nextElement).getTarget() : nextElement;
            try {
                if (orb != this.getTie(remote).orb()) {
                    continue;
                }
                try {
                    this.unexportObject(remote);
                }
                catch (final NoSuchObjectException ex) {}
            }
            catch (final BAD_OPERATION bad_OPERATION) {}
        }
    }
    
    @Override
    public RemoteException mapSystemException(final SystemException detail) {
        if (detail instanceof UnknownException) {
            final Throwable originalEx = ((UnknownException)detail).originalEx;
            if (originalEx instanceof Error) {
                return new ServerError("Error occurred in server thread", (Error)originalEx);
            }
            if (originalEx instanceof RemoteException) {
                return new ServerException("RemoteException occurred in server thread", (Exception)originalEx);
            }
            if (originalEx instanceof RuntimeException) {
                throw (RuntimeException)originalEx;
            }
        }
        final String name = detail.getClass().getName();
        final String substring = name.substring(name.lastIndexOf(46) + 1);
        String s = null;
        switch (detail.completed.value()) {
            case 0: {
                s = "Yes";
                break;
            }
            case 1: {
                s = "No";
                break;
            }
            default: {
                s = "Maybe";
                break;
            }
        }
        final String string = "CORBA " + substring + " " + detail.minor + " " + s;
        if (detail instanceof COMM_FAILURE) {
            return new MarshalException(string, detail);
        }
        if (detail instanceof INV_OBJREF) {
            final NoSuchObjectException ex = new NoSuchObjectException(string);
            ex.detail = detail;
            return ex;
        }
        if (detail instanceof NO_PERMISSION) {
            return new AccessException(string, detail);
        }
        if (detail instanceof MARSHAL) {
            return new MarshalException(string, detail);
        }
        if (detail instanceof OBJECT_NOT_EXIST) {
            final NoSuchObjectException ex2 = new NoSuchObjectException(string);
            ex2.detail = detail;
            return ex2;
        }
        if (detail instanceof TRANSACTION_REQUIRED) {
            final TransactionRequiredException ex3 = new TransactionRequiredException(string);
            ex3.detail = detail;
            return ex3;
        }
        if (detail instanceof TRANSACTION_ROLLEDBACK) {
            final TransactionRolledbackException ex4 = new TransactionRolledbackException(string);
            ex4.detail = detail;
            return ex4;
        }
        if (detail instanceof INVALID_TRANSACTION) {
            final InvalidTransactionException ex5 = new InvalidTransactionException(string);
            ex5.detail = detail;
            return ex5;
        }
        if (detail instanceof BAD_PARAM) {
            Exception ex6 = detail;
            if (detail.minor == 1398079489 || detail.minor == 1330446342) {
                if (detail.getMessage() != null) {
                    ex6 = new NotSerializableException(detail.getMessage());
                }
                else {
                    ex6 = new NotSerializableException();
                }
                ex6.initCause(detail);
            }
            return new MarshalException(string, ex6);
        }
        if (detail instanceof ACTIVITY_REQUIRED) {
            try {
                return (RemoteException)SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityRequiredException").getConstructor(String.class, Throwable.class).newInstance(string, detail);
            }
            catch (final Throwable t) {
                this.utilWrapper.classNotFound(t, "javax.activity.ActivityRequiredException");
                return new RemoteException(string, detail);
            }
        }
        if (detail instanceof ACTIVITY_COMPLETED) {
            try {
                return (RemoteException)SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityCompletedException").getConstructor(String.class, Throwable.class).newInstance(string, detail);
            }
            catch (final Throwable t2) {
                this.utilWrapper.classNotFound(t2, "javax.activity.ActivityCompletedException");
                return new RemoteException(string, detail);
            }
        }
        if (detail instanceof INVALID_ACTIVITY) {
            try {
                return (RemoteException)SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.InvalidActivityException").getConstructor(String.class, Throwable.class).newInstance(string, detail);
            }
            catch (final Throwable t3) {
                this.utilWrapper.classNotFound(t3, "javax.activity.InvalidActivityException");
            }
        }
        return new RemoteException(string, detail);
    }
    
    @Override
    public void writeAny(final OutputStream outputStream, final Object o) {
        final ORB orb = outputStream.orb();
        final Any create_any = orb.create_any();
        final Object autoConnect = Utility.autoConnect(o, orb, false);
        if (autoConnect instanceof org.omg.CORBA.Object) {
            create_any.insert_Object((org.omg.CORBA.Object)autoConnect);
        }
        else if (autoConnect == null) {
            create_any.insert_Value(null, this.createTypeCodeForNull(orb));
        }
        else if (autoConnect instanceof Serializable) {
            final TypeCode typeCode = this.createTypeCode((Serializable)autoConnect, create_any, orb);
            if (typeCode == null) {
                create_any.insert_Value((Serializable)autoConnect);
            }
            else {
                create_any.insert_Value((Serializable)autoConnect, typeCode);
            }
        }
        else if (autoConnect instanceof Remote) {
            ORBUtility.throwNotSerializableForCorba(((Serializable)autoConnect).getClass().getName());
        }
        else {
            ORBUtility.throwNotSerializableForCorba(((Serializable)autoConnect).getClass().getName());
        }
        outputStream.write_any(create_any);
    }
    
    private TypeCode createTypeCode(final Serializable s, final Any any, final ORB orb) {
        if (any instanceof AnyImpl && orb instanceof com.sun.corba.se.spi.orb.ORB) {
            return ((AnyImpl)any).createTypeCodeForClass(s.getClass(), (com.sun.corba.se.spi.orb.ORB)orb);
        }
        return null;
    }
    
    private TypeCode createTypeCodeForNull(final ORB orb) {
        if (orb instanceof com.sun.corba.se.spi.orb.ORB) {
            final com.sun.corba.se.spi.orb.ORB orb2 = (com.sun.corba.se.spi.orb.ORB)orb;
            if (!ORBVersionFactory.getFOREIGN().equals(orb2.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(orb2.getORBVersion()) > 0) {
                return orb.get_primitive_tc(TCKind.tk_value);
            }
        }
        return orb.create_abstract_interface_tc("IDL:omg.org/CORBA/AbstractBase:1.0", "");
    }
    
    @Override
    public Object readAny(final InputStream inputStream) {
        final Any read_any = inputStream.read_any();
        if (read_any.type().kind().value() == 14) {
            return read_any.extract_Object();
        }
        return read_any.extract_Value();
    }
    
    @Override
    public void writeRemoteObject(final OutputStream outputStream, final Object o) {
        outputStream.write_Object((org.omg.CORBA.Object)Utility.autoConnect(o, outputStream.orb(), false));
    }
    
    @Override
    public void writeAbstractObject(final OutputStream outputStream, final Object o) {
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_abstract_interface(Utility.autoConnect(o, outputStream.orb(), false));
    }
    
    @Override
    public void registerTarget(final Tie tie, final Remote target) {
        synchronized (Util.exportedServants) {
            if (lookupTie(target) == null) {
                Util.exportedServants.put(target, tie);
                tie.setTarget(target);
                if (Util.keepAlive == null) {
                    (Util.keepAlive = AccessController.doPrivileged((PrivilegedAction<KeepAlive>)new PrivilegedAction() {
                        @Override
                        public Object run() {
                            return new KeepAlive();
                        }
                    })).start();
                }
            }
        }
    }
    
    @Override
    public void unexportObject(final Remote remote) throws NoSuchObjectException {
        synchronized (Util.exportedServants) {
            final Tie lookupTie = lookupTie(remote);
            if (lookupTie == null) {
                throw new NoSuchObjectException("Tie not found");
            }
            Util.exportedServants.remove(remote);
            Utility.purgeStubForTie(lookupTie);
            Utility.purgeTieAndServant(lookupTie);
            try {
                this.cleanUpTie(lookupTie);
            }
            catch (final BAD_OPERATION bad_OPERATION) {}
            catch (final OBJ_ADAPTER obj_ADAPTER) {}
            if (Util.exportedServants.isEmpty()) {
                Util.keepAlive.quit();
                Util.keepAlive = null;
            }
        }
    }
    
    protected void cleanUpTie(final Tie tie) throws NoSuchObjectException {
        tie.setTarget(null);
        tie.deactivate();
    }
    
    @Override
    public Tie getTie(final Remote remote) {
        synchronized (Util.exportedServants) {
            return lookupTie(remote);
        }
    }
    
    private static Tie lookupTie(final Remote remote) {
        Tie tie = (Tie)Util.exportedServants.get(remote);
        if (tie == null && remote instanceof Tie && Util.exportedServants.contains(remote)) {
            tie = (Tie)remote;
        }
        return tie;
    }
    
    @Override
    public ValueHandler createValueHandler() {
        return Util.valueHandlerSingleton;
    }
    
    @Override
    public String getCodebase(final Class clazz) {
        return RMIClassLoader.getClassAnnotation(clazz);
    }
    
    @Override
    public Class loadClass(final String s, final String s2, final ClassLoader classLoader) throws ClassNotFoundException {
        return JDKBridge.loadClass(s, s2, classLoader);
    }
    
    @Override
    public boolean isLocal(final Stub stub) throws RemoteException {
        boolean b = false;
        try {
            final Delegate get_delegate = stub._get_delegate();
            if (get_delegate instanceof CorbaClientDelegate) {
                final ContactInfoList contactInfoList = ((CorbaClientDelegate)get_delegate).getContactInfoList();
                if (contactInfoList instanceof CorbaContactInfoList) {
                    b = ((CorbaContactInfoList)contactInfoList).getLocalClientRequestDispatcher().useLocalInvocation(null);
                }
            }
            else {
                b = get_delegate.is_local(stub);
            }
        }
        catch (final SystemException ex) {
            throw javax.rmi.CORBA.Util.mapSystemException(ex);
        }
        return b;
    }
    
    @Override
    public RemoteException wrapException(final Throwable t) {
        if (t instanceof SystemException) {
            return this.mapSystemException((SystemException)t);
        }
        if (t instanceof Error) {
            return new ServerError("Error occurred in server thread", (Error)t);
        }
        if (t instanceof RemoteException) {
            return new ServerException("RemoteException occurred in server thread", (Exception)t);
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Exception) {
            return new UnexpectedException(t.toString(), (Exception)t);
        }
        return new UnexpectedException(t.toString());
    }
    
    @Override
    public Object[] copyObjects(final Object[] array, final ORB orb) throws RemoteException {
        if (array == null) {
            throw new NullPointerException();
        }
        final Class<?> componentType = array.getClass().getComponentType();
        if (Remote.class.isAssignableFrom(componentType) && !componentType.isInterface()) {
            final Remote[] array2 = new Remote[array.length];
            System.arraycopy(array, 0, array2, 0, array.length);
            return (Object[])this.copyObject(array2, orb);
        }
        return (Object[])this.copyObject(array, orb);
    }
    
    @Override
    public Object copyObject(final Object o, final ORB orb) throws RemoteException {
        if (orb instanceof com.sun.corba.se.spi.orb.ORB) {
            final com.sun.corba.se.spi.orb.ORB orb2 = (com.sun.corba.se.spi.orb.ORB)orb;
            try {
                try {
                    return orb2.peekInvocationInfo().getCopierFactory().make().copy(o);
                }
                catch (final EmptyStackException ex) {
                    return orb2.getCopierManager().getDefaultObjectCopierFactory().make().copy(o);
                }
            }
            catch (final ReflectiveCopyException ex2) {
                final RemoteException ex3 = new RemoteException();
                ex3.initCause(ex2);
                throw ex3;
            }
        }
        final org.omg.CORBA_2_3.portable.OutputStream outputStream = (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
        outputStream.write_value((Serializable)o);
        return ((org.omg.CORBA_2_3.portable.InputStream)outputStream.create_input_stream()).read_value();
    }
    
    static {
        Util.keepAlive = null;
        Util.exportedServants = new IdentityHashtable();
        valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
        Util.instance = null;
    }
}
