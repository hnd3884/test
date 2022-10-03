package io.netty.channel.socket.nio;

import io.netty.util.internal.SuppressJava6Requirement;
import java.net.StandardProtocolFamily;
import java.net.ProtocolFamily;
import io.netty.channel.socket.InternetProtocolFamily;

final class ProtocolFamilyConverter
{
    private ProtocolFamilyConverter() {
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    public static ProtocolFamily convert(final InternetProtocolFamily family) {
        switch (family) {
            case IPv4: {
                return StandardProtocolFamily.INET;
            }
            case IPv6: {
                return StandardProtocolFamily.INET6;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
}
