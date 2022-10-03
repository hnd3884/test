package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.spi.orb.ORB;
import javax.rmi.CORBA.Util;
import org.omg.CORBA_2_3.portable.OutputStream;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA_2_3.portable.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.IDLEntity;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Method;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;

public class DynamicMethodMarshallerImpl implements DynamicMethodMarshaller
{
    Method method;
    ExceptionHandler ehandler;
    boolean hasArguments;
    boolean hasVoidResult;
    boolean needsArgumentCopy;
    boolean needsResultCopy;
    ReaderWriter[] argRWs;
    ReaderWriter resultRW;
    private static ReaderWriter booleanRW;
    private static ReaderWriter byteRW;
    private static ReaderWriter charRW;
    private static ReaderWriter shortRW;
    private static ReaderWriter intRW;
    private static ReaderWriter longRW;
    private static ReaderWriter floatRW;
    private static ReaderWriter doubleRW;
    private static ReaderWriter corbaObjectRW;
    private static ReaderWriter anyRW;
    private static ReaderWriter abstractInterfaceRW;
    
    private static boolean isAnyClass(final Class clazz) {
        return clazz.equals(Object.class) || clazz.equals(Serializable.class) || clazz.equals(Externalizable.class);
    }
    
    private static boolean isAbstractInterface(final Class clazz) {
        if (IDLEntity.class.isAssignableFrom(clazz)) {
            return clazz.isInterface();
        }
        return clazz.isInterface() && allMethodsThrowRemoteException(clazz);
    }
    
