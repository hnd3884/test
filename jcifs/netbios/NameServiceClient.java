package jcifs.netbios;

import java.net.SocketTimeoutException;
import java.io.PrintStream;
import jcifs.util.Hexdump;
import java.io.IOException;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import jcifs.Config;
import java.util.HashMap;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import jcifs.util.LogStream;
import java.net.InetAddress;

class NameServiceClient implements Runnable
{
    static final int DEFAULT_SO_TIMEOUT = 5000;
    static final int DEFAULT_RCV_BUF_SIZE = 576;
    static final int DEFAULT_SND_BUF_SIZE = 576;
    static final int NAME_SERVICE_UDP_PORT = 137;
    static final int DEFAULT_RETRY_COUNT = 2;
    static final int DEFAULT_RETRY_TIMEOUT = 3000;
    static final int RESOLVER_LMHOSTS = 1;
    static final int RESOLVER_BCAST = 2;
    static final int RESOLVER_WINS = 3;
    private static final int SND_BUF_SIZE;
    private static final int RCV_BUF_SIZE;
    private static final int SO_TIMEOUT;
    private static final int RETRY_COUNT;
    private static final int RETRY_TIMEOUT;
    private static final int LPORT;
    private static final InetAddress LADDR;
    private static final String RO;
    private static LogStream log;
    private final Object LOCK;
    private int lport;
    private int closeTimeout;
    private byte[] snd_buf;
    private byte[] rcv_buf;
    private DatagramSocket socket;
    private DatagramPacket in;
    private DatagramPacket out;
    private HashMap responseTable;
    private Thread thread;
    private int nextNameTrnId;
    private int[] resolveOrder;
    InetAddress laddr;
    InetAddress baddr;
    
    NameServiceClient() {
        this(NameServiceClient.LPORT, NameServiceClient.LADDR);
    }
    
    NameServiceClient(final int lport, final InetAddress laddr) {
        this.LOCK = new Object();
        this.responseTable = new HashMap();
        this.nextNameTrnId = 0;
        this.lport = lport;
        this.laddr = laddr;
        try {
            this.baddr = Config.getInetAddress("jcifs.netbios.baddr", InetAddress.getByName("255.255.255.255"));
        }
        catch (final UnknownHostException ex) {}
        this.snd_buf = new byte[NameServiceClient.SND_BUF_SIZE];
        this.rcv_buf = new byte[NameServiceClient.RCV_BUF_SIZE];
        this.out = new DatagramPacket(this.snd_buf, NameServiceClient.SND_BUF_SIZE, this.baddr, 137);
        this.in = new DatagramPacket(this.rcv_buf, NameServiceClient.RCV_BUF_SIZE);
        if (NameServiceClient.RO == null || NameServiceClient.RO.length() == 0) {
            if (NbtAddress.getWINSAddress() == null) {
                (this.resolveOrder = new int[2])[0] = 1;
                this.resolveOrder[1] = 2;
            }
            else {
                (this.resolveOrder = new int[3])[0] = 1;
                this.resolveOrder[1] = 3;
                this.resolveOrder[2] = 2;
            }
        }
        else {
            final int[] tmp = new int[3];
            final StringTokenizer st = new StringTokenizer(NameServiceClient.RO, ",");
            int i = 0;
            while (st.hasMoreTokens()) {
                final String s = st.nextToken().trim();
                if (s.equalsIgnoreCase("LMHOSTS")) {
                    tmp[i++] = 1;
                }
                else if (s.equalsIgnoreCase("WINS")) {
                    if (NbtAddress.getWINSAddress() == null) {
                        final LogStream log = NameServiceClient.log;
                        if (LogStream.level <= 1) {
                            continue;
                        }
                        NameServiceClient.log.println("NetBIOS resolveOrder specifies WINS however the jcifs.netbios.wins property has not been set");
                    }
                    else {
                        tmp[i++] = 3;
                    }
                }
                else if (s.equalsIgnoreCase("BCAST")) {
                    tmp[i++] = 2;
                }
                else {
                    if (s.equalsIgnoreCase("DNS")) {
                        continue;
                    }
                    final LogStream log2 = NameServiceClient.log;
                    if (LogStream.level <= 1) {
                        continue;
                    }
                    NameServiceClient.log.println("unknown resolver method: " + s);
                }
            }
            System.arraycopy(tmp, 0, this.resolveOrder = new int[i], 0, i);
        }
    }
    
    int getNextNameTrnId() {
        if ((++this.nextNameTrnId & 0xFFFF) == 0x0) {
            this.nextNameTrnId = 1;
        }
        return this.nextNameTrnId;
    }
    
