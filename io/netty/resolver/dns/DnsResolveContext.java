package io.netty.resolver.dns;

import java.util.NoSuchElementException;
import java.util.AbstractList;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.dns.DnsRawRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.handler.codec.dns.DnsResponseCode;
import java.net.InetAddress;
import io.netty.util.internal.PlatformDependent;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import java.util.Iterator;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.StringUtil;
import java.net.UnknownHostException;
import io.netty.channel.EventLoop;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.net.InetSocketAddress;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.concurrent.Future;
import java.util.Set;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;

abstract class DnsResolveContext<T>
{
    private static final InternalLogger logger;
    private static final RuntimeException NXDOMAIN_QUERY_FAILED_EXCEPTION;
    private static final RuntimeException CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION;
    private static final RuntimeException NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION;
    private static final RuntimeException UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION;
    private static final RuntimeException NAME_SERVERS_EXHAUSTED_EXCEPTION;
    final DnsNameResolver parent;
    private final Promise<?> originalPromise;
    private final DnsServerAddressStream nameServerAddrs;
    private final String hostname;
    private final int dnsClass;
    private final DnsRecordType[] expectedTypes;
    final DnsRecord[] additionals;
    private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress;
    private List<T> finalResult;
    private int allowedQueries;
    private boolean triedCNAME;
    private boolean completeEarly;
    
    DnsResolveContext(final DnsNameResolver parent, final Promise<?> originalPromise, final String hostname, final int dnsClass, final DnsRecordType[] expectedTypes, final DnsRecord[] additionals, final DnsServerAddressStream nameServerAddrs, final int allowedQueries) {
        this.queriesInProgress = Collections.newSetFromMap(new IdentityHashMap<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>, Boolean>());
        assert expectedTypes.length > 0;
        this.parent = parent;
        this.originalPromise = originalPromise;
        this.hostname = hostname;
        this.dnsClass = dnsClass;
        this.expectedTypes = expectedTypes;
        this.additionals = additionals;
        this.nameServerAddrs = ObjectUtil.checkNotNull(nameServerAddrs, "nameServerAddrs");
        this.allowedQueries = allowedQueries;
    }
    
    DnsCache resolveCache() {
        return this.parent.resolveCache();
    }
    
    DnsCnameCache cnameCache() {
        return this.parent.cnameCache();
    }
    
    AuthoritativeDnsServerCache authoritativeDnsServerCache() {
        return this.parent.authoritativeDnsServerCache();
    }
    
    abstract DnsResolveContext<T> newResolverContext(final DnsNameResolver p0, final Promise<?> p1, final String p2, final int p3, final DnsRecordType[] p4, final DnsRecord[] p5, final DnsServerAddressStream p6, final int p7);
    
    abstract T convertRecord(final DnsRecord p0, final String p1, final DnsRecord[] p2, final EventLoop p3);
    
    abstract List<T> filterResults(final List<T> p0);
    
    abstract boolean isCompleteEarly(final T p0);
    
    abstract boolean isDuplicateAllowed();
    
    abstract void cache(final String p0, final DnsRecord[] p1, final DnsRecord p2, final T p3);
    
    abstract void cache(final String p0, final DnsRecord[] p1, final UnknownHostException p2);
    
    void resolve(final Promise<List<T>> promise) {
        final String[] searchDomains = this.parent.searchDomains();
        if (searchDomains.length == 0 || this.parent.ndots() == 0 || StringUtil.endsWith(this.hostname, '.')) {
            this.internalResolve(this.hostname, promise);
        }
        else {
            final boolean startWithoutSearchDomain = this.hasNDots();
            final String initialHostname = startWithoutSearchDomain ? this.hostname : (this.hostname + '.' + searchDomains[0]);
            final int initialSearchDomainIdx = startWithoutSearchDomain ? 0 : 1;
            final Promise<List<T>> searchDomainPromise = this.parent.executor().newPromise();
            searchDomainPromise.addListener((GenericFutureListener<? extends Future<? super List<T>>>)new FutureListener<List<T>>() {
                private int searchDomainIdx = initialSearchDomainIdx;
                
                @Override
                public void operationComplete(final Future<List<T>> future) {
                    final Throwable cause = future.cause();
                    if (cause == null) {
                        final List<T> result = future.getNow();
                        if (!promise.trySuccess(result)) {
                            for (final T item : result) {
                                ReferenceCountUtil.safeRelease(item);
                            }
                        }
                    }
                    else if (DnsNameResolver.isTransportOrTimeoutError(cause)) {
                        promise.tryFailure(new SearchDomainUnknownHostException(cause, DnsResolveContext.this.hostname));
                    }
                    else if (this.searchDomainIdx < searchDomains.length) {
                        final Promise<List<T>> newPromise = DnsResolveContext.this.parent.executor().newPromise();
                        newPromise.addListener((GenericFutureListener<? extends Future<? super List<T>>>)this);
                        DnsResolveContext.this.doSearchDomainQuery(DnsResolveContext.this.hostname + '.' + searchDomains[this.searchDomainIdx++], newPromise);
                    }
                    else if (!startWithoutSearchDomain) {
                        DnsResolveContext.this.internalResolve(DnsResolveContext.this.hostname, promise);
                    }
                    else {
                        promise.tryFailure(new SearchDomainUnknownHostException(cause, DnsResolveContext.this.hostname));
                    }
                }
            });
            this.doSearchDomainQuery(initialHostname, searchDomainPromise);
        }
    }
    
