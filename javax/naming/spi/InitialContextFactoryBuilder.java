package javax.naming.spi;

import javax.naming.NamingException;
import java.util.Hashtable;

public interface InitialContextFactoryBuilder
{
    InitialContextFactory createInitialContextFactory(final Hashtable<?, ?> p0) throws NamingException;
}
