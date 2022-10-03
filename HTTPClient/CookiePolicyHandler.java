package HTTPClient;

public interface CookiePolicyHandler
{
    boolean acceptCookie(final Cookie p0, final RoRequest p1, final RoResponse p2);
    
    boolean sendCookie(final Cookie p0, final RoRequest p1);
}