    private static boolean allMethodsThrowRemoteException(final Class clazz) {
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (method.getDeclaringClass() != Object.class && !throwsRemote(method)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean throwsRemote(final Method method) {
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int i = 0; i < exceptionTypes.length; ++i) {
            if (RemoteException.class.isAssignableFrom(exceptionTypes[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static ReaderWriter makeReaderWriter(final Class clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return DynamicMethodMarshallerImpl.booleanRW;
        }
        if (clazz.equals(Byte.TYPE)) {
            return DynamicMethodMarshallerImpl.byteRW;
        }
        if (clazz.equals(Character.TYPE)) {
            return DynamicMethodMarshallerImpl.charRW;
        }
        if (clazz.equals(Short.TYPE)) {
            return DynamicMethodMarshallerImpl.shortRW;
        }
        if (clazz.equals(Integer.TYPE)) {
            return DynamicMethodMarshallerImpl.intRW;
        }
        if (clazz.equals(Long.TYPE)) {
            return DynamicMethodMarshallerImpl.longRW;
        }
        if (clazz.equals(Float.TYPE)) {
            return DynamicMethodMarshallerImpl.floatRW;
        }
        if (clazz.equals(Double.TYPE)) {
            return DynamicMethodMarshallerImpl.doubleRW;
        }
        if (Remote.class.isAssignableFrom(clazz)) {
            return new ReaderWriterBase("remote(" + clazz.getName() + ")") {
                @Override
                public Object read(final InputStream inputStream) {
                    return PortableRemoteObject.narrow(inputStream.read_Object(), clazz);
                }
                
                @Override
                public void write(final OutputStream outputStream, final Object o) {
                    Util.writeRemoteObject(outputStream, o);
                }
            };
        }
        if (clazz.equals(org.omg.CORBA.Object.class)) {
            return DynamicMethodMarshallerImpl.corbaObjectRW;
        }
        if (org.omg.CORBA.Object.class.isAssignableFrom(clazz)) {
            return new ReaderWriterBase("org.omg.CORBA.Object(" + clazz.getName() + ")") {
                @Override
                public Object read(final InputStream inputStream) {
                    return inputStream.read_Object(clazz);
                }
                
                @Override
                public void write(final OutputStream outputStream, final Object o) {
                    outputStream.write_Object((org.omg.CORBA.Object)o);
                }
            };
        }
        if (isAnyClass(clazz)) {
            return DynamicMethodMarshallerImpl.anyRW;
        }
        if (isAbstractInterface(clazz)) {
            return DynamicMethodMarshallerImpl.abstractInterfaceRW;
        }
        return new ReaderWriterBase("value(" + clazz.getName() + ")") {
            @Override
            public Object read(final InputStream inputStream) {
                return inputStream.read_value(clazz);
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_value((Serializable)o, clazz);
            }
        };
    }
    
    public DynamicMethodMarshallerImpl(final Method method) {
        this.hasArguments = true;
        this.hasVoidResult = true;
        this.argRWs = null;
        this.resultRW = null;
        this.method = method;
        this.ehandler = new ExceptionHandlerImpl(method.getExceptionTypes());
        this.needsArgumentCopy = false;
        final Class<?>[] parameterTypes = method.getParameterTypes();
        this.hasArguments = (parameterTypes.length > 0);
        if (this.hasArguments) {
            this.argRWs = new ReaderWriter[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                if (!parameterTypes[i].isPrimitive()) {
                    this.needsArgumentCopy = true;
                }
                this.argRWs[i] = makeReaderWriter(parameterTypes[i]);
            }
        }
        final Class<?> returnType = method.getReturnType();
        this.needsResultCopy = false;
        if (!(this.hasVoidResult = returnType.equals(Void.TYPE))) {
            this.needsResultCopy = !returnType.isPrimitive();
            this.resultRW = makeReaderWriter(returnType);
        }
    }
    
    @Override
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public Object[] copyArguments(final Object[] array, final ORB orb) throws RemoteException {
        if (this.needsArgumentCopy) {
            return Util.copyObjects(array, orb);
        }
        return array;
    }
    
    @Override
    public Object[] readArguments(final InputStream inputStream) {
        Object[] array = null;
        if (this.hasArguments) {
            array = new Object[this.argRWs.length];
            for (int i = 0; i < this.argRWs.length; ++i) {
                array[i] = this.argRWs[i].read(inputStream);
            }
        }
        return array;
    }
    
    @Override
    public void writeArguments(final OutputStream outputStream, final Object[] array) {
        if (this.hasArguments) {
            if (array.length != this.argRWs.length) {
                throw new IllegalArgumentException("Expected " + this.argRWs.length + " arguments, but got " + array.length + " arguments.");
            }
            for (int i = 0; i < this.argRWs.length; ++i) {
                this.argRWs[i].write(outputStream, array[i]);
            }
        }
    }
    
    @Override
    public Object copyResult(final Object o, final ORB orb) throws RemoteException {
        if (this.needsResultCopy) {
            return Util.copyObject(o, orb);
        }
        return o;
    }
    
    @Override
    public Object readResult(final InputStream inputStream) {
        if (this.hasVoidResult) {
            return null;
        }
        return this.resultRW.read(inputStream);
    }
    
    @Override
    public void writeResult(final OutputStream outputStream, final Object o) {
        if (!this.hasVoidResult) {
            this.resultRW.write(outputStream, o);
        }
    }
    
    @Override
    public boolean isDeclaredException(final Throwable t) {
        return this.ehandler.isDeclaredException(t.getClass());
    }
    
    @Override
    public void writeException(final OutputStream outputStream, final Exception ex) {
        this.ehandler.writeException(outputStream, ex);
    }
    
    @Override
    public Exception readException(final ApplicationException ex) {
        return this.ehandler.readException(ex);
    }
    
    static {
        DynamicMethodMarshallerImpl.booleanRW = new ReaderWriterBase("boolean") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Boolean(inputStream.read_boolean());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_boolean((boolean)o);
            }
        };
        DynamicMethodMarshallerImpl.byteRW = new ReaderWriterBase("byte") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Byte(inputStream.read_octet());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_octet((byte)o);
            }
        };
        DynamicMethodMarshallerImpl.charRW = new ReaderWriterBase("char") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Character(inputStream.read_wchar());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_wchar((char)o);
            }
        };
        DynamicMethodMarshallerImpl.shortRW = new ReaderWriterBase("short") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Short(inputStream.read_short());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_short((short)o);
            }
        };
        DynamicMethodMarshallerImpl.intRW = new ReaderWriterBase("int") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Integer(inputStream.read_long());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_long((int)o);
            }
        };
        DynamicMethodMarshallerImpl.longRW = new ReaderWriterBase("long") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Long(inputStream.read_longlong());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_longlong((long)o);
            }
        };
        DynamicMethodMarshallerImpl.floatRW = new ReaderWriterBase("float") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Float(inputStream.read_float());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_float((float)o);
            }
        };
        DynamicMethodMarshallerImpl.doubleRW = new ReaderWriterBase("double") {
            @Override
            public Object read(final InputStream inputStream) {
                return new Double(inputStream.read_double());
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_double((double)o);
            }
        };
        DynamicMethodMarshallerImpl.corbaObjectRW = new ReaderWriterBase("org.omg.CORBA.Object") {
            @Override
            public Object read(final InputStream inputStream) {
                return inputStream.read_Object();
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                outputStream.write_Object((org.omg.CORBA.Object)o);
            }
        };
        DynamicMethodMarshallerImpl.anyRW = new ReaderWriterBase("any") {
            @Override
            public Object read(final InputStream inputStream) {
                return Util.readAny(inputStream);
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                Util.writeAny(outputStream, o);
            }
        };
        DynamicMethodMarshallerImpl.abstractInterfaceRW = new ReaderWriterBase("abstract_interface") {
            @Override
            public Object read(final InputStream inputStream) {
                return inputStream.read_abstract_interface();
            }
            
            @Override
            public void write(final OutputStream outputStream, final Object o) {
                Util.writeAbstractObject(outputStream, o);
            }
        };
    }
    
    abstract static class ReaderWriterBase implements ReaderWriter
    {
        String name;
        
        public ReaderWriterBase(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return "ReaderWriter[" + this.name + "]";
        }
    }
    
    public interface ReaderWriter
    {
        Object read(final InputStream p0);
        
        void write(final OutputStream p0, final Object p1);
    }
}
