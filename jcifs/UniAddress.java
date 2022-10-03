package jcifs;

import java.util.StringTokenizer;
import java.io.IOException;
import jcifs.netbios.Lmhosts;
import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import jcifs.util.LogStream;
import java.net.InetAddress;

public class UniAddress
{
    private static final int RESOLVER_WINS = 0;
    private static final int RESOLVER_BCAST = 1;
    private static final int RESOLVER_DNS = 2;
    private static final int RESOLVER_LMHOSTS = 3;
    private static int[] resolveOrder;
    private static InetAddress baddr;
    private static LogStream log;
    Object addr;
    String calledName;
    
    static NbtAddress lookupServerOrWorkgroup(final String name, final InetAddress svr) throws UnknownHostException {
        final Sem sem = new Sem(2);
        final int type = NbtAddress.isWINS(svr) ? 27 : 29;
        final QueryThread q1x = new QueryThread(sem, name, type, null, svr);
        final QueryThread q20 = new QueryThread(sem, name, 32, null, svr);
        q1x.setDaemon(true);
        q20.setDaemon(true);
        try {
            synchronized (sem) {
                q1x.start();
                q20.start();
                while (sem.count > 0 && q1x.ans == null && q20.ans == null) {
                    sem.wait();
                }
            }
        }
        catch (final InterruptedException ie) {
            throw new UnknownHostException(name);
        }
        if (q1x.ans != null) {
            return q1x.ans;
        }
        if (q20.ans != null) {
            return q20.ans;
        }
        throw q1x.uhe;
    }
    
    public static UniAddress getByName(final String hostname) throws UnknownHostException {
        return getByName(hostname, false);
    }
    
    static boolean isDotQuadIP(final String hostname) {
        if (Character.isDigit(hostname.charAt(0))) {
            int i;
            int dots = i = 0;
            final int len = hostname.length();
            final char[] data = hostname.toCharArray();
            while (i < len && Character.isDigit(data[i++])) {
                if (i == len && dots == 3) {
                    return true;
                }
                if (i >= len || data[i] != '.') {
                    continue;
                }
                ++dots;
                ++i;
            }
        }
        return false;
    }
    
