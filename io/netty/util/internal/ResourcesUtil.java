package io.netty.util.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.File;

public final class ResourcesUtil
{
    public static File getFile(final Class resourceClass, final String fileName) {
        try {
            return new File(URLDecoder.decode(resourceClass.getResource(fileName).getFile(), "UTF-8"));
        }
        catch (final UnsupportedEncodingException e) {
            return new File(resourceClass.getResource(fileName).getFile());
        }
    }
    
    private ResourcesUtil() {
    }
}
