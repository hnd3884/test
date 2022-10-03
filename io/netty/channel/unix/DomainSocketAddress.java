package io.netty.channel.unix;

import java.io.File;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

public class DomainSocketAddress extends SocketAddress
{
    private static final long serialVersionUID = -6934618000832236893L;
    private final String socketPath;
    
    public DomainSocketAddress(final String socketPath) {
        this.socketPath = ObjectUtil.checkNotNull(socketPath, "socketPath");
    }
    
    public DomainSocketAddress(final File file) {
        this(file.getPath());
    }
    
    public String path() {
        return this.socketPath;
    }
    
    @Override
    public String toString() {
        return this.path();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof DomainSocketAddress && ((DomainSocketAddress)o).socketPath.equals(this.socketPath));
    }
    
    @Override
    public int hashCode() {
        return this.socketPath.hashCode();
    }
}
