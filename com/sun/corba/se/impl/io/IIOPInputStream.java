package com.sun.corba.se.impl.io;

import java.util.HashMap;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import java.security.PrivilegedActionException;
import java.util.Map;
import java.rmi.Remote;
import com.sun.corba.se.impl.util.Utility;
import javax.rmi.CORBA.Util;
import java.util.Enumeration;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import java.lang.reflect.InvocationTargetException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.EOFException;
import java.io.StreamCorruptedException;
import java.io.NotActiveException;
import org.omg.CORBA.portable.IndirectionException;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA.CompletionStatus;
import java.security.AccessController;
import java.io.OptionalDataException;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Constructor;
import org.omg.CORBA.TypeCode;
import java.util.Vector;
import java.io.IOException;
import com.sun.org.omg.SendingContext.CodeBase;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA.ValueMember;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import sun.corba.Bridge;

public class IIOPInputStream extends InputStreamHook
{
    private static Bridge bridge;
    private static OMGSystemException omgWrapper;
    private static UtilSystemException utilWrapper;
    private ValueMember[] defaultReadObjectFVDMembers;
    private org.omg.CORBA_2_3.portable.InputStream orbStream;
    private CodeBase cbSender;
    private ValueHandlerImpl vhandler;
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private Class currentClass;
    private int recursionDepth;
    private int simpleReadDepth;
    ActiveRecursionManager activeRecursionMgr;
    private IOException abortIOException;
    private ClassNotFoundException abortClassNotFoundException;
    private Vector callbacks;
    ObjectStreamClass[] classdesc;
    Class[] classes;
    int spClass;
    private static final String kEmptyStr = "";
    public static final TypeCode kRemoteTypeCode;
    public static final TypeCode kValueTypeCode;
    private static final boolean useFVDOnly = false;
    private byte streamFormatVersion;
    private static final Constructor OPT_DATA_EXCEPTION_CTOR;
    private Object[] readObjectArgList;
    
