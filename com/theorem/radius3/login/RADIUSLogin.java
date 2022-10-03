package com.theorem.radius3.login;

import java.math.BigInteger;
import com.theorem.radius3.radutil.ByteIterator;
import com.theorem.radius3.RADIUSException;
import com.theorem.radius3.EAPException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.PacketType;
import java.net.InetAddress;
import com.theorem.radius3.eap.EAPMD5Client;
import com.theorem.radius3.radutil.Util;
import javax.security.auth.callback.TextInputCallback;
import java.net.UnknownHostException;
import java.net.SocketException;
import com.theorem.radius3.RADIUSClient;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import com.theorem.radius3.Attribute;
import java.util.Iterator;
import com.theorem.radius3.WISPr;
import com.theorem.radius3.Microsoft;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.A;
import com.theorem.radius3.AttributeList;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import javax.security.auth.spi.LoginModule;

public final class RADIUSLogin implements LoginModule
{
    public static final String AUTH_PAP = "PAP";
    public static final String AUTH_CHAP = "CHAP";
    public static final String AUTH_MSCHAP = "MSCHAP";
    public static final String AUTH_MSCHAP2 = "MSCHAP2";
    public static final String AUTH_EAPMD5 = "EAPMD5";
    private static final String[] a;
    protected Subject b;
    protected CallbackHandler c;
    private Map d;
    private Map e;
    private boolean f;
    private String g;
    private String h;
    private int i;
    private String j;
    private int k;
    private String l;
    private String m;
    private String n;
    private AttributeList o;
    private String p;
    private boolean q;
    private boolean r;
    private String s;
    private RADIUSPrincipal t;
    
    public RADIUSLogin() {
        this.f = false;
        this.p = null;
        this.q = false;
        this.r = false;
    }
    
    public final void initialize(final Subject b, final CallbackHandler c, final Map d, final Map e) {
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = e.get("debug").equals("true");
        this.g = e.get("debugFile");
        this.h = e.get("server");
        this.j = e.get("secret");
        this.l = e.get("namePrompt");
        if (this.l == null) {
            this.l = "Name";
        }
        this.m = e.get("passwordPrompt");
        if (this.m == null) {
            this.m = "Password";
        }
        this.n = e.get("authtype");
        if (this.n == null) {
            this.n = "PAP";
        }
        final String s = e.get("port");
        try {
            this.i = Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            this.i = 1812;
        }
        final String s2 = e.get("timeout");
        try {
            this.k = Integer.parseInt(s2) * 1000;
        }
        catch (final NumberFormatException ex2) {
            this.k = 10000;
        }
        new A();
        new Cisco();
        new Microsoft();
        new WISPr();
        this.o = new AttributeList();
        final Iterator iterator = e.keySet().iterator();
        while (iterator.hasNext()) {
            final String s3 = (String)iterator.next();
            final String s4 = e.get(s3);
            try {
                final Attribute a = this.a(s3, s4);
                if (a == null) {
                    continue;
                }
                this.o.addAttribute(a);
            }
            catch (final Exception ex3) {
                this.p = "Parse error in attribute: " + s3;
            }
        }
    }
    
