package com.sun.jndi.dns;

import java.util.Random;
import sun.security.jca.JCAUtil;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import java.net.DatagramPacket;
import java.io.IOException;
import javax.naming.NameNotFoundException;
import javax.naming.CommunicationException;
import java.net.SocketException;
import java.net.DatagramSocket;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.HashMap;
import java.net.UnknownHostException;
import javax.naming.ConfigurationException;
import java.util.Map;
import java.net.InetAddress;
import java.security.SecureRandom;

public class DnsClient
{
    private static final int IDENT_OFFSET = 0;
    private static final int FLAGS_OFFSET = 2;
    private static final int NUMQ_OFFSET = 4;
    private static final int NUMANS_OFFSET = 6;
    private static final int NUMAUTH_OFFSET = 8;
    private static final int NUMADD_OFFSET = 10;
    private static final int DNS_HDR_SIZE = 12;
    private static final int NO_ERROR = 0;
    private static final int FORMAT_ERROR = 1;
    private static final int SERVER_FAILURE = 2;
    private static final int NAME_ERROR = 3;
    private static final int NOT_IMPL = 4;
    private static final int REFUSED = 5;
    private static final String[] rcodeDescription;
    private static final int DEFAULT_PORT = 53;
    private static final int TRANSACTION_ID_BOUND = 65536;
    private static final SecureRandom random;
    private InetAddress[] servers;
    private int[] serverPorts;
    private int timeout;
    private int retries;
    private final Object udpSocketLock;
    private static final DNSDatagramSocketFactory factory;
    private Map<Integer, ResourceRecord> reqs;
    private Map<Integer, byte[]> resps;
    private Object queuesLock;
    private static final boolean debug = false;
    
