package sun.security.jgss;

public class GSSCaller
{
    public static final GSSCaller CALLER_UNKNOWN;
    public static final GSSCaller CALLER_INITIATE;
    public static final GSSCaller CALLER_ACCEPT;
    public static final GSSCaller CALLER_SSL_CLIENT;
    public static final GSSCaller CALLER_SSL_SERVER;
    private String name;
    
    GSSCaller(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "GSSCaller{" + this.name + '}';
    }
    
    static {
        CALLER_UNKNOWN = new GSSCaller("UNKNOWN");
        CALLER_INITIATE = new GSSCaller("INITIATE");
        CALLER_ACCEPT = new GSSCaller("ACCEPT");
        CALLER_SSL_CLIENT = new GSSCaller("SSL_CLIENT");
        CALLER_SSL_SERVER = new GSSCaller("SSL_SERVER");
    }
}
