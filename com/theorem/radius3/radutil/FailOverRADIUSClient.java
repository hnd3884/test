package com.theorem.radius3.radutil;

import java.util.Iterator;
import com.theorem.radius3.RADIUSException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.EAPException;
import java.net.SocketException;
import com.theorem.radius3.PacketType;
import com.theorem.radius3.eap.EAPMD5Client;
import com.theorem.radius3.RADIUSClient;
import java.net.InetAddress;
import com.theorem.radius3.AttributeList;
import java.util.ArrayList;

public class FailOverRADIUSClient
{
    private ArrayList a;
    private AttributeList b;
    private boolean c;
    private byte[] d;
    private byte[] e;
    private byte[] f;
    private String g;
    private InetAddress h;
    private int i;
    private boolean j;
    private String k;
    private int l;
    private String m;
    private boolean n;
    public static final String AUTH_PAP = "PAP";
    public static final String AUTH_CHAP = "CHAP";
    public static final String AUTH_MSCHAP = "MSCHAP";
    public static final String AUTH_MSCHAP2 = "MSCHAP2";
    public static final String AUTH_EAPMD5 = "EAPMD5";
    
    public FailOverRADIUSClient() {
        this.a = new ArrayList();
        this.g = "PAP";
        this.c = false;
        this.n = false;
    }
    
    public final void addServer(final InetAddress inetAddress, final String s, final AttributeList list) throws Exception {
        if (inetAddress == null) {
            throw new Exception("RADIUS server cannot be null.");
        }
        if (s == null) {
            throw new Exception("RADIUS secret cannot be null.");
        }
        final ServerData serverData = new ServerData(inetAddress, s, 0, 0, list);
        this.a.add(serverData);
        if (this.n) {
            System.out.println("Adding " + serverData);
        }
    }
    
    public final void addServer(final InetAddress inetAddress, final int n, final int n2, final String s, final AttributeList list) throws Exception {
        if (inetAddress == null) {
            throw new Exception("RADIUS server cannot be null.");
        }
        if (s == null) {
            throw new Exception("RADIUS secret cannot be null.");
        }
        final ServerData serverData = new ServerData(inetAddress, s, n, n2, list);
        this.a.add(serverData);
        if (this.n) {
            System.out.println("Adding " + serverData);
        }
    }
    
    public final void setTimeout(final int i) {
        this.i = i;
        if (this.n) {
            System.out.println("Setting Timeout to " + i);
        }
    }
    
    public final void setDebug(final boolean n) {
        this.n = n;
    }
    
    public final void allowEmptyAttributes(final boolean c) {
        this.c = c;
        if (this.n) {
            System.out.println(this.c ? "Enabling empty attributes" : "Disabling empty attributes");
        }
    }
    
    public final void setAttributes(final AttributeList list) {
        this.d = list.createRadiusAttributeBlock();
        if (this.n) {
            System.out.println("Common attributes:\n" + list);
        }
    }
    
    public final AttributeList getAttributes() {
        return this.b;
    }
    
    public final void setName(final String s) {
        this.e = Util.toUTF8(s);
    }
    
    public final void setPassword(final byte[] f) {
        this.f = f;
    }
    
    public final void setPassword(final String s) {
        this.f = Util.toUTF8(s);
    }
    
    public final void setAuthenticationType(final String g) throws Exception {
        if (!g.equals("PAP") && !g.equals("CHAP") && !g.equals("MSCHAP") && !g.equals("MSCHAP2") && !g.equals("EAPMD5")) {
            throw new Exception("Uknown authentication type: " + g);
        }
        this.g = g;
        if (this.n) {
            System.out.println("Using authentication type " + this.g);
        }
    }
    
