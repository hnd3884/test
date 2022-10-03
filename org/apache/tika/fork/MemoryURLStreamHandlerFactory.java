package org.apache.tika.fork;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class MemoryURLStreamHandlerFactory implements URLStreamHandlerFactory
{
    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        if ("tika-in-memory".equals(protocol)) {
            return new MemoryURLStreamHandler();
        }
        return null;
    }
}
