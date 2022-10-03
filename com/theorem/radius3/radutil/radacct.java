package com.theorem.radius3.radutil;

import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.AttributeList;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.InetAddress;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.Microsoft;
import com.theorem.radius3.A;
import com.theorem.radius3.RADIUSClient;

public class radacct
{
    static String a;
    static String b;
    static String c;
    static String d;
    static String e;
    static int f;
    static int g;
    static String h;
    static int i;
    
    public static void main(final String[] array) {
        a(array);
        RADIUSClient radiusClient = null;
        InetAddress byName = null;
        System.out.println("Radtest running RADIUS client version 3.43p RADIUS Secure Version");
        try {
            radiusClient = new RADIUSClient(radacct.a, radacct.i, radacct.b, 5000);
            radiusClient.setDebug(true);
            new A();
            new Microsoft();
            new Cisco();
            if (radacct.c != null) {
                byName = InetAddress.getByName(radacct.c);
            }
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
        System.out.println("Accounting using User-Name: " + radacct.e + ", Accounting-Session-Id " + radacct.h);
        try {
            AttributeList attributes = new AttributeList();
            if (radacct.c != null) {
                attributes.addAttribute(4, byName);
            }
            if (radacct.d != null) {
                attributes.addAttribute(32, radacct.d);
            }
            for (int i = 0; i < 2; ++i) {
                if (radacct.c != null) {
                    attributes.addAttribute(4, byName);
                }
                if (radacct.d != null) {
                    attributes.addAttribute(32, radacct.d);
                }
                attributes.addAttribute(44, radacct.h);
                attributes.addAttribute(1, radacct.e);
                if (i == 0) {
                    attributes.addAttribute(40, 1);
                    System.out.println("Sending Start Accounting:\n" + attributes);
                }
                else {
                    radiusClient.reset();
                    attributes.addAttribute(40, 2);
                    attributes.addAttribute(42, radacct.f);
                    attributes.addAttribute(43, radacct.g);
                    System.out.println("Sending Stop Accounting:\n" + attributes);
                }
                final int accounting = radiusClient.accounting(attributes);
                switch (accounting) {
                    case 0: {
                        System.out.println("Received bad packet: " + radiusClient.getErrorString());
                        break;
                    }
                    case 5: {
                        System.out.println("Accounted");
                        attributes = radiusClient.getAttributes();
                        System.out.println("Attributes returned from server:\n" + attributes);
                        break;
                    }
                    default: {
                        System.out.println("Unexpected packet type returned: " + accounting);
                        break;
                    }
                }
            }
        }
        catch (final SocketException ex3) {
            System.err.println("Radius accounting failed: " + ex3.getMessage());
            System.exit(1);
        }
        catch (final ClientReceiveException ex4) {
            System.err.println("Radius accounting failed: " + ex4.getMessage());
            System.exit(1);
        }
        catch (final ClientSendException ex5) {
            System.err.println("Radius accounting failed: " + ex5.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
    
    static void a(final String[] array) {
        int n = 0;
        String substring = null;
        if (array.length < 6) {
            a("Too few arguments");
        }
        radacct.e = array[n++];
        try {
            substring = array[n++];
            radacct.f = Integer.parseInt(substring);
        }
        catch (final NumberFormatException ex) {
            a("input count [" + substring + " not a number.");
        }
        try {
            substring = array[n++];
            radacct.g = Integer.parseInt(substring);
        }
        catch (final NumberFormatException ex2) {
            a("output count [" + substring + " not a number.");
        }
        radacct.h = array[n++];
        final String a = array[n++];
        final int index;
        if ((index = a.indexOf(58)) >= 0) {
            radacct.a = a.substring(0, index);
            try {
                substring = a.substring(index + 1);
                radacct.i = Integer.parseInt(substring);
            }
            catch (final NumberFormatException ex3) {
                a("Server port number [" + substring + " not a number.");
            }
        }
        else {
            radacct.a = a;
            radacct.i = 1813;
        }
        radacct.b = array[n++];
        if (array.length == 7) {
            radacct.d = array[n++];
        }
    }
    
    static void a(final String s) {
        System.out.println("radacct User-Name  inputCount ouputCount sessionId server[:port default is 1812]  secret [nasname]\nReason " + s);
        System.out.println();
        System.out.println("E.g. radacct michael 0000 10200 Acct-Zither 192.168.1.1 seversecret NAS1");
        System.exit(1);
    }
}
