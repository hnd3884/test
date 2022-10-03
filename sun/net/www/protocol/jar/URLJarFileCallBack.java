package sun.net.www.protocol.jar;

import java.io.IOException;
import java.util.jar.JarFile;
import java.net.URL;

public interface URLJarFileCallBack
{
    JarFile retrieve(final URL p0) throws IOException;
}
