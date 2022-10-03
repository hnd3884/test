package javax.naming.spi;

import javax.naming.directory.Attributes;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

public interface DirObjectFactory extends ObjectFactory
{
    Object getObjectInstance(final Object p0, final Name p1, final Context p2, final Hashtable<?, ?> p3, final Attributes p4) throws Exception;
}
