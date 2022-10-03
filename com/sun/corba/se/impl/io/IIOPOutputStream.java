package com.sun.corba.se.impl.io;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.impl.util.RepositoryId;
import java.rmi.Remote;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.io.ObjectOutput;
import java.io.Externalizable;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.NotActiveException;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.portable.ValueOutputStream;
import java.util.Stack;
import java.io.IOException;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.Bridge;
import com.sun.corba.se.impl.logging.UtilSystemException;

public class IIOPOutputStream extends OutputStreamHook
{
    private UtilSystemException wrapper;
    private static Bridge bridge;
    private org.omg.CORBA_2_3.portable.OutputStream orbStream;
    private Object currentObject;
    private ObjectStreamClass currentClassDesc;
    private int recursionDepth;
    private int simpleWriteDepth;
    private IOException abortIOException;
    private Stack classDescStack;
    private Object[] writeObjectArgList;
    
    public IIOPOutputStream() throws IOException {
        this.wrapper = UtilSystemException.get("rpc.encoding");
        this.currentObject = null;
        this.currentClassDesc = null;
        this.recursionDepth = 0;
        this.simpleWriteDepth = 0;
        this.abortIOException = null;
        this.classDescStack = new Stack();
        this.writeObjectArgList = new Object[] { this };
    }
    
    @Override
    protected void beginOptionalCustomData() {
        if (this.streamFormatVersion == 2) {
            ((ValueOutputStream)this.orbStream).start_value(this.currentClassDesc.getRMIIIOPOptionalDataRepId());
        }
    }
    
    final void setOrbStream(final org.omg.CORBA_2_3.portable.OutputStream orbStream) {
        this.orbStream = orbStream;
    }
    
    @Override
    final org.omg.CORBA_2_3.portable.OutputStream getOrbStream() {
        return this.orbStream;
    }
    
    final void increaseRecursionDepth() {
        ++this.recursionDepth;
    }
    
    final int decreaseRecursionDepth() {
        return --this.recursionDepth;
    }
    
    public final void writeObjectOverride(final Object o) throws IOException {
        this.writeObjectState.writeData(this);
        Util.writeAbstractObject(this.orbStream, o);
    }
    
    public final void simpleWriteObject(final Object o, final byte streamFormatVersion) {
        final byte streamFormatVersion2 = this.streamFormatVersion;
        this.streamFormatVersion = streamFormatVersion;
        final Object currentObject = this.currentObject;
        final ObjectStreamClass currentClassDesc = this.currentClassDesc;
        ++this.simpleWriteDepth;
        try {
            this.outputObject(o);
        }
        catch (final IOException abortIOException) {
            if (this.abortIOException == null) {
                this.abortIOException = abortIOException;
            }
        }
        finally {
            this.streamFormatVersion = streamFormatVersion2;
            --this.simpleWriteDepth;
            this.currentObject = currentObject;
            this.currentClassDesc = currentClassDesc;
        }
        final IOException abortIOException2 = this.abortIOException;
        if (this.simpleWriteDepth == 0) {
            this.abortIOException = null;
        }
        if (abortIOException2 != null) {
            IIOPOutputStream.bridge.throwException(abortIOException2);
        }
    }
    
    @Override
    ObjectStreamField[] getFieldsNoCopy() {
        return this.currentClassDesc.getFieldsNoCopy();
    }
    
    @Override
    public final void defaultWriteObjectDelegate() {
        try {
            if (this.currentObject == null || this.currentClassDesc == null) {
                throw new NotActiveException("defaultWriteObjectDelegate");
            }
            final ObjectStreamField[] fieldsNoCopy = this.currentClassDesc.getFieldsNoCopy();
            if (fieldsNoCopy.length > 0) {
                this.outputClassFields(this.currentObject, this.currentClassDesc.forClass(), fieldsNoCopy);
            }
        }
        catch (final IOException ex) {
            IIOPOutputStream.bridge.throwException(ex);
        }
    }
    
    public final boolean enableReplaceObjectDelegate(final boolean b) {
        return false;
    }
    
    @Override
    protected final void annotateClass(final Class<?> clazz) throws IOException {
        throw new IOException("Method annotateClass not supported");
    }
    
    @Override
    public final void close() throws IOException {
    }
    
    @Override
    protected final void drain() throws IOException {
    }
    