    public final int authenticate() {
        if (this.n) {
            System.out.println("----------- Authenticating -----------");
        }
        final Iterator iterator = this.a.iterator();
        int h = -1;
        this.k = "No error.";
        RADIUSClient radiusClient = null;
        if (this.a.isEmpty()) {
            this.k = "No server to authenticate against.";
            this.j = true;
            return h;
        }
        while (iterator.hasNext()) {
            this.j = false;
            final ServerData serverData = (ServerData)iterator.next();
            serverData.a();
            serverData.k = true;
            this.m = serverData.j;
            this.l = serverData.i;
            this.h = serverData.a;
            if (this.n) {
                System.out.println("Trying server " + this.h.getHostAddress());
            }
            final AttributeList list = new AttributeList();
            if (this.d != null) {
                list.loadRadiusAttributes(this.d, 0, this.d.length, this.c);
            }
            final AttributeList list2 = new AttributeList();
            if (serverData.e != null) {
                list2.loadRadiusAttributes(serverData.e, 0, serverData.e.length, this.c);
            }
            list.mergeAttributes(list2);
            try {
                radiusClient = new RADIUSClient(serverData.a, serverData.b, serverData.d, this.i);
                radiusClient.setDebug(this.n);
                if (this.g.equals("PAP")) {
                    list.addAttribute(1, this.e);
                    list.addAttribute(2, radiusClient.encryptPassword(this.f));
                    h = radiusClient.authenticate(list);
                }
                else if (this.g.equals("CHAP")) {
                    list.addAttribute(1, this.e);
                    radiusClient.createCHAP(this.f, list);
                    h = radiusClient.authenticate(list);
                }
                else if (this.g.equals("MSCHAP")) {
                    radiusClient.createMSCHAP(this.f, list);
                    list.addAttribute(1, this.e);
                    h = radiusClient.authenticate(list);
                }
                else if (this.g.equals("MSCHAP2")) {
                    radiusClient.createMSCHAP2(this.e, this.f, list);
                    list.addAttribute(1, this.e);
                    h = radiusClient.authenticate(list);
                }
                else {
                    if (!this.g.equals("EAPMD5")) {
                        radiusClient.close();
                        this.j = true;
                        this.h = null;
                        this.k = "Uknown authtype of " + this.g;
                        if (this.n) {
                            System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
                        }
                        break;
                    }
                    final boolean authenticate = new EAPMD5Client(radiusClient, list).authenticate(this.e, this.f);
                    h = radiusClient.getPacketType();
                    if (authenticate && h != 2) {
                        radiusClient.close();
                        this.j = true;
                        this.h = null;
                        this.k = "EAPMD5: incorrect result EAP authentication succesful and RADIUS packet type is " + new PacketType().getName(h);
                        if (!this.n) {
                            continue;
                        }
                        System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
                        continue;
                    }
                }
                this.b = radiusClient.getAttributes();
                if ((serverData.h = h) == 0) {
                    final ServerData serverData2 = serverData;
                    final int error = radiusClient.getError();
                    this.l = error;
                    serverData2.i = error;
                    final ServerData serverData3 = serverData;
                    final String errorString = radiusClient.getErrorString();
                    this.m = errorString;
                    serverData3.j = errorString;
                }
                radiusClient.close();
                if (h == 2) {
                    break;
                }
                continue;
            }
            catch (final SocketException ex) {
                radiusClient.close();
                final ServerData serverData4 = serverData;
                final boolean b = true;
                this.j = b;
                serverData4.g = b;
                this.h = null;
                final ServerData serverData5 = serverData;
                final String string = "SocketException: " + ex.getMessage();
                this.k = string;
                serverData5.f = string;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final EAPException ex2) {
                radiusClient.close();
                final ServerData serverData6 = serverData;
                final boolean b2 = true;
                this.j = b2;
                serverData6.g = b2;
                this.h = null;
                final ServerData serverData7 = serverData;
                final String string2 = "EAPClientException: " + ex2.getMessage();
                this.k = string2;
                serverData7.f = string2;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final ClientSendException ex3) {
                radiusClient.close();
                final ServerData serverData8 = serverData;
                final boolean b3 = true;
                this.j = b3;
                serverData8.g = b3;
                final ServerData serverData9 = serverData;
                final String string3 = "Client Send Exception: " + ex3.getMessage();
                this.k = string3;
                serverData9.f = string3;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final ClientReceiveException ex4) {
                radiusClient.close();
                final ServerData serverData10 = serverData;
                final boolean b4 = true;
                this.j = b4;
                serverData10.g = b4;
                final ServerData serverData11 = serverData;
                final String string4 = "Client Receive Exception: " + ex4.getMessage();
                this.k = string4;
                serverData11.f = string4;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final RADIUSException ex5) {
                radiusClient.close();
                final ServerData serverData12 = serverData;
                final boolean b5 = true;
                this.j = b5;
                serverData12.g = b5;
                final ServerData serverData13 = serverData;
                final String string5 = "RADIUSException: " + ex5.getMessage();
                this.k = string5;
                serverData13.f = string5;
                if (!this.n) {
                    continue;
                }
                System.out.println("Client failed: " + this.k);
            }
        }
        if (this.n) {
            if (this.j) {
                System.out.println("Authentication by server " + this.h.getHostAddress() + " returned " + new PacketType().getName(h));
            }
            else {
                System.out.println("Authentication failed to be handled by any server.");
            }
        }
        return h;
    }
    