    public final boolean login() throws LoginException {
        if (this.p != null) {
            throw new LoginException("Configuration error in RADIUSLogin.config: " + this.p);
        }
        if (this.c == null) {
            throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
        }
        if (this.h == null || this.j == null) {
            throw new LoginException("Missing or bad RADIUS options. Server or secret are missing.");
        }
        String name;
        char[] password;
        AttributeList requestAttributes;
        try {
            final RADIUSNameCallback radiusNameCallback = new RADIUSNameCallback(this.l);
            final RADIUSPasswordCallback radiusPasswordCallback = new RADIUSPasswordCallback(this.m, false);
            final SetAccessRequestAttributes setAccessRequestAttributes = new SetAccessRequestAttributes();
            this.c.handle(new Callback[] { radiusNameCallback, radiusPasswordCallback, setAccessRequestAttributes });
            name = radiusNameCallback.getName();
            if (name == null) {
                throw new LoginException("Error: No user name specified.");
            }
            password = radiusPasswordCallback.getPassword();
            radiusPasswordCallback.clearPassword();
            if (password == null) {
                password = new char[0];
            }
            requestAttributes = setAccessRequestAttributes.getRequestAttributes();
        }
        catch (final IOException ex) {
            final ClientException ex2 = new ClientException();
            ex2.a(ex);
            this.a(ex2);
            throw new LoginException(ex.getMessage());
        }
        catch (final UnsupportedCallbackException ex3) {
            throw new LoginException("Error: " + ex3.getCallback().toString() + " callback not understood by application.");
        }
        RADIUSClient radiusClient;
        try {
            radiusClient = new RADIUSClient(this.h, this.i, this.j, this.k);
            radiusClient.setDebug(this.f);
        }
        catch (final SocketException ex4) {
            final ClientException ex5 = new ClientException();
            ex5.a(ex4);
            this.a(ex5);
            throw new LoginException("SocketException creating RADIUSClient: " + ex4.getMessage());
        }
        catch (final UnknownHostException ex6) {
            final ClientException ex7 = new ClientException();
            ex7.a(ex6);
            this.a(ex7);
            throw new LoginException("UnknownHostException creating RADIUSClient: " + ex6.getMessage());
        }
        int n = this.a(radiusClient, name, password, requestAttributes);
        do {
            switch (n) {
                case 3: {
                    final AccessReject accessReject = new AccessReject();
                    accessReject.c = radiusClient.getAttributes();
                    this.a(accessReject);
                    throw new LoginException("Access rejected.");
                }
                case 0: {
                    final AccessBadPacket accessBadPacket = new AccessBadPacket();
                    accessBadPacket.c = radiusClient.getAttributes();
                    accessBadPacket.a(radiusClient.getErrorString(), radiusClient.getError());
                    this.a(accessBadPacket);
                    throw new LoginException("Bad RADIUS response packet.");
                }
                case 11: {
                    final AttributeList attributes = radiusClient.getAttributes();
                    final TextInputCallback textInputCallback = new TextInputCallback(attributes.getStringAttribute(18));
                    final Callback[] array = { textInputCallback };
                    try {
                        this.c.handle(array);
                    }
                    catch (final IOException ex8) {
                        throw new LoginException("IOException handling challenge callback: " + ex8.getMessage());
                    }
                    catch (final UnsupportedCallbackException ex9) {
                        throw new LoginException("UnsupportedCallbackException handling challenge callback: " + ex9.getMessage());
                    }
                    final String text = textInputCallback.getText();
                    if (text == null) {
                        throw new LoginException("No response was provided to the challenge.");
                    }
                    final AttributeList list = new AttributeList(requestAttributes);
                    list.addAttribute(24, attributes.getStringAttribute(24));
                    n = this.a(radiusClient, name, text.toCharArray(), list);
                    continue;
                }
                case 2: {
                    this.s = name;
                    this.q = true;
                    final AccessAccept accessAccept = new AccessAccept();
                    accessAccept.c = radiusClient.getAttributes();
                    this.a(accessAccept);
                    continue;
                }
                default: {
                    throw new LoginException("Unexpected packet type returned: " + n);
                }
            }
        } while (!this.q);
        return true;
    }
    
    private final int a(final RADIUSClient radiusClient, final String s, final char[] array, final AttributeList list) throws LoginException {
        final AttributeList list2 = new AttributeList();
        list2.mergeAttributes(this.o);
        if (list != null) {
            list2.mergeAttributes(list);
        }
        try {
            radiusClient.reset();
        }
        catch (final SocketException ex) {
            throw new LoginException("SocketException resetting RADIUS Client: " + ex.getMessage());
        }
        final byte[] bytes = RADIUSClient.getBytes(new String(array));
        int n;
        try {
            if (this.n.equals("PAP")) {
                list2.addAttribute(1, s);
                list2.addAttribute(2, radiusClient.encryptPassword(bytes));
                n = radiusClient.authenticate(list2);
            }
            else if (this.n.equals("CHAP")) {
                list2.addAttribute(1, s);
                radiusClient.createCHAP(bytes, list2);
                n = radiusClient.authenticate(list2);
            }
            else if (this.n.equals("MSCHAP")) {
                radiusClient.createMSCHAP(bytes, list2);
                list2.addAttribute(1, s);
                n = radiusClient.authenticate(list2);
            }
            else if (this.n.equals("MSCHAP2")) {
                radiusClient.createMSCHAP2(RADIUSClient.getBytes(s), bytes, list2);
                list2.addAttribute(1, s);
                n = radiusClient.authenticate(list2);
            }
            else {
                if (!this.n.equals("EAPMD5")) {
                    throw new LoginException("Uknown authtype of " + this.n);
                }
                final boolean authenticate = new EAPMD5Client(Util.toUTF8(s), radiusClient, null, null).authenticate(Util.toUTF8(new String(array)));
                n = radiusClient.getPacketType();
                if (authenticate && n != 2) {
                    throw new LoginException("EAPMD5: incorrect result EAP authentication succesful and RADIUS packet type is " + new PacketType().getName(n));
                }
            }
        }
        catch (final ClientSendException ex2) {
            final ClientException ex3 = new ClientException();
            ex3.a(ex2);
            this.a(ex3);
            throw new LoginException("Failed with a ClientSendExcepton (" + ex2.getMessage() + ")");
        }
        catch (final ClientReceiveException ex4) {
            final ClientException ex5 = new ClientException();
            ex5.a(ex4);
            this.a(ex5);
            throw new LoginException("Failed with a ClientReceiveException (" + ex4.getMessage() + ")");
        }
        catch (final EAPException ex6) {
            final ClientException ex7 = new ClientException();
            ex7.a(ex6);
            this.a(ex7);
            throw new LoginException("Failed with a EAPException (" + ex6.getMessage() + ")");
        }
        catch (final RADIUSException ex8) {
            final ClientException ex9 = new ClientException();
            ex9.a(ex8);
            this.a(ex9);
            throw new LoginException("Failed with a RADIUSException (" + ex8.getMessage() + ")");
        }
        if (this.f) {
            final AttributeList attributes = radiusClient.getAttributes();
            System.out.println("Result: " + n);
            System.out.println("Response attributes: " + attributes);
        }
        return n;
    }
    
