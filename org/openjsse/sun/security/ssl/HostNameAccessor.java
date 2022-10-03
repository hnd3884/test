package org.openjsse.sun.security.ssl;

import sun.misc.JavaNetAccess;
import sun.misc.SharedSecrets;
import java.net.InetAddress;

public class HostNameAccessor
{
    public static String getOriginalHostName(final InetAddress inetAddress) {
        final JavaNetAccess jna = SharedSecrets.getJavaNetAccess();
        return jna.getOriginalHostName(inetAddress);
    }
}
