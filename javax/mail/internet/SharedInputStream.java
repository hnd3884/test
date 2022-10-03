package javax.mail.internet;

import java.io.InputStream;

public interface SharedInputStream
{
    long getPosition();
    
    InputStream newStream(final long p0, final long p1);
}