    public final boolean commit() throws LoginException {
        if (!this.q) {
            return false;
        }
        this.t = new RADIUSPrincipal(this.s);
        if (!this.b.getPrincipals().contains(this.t)) {
            this.b.getPrincipals().add(this.t);
        }
        if (this.f) {
            System.out.println("RADIUSLogin: added RADIUSPrincipal to Subject");
        }
        this.s = null;
        return this.r = true;
    }
    
    public final boolean abort() throws LoginException {
        if (!this.q) {
            return false;
        }
        if (this.q && !this.r) {
            this.q = false;
            this.s = null;
            this.t = null;
        }
        else {
            this.logout();
        }
        return true;
    }
    
    public final boolean logout() throws LoginException {
        this.b.getPrincipals().remove(this.t);
        this.q = false;
        this.q = this.r;
        this.s = null;
        this.t = null;
        return true;
    }
    
    private final Attribute a(final String s, final String s2) throws Exception {
        for (int i = 0; i < RADIUSLogin.a.length; ++i) {
            if (s.equals(RADIUSLogin.a[i])) {
                return null;
            }
        }
        final int tag = Attribute.getTag(s);
        if (tag == 0) {
            throw new Exception("Uknown attribute " + s);
        }
        switch (s2.charAt(0)) {
            case '#': {
                if (Character.isDigit(s2.charAt(1))) {
                    try {
                        return new Attribute(tag, ByteIterator.decoct(Integer.parseInt(s2.substring(1))));
                    }
                    catch (final NumberFormatException ex) {
                        throw new Exception("Attribute value is not a number: " + s + "=" + s2);
                    }
                }
                final int valueName = Attribute.getValueName(tag, s2.substring(1));
                if (valueName == 0) {
                    throw new Exception("Can't find integer representation value for : " + s + "=" + s2);
                }
                return new Attribute(tag, ByteIterator.decoct(valueName));
            }
            case '@': {
                try {
                    return new Attribute(tag, InetAddress.getByName(s2.substring(1)).getAddress());
                }
                catch (final UnknownHostException ex2) {
                    throw new Exception("Attribute value is not an IP Address: " + s + "=" + s2);
                }
                break;
            }
        }
        if (!s2.startsWith("0x")) {
            if (!s2.startsWith("0X")) {
                return new Attribute(tag, RADIUSClient.getBytes(s2));
            }
        }
        try {
            final byte[] byteArray = new BigInteger(s2.substring(2), 16).toByteArray();
            if (byteArray.length > 253) {
                throw new Exception("Attribute binary value is too long (max=253): " + s + "=" + s2);
            }
            return new Attribute(tag, byteArray);
        }
        catch (final NumberFormatException ex3) {
            throw new Exception("Attribute value is not a hex string: " + s + "=" + s2);
        }
        return new Attribute(tag, RADIUSClient.getBytes(s2));
    }
    
    private final void a(final RADIUSCallback radiusCallback) throws LoginException {
        radiusCallback.setReady(true);
        final Callback[] array = { (Callback)radiusCallback };
        try {
            this.c.handle(array);
            radiusCallback.setReady(false);
        }
        catch (final IOException ex) {
            radiusCallback.setReady(false);
            throw new LoginException("IOException handling challenge callback: " + ex.getMessage());
        }
        catch (final UnsupportedCallbackException ex2) {
            radiusCallback.setReady(false);
            throw new LoginException("UnsupportedCallbackException handling challenge callback: " + ex2.getMessage());
        }
    }
    
    static {
        a = new String[] { "debug", "debugFile", "server", "port", "secret", "authtype", "timeout", "passwordPrompt", "namePrompt" };
    }
}