    @Override
    public final void flush() throws IOException {
        try {
            this.orbStream.flush();
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    protected final Object replaceObject(final Object o) throws IOException {
        throw new IOException("Method replaceObject not supported");
    }
    
    @Override
    public final void reset() throws IOException {
        try {
            if (this.currentObject != null || this.currentClassDesc != null) {
                throw new IOException("Illegal call to reset");
            }
            this.abortIOException = null;
            if (this.classDescStack == null) {
                this.classDescStack = new Stack();
            }
            else {
                this.classDescStack.setSize(0);
            }
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void write(final byte[] array) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_octet_array(array, 0, array.length);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void write(final byte[] array, final int n, final int n2) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_octet_array(array, n, n2);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void write(final int n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_octet((byte)(n & 0xFF));
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeBoolean(final boolean b) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_boolean(b);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeByte(final int n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_octet((byte)n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeBytes(final String s) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            final byte[] bytes = s.getBytes();
            this.orbStream.write_octet_array(bytes, 0, bytes.length);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeChar(final int n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_wchar((char)n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeChars(final String s) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            final char[] charArray = s.toCharArray();
            this.orbStream.write_wchar_array(charArray, 0, charArray.length);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeDouble(final double n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_double(n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeFloat(final float n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_float(n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeInt(final int n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_long(n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeLong(final long n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_longlong(n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    public final void writeShort(final int n) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.orbStream.write_short((short)n);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    @Override
    protected final void writeStreamHeader() throws IOException {
    }
    
    protected void internalWriteUTF(final org.omg.CORBA.portable.OutputStream outputStream, final String s) {
        outputStream.write_wstring(s);
    }
    
    @Override
    public final void writeUTF(final String s) throws IOException {
        try {
            this.writeObjectState.writeData(this);
            this.internalWriteUTF(this.orbStream, s);
        }
        catch (final Error error) {
            final IOException ex = new IOException(error.getMessage());
            ex.initCause(error);
            throw ex;
        }
    }
    
    private boolean checkSpecialClasses(final Object o) throws IOException {
        if (o instanceof ObjectStreamClass) {
            throw new IOException("Serialization of ObjectStreamClass not supported");
        }
        return false;
    }
    
    private boolean checkSubstitutableSpecialClasses(final Object o) throws IOException {
        if (o instanceof String) {
            this.orbStream.write_value((Serializable)o);
            return true;
        }
        return false;
    }
    
    private void outputObject(final Object currentObject) throws IOException {
        this.currentObject = currentObject;
        final Class<?> class1 = currentObject.getClass();
        this.currentClassDesc = ObjectStreamClass.lookup(class1);
        if (this.currentClassDesc == null) {
            throw new NotSerializableException(class1.getName());
        }
        if (this.currentClassDesc.isExternalizable()) {
            this.orbStream.write_octet(this.streamFormatVersion);
            ((Externalizable)currentObject).writeExternal(this);
        }
        else {
            if (this.currentClassDesc.forClass().getName().equals("java.lang.String")) {
                this.writeUTF((String)currentObject);
                return;
            }
            final int size = this.classDescStack.size();
            try {
                ObjectStreamClass superclass;
                while ((superclass = this.currentClassDesc.getSuperclass()) != null) {
                    this.classDescStack.push(this.currentClassDesc);
                    this.currentClassDesc = superclass;
                }
                do {
                    final WriteObjectState writeObjectState = this.writeObjectState;
                    try {
                        this.setState(IIOPOutputStream.NOT_IN_WRITE_OBJECT);
                        if (this.currentClassDesc.hasWriteObject()) {
                            this.invokeObjectWriter(this.currentClassDesc, currentObject);
                        }
                        else {
                            this.defaultWriteObjectDelegate();
                        }
                    }
                    finally {
                        this.setState(writeObjectState);
                    }
                } while (this.classDescStack.size() > size && (this.currentClassDesc = this.classDescStack.pop()) != null);
            }
            finally {
                this.classDescStack.setSize(size);
            }
        }
    }
    
    private void invokeObjectWriter(final ObjectStreamClass objectStreamClass, final Object o) throws IOException {
        objectStreamClass.forClass();
        try {
            this.orbStream.write_octet(this.streamFormatVersion);
            this.writeObjectState.enterWriteObject(this);
            objectStreamClass.writeObjectMethod.invoke(o, this.writeObjectArgList);
            this.writeObjectState.exitWriteObject(this);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (targetException instanceof IOException) {
                throw (IOException)targetException;
            }
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            if (targetException instanceof Error) {
                throw (Error)targetException;
            }
            throw new Error("invokeObjectWriter internal error", ex);
        }
        catch (final IllegalAccessException ex2) {}
    }
    
    @Override
    void writeField(final ObjectStreamField objectStreamField, final Object o) throws IOException {
        switch (objectStreamField.getTypeCode()) {
            case 'B': {
                if (o == null) {
                    this.orbStream.write_octet((byte)0);
                    break;
                }
                this.orbStream.write_octet((byte)o);
                break;
            }
            case 'C': {
                if (o == null) {
                    this.orbStream.write_wchar('\0');
                    break;
                }
                this.orbStream.write_wchar((char)o);
                break;
            }
            case 'F': {
                if (o == null) {
                    this.orbStream.write_float(0.0f);
                    break;
                }
                this.orbStream.write_float((float)o);
                break;
            }
            case 'D': {
                if (o == null) {
                    this.orbStream.write_double(0.0);
                    break;
                }
                this.orbStream.write_double((double)o);
                break;
            }
            case 'I': {
                if (o == null) {
                    this.orbStream.write_long(0);
                    break;
                }
                this.orbStream.write_long((int)o);
                break;
            }
            case 'J': {
                if (o == null) {
                    this.orbStream.write_longlong(0L);
                    break;
                }
                this.orbStream.write_longlong((long)o);
                break;
            }
            case 'S': {
                if (o == null) {
                    this.orbStream.write_short((short)0);
                    break;
                }
                this.orbStream.write_short((short)o);
                break;
            }
            case 'Z': {
                if (o == null) {
                    this.orbStream.write_boolean(false);
                    break;
                }
                this.orbStream.write_boolean((boolean)o);
                break;
            }
            case 'L':
            case '[': {
                this.writeObjectField(objectStreamField, o);
                break;
            }
            default: {
                throw new InvalidClassException(this.currentClassDesc.getName());
            }
        }
    }
    
    private void writeObjectField(final ObjectStreamField objectStreamField, final Object o) throws IOException {
        if (ObjectStreamClassCorbaExt.isAny(objectStreamField.getTypeString())) {
            Util.writeAny(this.orbStream, o);
        }
        else {
            final Class type = objectStreamField.getType();
            int n = 2;
            if (type.isInterface()) {
                type.getName();
                if (Remote.class.isAssignableFrom(type)) {
                    n = 0;
                }
                else if (org.omg.CORBA.Object.class.isAssignableFrom(type)) {
                    n = 0;
                }
                else if (RepositoryId.isAbstractBase(type)) {
                    n = 1;
                }
                else if (ObjectStreamClassCorbaExt.isAbstractInterface(type)) {
                    n = 1;
                }
            }
            switch (n) {
                case 0: {
                    Util.writeRemoteObject(this.orbStream, o);
                    break;
                }
                case 1: {
                    Util.writeAbstractObject(this.orbStream, o);
                    break;
                }
                case 2: {
                    try {
                        this.orbStream.write_value((Serializable)o, type);
                    }
                    catch (final ClassCastException ex) {
                        if (o instanceof Serializable) {
                            throw ex;
                        }
                        Utility.throwNotSerializableForCorba(o.getClass().getName());
                    }
                    break;
                }
            }
        }
    }
    
    private void outputClassFields(final Object o, final Class clazz, final ObjectStreamField[] array) throws IOException, InvalidClassException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].getField() == null) {
                throw new InvalidClassException(clazz.getName(), "Nonexistent field " + array[i].getName());
            }
            try {
                switch (array[i].getTypeCode()) {
                    case 'B': {
                        this.orbStream.write_octet(array[i].getField().getByte(o));
                        break;
                    }
                    case 'C': {
                        this.orbStream.write_wchar(array[i].getField().getChar(o));
                        break;
                    }
                    case 'F': {
                        this.orbStream.write_float(array[i].getField().getFloat(o));
                        break;
                    }
                    case 'D': {
                        this.orbStream.write_double(array[i].getField().getDouble(o));
                        break;
                    }
                    case 'I': {
                        this.orbStream.write_long(array[i].getField().getInt(o));
                        break;
                    }
                    case 'J': {
                        this.orbStream.write_longlong(array[i].getField().getLong(o));
                        break;
                    }
                    case 'S': {
                        this.orbStream.write_short(array[i].getField().getShort(o));
                        break;
                    }
                    case 'Z': {
                        this.orbStream.write_boolean(array[i].getField().getBoolean(o));
                        break;
                    }
                    case 'L':
                    case '[': {
                        this.writeObjectField(array[i], array[i].getField().get(o));
                        break;
                    }
                    default: {
                        throw new InvalidClassException(clazz.getName());
                    }
                }
            }
            catch (final IllegalAccessException ex) {
                throw this.wrapper.illegalFieldAccess(ex, array[i].getName());
            }
        }
    }
    
    static {
        IIOPOutputStream.bridge = AccessController.doPrivileged((PrivilegedAction<Bridge>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Bridge.get();
            }
        });
    }
}