    void ensureOpen(final int timeout) throws IOException {
        this.closeTimeout = 0;
        if (NameServiceClient.SO_TIMEOUT != 0) {
            this.closeTimeout = Math.max(NameServiceClient.SO_TIMEOUT, timeout);
        }
        if (this.socket == null) {
            this.socket = new DatagramSocket(this.lport, this.laddr);
            (this.thread = new Thread(this, "JCIFS-NameServiceClient")).setDaemon(true);
            this.thread.start();
        }
    }
    
    void tryClose() {
        synchronized (this.LOCK) {
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
            this.thread = null;
            this.responseTable.clear();
        }
    }
    
    public void run() {
        try {
            while (this.thread == Thread.currentThread()) {
                this.in.setLength(NameServiceClient.RCV_BUF_SIZE);
                this.socket.setSoTimeout(this.closeTimeout);
                this.socket.receive(this.in);
                final LogStream log = NameServiceClient.log;
                if (LogStream.level > 3) {
                    NameServiceClient.log.println("NetBIOS: new data read from socket");
                }
                final int nameTrnId = NameServicePacket.readNameTrnId(this.rcv_buf, 0);
                final NameServicePacket response = this.responseTable.get(new Integer(nameTrnId));
                if (response != null) {
                    if (response.received) {
                        continue;
                    }
                    synchronized (response) {
                        response.readWireFormat(this.rcv_buf, 0);
                        response.received = true;
                        final LogStream log2 = NameServiceClient.log;
                        if (LogStream.level > 3) {
                            NameServiceClient.log.println(response);
                            Hexdump.hexdump(NameServiceClient.log, this.rcv_buf, 0, this.in.getLength());
                        }
                        response.notify();
                        continue;
                    }
                    break;
                }
            }
        }
        catch (final SocketTimeoutException ste) {}
        catch (final Exception ex) {
            final LogStream log3 = NameServiceClient.log;
            if (LogStream.level > 2) {
                ex.printStackTrace(NameServiceClient.log);
            }
        }
        finally {
            this.tryClose();
        }
    }
    
    void send(final NameServicePacket request, final NameServicePacket response, final int timeout) throws IOException {
        Integer nid = null;
        int max = NbtAddress.NBNS.length;
        if (max == 0) {
            max = 1;
        }
        synchronized (response) {
            while (max-- > 0) {
                try {
                    synchronized (this.LOCK) {
                        request.nameTrnId = this.getNextNameTrnId();
                        nid = new Integer(request.nameTrnId);
                        this.out.setAddress(request.addr);
                        this.out.setLength(request.writeWireFormat(this.snd_buf, 0));
                        response.received = false;
                        this.responseTable.put(nid, response);
                        this.ensureOpen(timeout + 1000);
                        this.socket.send(this.out);
                        final LogStream log = NameServiceClient.log;
                        if (LogStream.level > 3) {
                            NameServiceClient.log.println(request);
                            Hexdump.hexdump(NameServiceClient.log, this.snd_buf, 0, this.out.getLength());
                        }
                    }
                    response.wait(timeout);
                    if (response.received) {
                        return;
                    }
                }
                catch (final InterruptedException ie) {}
                finally {
                    this.responseTable.remove(nid);
                }
                if (!NbtAddress.isWINS(request.addr)) {
                    break;
                }
                if (request.addr == NbtAddress.getWINSAddress()) {
                    NbtAddress.switchWINS();
                }
                request.addr = NbtAddress.getWINSAddress();
            }
        }
    }
    
    NbtAddress[] getAllByName(final Name name, final InetAddress addr) throws UnknownHostException {
        final NameQueryRequest request = new NameQueryRequest(name);
        final NameQueryResponse response = new NameQueryResponse();
        request.addr = ((addr != null) ? addr : NbtAddress.getWINSAddress());
        request.isBroadcast = (request.addr == null);
        int n;
        if (request.isBroadcast) {
            request.addr = this.baddr;
            n = NameServiceClient.RETRY_COUNT;
        }
        else {
            request.isBroadcast = false;
            n = 1;
        }
        while (true) {
            try {
                this.send(request, response, NameServiceClient.RETRY_TIMEOUT);
            }
            catch (final IOException ioe) {
                final LogStream log = NameServiceClient.log;
                if (LogStream.level > 1) {
                    ioe.printStackTrace(NameServiceClient.log);
                }
                throw new UnknownHostException(name.name);
            }
            if (response.received && response.resultCode == 0) {
                return response.addrEntry;
            }
            if (--n <= 0 || !request.isBroadcast) {
                throw new UnknownHostException(name.name);
            }
        }
    }
    
