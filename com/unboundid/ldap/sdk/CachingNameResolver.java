package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Arrays;
import java.net.UnknownHostException;
import com.unboundid.util.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.net.InetAddress;
import com.unboundid.util.ObjectPair;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CachingNameResolver extends NameResolver
{
    private static final int DEFAULT_TIMEOUT_MILLIS = 3600000;
    private final AtomicReference<ObjectPair<Long, InetAddress>> localHostAddress;
    private final AtomicReference<ObjectPair<Long, InetAddress>> loopbackAddress;
    private final Map<InetAddress, ObjectPair<Long, String>> addressToNameMap;
    private final Map<String, ObjectPair<Long, InetAddress[]>> nameToAddressMap;
    private final long timeoutMillis;
    
    public CachingNameResolver() {
        this(3600000);
    }
    
    public CachingNameResolver(final int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.localHostAddress = new AtomicReference<ObjectPair<Long, InetAddress>>();
        this.loopbackAddress = new AtomicReference<ObjectPair<Long, InetAddress>>();
        this.addressToNameMap = new ConcurrentHashMap<InetAddress, ObjectPair<Long, String>>(20);
        this.nameToAddressMap = new ConcurrentHashMap<String, ObjectPair<Long, InetAddress[]>>(20);
    }
    
    public int getTimeoutMillis() {
        return (int)this.timeoutMillis;
    }
    
    @Override
    public InetAddress getByName(final String host) throws UnknownHostException, SecurityException {
        final InetAddress[] addresses = this.getAllByNameInternal(host);
        if (addresses.length == 1) {
            return addresses[0];
        }
        return addresses[ThreadLocalRandom.get().nextInt(addresses.length)];
    }
    
    @Override
    public InetAddress[] getAllByName(final String host) throws UnknownHostException, SecurityException {
        final InetAddress[] addresses = this.getAllByNameInternal(host);
        return Arrays.copyOf(addresses, addresses.length);
    }
    
    public InetAddress[] getAllByNameInternal(final String host) throws UnknownHostException, SecurityException {
        String lowerHost;
        if (host == null) {
            lowerHost = "";
        }
        else {
            lowerHost = StaticUtils.toLowerCase(host);
        }
        final ObjectPair<Long, InetAddress[]> cachedRecord = this.nameToAddressMap.get(lowerHost);
        if (cachedRecord == null) {
            return this.lookUpAndCache(host, lowerHost);
        }
        if (System.currentTimeMillis() <= cachedRecord.getFirst()) {
            return cachedRecord.getSecond();
        }
        try {
            return this.lookUpAndCache(host, lowerHost);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return cachedRecord.getSecond();
        }
    }
    
    private InetAddress[] lookUpAndCache(final String host, final String lowerHost) throws UnknownHostException, SecurityException {
        final InetAddress[] addresses = InetAddress.getAllByName(host);
        final long cacheRecordExpirationTime = System.currentTimeMillis() + this.timeoutMillis;
        final ObjectPair<Long, InetAddress[]> cacheRecord = new ObjectPair<Long, InetAddress[]>(cacheRecordExpirationTime, addresses);
        this.nameToAddressMap.put(lowerHost, cacheRecord);
        return addresses;
    }
    
    @Override
    public String getHostName(final InetAddress inetAddress) {
        final String stringRepresentation = String.valueOf(inetAddress);
        final int lastSlashPos = stringRepresentation.lastIndexOf(47);
        if (lastSlashPos > 0) {
            return stringRepresentation.substring(0, lastSlashPos);
        }
        return this.getCanonicalHostName(inetAddress);
    }
    
    @Override
    public String getCanonicalHostName(final InetAddress inetAddress) {
        final ObjectPair<Long, String> cachedRecord = this.addressToNameMap.get(inetAddress);
        if (cachedRecord == null) {
            return this.lookUpAndCache(inetAddress, null);
        }
        if (System.currentTimeMillis() <= cachedRecord.getFirst()) {
            return cachedRecord.getSecond();
        }
        return this.lookUpAndCache(inetAddress, cachedRecord.getSecond());
    }
    
    private String lookUpAndCache(final InetAddress inetAddress, final String cachedName) {
        final String canonicalHostName = inetAddress.getCanonicalHostName();
        if (!canonicalHostName.equals(inetAddress.getHostAddress())) {
            final long cacheRecordExpirationTime = System.currentTimeMillis() + this.timeoutMillis;
            final ObjectPair<Long, String> cacheRecord = new ObjectPair<Long, String>(cacheRecordExpirationTime, canonicalHostName);
            this.addressToNameMap.put(inetAddress, cacheRecord);
            return canonicalHostName;
        }
        if (cachedName == null) {
            return canonicalHostName;
        }
        return cachedName;
    }
    
    @Override
    public InetAddress getLocalHost() throws UnknownHostException, SecurityException {
        final ObjectPair<Long, InetAddress> cachedAddress = this.localHostAddress.get();
        if (cachedAddress == null) {
            final InetAddress localHost = InetAddress.getLocalHost();
            final long expirationTime = System.currentTimeMillis() + this.timeoutMillis;
            this.localHostAddress.set(new ObjectPair<Long, InetAddress>(expirationTime, localHost));
            return localHost;
        }
        final long cachedRecordExpirationTime = cachedAddress.getFirst();
        if (System.currentTimeMillis() <= cachedRecordExpirationTime) {
            return cachedAddress.getSecond();
        }
        try {
            final InetAddress localHost2 = InetAddress.getLocalHost();
            final long expirationTime2 = System.currentTimeMillis() + this.timeoutMillis;
            this.localHostAddress.set(new ObjectPair<Long, InetAddress>(expirationTime2, localHost2));
            return localHost2;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return cachedAddress.getSecond();
        }
    }
    
    @Override
    public InetAddress getLoopbackAddress() {
        final ObjectPair<Long, InetAddress> cachedAddress = this.loopbackAddress.get();
        if (cachedAddress == null) {
            final InetAddress address = InetAddress.getLoopbackAddress();
            final long expirationTime = System.currentTimeMillis() + this.timeoutMillis;
            this.loopbackAddress.set(new ObjectPair<Long, InetAddress>(expirationTime, address));
            return address;
        }
        final long cachedRecordExpirationTime = cachedAddress.getFirst();
        if (System.currentTimeMillis() <= cachedRecordExpirationTime) {
            return cachedAddress.getSecond();
        }
        try {
            final InetAddress address2 = InetAddress.getLoopbackAddress();
            final long expirationTime2 = System.currentTimeMillis() + this.timeoutMillis;
            this.loopbackAddress.set(new ObjectPair<Long, InetAddress>(expirationTime2, address2));
            return address2;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return cachedAddress.getSecond();
        }
    }
    
    public void clearCache() {
        this.localHostAddress.set(null);
        this.loopbackAddress.set(null);
        this.addressToNameMap.clear();
        this.nameToAddressMap.clear();
    }
    
    Map<InetAddress, ObjectPair<Long, String>> getAddressToNameMap() {
        return this.addressToNameMap;
    }
    
    Map<String, ObjectPair<Long, InetAddress[]>> getNameToAddressMap() {
        return this.nameToAddressMap;
    }
    
    AtomicReference<ObjectPair<Long, InetAddress>> getLocalHostAddressReference() {
        return this.localHostAddress;
    }
    
    AtomicReference<ObjectPair<Long, InetAddress>> getLoopbackAddressReference() {
        return this.loopbackAddress;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("CachingNameResolver(timeoutMillis=");
        buffer.append(this.timeoutMillis);
        buffer.append(')');
    }
}
