package javax.naming.spi;

import javax.naming.NamingException;
import java.util.Hashtable;

public interface ObjectFactoryBuilder
{
    ObjectFactory createObjectFactory(final Object p0, final Hashtable<?, ?> p1) throws NamingException;
}
