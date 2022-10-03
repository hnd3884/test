package jcifs.netbios;

import jcifs.Config;
import jcifs.util.Hexdump;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.net.InetAddress;

public final class NbtAddress
{
    static final String ANY_HOSTS_NAME = "*\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
    public static final String MASTER_BROWSER_NAME = "\u0001\u0002__MSBROWSE__\u0002";
    public static final String SMBSERVER_NAME = "*SMBSERVER     ";
    public static final int B_NODE = 0;
    public static final int P_NODE = 1;
    public static final int M_NODE = 2;
    public static final int H_NODE = 3;
    static final InetAddress[] NBNS;
    private static final NameServiceClient CLIENT;
    private static final int DEFAULT_CACHE_POLICY = 30;
    private static final int CACHE_POLICY;
    private static final int FOREVER = -1;
    private static int nbnsIndex;
    private static final HashMap ADDRESS_CACHE;
    private static final HashMap LOOKUP_TABLE;
    static final Name UNKNOWN_NAME;
    static final NbtAddress UNKNOWN_ADDRESS;
    static final byte[] UNKNOWN_MAC_ADDRESS;
    static NbtAddress localhost;
    Name hostName;
    int address;
    int nodeType;
    boolean groupName;
    boolean isBeingDeleted;
    boolean isInConflict;
    boolean isActive;
    boolean isPermanent;
    boolean isDataFromNodeStatus;
    byte[] macAddress;
    String calledName;
    
    static void cacheAddress(final Name hostName, final NbtAddress addr) {
        if (NbtAddress.CACHE_POLICY == 0) {
            return;
        }
        long expiration = -1L;
        if (NbtAddress.CACHE_POLICY != -1) {
            expiration = System.currentTimeMillis() + NbtAddress.CACHE_POLICY * 1000;
        }
        cacheAddress(hostName, addr, expiration);
    }
    
    static void cacheAddress(final Name hostName, final NbtAddress addr, final long expiration) {
        if (NbtAddress.CACHE_POLICY == 0) {
            return;
        }
        synchronized (NbtAddress.ADDRESS_CACHE) {
            CacheEntry entry = NbtAddress.ADDRESS_CACHE.get(hostName);
            if (entry == null) {
                entry = new CacheEntry(hostName, addr, expiration);
                NbtAddress.ADDRESS_CACHE.put(hostName, entry);
            }
            else {
                entry.address = addr;
                entry.expiration = expiration;
            }
        }
    }
    
    static void cacheAddressArray(final NbtAddress[] addrs) {
        if (NbtAddress.CACHE_POLICY == 0) {
            return;
        }
        long expiration = -1L;
        if (NbtAddress.CACHE_POLICY != -1) {
            expiration = System.currentTimeMillis() + NbtAddress.CACHE_POLICY * 1000;
        }
        synchronized (NbtAddress.ADDRESS_CACHE) {
            for (int i = 0; i < addrs.length; ++i) {
                CacheEntry entry = NbtAddress.ADDRESS_CACHE.get(addrs[i].hostName);
                if (entry == null) {
                    entry = new CacheEntry(addrs[i].hostName, addrs[i], expiration);
                    NbtAddress.ADDRESS_CACHE.put(addrs[i].hostName, entry);
                }
                else {
                    entry.address = addrs[i];
                    entry.expiration = expiration;
                }
            }
        }
    }
    
    static NbtAddress getCachedAddress(final Name hostName) {
        if (NbtAddress.CACHE_POLICY == 0) {
            return null;
        }
        synchronized (NbtAddress.ADDRESS_CACHE) {
            CacheEntry entry = NbtAddress.ADDRESS_CACHE.get(hostName);
            if (entry != null && entry.expiration < System.currentTimeMillis() && entry.expiration >= 0L) {
                entry = null;
            }
            return (entry != null) ? entry.address : null;
        }
    }
    
