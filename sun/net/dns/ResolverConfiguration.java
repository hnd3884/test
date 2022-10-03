package sun.net.dns;

import java.util.List;

public abstract class ResolverConfiguration
{
    private static final Object lock;
    private static ResolverConfiguration provider;
    
    protected ResolverConfiguration() {
    }
    
    public static ResolverConfiguration open() {
        synchronized (ResolverConfiguration.lock) {
            if (ResolverConfiguration.provider == null) {
                ResolverConfiguration.provider = new ResolverConfigurationImpl();
            }
            return ResolverConfiguration.provider;
        }
    }
    
    public abstract List<String> searchlist();
    
    public abstract List<String> nameservers();
    
    public abstract Options options();
    
    static {
        lock = new Object();
    }
    
    public abstract static class Options
    {
        public int attempts() {
            return -1;
        }
        
        public int retrans() {
            return -1;
        }
    }
}
