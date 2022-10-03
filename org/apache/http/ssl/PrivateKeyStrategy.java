package org.apache.http.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy
{
    String chooseAlias(final Map<String, PrivateKeyDetails> p0, final Socket p1);
}
