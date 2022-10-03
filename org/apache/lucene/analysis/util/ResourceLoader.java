package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader
{
    InputStream openResource(final String p0) throws IOException;
    
     <T> Class<? extends T> findClass(final String p0, final Class<T> p1);
    
     <T> T newInstance(final String p0, final Class<T> p1);
}