    static boolean isAllDigits(final String hostname) {
        for (int i = 0; i < hostname.length(); ++i) {
            if (!Character.isDigit(hostname.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static UniAddress getByName(final String hostname, final boolean possibleNTDomainOrWorkgroup) throws UnknownHostException {
        if (hostname == null || hostname.length() == 0) {
            throw new UnknownHostException();
        }
        if (isDotQuadIP(hostname)) {
            return new UniAddress(NbtAddress.getByName(hostname));
        }
        for (int i = 0; i < UniAddress.resolveOrder.length; ++i) {
            try {
                switch (UniAddress.resolveOrder[i]) {
                    case 3: {
                        final Object addr;
                        if ((addr = Lmhosts.getByName(hostname)) == null) {
                            continue;
                        }
                        break;
                    }
                    case 0: {
                        if (hostname == "\u0001\u0002__MSBROWSE__\u0002" || hostname.length() > 15) {
                            continue;
                        }
                        if (possibleNTDomainOrWorkgroup) {
                            final Object addr = lookupServerOrWorkgroup(hostname, NbtAddress.getWINSAddress());
                            break;
                        }
                        final Object addr = NbtAddress.getByName(hostname, 32, null, NbtAddress.getWINSAddress());
                        break;
                    }
                    case 1: {
                        if (hostname.length() > 15) {
                            continue;
                        }
                        if (possibleNTDomainOrWorkgroup) {
                            final Object addr = lookupServerOrWorkgroup(hostname, UniAddress.baddr);
                            break;
                        }
                        final Object addr = NbtAddress.getByName(hostname, 32, null, UniAddress.baddr);
                        break;
                    }
                    case 2: {
                        if (isAllDigits(hostname)) {
                            throw new UnknownHostException(hostname);
                        }
                        final Object addr = InetAddress.getByName(hostname);
                        break;
                    }
                    default: {
                        throw new UnknownHostException(hostname);
                    }
                }
                final Object addr2;
                return new UniAddress(addr2);
            }
            catch (final IOException ex) {}
        }
        throw new UnknownHostException(hostname);
    }
    
    public UniAddress(final Object addr) {
        if (addr == null) {
            throw new IllegalArgumentException();
        }
        this.addr = addr;
    }
    
    public int hashCode() {
        return this.addr.hashCode();
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof UniAddress && this.addr.hashCode() == obj.hashCode();
    }
    
    public String firstCalledName() {
        if (this.addr instanceof NbtAddress) {
            return ((NbtAddress)this.addr).firstCalledName();
        }
        this.calledName = ((InetAddress)this.addr).getHostName();
        if (isDotQuadIP(this.calledName)) {
            this.calledName = "*SMBSERVER     ";
        }
        else {
            final int i = this.calledName.indexOf(46);
            if (i > 1 && i < 15) {
                this.calledName = this.calledName.substring(0, i).toUpperCase();
            }
            else if (this.calledName.length() > 15) {
                this.calledName = "*SMBSERVER     ";
            }
            else {
                this.calledName = this.calledName.toUpperCase();
            }
        }
        return this.calledName;
    }
    
    public String nextCalledName() {
        if (this.addr instanceof NbtAddress) {
            return ((NbtAddress)this.addr).nextCalledName();
        }
        if (this.calledName != "*SMBSERVER     ") {
            return this.calledName = "*SMBSERVER     ";
        }
        return null;
    }
    
    public Object getAddress() {
        return this.addr;
    }
    
    public String getHostName() {
        if (this.addr instanceof NbtAddress) {
            return ((NbtAddress)this.addr).getHostName();
        }
        return ((InetAddress)this.addr).getHostName();
    }
    
    public String getHostAddress() {
        if (this.addr instanceof NbtAddress) {
            return ((NbtAddress)this.addr).getHostAddress();
        }
        return ((InetAddress)this.addr).getHostAddress();
    }
    
    public String toString() {
        return this.addr.toString();
    }
    
    static {
        UniAddress.log = LogStream.getInstance();
        final String ro = Config.getProperty("jcifs.resolveOrder");
        final InetAddress nbns = NbtAddress.getWINSAddress();
        try {
            UniAddress.baddr = Config.getInetAddress("jcifs.netbios.baddr", InetAddress.getByName("255.255.255.255"));
        }
        catch (final UnknownHostException ex) {}
        if (ro == null || ro.length() == 0) {
            if (nbns == null) {
                (UniAddress.resolveOrder = new int[3])[0] = 3;
                UniAddress.resolveOrder[1] = 1;
                UniAddress.resolveOrder[2] = 2;
            }
            else {
                (UniAddress.resolveOrder = new int[4])[0] = 3;
                UniAddress.resolveOrder[1] = 0;
                UniAddress.resolveOrder[2] = 1;
                UniAddress.resolveOrder[3] = 2;
            }
        }
        else {
            final int[] tmp = new int[4];
            final StringTokenizer st = new StringTokenizer(ro, ",");
            int i = 0;
            while (st.hasMoreTokens()) {
                final String s = st.nextToken().trim();
                if (s.equalsIgnoreCase("LMHOSTS")) {
                    tmp[i++] = 3;
                }
                else if (s.equalsIgnoreCase("WINS")) {
                    if (nbns == null) {
                        final LogStream log = UniAddress.log;
                        if (LogStream.level <= 1) {
                            continue;
                        }
                        UniAddress.log.println("UniAddress resolveOrder specifies WINS however the jcifs.netbios.wins property has not been set");
                    }
                    else {
                        tmp[i++] = 0;
                    }
                }
                else if (s.equalsIgnoreCase("BCAST")) {
                    tmp[i++] = 1;
                }
                else if (s.equalsIgnoreCase("DNS")) {
                    tmp[i++] = 2;
                }
                else {
                    final LogStream log2 = UniAddress.log;
                    if (LogStream.level <= 1) {
                        continue;
                    }
                    UniAddress.log.println("unknown resolver method: " + s);
                }
            }
            System.arraycopy(tmp, 0, UniAddress.resolveOrder = new int[i], 0, i);
        }
    }
    
    static class Sem
    {
        int count;
        
        Sem(final int count) {
            this.count = count;
        }
    }
    
    static class QueryThread extends Thread
    {
        Sem sem;
        String host;
        String scope;
        int type;
        NbtAddress ans;
        InetAddress svr;
        UnknownHostException uhe;
        
        QueryThread(final Sem sem, final String host, final int type, final String scope, final InetAddress svr) {
            super("JCIFS-QueryThread: " + host);
            this.ans = null;
            this.sem = sem;
            this.host = host;
            this.type = type;
            this.scope = scope;
            this.svr = svr;
        }
        
        public void run() {
            try {
                this.ans = NbtAddress.getByName(this.host, this.type, this.scope, this.svr);
            }
            catch (final UnknownHostException uhe) {
                this.uhe = uhe;
            }
            catch (final Exception ex) {
                this.uhe = new UnknownHostException(ex.getMessage());
            }
            finally {
                synchronized (this.sem) {
                    final Sem sem = this.sem;
                    --sem.count;
                    this.sem.notify();
                }
            }
        }
    }
}
