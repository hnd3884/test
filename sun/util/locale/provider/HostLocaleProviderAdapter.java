package sun.util.locale.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.spi.LocaleServiceProvider;

public class HostLocaleProviderAdapter extends AuxLocaleProviderAdapter
{
    @Override
    public Type getAdapterType() {
        return Type.HOST;
    }
    
    @Override
    protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> clazz) {
        try {
            return (P)HostLocaleProviderAdapterImpl.class.getMethod("get" + clazz.getSimpleName(), (Class<?>[])null).invoke(null, (Object[])null);
        }
        catch (final NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LocaleServiceProviderPool.config(HostLocaleProviderAdapter.class, ((Throwable)ex).toString());
            return null;
        }
    }
}
