package sun.misc;

import java.net.HttpCookie;
import java.util.List;

public interface JavaNetHttpCookieAccess
{
    List<HttpCookie> parse(final String p0);
    
    String header(final HttpCookie p0);
}
