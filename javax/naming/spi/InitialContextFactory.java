package javax.naming.spi;

import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;

public interface InitialContextFactory
{
    Context getInitialContext(final Hashtable<?, ?> p0) throws NamingException;
}
