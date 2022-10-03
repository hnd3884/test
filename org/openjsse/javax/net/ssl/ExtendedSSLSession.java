package org.openjsse.javax.net.ssl;

import java.util.List;

public abstract class ExtendedSSLSession extends javax.net.ssl.ExtendedSSLSession
{
    public List<byte[]> getStatusResponses() {
        throw new UnsupportedOperationException();
    }
}
