package java.nio.channels;

import java.util.Set;
import java.net.SocketOption;
import java.io.IOException;
import java.net.SocketAddress;

public interface NetworkChannel extends Channel
{
    NetworkChannel bind(final SocketAddress p0) throws IOException;
    
    SocketAddress getLocalAddress() throws IOException;
    
     <T> NetworkChannel setOption(final SocketOption<T> p0, final T p1) throws IOException;
    
     <T> T getOption(final SocketOption<T> p0) throws IOException;
    
    Set<SocketOption<?>> supportedOptions();
}
