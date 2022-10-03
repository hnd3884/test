package com.theorem.radius3.dmcoaclient;

import java.io.IOException;
import com.theorem.radius3.Ascend;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.Microsoft;
import com.theorem.radius3.A;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import com.theorem.radius3.radutil.ByteIterator;
import java.security.MessageDigest;
import com.theorem.radius3.Attribute;
import com.theorem.radius3.radutil.MD5Digest;
import java.util.Date;
import com.theorem.radius3.RADIUSClient;
import com.theorem.radius3.PacketType;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.CharArrayWriter;
import com.theorem.radius3.AV;
import com.theorem.radius3.dictionary.RADIUSDictionary;
import com.theorem.radius3.RADIUSException;
import java.net.InetAddress;
import com.theorem.radius3.AttributeList;

public final class DMCOAReceiver implements Runnable
{
    public static final int DEFAULT_WINDOW = 300;
    private static int a;
    private static int b;
    private static final byte[] c;
    private boolean d;
    private int e;
    private RC f;
    private int g;
    private int h;
    private byte[] i;
    private AttributeList j;
    private DMCOACallback k;
    private Thread l;
    private boolean m;
    private int n;
    
    public DMCOAReceiver(final InetAddress inetAddress, final byte[] array, final boolean b, final int e, final DMCOACallback k) {
        this.d = false;
        (this.f = new RC(inetAddress, array)).a(b);
        this.e = e;
        if (this.e == 0) {
            this.e = 3799;
        }
        this.k = k;
    }
    
    public final void start() throws RADIUSException {
        if (this.f.getDebugIndicator()) {
            this.f.a("Receiver thread - starting.");
        }
        (this.l = new Thread(this, "DM/COA RADIUS Packet Receiver")).setDaemon(true);
        this.l.start();
        int a = DMCOAReceiver.a;
        while (a-- > 0) {
            try {
                Thread.sleep(DMCOAReceiver.b);
                if (this.d) {
                    if (this.f.getDebugIndicator()) {
                        this.f.a("Receiver thread has started.");
                    }
                    return;
                }
                continue;
            }
            catch (final InterruptedException ex) {}
            break;
        }
        throw new RADIUSException("Server failed to start in " + (DMCOAReceiver.a - a) * DMCOAReceiver.b + " milliseconds.");
    }
    
    public final void stop() {
        this.l.interrupt();
    }
    
    public final boolean useEventTimestamp(final boolean m, final int n) {
        final boolean i = this.m;
        this.m = m;
        this.n = n;
        return i;
    }
    
    public final void addDictionary(final RADIUSDictionary radiusDictionary) {
        AV.addDictionary(radiusDictionary);
    }
    
    public final void run() {
        try {
            this.a();
        }
        catch (final InterruptedException ex) {
            if (this.f.getDebugIndicator()) {
                this.f.a("Receiver thread has terminated.");
            }
        }
        catch (final Exception ex2) {
            if (!this.f.getDebugIndicator()) {
                return;
            }
            final CharArrayWriter charArrayWriter = new CharArrayWriter();
            ex2.printStackTrace(new PrintWriter(charArrayWriter));
            this.f.a(charArrayWriter.toString());
        }
    }
    
