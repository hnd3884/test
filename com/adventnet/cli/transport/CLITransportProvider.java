package com.adventnet.cli.transport;

import java.io.IOException;
import com.adventnet.cli.CLIMessage;

public interface CLITransportProvider
{
    public static final String STREAM_CLOSED = "Stream closed By Remote Peer";
    
    void open(final CLIProtocolOptions p0) throws Exception;
    
    void close() throws Exception;
    
    void write(final CLIMessage p0) throws IOException;
    
    CLIMessage read() throws IOException;
}
