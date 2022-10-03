package com.sun.corba.se.impl.io;

import org.omg.CORBA.TCKind;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.omg.CORBA.portable.IndirectionException;
import java.lang.reflect.Array;
import com.sun.corba.se.impl.util.Utility;
import java.rmi.Remote;
import javax.rmi.CORBA.Util;
import java.io.IOException;
import com.sun.corba.se.impl.util.RepositoryId;
import javax.rmi.CORBA.ValueHandler;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueOutputStream;
import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.Hashtable;
import javax.rmi.CORBA.ValueHandlerMultiFormat;

public final class ValueHandlerImpl implements ValueHandlerMultiFormat
{
    public static final String FORMAT_VERSION_PROPERTY = "com.sun.CORBA.MaxStreamFormatVersion";
    private static final byte MAX_SUPPORTED_FORMAT_VERSION = 2;
    private static final byte STREAM_FORMAT_VERSION_1 = 1;
    private static final byte MAX_STREAM_FORMAT_VERSION;
    public static final short kRemoteType = 0;
    public static final short kAbstractType = 1;
    public static final short kValueType = 2;
    private Hashtable inputStreamPairs;
    private Hashtable outputStreamPairs;
    private CodeBase codeBase;
    private boolean useHashtables;
    private boolean isInputStream;
    private IIOPOutputStream outputStreamBridge;
    private IIOPInputStream inputStreamBridge;
    private OMGSystemException omgWrapper;
    private UtilSystemException utilWrapper;
    
