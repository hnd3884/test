package javax.management.loading;

import java.net.URLStreamHandlerFactory;
import java.net.URL;

public class PrivateMLet extends MLet implements PrivateClassLoader
{
    private static final long serialVersionUID = 2503458973393711979L;
    
    public PrivateMLet(final URL[] array, final boolean b) {
        super(array, b);
    }
    
    public PrivateMLet(final URL[] array, final ClassLoader classLoader, final boolean b) {
        super(array, classLoader, b);
    }
    
    public PrivateMLet(final URL[] array, final ClassLoader classLoader, final URLStreamHandlerFactory urlStreamHandlerFactory, final boolean b) {
        super(array, classLoader, urlStreamHandlerFactory, b);
    }
}