    private boolean hasNDots() {
        int idx = this.hostname.length() - 1;
        int dots = 0;
        while (idx >= 0) {
            if (this.hostname.charAt(idx) == '.' && ++dots >= this.parent.ndots()) {
                return true;
            }
            --idx;
        }
        return false;
    }
    
    void doSearchDomainQuery(final String hostname, final Promise<List<T>> nextPromise) {
        final DnsResolveContext<T> nextContext = this.newResolverContext(this.parent, this.originalPromise, hostname, this.dnsClass, this.expectedTypes, this.additionals, this.nameServerAddrs, this.parent.maxQueriesPerResolve());
        nextContext.internalResolve(hostname, nextPromise);
    }
    
    private static String hostnameWithDot(final String name) {
        if (StringUtil.endsWith(name, '.')) {
            return name;
        }
        return name + '.';
    }
    
    static String cnameResolveFromCache(final DnsCnameCache cnameCache, final String name) throws UnknownHostException {
        final String first = cnameCache.get(hostnameWithDot(name));
        if (first == null) {
            return name;
        }
        final String second = cnameCache.get(hostnameWithDot(first));
        if (second == null) {
            return first;
        }
        checkCnameLoop(name, first, second);
        return cnameResolveFromCacheLoop(cnameCache, name, first, second);
    }
    
    private static String cnameResolveFromCacheLoop(final DnsCnameCache cnameCache, final String hostname, String first, String mapping) throws UnknownHostException {
        boolean advance = false;
        String name = mapping;
        while ((mapping = cnameCache.get(hostnameWithDot(name))) != null) {
            checkCnameLoop(hostname, first, mapping);
            name = mapping;
            if (advance) {
                first = cnameCache.get(first);
            }
            advance = !advance;
        }
        return name;
    }
    
    private static void checkCnameLoop(final String hostname, final String first, final String second) throws UnknownHostException {
        if (first.equals(second)) {
            throw new UnknownHostException("CNAME loop detected for '" + hostname + '\'');
        }
    }
    
    private void internalResolve(String name, final Promise<List<T>> promise) {
        try {
            name = cnameResolveFromCache(this.cnameCache(), name);
        }
        catch (final Throwable cause) {
            promise.tryFailure(cause);
            return;
        }
        try {
            final DnsServerAddressStream nameServerAddressStream = this.getNameServers(name);
            final int end = this.expectedTypes.length - 1;
            for (int i = 0; i < end; ++i) {
                if (!this.query(name, this.expectedTypes[i], nameServerAddressStream.duplicate(), false, promise)) {
                    return;
                }
            }
            this.query(name, this.expectedTypes[end], nameServerAddressStream, false, promise);
        }
        finally {
            this.parent.flushQueries();
        }
    }
    
    private DnsServerAddressStream getNameServersFromCache(String hostname) {
        final int len = hostname.length();
        if (len == 0) {
            return null;
        }
        if (hostname.charAt(len - 1) != '.') {
            hostname += ".";
        }
        int idx = hostname.indexOf(46);
        if (idx == hostname.length() - 1) {
            return null;
        }
        while (true) {
            hostname = hostname.substring(idx + 1);
            final int idx2 = hostname.indexOf(46);
            if (idx2 <= 0 || idx2 == hostname.length() - 1) {
                return null;
            }
            idx = idx2;
            final DnsServerAddressStream entries = this.authoritativeDnsServerCache().get(hostname);
            if (entries != null) {
                return entries;
            }
        }
    }
    
