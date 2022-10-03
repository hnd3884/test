package com.theorem.radius3.eap;

import java.io.IOException;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.ClientReceiveException;
import java.net.SocketException;
import com.theorem.radius3.EAPException;
import java.net.InetAddress;
import com.theorem.radius3.radutil.RadRand;
import com.theorem.radius3.EAPPacket;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.RADIUSClient;

public class EAPClient
{
    protected byte[] a;
    public RADIUSClient radClient;
    private AttributeList b;
    private AttributeList c;
    private AttributeList d;
    private boolean e;
    private EAPPacket f;
    private RadRand g;
    private InetAddress h;
    private String i;
    private InetAddress j;
    private int k;
    private String l;
    private int m;
    
    public EAPClient(final RADIUSClient radClient, final AttributeList b) throws EAPException {
        this.e = false;
        this.g = new RadRand();
        this.radClient = radClient;
        this.b = b;
    }
    
    public EAPClient(final byte[] a, final RADIUSClient radClient, final InetAddress h, final String i) throws EAPException {
        this.e = false;
        this.g = new RadRand();
        this.a = a;
        this.radClient = radClient;
        this.h = h;
        this.i = i;
        this.reset(a);
    }
    
    public EAPClient(final byte[] a, final InetAddress j, final int k, final String l, final int m, final InetAddress h, final String i) throws EAPException {
        this.e = false;
        this.g = new RadRand();
        this.h = h;
        this.i = i;
        this.j = j;
        this.k = k;
        this.l = l;
        this.m = m;
        this.reset(this.a = a);
    }
    
    public final void reset(final byte[] array) throws EAPException {
        try {
            if (this.radClient == null) {
                (this.radClient = new RADIUSClient(this.j, this.k, this.l, this.m)).setDebug(this.e);
            }
            else {
                this.radClient.reset();
            }
        }
        catch (final SocketException ex) {
            throw new EAPException(ex.getMessage());
        }
        if (this.b != null) {
            return;
        }
        this.b = new AttributeList();
        if (this.h != null) {
            this.b.addAttribute(4, this.h);
        }
        if (this.i != null) {
            this.b.addAttribute(32, this.i);
        }
        this.b.addAttribute(1, array);
    }
    
    public final void debug(final boolean b) {
        this.e = b;
        this.radClient.setDebug(b);
    }
    
    public final void addAttributes(final AttributeList c) {
        this.c = c;
    }
    
    public final int send(final AttributeList list) throws EAPException {
        final AttributeList list2 = new AttributeList();
        list2.mergeAttributes(this.b);
        list2.mergeAttributes(list);
        list2.mergeAttributes(this.c);
        if (this.d != null) {
            list2.mergeAttributes(this.d.getAttributeList(24));
        }
        int authenticate;
        try {
            this.radClient.reset();
            authenticate = this.radClient.authenticate(list2);
            switch (authenticate) {
                case 3: {
                    throw new EAPException("Rejection received");
                }
                case 0: {
                    throw new EAPException("Malformed packet received.");
                }
                case 2:
                case 11: {
                    break;
                }
                default: {
                    throw new EAPException("Malformed packet received - unexpected packet type of " + authenticate + " was returned from the server, error = " + this.radClient.getErrorString());
                }
            }
        }
        catch (final SocketException ex) {
            throw new EAPException(ex.getMessage());
        }
        catch (final ClientReceiveException ex2) {
            throw new EAPException(ex2.getMessage());
        }
        catch (final ClientSendException ex3) {
            throw new EAPException(ex3.getMessage());
        }
        this.d = this.radClient.getAttributes();
        final EAPPacket eapPacket = new EAPPacket(list);
        this.f = new EAPPacket(this.d);
        if (this.f.getType() != 1 && this.f.getCode() == 1) {
            return authenticate;
        }
        if (this.f.getCode() == 1) {
            throw new EAPException("Unexpected EAP REQUEST packet.");
        }
        if (this.f.getCode() == 2 && eapPacket.getPacketIdentifier() != this.f.getPacketIdentifier()) {
            throw new EAPException("Packet Identifier doesn't match on the sent and received packets (sent " + eapPacket.getPacketIdentifier() + " received " + this.f.getPacketIdentifier() + ")");
        }
        return authenticate;
    }
    
    public final AttributeList getAttributes() {
        return this.d;
    }
    
    public final EAPPacket getEAPPacket() throws EAPException {
        if (this.f == null) {
            throw new EAPException("No EAP packet received.");
        }
        return this.f;
    }
    
    public final int createPacketId() {
        return this.g.nextInt() & 0xFF;
    }
    
    public final void setDebug(final boolean b, final String s) throws IOException {
        this.radClient.setDebug(b, s);
    }
    
    public final boolean setDebug(final boolean debug) {
        return this.radClient.setDebug(debug);
    }
    
    public final void logToDebug(final String s) {
        this.radClient.logToDebug(s);
    }
}
