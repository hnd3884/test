package sun.rmi.server;

import java.io.IOException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;

public interface Dispatcher
{
    void dispatch(final Remote p0, final RemoteCall p1) throws IOException;
}