    private static byte getMaxStreamFormatVersion() {
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                @Override
                public Object run() {
                    return System.getProperty("com.sun.CORBA.MaxStreamFormatVersion");
                }
            });
            if (s == null) {
                return 2;
            }
            final byte byte1 = Byte.parseByte(s);
            if (byte1 < 1 || byte1 > 2) {
                throw new ExceptionInInitializerError("Invalid stream format version: " + byte1 + ".  Valid range is 1 through " + 2);
            }
            return byte1;
        }
        catch (final Exception ex) {
            final ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError(ex);
            exceptionInInitializerError.initCause(ex);
            throw exceptionInInitializerError;
        }
    }
    
    @Override
    public byte getMaximumStreamFormatVersion() {
        return ValueHandlerImpl.MAX_STREAM_FORMAT_VERSION;
    }
    
    @Override
    public void writeValue(final OutputStream outputStream, final Serializable s, final byte b) {
        if (b == 2) {
            if (!(outputStream instanceof ValueOutputStream)) {
                throw this.omgWrapper.notAValueoutputstream();
            }
        }
        else if (b != 1) {
            throw this.omgWrapper.invalidStreamFormatVersion(new Integer(b));
        }
        this.writeValueWithVersion(outputStream, s, b);
    }
    
    private ValueHandlerImpl() {
        this.inputStreamPairs = null;
        this.outputStreamPairs = null;
        this.codeBase = null;
        this.useHashtables = true;
        this.isInputStream = true;
        this.outputStreamBridge = null;
        this.inputStreamBridge = null;
        this.omgWrapper = OMGSystemException.get("rpc.encoding");
        this.utilWrapper = UtilSystemException.get("rpc.encoding");
    }
    
    private ValueHandlerImpl(final boolean isInputStream) {
        this();
        this.useHashtables = false;
        this.isInputStream = isInputStream;
    }
    
    static ValueHandlerImpl getInstance() {
        return new ValueHandlerImpl();
    }
    
    static ValueHandlerImpl getInstance(final boolean b) {
        return new ValueHandlerImpl(b);
    }
    
    @Override
    public void writeValue(final OutputStream outputStream, final Serializable s) {
        this.writeValueWithVersion(outputStream, s, (byte)1);
    }
    
    private void writeValueWithVersion(final OutputStream outputStream, final Serializable s, final byte b) {
        final org.omg.CORBA_2_3.portable.OutputStream outputStream2 = (org.omg.CORBA_2_3.portable.OutputStream)outputStream;
        if (!this.useHashtables) {
            if (this.outputStreamBridge == null) {
                (this.outputStreamBridge = this.createOutputStream()).setOrbStream(outputStream2);
            }
            try {
                this.outputStreamBridge.increaseRecursionDepth();
                this.writeValueInternal(this.outputStreamBridge, outputStream2, s, b);
            }
            finally {
                this.outputStreamBridge.decreaseRecursionDepth();
            }
            return;
        }
        if (this.outputStreamPairs == null) {
            this.outputStreamPairs = new Hashtable();
        }
        IIOPOutputStream outputStream3 = this.outputStreamPairs.get(outputStream);
        if (outputStream3 == null) {
            outputStream3 = this.createOutputStream();
            outputStream3.setOrbStream(outputStream2);
            this.outputStreamPairs.put(outputStream, outputStream3);
        }
        try {
            outputStream3.increaseRecursionDepth();
            this.writeValueInternal(outputStream3, outputStream2, s, b);
        }
        finally {
            if (outputStream3.decreaseRecursionDepth() == 0) {
                this.outputStreamPairs.remove(outputStream);
            }
        }
    }
    
    private void writeValueInternal(final IIOPOutputStream iiopOutputStream, final org.omg.CORBA_2_3.portable.OutputStream outputStream, final Serializable s, final byte b) {
        final Class<? extends Serializable> class1 = s.getClass();
        if (class1.isArray()) {
            this.write_Array(outputStream, s, class1.getComponentType());
        }
        else {
            iiopOutputStream.simpleWriteObject(s, b);
        }
    }
    
    @Override
    public Serializable readValue(final InputStream inputStream, final int n, final Class clazz, final String s, final RunTime runTime) {
        final CodeBase narrow = CodeBaseHelper.narrow(runTime);
        final org.omg.CORBA_2_3.portable.InputStream inputStream2 = (org.omg.CORBA_2_3.portable.InputStream)inputStream;
        if (!this.useHashtables) {
            if (this.inputStreamBridge == null) {
                (this.inputStreamBridge = this.createInputStream()).setOrbStream(inputStream2);
                this.inputStreamBridge.setSender(narrow);
                this.inputStreamBridge.setValueHandler(this);
            }
            Serializable valueInternal = null;
            try {
                this.inputStreamBridge.increaseRecursionDepth();
                valueInternal = this.readValueInternal(this.inputStreamBridge, inputStream2, n, clazz, s, narrow);
            }
            finally {
                if (this.inputStreamBridge.decreaseRecursionDepth() == 0) {}
            }
            return valueInternal;
        }
        if (this.inputStreamPairs == null) {
            this.inputStreamPairs = new Hashtable();
        }
        IIOPInputStream inputStream3 = this.inputStreamPairs.get(inputStream);
        if (inputStream3 == null) {
            inputStream3 = this.createInputStream();
            inputStream3.setOrbStream(inputStream2);
            inputStream3.setSender(narrow);
            inputStream3.setValueHandler(this);
            this.inputStreamPairs.put(inputStream, inputStream3);
        }
        Serializable valueInternal2 = null;
        try {
            inputStream3.increaseRecursionDepth();
            valueInternal2 = this.readValueInternal(inputStream3, inputStream2, n, clazz, s, narrow);
        }
        finally {
            if (inputStream3.decreaseRecursionDepth() == 0) {
                this.inputStreamPairs.remove(inputStream);
            }
        }
        return valueInternal2;
    }
    
    private Serializable readValueInternal(final IIOPInputStream iiopInputStream, final org.omg.CORBA_2_3.portable.InputStream inputStream, final int n, final Class clazz, final String s, final CodeBase codeBase) {
        final Serializable s2 = null;
        if (clazz == null) {
            if (this.isArray(s)) {
                this.read_Array(iiopInputStream, inputStream, null, codeBase, n);
            }
            else {
                iiopInputStream.simpleSkipObject(s, codeBase);
            }
            return s2;
        }
        Serializable s3;
        if (clazz.isArray()) {
            s3 = (Serializable)this.read_Array(iiopInputStream, inputStream, clazz, codeBase, n);
        }
        else {
            s3 = (Serializable)iiopInputStream.simpleReadObject(clazz, s, codeBase, n);
        }
        return s3;
    }
    
    @Override
    public String getRMIRepositoryID(final Class clazz) {
        return RepositoryId.createForJavaType(clazz);
    }
    
    @Override
    public boolean isCustomMarshaled(final Class clazz) {
        return ObjectStreamClass.lookup(clazz).isCustomMarshaled();
    }
    
    @Override
    public RunTime getRunTimeCodeBase() {
        if (this.codeBase != null) {
            return this.codeBase;
        }
        this.codeBase = new FVDCodeBaseImpl();
        ((FVDCodeBaseImpl)this.codeBase).setValueHandler(this);
        return this.codeBase;
    }
    
    public boolean useFullValueDescription(final Class clazz, final String s) throws IOException {
        return RepositoryId.useFullValueDescription(clazz, s);
    }
    
    public String getClassName(final String s) {
        return RepositoryId.cache.getId(s).getClassName();
    }
    
    public Class getClassFromType(final String s) throws ClassNotFoundException {
        return RepositoryId.cache.getId(s).getClassFromType();
    }
    
    public Class getAnyClassFromType(final String s) throws ClassNotFoundException {
        return RepositoryId.cache.getId(s).getAnyClassFromType();
    }
    
    public String createForAnyType(final Class clazz) {
        return RepositoryId.createForAnyType(clazz);
    }
    
    public String getDefinedInId(final String s) {
        return RepositoryId.cache.getId(s).getDefinedInId();
    }
    
    public String getUnqualifiedName(final String s) {
        return RepositoryId.cache.getId(s).getUnqualifiedName();
    }
    
    public String getSerialVersionUID(final String s) {
        return RepositoryId.cache.getId(s).getSerialVersionUID();
    }
    
    public boolean isAbstractBase(final Class clazz) {
        return RepositoryId.isAbstractBase(clazz);
    }
    
    public boolean isSequence(final String s) {
        return RepositoryId.cache.getId(s).isSequence();
    }
    
    @Override
    public Serializable writeReplace(final Serializable s) {
        return ObjectStreamClass.lookup(s.getClass()).writeReplace(s);
    }
    
    private void writeCharArray(final org.omg.CORBA_2_3.portable.OutputStream outputStream, final char[] array, final int n, final int n2) {
        outputStream.write_wchar_array(array, n, n2);
    }
    
    private void write_Array(final org.omg.CORBA_2_3.portable.OutputStream outputStream, final Serializable s, final Class clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                final int[] array = (Object)s;
                final int length = array.length;
                outputStream.write_ulong(length);
                outputStream.write_long_array(array, 0, length);
            }
            else if (clazz == Byte.TYPE) {
                final byte[] array2 = (Object)s;
                final int length2 = array2.length;
                outputStream.write_ulong(length2);
                outputStream.write_octet_array(array2, 0, length2);
            }
            else if (clazz == Long.TYPE) {
                final long[] array3 = (Object)s;
                final int length3 = array3.length;
                outputStream.write_ulong(length3);
                outputStream.write_longlong_array(array3, 0, length3);
            }
            else if (clazz == Float.TYPE) {
                final float[] array4 = (Object)s;
                final int length4 = array4.length;
                outputStream.write_ulong(length4);
                outputStream.write_float_array(array4, 0, length4);
            }
            else if (clazz == Double.TYPE) {
                final double[] array5 = (Object)s;
                final int length5 = array5.length;
                outputStream.write_ulong(length5);
                outputStream.write_double_array(array5, 0, length5);
            }
            else if (clazz == Short.TYPE) {
                final short[] array6 = (Object)s;
                final int length6 = array6.length;
                outputStream.write_ulong(length6);
                outputStream.write_short_array(array6, 0, length6);
            }
            else if (clazz == Character.TYPE) {
                final char[] array7 = (Object)s;
                final int length7 = array7.length;
                outputStream.write_ulong(length7);
                this.writeCharArray(outputStream, array7, 0, length7);
            }
            else {
                if (clazz != Boolean.TYPE) {
                    throw new Error("Invalid primitive type : " + s.getClass().getName());
                }
                final boolean[] array8 = (Object)s;
                final int length8 = array8.length;
                outputStream.write_ulong(length8);
                outputStream.write_boolean_array(array8, 0, length8);
            }
        }
        else if (clazz == Object.class) {
            final Object[] array9 = (Object)s;
            final int length9 = array9.length;
            outputStream.write_ulong(length9);
            for (int i = 0; i < length9; ++i) {
                Util.writeAny(outputStream, array9[i]);
            }
        }
        else {
            final Object[] array10 = (Object)s;
            final int length10 = array10.length;
            outputStream.write_ulong(length10);
            int n = 2;
            if (clazz.isInterface()) {
                clazz.getName();
                if (Remote.class.isAssignableFrom(clazz)) {
                    n = 0;
                }
                else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz)) {
                    n = 0;
                }
                else if (RepositoryId.isAbstractBase(clazz)) {
                    n = 1;
                }
                else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz)) {
                    n = 1;
                }
            }
            for (int j = 0; j < length10; ++j) {
                switch (n) {
                    case 0: {
                        Util.writeRemoteObject(outputStream, array10[j]);
                        break;
                    }
                    case 1: {
                        Util.writeAbstractObject(outputStream, array10[j]);
                        break;
                    }
                    case 2: {
                        try {
                            outputStream.write_value((Serializable)array10[j]);
                        }
                        catch (final ClassCastException ex) {
                            if (array10[j] instanceof Serializable) {
                                throw ex;
                            }
                            Utility.throwNotSerializableForCorba(array10[j].getClass().getName());
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void readCharArray(final org.omg.CORBA_2_3.portable.InputStream inputStream, final char[] array, final int n, final int n2) {
        inputStream.read_wchar_array(array, n, n2);
    }
    
    private Object read_Array(final IIOPInputStream iiopInputStream, final org.omg.CORBA_2_3.portable.InputStream inputStream, final Class clazz, final CodeBase codeBase, final int n) {
        try {
            final int read_ulong = inputStream.read_ulong();
            if (clazz == null) {
                for (int i = 0; i < read_ulong; ++i) {
                    inputStream.read_value();
                }
                return null;
            }
            Class clazz3;
            final Class clazz2 = clazz3 = clazz.getComponentType();
            if (clazz2.isPrimitive()) {
                if (clazz2 == Integer.TYPE) {
                    final int[] array = new int[read_ulong];
                    inputStream.read_long_array(array, 0, read_ulong);
                    return array;
                }
                if (clazz2 == Byte.TYPE) {
                    final byte[] array2 = new byte[read_ulong];
                    inputStream.read_octet_array(array2, 0, read_ulong);
                    return array2;
                }
                if (clazz2 == Long.TYPE) {
                    final long[] array3 = new long[read_ulong];
                    inputStream.read_longlong_array(array3, 0, read_ulong);
                    return array3;
                }
                if (clazz2 == Float.TYPE) {
                    final float[] array4 = new float[read_ulong];
                    inputStream.read_float_array(array4, 0, read_ulong);
                    return array4;
                }
                if (clazz2 == Double.TYPE) {
                    final double[] array5 = new double[read_ulong];
                    inputStream.read_double_array(array5, 0, read_ulong);
                    return array5;
                }
                if (clazz2 == Short.TYPE) {
                    final short[] array6 = new short[read_ulong];
                    inputStream.read_short_array(array6, 0, read_ulong);
                    return array6;
                }
                if (clazz2 == Character.TYPE) {
                    final char[] array7 = new char[read_ulong];
                    this.readCharArray(inputStream, array7, 0, read_ulong);
                    return array7;
                }
                if (clazz2 == Boolean.TYPE) {
                    final boolean[] array8 = new boolean[read_ulong];
                    inputStream.read_boolean_array(array8, 0, read_ulong);
                    return array8;
                }
                throw new Error("Invalid primitive componentType : " + clazz.getName());
            }
            else {
                if (clazz2 == Object.class) {
                    final Object[] array9 = (Object[])Array.newInstance(clazz2, read_ulong);
                    iiopInputStream.activeRecursionMgr.addObject(n, array9);
                    for (int j = 0; j < read_ulong; ++j) {
                        Object o;
                        try {
                            o = Util.readAny(inputStream);
                        }
                        catch (final IndirectionException ex) {
                            try {
                                o = iiopInputStream.activeRecursionMgr.getObject(ex.offset);
                            }
                            catch (final IOException ex2) {
                                throw this.utilWrapper.invalidIndirection(ex2, new Integer(ex.offset));
                            }
                        }
                        array9[j] = o;
                    }
                    return array9;
                }
                final Object[] array10 = (Object[])Array.newInstance(clazz2, read_ulong);
                iiopInputStream.activeRecursionMgr.addObject(n, array10);
                int n2 = 2;
                boolean b = false;
                if (clazz2.isInterface()) {
                    boolean b2 = false;
                    if (Remote.class.isAssignableFrom(clazz2)) {
                        n2 = 0;
                        b2 = true;
                    }
                    else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz2)) {
                        n2 = 0;
                        b2 = true;
                    }
                    else if (RepositoryId.isAbstractBase(clazz2)) {
                        n2 = 1;
                        b2 = true;
                    }
                    else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz2)) {
                        n2 = 1;
                    }
                    if (b2) {
                        try {
                            clazz3 = Utility.loadStubClass(RepositoryId.createForAnyType(clazz2), Util.getCodebase(clazz2), clazz2);
                        }
                        catch (final ClassNotFoundException ex3) {
                            b = true;
                        }
                    }
                    else {
                        b = true;
                    }
                }
                for (int k = 0; k < read_ulong; ++k) {
                    try {
                        switch (n2) {
                            case 0: {
                                if (!b) {
                                    array10[k] = inputStream.read_Object(clazz3);
                                    break;
                                }
                                array10[k] = Utility.readObjectAndNarrow(inputStream, clazz3);
                                break;
                            }
                            case 1: {
                                if (!b) {
                                    array10[k] = inputStream.read_abstract_interface(clazz3);
                                    break;
                                }
                                array10[k] = Utility.readAbstractAndNarrow(inputStream, clazz3);
                                break;
                            }
                            case 2: {
                                array10[k] = inputStream.read_value(clazz3);
                                break;
                            }
                        }
                    }
                    catch (final IndirectionException ex4) {
                        try {
                            array10[k] = iiopInputStream.activeRecursionMgr.getObject(ex4.offset);
                        }
                        catch (final IOException ex5) {
                            throw this.utilWrapper.invalidIndirection(ex5, new Integer(ex4.offset));
                        }
                    }
                }
                return array10;
            }
        }
        finally {
            iiopInputStream.activeRecursionMgr.removeObject(n);
        }
    }
    
    private boolean isArray(final String s) {
        return RepositoryId.cache.getId(s).isSequence();
    }
    
    private String getOutputStreamClassName() {
        return "com.sun.corba.se.impl.io.IIOPOutputStream";
    }
    
    private IIOPOutputStream createOutputStream() {
        final String outputStreamClassName = this.getOutputStreamClassName();
        try {
            final IIOPOutputStream outputStreamBuiltIn = this.createOutputStreamBuiltIn(outputStreamClassName);
            if (outputStreamBuiltIn != null) {
                return outputStreamBuiltIn;
            }
            return this.createCustom(IIOPOutputStream.class, outputStreamClassName);
        }
        catch (final Throwable t) {
            final InternalError internalError = new InternalError("Error loading " + outputStreamClassName);
            internalError.initCause(t);
            throw internalError;
        }
    }
    
    private IIOPOutputStream createOutputStreamBuiltIn(final String s) throws Throwable {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<IIOPOutputStream>)new PrivilegedExceptionAction<IIOPOutputStream>() {
                @Override
                public IIOPOutputStream run() throws IOException {
                    return ValueHandlerImpl.this.createOutputStreamBuiltInNoPriv(s);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw ex.getCause();
        }
    }
    
    private IIOPOutputStream createOutputStreamBuiltInNoPriv(final String s) throws IOException {
        return s.equals(IIOPOutputStream.class.getName()) ? new IIOPOutputStream() : null;
    }
    
    private String getInputStreamClassName() {
        return "com.sun.corba.se.impl.io.IIOPInputStream";
    }
    
    private IIOPInputStream createInputStream() {
        final String inputStreamClassName = this.getInputStreamClassName();
        try {
            final IIOPInputStream inputStreamBuiltIn = this.createInputStreamBuiltIn(inputStreamClassName);
            if (inputStreamBuiltIn != null) {
                return inputStreamBuiltIn;
            }
            return this.createCustom(IIOPInputStream.class, inputStreamClassName);
        }
        catch (final Throwable t) {
            final InternalError internalError = new InternalError("Error loading " + inputStreamClassName);
            internalError.initCause(t);
            throw internalError;
        }
    }
    
    private IIOPInputStream createInputStreamBuiltIn(final String s) throws Throwable {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<IIOPInputStream>)new PrivilegedExceptionAction<IIOPInputStream>() {
                @Override
                public IIOPInputStream run() throws IOException {
                    return ValueHandlerImpl.this.createInputStreamBuiltInNoPriv(s);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw ex.getCause();
        }
    }
    
    private IIOPInputStream createInputStreamBuiltInNoPriv(final String s) throws IOException {
        return s.equals(IIOPInputStream.class.getName()) ? new IIOPInputStream() : null;
    }
    
    private <T> T createCustom(final Class<T> clazz, final String s) throws Throwable {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return (T)classLoader.loadClass(s).asSubclass(clazz).newInstance();
    }
    
    TCKind getJavaCharTCKind() {
        return TCKind.tk_wchar;
    }
    
    static {
        MAX_STREAM_FORMAT_VERSION = getMaxStreamFormatVersion();
    }
}