    private final void a() throws Exception {
        if (this.f.getDebugIndicator()) {
            this.f.a("DM / COA Receiver starting on port " + this.e + "\n");
        }
        final int length = 4097;
        final DatagramPacket datagramPacket = new DatagramPacket(new byte[length], length);
        final DatagramSocket datagramSocket = new DatagramSocket(this.e);
        datagramSocket.setSoTimeout(0);
        while (true) {
            this.d = true;
            datagramSocket.receive(datagramPacket);
            final String hostAddress = datagramPacket.getAddress().getHostAddress();
            if (this.f.getDebugIndicator()) {
                this.f.a(hostAddress + " Packet received from port " + datagramPacket.getPort() + "\n");
            }
            if (!datagramPacket.getAddress().equals(this.f.a())) {
                this.f.a(hostAddress + " Error: Packet arrived from unknown server:\n");
            }
            else {
                final byte[] data = datagramPacket.getData();
                final int length2 = datagramPacket.getLength();
                if (length2 < 20) {
                    this.f.a(hostAddress + " Error: Packet is too small: " + length2 + "\n");
                }
                else {
                    this.g = (data[1] & 0xFF);
                    this.h = (data[0] & 0xFF);
                    if (this.h != 40 && this.h != 43) {
                        this.f.a(hostAddress + " Error: Unexpected packet type of " + new PacketType().getName(this.h) + "\n");
                    }
                    else if (length2 > 4096) {
                        this.f.a(hostAddress + " Error: Packet length exceed maximum allowable length\n");
                    }
                    else {
                        final int n = (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
                        if (n > length2) {
                            this.f.a(hostAddress + " Error: Stated packet length exceed physical packet length\n");
                        }
                        else {
                            System.arraycopy(data, 4, this.i = new byte[16], 0, 16);
                            if (!RADIUSClient.checkAccountingAuthenticator(data, n, DMCOAReceiver.c, this.f.b())) {
                                this.f.a(hostAddress + " Error: Authenticator is incorrect - probably secret mismatch");
                            }
                            else if (length2 - 20 < 0) {
                                this.f.a(hostAddress + " Error: Attribute size is negative.\n");
                            }
                            else {
                                try {
                                    (this.j = new AttributeList()).loadRadiusAttributes(data, 20, length2 - 20, false);
                                }
                                catch (final ArrayIndexOutOfBoundsException ex) {
                                    this.f.a(hostAddress + " Error: Attributes are corrupt.\n");
                                    continue;
                                }
                                if (this.m) {
                                    final Attribute[] attributeArray = this.j.getAttributeArray(55);
                                    if (attributeArray.length == 0) {
                                        this.f.a(hostAddress + " Error: Missing Event-Timestamp attribute..\n");
                                        continue;
                                    }
                                    final Date date = attributeArray[0].getDate();
                                    final Date date2 = new Date();
                                    if (date2.before(date)) {
                                        this.f.a(hostAddress + " Error: Event-Timestamp value is in the future.\n");
                                        continue;
                                    }
                                    if (date2.getTime() - date.getTime() > this.n) {
                                        this.f.a(hostAddress + " Error: Event-Timestamp value is too old:." + (date2.getTime() - date.getTime()) + " is greater than " + this.n + " seconds.\n");
                                        continue;
                                    }
                                }
                                if (this.f.getDebugIndicator()) {
                                    this.a("DMCOA Request Packet", this.g, this.h, data, length2, this.j, datagramPacket.getAddress(), datagramPacket.getPort());
                                }
                                final DMCOAResponse dmcoaCallback = this.k.dmcoaCallback(this.h, this.j);
                                if (dmcoaCallback == null) {
                                    this.f.a(hostAddress + " Error: Callback returned a null DMCOAResponse.\n");
                                }
                                else {
                                    final AttributeList responseAttributes = dmcoaCallback.getResponseAttributes();
                                    if (responseAttributes == null) {
                                        this.f.a(hostAddress + " Error: DMCOAResponse is missing response attributes.\n");
                                    }
                                    else {
                                        final int packetType = dmcoaCallback.getPacketType();
                                        switch (packetType) {
                                            case 41:
                                            case 42:
                                            case 44:
                                            case 45: {
                                                responseAttributes.deleteAll(24);
                                                responseAttributes.mergeAttributes(this.j.getAttributeArray(24));
                                                final byte[] radiusAttributeBlock = responseAttributes.createRadiusAttributeBlock();
                                                final int n2 = 20 + radiusAttributeBlock.length;
                                                final byte[] data2 = new byte[n2];
                                                final byte[] array = new byte[n2];
                                                int n3 = 0;
                                                data2[n3++] = (byte)packetType;
                                                data2[n3++] = (byte)this.g;
                                                data2[n3++] = (byte)(n2 >> 8);
                                                data2[n3++] = (byte)(n2 & 0xFF);
                                                System.arraycopy(this.i, 0, data2, n3, 16);
                                                n3 += 16;
                                                System.arraycopy(radiusAttributeBlock, 0, data2, n3, radiusAttributeBlock.length);
                                                final int n4 = n3 + radiusAttributeBlock.length;
                                                final MessageDigest value = MD5Digest.get();
                                                value.update(data2);
                                                value.update(this.f.b());
                                                System.arraycopy(this.i = value.digest(), 0, data2, 4, 16);
                                                final InetAddress address = datagramPacket.getAddress();
                                                final int port = datagramPacket.getPort();
                                                if (this.f.getDebugIndicator()) {
                                                    this.a("DMCOA Response Packet", this.g, dmcoaCallback.getPacketType(), data2, data2.length, responseAttributes, address, port);
                                                }
                                                final DatagramPacket datagramPacket2 = new DatagramPacket(array, array.length);
                                                datagramPacket2.setData(data2);
                                                datagramPacket2.setLength(data2.length);
                                                datagramPacket2.setPort(port);
                                                datagramPacket2.setAddress(address);
                                                datagramSocket.send(datagramPacket2);
                                                datagramPacket.setLength(length);
                                                continue;
                                            }
                                            default: {
                                                this.f.a(hostAddress + " Error: DMCOAResponse has an unexpected packet type ." + new PacketType().getName(packetType) + "\n");
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private final void a(final String s, final int n, final int n2, final byte[] array, final int n3, final AttributeList list, final InetAddress inetAddress, final int n4) {
        final String string = "<" + n + "> ";
        this.f.a('\n' + string + "------------------- " + s + " -----------------\n" + string + "Address: " + inetAddress.getHostAddress() + ":" + n4 + "  Packet Length: " + n3 + " Type: " + new PacketType().getName(n2) + "\n" + new ByteIterator(array, false).dump(0, n3) + "\nAttributes:\n" + list + "\n" + string + "---------------------------------------------------\n");
    }
    
    static {
        DMCOAReceiver.a = 10;
        DMCOAReceiver.b = 100;
        c = new byte[16];
    }
    
    class RC
    {
        boolean a;
        byte[] b;
        InetAddress c;
        private boolean d;
        private BufferedWriter e;
        
        RC(final InetAddress c, final byte[] b) {
            this.a = false;
            this.d = false;
            this.c = c;
            this.b = b;
        }
        
        public final boolean getDebugIndicator() {
            return this.d;
        }
        
        final InetAddress a() {
            return this.c;
        }
        
        final byte[] b() {
            return this.b;
        }
        
        final void a(final boolean d, final String s) throws IOException {
            this.d = d;
            if (s == null) {
                this.e = new BufferedWriter(new OutputStreamWriter(System.out, "UTF8"));
            }
            else {
                this.e = new BufferedWriter(new FileWriter(s));
            }
            new A();
            new Microsoft();
            new Cisco();
            new Ascend();
        }
        
        final boolean a(final boolean b) {
            try {
                this.a(b, null);
            }
            catch (final IOException ex) {
                System.err.println("Can't write to stdout");
                return false;
            }
            return true;
        }
        
        final void a(final String s) {
            if (this.d) {
                if (this.e != null) {
                    try {
                        this.e.write(s, 0, s.length());
                        this.e.flush();
                    }
                    catch (final IOException ex) {
                        System.out.println(s);
                    }
                }
                else {
                    System.out.println(s);
                }
            }
        }
    }
}
