package com.theorem.radius3.radutil;

import com.theorem.radius3.module.RADIUSModuleException;
import com.theorem.radius3.module.LEAPCLIENT;
import com.theorem.radius3.EAPException;
import com.theorem.radius3.eap.EAPMD5Client;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.RADIUSException;
import com.theorem.radius3.AttributeList;
import java.net.UnknownHostException;
import java.net.SocketException;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.Microsoft;
import com.theorem.radius3.A;
import com.theorem.radius3.RADIUSClient;
import java.net.InetAddress;

public class radauth
{
    static String a;
    static String b;
    static String c;
    static String d;
    static String e;
    static int f;
    static int g;
    static String h;
    static InetAddress i;
    
    public static void main(final String[] array) {
        a(array);
        RADIUSClient radiusClient = null;
        int n = 0;
        InetAddress localHost = null;
        System.out.println("Radtest running RADIUS client version 3.43p RADIUS Secure Version");
        if (radauth.h.equals("EAPMD5")) {
            a();
        }
        try {
            radiusClient = new RADIUSClient(radauth.a, radauth.g, radauth.b, 10000);
            radiusClient.setDebug(true);
            new A();
            new Microsoft();
            new Cisco();
            localHost = InetAddress.getLocalHost();
        }
        catch (final SocketException ex) {
            System.err.println("Radius failed: " + ex.getMessage());
            System.exit(1);
        }
        catch (final UnknownHostException ex2) {
            System.err.println("Radius failed: " + ex2.getMessage());
            System.exit(1);
        }
        System.out.println("\n-------------------------------- Authentication -------------------------------");
        System.out.println("Authenticating: " + radauth.d + " " + radauth.e);
        System.out.println("Sending to server " + radauth.a + ":" + radauth.g);
        final byte[] utf8 = Util.toUTF8(radauth.d);
        final byte[] utf9 = Util.toUTF8(radauth.e);
        try {
            final AttributeList list = new AttributeList();
            if (radauth.c == null) {
                list.addAttribute(4, localHost);
            }
            if (radauth.c != null) {
                list.addAttribute(32, radauth.c);
            }
            list.addAttribute(5, radauth.f);
            System.out.println("Sending Attributes:\n" + list);
            if (radauth.h.equals("PAP")) {
                n = radiusClient.authenticate(radauth.d, radauth.e, list);
            }
            else if (radauth.h.equals("CHAP")) {
                list.addAttribute(1, radauth.d);
                n = radiusClient.authenticate(utf9, list);
            }
            else if (radauth.h.equals("MSCHAP")) {
                try {
                    radiusClient.createMSCHAP(utf9, list);
                }
                catch (final RADIUSException ex3) {
                    System.out.println("Can't access the DES encoding algorithm -:" + ex3.getMessage());
                    System.exit(1);
                }
                list.addAttribute(1, radauth.d);
                n = radiusClient.authenticate(list);
            }
            else if (radauth.h.equals("MSCHAP2")) {
                try {
                    radiusClient.createMSCHAP2(utf8, utf9, list);
                    list.addAttribute(1, radauth.d);
                    n = radiusClient.authenticate(list);
                    if (!radiusClient.cmpMSCHAP2(utf8, utf9, radiusClient.getAttributes())) {
                        System.out.println("MS-CHAP V2: Failed to authenticate the server.");
                        n = 3;
                    }
                }
                catch (final RADIUSException ex4) {
                    System.out.println("Can't access the DES encoding algorithm -:" + ex4.getMessage());
                    System.exit(1);
                }
            }
            else if (radauth.h.equals("LEAP")) {
                System.exit((a(radiusClient, list).length == 0) ? 1 : 0);
            }
            else if (radauth.h.equals("SHUTDOWN")) {
                list.addAttribute(6, 66);
                n = radiusClient.authenticate(radauth.d, radauth.e, list);
            }
            else {
                System.out.println("Error: unknown authentication type: " + radauth.h);
                System.exit(1);
            }
            switch (n) {
                case 3: {
                    System.out.println("Failed to authenticate");
                    break;
                }
                case 0: {
                    System.out.println("Received bad packet: " + radiusClient.getErrorString());
                    break;
                }
                case 11: {
                    System.out.println("Access was challenged. Can't handle this yet.");
                    break;
                }
                case 2: {
                    System.out.println("Authenticated");
                    System.out.println("Attributes returned from server:\n" + radiusClient.getAttributes());
                    break;
                }
                default: {
                    System.out.println("Unexpected packet type returned: " + n);
                    break;
                }
            }
        }
        catch (final ClientReceiveException ex5) {
            System.err.println("Radius authentication failed: " + ex5.getMessage());
            System.exit(1);
        }
        catch (final ClientSendException ex6) {
            System.err.println("Radius authentication failed: " + ex6.getMessage());
            System.exit(1);
        }
        catch (final SocketException ex7) {
            System.err.println("Radius authentication failed: " + ex7.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
    
    private static void a() {
        try {
            final InetAddress byName = InetAddress.getByName(radauth.a);
            if (radauth.c == null) {
                radauth.i = InetAddress.getLocalHost();
            }
            final EAPMD5Client eapmd5Client = new EAPMD5Client(radauth.d.getBytes(), byName, radauth.g, radauth.b, 10000, radauth.i, radauth.c);
            new A();
            new Microsoft();
            new Cisco();
            eapmd5Client.setDebug(true);
            System.out.println("\n-------------------------------- Authentication -------------------------------");
            if (eapmd5Client.authenticate(radauth.e.getBytes())) {
                System.out.println("EAPMD5 worked.");
            }
            else {
                System.out.println("EAPMD5 failed.");
            }
            System.out.println("Attributes returned from server:\n" + eapmd5Client.getAttributes());
        }
        catch (final UnknownHostException ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(1);
        }
        catch (final EAPException ex2) {
            System.out.println("Error: " + ex2.getMessage());
            System.exit(1);
        }
        catch (final Exception ex3) {
            ex3.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
    
    private static byte[] a(final RADIUSClient radiusClient, final AttributeList commonAttributes) throws ClientReceiveException, ClientSendException, SocketException {
        byte[] sessionKey = new byte[0];
        try {
            final LEAPCLIENT leapclient = (LEAPCLIENT)radiusClient.getModuleInstance("LEAPCLIENT", radiusClient);
            leapclient.setUserName(radauth.d);
            leapclient.setLEAPIdentity(radauth.d);
            leapclient.setPassword(Util.toUTF8(radauth.e));
            leapclient.setCommonAttributes(commonAttributes);
            if (leapclient.authenticate()) {
                System.out.println("LEAP worked.");
                sessionKey = leapclient.getSessionKey();
                System.out.println("Session key: " + Util.toHexString(sessionKey));
            }
            else {
                System.out.println("LEAP failed.");
            }
        }
        catch (final RADIUSModuleException ex) {
            ex.printStackTrace();
        }
        catch (final EAPException ex2) {
            ex2.printStackTrace();
        }
        return sessionKey;
    }
    
    static void a(final String[] array) {
        int n = 0;
        String substring = null;
        if (array.length < 6) {
            a("Too few arguments");
        }
        radauth.d = array[n++];
        radauth.e = array[n++];
        radauth.h = array[n++];
        final String a = array[n++];
        final int index;
        if ((index = a.indexOf(58)) >= 0) {
            radauth.a = a.substring(0, index);
            try {
                substring = a.substring(index + 1);
                radauth.g = Integer.parseInt(substring);
            }
            catch (final NumberFormatException ex) {
                a("Server port number [" + substring + " not a number.");
            }
        }
        else {
            radauth.a = a;
            radauth.g = 1812;
        }
        try {
            substring = array[n++];
            radauth.f = Integer.parseInt(substring);
        }
        catch (final NumberFormatException ex2) {
            a("Nas-Port number [" + substring + " not a number.");
        }
        radauth.b = array[n++];
        if (array.length == 7) {
            radauth.c = array[n++];
        }
    }
    
    static void a(final String s) {
        System.out.println("radauth login password auth type server[:port default is 1812] nas_port_id secretkey [nasname]\nReason " + s);
        System.out.println("Auth type may be PAP, CHAP, EAPMD5, MSCHAP or MSCHAP2.");
        System.out.println();
        System.out.println("E.g. radauth michael test PAP 192.168.1.1 1 testsecret");
        System.exit(1);
    }
}
