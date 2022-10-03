package com.theorem.radius3;

import java.util.Iterator;
import java.util.ArrayList;
import com.theorem.radius3.module.RADIUSModuleException;
import java.io.UnsupportedEncodingException;
import com.theorem.radius3.radutil.ByteIterator;
import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import com.theorem.radius3.radutil.HMAC_MD5;
import java.net.DatagramPacket;
import com.theorem.radius3.dictionary.RADIUSDictionary;
import java.security.MessageDigest;
import com.theorem.radius3.radutil.MD5Digest;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.net.SocketException;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.module.ClientModule;
import com.theorem.radius3.radutil.BitArray;
import java.util.HashMap;
import java.io.BufferedWriter;
import com.theorem.radius3.radutil.RadRand;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.Serializable;

public final class RADIUSClient implements Serializable
{
    public static final String Version = "3.43p";
    public static final int AUTH_PORT = 1812;
    public static final int ALT_AUTH_PORT = 1645;
    public static final int ACCT_PORT = 1813;
    public static final int ALT_ACCT_PORT = 1646;
    public static final int DMCOA_PORT = 3799;
    public static final int Access_Request = 1;
    public static final int Access_Accept = 2;
    public static final int Access_Reject = 3;
    public static final int Access_Challenge = 11;
    public static final int Disconnect_Request = 40;
    public static final int Disconnect_ACK = 41;
    public static final int Disconnect_NAK = 42;
    public static final int CoA_Request = 43;
    public static final int CoA_ACK = 44;
    public static final int CoA_NAK = 45;
    public static final int Access_BadPacket = 0;
    public static final int Accounting_Request = 4;
    public static final int Accounting_Response = 5;
    public static final int MAX_PACKET_SIZE = 4096;
    public static final int SOCKET_TIMEOUT = 10000;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_DUPLICATE = 1;
    public static final int ERROR_MISSING_USER_NAME = 2;
    public static final int ERROR_MISMATCHED_ID = 3;
    public static final int ERROR_CORRUPT = 4;
    public static final int ERROR_AUTHENTICATOR = 5;
    public static final int ERROR_MESSAGE_AUTHENTICATOR = 6;
    public static final int ERROR_WRONG_HOST = 7;
    public static final int ERROR_PACKET_SIZE = 8;
    public static final int ERROR_DES = 9;
    private String a;
    private int b;
    private InetAddress c;
    private DatagramSocket d;
    private byte[] e;
    private int f;
    private byte[] g;
    private int h;
    private byte[] i;
    private int j;
    private AttributeList k;
    private byte[] l;
    private int m;
    private int n;
    private int o;
    private boolean p;
    private InetAddress q;
    private static final byte[] r;
    private transient RadRand s;
    private boolean t;
    private BufferedWriter u;
    private int v;
    private HashMap w;
    private long x;
    private boolean y;
    private int z;
    private BitArray aa;
    private byte ab;
    private byte[] ac;
    private byte[] ad;
    public transient ClientModule module;
    
    public RADIUSClient(final String s, final int n, final String s2) throws SocketException, UnknownHostException {
        this.a = "";
        this.m = 4096;
        this.p = false;
        this.t = false;
        this.v = 0;
        this.a(InetAddress.getByName(s), n, Util.toUTF8(s2), 10000);
        this.n = 10000;
    }
    
    public RADIUSClient(final InetAddress inetAddress, final int n, final String s) throws SocketException {
        this.a = "";
        this.m = 4096;
        this.p = false;
        this.t = false;
        this.v = 0;
        this.a(inetAddress, n, Util.toUTF8(s), 10000);
        this.n = 10000;
    }
    
    public RADIUSClient(final String s, final int n, final String s2, final int n2) throws SocketException, UnknownHostException {
        this.a = "";
        this.m = 4096;
        this.p = false;
        this.t = false;
        this.v = 0;
        this.a(InetAddress.getByName(s), n, Util.toUTF8(s2), n2);
    }
    
    public RADIUSClient(final InetAddress inetAddress, final int n, final String s, final int n2) throws SocketException {
        this.a = "";
        this.m = 4096;
        this.p = false;
        this.t = false;
        this.v = 0;
        this.a(inetAddress, n, Util.toUTF8(s), n2);
    }
    
