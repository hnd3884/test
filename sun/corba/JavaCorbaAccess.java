package sun.corba;

import com.sun.corba.se.impl.io.ValueHandlerImpl;

public interface JavaCorbaAccess
{
    ValueHandlerImpl newValueHandlerImpl();
    
    Class<?> loadClass(final String p0) throws ClassNotFoundException;
}
