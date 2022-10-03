package com.maverick.ssh;

import java.io.IOException;

public interface SocketTimeoutSupport
{
    void setSoTimeout(final int p0) throws IOException;
    
    int getSoTimeout() throws IOException;
}
