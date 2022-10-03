package javax.naming.spi;

import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

public interface StateFactory
{
    Object getStateToBind(final Object p0, final Name p1, final Context p2, final Hashtable<?, ?> p3) throws NamingException;
}
