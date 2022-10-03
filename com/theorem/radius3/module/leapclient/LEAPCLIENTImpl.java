package com.theorem.radius3.module.leapclient;

import com.theorem.radius3.ClientReceiveException;
import java.net.SocketException;
import com.theorem.radius3.VendorSpecific;
import com.theorem.radius3.Attribute;
import com.theorem.radius3.RADIUSEncrypt;
import com.theorem.radius3.PacketType;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.EAPPacket;
import com.theorem.radius3.EAPException;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.RADIUSException;
import com.theorem.radius3.RADIUSClient;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.MSChap;
import com.theorem.radius3.module.LEAPCLIENT;

public class LEAPCLIENTImpl extends LEAPCLIENT
{
    public final byte LEAPVersion = 1;
    public static final int NO_STATE = 0;
    public static final int SEND_SUCCESS = 8;
    private boolean a;
    private LEAPPacket b;
    private int c;
    private byte[] d;
    private byte[] e;
    private byte[] f;
    private MSChap g;
    private int h;
    private byte[] i;
    private byte[] j;
    private AttributeList k;
    private byte[] l;
    private int m;
    
    public LEAPCLIENTImpl(final RADIUSClient a) throws RADIUSException {
        super(a);
        this.c = 1;
        this.m = 0;
        this.g = new MSChap();
        super.a = a;
        this.a = a.getDebugIndicator();
        this.h = 0;
        this.k = new AttributeList();
    }
    
    public final void setUserName(final String s) {
        this.f = Util.toUTF8(s);
        if (this.e == null) {
            this.e = Util.toUTF8(s);
        }
    }
    
    public final void setLEAPIdentity(final String s) {
        this.e = Util.toUTF8(s);
        if (this.e == null) {
            this.e = Util.toUTF8(s);
        }
    }
    
    public final void setPassword(final byte[] d) {
        this.d = d;
    }
    
    public final void setCommonAttributes(final AttributeList list) {
        this.k.mergeAttributes(list);
    }
    
