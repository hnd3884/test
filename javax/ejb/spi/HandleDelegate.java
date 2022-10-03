package javax.ejb.spi;

import javax.ejb.EJBHome;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.ejb.EJBObject;

public interface HandleDelegate
{
    void writeEJBObject(final EJBObject p0, final ObjectOutputStream p1) throws IOException;
    
    EJBObject readEJBObject(final ObjectInputStream p0) throws IOException, ClassNotFoundException;
    
    void writeEJBHome(final EJBHome p0, final ObjectOutputStream p1) throws IOException;
    
    EJBHome readEJBHome(final ObjectInputStream p0) throws IOException, ClassNotFoundException;
}