    public RADIUSClient(final InetAddress inetAddress, final int n, final byte[] array, final int n2) throws SocketException {
        this.a = "";
        this.m = 4096;
        this.p = false;
        this.t = false;
        this.v = 0;
        this.a(inetAddress, n, array, n2);
    }
    
    private final void a(final InetAddress c, final int n, final byte[] l, final int n2) throws SocketException {
        new A();
        this.s = new RadRand();
        this.c = c;
        this.b = ((n == 0) ? 1812 : n);
        this.l = l;
        (this.d = new DatagramSocket()).setSoTimeout(n2);
        this.n = n2;
        this.h = this.g();
        this.g = this.f();
        this.w = new HashMap();
        this.x = System.currentTimeMillis() + 30000L;
        this.z = 0;
    }
    
    public final void reset() throws SocketException {
        this.h = this.g();
        this.g = this.f();
        if (this.p) {
            this.d();
            return;
        }
        if (this.o != 0) {
            ++this.o;
            this.d();
        }
        else {
            final DatagramSocket d = this.d;
            (this.d = new DatagramSocket()).setSoTimeout(this.n);
            if (d != null) {
                d.close();
            }
        }
    }
    
    public final void bind(final String s) throws SocketException, UnknownHostException {
        this.q = InetAddress.getByName(s);
        this.d();
    }
    
    public final void bind(final InetAddress q) throws SocketException {
        this.q = q;
        this.d();
    }
    
    public final void bind(final InetAddress q, final int o) throws SocketException {
        this.q = q;
        this.o = o;
        this.p = true;
        this.d();
    }
    
    private final void d() throws SocketException {
        if (this.p) {
            if (this.d == null || this.d.getLocalPort() != 3799) {
                if (this.d != null) {
                    this.d.close();
                }
                (this.d = new DatagramSocket(this.o, this.q)).setSoTimeout(this.n);
            }
            return;
        }
        int n = 0;
        if (this.o == 0) {
            this.o = 2048;
        }
        if (this.d != null) {
            this.d.close();
        }
        while (this.o < 65536) {
            try {
                this.d = new DatagramSocket(this.o, this.q);
            }
            catch (final SocketException ex) {
                if (this.o >= 65535) {
                    if (++n >= 2) {
                        throw new SocketException("Can't find a suitable port to open.");
                    }
                    this.o = 2048;
                }
                ++this.o;
                continue;
            }
            break;
        }
        this.d.setSoTimeout(this.n);
    }
    
    public final void setDebug(final boolean t, final String s) throws IOException {
        this.t = t;
        if (s == null) {
            this.u = new BufferedWriter(new OutputStreamWriter(System.out, "UTF8"));
        }
        else {
            this.u = new BufferedWriter(new FileWriter(s));
        }
        new A();
        new Microsoft();
        new Cisco();
        new Ascend();
    }
    
    public final boolean setDebug(final boolean b) {
        try {
            this.setDebug(b, null);
        }
        catch (final IOException ex) {
            System.err.println("Can't write to stdout");
            return false;
        }
        return true;
    }
    
    public final boolean getDebugIndicator() {
        return this.t;
    }
    
    public final void logToDebug(final String s) {
        if (this.u != null) {
            try {
                this.u.write(s, 0, s.length());
                this.u.flush();
            }
            catch (final IOException ex) {
                System.out.println(s);
            }
        }
        else {
            System.out.println(s);
        }
    }
    
    public final int authenticate(final String s, final AttributeList list) throws ClientSendException, ClientReceiveException {
        final byte[] array = (s == null) ? new byte[0] : Util.toUTF8(s);
        return this.authenticate(s, list);
    }
    
    public final int authenticate(final String s, final String s2, final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        if (s == null) {
            this.v = 2;
            return 0;
        }
        this.k.addAttribute(new Attribute(1, Util.toUTF8(s)));
        this.k.addAttribute(new Attribute(2, RADIUSEncrypt.encrypt(Util.toUTF8(s2), this.l, this.g)));
        return this.authenticate(this.k);
    }
    