    public DnsClient(final String[] array, final int timeout, final int retries) throws NamingException {
        this.udpSocketLock = new Object();
        this.queuesLock = new Object();
        this.timeout = timeout;
        this.retries = retries;
        this.servers = new InetAddress[array.length];
        this.serverPorts = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            final int index = array[i].indexOf(58, array[i].indexOf(93) + 1);
            this.serverPorts[i] = ((index < 0) ? 53 : Integer.parseInt(array[i].substring(index + 1)));
            final String s = (index < 0) ? array[i] : array[i].substring(0, index);
            try {
                this.servers[i] = InetAddress.getByName(s);
            }
            catch (final UnknownHostException rootCause) {
                final ConfigurationException ex = new ConfigurationException("Unknown DNS server: " + s);
                ex.setRootCause(rootCause);
                throw ex;
            }
        }
        this.reqs = Collections.synchronizedMap(new HashMap<Integer, ResourceRecord>());
        this.resps = Collections.synchronizedMap(new HashMap<Integer, byte[]>());
    }
    
    DatagramSocket getDatagramSocket() throws NamingException {
        try {
            return DnsClient.factory.open();
        }
        catch (final SocketException rootCause) {
            final ConfigurationException ex = new ConfigurationException();
            ex.setRootCause(rootCause);
            throw ex;
        }
    }
    
    @Override
    protected void finalize() {
        this.close();
    }
    
    public void close() {
        synchronized (this.queuesLock) {
            this.reqs.clear();
            this.resps.clear();
        }
    }
    
    ResourceRecords query(final DnsName dnsName, final int n, final int n2, final boolean b, final boolean b2) throws NamingException {
        int nextInt;
        Packet queryPacket;
        do {
            nextInt = DnsClient.random.nextInt(65536);
            queryPacket = this.makeQueryPacket(dnsName, nextInt, n, n2, b);
        } while (this.reqs.putIfAbsent(nextInt, new ResourceRecord(queryPacket.getData(), queryPacket.length(), 12, true, false)) != null);
        Throwable rootCause = null;
        final boolean[] array = new boolean[this.servers.length];
        try {
            for (int i = 0; i < this.retries; ++i) {
                for (int j = 0; j < this.servers.length; ++j) {
                    if (!array[j]) {
                        try {
                            byte[] array2 = this.doUdpQuery(queryPacket, this.servers[j], this.serverPorts[j], i, nextInt);
                            if (array2 == null) {
                                if (this.resps.size() > 0) {
                                    array2 = this.lookupResponse(nextInt);
                                }
                                if (array2 == null) {
                                    continue;
                                }
                            }
                            Header header = new Header(array2, array2.length);
                            if (!b2 || header.authoritative) {
                                if (header.truncated) {
                                    for (int k = 0; k < this.servers.length; ++k) {
                                        final int n3 = (j + k) % this.servers.length;
                                        if (!array[n3]) {
                                            try {
                                                final Tcp tcp = new Tcp(this.servers[n3], this.serverPorts[n3]);
                                                byte[] doTcpQuery;
                                                try {
                                                    doTcpQuery = this.doTcpQuery(tcp, queryPacket);
                                                }
                                                finally {
                                                    tcp.close();
                                                }
                                                final Header header2 = new Header(doTcpQuery, doTcpQuery.length);
                                                if (header2.query) {
                                                    throw new CommunicationException("DNS error: expecting response");
                                                }
                                                this.checkResponseCode(header2);
                                                if (!b2 || header2.authoritative) {
                                                    header = header2;
                                                    array2 = doTcpQuery;
                                                    break;
                                                }
                                                array[n3] = true;
                                            }
                                            catch (final Exception ex) {}
                                        }
                                    }
                                }
                                return new ResourceRecords(array2, array2.length, header, false);
                            }
                            rootCause = new NameNotFoundException("DNS response not authoritative");
                            array[j] = true;
                        }
                        catch (final IOException ex2) {
                            if (rootCause == null) {
                                rootCause = ex2;
                            }
                            if (ex2.getClass().getName().equals("java.net.PortUnreachableException")) {
                                array[j] = true;
                            }
                        }
                        catch (final NameNotFoundException ex3) {
                            throw ex3;
                        }
                        catch (final CommunicationException ex4) {
                            if (rootCause == null) {
                                rootCause = ex4;
                            }
                        }
                        catch (final NamingException ex5) {
                            if (rootCause == null) {
                                rootCause = ex5;
                            }
                            array[j] = true;
                        }
                    }
                }
            }
        }
        finally {
            this.reqs.remove(nextInt);
        }
        if (rootCause instanceof NamingException) {
            throw (NamingException)rootCause;
        }
        final CommunicationException ex6 = new CommunicationException("DNS error");
        ex6.setRootCause(rootCause);
        throw ex6;
    }
    
    ResourceRecords queryZone(final DnsName dnsName, final int n, final boolean b) throws NamingException {
        final Packet queryPacket = this.makeQueryPacket(dnsName, DnsClient.random.nextInt(65536), n, 252, b);
        Throwable rootCause = null;
        for (int i = 0; i < this.servers.length; ++i) {
            try {
                final Tcp tcp = new Tcp(this.servers[i], this.serverPorts[i]);
                try {
                    final byte[] doTcpQuery = this.doTcpQuery(tcp, queryPacket);
                    final Header header = new Header(doTcpQuery, doTcpQuery.length);
                    this.checkResponseCode(header);
                    final ResourceRecords resourceRecords = new ResourceRecords(doTcpQuery, doTcpQuery.length, header, true);
                    if (resourceRecords.getFirstAnsType() != 6) {
                        throw new CommunicationException("DNS error: zone xfer doesn't begin with SOA");
                    }
                    if (resourceRecords.answer.size() == 1 || resourceRecords.getLastAnsType() != 6) {
                        do {
                            final byte[] continueTcpQuery = this.continueTcpQuery(tcp);
                            if (continueTcpQuery == null) {
                                throw new CommunicationException("DNS error: incomplete zone transfer");
                            }
                            final Header header2 = new Header(continueTcpQuery, continueTcpQuery.length);
                            this.checkResponseCode(header2);
                            resourceRecords.add(continueTcpQuery, continueTcpQuery.length, header2);
                        } while (resourceRecords.getLastAnsType() != 6);
                    }
                    resourceRecords.answer.removeElementAt(resourceRecords.answer.size() - 1);
                    return resourceRecords;
                }
                finally {
                    tcp.close();
                }
            }
            catch (final IOException ex) {
                rootCause = ex;
            }
            catch (final NameNotFoundException ex2) {
                throw ex2;
            }
            catch (final NamingException ex3) {
                rootCause = ex3;
            }
        }
        if (rootCause instanceof NamingException) {
            throw (NamingException)rootCause;
        }
        final CommunicationException ex4 = new CommunicationException("DNS error during zone transfer");
        ex4.setRootCause(rootCause);
        throw ex4;
    }
    
    private byte[] doUdpQuery(final Packet packet, final InetAddress inetAddress, final int n, final int n2, final int n3) throws IOException, NamingException {
        final int n4 = 50;
        synchronized (this.udpSocketLock) {
            try (final DatagramSocket datagramSocket = this.getDatagramSocket()) {
                final DatagramPacket datagramPacket = new DatagramPacket(packet.getData(), packet.length(), inetAddress, n);
                final DatagramPacket datagramPacket2 = new DatagramPacket(new byte[8000], 8000);
                datagramSocket.connect(inetAddress, n);
                final int n5 = this.timeout * (1 << n2);
                try {
                    datagramSocket.send(datagramPacket);
                    int i = n5;
                    do {
                        datagramSocket.setSoTimeout(i);
                        final long currentTimeMillis = System.currentTimeMillis();
                        datagramSocket.receive(datagramPacket2);
                        final long currentTimeMillis2 = System.currentTimeMillis();
                        final byte[] data = datagramPacket2.getData();
                        if (this.isMatchResponse(data, n3)) {
                            return data;
                        }
                        i = n5 - (int)(currentTimeMillis2 - currentTimeMillis);
                    } while (i > n4);
                }
                finally {
                    datagramSocket.disconnect();
                }
                return null;
            }
        }
    }
    
    private byte[] doTcpQuery(final Tcp tcp, final Packet packet) throws IOException {
        final int length = packet.length();
        tcp.out.write(length >> 8);
        tcp.out.write(length);
        tcp.out.write(packet.getData(), 0, length);
        tcp.out.flush();
        final byte[] continueTcpQuery = this.continueTcpQuery(tcp);
        if (continueTcpQuery == null) {
            throw new IOException("DNS error: no response");
        }
        return continueTcpQuery;
    }
    
    private byte[] continueTcpQuery(final Tcp tcp) throws IOException {
        final int read = tcp.in.read();
        if (read == -1) {
            return null;
        }
        final int read2 = tcp.in.read();
        if (read2 == -1) {
            throw new IOException("Corrupted DNS response: bad length");
        }
        int i = read << 8 | read2;
        final byte[] array = new byte[i];
        int read3;
        for (int n = 0; i > 0; i -= read3, n += read3) {
            read3 = tcp.in.read(array, n, i);
            if (read3 == -1) {
                throw new IOException("Corrupted DNS response: too little data");
            }
        }
        return array;
    }
    
    private Packet makeQueryPacket(final DnsName dnsName, final int n, final int n2, final int n3, final boolean b) {
        final short octets = dnsName.getOctets();
        final Packet packet = new Packet(12 + octets + 4);
        final int n4 = b ? 256 : 0;
        packet.putShort(n, 0);
        packet.putShort(n4, 2);
        packet.putShort(1, 4);
        packet.putShort(0, 6);
        packet.putInt(0, 8);
        this.makeQueryName(dnsName, packet, 12);
        packet.putShort(n3, 12 + octets);
        packet.putShort(n2, 12 + octets + 2);
        return packet;
    }
    
    private void makeQueryName(final DnsName dnsName, final Packet packet, int n) {
        for (int i = dnsName.size() - 1; i >= 0; --i) {
            final String value = dnsName.get(i);
            final int length = value.length();
            packet.putByte(length, n++);
            for (int j = 0; j < length; ++j) {
                packet.putByte(value.charAt(j), n++);
            }
        }
        if (!dnsName.hasRootLabel()) {
            packet.putByte(0, n);
        }
    }
    
    private byte[] lookupResponse(final Integer n) throws NamingException {
        final byte[] array;
        if ((array = this.resps.get(n)) != null) {
            this.checkResponseCode(new Header(array, array.length));
            synchronized (this.queuesLock) {
                this.resps.remove(n);
                this.reqs.remove(n);
            }
        }
        return array;
    }
    
    private boolean isMatchResponse(final byte[] array, final int n) throws NamingException {
        final Header header = new Header(array, array.length);
        if (header.query) {
            throw new CommunicationException("DNS error: expecting response");
        }
        if (!this.reqs.containsKey(n)) {
            return false;
        }
        if (header.xid == n) {
            this.checkResponseCode(header);
            if (!header.query && header.numQuestions == 1) {
                final ResourceRecord resourceRecord = new ResourceRecord(array, array.length, 12, true, false);
                final ResourceRecord resourceRecord2 = this.reqs.get(n);
                final int type = resourceRecord2.getType();
                final int rrclass = resourceRecord2.getRrclass();
                final DnsName name = resourceRecord2.getName();
                if ((type == 255 || type == resourceRecord.getType()) && (rrclass == 255 || rrclass == resourceRecord.getRrclass()) && name.equals(resourceRecord.getName())) {
                    synchronized (this.queuesLock) {
                        this.resps.remove(n);
                        this.reqs.remove(n);
                    }
                    return true;
                }
            }
            return false;
        }
        synchronized (this.queuesLock) {
            if (this.reqs.containsKey(header.xid)) {
                this.resps.put(header.xid, array);
            }
        }
        return false;
    }
    
    private void checkResponseCode(final Header header) throws NamingException {
        final int rcode = header.rcode;
        if (rcode == 0) {
            return;
        }
        final String string = ((rcode < DnsClient.rcodeDescription.length) ? DnsClient.rcodeDescription[rcode] : "DNS error") + " [response code " + rcode + "]";
        switch (rcode) {
            case 2: {
                throw new ServiceUnavailableException(string);
            }
            case 3: {
                throw new NameNotFoundException(string);
            }
            case 4:
            case 5: {
                throw new OperationNotSupportedException(string);
            }
            default: {
                throw new NamingException(string);
            }
        }
    }
    
    private static void dprint(final String s) {
    }
    
    static {
        rcodeDescription = new String[] { "No error", "DNS format error", "DNS server failure", "DNS name not found", "DNS operation not supported", "DNS service refused" };
        random = JCAUtil.getSecureRandom();
        factory = new DNSDatagramSocketFactory(DnsClient.random);
    }
}