    static NbtAddress doNameQuery(final Name name, InetAddress svr) throws UnknownHostException {
        if (name.hexCode == 29 && svr == null) {
            svr = NbtAddress.CLIENT.baddr;
        }
        name.srcHashCode = ((svr != null) ? svr.hashCode() : 0);
        NbtAddress addr = getCachedAddress(name);
        if (addr == null && (addr = (NbtAddress)checkLookupTable(name)) == null) {
            try {
                addr = NbtAddress.CLIENT.getByName(name, svr);
            }
            catch (final UnknownHostException uhe) {
                addr = NbtAddress.UNKNOWN_ADDRESS;
            }
            finally {
                cacheAddress(name, addr);
                updateLookupTable(name);
            }
        }
        if (addr == NbtAddress.UNKNOWN_ADDRESS) {
            throw new UnknownHostException(name.toString());
        }
        return addr;
    }
    
    private static Object checkLookupTable(final Name name) {
        synchronized (NbtAddress.LOOKUP_TABLE) {
            if (!NbtAddress.LOOKUP_TABLE.containsKey(name)) {
                NbtAddress.LOOKUP_TABLE.put(name, name);
                return null;
            }
            while (NbtAddress.LOOKUP_TABLE.containsKey(name)) {
                try {
                    NbtAddress.LOOKUP_TABLE.wait();
                }
                catch (final InterruptedException e) {}
            }
        }
        final Object obj = getCachedAddress(name);
        if (obj == null) {
            synchronized (NbtAddress.LOOKUP_TABLE) {
                NbtAddress.LOOKUP_TABLE.put(name, name);
            }
        }
        return obj;
    }
    
    private static void updateLookupTable(final Name name) {
        synchronized (NbtAddress.LOOKUP_TABLE) {
            NbtAddress.LOOKUP_TABLE.remove(name);
            NbtAddress.LOOKUP_TABLE.notifyAll();
        }
    }
    
    public static NbtAddress getLocalHost() throws UnknownHostException {
        return NbtAddress.localhost;
    }
    
    public static Name getLocalName() {
        return NbtAddress.localhost.hostName;
    }
    
    public static NbtAddress getByName(final String host) throws UnknownHostException {
        return getByName(host, 0, null);
    }
    
    public static NbtAddress getByName(final String host, final int type, final String scope) throws UnknownHostException {
        return getByName(host, type, scope, null);
    }
    
    public static NbtAddress getByName(final String host, final int type, final String scope, final InetAddress svr) throws UnknownHostException {
        if (host == null || host.length() == 0) {
            return getLocalHost();
        }
        if (!Character.isDigit(host.charAt(0))) {
            return doNameQuery(new Name(host, type, scope), svr);
        }
        int IP = 0;
        int hitDots = 0;
        final char[] data = host.toCharArray();
        for (int i = 0; i < data.length; ++i) {
            char c = data[i];
            if (c < '0' || c > '9') {
                return doNameQuery(new Name(host, type, scope), svr);
            }
            int b = 0;
            while (c != '.') {
                if (c < '0' || c > '9') {
                    return doNameQuery(new Name(host, type, scope), svr);
                }
                b = b * 10 + c - 48;
                if (++i >= data.length) {
                    break;
                }
                c = data[i];
            }
            if (b > 255) {
                return doNameQuery(new Name(host, type, scope), svr);
            }
            IP = (IP << 8) + b;
            ++hitDots;
        }
        if (hitDots != 4 || host.endsWith(".")) {
            return doNameQuery(new Name(host, type, scope), svr);
        }
        return new NbtAddress(NbtAddress.UNKNOWN_NAME, IP, false, 0);
    }
    
    public static NbtAddress[] getAllByName(final String host, final int type, final String scope, final InetAddress svr) throws UnknownHostException {
        return NbtAddress.CLIENT.getAllByName(new Name(host, type, scope), svr);
    }
    
    public static NbtAddress[] getAllByAddress(final String host) throws UnknownHostException {
        return getAllByAddress(getByName(host, 0, null));
    }
    
    public static NbtAddress[] getAllByAddress(final String host, final int type, final String scope) throws UnknownHostException {
        return getAllByAddress(getByName(host, type, scope));
    }
    
