package io.netty.resolver.dns;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.List;
import io.netty.util.internal.SocketUtils;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.io.IOException;
import java.util.Collection;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;
import io.netty.util.internal.logging.InternalLogger;

public final class UnixResolverDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider
{
    private static final InternalLogger logger;
    private static final Pattern WHITESPACE_PATTERN;
    private static final String RES_OPTIONS;
    private static final String ETC_RESOLV_CONF_FILE = "/etc/resolv.conf";
    private static final String ETC_RESOLVER_DIR = "/etc/resolver";
    private static final String NAMESERVER_ROW_LABEL = "nameserver";
    private static final String SORTLIST_ROW_LABEL = "sortlist";
    private static final String OPTIONS_ROW_LABEL = "options ";
    private static final String OPTIONS_ROTATE_FLAG = "rotate";
    private static final String DOMAIN_ROW_LABEL = "domain";
    private static final String SEARCH_ROW_LABEL = "search";
    private static final String PORT_ROW_LABEL = "port";
    private final DnsServerAddresses defaultNameServerAddresses;
    private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;
    
    static DnsServerAddressStreamProvider parseSilently() {
        try {
            final UnixResolverDnsServerAddressStreamProvider nameServerCache = new UnixResolverDnsServerAddressStreamProvider("/etc/resolv.conf", "/etc/resolver");
            return nameServerCache.mayOverrideNameServers() ? nameServerCache : DefaultDnsServerAddressStreamProvider.INSTANCE;
        }
        catch (final Exception e) {
            if (UnixResolverDnsServerAddressStreamProvider.logger.isDebugEnabled()) {
                UnixResolverDnsServerAddressStreamProvider.logger.debug("failed to parse {} and/or {}", "/etc/resolv.conf", "/etc/resolver", e);
            }
            return DefaultDnsServerAddressStreamProvider.INSTANCE;
        }
    }
    
    public UnixResolverDnsServerAddressStreamProvider(final File etcResolvConf, final File... etcResolverFiles) throws IOException {
        final Map<String, DnsServerAddresses> etcResolvConfMap = parse(ObjectUtil.checkNotNull(etcResolvConf, "etcResolvConf"));
        final boolean useEtcResolverFiles = etcResolverFiles != null && etcResolverFiles.length != 0;
        this.domainToNameServerStreamMap = (useEtcResolverFiles ? parse(etcResolverFiles) : etcResolvConfMap);
        final DnsServerAddresses defaultNameServerAddresses = etcResolvConfMap.get(etcResolvConf.getName());
        if (defaultNameServerAddresses == null) {
            final Collection<DnsServerAddresses> values = etcResolvConfMap.values();
            if (values.isEmpty()) {
                throw new IllegalArgumentException(etcResolvConf + " didn't provide any name servers");
            }
            this.defaultNameServerAddresses = values.iterator().next();
        }
        else {
            this.defaultNameServerAddresses = defaultNameServerAddresses;
        }
        if (useEtcResolverFiles) {
            this.domainToNameServerStreamMap.putAll(etcResolvConfMap);
        }
    }
    
