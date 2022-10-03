package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;

abstract class HelloExtension
{
    final ExtensionType type;
    
    HelloExtension(final ExtensionType type) {
        this.type = type;
    }
    
    abstract int length();
    
    abstract void send(final HandshakeOutStream p0) throws IOException;
    
    @Override
    public abstract String toString();
}
