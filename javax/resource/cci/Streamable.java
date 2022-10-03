package javax.resource.cci;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Streamable
{
    void read(final InputStream p0) throws IOException;
    
    void write(final OutputStream p0) throws IOException;
}
