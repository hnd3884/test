package sun.nio.fs;

import java.util.Map;
import java.io.IOException;

interface DynamicFileAttributeView
{
    void setAttribute(final String p0, final Object p1) throws IOException;
    
    Map<String, Object> readAttributes(final String[] p0) throws IOException;
}