    public final boolean authenticate() throws ClientSendException, SocketException, ClientReceiveException, EAPException {
        Attribute[] attributeArray = null;
        if (this.d == null || this.e == null || this.f == null) {
            throw new EAPException("Missing password, LEAP identity, or RADIUS User-Name.");
        }
        if (!this.k.exists(1)) {
            this.k.addAttribute(1, this.f);
        }
        while (true) {
            if (this.a) {
                super.a.logToDebug("LEAPClient.State is " + this.a() + "\n");
            }
            AttributeList list = null;
            switch (this.c) {
                case 1: {
                    list = new LEAPPacket().createIdentityResponse(this.h, this.e);
                    if (this.a) {
                        super.a.logToDebug("LEAPClient.Sending Identity packet, identity (" + Util.toUTF8(this.e) + ")\n");
                    }
                    this.c = 3;
                    break;
                }
                case 2: {
                    if (this.a) {
                        super.a.logToDebug("Received EAP type " + this.a() + "\n");
                    }
                    if (++this.m > 1) {
                        throw new EAPException("More than one NAK received.");
                    }
                    list = new EAPPacket().createNAKResponse(this.b.getPacketIdentifier(), new byte[] { 17 });
                    this.c = 3;
                    break;
                }
                case 4: {
                    try {
                        final byte[] ntChallengeResponse = this.g.NtChallengeResponse(this.i, this.d, false);
                        if (this.a) {
                            super.a.logToDebug("LEAPClient.Creating ntHash using challenge: " + Util.toHexString(this.i) + " to create response " + Util.toHexString(ntChallengeResponse) + "\n");
                        }
                        this.b = new LEAPPacket(this.h, 2, ntChallengeResponse);
                        list = this.b.toAttributeList();
                        if (this.a) {
                            super.a.logToDebug("LEAPClient.Sending NtChallengeResponse: " + Util.toHexString(ntChallengeResponse) + " length " + ntChallengeResponse.length + "\n");
                        }
                        this.c = 5;
                        break;
                    }
                    catch (final RADIUSException ex) {
                        throw new ClientSendException(ex.getMessage());
                    }
                }
                case 6: {
                    final MSChap g = this.g;
                    this.j = MSChap.createChallenge();
                    this.b = new LEAPPacket(this.h, 1, this.j);
                    list = this.b.toAttributeList();
                    if (this.a) {
                        super.a.logToDebug("LEAPClient. Sending APC challenge " + Util.toHexString(this.j) + "\n");
                    }
                    this.c = 7;
                    break;
                }
                default: {
                    throw new EAPException("Uknown state found while receiving data: " + this.a());
                }
            }
            list.mergeAttributes(this.k);
            list.mergeAttributes(attributeArray);
            super.a.reset();
            final int authenticate = super.a.authenticate(list);
            if (authenticate == 3) {
                if (this.a) {
                    super.a.logToDebug("LEAPClient.Access reject received \n");
                }
                return false;
            }
            final AttributeList attributes = super.a.getAttributes();
            attributeArray = attributes.getAttributeArray(24);
            this.b = new LEAPPacket(attributes);
            ++this.h;
            if (this.h != this.b.getPacketIdentifier()) {
                throw new EAPException("LEAP: incorrect packet Identifier " + this.b.getPacketIdentifier() + ", expecting " + this.h);
            }
            if (this.a) {
                super.a.logToDebug("LEAPClient.State is " + this.a() + "\n");
            }
            switch (this.c) {
                case 3: {
                    if (this.b.getType() != 17) {
                        this.c = 2;
                        continue;
                    }
                    if (this.b.getCode() != 1) {
                        throw new EAPException("LEAP: Expected REQUEST, found " + this.b);
                    }
                    if (this.b.getType() != 17) {
                        throw new EAPException("LEAP: Expected type LEAP, found " + this.b.getTypeName());
                    }
                    if (authenticate != 11) {
                        String s = "LEAP: Expected Access-Challenge, found " + new PacketType().getName(authenticate);
                        if (authenticate == 0) {
                            s = s + " - " + super.a.getErrorString();
                        }
                        throw new EAPException(s + "\n");
                    }
                    final byte[] leapData = this.b.getLEAPData();
                    this.i = new byte[8];
                    try {
                        System.arraycopy(leapData, 0, this.i, 0, 8);
                    }
                    catch (final IndexOutOfBoundsException ex2) {
                        throw new EAPException("LEAP " + this.a() + " Challenge length error. expecting " + 8 + " EAP data contained " + leapData.length + " bytes");
                    }
                    if (this.a) {
                        super.a.logToDebug("LEAPClient: Sending challenge " + Util.toHexString(this.i) + " length " + this.i.length + "\n");
                    }
                    this.c = 4;
                    continue;
                }
                case 5: {
                    if (this.b.getCode() != 3) {
                        throw new EAPException("LEAP: Expected SUCCESS, got " + this.b);
                    }
                    if (this.a) {
                        super.a.logToDebug("LEAPClient: EAP Success received.\n");
                    }
                    this.c = 6;
                    continue;
                }
                case 7: {
                    if (authenticate != 2) {
                        String s2 = "LEAP: Expected Access-Accept, found " + new PacketType().getName(authenticate);
                        if (authenticate == 0) {
                            s2 = s2 + " - " + super.a.getErrorString();
                        }
                        throw new EAPException(s2 + "\n");
                    }
                    final VendorSpecific[] vendorSpecific = attributes.getVendorSpecific(9);
                    if (vendorSpecific.length == 0 || vendorSpecific.length > 1) {
                        String string;
                        if (vendorSpecific.length == 0) {
                            string = "LEAP: Expecting Cisco VSA with session key, but none found in final packet.";
                        }
                        else {
                            string = "LEAP: Too many Cisco VSA's  expecting 1, found " + vendorSpecific.length;
                        }
                        if (this.a) {
                            super.a.logToDebug(string + "\n");
                        }
                        throw new EAPException(string);
                    }
                    if (this.a) {
                        super.a.logToDebug("LEAPClient. Cisco VSA:\n" + vendorSpecific[0]);
                    }
                    final byte[] attributeData = vendorSpecific[0].getAttributeAt(0).getAttributeData();
                    final byte[] array = new byte[attributeData.length - 17];
                    try {
                        System.arraycopy(attributeData, 17, array, 0, array.length);
                    }
                    catch (final IndexOutOfBoundsException ex3) {
                        throw new EAPException("LEAP " + this.a() + " Cisco VSA data length error. expecting " + array.length + " VSA data contained " + attributeData.length + " bytes");
                    }
                    this.l = RADIUSEncrypt.decipherTunnelPassword(array, super.a.getSecret(), super.a.getRequestAuthenticator());
                    this.b = new LEAPPacket(attributes);
                    final byte[] leapData2 = this.b.getLEAPData();
                    final byte[] array2 = new byte[24];
                    System.arraycopy(leapData2, 0, array2, 0, 24);
                    final byte[] hashNtPasswordHash = this.g.HashNtPasswordHash(this.g.NtPasswordHash(this.g.toUnicode(this.d)));
                    byte[] challengeResponse;
                    try {
                        challengeResponse = this.g.ChallengeResponse(this.j, hashNtPasswordHash);
                    }
                    catch (final RADIUSException ex4) {
                        throw new ClientSendException(ex4.getMessage());
                    }
                    if (!Util.cmp(challengeResponse, array2)) {
                        throw new EAPException("LEAP: AP response [" + Util.toHexString(array2) + " does not match calculated response [" + Util.toHexString(challengeResponse) + "]");
                    }
                    if (this.a) {
                        super.a.logToDebug("LEAPClient. Session key: length " + this.l.length + ", " + Util.toHexString(this.l) + "\n");
                    }
                    this.c = 0;
                    return true;
                }
                default: {
                    throw new EAPException("LEAP: Internal Error - unknown state: " + this.c);
                }
            }
        }
    }
    
    public final byte[] getSessionKey() {
        return this.l;
    }
    
    private final String a() {
        String string = null;
        switch (this.c) {
            case 7: {
                string = "SEND_APC_RESPONSE_STEP6";
                break;
            }
            case 3: {
                string = "CLIENT_CHALLENGE_STEP2";
                break;
            }
            case 0: {
                string = "NO_STATE";
                break;
            }
            default: {
                string = "Unknown (" + this.c + ")";
                break;
            }
            case 1: {
                string = "IDENTITY_STEP1";
                break;
            }
            case 4: {
                string = "MSCHAP_RESPONSE_STEP3";
                break;
            }
            case 6: {
                string = "APC_REQUEST_STEP5";
                break;
            }
            case 2: {
                string = "SEND_NAK";
                break;
            }
            case 5: {
                string = "SEND_SUCCESS_STEP4";
                break;
            }
        }
        return string;
    }
}