    private void query(final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, final boolean flush, final Promise<List<T>> promise, final Throwable cause) {
        if (this.completeEarly || nameServerAddrStreamIndex >= nameServerAddrStream.size() || this.allowedQueries == 0 || this.originalPromise.isCancelled() || promise.isCancelled()) {
            this.tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, promise, cause);
            return;
        }
        --this.allowedQueries;
        final InetSocketAddress nameServerAddr = nameServerAddrStream.next();
        if (nameServerAddr.isUnresolved()) {
            this.queryUnresolvedNameServer(nameServerAddr, nameServerAddrStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, promise, cause);
            return;
        }
        final ChannelPromise writePromise = this.parent.ch.newPromise();
        final Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> queryPromise = this.parent.ch.eventLoop().newPromise();
        final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = this.parent.query0(nameServerAddr, question, this.additionals, flush, writePromise, queryPromise);
        this.queriesInProgress.add(f);
        queryLifecycleObserver.queryWritten(nameServerAddr, writePromise);
        f.addListener(new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
            @Override
            public void operationComplete(final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
                DnsResolveContext.this.queriesInProgress.remove(future);
                if (promise.isDone() || future.isCancelled()) {
                    queryLifecycleObserver.queryCancelled(DnsResolveContext.this.allowedQueries);
                    final AddressedEnvelope<DnsResponse, InetSocketAddress> result = future.getNow();
                    if (result != null) {
                        result.release();
                    }
                    return;
                }
                final Throwable queryCause = future.cause();
                try {
                    if (queryCause == null) {
                        DnsResolveContext.this.onResponse(nameServerAddrStream, nameServerAddrStreamIndex, question, future.getNow(), queryLifecycleObserver, promise);
                    }
                    else {
                        queryLifecycleObserver.queryFailed(queryCause);
                        DnsResolveContext.this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, DnsResolveContext.this.newDnsQueryLifecycleObserver(question), true, promise, queryCause);
                    }
                }
                finally {
                    DnsResolveContext.this.tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, NoopDnsQueryLifecycleObserver.INSTANCE, promise, queryCause);
                }
            }
        });
    }
    
    private void queryUnresolvedNameServer(final InetSocketAddress nameServerAddr, final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise, final Throwable cause) {
        final String nameServerName = (PlatformDependent.javaVersion() >= 7) ? nameServerAddr.getHostString() : nameServerAddr.getHostName();
        assert nameServerName != null;
        final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> resolveFuture = this.parent.executor().newSucceededFuture((AddressedEnvelope<DnsResponse, InetSocketAddress>)null);
        this.queriesInProgress.add(resolveFuture);
        final Promise<List<InetAddress>> resolverPromise = this.parent.executor().newPromise();
        resolverPromise.addListener((GenericFutureListener<? extends Future<? super List<InetAddress>>>)new FutureListener<List<InetAddress>>() {
            @Override
            public void operationComplete(final Future<List<InetAddress>> future) {
                DnsResolveContext.this.queriesInProgress.remove(resolveFuture);
                if (future.isSuccess()) {
                    final List<InetAddress> resolvedAddresses = future.getNow();
                    final DnsServerAddressStream addressStream = new CombinedDnsServerAddressStream(nameServerAddr, resolvedAddresses, nameServerAddrStream);
                    DnsResolveContext.this.query(addressStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, true, promise, cause);
                }
                else {
                    DnsResolveContext.this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver, true, promise, cause);
                }
            }
        });
        final DnsCache resolveCache = this.resolveCache();
        if (!DnsNameResolver.doResolveAllCached(nameServerName, this.additionals, resolverPromise, resolveCache, this.parent.resolvedInternetProtocolFamiliesUnsafe())) {
            new DnsAddressResolveContext(this.parent, this.originalPromise, nameServerName, this.additionals, this.parent.newNameServerAddressStream(nameServerName), this.allowedQueries, resolveCache, redirectAuthoritativeDnsServerCache(this.authoritativeDnsServerCache()), false).resolve(resolverPromise);
        }
    }
    
    private static AuthoritativeDnsServerCache redirectAuthoritativeDnsServerCache(final AuthoritativeDnsServerCache authoritativeDnsServerCache) {
        if (authoritativeDnsServerCache instanceof RedirectAuthoritativeDnsServerCache) {
            return authoritativeDnsServerCache;
        }
        return new RedirectAuthoritativeDnsServerCache(authoritativeDnsServerCache);
    }
    
    private void onResponse(final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise) {
        try {
            final DnsResponse res = envelope.content();
            final DnsResponseCode code = res.code();
            if (code == DnsResponseCode.NOERROR) {
                if (this.handleRedirect(question, envelope, queryLifecycleObserver, promise)) {
                    return;
                }
                final DnsRecordType type = question.type();
                if (type == DnsRecordType.CNAME) {
                    this.onResponseCNAME(question, buildAliasMap(envelope.content(), this.cnameCache(), this.parent.executor()), queryLifecycleObserver, promise);
                    return;
                }
                for (final DnsRecordType expectedType : this.expectedTypes) {
                    if (type == expectedType) {
                        this.onExpectedResponse(question, envelope, queryLifecycleObserver, promise);
                        return;
                    }
                }
                queryLifecycleObserver.queryFailed(DnsResolveContext.UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION);
            }
            else if (code != DnsResponseCode.NXDOMAIN) {
                this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver.queryNoAnswer(code), true, promise, null);
            }
            else {
                queryLifecycleObserver.queryFailed(DnsResolveContext.NXDOMAIN_QUERY_FAILED_EXCEPTION);
                if (!res.isAuthoritativeAnswer()) {
                    this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, this.newDnsQueryLifecycleObserver(question), true, promise, null);
                }
            }
        }
        finally {
            ReferenceCountUtil.safeRelease(envelope);
        }
    }
    
    private boolean handleRedirect(final DnsQuestion question, final AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise) {
        final DnsResponse res = envelope.content();
        if (res.count(DnsSection.ANSWER) == 0) {
            final AuthoritativeNameServerList serverNames = extractAuthoritativeNameServers(question.name(), res);
            if (serverNames != null) {
                final int additionalCount = res.count(DnsSection.ADDITIONAL);
                final AuthoritativeDnsServerCache authoritativeDnsServerCache = this.authoritativeDnsServerCache();
                for (int i = 0; i < additionalCount; ++i) {
                    final DnsRecord r = res.recordAt(DnsSection.ADDITIONAL, i);
                    if (r.type() != DnsRecordType.A || this.parent.supportsARecords()) {
                        if (r.type() != DnsRecordType.AAAA || this.parent.supportsAAAARecords()) {
                            serverNames.handleWithAdditional(this.parent, r, authoritativeDnsServerCache);
                        }
                    }
                }
                serverNames.handleWithoutAdditionals(this.parent, this.resolveCache(), authoritativeDnsServerCache);
                final List<InetSocketAddress> addresses = serverNames.addressList();
                final DnsServerAddressStream serverStream = this.parent.newRedirectDnsServerStream(question.name(), addresses);
                if (serverStream != null) {
                    this.query(serverStream, 0, question, queryLifecycleObserver.queryRedirected(new DnsAddressStreamList(serverStream)), true, promise, null);
                    return true;
                }
            }
        }
        return false;
    }
    
    private static AuthoritativeNameServerList extractAuthoritativeNameServers(final String questionName, final DnsResponse res) {
        final int authorityCount = res.count(DnsSection.AUTHORITY);
        if (authorityCount == 0) {
            return null;
        }
        final AuthoritativeNameServerList serverNames = new AuthoritativeNameServerList(questionName);
        for (int i = 0; i < authorityCount; ++i) {
            serverNames.add(res.recordAt(DnsSection.AUTHORITY, i));
        }
        return serverNames.isEmpty() ? null : serverNames;
    }
    
    private void onExpectedResponse(final DnsQuestion question, final AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise) {
        final DnsResponse response = envelope.content();
        final Map<String, String> cnames = buildAliasMap(response, this.cnameCache(), this.parent.executor());
        final int answerCount = response.count(DnsSection.ANSWER);
        boolean found = false;
        boolean completeEarly = this.completeEarly;
        for (int i = 0; i < answerCount; ++i) {
            final DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
            final DnsRecordType type = r.type();
            boolean matches = false;
            for (final DnsRecordType expectedType : this.expectedTypes) {
                if (type == expectedType) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                final String questionName = question.name().toLowerCase(Locale.US);
                final String recordName = r.name().toLowerCase(Locale.US);
                if (!recordName.equals(questionName)) {
                    final Map<String, String> cnamesCopy = new HashMap<String, String>(cnames);
                    String resolved = questionName;
                    do {
                        resolved = cnamesCopy.remove(resolved);
                        if (recordName.equals(resolved)) {
                            break;
                        }
                    } while (resolved != null);
                    if (resolved == null) {
                        assert questionName.charAt(questionName.length() - 1) == '.';
                        for (final String searchDomain : this.parent.searchDomains()) {
                            if (!searchDomain.isEmpty()) {
                                String fqdn;
                                if (searchDomain.charAt(searchDomain.length() - 1) == '.') {
                                    fqdn = questionName + searchDomain;
                                }
                                else {
                                    fqdn = questionName + searchDomain + '.';
                                }
                                if (recordName.equals(fqdn)) {
                                    resolved = recordName;
                                    break;
                                }
                            }
                        }
                        if (resolved == null) {
                            if (DnsResolveContext.logger.isDebugEnabled()) {
                                DnsResolveContext.logger.debug("Ignoring record {} as it contains a different name than the question name [{}]. Cnames: {}, Search domains: {}", r.toString(), questionName, cnames, this.parent.searchDomains());
                            }
                            continue;
                        }
                    }
                }
                final T converted = this.convertRecord(r, this.hostname, this.additionals, this.parent.executor());
                if (converted == null) {
                    if (DnsResolveContext.logger.isDebugEnabled()) {
                        DnsResolveContext.logger.debug("Ignoring record {} as the converted record is null. hostname [{}], Additionals: {}", r.toString(), this.hostname, this.additionals);
                    }
                }
                else {
                    boolean shouldRelease = false;
                    if (!completeEarly) {
                        completeEarly = this.isCompleteEarly(converted);
                    }
                    if (this.finalResult == null) {
                        (this.finalResult = new ArrayList<T>(8)).add(converted);
                    }
                    else if (this.isDuplicateAllowed() || !this.finalResult.contains(converted)) {
                        this.finalResult.add(converted);
                    }
                    else {
                        shouldRelease = true;
                    }
                    this.cache(this.hostname, this.additionals, r, converted);
                    found = true;
                    if (shouldRelease) {
                        ReferenceCountUtil.release(converted);
                    }
                }
            }
        }
        if (cnames.isEmpty()) {
            if (found) {
                if (completeEarly) {
                    this.completeEarly = true;
                }
                queryLifecycleObserver.querySucceed();
                return;
            }
            queryLifecycleObserver.queryFailed(DnsResolveContext.NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION);
        }
        else {
            queryLifecycleObserver.querySucceed();
            this.onResponseCNAME(question, cnames, this.newDnsQueryLifecycleObserver(question), promise);
        }
    }
    
    private void onResponseCNAME(final DnsQuestion question, final Map<String, String> cnames, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise) {
        String resolved = question.name().toLowerCase(Locale.US);
        boolean found = false;
        while (!cnames.isEmpty()) {
            final String next = cnames.remove(resolved);
            if (next == null) {
                break;
            }
            found = true;
            resolved = next;
        }
        if (found) {
            this.followCname(question, resolved, queryLifecycleObserver, promise);
        }
        else {
            queryLifecycleObserver.queryFailed(DnsResolveContext.CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION);
        }
    }
    
    private static Map<String, String> buildAliasMap(final DnsResponse response, final DnsCnameCache cache, final EventLoop loop) {
        final int answerCount = response.count(DnsSection.ANSWER);
        Map<String, String> cnames = null;
        for (int i = 0; i < answerCount; ++i) {
            final DnsRecord r = response.recordAt(DnsSection.ANSWER, i);
            final DnsRecordType type = r.type();
            if (type == DnsRecordType.CNAME) {
                if (r instanceof DnsRawRecord) {
                    final ByteBuf recordContent = ((ByteBufHolder)r).content();
                    final String domainName = decodeDomainName(recordContent);
                    if (domainName != null) {
                        if (cnames == null) {
                            cnames = new HashMap<String, String>(Math.min(8, answerCount));
                        }
                        final String name = r.name().toLowerCase(Locale.US);
                        final String mapping = domainName.toLowerCase(Locale.US);
                        final String nameWithDot = hostnameWithDot(name);
                        final String mappingWithDot = hostnameWithDot(mapping);
                        if (!nameWithDot.equalsIgnoreCase(mappingWithDot)) {
                            cache.cache(nameWithDot, mappingWithDot, r.timeToLive(), loop);
                            cnames.put(name, mapping);
                        }
                    }
                }
            }
        }
        return (cnames != null) ? cnames : Collections.emptyMap();
    }
    
    private void tryToFinishResolve(final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise, final Throwable cause) {
        if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
            queryLifecycleObserver.queryCancelled(this.allowedQueries);
            return;
        }
        if (this.finalResult == null) {
            if (nameServerAddrStreamIndex < nameServerAddrStream.size()) {
                if (queryLifecycleObserver == NoopDnsQueryLifecycleObserver.INSTANCE) {
                    this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, this.newDnsQueryLifecycleObserver(question), true, promise, cause);
                }
                else {
                    this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver, true, promise, cause);
                }
                return;
            }
            queryLifecycleObserver.queryFailed(DnsResolveContext.NAME_SERVERS_EXHAUSTED_EXCEPTION);
            if (cause == null && !this.triedCNAME && (question.type() == DnsRecordType.A || question.type() == DnsRecordType.AAAA)) {
                this.triedCNAME = true;
                this.query(this.hostname, DnsRecordType.CNAME, this.getNameServers(this.hostname), true, promise);
                return;
            }
        }
        else {
            queryLifecycleObserver.queryCancelled(this.allowedQueries);
        }
        this.finishResolve(promise, cause);
    }
    
    private void finishResolve(final Promise<List<T>> promise, final Throwable cause) {
        if (!this.completeEarly && !this.queriesInProgress.isEmpty()) {
            final Iterator<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> i = this.queriesInProgress.iterator();
            while (i.hasNext()) {
                final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = i.next();
                i.remove();
                f.cancel(false);
            }
        }
        if (this.finalResult != null) {
            if (!promise.isDone()) {
                final List<T> result = this.filterResults(this.finalResult);
                if (!DnsNameResolver.trySuccess(promise, result)) {
                    for (final T item : result) {
                        ReferenceCountUtil.safeRelease(item);
                    }
                }
            }
            return;
        }
        final int maxAllowedQueries = this.parent.maxQueriesPerResolve();
        final int tries = maxAllowedQueries - this.allowedQueries;
        final StringBuilder buf = new StringBuilder(64);
        buf.append("failed to resolve '").append(this.hostname).append('\'');
        if (tries > 1) {
            if (tries < maxAllowedQueries) {
                buf.append(" after ").append(tries).append(" queries ");
            }
            else {
                buf.append(". Exceeded max queries per resolve ").append(maxAllowedQueries).append(' ');
            }
        }
        final UnknownHostException unknownHostException = new UnknownHostException(buf.toString());
        if (cause == null) {
            this.cache(this.hostname, this.additionals, unknownHostException);
        }
        else {
            unknownHostException.initCause(cause);
        }
        promise.tryFailure(unknownHostException);
    }
    
    static String decodeDomainName(final ByteBuf in) {
        in.markReaderIndex();
        try {
            return DefaultDnsRecordDecoder.decodeName(in);
        }
        catch (final CorruptedFrameException e) {
            return null;
        }
        finally {
            in.resetReaderIndex();
        }
    }
    
    private DnsServerAddressStream getNameServers(final String name) {
        final DnsServerAddressStream stream = this.getNameServersFromCache(name);
        if (stream != null) {
            return stream;
        }
        if (name.equals(this.hostname)) {
            return this.nameServerAddrs.duplicate();
        }
        return this.parent.newNameServerAddressStream(name);
    }
    
    private void followCname(final DnsQuestion question, String cname, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<List<T>> promise) {
        DnsServerAddressStream stream;
        DnsQuestion cnameQuestion;
        try {
            cname = cnameResolveFromCache(this.cnameCache(), cname);
            stream = this.getNameServers(cname);
            cnameQuestion = new DefaultDnsQuestion(cname, question.type(), this.dnsClass);
        }
        catch (final Throwable cause) {
            queryLifecycleObserver.queryFailed(cause);
            PlatformDependent.throwException(cause);
            return;
        }
        this.query(stream, 0, cnameQuestion, queryLifecycleObserver.queryCNAMEd(cnameQuestion), true, promise, null);
    }
    
    private boolean query(final String hostname, final DnsRecordType type, final DnsServerAddressStream dnsServerAddressStream, final boolean flush, final Promise<List<T>> promise) {
        DnsQuestion question;
        try {
            question = new DefaultDnsQuestion(hostname, type, this.dnsClass);
        }
        catch (final Throwable cause) {
            promise.tryFailure(new IllegalArgumentException("Unable to create DNS Question for: [" + hostname + ", " + type + ']', cause));
            return false;
        }
        this.query(dnsServerAddressStream, 0, question, this.newDnsQueryLifecycleObserver(question), flush, promise, null);
        return true;
    }
    
    private DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(final DnsQuestion question) {
        return this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(question);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DnsResolveContext.class);
        NXDOMAIN_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No answer found and NXDOMAIN response code returned", DnsResolveContext.class, "onResponse(..)");
        CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No matching CNAME record found", DnsResolveContext.class, "onResponseCNAME(..)");
        NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("No matching record type found", DnsResolveContext.class, "onResponseAorAAAA(..)");
        UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION = DnsResolveContextException.newStatic("Response type was unrecognized", DnsResolveContext.class, "onResponse(..)");
        NAME_SERVERS_EXHAUSTED_EXCEPTION = DnsResolveContextException.newStatic("No name servers returned an answer", DnsResolveContext.class, "tryToFinishResolve(..)");
    }
    
    static final class DnsResolveContextException extends RuntimeException
    {
        private static final long serialVersionUID = 1209303419266433003L;
        
        private DnsResolveContextException(final String message) {
            super(message);
        }
        
        @SuppressJava6Requirement(reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
        private DnsResolveContextException(final String message, final boolean shared) {
            super(message, null, false, true);
            assert shared;
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
        
        static DnsResolveContextException newStatic(final String message, final Class<?> clazz, final String method) {
            DnsResolveContextException exception;
            if (PlatformDependent.javaVersion() >= 7) {
                exception = new DnsResolveContextException(message, true);
            }
            else {
                exception = new DnsResolveContextException(message);
            }
            return ThrowableUtil.unknownStackTrace(exception, clazz, method);
        }
    }
    
    private static final class SearchDomainUnknownHostException extends UnknownHostException
    {
        private static final long serialVersionUID = -8573510133644997085L;
        
        SearchDomainUnknownHostException(final Throwable cause, final String originalHostname) {
            super("Search domain query failed. Original hostname: '" + originalHostname + "' " + cause.getMessage());
            this.setStackTrace(cause.getStackTrace());
            this.initCause(cause.getCause());
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
    
    private static final class RedirectAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache
    {
        private final AuthoritativeDnsServerCache wrapped;
        
        RedirectAuthoritativeDnsServerCache(final AuthoritativeDnsServerCache authoritativeDnsServerCache) {
            this.wrapped = authoritativeDnsServerCache;
        }
        
        @Override
        public DnsServerAddressStream get(final String hostname) {
            return null;
        }
        
        @Override
        public void cache(final String hostname, final InetSocketAddress address, final long originalTtl, final EventLoop loop) {
            this.wrapped.cache(hostname, address, originalTtl, loop);
        }
        
        @Override
        public void clear() {
            this.wrapped.clear();
        }
        
        @Override
        public boolean clear(final String hostname) {
            return this.wrapped.clear(hostname);
        }
    }
    
    private static final class DnsAddressStreamList extends AbstractList<InetSocketAddress>
    {
        private final DnsServerAddressStream duplicate;
        private List<InetSocketAddress> addresses;
        
        DnsAddressStreamList(final DnsServerAddressStream stream) {
            this.duplicate = stream.duplicate();
        }
        
        @Override
        public InetSocketAddress get(final int index) {
            if (this.addresses == null) {
                final DnsServerAddressStream stream = this.duplicate.duplicate();
                this.addresses = new ArrayList<InetSocketAddress>(this.size());
                for (int i = 0; i < stream.size(); ++i) {
                    this.addresses.add(stream.next());
                }
            }
            return this.addresses.get(index);
        }
        
        @Override
        public int size() {
            return this.duplicate.size();
        }
        
        @Override
        public Iterator<InetSocketAddress> iterator() {
            return new Iterator<InetSocketAddress>() {
                private final DnsServerAddressStream stream = DnsAddressStreamList.this.duplicate.duplicate();
                private int i;
                
                @Override
                public boolean hasNext() {
                    return this.i < this.stream.size();
                }
                
                @Override
                public InetSocketAddress next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    ++this.i;
                    return this.stream.next();
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    private final class CombinedDnsServerAddressStream implements DnsServerAddressStream
    {
        private final InetSocketAddress replaced;
        private final DnsServerAddressStream originalStream;
        private final List<InetAddress> resolvedAddresses;
        private Iterator<InetAddress> resolved;
        
        CombinedDnsServerAddressStream(final InetSocketAddress replaced, final List<InetAddress> resolvedAddresses, final DnsServerAddressStream originalStream) {
            this.replaced = replaced;
            this.resolvedAddresses = resolvedAddresses;
            this.originalStream = originalStream;
            this.resolved = resolvedAddresses.iterator();
        }
        
        @Override
        public InetSocketAddress next() {
            if (this.resolved.hasNext()) {
                return this.nextResolved0();
            }
            final InetSocketAddress address = this.originalStream.next();
            if (address.equals(this.replaced)) {
                this.resolved = this.resolvedAddresses.iterator();
                return this.nextResolved0();
            }
            return address;
        }
        
        private InetSocketAddress nextResolved0() {
            return DnsResolveContext.this.parent.newRedirectServerAddress(this.resolved.next());
        }
        
        @Override
        public int size() {
            return this.originalStream.size() + this.resolvedAddresses.size() - 1;
        }
        
        @Override
        public DnsServerAddressStream duplicate() {
            return new CombinedDnsServerAddressStream(this.replaced, this.resolvedAddresses, this.originalStream.duplicate());
        }
    }
    
    private static final class AuthoritativeNameServerList
    {
        private final String questionName;
        private AuthoritativeNameServer head;
        private int nameServerCount;
        
        AuthoritativeNameServerList(final String questionName) {
            this.questionName = questionName.toLowerCase(Locale.US);
        }
        
        void add(final DnsRecord r) {
            if (r.type() != DnsRecordType.NS || !(r instanceof DnsRawRecord)) {
                return;
            }
            if (this.questionName.length() < r.name().length()) {
                return;
            }
            final String recordName = r.name().toLowerCase(Locale.US);
            int dots = 0;
            for (int a = recordName.length() - 1, b = this.questionName.length() - 1; a >= 0; --a, --b) {
                final char c = recordName.charAt(a);
                if (this.questionName.charAt(b) != c) {
                    return;
                }
                if (c == '.') {
                    ++dots;
                }
            }
            if (this.head != null && this.head.dots > dots) {
                return;
            }
            final ByteBuf recordContent = ((ByteBufHolder)r).content();
            final String domainName = DnsResolveContext.decodeDomainName(recordContent);
            if (domainName == null) {
                return;
            }
            if (this.head == null || this.head.dots < dots) {
                this.nameServerCount = 1;
                this.head = new AuthoritativeNameServer(dots, r.timeToLive(), recordName, domainName);
            }
            else if (this.head.dots == dots) {
                AuthoritativeNameServer serverName;
                for (serverName = this.head; serverName.next != null; serverName = serverName.next) {}
                serverName.next = new AuthoritativeNameServer(dots, r.timeToLive(), recordName, domainName);
                ++this.nameServerCount;
            }
        }
        
        void handleWithAdditional(final DnsNameResolver parent, final DnsRecord r, final AuthoritativeDnsServerCache authoritativeCache) {
            AuthoritativeNameServer serverName = this.head;
            final String nsName = r.name();
            final InetAddress resolved = DnsAddressDecoder.decodeAddress(r, nsName, parent.isDecodeIdn());
            if (resolved == null) {
                return;
            }
            while (serverName != null) {
                if (serverName.nsName.equalsIgnoreCase(nsName)) {
                    if (serverName.address != null) {
                        while (serverName.next != null && serverName.next.isCopy) {
                            serverName = serverName.next;
                        }
                        final AuthoritativeNameServer server = new AuthoritativeNameServer(serverName);
                        server.next = serverName.next;
                        serverName.next = server;
                        serverName = server;
                        ++this.nameServerCount;
                    }
                    serverName.update(parent.newRedirectServerAddress(resolved), r.timeToLive());
                    cache(serverName, authoritativeCache, parent.executor());
                    return;
                }
                serverName = serverName.next;
            }
        }
        
        void handleWithoutAdditionals(final DnsNameResolver parent, final DnsCache cache, final AuthoritativeDnsServerCache authoritativeCache) {
            for (AuthoritativeNameServer serverName = this.head; serverName != null; serverName = serverName.next) {
                if (serverName.address == null) {
                    cacheUnresolved(serverName, authoritativeCache, parent.executor());
                    final List<? extends DnsCacheEntry> entries = cache.get(serverName.nsName, null);
                    if (entries != null && !entries.isEmpty()) {
                        InetAddress address = ((DnsCacheEntry)entries.get(0)).address();
                        if (address != null) {
                            serverName.update(parent.newRedirectServerAddress(address));
                            for (int i = 1; i < entries.size(); ++i) {
                                address = ((DnsCacheEntry)entries.get(i)).address();
                                assert address != null : "Cache returned a cached failure, should never return anything else";
                                final AuthoritativeNameServer server = new AuthoritativeNameServer(serverName);
                                server.next = serverName.next;
                                serverName.next = server;
                                serverName = server;
                                serverName.update(parent.newRedirectServerAddress(address));
                                ++this.nameServerCount;
                            }
                        }
                    }
                }
            }
        }
        
        private static void cacheUnresolved(final AuthoritativeNameServer server, final AuthoritativeDnsServerCache authoritativeCache, final EventLoop loop) {
            server.address = InetSocketAddress.createUnresolved(server.nsName, 53);
            cache(server, authoritativeCache, loop);
        }
        
        private static void cache(final AuthoritativeNameServer server, final AuthoritativeDnsServerCache cache, final EventLoop loop) {
            if (!server.isRootServer()) {
                cache.cache(server.domainName, server.address, server.ttl, loop);
            }
        }
        
        boolean isEmpty() {
            return this.nameServerCount == 0;
        }
        
        List<InetSocketAddress> addressList() {
            final List<InetSocketAddress> addressList = new ArrayList<InetSocketAddress>(this.nameServerCount);
            for (AuthoritativeNameServer server = this.head; server != null; server = server.next) {
                if (server.address != null) {
                    addressList.add(server.address);
                }
            }
            return addressList;
        }
    }
    
    private static final class AuthoritativeNameServer
    {
        private final int dots;
        private final String domainName;
        final boolean isCopy;
        final String nsName;
        private long ttl;
        private InetSocketAddress address;
        AuthoritativeNameServer next;
        
        AuthoritativeNameServer(final int dots, final long ttl, final String domainName, final String nsName) {
            this.dots = dots;
            this.ttl = ttl;
            this.nsName = nsName;
            this.domainName = domainName;
            this.isCopy = false;
        }
        
        AuthoritativeNameServer(final AuthoritativeNameServer server) {
            this.dots = server.dots;
            this.ttl = server.ttl;
            this.nsName = server.nsName;
            this.domainName = server.domainName;
            this.isCopy = true;
        }
        
        boolean isRootServer() {
            return this.dots == 1;
        }
        
        void update(final InetSocketAddress address, final long ttl) {
            assert !(!this.address.isUnresolved());
            this.address = address;
            this.ttl = Math.min(this.ttl, ttl);
        }
        
        void update(final InetSocketAddress address) {
            this.update(address, Long.MAX_VALUE);
        }
    }
}
