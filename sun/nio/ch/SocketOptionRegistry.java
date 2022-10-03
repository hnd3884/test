package sun.nio.ch;

import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;
import java.net.ProtocolFamily;
import java.net.SocketOption;

class SocketOptionRegistry
{
    private SocketOptionRegistry() {
    }
    
    public static OptionKey findOption(final SocketOption<?> socketOption, final ProtocolFamily protocolFamily) {
        return LazyInitialization.options.get(new RegistryKey(socketOption, protocolFamily));
    }
    
    private static class RegistryKey
    {
        private final SocketOption<?> name;
        private final ProtocolFamily family;
        
        RegistryKey(final SocketOption<?> name, final ProtocolFamily family) {
            this.name = name;
            this.family = family;
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode() + this.family.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof RegistryKey)) {
                return false;
            }
            final RegistryKey registryKey = (RegistryKey)o;
            return this.name == registryKey.name && this.family == registryKey.family;
        }
    }
    
    private static class LazyInitialization
    {
        static final Map<RegistryKey, OptionKey> options;
        
        private static Map<RegistryKey, OptionKey> options() {
            final HashMap hashMap = new HashMap();
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_BROADCAST, Net.UNSPEC), new OptionKey(65535, 32));
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_KEEPALIVE, Net.UNSPEC), new OptionKey(65535, 8));
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_LINGER, Net.UNSPEC), new OptionKey(65535, 128));
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_SNDBUF, Net.UNSPEC), new OptionKey(65535, 4097));
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_RCVBUF, Net.UNSPEC), new OptionKey(65535, 4098));
            hashMap.put(new RegistryKey(StandardSocketOptions.SO_REUSEADDR, Net.UNSPEC), new OptionKey(65535, 4));
            hashMap.put(new RegistryKey(StandardSocketOptions.TCP_NODELAY, Net.UNSPEC), new OptionKey(6, 1));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET), new OptionKey(0, 3));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET), new OptionKey(0, 9));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET), new OptionKey(0, 10));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET), new OptionKey(0, 11));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET6), new OptionKey(41, 39));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET6), new OptionKey(41, 9));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET6), new OptionKey(41, 10));
            hashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET6), new OptionKey(41, 11));
            hashMap.put(new RegistryKey(ExtendedSocketOption.SO_OOBINLINE, Net.UNSPEC), new OptionKey(65535, 256));
            return hashMap;
        }
        
        static {
            options = options();
        }
    }
}