    public UnixResolverDnsServerAddressStreamProvider(final String etcResolvConf, final String etcResolverDir) throws IOException {
        this((etcResolvConf == null) ? null : new File(etcResolvConf), (File[])((etcResolverDir == null) ? null : new File(etcResolverDir).listFiles()));
    }
    
    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        while (true) {
            final int i = hostname.indexOf(46, 1);
            if (i < 0 || i == hostname.length() - 1) {
                return this.defaultNameServerAddresses.stream();
            }
            final DnsServerAddresses addresses = this.domainToNameServerStreamMap.get(hostname);
            if (addresses != null) {
                return addresses.stream();
            }
            hostname = hostname.substring(i + 1);
        }
    }
    
    private boolean mayOverrideNameServers() {
        return !this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses.stream().next() != null;
    }
    
    private static Map<String, DnsServerAddresses> parse(final File... etcResolverFiles) throws IOException {
        final Map<String, DnsServerAddresses> domainToNameServerStreamMap = new HashMap<String, DnsServerAddresses>(etcResolverFiles.length << 1);
        final boolean rotateGlobal = UnixResolverDnsServerAddressStreamProvider.RES_OPTIONS != null && UnixResolverDnsServerAddressStreamProvider.RES_OPTIONS.contains("rotate");
        for (final File etcResolverFile : etcResolverFiles) {
            if (etcResolverFile.isFile()) {
                final FileReader fr = new FileReader(etcResolverFile);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(fr);
                    List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(2);
                    String domainName = etcResolverFile.getName();
                    boolean rotate = rotateGlobal;
                    int port = 53;
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        try {
                            final char c;
                            if (line.isEmpty() || (c = line.charAt(0)) == '#' || c == ';') {
                                continue;
                            }
                            if (!rotate && line.startsWith("options ")) {
                                rotate = line.contains("rotate");
                            }
                            else if (line.startsWith("nameserver")) {
                                int i = StringUtil.indexOfNonWhiteSpace(line, "nameserver".length());
                                if (i < 0) {
                                    throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". value: " + line);
                                }
                                final int x = StringUtil.indexOfWhiteSpace(line, i);
                                String maybeIP;
                                if (x == -1) {
                                    maybeIP = line.substring(i);
                                }
                                else {
                                    final int idx = StringUtil.indexOfNonWhiteSpace(line, x);
                                    if (idx == -1 || line.charAt(idx) != '#') {
                                        throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". value: " + line);
                                    }
                                    maybeIP = line.substring(i, x);
                                }
                                if (!NetUtil.isValidIpV4Address(maybeIP) && !NetUtil.isValidIpV6Address(maybeIP)) {
                                    i = maybeIP.lastIndexOf(46);
                                    if (i + 1 >= maybeIP.length()) {
                                        throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". invalid IP value: " + line);
                                    }
                                    port = Integer.parseInt(maybeIP.substring(i + 1));
                                    maybeIP = maybeIP.substring(0, i);
                                }
                                addresses.add(SocketUtils.socketAddress(maybeIP, port));
                            }
                            else if (line.startsWith("domain")) {
                                final int i = StringUtil.indexOfNonWhiteSpace(line, "domain".length());
                                if (i < 0) {
                                    throw new IllegalArgumentException("error parsing label domain in file " + etcResolverFile + " value: " + line);
                                }
                                domainName = line.substring(i);
                                if (!addresses.isEmpty()) {
                                    putIfAbsent(domainToNameServerStreamMap, domainName, addresses, rotate);
                                }
                                addresses = new ArrayList<InetSocketAddress>(2);
                            }
                            else if (line.startsWith("port")) {
                                final int i = StringUtil.indexOfNonWhiteSpace(line, "port".length());
                                if (i < 0) {
                                    throw new IllegalArgumentException("error parsing label port in file " + etcResolverFile + " value: " + line);
                                }
                                port = Integer.parseInt(line.substring(i));
                            }
                            else {
                                if (!line.startsWith("sortlist")) {
                                    continue;
                                }
                                UnixResolverDnsServerAddressStreamProvider.logger.info("row type {} not supported. Ignoring line: {}", "sortlist", line);
                            }
                        }
                        catch (final IllegalArgumentException e) {
                            UnixResolverDnsServerAddressStreamProvider.logger.warn("Could not parse entry. Ignoring line: {}", line, e);
                        }
                    }
                    if (!addresses.isEmpty()) {
                        putIfAbsent(domainToNameServerStreamMap, domainName, addresses, rotate);
                    }
                }
                finally {
                    if (br == null) {
                        fr.close();
                    }
                    else {
                        br.close();
                    }
                }
            }
        }
        return domainToNameServerStreamMap;
    }
    
    private static void putIfAbsent(final Map<String, DnsServerAddresses> domainToNameServerStreamMap, final String domainName, final List<InetSocketAddress> addresses, final boolean rotate) {
        final DnsServerAddresses addrs = rotate ? DnsServerAddresses.rotational(addresses) : DnsServerAddresses.sequential(addresses);
        putIfAbsent(domainToNameServerStreamMap, domainName, addrs);
    }
    
    private static void putIfAbsent(final Map<String, DnsServerAddresses> domainToNameServerStreamMap, final String domainName, final DnsServerAddresses addresses) {
        final DnsServerAddresses existingAddresses = domainToNameServerStreamMap.put(domainName, addresses);
        if (existingAddresses != null) {
            domainToNameServerStreamMap.put(domainName, existingAddresses);
            if (UnixResolverDnsServerAddressStreamProvider.logger.isDebugEnabled()) {
                UnixResolverDnsServerAddressStreamProvider.logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", domainName, existingAddresses, addresses);
            }
        }
    }
    
    static UnixResolverOptions parseEtcResolverOptions() throws IOException {
        return parseEtcResolverOptions(new File("/etc/resolv.conf"));
    }
    
    static UnixResolverOptions parseEtcResolverOptions(final File etcResolvConf) throws IOException {
        final UnixResolverOptions.Builder optionsBuilder = UnixResolverOptions.newBuilder();
        final FileReader fr = new FileReader(etcResolvConf);
        BufferedReader br = null;
        try {
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("options ")) {
                    parseResOptions(line.substring("options ".length()), optionsBuilder);
                    break;
                }
            }
        }
        finally {
            if (br == null) {
                fr.close();
            }
            else {
                br.close();
            }
        }
        if (UnixResolverDnsServerAddressStreamProvider.RES_OPTIONS != null) {
            parseResOptions(UnixResolverDnsServerAddressStreamProvider.RES_OPTIONS, optionsBuilder);
        }
        return optionsBuilder.build();
    }
    
    private static void parseResOptions(final String line, final UnixResolverOptions.Builder builder) {
        final String[] split;
        final String[] opts = split = UnixResolverDnsServerAddressStreamProvider.WHITESPACE_PATTERN.split(line);
        for (final String opt : split) {
            try {
                if (opt.startsWith("ndots:")) {
                    builder.setNdots(parseResIntOption(opt, "ndots:"));
                }
                else if (opt.startsWith("attempts:")) {
                    builder.setAttempts(parseResIntOption(opt, "attempts:"));
                }
                else if (opt.startsWith("timeout:")) {
                    builder.setTimeout(parseResIntOption(opt, "timeout:"));
                }
            }
            catch (final NumberFormatException ex) {}
        }
    }
    
    private static int parseResIntOption(final String opt, final String fullLabel) {
        final String optValue = opt.substring(fullLabel.length());
        return Integer.parseInt(optValue);
    }
    
    static List<String> parseEtcResolverSearchDomains() throws IOException {
        return parseEtcResolverSearchDomains(new File("/etc/resolv.conf"));
    }
    
    static List<String> parseEtcResolverSearchDomains(final File etcResolvConf) throws IOException {
        String localDomain = null;
        final List<String> searchDomains = new ArrayList<String>();
        final FileReader fr = new FileReader(etcResolvConf);
        BufferedReader br = null;
        try {
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (localDomain == null && line.startsWith("domain")) {
                    final int i = StringUtil.indexOfNonWhiteSpace(line, "domain".length());
                    if (i < 0) {
                        continue;
                    }
                    localDomain = line.substring(i);
                }
                else {
                    if (!line.startsWith("search")) {
                        continue;
                    }
                    final int i = StringUtil.indexOfNonWhiteSpace(line, "search".length());
                    if (i < 0) {
                        continue;
                    }
                    final String[] domains = UnixResolverDnsServerAddressStreamProvider.WHITESPACE_PATTERN.split(line.substring(i));
                    Collections.addAll(searchDomains, domains);
                }
            }
        }
        finally {
            if (br == null) {
                fr.close();
            }
            else {
                br.close();
            }
        }
        return (localDomain != null && searchDomains.isEmpty()) ? Collections.singletonList(localDomain) : searchDomains;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
        WHITESPACE_PATTERN = Pattern.compile("\\s+");
        RES_OPTIONS = System.getenv("RES_OPTIONS");
    }
}
