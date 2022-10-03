package io.netty.resolver;

import java.util.Iterator;
import java.util.HashMap;
import java.net.InetAddress;
import java.util.List;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.util.Map;
import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class HostsFileParser
{
    public static HostsFileEntries parseSilently() {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently());
    }
    
    public static HostsFileEntries parseSilently(final Charset... charsets) {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently(charsets));
    }
    
    public static HostsFileEntries parse() throws IOException {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parse());
    }
    
    public static HostsFileEntries parse(final File file) throws IOException {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parse(file, new Charset[0]));
    }
    
    public static HostsFileEntries parse(final File file, final Charset... charsets) throws IOException {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parse(file, charsets));
    }
    
    public static HostsFileEntries parse(final Reader reader) throws IOException {
        return hostsFileEntries(HostsFileEntriesProvider.parser().parse(reader));
    }
    
    private HostsFileParser() {
    }
    
    private static HostsFileEntries hostsFileEntries(final HostsFileEntriesProvider provider) {
        return (provider == HostsFileEntriesProvider.EMPTY) ? HostsFileEntries.EMPTY : new HostsFileEntries((Map<String, Inet4Address>)toMapWithSingleValue(provider.ipv4Entries()), (Map<String, Inet6Address>)toMapWithSingleValue(provider.ipv6Entries()));
    }
    
    private static Map<String, ?> toMapWithSingleValue(final Map<String, List<InetAddress>> fromMapWithListValue) {
        final Map<String, InetAddress> result = new HashMap<String, InetAddress>(fromMapWithListValue.size());
        for (final Map.Entry<String, List<InetAddress>> entry : fromMapWithListValue.entrySet()) {
            final List<InetAddress> value = entry.getValue();
            if (!value.isEmpty()) {
                result.put(entry.getKey(), value.get(0));
            }
        }
        return result;
    }
}