    public static NbtAddress[] getAllByAddress(final NbtAddress addr) throws UnknownHostException {
        try {
            final NbtAddress[] addrs = NbtAddress.CLIENT.getNodeStatus(addr);
            cacheAddressArray(addrs);
            return addrs;
        }
        catch (final UnknownHostException uhe) {
            throw new UnknownHostException("no name with type 0x" + Hexdump.toHexString(addr.hostName.hexCode, 2) + ((addr.hostName.scope == null || addr.hostName.scope.length() == 0) ? " with no scope" : (" with scope " + addr.hostName.scope)) + " for host " + addr.getHostAddress());
        }
    }
    
    public static InetAddress getWINSAddress() {
        return (NbtAddress.NBNS.length == 0) ? null : NbtAddress.NBNS[NbtAddress.nbnsIndex];
    }
    
    public static boolean isWINS(final InetAddress svr) {
        for (int i = 0; svr != null && i < NbtAddress.NBNS.length; ++i) {
            if (svr.hashCode() == NbtAddress.NBNS[i].hashCode()) {
                return true;
            }
        }
        return false;
    }
    
    static InetAddress switchWINS() {
        NbtAddress.nbnsIndex = ((NbtAddress.nbnsIndex + 1 < NbtAddress.NBNS.length) ? (NbtAddress.nbnsIndex + 1) : 0);
        return (NbtAddress.NBNS.length == 0) ? null : NbtAddress.NBNS[NbtAddress.nbnsIndex];
    }
    
    NbtAddress(final Name hostName, final int address, final boolean groupName, final int nodeType) {
        this.hostName = hostName;
        this.address = address;
        this.groupName = groupName;
        this.nodeType = nodeType;
    }
    
    NbtAddress(final Name hostName, final int address, final boolean groupName, final int nodeType, final boolean isBeingDeleted, final boolean isInConflict, final boolean isActive, final boolean isPermanent, final byte[] macAddress) {
        this.hostName = hostName;
        this.address = address;
        this.groupName = groupName;
        this.nodeType = nodeType;
        this.isBeingDeleted = isBeingDeleted;
        this.isInConflict = isInConflict;
        this.isActive = isActive;
        this.isPermanent = isPermanent;
        this.macAddress = macAddress;
        this.isDataFromNodeStatus = true;
    }
    
    public String firstCalledName() {
        this.calledName = this.hostName.name;
        if (Character.isDigit(this.calledName.charAt(0))) {
            int i;
            int dots = i = 0;
            final int len = this.calledName.length();
            final char[] data = this.calledName.toCharArray();
            while (i < len && Character.isDigit(data[i++])) {
                if (i == len && dots == 3) {
                    this.calledName = "*SMBSERVER     ";
                    break;
                }
                if (i >= len || data[i] != '.') {
                    continue;
                }
                ++dots;
                ++i;
            }
        }
        else {
            switch (this.hostName.hexCode) {
                case 27:
                case 28:
                case 29: {
                    this.calledName = "*SMBSERVER     ";
                    break;
                }
            }
        }
        return this.calledName;
    }
    
    public String nextCalledName() {
        if (this.calledName == this.hostName.name) {
            this.calledName = "*SMBSERVER     ";
        }
        else if (this.calledName == "*SMBSERVER     ") {
            try {
                final NbtAddress[] addrs = NbtAddress.CLIENT.getNodeStatus(this);
                if (this.hostName.hexCode == 29) {
                    for (int i = 0; i < addrs.length; ++i) {
                        if (addrs[i].hostName.hexCode == 32) {
                            return addrs[i].hostName.name;
                        }
                    }
                    return null;
                }
                if (this.isDataFromNodeStatus) {
                    this.calledName = null;
                    return this.hostName.name;
                }
            }
            catch (final UnknownHostException uhe) {
                this.calledName = null;
            }
        }
        else {
            this.calledName = null;
        }
        return this.calledName;
    }
    
    void checkData() throws UnknownHostException {
        if (this.hostName == NbtAddress.UNKNOWN_NAME) {
            getAllByAddress(this);
        }
    }
    
    void checkNodeStatusData() throws UnknownHostException {
        if (!this.isDataFromNodeStatus) {
            getAllByAddress(this);
        }
    }
    
    public boolean isGroupAddress() throws UnknownHostException {
        this.checkData();
        return this.groupName;
    }
    
