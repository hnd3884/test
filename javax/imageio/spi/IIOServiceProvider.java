package javax.imageio.spi;

import java.util.Locale;

public abstract class IIOServiceProvider implements RegisterableService
{
    protected String vendorName;
    protected String version;
    
    public IIOServiceProvider(final String vendorName, final String version) {
        if (vendorName == null) {
            throw new IllegalArgumentException("vendorName == null!");
        }
        if (version == null) {
            throw new IllegalArgumentException("version == null!");
        }
        this.vendorName = vendorName;
        this.version = version;
    }
    
    public IIOServiceProvider() {
    }
    
    @Override
    public void onRegistration(final ServiceRegistry serviceRegistry, final Class<?> clazz) {
    }
    
    @Override
    public void onDeregistration(final ServiceRegistry serviceRegistry, final Class<?> clazz) {
    }
    
    public String getVendorName() {
        return this.vendorName;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public abstract String getDescription(final Locale p0);
}