    NbtAddress getByName(final Name name, final InetAddress addr) throws UnknownHostException {
        final NameQueryRequest request = new NameQueryRequest(name);
        final NameQueryResponse response = new NameQueryResponse();
        if (addr == null) {
            for (int i = 0; i < this.resolveOrder.length; ++i) {
                try {
                    switch (this.resolveOrder[i]) {
                        case 1: {
                            final NbtAddress ans = Lmhosts.getByName(name);
                            if (ans != null) {
                                ans.hostName.srcHashCode = 0;
                                return ans;
                            }
                            break;
                        }
                        case 2:
                        case 3: {
                            if (this.resolveOrder[i] == 3 && name.name != "\u0001\u0002__MSBROWSE__\u0002" && name.hexCode != 29) {
                                request.addr = NbtAddress.getWINSAddress();
                                request.isBroadcast = false;
                            }
                            else {
                                request.addr = this.baddr;
                                request.isBroadcast = true;
                            }
                            int n = NameServiceClient.RETRY_COUNT;
                            while (n-- > 0) {
                                try {
                                    this.send(request, response, NameServiceClient.RETRY_TIMEOUT);
                                }
                                catch (final IOException ioe) {
                                    final LogStream log = NameServiceClient.log;
                                    if (LogStream.level > 1) {
                                        ioe.printStackTrace(NameServiceClient.log);
                                    }
                                    throw new UnknownHostException(name.name);
                                }
                                if (response.received && response.resultCode == 0) {
                                    response.addrEntry[0].hostName.srcHashCode = request.addr.hashCode();
                                    return response.addrEntry[0];
                                }
                                if (this.resolveOrder[i] == 3) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                catch (final IOException ex) {}
            }
            throw new UnknownHostException(name.name);
        }
        request.addr = addr;
        request.isBroadcast = (addr.getAddress()[3] == -1);
        int n = NameServiceClient.RETRY_COUNT;
        while (true) {
            try {
                this.send(request, response, NameServiceClient.RETRY_TIMEOUT);
            }
            catch (final IOException ioe2) {
                final LogStream log2 = NameServiceClient.log;
                if (LogStream.level > 1) {
                    ioe2.printStackTrace(NameServiceClient.log);
                }
                throw new UnknownHostException(name.name);
            }
            if (response.received && response.resultCode == 0) {
                final int last = response.addrEntry.length - 1;
                response.addrEntry[last].hostName.srcHashCode = addr.hashCode();
                return response.addrEntry[last];
            }
            if (--n <= 0 || !request.isBroadcast) {
                throw new UnknownHostException(name.name);
            }
        }
    }
    
    NbtAddress[] getNodeStatus(final NbtAddress addr) throws UnknownHostException {
        final NodeStatusResponse response = new NodeStatusResponse(addr);
        final NodeStatusRequest request = new NodeStatusRequest(new Name("*\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000", 0, null));
        request.addr = addr.getInetAddress();
        int n = NameServiceClient.RETRY_COUNT;
        while (n-- > 0) {
            try {
                this.send(request, response, NameServiceClient.RETRY_TIMEOUT);
            }
            catch (final IOException ioe) {
                final LogStream log = NameServiceClient.log;
                if (LogStream.level > 1) {
                    ioe.printStackTrace(NameServiceClient.log);
                }
                throw new UnknownHostException(addr.toString());
            }
            if (response.received && response.resultCode == 0) {
                final int srcHashCode = request.addr.hashCode();
                for (int i = 0; i < response.addressArray.length; ++i) {
                    response.addressArray[i].hostName.srcHashCode = srcHashCode;
                }
                return response.addressArray;
            }
        }
        throw new UnknownHostException(addr.hostName.name);
    }
    
    static {
        SND_BUF_SIZE = Config.getInt("jcifs.netbios.snd_buf_size", 576);
        RCV_BUF_SIZE = Config.getInt("jcifs.netbios.rcv_buf_size", 576);
        SO_TIMEOUT = Config.getInt("jcifs.netbios.soTimeout", 5000);
        RETRY_COUNT = Config.getInt("jcifs.netbios.retryCount", 2);
        RETRY_TIMEOUT = Config.getInt("jcifs.netbios.retryTimeout", 3000);
        LPORT = Config.getInt("jcifs.netbios.lport", 0);
        LADDR = Config.getInetAddress("jcifs.netbios.laddr", null);
        RO = Config.getProperty("jcifs.resolveOrder");
        NameServiceClient.log = LogStream.getInstance();
    }
}
