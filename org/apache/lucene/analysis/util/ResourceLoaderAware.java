package org.apache.lucene.analysis.util;

import java.io.IOException;

public interface ResourceLoaderAware
{
    void inform(final ResourceLoader p0) throws IOException;
}
