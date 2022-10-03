package sun.security.timestamp;

import java.io.IOException;

public interface Timestamper
{
    TSResponse generateTimestamp(final TSRequest p0) throws IOException;
}