    public int getNodeType() throws UnknownHostException {
        this.checkData();
        return this.nodeType;
    }
    
    public boolean isBeingDeleted() throws UnknownHostException {
        this.checkNodeStatusData();
        return this.isBeingDeleted;
    }
    
    public boolean isInConflict() throws UnknownHostException {
        this.checkNodeStatusData();
        return this.isInConflict;
    }
    
    public boolean isActive() throws UnknownHostException {
        this.checkNodeStatusData();
        return this.isActive;
    }
    
    public boolean isPermanent() throws UnknownHostException {
        this.checkNodeStatusData();
        return this.isPermanent;
    }
    
    public byte[] getMacAddress() throws UnknownHostException {
        this.checkNodeStatusData();
        return this.macAddress;
    }
    
    public String getHostName() {
        try {
            this.checkData();
        }
        catch (final UnknownHostException uhe) {
            return this.getHostAddress();
        }
        return this.hostName.name;
    }
    
    public byte[] getAddress() {
        final byte[] addr = { (byte)(this.address >>> 24 & 0xFF), (byte)(this.address >>> 16 & 0xFF), (byte)(this.address >>> 8 & 0xFF), (byte)(this.address & 0xFF) };
        return addr;
    }
    
    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(this.getHostAddress());
    }
    
    public String getHostAddress() {
        return (this.address >>> 24 & 0xFF) + "." + (this.address >>> 16 & 0xFF) + "." + (this.address >>> 8 & 0xFF) + "." + (this.address >>> 0 & 0xFF);
    }
    
    public int getNameType() {
        return this.hostName.hexCode;
    }
    
    public int hashCode() {
        return this.address;
    }
    
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof NbtAddress && ((NbtAddress)obj).address == this.address;
    }
    
    public String toString() {
        return this.hostName.toString() + "/" + this.getHostAddress();
    }
    
    static {
        NBNS = Config.getInetAddressArray("jcifs.netbios.wins", ",", new InetAddress[0]);
        CLIENT = new NameServiceClient();
        CACHE_POLICY = Config.getInt("jcifs.netbios.cachePolicy", 30);
        NbtAddress.nbnsIndex = 0;
        ADDRESS_CACHE = new HashMap();
        LOOKUP_TABLE = new HashMap();
        UNKNOWN_NAME = new Name("0.0.0.0", 0, null);
        UNKNOWN_ADDRESS = new NbtAddress(NbtAddress.UNKNOWN_NAME, 0, false, 0);
        UNKNOWN_MAC_ADDRESS = new byte[] { 0, 0, 0, 0, 0, 0 };
        NbtAddress.ADDRESS_CACHE.put(NbtAddress.UNKNOWN_NAME, new CacheEntry(NbtAddress.UNKNOWN_NAME, NbtAddress.UNKNOWN_ADDRESS, -1L));
        InetAddress localInetAddress = NbtAddress.CLIENT.laddr;
        if (localInetAddress == null) {
            try {
                localInetAddress = InetAddress.getLocalHost();
            }
            catch (final UnknownHostException ex) {}
        }
        String localHostname = Config.getProperty("jcifs.netbios.hostname", null);
        if (localHostname == null || localHostname.length() == 0) {
            final byte[] addr = localInetAddress.getAddress();
            localHostname = "JCIFS" + (addr[2] & 0xFF) + "_" + (addr[3] & 0xFF) + "_" + Hexdump.toHexString((int)(Math.random() * 255.0), 2);
        }
        final Name localName = new Name(localHostname, 0, Config.getProperty("jcifs.netbios.scope", null));
        cacheAddress(localName, NbtAddress.localhost = new NbtAddress(localName, localInetAddress.hashCode(), false, 0, false, false, true, false, NbtAddress.UNKNOWN_MAC_ADDRESS), -1L);
    }
    
    static final class CacheEntry
    {
        Name hostName;
        NbtAddress address;
        long expiration;
        
        CacheEntry(final Name hostName, final NbtAddress address, final long expiration) {
            this.hostName = hostName;
            this.address = address;
            this.expiration = expiration;
        }
    }
}
