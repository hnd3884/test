package org.glassfish.jersey.client;

import java.io.IOException;
import java.io.InputStream;

public interface ChunkParser
{
    byte[] readChunk(final InputStream p0) throws IOException;
}
