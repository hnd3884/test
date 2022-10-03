package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory
{
    InputStream getInputStream() throws IOException;
}
