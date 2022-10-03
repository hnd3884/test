package io.netty.resolver;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.util.Locale;
import io.netty.util.NetUtil;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import io.netty.util.internal.logging.InternalLogger;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.HashMap;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public final class HostsFileEntriesProvider
{
    static final HostsFileEntriesProvider EMPTY;
    private final Map<String, List<InetAddress>> ipv4Entries;
    private final Map<String, List<InetAddress>> ipv6Entries;
    
    public static Parser parser() {
        return ParserImpl.INSTANCE;
    }
    
    HostsFileEntriesProvider(final Map<String, List<InetAddress>> ipv4Entries, final Map<String, List<InetAddress>> ipv6Entries) {
        this.ipv4Entries = Collections.unmodifiableMap((Map<? extends String, ? extends List<InetAddress>>)new HashMap<String, List<InetAddress>>(ipv4Entries));
        this.ipv6Entries = Collections.unmodifiableMap((Map<? extends String, ? extends List<InetAddress>>)new HashMap<String, List<InetAddress>>(ipv6Entries));
    }
    
    public Map<String, List<InetAddress>> ipv4Entries() {
        return this.ipv4Entries;
    }
    
    public Map<String, List<InetAddress>> ipv6Entries() {
        return this.ipv6Entries;
    }
    
    static {
        EMPTY = new HostsFileEntriesProvider(Collections.emptyMap(), Collections.emptyMap());
    }
    
    private static final class ParserImpl implements Parser
    {
        private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
        private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
        private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
        private static final Pattern WHITESPACES;
        private static final InternalLogger logger;
        static final ParserImpl INSTANCE;
        
        @Override
        public HostsFileEntriesProvider parse() throws IOException {
            return this.parse(locateHostsFile(), Charset.defaultCharset());
        }
        
        @Override
        public HostsFileEntriesProvider parse(final Charset... charsets) throws IOException {
            return this.parse(locateHostsFile(), charsets);
        }
        
        @Override
        public HostsFileEntriesProvider parse(final File file, Charset... charsets) throws IOException {
            ObjectUtil.checkNotNull(file, "file");
            ObjectUtil.checkNotNull(charsets, "charsets");
            if (charsets.length == 0) {
                charsets = new Charset[] { Charset.defaultCharset() };
            }
            if (file.exists() && file.isFile()) {
                for (final Charset charset : charsets) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
                    try {
                        final HostsFileEntriesProvider entries = this.parse(reader);
                        if (entries != HostsFileEntriesProvider.EMPTY) {
                            return entries;
                        }
                    }
                    finally {
                        reader.close();
                    }
                }
            }
            return HostsFileEntriesProvider.EMPTY;
        }
        
        @Override
        public HostsFileEntriesProvider parse(final Reader reader) throws IOException {
            ObjectUtil.checkNotNull(reader, "reader");
            final BufferedReader buff = new BufferedReader(reader);
            try {
                final Map<String, List<InetAddress>> ipv4Entries = new HashMap<String, List<InetAddress>>();
                final Map<String, List<InetAddress>> ipv6Entries = new HashMap<String, List<InetAddress>>();
                String line;
                while ((line = buff.readLine()) != null) {
                    final int commentPosition = line.indexOf(35);
                    if (commentPosition != -1) {
                        line = line.substring(0, commentPosition);
                    }
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    final List<String> lineParts = new ArrayList<String>();
                    for (final String s : ParserImpl.WHITESPACES.split(line)) {
                        if (!s.isEmpty()) {
                            lineParts.add(s);
                        }
                    }
                    if (lineParts.size() < 2) {
                        continue;
                    }
                    final byte[] ipBytes = NetUtil.createByteArrayFromIpAddressString(lineParts.get(0));
                    if (ipBytes == null) {
                        continue;
                    }
                    for (int i = 1; i < lineParts.size(); ++i) {
                        final String hostname = lineParts.get(i);
                        final String hostnameLower = hostname.toLowerCase(Locale.ENGLISH);
                        final InetAddress address = InetAddress.getByAddress(hostname, ipBytes);
                        List<InetAddress> addresses;
                        if (address instanceof Inet4Address) {
                            addresses = ipv4Entries.get(hostnameLower);
                            if (addresses == null) {
                                addresses = new ArrayList<InetAddress>();
                                ipv4Entries.put(hostnameLower, addresses);
                            }
                        }
                        else {
                            addresses = ipv6Entries.get(hostnameLower);
                            if (addresses == null) {
                                addresses = new ArrayList<InetAddress>();
                                ipv6Entries.put(hostnameLower, addresses);
                            }
                        }
                        addresses.add(address);
                    }
                }
                return (ipv4Entries.isEmpty() && ipv6Entries.isEmpty()) ? HostsFileEntriesProvider.EMPTY : new HostsFileEntriesProvider(ipv4Entries, ipv6Entries);
            }
            finally {
                try {
                    buff.close();
                }
                catch (final IOException e) {
                    ParserImpl.logger.warn("Failed to close a reader", e);
                }
            }
        }
        
        @Override
        public HostsFileEntriesProvider parseSilently() {
            return this.parseSilently(locateHostsFile(), Charset.defaultCharset());
        }
        
        @Override
        public HostsFileEntriesProvider parseSilently(final Charset... charsets) {
            return this.parseSilently(locateHostsFile(), charsets);
        }
        
        @Override
        public HostsFileEntriesProvider parseSilently(final File file, final Charset... charsets) {
            try {
                return this.parse(file, charsets);
            }
            catch (final IOException e) {
                if (ParserImpl.logger.isWarnEnabled()) {
                    ParserImpl.logger.warn("Failed to load and parse hosts file at " + file.getPath(), e);
                }
                return HostsFileEntriesProvider.EMPTY;
            }
        }
        
        private static File locateHostsFile() {
            File hostsFile;
            if (PlatformDependent.isWindows()) {
                hostsFile = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\etc\\hosts");
                if (!hostsFile.exists()) {
                    hostsFile = new File("C:\\Windows\\system32\\drivers\\etc\\hosts");
                }
            }
            else {
                hostsFile = new File("/etc/hosts");
            }
            return hostsFile;
        }
        
        static {
            WHITESPACES = Pattern.compile("[ \t]+");
            logger = InternalLoggerFactory.getInstance(Parser.class);
            INSTANCE = new ParserImpl();
        }
    }
    
    public interface Parser
    {
        HostsFileEntriesProvider parse() throws IOException;
        
        HostsFileEntriesProvider parse(final Charset... p0) throws IOException;
        
        HostsFileEntriesProvider parse(final File p0, final Charset... p1) throws IOException;
        
        HostsFileEntriesProvider parse(final Reader p0) throws IOException;
        
        HostsFileEntriesProvider parseSilently();
        
        HostsFileEntriesProvider parseSilently(final Charset... p0);
        
        HostsFileEntriesProvider parseSilently(final File p0, final Charset... p1);
    }
}
