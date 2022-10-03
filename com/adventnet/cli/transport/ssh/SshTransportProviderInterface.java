package com.adventnet.cli.transport.ssh;

import java.io.IOException;
import com.adventnet.cli.transport.LoginException;
import com.adventnet.cli.transport.ConnectException;
import com.adventnet.cli.transport.CLITransportProvider;

public interface SshTransportProviderInterface extends CLITransportProvider
{
    void connect(final String p0, final int p1) throws ConnectException;
    
    String login(final String p0, final String p1, final String p2) throws LoginException;
    
    void setSocketTimeout(final int p0) throws IOException;
    
    void write(final byte[] p0) throws IOException;
    
    int read(final byte[] p0) throws IOException;
    
    void setTerminalType(final String p0);
}
