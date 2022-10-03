package sun.security.jca;

public final class ServiceId
{
    public final String type;
    public final String algorithm;
    
    public ServiceId(final String type, final String algorithm) {
        this.type = type;
        this.algorithm = algorithm;
    }
}
