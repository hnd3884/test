package javax.imageio.spi;

public interface RegisterableService
{
    void onRegistration(final ServiceRegistry p0, final Class<?> p1);
    
    void onDeregistration(final ServiceRegistry p0, final Class<?> p1);
}