    public final int authenticate(final byte[] array, final AttributeList list) throws ClientSendException, ClientReceiveException {
        (this.k = new AttributeList(list)).deleteAll(3);
        this.k.deleteAll(60);
        this.createCHAP(array, this.k);
        return this.authenticate(this.k);
    }
    
    public final int authenticate(final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        if (this.k.exists(80)) {
            this.k.setAttribute(80, new byte[16]);
        }
        this.a(1, 1);
        return this.e();
    }
    
    public final int authenticate(final int n, final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        if (this.k.exists(80)) {
            this.k.setAttribute(80, new byte[16]);
        }
        this.a(n, 1);
        return this.e();
    }
    
    public final int authenticate(final byte[] array) throws ClientSendException, ClientReceiveException {
        return this.sendRawPacket(array);
    }
    
    public final int sendRawPacket(final byte[] array) throws ClientSendException, ClientReceiveException {
        this.a(array);
        return this.e();
    }
    
    public final int accounting(final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        this.g = RADIUSClient.r;
        this.a(4, 2);
        return this.e();
    }
    
    public final int accounting(final int n, final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        this.g = RADIUSClient.r;
        this.a(n, 2);
        return this.e();
    }
    
    public final int dmcoa(final int n, final AttributeList list) throws ClientSendException, ClientReceiveException {
        return this.accounting(n, list);
    }
    
    public final int extended(final int n, final AttributeList list) throws ClientSendException, ClientReceiveException {
        this.k = new AttributeList(list);
        if (this.k.exists(80)) {
            this.k.setAttribute(80, new byte[16]);
        }
        this.a(n, 1);
        return this.e();
    }
    
    public final int retry() throws ClientSendException, ClientReceiveException {
        if (this.i == null) {
            throw new ClientSendException("There is no packet to retry.");
        }
        this.a(this.i);
        return this.e();
    }
    
    public final void setMaximumPacketSize(final int m) {
        this.m = m;
    }
    
    public final int getMaximumPacketSize() {
        return this.m;
    }
    
    public final AttributeList getAttributes() {
        if (this.k == null) {
            return this.getRequestAttributes();
        }
        return this.k;
    }
    
