package org.apache.commons.httpclient.protocol;

import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;

public interface SecureProtocolSocketFactory extends ProtocolSocketFactory
{
    Socket createSocket(final Socket p0, final String p1, final int p2, final boolean p3) throws IOException, UnknownHostException;
}
