package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public interface ValueHandlerMultiFormat extends ValueHandler
{
    byte getMaximumStreamFormatVersion();
    
    void writeValue(final OutputStream p0, final Serializable p1, final byte p2);
}