    public final AttributeList getRequestAttributes() {
        final AttributeList list = new AttributeList();
        try {
            if (this.i != null) {
                list.loadRadiusAttributes(this.i, 20, this.i.length - 20, this.y);
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {}
        return list;
    }
    
    public final void close() {
        this.d.close();
        this.d = null;
    }
    
    protected final byte[] a() {
        final byte[] array = new byte[16];
        System.arraycopy(this.g, 0, array, 0, 16);
        return array;
    }
    
    public final byte[] getPassword() {
        if (this.k == null) {
            final byte[] array = new byte[16];
            System.arraycopy(this.g, 0, array, 0, 16);
            return array;
        }
        byte[] array2 = this.k.getBinaryAttribute(2);
        if (array2 == null) {
            array2 = this.k.getBinaryAttribute(3);
            if (array2 == null) {
                array2 = new byte[16];
                System.arraycopy(this.g, 0, array2, 0, 16);
            }
        }
        return array2;
    }
    
    public final boolean setEncoding(final String s) {
        return true;
    }
    
    public final void createCHAP(final byte[] array, final AttributeList list) {
        final MessageDigest value = MD5Digest.get();
        final byte[] challenge16 = this.createChallenge16();
        list.addAttribute(60, challenge16);
        final byte[] array2 = new byte[17];
        array2[0] = this.s.nextByte();
        value.reset();
        value.update(array2, 0, 1);
        value.update(array);
        value.update(challenge16);
        System.arraycopy(value.digest(), 0, array2, 1, 16);
        list.addAttribute(3, array2);
    }
    
    public final void createMSCHAP(final byte[] array, final AttributeList list) throws RADIUSException {
        MSChap msChap;
        try {
            msChap = new MSChap();
        }
        catch (final RADIUSException ex) {
            this.v = 9;
            this.a = "DES encoding has a problem - " + ex.getMessage();
            throw ex;
        }
        final byte[] challenge8 = this.createChallenge8();
        final VendorSpecific vendorSpecific = new VendorSpecific(311);
        vendorSpecific.addAttribute(11, challenge8);
        list.addAttribute(vendorSpecific.getAttribute());
        final VendorSpecific vendorSpecific2 = new VendorSpecific(311);
        final byte[] array2 = new byte[50];
        array2[0] = (this.ab = msChap.getIdent(this.ab));
        array2[1] = 1;
        final byte[] lmChallengeResponse = msChap.LmChallengeResponse(challenge8, array);
        System.arraycopy(lmChallengeResponse, 0, array2, 2, lmChallengeResponse.length);
        final byte[] ntChallengeResponse = msChap.NtChallengeResponse(challenge8, array, false);
        System.arraycopy(ntChallengeResponse, 0, array2, 26, ntChallengeResponse.length);
        vendorSpecific2.addAttribute(1, array2);
        list.addAttribute(vendorSpecific2.getAttribute());
    }
    
    public final MPPE getMSCHAPMPPE(final boolean b) {
        final MPPE mppe = new MPPE();
        mppe.a(this.getAttributes(), true, this.getSecret(), this.a());
        return mppe;
    }
    
    public final void createMSCHAP2(final byte[] array, final byte[] array2, final AttributeList list) throws RADIUSException {
        final MSChapV2 msChapV2 = new MSChapV2();
        this.ac = this.createChallenge16();
        this.ad = this.createChallenge16();
        final VendorSpecific vendorSpecific = new VendorSpecific(311);
        vendorSpecific.addAttribute(11, this.ad);
        list.addAttribute(vendorSpecific.getAttribute());
        final VendorSpecific vendorSpecific2 = new VendorSpecific(311);
        final byte[] array3 = new byte[50];
        array3[0] = (this.ab = msChapV2.getIdent(this.ab));
        System.arraycopy(this.ac, 0, array3, 2, this.ac.length);
        final byte[] a = msChapV2.a(this.ad, this.ac, array, array2, false);
        System.arraycopy(a, 0, array3, 26, a.length);
        vendorSpecific2.addAttribute(25, array3);
        list.addAttribute(vendorSpecific2.getAttribute());
    }
    
    public final boolean cmpMSCHAP2(final byte[] array, final byte[] array2, final AttributeList list) throws RADIUSException {
        final AttributeList attributes = this.getAttributes();
        final Attribute[] vendorSpecific = attributes.getVendorSpecific(311, 2);
        if (vendorSpecific.length > 0) {
            final byte[] attributeData = vendorSpecific[0].getAttributeData();
            if (attributeData.length < 2) {
                if (this.t) {
                    this.a("MS-CHAP-Error attribute value is too small (" + attributeData.length + ")", this.h);
                }
                return false;
            }
            if (!this.a(attributeData[0])) {
                return false;
            }
            if (this.t) {
                this.a("MSCHAP2 MS-CHAP-Error found " + new String(attributeData, 1, attributeData.length - 1), this.h);
            }
            return false;
        }
        else {
            final Attribute[] vendorSpecific2 = attributes.getVendorSpecific(311, 26);
            if (vendorSpecific2.length == 0) {
                if (this.t) {
                    this.a("MSCHAPV2: missing MS-CHAP2-Success attribute from response", this.h);
                }
                return false;
            }
            final byte[] attributeData2 = vendorSpecific2[0].getAttributeData();
            if (attributeData2.length != 43) {
                if (this.t) {
                    this.a("MS-CHAP2-Success attribute is not the expected length of 43, but " + attributeData2.length, this.h);
                }
                return false;
            }
            if (!this.a(attributeData2[0])) {
                return false;
            }
            MSChapV2 msChapV2;
            try {
                msChapV2 = new MSChapV2();
            }
            catch (final RADIUSException ex) {
                this.v = 9;
                this.a = "DES encoding has a problem - " + ex.getMessage();
                throw ex;
            }
            final byte[] a = msChapV2.a(this.ad, this.ac, array, array2, false);
            final byte[] array3 = new byte[42];
            System.arraycopy(attributeData2, 1, array3, 0, 42);
            if (!msChapV2.a(array2, false, a, this.ac, this.ad, array, Util.toUTF8(array3))) {
                if (this.t) {
                    this.a("MS-CHAP2: Authenticator response in MS_CHAP2_Success attribute doesn't match regenerated response.", this.h);
                }
                return false;
            }
            return true;
        }
    }
    
    private final boolean a(final byte b) {
        if (b != this.ab) {
            if (this.t) {
                this.a("MSCHAP2 Identifier is " + b + " expecting " + this.ab, this.h);
            }
            return false;
        }
        return true;
    }
    
    public final byte[] getTunnelPassword(final int n) {
        final Attribute[] attributeArray = this.k.getAttributeArray(69);
        if (attributeArray.length == 0) {
            return null;
        }
        int i = 0;
        while (i < attributeArray.length) {
            final Attribute attribute = attributeArray[i];
            attribute.convertToTunnel();
            final int tunnelTag = attribute.getTunnelTag();
            if (tunnelTag == n || tunnelTag == 0 || tunnelTag > 31) {
                final byte[] trim = RADIUSEncrypt.trim(RADIUSEncrypt.decipherTunnelPassword(attribute.getAttributeData(), this.l, this.g));
                final int n2 = trim[2] & 0xFF;
                if (n2 != trim.length - 3) {
                    return null;
                }
                final byte[] array = new byte[n2];
                System.arraycopy(trim, 3, array, 0, n2);
                return array;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    public final void addDictionary(final RADIUSDictionary radiusDictionary) {
        AV.addDictionary(radiusDictionary);
    }
    
    public final String toString() {
        return "Radius Client connected to " + this.c + ":" + this.b + " Local Port: " + this.d.getLocalPort();
    }
    
    private final void a(final int j, final int n) throws ClientSendException {
        this.j = j;
        final byte[] radiusAttributeBlock = this.k.createRadiusAttributeBlock();
        final int n2 = 20 + radiusAttributeBlock.length;
        final byte[] array = new byte[n2];
        final DatagramPacket datagramPacket = new DatagramPacket(new byte[n2], n2);
        int n3 = 0;
        array[n3++] = (byte)j;
        array[n3++] = (byte)this.h;
        array[n3++] = (byte)(n2 >> 8);
        array[n3++] = (byte)(n2 & 0xFF);
        System.arraycopy(this.g, 0, array, n3, 16);
        n3 += 16;
        System.arraycopy(radiusAttributeBlock, 0, array, n3, radiusAttributeBlock.length);
        final int length = n3 + radiusAttributeBlock.length;
        if (this.k.exists(80)) {
            final Mac value = HMAC_MD5.get();
            try {
                value.init(new SecretKeySpec(this.l, "NONE"));
            }
            catch (final InvalidKeyException ex) {
                throw new ClientSendException("Error creating Message-Authenticator: " + ex.getMessage());
            }
            final byte[] doFinal = value.doFinal(array);
            System.arraycopy(doFinal, 0, array, 20 + this.k.findPosition(80) + 1 + 1, 16);
            this.k.setAttribute(80, doFinal);
        }
        if (n == 2) {
            final MessageDigest value2 = MD5Digest.get();
            value2.update(array, 0, length);
            value2.update(this.l);
            System.arraycopy(this.g = value2.digest(), 0, array, 4, this.g.length);
        }
        datagramPacket.setData(array);
        datagramPacket.setLength(length);
        datagramPacket.setPort(this.b);
        datagramPacket.setAddress(this.c);
        if (this.t) {
            this.a("Request Packet", this.h, j, array, length, this.c, this.b);
        }
        this.i = array;
        try {
            this.d.send(datagramPacket);
        }
        catch (final IOException ex2) {
            final String string = "Packet Send Failed (" + ex2.getMessage() + ")";
            if (this.t) {
                this.a(string, this.h, ex2);
            }
            throw new ClientSendException(string);
        }
    }
    
    private final void a(final byte[] i) throws ClientSendException {
        try {
            final DatagramPacket datagramPacket = new DatagramPacket(i, i.length);
            datagramPacket.setPort(this.b);
            datagramPacket.setAddress(this.c);
            final ByteIterator byteIterator = new ByteIterator(i, false);
            final int unsignedByte = byteIterator.readUnsignedByte();
            this.h = byteIterator.readUnsignedByte();
            final short short1 = byteIterator.readShort();
            if (this.t) {
                final int n = (int)(byteIterator.length() - byteIterator.current());
                final byte[] array = new byte[n];
                byteIterator.read(array);
                this.k = new AttributeList();
                try {
                    this.k.loadRadiusAttributes(array, 0, n, this.y);
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    this.a("Error extracting attributes: " + ex.getMessage(), this.h, ex);
                }
                this.a("Request Packet", this.h, unsignedByte, i, short1, this.c, this.b);
            }
            this.i = i;
            this.d.send(datagramPacket);
        }
        catch (final IOException ex2) {
            final String string = "Packet Send Failed (" + ex2.getMessage() + ")";
            if (this.t) {
                this.a(string, this.h, ex2);
            }
            throw new ClientSendException(string);
        }
    }
    
    private final int e() throws ClientReceiveException {
        this.k = null;
        final byte[] array = new byte[this.m];
        final DatagramPacket datagramPacket = new DatagramPacket(array, array.length);
        try {
            this.d.receive(datagramPacket);
        }
        catch (final IOException ex) {
            final String s = "Packet Receive Failed";
            if (this.t) {
                this.a(s, this.h, ex);
            }
            throw new ClientReceiveException(s);
        }
        if (!this.c.equals(datagramPacket.getAddress())) {
            this.v = 7;
            return 0;
        }
        this.e = datagramPacket.getData();
        this.f = datagramPacket.getLength();
        final ByteIterator byteIterator = new ByteIterator(this.e, false);
        if (this.f < 20) {
            if (this.t) {
                if (this.f > 2) {
                    final int unsignedByte = byteIterator.readUnsignedByte();
                    final int unsignedByte2 = byteIterator.readUnsignedByte();
                    this.k = new AttributeList();
                    this.a("Response Packet - Duplicate packet received", unsignedByte2, unsignedByte, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
                }
                else {
                    this.logToDebug("Incomplete packet cannot be displayed.");
                }
            }
            this.v = 8;
            return 0;
        }
        final int unsignedByte3 = byteIterator.readUnsignedByte();
        final int unsignedByte4 = byteIterator.readUnsignedByte();
        final int unsignedShort = byteIterator.readUnsignedShort();
        if (unsignedShort > this.f) {
            this.k = new AttributeList();
            this.a("Response Packet - Stated packet length is less than physical packet length", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
            this.v = 8;
            return 0;
        }
        this.f = unsignedShort;
        if (this.b(this.d.getLocalPort(), unsignedByte4)) {
            if (this.t) {
                this.k = new AttributeList();
                this.a("Response Packet - Duplicate packet received", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
            }
            this.v = 1;
            return 0;
        }
        if (unsignedByte4 != this.h) {
            if (this.t) {
                this.k = new AttributeList();
                this.a("Response Packet - Mismatched packet Id", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
            }
            this.v = 3;
            return this.z = 0;
        }
        this.k = new AttributeList();
        try {
            this.k.loadRadiusAttributes(this.e, 20, this.f - 20, this.y);
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            if (this.t) {
                this.k = new AttributeList();
                this.a("Response Packet - Corrupt Attributes (" + ex2.getMessage() + ")", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
            }
            this.v = 4;
            return this.z = 0;
        }
        switch (unsignedByte3) {
            case 5:
            case 41:
            case 42:
            case 44:
            case 45: {
                final boolean b = this.b();
                if (this.t) {
                    this.a("Response Packet" + (b ? "" : " - Bad authenticator"), unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
                }
                this.z = (b ? unsignedByte3 : false);
                if (this.z == 0) {
                    this.v = 5;
                }
                return this.z;
            }
            default: {
                if (!this.c()) {
                    if (this.t) {
                        this.a("Response Packet - Bad authenticator", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
                    }
                    this.v = 5;
                    return this.z = 0;
                }
                if (!this.a(unsignedByte3, this.e, this.f, this.k)) {
                    if (this.t) {
                        this.a("Response Packet - bad Message-Authenticator ", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
                    }
                    this.v = 6;
                    return this.z = 0;
                }
                if (this.t) {
                    this.a("Response Packet", unsignedByte4, unsignedByte3, this.e, this.f, datagramPacket.getAddress(), datagramPacket.getPort());
                }
                return this.z = unsignedByte3;
            }
        }
    }
    
    protected final boolean b() {
        final MessageDigest value = MD5Digest.get();
        value.update(this.e, 0, 4);
        value.update(this.g);
        if (this.f > 20) {
            value.update(this.e, 20, this.f - 20);
        }
        value.update(this.l);
        return Util.cmp(value.digest(), 0, this.e, 4, 16);
    }
    
    public static boolean checkAccountingAuthenticator(final byte[] array, final int n, final byte[] array2, final byte[] array3) {
        final MessageDigest value = MD5Digest.get();
        value.update(array, 0, 4);
        value.update(array2);
        if (n > 20) {
            value.update(array, 20, n - 20);
        }
        value.update(array3);
        return Util.cmp(value.digest(), 0, array, 4, 16);
    }
    
    protected final boolean c() {
        final MessageDigest value = MD5Digest.get();
        value.update(this.e, 0, 4);
        value.update(this.g);
        value.update(this.e, 20, this.f - 20);
        value.update(this.l);
        final byte[] digest = value.digest();
        if (this.f > 20 && this.e[0] == 3) {
            (this.k = new AttributeList()).loadRadiusAttributes(this.e, 20, this.f - 20, this.y);
        }
        return Util.cmp(this.e, 4, digest, 0, 16);
    }
    
    public static boolean checkAuthenticator(final byte[] array, final int n, final byte[] array2, final byte[] array3) {
        final MessageDigest value = MD5Digest.get();
        value.update(array, 0, 4);
        value.update(array2);
        value.update(array, 20, n - 20);
        value.update(array3);
        return Util.cmp(array, 4, value.digest(), 0, 16);
    }
    
    private final boolean a(final int n, final byte[] array, final int n2, final AttributeList list) {
        final int position = list.findPosition(80);
        if (position < 0) {
            return true;
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, 0, array2, 0, n2);
        System.arraycopy(this.g, 0, array2, 4, 16);
        System.arraycopy(RADIUSClient.r, 0, array2, position + 20 + 1 + 1, 16);
        final Mac value = HMAC_MD5.get();
        try {
            value.init(new SecretKeySpec(this.l, "NONE"));
        }
        catch (final InvalidKeyException ex) {
            return false;
        }
        return Util.cmp(value.doFinal(array2), list.getBinaryAttribute(80));
    }
    
    private final byte[] f() {
        this.g = new byte[16];
        this.s.nextBytes(this.g);
        return this.g;
    }
    
    public final byte[] encryptPassword(final byte[] array) {
        return RADIUSEncrypt.encrypt(array, this.l, this.g);
    }
    
    public final byte[] decode(final byte[] array) {
        if (array.length % 16 != 0) {
            return null;
        }
        final int length = array.length;
        final byte[] array2 = new byte[length];
        final MessageDigest value = MD5Digest.get();
        value.update(this.l);
        value.update(this.g);
        final byte[] digest = value.digest();
        for (int i = 0; i < 16; ++i) {
            array2[i] = (byte)(digest[i] ^ array[i]);
        }
        final int n = length / 16;
        int n2 = 0;
        int n3 = 16;
        for (int j = 1; j < n; ++j) {
            value.reset();
            value.update(this.l);
            value.update(array, n2, 16);
            final byte[] digest2 = value.digest();
            for (int k = 0; k < 16; ++k, ++n3) {
                array2[n3] = (byte)(digest2[k] ^ array[n3]);
            }
            n2 += 16;
        }
        return array2;
    }
    
    public final byte[] trim(final byte[] array) {
        int n;
        for (n = array.length - 1; n > 0 && array[n] == 0; --n) {}
        final byte[] array2 = new byte[++n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public static byte[] getBytes(final String s) {
        return Util.toUTF8(s);
    }
    
    protected final void finalize() {
        this.d.close();
    }
    
    public static boolean testEnc(final String s) {
        try {
            final byte[] array = { 0, 1 };
            new String(array, 0, array.length, s);
            return true;
        }
        catch (final UnsupportedEncodingException ex) {
            return false;
        }
    }
    
    public final byte[] getRequestAuthenticator() {
        return this.g;
    }
    
    public final int getPacketType() {
        return this.z;
    }
    
    public final String getPacketTypeName() {
        return this.getPacketTypeName(this.z);
    }
    
    public final String getPacketTypeName(final int n) {
        return new PacketType().getName(n);
    }
    
    public final byte[] getSecret() {
        return this.l;
    }
    
    public final int getError() {
        return this.v;
    }
    
    public final String getErrorString() {
        String a = null;
        switch (this.v) {
            case 0: {
                a = "No Error";
                break;
            }
            default: {
                a = "Unknown";
                break;
            }
            case 4: {
                a = "Corrupt attributes";
                break;
            }
            case 1: {
                a = "Duplicate packet received";
                break;
            }
            case 2: {
                a = "Missing User-Name";
                break;
            }
            case 8: {
                a = "The reply packet was either too small or too large (packet size > physical size)";
                break;
            }
            case 5: {
                a = "Authenticator is incorrect";
                break;
            }
            case 3: {
                a = "Mismatched packet id in received packet";
                break;
            }
            case 7: {
                a = "The reply packet did not arrive from the correct host";
                break;
            }
            case 9: {
                a = this.a;
                break;
            }
        }
        return a + " (" + this.v + ")";
    }
    
    private final void a(final String s, final int n, final int n2, final byte[] array, final int n3, final InetAddress inetAddress, final int n4) {
        final String string = "<" + n + "> ";
        final String string2 = '\n' + string + "------------------- " + s + " -----------------\n" + string + "Address: " + inetAddress.getHostAddress() + ":" + n4 + " Packet Length: " + n3 + " Type: " + new PacketType().getName(n2) + "\n" + new ByteIterator(array, false).dump(0, n3) + "\nAttributes:\n" + this.k + "\n" + string + "---------------------------------------------------\n";
        try {
            this.u.write(string2, 0, string2.length());
            this.u.flush();
        }
        catch (final IOException ex) {
            System.out.println(string2);
        }
    }
    
    private final void a(final String s, final int n) {
        this.a(s, n, null);
    }
    
    private final void a(final String s, final int n, final Exception ex) {
        String s2 = '\n' + ("<" + n + "> ") + "-- " + s;
        if (ex != null) {
            final String string = "Z" + ex.getMessage();
            if (string != null && !string.equals("")) {
                s2 = s2 + " (" + ex.getMessage() + ")";
            }
            else {
                s2 = s2 + " (" + ex.getClass().getName() + ")";
            }
        }
        final String string2 = s2 + " ---\n";
        try {
            this.u.write(string2, 0, string2.length());
            this.u.flush();
        }
        catch (final IOException ex2) {
            System.out.println(string2);
        }
    }
    
    public final byte[] createChallenge16() {
        final byte[] array = new byte[16];
        this.s.nextBytes(array);
        return array;
    }
    
    public final byte[] createChallenge8() {
        final byte[] array = new byte[8];
        this.s.nextBytes(array);
        return array;
    }
    
    public final InetAddress getServer() {
        return this.c;
    }
    
    public final Object getModuleInstance(final String s, final Object o) throws RADIUSModuleException {
        return new ClientModule().getInstance(s, o);
    }
    
    public final void allowEmptyAttributes(final boolean y) {
        this.y = y;
    }
    
    private final boolean b(final int n, final int n2) {
        final Integer n3 = new Integer(n << 8 | n2);
        if (this.w.containsKey(n3)) {
            return true;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        this.w.put(n3, new Long(currentTimeMillis + 30000L));
        final long n4 = currentTimeMillis;
        final ArrayList list = new ArrayList();
        if (n4 > this.x) {
            final Iterator iterator = this.w.keySet().iterator();
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if ((long)this.w.get(next) < n4) {
                    list.add(next);
                }
            }
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                this.w.remove(iterator2.next());
            }
            this.x = System.currentTimeMillis() + 30000L;
        }
        return false;
    }
    
    private final int g() {
        if (this.aa == null) {
            this.aa = new BitArray(256);
        }
        int n = this.aa.nextClearBit(this.s.nextUnsignedByte());
        if (n > 255) {
            n = this.aa.nextClearBit(0);
        }
        this.aa.set(n);
        if (this.aa.cardinality() > (this.p ? 253 : 246)) {
            this.aa.clear();
            this.aa.set(n);
        }
        return n;
    }
    
    static {
        r = new byte[16];
    }
}
