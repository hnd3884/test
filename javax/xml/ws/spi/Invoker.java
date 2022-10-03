package javax.xml.ws.spi;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker
{
    public abstract void inject(final WebServiceContext p0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
    
    public abstract Object invoke(final Method p0, final Object... p1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