    public final int accounting() {
        if (this.n) {
            System.out.println("----------- Accounting -----------");
        }
        final Iterator iterator = this.a.iterator();
        int n = 0;
        this.k = "No error.";
        if (this.a.isEmpty()) {
            this.k = "No server to authenticate against.";
            this.j = true;
            return n;
        }
        while (iterator.hasNext()) {
            this.j = false;
            final ServerData serverData = (ServerData)iterator.next();
            serverData.a();
            serverData.k = true;
            this.m = serverData.j;
            this.l = serverData.i;
            this.h = serverData.a;
            RADIUSClient radiusClient = null;
            if (this.n) {
                System.out.println("Trying server " + this.h.getHostAddress());
            }
            final AttributeList list = new AttributeList();
            if (this.d != null) {
                list.loadRadiusAttributes(this.d, 0, this.d.length, this.c);
            }
            final AttributeList list2 = new AttributeList();
            if (serverData.e != null) {
                list2.loadRadiusAttributes(serverData.e, 0, serverData.e.length, this.c);
            }
            list.mergeAttributes(list2);
            if (this.n) {
                System.out.println("Sending merged attributes:\n" + list);
            }
            try {
                radiusClient = new RADIUSClient(serverData.a, serverData.b, serverData.d, this.i);
                radiusClient.setDebug(this.n);
                n = (serverData.h = radiusClient.accounting(list));
                this.b = radiusClient.getAttributes();
                if (n == 5) {
                    break;
                }
                if (n != 0) {
                    continue;
                }
                final ServerData serverData2 = serverData;
                final int error = radiusClient.getError();
                this.l = error;
                serverData2.i = error;
                final ServerData serverData3 = serverData;
                final String errorString = radiusClient.getErrorString();
                this.m = errorString;
                serverData3.j = errorString;
            }
            catch (final SocketException ex) {
                radiusClient.close();
                final ServerData serverData4 = serverData;
                final boolean b = true;
                this.j = b;
                serverData4.g = b;
                this.h = null;
                final ServerData serverData5 = serverData;
                final String string = "Socket Exception: " + ex.getMessage();
                this.k = string;
                serverData5.f = string;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final ClientSendException ex2) {
                final ServerData serverData6 = serverData;
                final boolean b2 = true;
                this.j = b2;
                serverData6.g = b2;
                final ServerData serverData7 = serverData;
                final String string2 = "Client Send Exception: " + ex2.getMessage();
                this.k = string2;
                serverData7.f = string2;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
            catch (final ClientReceiveException ex3) {
                final ServerData serverData8 = serverData;
                final boolean b3 = true;
                this.j = b3;
                serverData8.g = b3;
                final ServerData serverData9 = serverData;
                final String string3 = "Client Receive Exception: " + ex3.getMessage();
                this.k = string3;
                serverData9.f = string3;
                if (!this.n) {
                    continue;
                }
                System.out.println("Server " + this.h.getHostAddress() + " failed: " + this.k);
            }
        }
        if (this.n) {
            if (this.j) {
                System.out.println("Authentication by server " + this.h.getHostAddress() + " returned " + new PacketType().getName(n));
            }
            else {
                System.out.println("Authentication failed to be handled by any server.");
            }
        }
        return n;
    }
    
    public final boolean success() {
        return !this.j;
    }
    
    public final String getErrorMessage() {
        return this.k;
    }
    
    public final InetAddress getRespondingServer() {
        return this.h;
    }
    
    public final ServerData getRespondingServerData() {
        if (this.h == null) {
            return null;
        }
        final Iterator iterator = this.a.iterator();
        while (iterator.hasNext()) {
            final ServerData serverData = (ServerData)iterator.next();
            if (serverData.a.equals(this.h)) {
                return serverData;
            }
        }
        return null;
    }
    
    public final Iterator serverIterator() {
        return this.a.iterator();
    }
    
    public static void main(final String[] array) {
        try {
            final FailOverRADIUSClient failOverRADIUSClient = new FailOverRADIUSClient();
            failOverRADIUSClient.setDebug(true);
            failOverRADIUSClient.setTimeout(1000);
            failOverRADIUSClient.addServer(InetAddress.getByName("192.168.1.101"), "axltest", null);
            final InetAddress byName = InetAddress.getByName("127.0.0.1");
            final AttributeList list = new AttributeList();
            list.addAttribute(4, byName);
            failOverRADIUSClient.addServer(byName, "axltest", list);
            final AttributeList attributes = new AttributeList();
            attributes.addAttribute(5, 3);
            failOverRADIUSClient.setAttributes(attributes);
            failOverRADIUSClient.setName("michael");
            failOverRADIUSClient.setPassword(Util.toUTF8("test"));
            failOverRADIUSClient.setAuthenticationType("CHAP");
            final int authenticate = failOverRADIUSClient.authenticate();
            final InetAddress respondingServer = failOverRADIUSClient.getRespondingServer();
            final AttributeList attributes2 = failOverRADIUSClient.getAttributes();
            if (!failOverRADIUSClient.success()) {
                System.out.println("Authentication failed: " + failOverRADIUSClient.getErrorMessage());
            }
            switch (authenticate) {
                case 2: {
                    System.out.println("Authenticated:\n" + attributes2);
                    break;
                }
                case 3: {
                    System.out.println("Authentication Failed:\n" + attributes2);
                    break;
                }
                case 11: {
                    System.out.println("Challenged by " + respondingServer.getHostAddress() + "\n" + attributes2);
                    break;
                }
                case 0: {
                    final ServerData respondingServerData = failOverRADIUSClient.getRespondingServerData();
                    System.out.println("Bad Packet received: " + respondingServerData.getBadPacketErrorString());
                    System.out.println("Failed server information: " + respondingServerData);
                    break;
                }
                default: {
                    System.out.println("Uknown packet type: " + authenticate);
                    break;
                }
            }
            final Iterator serverIterator = failOverRADIUSClient.serverIterator();
            while (serverIterator.hasNext()) {
                System.out.println(serverIterator.next());
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    public class ServerData
    {
        protected InetAddress a;
        protected int b;
        protected int c;
        protected String d;
        protected byte[] e;
        protected String f;
        protected boolean g;
        protected int h;
        protected int i;
        protected String j;
        protected boolean k;
        
        ServerData(final InetAddress a, final String d, final int n, final int n2, final AttributeList list) {
            this.a = a;
            this.b = ((n == 0) ? 1812 : n);
            this.c = ((n2 == 0) ? 1813 : n2);
            this.d = d;
            if (list == null) {
                this.e = null;
            }
            else {
                this.e = list.createRadiusAttributeBlock();
            }
            this.a();
        }
        
        protected final void a() {
            this.g = false;
            this.f = "No error";
            this.h = -1;
            this.i = -1;
            this.j = this.f;
            this.k = false;
        }
        
        public final boolean sentRequest() {
            return this.k;
        }
        
        public final int getResponsePacketType() {
            return this.h;
        }
        
        public final String getResponsePacketName() {
            if (this.h == -1) {
                return "No packetType";
            }
            return new PacketType().getName(this.h);
        }
        
        public final int getBadPacketError() {
            return this.i;
        }
        
        public final String getBadPacketErrorString() {
            return this.j;
        }
        
        public final boolean isClientError() {
            return this.g;
        }
        
        public final String getClientErrorString() {
            return this.f;
        }
        
        public final String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("Server: server.").append(this.a.getHostAddress()).append(", ");
            sb.append("Auth: ").append(this.b).append(", ");
            sb.append("Acct: ").append(this.c).append(", ");
            sb.append("Secret: ").append(this.d);
            if (this.h > -1) {
                sb.append(", ");
                if (this.g) {
                    sb.append("Error: ").append(this.f).append(", ");
                }
                sb.append("Result: ").append(new PacketType().getName(this.h)).append(", ");
                if (this.h == 0) {
                    sb.append("BadPacket error: ").append(this.j);
                }
            }
            else if (this.g) {
                sb.append(", ");
                sb.append("Error: ").append(this.f);
            }
            return sb.toString();
        }
    }
}
