package com.sun.corba.se.impl.presentation.rmi;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.UnexpectedException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA.UserException;
import java.rmi.RemoteException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class ExceptionHandlerImpl implements ExceptionHandler
{
    private ExceptionRW[] rws;
    private final ORBUtilSystemException wrapper;
    
    public ExceptionHandlerImpl(final Class[] array) {
        this.wrapper = ORBUtilSystemException.get("rpc.presentation");
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (!RemoteException.class.isAssignableFrom(array[i])) {
                ++n;
            }
        }
        this.rws = new ExceptionRW[n];
        int n2 = 0;
        for (int j = 0; j < array.length; ++j) {
            final Class clazz = array[j];
            if (!RemoteException.class.isAssignableFrom(clazz)) {
                ExceptionRWBase exceptionRWBase;
                if (UserException.class.isAssignableFrom(clazz)) {
                    exceptionRWBase = new ExceptionRWIDLImpl(clazz);
                }
                else {
                    exceptionRWBase = new ExceptionRWRMIImpl(clazz);
                }
                this.rws[n2++] = exceptionRWBase;
            }
        }
    }
    
    private int findDeclaredException(final Class clazz) {
        for (int i = 0; i < this.rws.length; ++i) {
            if (this.rws[i].getExceptionClass().isAssignableFrom(clazz)) {
                return i;
            }
        }
        return -1;
    }
    
    private int findDeclaredException(final String s) {
        for (int i = 0; i < this.rws.length; ++i) {
            if (this.rws[i] == null) {
                return -1;
            }
            if (s.equals(this.rws[i].getId())) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean isDeclaredException(final Class clazz) {
        return this.findDeclaredException(clazz) >= 0;
    }
    
    @Override
    public void writeException(final OutputStream outputStream, final Exception ex) {
        final int declaredException = this.findDeclaredException(ex.getClass());
        if (declaredException < 0) {
            throw this.wrapper.writeUndeclaredException(ex, ex.getClass().getName());
        }
        this.rws[declaredException].write(outputStream, ex);
    }
    
    @Override
    public Exception readException(final ApplicationException ex) {
        final InputStream inputStream = (InputStream)ex.getInputStream();
        final int declaredException = this.findDeclaredException(ex.getId());
        if (declaredException < 0) {
            final UnexpectedException ex2 = new UnexpectedException(inputStream.read_string());
            ex2.initCause(ex);
            return ex2;
        }
        return this.rws[declaredException].read(inputStream);
    }
    
    public ExceptionRW getRMIExceptionRW(final Class clazz) {
        return new ExceptionRWRMIImpl(clazz);
    }
    
    public abstract class ExceptionRWBase implements ExceptionRW
    {
        private Class cls;
        private String id;
        
        public ExceptionRWBase(final Class cls) {
            this.cls = cls;
        }
        
        @Override
        public Class getExceptionClass() {
            return this.cls;
        }
        
        @Override
        public String getId() {
            return this.id;
        }
        
        void setId(final String id) {
            this.id = id;
        }
    }
    
    public class ExceptionRWIDLImpl extends ExceptionRWBase
    {
        private Method readMethod;
        private Method writeMethod;
        
        public ExceptionRWIDLImpl(final Class clazz) {
            super(clazz);
            final String string = clazz.getName() + "Helper";
            final ClassLoader classLoader = clazz.getClassLoader();
            Class<?> forName;
            try {
                forName = Class.forName(string, true, classLoader);
                this.setId((String)forName.getDeclaredMethod("id", (Class[])null).invoke(null, (Object[])null));
            }
            catch (final Exception ex) {
                throw ExceptionHandlerImpl.this.wrapper.badHelperIdMethod(ex, string);
            }
            try {
                this.writeMethod = forName.getDeclaredMethod("write", org.omg.CORBA.portable.OutputStream.class, clazz);
            }
            catch (final Exception ex2) {
                throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod(ex2, string);
            }
            try {
                this.readMethod = forName.getDeclaredMethod("read", org.omg.CORBA.portable.InputStream.class);
            }
            catch (final Exception ex3) {
                throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod(ex3, string);
            }
        }
        
        @Override
        public void write(final OutputStream outputStream, final Exception ex) {
            try {
                this.writeMethod.invoke(null, outputStream, ex);
            }
            catch (final Exception ex2) {
                throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod(ex2, this.writeMethod.getDeclaringClass().getName());
            }
        }
        
        @Override
        public Exception read(final InputStream inputStream) {
            try {
                return (Exception)this.readMethod.invoke(null, inputStream);
            }
            catch (final Exception ex) {
                throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod(ex, this.readMethod.getDeclaringClass().getName());
            }
        }
    }
    
    public class ExceptionRWRMIImpl extends ExceptionRWBase
    {
        public ExceptionRWRMIImpl(final Class clazz) {
            super(clazz);
            this.setId(IDLNameTranslatorImpl.getExceptionId(clazz));
        }
        
        @Override
        public void write(final OutputStream outputStream, final Exception ex) {
            outputStream.write_string(this.getId());
            outputStream.write_value(ex, this.getExceptionClass());
        }
        
        @Override
        public Exception read(final InputStream inputStream) {
            inputStream.read_string();
            return (Exception)inputStream.read_value(this.getExceptionClass());
        }
    }
    
    public interface ExceptionRW
    {
        Class getExceptionClass();
        
        String getId();
        
        void write(final OutputStream p0, final Exception p1);
        
        Exception read(final InputStream p0);
    }
}
