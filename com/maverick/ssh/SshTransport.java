package com.maverick.ssh;

import java.io.IOException;

public interface SshTransport extends SshIO
{
    String getHost();
    
    int getPort();
    
    SshTransport duplicate() throws IOException;
}