    private static Constructor getOptDataExceptionCtor() {
        try {
            final Constructor constructor = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws NoSuchMethodException, SecurityException {
                    final Constructor<OptionalDataException> declaredConstructor = OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
                    declaredConstructor.setAccessible(true);
                    return declaredConstructor;
                }
            });
            if (constructor == null) {
                throw new Error("Unable to find OptionalDataException constructor");
            }
            return constructor;
        }
        catch (final Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    private OptionalDataException createOptionalDataException() {
        try {
            final OptionalDataException ex = IIOPInputStream.OPT_DATA_EXCEPTION_CTOR.newInstance(Boolean.TRUE);
            if (ex == null) {
                throw new Error("Created null OptionalDataException");
            }
            return ex;
        }
        catch (final Exception ex2) {
            throw new Error("Couldn't create OptionalDataException", ex2);
        }
    }
    
    @Override
    protected byte getStreamFormatVersion() {
        return this.streamFormatVersion;
    }
    
    private void readFormatVersion() throws IOException {
        this.streamFormatVersion = this.orbStream.read_octet();
        if (this.streamFormatVersion < 1 || this.streamFormatVersion > this.vhandler.getMaximumStreamFormatVersion()) {
            final MARSHAL unsupportedFormatVersion = IIOPInputStream.omgWrapper.unsupportedFormatVersion(CompletionStatus.COMPLETED_MAYBE);
            final IOException ex = new IOException("Unsupported format version: " + this.streamFormatVersion);
            ex.initCause(unsupportedFormatVersion);
            throw ex;
        }
        if (this.streamFormatVersion == 2 && !(this.orbStream instanceof ValueInputStream)) {
            final BAD_PARAM notAValueinputstream = IIOPInputStream.omgWrapper.notAValueinputstream(CompletionStatus.COMPLETED_MAYBE);
            final IOException ex2 = new IOException("Not a ValueInputStream");
            ex2.initCause(notAValueinputstream);
            throw ex2;
        }
    }
    
    public static void setTestFVDFlag(final boolean b) {
    }
    
    public IIOPInputStream() throws IOException {
        this.defaultReadObjectFVDMembers = null;
        this.currentObject = null;
        this.currentClassDesc = null;
        this.currentClass = null;
        this.recursionDepth = 0;
        this.simpleReadDepth = 0;
        this.activeRecursionMgr = new ActiveRecursionManager();
        this.abortIOException = null;
        this.abortClassNotFoundException = null;
        this.readObjectArgList = new Object[] { this };
        this.resetStream();
    }
    
    final void setOrbStream(final org.omg.CORBA_2_3.portable.InputStream orbStream) {
        this.orbStream = orbStream;
    }
    
    @Override
    final org.omg.CORBA_2_3.portable.InputStream getOrbStream() {
        return this.orbStream;
    }
    
    public final void setSender(final CodeBase cbSender) {
        this.cbSender = cbSender;
    }
    
    public final CodeBase getSender() {
        return this.cbSender;
    }
    
    public final void setValueHandler(final ValueHandler valueHandler) {
        this.vhandler = (ValueHandlerImpl)valueHandler;
    }
    
    public final ValueHandler getValueHandler() {
        return this.vhandler;
    }
    
    final void increaseRecursionDepth() {
        ++this.recursionDepth;
    }
    
    final int decreaseRecursionDepth() {
        return --this.recursionDepth;
    }
    
    public final synchronized Object readObjectDelegate() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_abstract_interface();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, true);
            throw marshal;
        }
        catch (final IndirectionException ex) {
            return this.activeRecursionMgr.getObject(ex.offset);
        }
    }
    
    final synchronized Object simpleReadObject(final Class clazz, final String s, final CodeBase codeBase, final int n) {
        final Object currentObject = this.currentObject;
        final ObjectStreamClass currentClassDesc = this.currentClassDesc;
        final Class currentClass = this.currentClass;
        final byte streamFormatVersion = this.streamFormatVersion;
        ++this.simpleReadDepth;
        Object o = null;
        try {
            if (this.vhandler.useFullValueDescription(clazz, s)) {
                o = this.inputObjectUsingFVD(clazz, s, codeBase, n);
            }
            else {
                o = this.inputObject(clazz, s, codeBase, n);
            }
            o = this.currentClassDesc.readResolve(o);
        }
        catch (final ClassNotFoundException ex) {
            IIOPInputStream.bridge.throwException(ex);
            return null;
        }
        catch (final IOException ex2) {
            IIOPInputStream.bridge.throwException(ex2);
            return null;
        }
        finally {
            --this.simpleReadDepth;
            this.currentObject = currentObject;
            this.currentClassDesc = currentClassDesc;
            this.currentClass = currentClass;
            this.streamFormatVersion = streamFormatVersion;
        }
        final IOException abortIOException = this.abortIOException;
        if (this.simpleReadDepth == 0) {
            this.abortIOException = null;
        }
        if (abortIOException != null) {
            IIOPInputStream.bridge.throwException(abortIOException);
            return null;
        }
        final ClassNotFoundException abortClassNotFoundException = this.abortClassNotFoundException;
        if (this.simpleReadDepth == 0) {
            this.abortClassNotFoundException = null;
        }
        if (abortClassNotFoundException != null) {
            IIOPInputStream.bridge.throwException(abortClassNotFoundException);
            return null;
        }
        return o;
    }
    
    public final synchronized void simpleSkipObject(final String s, final CodeBase codeBase) {
        final Object currentObject = this.currentObject;
        final ObjectStreamClass currentClassDesc = this.currentClassDesc;
        final Class currentClass = this.currentClass;
        final byte streamFormatVersion = this.streamFormatVersion;
        ++this.simpleReadDepth;
        try {
            this.skipObjectUsingFVD(s, codeBase);
        }
        catch (final ClassNotFoundException ex) {
            IIOPInputStream.bridge.throwException(ex);
            return;
        }
        catch (final IOException ex2) {
            IIOPInputStream.bridge.throwException(ex2);
            return;
        }
        finally {
            --this.simpleReadDepth;
            this.streamFormatVersion = streamFormatVersion;
            this.currentObject = currentObject;
            this.currentClassDesc = currentClassDesc;
            this.currentClass = currentClass;
        }
        final IOException abortIOException = this.abortIOException;
        if (this.simpleReadDepth == 0) {
            this.abortIOException = null;
        }
        if (abortIOException != null) {
            IIOPInputStream.bridge.throwException(abortIOException);
            return;
        }
        final ClassNotFoundException abortClassNotFoundException = this.abortClassNotFoundException;
        if (this.simpleReadDepth == 0) {
            this.abortClassNotFoundException = null;
        }
        if (abortClassNotFoundException != null) {
            IIOPInputStream.bridge.throwException(abortClassNotFoundException);
        }
    }
    
    @Override
    protected final Object readObjectOverride() throws OptionalDataException, ClassNotFoundException, IOException {
        return this.readObjectDelegate();
    }
    
    @Override
    final synchronized void defaultReadObjectDelegate() {
        try {
            if (this.currentObject == null || this.currentClassDesc == null) {
                throw new NotActiveException("defaultReadObjectDelegate");
            }
            if (!this.currentClassDesc.forClass().isAssignableFrom(this.currentObject.getClass())) {
                throw new IOException("Object Type mismatch");
            }
            if (this.defaultReadObjectFVDMembers != null && this.defaultReadObjectFVDMembers.length > 0) {
                this.inputClassFields(this.currentObject, this.currentClass, this.currentClassDesc, this.defaultReadObjectFVDMembers, this.cbSender);
            }
            else {
                final ObjectStreamField[] fieldsNoCopy = this.currentClassDesc.getFieldsNoCopy();
                if (fieldsNoCopy.length > 0) {
                    this.inputClassFields(this.currentObject, this.currentClass, fieldsNoCopy, this.cbSender);
                }
            }
        }
        catch (final NotActiveException ex) {
            IIOPInputStream.bridge.throwException(ex);
        }
        catch (final IOException ex2) {
            IIOPInputStream.bridge.throwException(ex2);
        }
        catch (final ClassNotFoundException ex3) {
            IIOPInputStream.bridge.throwException(ex3);
        }
    }
    
    public final boolean enableResolveObjectDelegate(final boolean b) {
        return false;
    }
    
    @Override
    public final void mark(final int n) {
        this.orbStream.mark(n);
    }
    
    @Override
    public final boolean markSupported() {
        return this.orbStream.markSupported();
    }
    
    @Override
    public final void reset() throws IOException {
        try {
            this.orbStream.reset();
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final int available() throws IOException {
        return 0;
    }
    
    @Override
    public final void close() throws IOException {
    }
    
    @Override
    public final int read() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_octet() << 0 & 0xFF;
        }
        catch (final MARSHAL marshal) {
            if (marshal.minor == 1330446344) {
                this.setState(IIOPInputStream.IN_READ_OBJECT_NO_MORE_OPT_DATA);
                return -1;
            }
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final int read(final byte[] array, final int n, final int n2) throws IOException {
        try {
            this.readObjectState.readData(this);
            this.orbStream.read_octet_array(array, n, n2);
            return n2;
        }
        catch (final MARSHAL marshal) {
            if (marshal.minor == 1330446344) {
                this.setState(IIOPInputStream.IN_READ_OBJECT_NO_MORE_OPT_DATA);
                return -1;
            }
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final boolean readBoolean() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_boolean();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final byte readByte() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_octet();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final char readChar() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_wchar();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final double readDouble() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_double();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final float readFloat() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_float();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    @Override
    public final void readFully(final byte[] array, final int n, final int n2) throws IOException {
        try {
            this.readObjectState.readData(this);
            this.orbStream.read_octet_array(array, n, n2);
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final int readInt() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_long();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final String readLine() throws IOException {
        throw new IOException("Method readLine not supported");
    }
    
    @Override
    public final long readLong() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_longlong();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final short readShort() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_short();
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    protected final void readStreamHeader() throws IOException, StreamCorruptedException {
    }
    
    @Override
    public final int readUnsignedByte() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_octet() << 0 & 0xFF;
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final int readUnsignedShort() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.orbStream.read_ushort() << 0 & 0xFFFF;
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    protected String internalReadUTF(final org.omg.CORBA.portable.InputStream inputStream) {
        return inputStream.read_wstring();
    }
    
    @Override
    public final String readUTF() throws IOException {
        try {
            this.readObjectState.readData(this);
            return this.internalReadUTF(this.orbStream);
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    private void handleOptionalDataMarshalException(final MARSHAL marshal, final boolean b) throws IOException {
        if (marshal.minor == 1330446344) {
            IOException optionalDataException;
            if (!b) {
                optionalDataException = new EOFException("No more optional data");
            }
            else {
                optionalDataException = this.createOptionalDataException();
            }
            optionalDataException.initCause(marshal);
            this.setState(IIOPInputStream.IN_READ_OBJECT_NO_MORE_OPT_DATA);
            throw optionalDataException;
        }
    }
    
    @Override
    public final synchronized void registerValidation(final ObjectInputValidation objectInputValidation, final int n) throws NotActiveException, InvalidObjectException {
        throw new Error("Method registerValidation not supported");
    }
    
    protected final Class resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        throw new IOException("Method resolveClass not supported");
    }
    
    @Override
    protected final Object resolveObject(final Object o) throws IOException {
        throw new IOException("Method resolveObject not supported");
    }
    
    @Override
    public final int skipBytes(final int n) throws IOException {
        try {
            this.readObjectState.readData(this);
            this.orbStream.read_octet_array(new byte[n], 0, n);
            return n;
        }
        catch (final MARSHAL marshal) {
            this.handleOptionalDataMarshalException(marshal, false);
            throw marshal;
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    private synchronized Object inputObject(final Class clazz, final String s, final CodeBase codeBase, final int n) throws IOException, ClassNotFoundException {
        this.currentClassDesc = ObjectStreamClass.lookup(clazz);
        this.currentClass = this.currentClassDesc.forClass();
        if (this.currentClass == null) {
            throw new ClassNotFoundException(this.currentClassDesc.getName());
        }
        try {
            if (Enum.class.isAssignableFrom(clazz)) {
                this.orbStream.read_long();
                return Enum.valueOf((Class<Enum>)clazz, (String)this.orbStream.read_value(String.class));
            }
            if (this.currentClassDesc.isExternalizable()) {
                try {
                    this.currentObject = ((this.currentClass == null) ? null : this.currentClassDesc.newInstance());
                    if (this.currentObject != null) {
                        this.activeRecursionMgr.addObject(n, this.currentObject);
                        this.readFormatVersion();
                        ((Externalizable)this.currentObject).readExternal(this);
                    }
                    return this.currentObject;
                }
                catch (final InvocationTargetException ex) {
                    final InvalidClassException ex2 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                    ex2.initCause(ex);
                    throw ex2;
                }
                catch (final UnsupportedOperationException ex3) {
                    final InvalidClassException ex4 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                    ex4.initCause(ex3);
                    throw ex4;
                }
                catch (final InstantiationException ex5) {
                    final InvalidClassException ex6 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                    ex6.initCause(ex5);
                    throw ex6;
                }
            }
            final ObjectStreamClass currentClassDesc = this.currentClassDesc;
            final Class currentClass = this.currentClass;
            final int spClass = this.spClass;
            if (this.currentClass.getName().equals("java.lang.String")) {
                return this.readUTF();
            }
            ObjectStreamClass objectStreamClass = this.currentClassDesc;
            Class clazz2 = this.currentClass;
            while (objectStreamClass != null && objectStreamClass.isSerializable()) {
                Class<?> forClass;
                Class superclass;
                for (forClass = objectStreamClass.forClass(), superclass = clazz2; superclass != null && forClass != superclass; superclass = superclass.getSuperclass()) {}
                ++this.spClass;
                if (this.spClass >= this.classes.length) {
                    final int n2 = this.classes.length * 2;
                    final Class[] classes = new Class[n2];
                    final ObjectStreamClass[] classdesc = new ObjectStreamClass[n2];
                    System.arraycopy(this.classes, 0, classes, 0, this.classes.length);
                    System.arraycopy(this.classdesc, 0, classdesc, 0, this.classes.length);
                    this.classes = classes;
                    this.classdesc = classdesc;
                }
                if (superclass == null) {
                    this.classdesc[this.spClass] = objectStreamClass;
                    this.classes[this.spClass] = null;
                }
                else {
                    this.classdesc[this.spClass] = objectStreamClass;
                    this.classes[this.spClass] = superclass;
                    clazz2 = superclass.getSuperclass();
                }
                objectStreamClass = objectStreamClass.getSuperclass();
            }
            try {
                this.currentObject = ((this.currentClass == null) ? null : this.currentClassDesc.newInstance());
                this.activeRecursionMgr.addObject(n, this.currentObject);
            }
            catch (final InvocationTargetException ex7) {
                final InvalidClassException ex8 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                ex8.initCause(ex7);
                throw ex8;
            }
            catch (final UnsupportedOperationException ex9) {
                final InvalidClassException ex10 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                ex10.initCause(ex9);
                throw ex10;
            }
            catch (final InstantiationException ex11) {
                final InvalidClassException ex12 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                ex12.initCause(ex11);
                throw ex12;
            }
            try {
                this.spClass = this.spClass;
                while (this.spClass > spClass) {
                    this.currentClassDesc = this.classdesc[this.spClass];
                    this.currentClass = this.classes[this.spClass];
                    if (this.classes[this.spClass] != null) {
                        final ReadObjectState readObjectState = this.readObjectState;
                        this.setState(IIOPInputStream.DEFAULT_STATE);
                        try {
                            if (this.currentClassDesc.hasWriteObject()) {
                                this.readFormatVersion();
                                this.readObjectState.beginUnmarshalCustomValue(this, this.readBoolean(), this.currentClassDesc.readObjectMethod != null);
                            }
                            else if (this.currentClassDesc.hasReadObject()) {
                                this.setState(IIOPInputStream.IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                            }
                            if (!this.invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass) || this.readObjectState == IIOPInputStream.IN_READ_OBJECT_DEFAULTS_SENT) {
                                final ObjectStreamField[] fieldsNoCopy = this.currentClassDesc.getFieldsNoCopy();
                                if (fieldsNoCopy.length > 0) {
                                    this.inputClassFields(this.currentObject, this.currentClass, fieldsNoCopy, codeBase);
                                }
                            }
                            if (this.currentClassDesc.hasWriteObject()) {
                                this.readObjectState.endUnmarshalCustomValue(this);
                            }
                        }
                        finally {
                            this.setState(readObjectState);
                        }
                    }
                    else {
                        final ObjectStreamField[] fieldsNoCopy2 = this.currentClassDesc.getFieldsNoCopy();
                        if (fieldsNoCopy2.length > 0) {
                            this.inputClassFields(null, this.currentClass, fieldsNoCopy2, codeBase);
                        }
                    }
                    --this.spClass;
                }
            }
            finally {
                this.spClass = spClass;
            }
        }
        finally {
            this.activeRecursionMgr.removeObject(n);
        }
        return this.currentObject;
    }
    
    private Vector getOrderedDescriptions(final String s, final CodeBase codeBase) {
        final Vector vector = new Vector();
        if (codeBase == null) {
            return vector;
        }
        for (FullValueDescription fullValueDescription = codeBase.meta(s); fullValueDescription != null; fullValueDescription = codeBase.meta(fullValueDescription.base_value)) {
            vector.insertElementAt(fullValueDescription, 0);
            if (fullValueDescription.base_value == null || "".equals(fullValueDescription.base_value)) {
                return vector;
            }
        }
        return vector;
    }
    
    private synchronized Object inputObjectUsingFVD(final Class currentClass, final String s, final CodeBase codeBase, final int n) throws IOException, ClassNotFoundException {
        final int spClass = this.spClass;
        try {
            this.currentClassDesc = ObjectStreamClass.lookup(currentClass);
            this.currentClass = currentClass;
            if (this.currentClassDesc.isExternalizable()) {
                try {
                    this.currentObject = ((this.currentClass == null) ? null : this.currentClassDesc.newInstance());
                    if (this.currentObject != null) {
                        this.activeRecursionMgr.addObject(n, this.currentObject);
                        this.readFormatVersion();
                        ((Externalizable)this.currentObject).readExternal(this);
                    }
                    return this.currentObject;
                }
                catch (final InvocationTargetException ex) {
                    final InvalidClassException ex2 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                    ex2.initCause(ex);
                    throw ex2;
                }
                catch (final UnsupportedOperationException ex3) {
                    final InvalidClassException ex4 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                    ex4.initCause(ex3);
                    throw ex4;
                }
                catch (final InstantiationException ex5) {
                    final InvalidClassException ex6 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                    ex6.initCause(ex5);
                    throw ex6;
                }
            }
            ObjectStreamClass objectStreamClass = this.currentClassDesc;
            Class clazz = this.currentClass;
            while (objectStreamClass != null && objectStreamClass.isSerializable()) {
                Class<?> forClass;
                Class superclass;
                for (forClass = objectStreamClass.forClass(), superclass = clazz; superclass != null && forClass != superclass; superclass = superclass.getSuperclass()) {}
                ++this.spClass;
                if (this.spClass >= this.classes.length) {
                    final int n2 = this.classes.length * 2;
                    final Class[] classes = new Class[n2];
                    final ObjectStreamClass[] classdesc = new ObjectStreamClass[n2];
                    System.arraycopy(this.classes, 0, classes, 0, this.classes.length);
                    System.arraycopy(this.classdesc, 0, classdesc, 0, this.classes.length);
                    this.classes = classes;
                    this.classdesc = classdesc;
                }
                if (superclass == null) {
                    this.classdesc[this.spClass] = objectStreamClass;
                    this.classes[this.spClass] = null;
                }
                else {
                    this.classdesc[this.spClass] = objectStreamClass;
                    this.classes[this.spClass] = superclass;
                    clazz = superclass.getSuperclass();
                }
                objectStreamClass = objectStreamClass.getSuperclass();
            }
            try {
                this.currentObject = ((this.currentClass == null) ? null : this.currentClassDesc.newInstance());
                this.activeRecursionMgr.addObject(n, this.currentObject);
            }
            catch (final InvocationTargetException ex7) {
                final InvalidClassException ex8 = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
                ex8.initCause(ex7);
                throw ex8;
            }
            catch (final UnsupportedOperationException ex9) {
                final InvalidClassException ex10 = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
                ex10.initCause(ex9);
                throw ex10;
            }
            catch (final InstantiationException ex11) {
                final InvalidClassException ex12 = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
                ex12.initCause(ex11);
                throw ex12;
            }
            final Enumeration elements = this.getOrderedDescriptions(s, codeBase).elements();
            while (elements.hasMoreElements() && this.spClass > spClass) {
                FullValueDescription fullValueDescription = (FullValueDescription)elements.nextElement();
                String s2 = this.vhandler.getClassName(fullValueDescription.id);
                String s3 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));
                while (this.spClass > spClass && !s2.equals(s3)) {
                    final int nextClass = this.findNextClass(s2, this.classes, this.spClass, spClass);
                    if (nextClass != -1) {
                        this.spClass = nextClass;
                        this.currentClass = this.classes[this.spClass];
                        s3 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));
                    }
                    else {
                        if (fullValueDescription.is_custom) {
                            this.readFormatVersion();
                            if (this.readBoolean()) {
                                this.inputClassFields(null, null, null, fullValueDescription.members, codeBase);
                            }
                            if (this.getStreamFormatVersion() == 2) {
                                ((ValueInputStream)this.getOrbStream()).start_value();
                                ((ValueInputStream)this.getOrbStream()).end_value();
                            }
                        }
                        else {
                            this.inputClassFields(null, this.currentClass, null, fullValueDescription.members, codeBase);
                        }
                        if (!elements.hasMoreElements()) {
                            return this.currentObject;
                        }
                        fullValueDescription = (FullValueDescription)elements.nextElement();
                        s2 = this.vhandler.getClassName(fullValueDescription.id);
                    }
                }
                final ObjectStreamClass lookup = ObjectStreamClass.lookup(this.currentClass);
                this.currentClassDesc = lookup;
                final ObjectStreamClass objectStreamClass2 = lookup;
                if (!s3.equals("java.lang.Object")) {
                    final ReadObjectState readObjectState = this.readObjectState;
                    this.setState(IIOPInputStream.DEFAULT_STATE);
                    try {
                        if (fullValueDescription.is_custom) {
                            this.readFormatVersion();
                            this.readObjectState.beginUnmarshalCustomValue(this, this.readBoolean(), this.currentClassDesc.readObjectMethod != null);
                        }
                        boolean invokeObjectReader = false;
                        try {
                            if (!fullValueDescription.is_custom && this.currentClassDesc.hasReadObject()) {
                                this.setState(IIOPInputStream.IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                            }
                            this.defaultReadObjectFVDMembers = fullValueDescription.members;
                            invokeObjectReader = this.invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass);
                        }
                        finally {
                            this.defaultReadObjectFVDMembers = null;
                        }
                        if (!invokeObjectReader || this.readObjectState == IIOPInputStream.IN_READ_OBJECT_DEFAULTS_SENT) {
                            this.inputClassFields(this.currentObject, this.currentClass, objectStreamClass2, fullValueDescription.members, codeBase);
                        }
                        if (fullValueDescription.is_custom) {
                            this.readObjectState.endUnmarshalCustomValue(this);
                        }
                    }
                    finally {
                        this.setState(readObjectState);
                    }
                    final Class[] classes2 = this.classes;
                    final int spClass2 = this.spClass - 1;
                    this.spClass = spClass2;
                    this.currentClass = classes2[spClass2];
                }
                else {
                    this.inputClassFields(null, this.currentClass, null, fullValueDescription.members, codeBase);
                    while (elements.hasMoreElements()) {
                        final FullValueDescription fullValueDescription2 = (FullValueDescription)elements.nextElement();
                        if (fullValueDescription2.is_custom) {
                            this.skipCustomUsingFVD(fullValueDescription2.members, codeBase);
                        }
                        else {
                            this.inputClassFields(null, this.currentClass, null, fullValueDescription2.members, codeBase);
                        }
                    }
                }
            }
            while (elements.hasMoreElements()) {
                final FullValueDescription fullValueDescription3 = (FullValueDescription)elements.nextElement();
                if (fullValueDescription3.is_custom) {
                    this.skipCustomUsingFVD(fullValueDescription3.members, codeBase);
                }
                else {
                    this.throwAwayData(fullValueDescription3.members, codeBase);
                }
            }
            return this.currentObject;
        }
        finally {
            this.spClass = spClass;
            this.activeRecursionMgr.removeObject(n);
        }
    }
    
    private Object skipObjectUsingFVD(final String s, final CodeBase codeBase) throws IOException, ClassNotFoundException {
        final Enumeration elements = this.getOrderedDescriptions(s, codeBase).elements();
        while (elements.hasMoreElements()) {
            final FullValueDescription fullValueDescription = (FullValueDescription)elements.nextElement();
            if (!this.vhandler.getClassName(fullValueDescription.id).equals("java.lang.Object")) {
                if (fullValueDescription.is_custom) {
                    this.readFormatVersion();
                    if (this.readBoolean()) {
                        this.inputClassFields(null, null, null, fullValueDescription.members, codeBase);
                    }
                    if (this.getStreamFormatVersion() != 2) {
                        continue;
                    }
                    ((ValueInputStream)this.getOrbStream()).start_value();
                    ((ValueInputStream)this.getOrbStream()).end_value();
                }
                else {
                    this.inputClassFields(null, null, null, fullValueDescription.members, codeBase);
                }
            }
        }
        return null;
    }
    
    private int findNextClass(final String s, final Class[] array, final int n, final int n2) {
        for (int i = n; i > n2; --i) {
            if (s.equals(array[i].getName())) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean invokeObjectReader(final ObjectStreamClass objectStreamClass, final Object o, final Class clazz) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        if (objectStreamClass.readObjectMethod == null) {
            return false;
        }
        try {
            objectStreamClass.readObjectMethod.invoke(o, this.readObjectArgList);
            return true;
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (targetException instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)targetException;
            }
            if (targetException instanceof IOException) {
                throw (IOException)targetException;
            }
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            if (targetException instanceof Error) {
                throw (Error)targetException;
            }
            throw new Error("internal error");
        }
        catch (final IllegalAccessException ex2) {
            return false;
        }
    }
    
    private void resetStream() throws IOException {
        if (this.classes == null) {
            this.classes = new Class[20];
        }
        else {
            for (int i = 0; i < this.classes.length; ++i) {
                this.classes[i] = null;
            }
        }
        if (this.classdesc == null) {
            this.classdesc = new ObjectStreamClass[20];
        }
        else {
            for (int j = 0; j < this.classdesc.length; ++j) {
                this.classdesc[j] = null;
            }
        }
        this.spClass = 0;
        if (this.callbacks != null) {
            this.callbacks.setSize(0);
        }
    }
    
    private void inputPrimitiveField(final Object o, final Class clazz, final ObjectStreamField objectStreamField) throws InvalidClassException, IOException {
        try {
            switch (objectStreamField.getTypeCode()) {
                case 'B': {
                    final byte read_octet = this.orbStream.read_octet();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putByte(o, objectStreamField.getFieldID(), read_octet);
                        break;
                    }
                    break;
                }
                case 'Z': {
                    final boolean read_boolean = this.orbStream.read_boolean();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putBoolean(o, objectStreamField.getFieldID(), read_boolean);
                        break;
                    }
                    break;
                }
                case 'C': {
                    final char read_wchar = this.orbStream.read_wchar();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putChar(o, objectStreamField.getFieldID(), read_wchar);
                        break;
                    }
                    break;
                }
                case 'S': {
                    final short read_short = this.orbStream.read_short();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putShort(o, objectStreamField.getFieldID(), read_short);
                        break;
                    }
                    break;
                }
                case 'I': {
                    final int read_long = this.orbStream.read_long();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putInt(o, objectStreamField.getFieldID(), read_long);
                        break;
                    }
                    break;
                }
                case 'J': {
                    final long read_longlong = this.orbStream.read_longlong();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putLong(o, objectStreamField.getFieldID(), read_longlong);
                        break;
                    }
                    break;
                }
                case 'F': {
                    final float read_float = this.orbStream.read_float();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putFloat(o, objectStreamField.getFieldID(), read_float);
                        break;
                    }
                    break;
                }
                case 'D': {
                    final double read_double = this.orbStream.read_double();
                    if (objectStreamField.getField() != null) {
                        IIOPInputStream.bridge.putDouble(o, objectStreamField.getFieldID(), read_double);
                        break;
                    }
                    break;
                }
                default: {
                    throw new InvalidClassException(clazz.getName());
                }
            }
        }
        catch (final IllegalArgumentException ex) {
            final ClassCastException ex2 = new ClassCastException("Assigning instance of class " + objectStreamField.getType().getName() + " to field " + this.currentClassDesc.getName() + '#' + objectStreamField.getField().getName());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private Object inputObjectField(final ValueMember valueMember, final CodeBase codeBase) throws IndirectionException, ClassNotFoundException, IOException, StreamCorruptedException {
        final String id = valueMember.id;
        Class classFromType;
        try {
            classFromType = this.vhandler.getClassFromType(id);
        }
        catch (final ClassNotFoundException ex) {
            classFromType = null;
        }
        String signature = null;
        if (classFromType != null) {
            signature = ValueUtility.getSignature(valueMember);
        }
        Object o = null;
        if (signature != null && (signature.equals("Ljava/lang/Object;") || signature.equals("Ljava/io/Serializable;") || signature.equals("Ljava/io/Externalizable;"))) {
            o = Util.readAny(this.orbStream);
        }
        else {
            int n = 2;
            if (!this.vhandler.isSequence(id)) {
                if (valueMember.type.kind().value() == IIOPInputStream.kRemoteTypeCode.kind().value()) {
                    n = 0;
                }
                else if (classFromType != null && classFromType.isInterface() && (this.vhandler.isAbstractBase(classFromType) || ObjectStreamClassCorbaExt.isAbstractInterface(classFromType))) {
                    n = 1;
                }
            }
            switch (n) {
                case 0: {
                    if (classFromType != null) {
                        o = Utility.readObjectAndNarrow(this.orbStream, classFromType);
                        break;
                    }
                    o = this.orbStream.read_Object();
                    break;
                }
                case 1: {
                    if (classFromType != null) {
                        o = Utility.readAbstractAndNarrow(this.orbStream, classFromType);
                        break;
                    }
                    o = this.orbStream.read_abstract_interface();
                    break;
                }
                case 2: {
                    if (classFromType != null) {
                        o = this.orbStream.read_value(classFromType);
                        break;
                    }
                    o = this.orbStream.read_value();
                    break;
                }
                default: {
                    throw new StreamCorruptedException("Unknown callType: " + n);
                }
            }
        }
        return o;
    }
    
    private Object inputObjectField(final ObjectStreamField objectStreamField) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IndirectionException, IOException {
        if (ObjectStreamClassCorbaExt.isAny(objectStreamField.getTypeString())) {
            return Util.readAny(this.orbStream);
        }
        Class clazz2;
        final Class clazz = clazz2 = objectStreamField.getType();
        int n = 2;
        boolean b = false;
        if (clazz.isInterface()) {
            boolean b2 = false;
            if (Remote.class.isAssignableFrom(clazz)) {
                n = 0;
            }
            else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz)) {
                n = 0;
                b2 = true;
            }
            else if (this.vhandler.isAbstractBase(clazz)) {
                n = 1;
                b2 = true;
            }
            else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz)) {
                n = 1;
            }
            if (b2) {
                try {
                    clazz2 = Utility.loadStubClass(this.vhandler.createForAnyType(clazz), Util.getCodebase(clazz), clazz);
                }
                catch (final ClassNotFoundException ex) {
                    b = true;
                }
            }
            else {
                b = true;
            }
        }
        Object o = null;
        switch (n) {
            case 0: {
                if (!b) {
                    o = this.orbStream.read_Object(clazz2);
                    break;
                }
                o = Utility.readObjectAndNarrow(this.orbStream, clazz2);
                break;
            }
            case 1: {
                if (!b) {
                    o = this.orbStream.read_abstract_interface(clazz2);
                    break;
                }
                o = Utility.readAbstractAndNarrow(this.orbStream, clazz2);
                break;
            }
            case 2: {
                o = this.orbStream.read_value(clazz2);
                break;
            }
            default: {
                throw new StreamCorruptedException("Unknown callType: " + n);
            }
        }
        return o;
    }
    
    private final boolean mustUseRemoteValueMembers() {
        return this.defaultReadObjectFVDMembers != null;
    }
    
    @Override
    void readFields(final Map map) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        if (this.mustUseRemoteValueMembers()) {
            this.inputRemoteMembersForReadFields(map);
        }
        else {
            this.inputCurrentClassFieldsForReadFields(map);
        }
    }
    
    private final void inputRemoteMembersForReadFields(final Map map) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        final ValueMember[] defaultReadObjectFVDMembers = this.defaultReadObjectFVDMembers;
        try {
            for (int i = 0; i < defaultReadObjectFVDMembers.length; ++i) {
                switch (defaultReadObjectFVDMembers[i].type.kind().value()) {
                    case 10: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Byte(this.orbStream.read_octet()));
                        break;
                    }
                    case 8: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Boolean(this.orbStream.read_boolean()));
                        break;
                    }
                    case 9:
                    case 26: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Character(this.orbStream.read_wchar()));
                        break;
                    }
                    case 2: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Short(this.orbStream.read_short()));
                        break;
                    }
                    case 3: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Integer(this.orbStream.read_long()));
                        break;
                    }
                    case 23: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Long(this.orbStream.read_longlong()));
                        break;
                    }
                    case 6: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Float(this.orbStream.read_float()));
                        break;
                    }
                    case 7: {
                        map.put(defaultReadObjectFVDMembers[i].name, new Double(this.orbStream.read_double()));
                        break;
                    }
                    case 14:
                    case 29:
                    case 30: {
                        Object o;
                        try {
                            o = this.inputObjectField(defaultReadObjectFVDMembers[i], this.cbSender);
                        }
                        catch (final IndirectionException ex) {
                            o = this.activeRecursionMgr.getObject(ex.offset);
                        }
                        map.put(defaultReadObjectFVDMembers[i].name, o);
                        break;
                    }
                    default: {
                        throw new StreamCorruptedException("Unknown kind: " + defaultReadObjectFVDMembers[i].type.kind().value());
                    }
                }
            }
        }
        catch (final Throwable t) {
            final StreamCorruptedException ex2 = new StreamCorruptedException(t.getMessage());
            ex2.initCause(t);
            throw ex2;
        }
    }
    
    private final void inputCurrentClassFieldsForReadFields(final Map map) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        final ObjectStreamField[] fieldsNoCopy = this.currentClassDesc.getFieldsNoCopy();
        final int n = fieldsNoCopy.length - this.currentClassDesc.objFields;
        for (int i = 0; i < n; ++i) {
            switch (fieldsNoCopy[i].getTypeCode()) {
                case 'B': {
                    map.put(fieldsNoCopy[i].getName(), new Byte(this.orbStream.read_octet()));
                    break;
                }
                case 'Z': {
                    map.put(fieldsNoCopy[i].getName(), new Boolean(this.orbStream.read_boolean()));
                    break;
                }
                case 'C': {
                    map.put(fieldsNoCopy[i].getName(), new Character(this.orbStream.read_wchar()));
                    break;
                }
                case 'S': {
                    map.put(fieldsNoCopy[i].getName(), new Short(this.orbStream.read_short()));
                    break;
                }
                case 'I': {
                    map.put(fieldsNoCopy[i].getName(), new Integer(this.orbStream.read_long()));
                    break;
                }
                case 'J': {
                    map.put(fieldsNoCopy[i].getName(), new Long(this.orbStream.read_longlong()));
                    break;
                }
                case 'F': {
                    map.put(fieldsNoCopy[i].getName(), new Float(this.orbStream.read_float()));
                    break;
                }
                case 'D': {
                    map.put(fieldsNoCopy[i].getName(), new Double(this.orbStream.read_double()));
                    break;
                }
                default: {
                    throw new InvalidClassException(this.currentClassDesc.getName());
                }
            }
        }
        if (this.currentClassDesc.objFields > 0) {
            for (int j = n; j < fieldsNoCopy.length; ++j) {
                Object o;
                try {
                    o = this.inputObjectField(fieldsNoCopy[j]);
                }
                catch (final IndirectionException ex) {
                    o = this.activeRecursionMgr.getObject(ex.offset);
                }
                map.put(fieldsNoCopy[j].getName(), o);
            }
        }
    }
    
    private void inputClassFields(final Object o, final Class<?> clazz, final ObjectStreamField[] array, final CodeBase codeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        final int n = array.length - this.currentClassDesc.objFields;
        if (o != null) {
            for (int i = 0; i < n; ++i) {
                this.inputPrimitiveField(o, clazz, array[i]);
            }
        }
        if (this.currentClassDesc.objFields > 0) {
            for (int j = n; j < array.length; ++j) {
                Object o2;
                try {
                    o2 = this.inputObjectField(array[j]);
                }
                catch (final IndirectionException ex) {
                    o2 = this.activeRecursionMgr.getObject(ex.offset);
                }
                if (o != null) {
                    if (array[j].getField() != null) {
                        try {
                            final Class clazz2 = array[j].getClazz();
                            if (o2 != null && !clazz2.isAssignableFrom(o2.getClass())) {
                                throw new IllegalArgumentException("Field mismatch");
                            }
                            final String name = array[j].getName();
                            Field declaredField;
                            try {
                                declaredField = getDeclaredField(clazz, name);
                            }
                            catch (final PrivilegedActionException ex2) {
                                throw new IllegalArgumentException(ex2.getException());
                            }
                            catch (final SecurityException ex3) {
                                throw new IllegalArgumentException(ex3);
                            }
                            catch (final NullPointerException ex4) {
                                continue;
                            }
                            catch (final NoSuchFieldException ex5) {
                                continue;
                            }
                            if (declaredField != null) {
                                if (!declaredField.getType().isAssignableFrom(clazz2)) {
                                    throw new IllegalArgumentException("Field Type mismatch");
                                }
                                if (o2 != null && !clazz2.isInstance(o2)) {
                                    throw new IllegalArgumentException();
                                }
                                IIOPInputStream.bridge.putObject(o, array[j].getFieldID(), o2);
                            }
                        }
                        catch (final IllegalArgumentException ex6) {
                            String name2 = "null";
                            String name3 = "null";
                            String name4 = "null";
                            if (o2 != null) {
                                name2 = o2.getClass().getName();
                            }
                            if (this.currentClassDesc != null) {
                                name3 = this.currentClassDesc.getName();
                            }
                            if (array[j] != null && array[j].getField() != null) {
                                name4 = array[j].getField().getName();
                            }
                            final ClassCastException ex7 = new ClassCastException("Assigning instance of class " + name2 + " to field " + name3 + '#' + name4);
                            ex7.initCause(ex6);
                            throw ex7;
                        }
                    }
                }
            }
        }
    }
    
    private void inputClassFields(final Object o, final Class clazz, final ObjectStreamClass objectStreamClass, final ValueMember[] array, final CodeBase codeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        try {
            for (int i = 0; i < array.length; ++i) {
                try {
                    Label_0677: {
                        switch (array[i].type.kind().value()) {
                            case 10: {
                                final byte read_octet = this.orbStream.read_octet();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setByteField(o, clazz, array[i].name, read_octet);
                                }
                                break Label_0677;
                            }
                            case 8: {
                                final boolean read_boolean = this.orbStream.read_boolean();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setBooleanField(o, clazz, array[i].name, read_boolean);
                                }
                                break Label_0677;
                            }
                            case 9:
                            case 26: {
                                final char read_wchar = this.orbStream.read_wchar();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setCharField(o, clazz, array[i].name, read_wchar);
                                }
                                break Label_0677;
                            }
                            case 2: {
                                final short read_short = this.orbStream.read_short();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setShortField(o, clazz, array[i].name, read_short);
                                }
                                break Label_0677;
                            }
                            case 3: {
                                final int read_long = this.orbStream.read_long();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setIntField(o, clazz, array[i].name, read_long);
                                }
                                break Label_0677;
                            }
                            case 23: {
                                final long read_longlong = this.orbStream.read_longlong();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setLongField(o, clazz, array[i].name, read_longlong);
                                }
                                break Label_0677;
                            }
                            case 6: {
                                final float read_float = this.orbStream.read_float();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setFloatField(o, clazz, array[i].name, read_float);
                                }
                                break Label_0677;
                            }
                            case 7: {
                                final double read_double = this.orbStream.read_double();
                                if (o != null && objectStreamClass.hasField(array[i])) {
                                    setDoubleField(o, clazz, array[i].name, read_double);
                                }
                                break Label_0677;
                            }
                            case 14:
                            case 29:
                            case 30: {
                                Object o2;
                                try {
                                    o2 = this.inputObjectField(array[i], codeBase);
                                }
                                catch (final IndirectionException ex) {
                                    o2 = this.activeRecursionMgr.getObject(ex.offset);
                                }
                                if (o == null) {
                                    continue;
                                }
                                try {
                                    if (objectStreamClass.hasField(array[i])) {
                                        setObjectField(o, clazz, array[i].name, o2);
                                    }
                                    break Label_0677;
                                }
                                catch (final IllegalArgumentException ex2) {
                                    final ClassCastException ex3 = new ClassCastException("Assigning instance of class " + o2.getClass().getName() + " to field " + array[i].name);
                                    ex3.initCause(ex2);
                                    throw ex3;
                                }
                                break;
                            }
                        }
                        throw new StreamCorruptedException("Unknown kind: " + array[i].type.kind().value());
                    }
                }
                catch (final IllegalArgumentException ex4) {
                    final ClassCastException ex5 = new ClassCastException("Assigning instance of class " + array[i].id + " to field " + this.currentClassDesc.getName() + '#' + array[i].name);
                    ex5.initCause(ex4);
                    throw ex5;
                }
            }
        }
        catch (final Throwable t) {
            final StreamCorruptedException ex6 = new StreamCorruptedException(t.getMessage());
            ex6.initCause(t);
            throw ex6;
        }
    }
    
    private void skipCustomUsingFVD(final ValueMember[] array, final CodeBase codeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        this.readFormatVersion();
        if (this.readBoolean()) {
            this.throwAwayData(array, codeBase);
        }
        if (this.getStreamFormatVersion() == 2) {
            ((ValueInputStream)this.getOrbStream()).start_value();
            ((ValueInputStream)this.getOrbStream()).end_value();
        }
    }
    
    private void throwAwayData(final ValueMember[] array, final CodeBase codeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
        for (int i = 0; i < array.length; ++i) {
            try {
                Label_0546: {
                    switch (array[i].type.kind().value()) {
                        case 10: {
                            this.orbStream.read_octet();
                            break Label_0546;
                        }
                        case 8: {
                            this.orbStream.read_boolean();
                            break Label_0546;
                        }
                        case 9:
                        case 26: {
                            this.orbStream.read_wchar();
                            break Label_0546;
                        }
                        case 2: {
                            this.orbStream.read_short();
                            break Label_0546;
                        }
                        case 3: {
                            this.orbStream.read_long();
                            break Label_0546;
                        }
                        case 23: {
                            this.orbStream.read_longlong();
                            break Label_0546;
                        }
                        case 6: {
                            this.orbStream.read_float();
                            break Label_0546;
                        }
                        case 7: {
                            this.orbStream.read_double();
                            break Label_0546;
                        }
                        case 14:
                        case 29:
                        case 30: {
                            final String id = array[i].id;
                            Class classFromType;
                            try {
                                classFromType = this.vhandler.getClassFromType(id);
                            }
                            catch (final ClassNotFoundException ex) {
                                classFromType = null;
                            }
                            String signature = null;
                            if (classFromType != null) {
                                signature = ValueUtility.getSignature(array[i]);
                            }
                            try {
                                if (signature != null && (signature.equals("Ljava/lang/Object;") || signature.equals("Ljava/io/Serializable;") || signature.equals("Ljava/io/Externalizable;"))) {
                                    Util.readAny(this.orbStream);
                                }
                                else {
                                    int n = 2;
                                    if (!this.vhandler.isSequence(id)) {
                                        final FullValueDescription meta = codeBase.meta(array[i].id);
                                        if (IIOPInputStream.kRemoteTypeCode == array[i].type) {
                                            n = 0;
                                        }
                                        else if (meta.is_abstract) {
                                            n = 1;
                                        }
                                    }
                                    switch (n) {
                                        case 0: {
                                            this.orbStream.read_Object();
                                            break;
                                        }
                                        case 1: {
                                            this.orbStream.read_abstract_interface();
                                            break;
                                        }
                                        case 2: {
                                            if (classFromType != null) {
                                                this.orbStream.read_value(classFromType);
                                                break;
                                            }
                                            this.orbStream.read_value();
                                            break;
                                        }
                                        default: {
                                            throw new StreamCorruptedException("Unknown callType: " + n);
                                        }
                                    }
                                }
                                break Label_0546;
                            }
                            catch (final IndirectionException ex2) {
                                continue;
                            }
                            break;
                        }
                    }
                    throw new StreamCorruptedException("Unknown kind: " + array[i].type.kind().value());
                }
            }
            catch (final IllegalArgumentException ex3) {
                final ClassCastException ex4 = new ClassCastException("Assigning instance of class " + array[i].id + " to field " + this.currentClassDesc.getName() + '#' + array[i].name);
                ex4.initCause(ex3);
                throw ex4;
            }
        }
    }
    
    private static void setObjectField(final Object o, final Class<?> clazz, final String s, final Object o2) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            final Class<?> type = declaredField.getType();
            if (o2 != null && !type.isInstance(o2)) {
                throw new Exception();
            }
            IIOPInputStream.bridge.putObject(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), o2);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetObjectField(ex, s, o.toString(), o2.toString());
            }
            throw IIOPInputStream.utilWrapper.errorSetObjectField(ex, s, "null " + clazz.getName() + " object", o2.toString());
        }
    }
    
    private static void setBooleanField(final Object o, final Class<?> clazz, final String s, final boolean b) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Boolean.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putBoolean(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), b);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetBooleanField(ex, s, o.toString(), new Boolean(b));
            }
            throw IIOPInputStream.utilWrapper.errorSetBooleanField(ex, s, "null " + clazz.getName() + " object", new Boolean(b));
        }
    }
    
    private static void setByteField(final Object o, final Class<?> clazz, final String s, final byte b) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Byte.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putByte(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), b);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetByteField(ex, s, o.toString(), new Byte(b));
            }
            throw IIOPInputStream.utilWrapper.errorSetByteField(ex, s, "null " + clazz.getName() + " object", new Byte(b));
        }
    }
    
    private static void setCharField(final Object o, final Class<?> clazz, final String s, final char c) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Character.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putChar(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), c);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetCharField(ex, s, o.toString(), new Character(c));
            }
            throw IIOPInputStream.utilWrapper.errorSetCharField(ex, s, "null " + clazz.getName() + " object", new Character(c));
        }
    }
    
    private static void setShortField(final Object o, final Class<?> clazz, final String s, final short n) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Short.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putShort(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), n);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetShortField(ex, s, o.toString(), new Short(n));
            }
            throw IIOPInputStream.utilWrapper.errorSetShortField(ex, s, "null " + clazz.getName() + " object", new Short(n));
        }
    }
    
    private static void setIntField(final Object o, final Class<?> clazz, final String s, final int n) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Integer.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putInt(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), n);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetIntField(ex, s, o.toString(), new Integer(n));
            }
            throw IIOPInputStream.utilWrapper.errorSetIntField(ex, s, "null " + clazz.getName() + " object", new Integer(n));
        }
    }
    
    private static void setLongField(final Object o, final Class<?> clazz, final String s, final long n) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Long.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putLong(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), n);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetLongField(ex, s, o.toString(), new Long(n));
            }
            throw IIOPInputStream.utilWrapper.errorSetLongField(ex, s, "null " + clazz.getName() + " object", new Long(n));
        }
    }
    
    private static void setFloatField(final Object o, final Class<?> clazz, final String s, final float n) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Float.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putFloat(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), n);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetFloatField(ex, s, o.toString(), new Float(n));
            }
            throw IIOPInputStream.utilWrapper.errorSetFloatField(ex, s, "null " + clazz.getName() + " object", new Float(n));
        }
    }
    
    private static void setDoubleField(final Object o, final Class<?> clazz, final String s, final double n) {
        try {
            final Field declaredField = getDeclaredField(clazz, s);
            if (declaredField == null || declaredField.getType() != Double.TYPE) {
                throw new InvalidObjectException("Field Type mismatch");
            }
            IIOPInputStream.bridge.putDouble(o, IIOPInputStream.bridge.objectFieldOffset(declaredField), n);
        }
        catch (final Exception ex) {
            if (o != null) {
                throw IIOPInputStream.utilWrapper.errorSetDoubleField(ex, s, o.toString(), new Double(n));
            }
            throw IIOPInputStream.utilWrapper.errorSetDoubleField(ex, s, "null " + clazz.getName() + " object", new Double(n));
        }
    }
    
    private static Field getDeclaredField(final Class<?> clazz, final String s) throws PrivilegedActionException, NoSuchFieldException, SecurityException {
        if (System.getSecurityManager() == null) {
            return clazz.getDeclaredField(s);
        }
        return AccessController.doPrivileged((PrivilegedExceptionAction<Field>)new PrivilegedExceptionAction<Field>() {
            @Override
            public Field run() throws NoSuchFieldException {
                return clazz.getDeclaredField(s);
            }
        });
    }
    
    static {
        IIOPInputStream.bridge = AccessController.doPrivileged((PrivilegedAction<Bridge>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Bridge.get();
            }
        });
        IIOPInputStream.omgWrapper = OMGSystemException.get("rpc.encoding");
        IIOPInputStream.utilWrapper = UtilSystemException.get("rpc.encoding");
        kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
        kValueTypeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
        OPT_DATA_EXCEPTION_CTOR = getOptDataExceptionCtor();
    }
    
    static class ActiveRecursionManager
    {
        private Map<Integer, Object> offsetToObjectMap;
        
        public ActiveRecursionManager() {
            this.offsetToObjectMap = new HashMap<Integer, Object>();
        }
        
        public void addObject(final int n, final Object o) {
            this.offsetToObjectMap.put(new Integer(n), o);
        }
        
        public Object getObject(final int n) throws IOException {
            final Integer n2 = new Integer(n);
            if (!this.offsetToObjectMap.containsKey(n2)) {
                throw new IOException("Invalid indirection to offset " + n);
            }
            return this.offsetToObjectMap.get(n2);
        }
        
        public void removeObject(final int n) {
            this.offsetToObjectMap.remove(new Integer(n));
        }
        
        public boolean containsObject(final int n) {
            return this.offsetToObjectMap.containsKey(new Integer(n));
        }
    }
}
