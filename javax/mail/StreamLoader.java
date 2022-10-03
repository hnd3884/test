package javax.mail;

import java.io.IOException;
import java.io.InputStream;

interface StreamLoader
{
    void load(final InputStream p0) throws IOException;
}
