package javax.rmi.CORBA;

import org.omg.SendingContext.RunTime;
import org.omg.CORBA.portable.InputStream;
import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public interface ValueHandler
{
    void writeValue(final OutputStream p0, final Serializable p1);
    
    Serializable readValue(final InputStream p0, final int p1, final Class p2, final String p3, final RunTime p4);
    
    String getRMIRepositoryID(final Class p0);
    
    boolean isCustomMarshaled(final Class p0);
    
    RunTime getRunTimeCodeBase();
    
    Serializable writeReplace(final Serializable p0);
}
