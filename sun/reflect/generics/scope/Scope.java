package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public interface Scope
{
    TypeVariable<?> lookup(final String p0);
}
