package sun.misc;

import java.net.InetAddress;
import java.net.URLClassLoader;

public interface JavaNetAccess
{
    URLClassPath getURLClassPath(final URLClassLoader p0);
    
    String getOriginalHostName(final InetAddress p0);
}
