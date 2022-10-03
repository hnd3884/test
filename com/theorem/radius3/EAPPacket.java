package com.theorem.radius3;

import java.security.MessageDigest;
import com.theorem.radius3.radutil.MD5Digest;
import com.theorem.radius3.radutil.ByteIterator;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.radutil.RadRand;

public class EAPPacket
{
    public static final int CODE_REQUEST = 1;
    public static final int CODE_RESPONSE = 2;
    public static final int CODE_SUCCESS = 3;
    public static final int CODE_FAILURE = 4;
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_IDENTITY = 1;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_NAK = 3;
    public static final int TYPE_MD5 = 4;
    public static final int TYPE_OTP = 5;
    public static final int TYPE_GTC = 6;
    public static final int TYPE_TLS = 13;
    public static final int TYPE_PEAP = 25;
    public static final int TYPE_LEAP = 17;
    public static final int TYPE_TTLS = 21;
    public static final int TYPE_SIM = 18;
    public static final int TYPE_AKA = 23;
    public static final int TYPE_INTERNATIONALIZATION = 28;
    public static final int TYPE_EXPANDED = 254;
    public static final int TYPE_EXPERIMENTAL_TYPE = 255;
    public static final int MAX_ATTRIBUTE_LENGTH = 255;
    public static final int MINPACKET = 4;
    private int a;
    private int b;
    private int c;
    private int d;
    private byte[] e;
    private int f;
    private boolean g;
    private static RadRand h;
    private int i;
    private int j;
    private byte[] k;
    private int l;
    
    public EAPPacket() {
        this.d = 0;
    }
    
    public EAPPacket(final byte[] array) throws EAPException {
        this.d = 0;
        this.a(array);
    }
    
    public EAPPacket(final int a, final int b, final int n, final byte[] array) {
        this.d = 0;
        this.a = a;
        this.b = b;
        this.setData(n, array);
    }
    
    public EAPPacket(final int a, final int b, final int i, final int j, final byte[] array) {
        this.d = 0;
        this.a = a;
        this.b = b;
        this.i = i;
        this.j = j;
        this.setData(254, array);
    }
    
    public EAPPacket(final AttributeList list) throws EAPException {
        this.d = 0;
        this.a(this.a(list));
    }
    
    public final int getCode() {
        return this.a;
    }
    
    public final String getCodeName() {
        String string = null;
        switch (this.a) {
            case 3: {
                string = "SUCCESS";
                break;
            }
            case 1: {
                string = "REQUEST";
                break;
            }
            case 4: {
                string = "FAILURE";
                break;
            }
            case 2: {
                string = "RESPONSE";
                break;
            }
            default: {
                string = "UNKNOWN: " + this.a;
                break;
            }
        }
        return string + "[" + this.a + "]";
    }
    
    public final int getType() {
        return this.d;
    }
    
    public final String getTypeName() {
        return this.getTypeName(this.d);
    }
    
    public final String getTypeName(final int n) {
        String s = null;
        switch (n) {
            case 254: {
                s = "EXPANDED";
                break;
            }
            case 4: {
                s = "MD5";
                break;
            }
            case 5: {
                s = "One Time Password";
                break;
            }
            case 3: {
                s = "NAK";
                break;
            }
            case 1: {
                s = "IDENTITY";
                break;
            }
            default: {
                s = "UNKNOWN: ";
                break;
            }
            case 13: {
                s = "EAP-TLS";
                break;
            }
            case 6: {
                s = "Generic Token Card";
                break;
            }
            case 17: {
                s = "EAP-LEAP";
                break;
            }
            case 28: {
                s = "Internationalization";
                break;
            }
            case 2: {
                s = "NOTIFICATION";
                break;
            }
        }
        return s + "[" + n + "]";
    }
    
    public final int getLength() {
        return this.c;
    }
    
    public final byte[] getData() {
        return this.e;
    }
    
    public final byte[] getEAPPacketData() {
        return this.a();
    }
    
    public final String getIdentity() {
        if (this.d != 1) {
            return null;
        }
        char[] charArray;
        int n;
        for (charArray = new String(this.e).toCharArray(), n = 0; n < charArray.length && charArray[n] != '\0'; ++n) {}
        if (n < charArray.length) {
            final char[] array = new char[n];
            System.arraycopy(charArray, 0, array, 0, n);
            return new String(array);
        }
        return Util.toUTF8(this.e);
    }
    
    public final byte[] getIdentityBytes() {
        if (this.d != 1) {
            return null;
        }
        return this.e;
    }
    
    public final int getPacketIdentifier() {
        return this.b;
    }
    
    public final int getVendorId() {
        return this.i;
    }
    
    public final int getVendorType() {
        return this.j;
    }
    
    public final boolean isEAPStart() {
        return this.g;
    }
    
    public final boolean isEAPTLS() {
        return this.d == 13;
    }
    
    public final boolean isEAPProtocol(final int n) {
        return this.d == n;
    }
    
    public final AttributeList createStart(final int packetIdentifier) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(null);
        return this.toAttributeList();
    }
    
    public final void setCode(final int a) {
        this.a = a;
    }
    
    public final void setData(byte[] array) {
        if (array == null) {
            array = new byte[0];
        }
        final int length = array.length;
        this.e = new byte[length];
        if (length > 0) {
            System.arraycopy(array, 0, this.e, 0, length);
        }
        this.c = 4 + length;
    }
    
    public final void setData(final int d, byte[] array) {
        this.d = d;
        if (array == null) {
            array = new byte[0];
        }
        final int length = array.length;
        this.e = new byte[length];
        if (length > 0) {
            System.arraycopy(array, 0, this.e, 0, length);
        }
        this.c = 5 + length;
    }
    
    public final void setPacketIdentifier(final int b) {
        this.b = b;
    }
    
    protected final byte[] a() {
        final int length = this.e.length;
        int n = 4 + ((this.d != 0) ? 1 : 0) + length;
        if (this.d == 254) {
            n += 7;
        }
        (this.k = new byte[n])[0] = (byte)this.a;
        this.k[1] = (byte)this.b;
        this.k[2] = (byte)(n >>> 8);
        this.k[3] = (byte)(n & 0xFF);
        if (length > 0) {
            this.k[4] = (byte)this.d;
        }
        if (this.d != 254) {
            if (length > 0) {
                System.arraycopy(this.e, 0, this.k, 5, length);
            }
        }
        else {
            this.k[5] = (byte)(this.i >>> 16 & 0xFF);
            this.k[6] = (byte)(this.i >>> 8 & 0xFF);
            this.k[7] = (byte)this.i;
            this.k[8] = (byte)(this.j >>> 24 & 0xFF);
            this.k[9] = (byte)(this.j >>> 16 & 0xFF);
            this.k[10] = (byte)(this.j >>> 8 & 0xFF);
            this.k[11] = (byte)(this.j & 0xFF);
            if (length > 0) {
                System.arraycopy(this.e, 0, this.k, 12, length);
            }
        }
        return this.k;
    }
    
    protected final void a(final byte[] array) throws EAPException {
        this.d = 0;
        if (array == null) {
            throw new EAPException("No EAP attribute data available.");
        }
        if (array.length < 4) {
            throw new EAPException("EAP packet too small - " + array.length + " < minimum packet size (" + 4 + ")");
        }
        if (array.length == 0) {
            this.g = true;
            return;
        }
        this.g = false;
        if (array.length < 4) {
            throw new EAPException("EAP packet too small - " + array.length + " < minimum packet size (" + 4 + ")");
        }
        if (array.length == 4) {
            this.a = (array[0] & 0xFF);
            this.d = 0;
            this.b = (array[1] & 0xFF);
            this.e = new byte[0];
            return;
        }
        this.a = (array[0] & 0xFF);
        if (this.a > 4) {
            throw new EAPException("EAP code is out of range 1 to 4, value is " + this.a);
        }
        this.c = ((array[2] & 0xFF) << 8 | (array[3] & 0xFF));
        this.b = (array[1] & 0xFF);
        if (this.c != array.length) {
            throw new EAPException("EAP Packet's physical size (" + array.length + ") doesn't match packet's stated length (" + this.c + ")");
        }
        if (this.c > 4) {
            this.d = (array[4] & 0xFF);
            if (this.d == 254) {
                this.i = ByteIterator.to24(array, 5);
                this.j = ByteIterator.toInt(array, 8);
                this.f = array.length - 12;
                System.arraycopy(array, 12, this.e = new byte[this.f], 0, this.f);
                return;
            }
            if (this.c > 5) {
                this.f = array.length - 5;
                System.arraycopy(array, 5, this.e = new byte[this.f], 0, this.f);
                return;
            }
        }
        this.f = 0;
        this.e = new byte[0];
    }
    
    public final void setRequest() {
        this.a = 1;
    }
    
    public final void setResponse() {
        this.a = 2;
    }
    
    public final void setFailure() {
        this.a = 4;
    }
    
    public final void setSuccess() {
        this.a = 3;
    }
    
    public final AttributeList toAttributeList() {
        return this.a(true);
    }
    
    public final AttributeList toServerAttributeList() {
        return this.a(false);
    }
    
    private final AttributeList a(final boolean b) {
        final AttributeList list = new AttributeList();
        final byte[] a = this.a();
        int i = a.length;
        int n = 0;
        while (i > 253) {
            final byte[] array = new byte[253];
            System.arraycopy(a, n, array, 0, 253);
            list.addAttribute(79, array);
            n += 253;
            i -= 253;
        }
        if (i > 0) {
            final byte[] array2 = new byte[i];
            System.arraycopy(a, n, array2, 0, i);
            list.addAttribute(79, array2);
        }
        if (b) {
            list.addAttribute(80);
        }
        return list;
    }
    
    public final AttributeList createIdentityRequest(final int packetIdentifier, final byte[] array) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(1, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createIdentityResponse(final int packetIdentifier, final byte[] array) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(1, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createNotificationRequest(final int packetIdentifier, final byte[] array) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(2, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createNotificationResponse(final int packetIdentifier) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(2, null);
        return this.toAttributeList();
    }
    
    public final AttributeList createExpandedNAK(final int packetIdentifier, final int[] array, final int[] array2) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.i = 0;
        this.j = 3;
        final byte[] array3 = new byte[8 * array.length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            array3[n++] = -2;
            array3[n++] = (byte)(array[i] >>> 16 & 0xFF);
            array3[n++] = (byte)(array[i] >>> 8 & 0xFF);
            array3[n++] = (byte)array[i];
            array3[n++] = (byte)(array2[i] >>> 24 & 0xFF);
            array3[n++] = (byte)(array2[i] >>> 16 & 0xFF);
            array3[n++] = (byte)(array2[i] >>> 8 & 0xFF);
            array3[n++] = (byte)array2[i];
        }
        this.setData(254, array3);
        return this.toAttributeList();
    }
    
    public final AttributeList createMD5Request(final int packetIdentifier, final byte[] array) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        final int length = array.length;
        final byte[] array2 = new byte[1 + length];
        array2[0] = (byte)length;
        System.arraycopy(array, 0, array2, 1, array.length);
        this.setData(4, array2);
        return this.toAttributeList();
    }
    
    public final AttributeList createMD5Response(final int packetIdentifier, final byte[] array, final byte[] array2) {
        if (array == null) {
            return this.createNAKResponse(packetIdentifier, new byte[] { 4 });
        }
        final int length = array2.length;
        final int length2 = array.length;
        final byte[] array3 = new byte[1 + length + length2];
        array3[0] = (byte)packetIdentifier;
        System.arraycopy(array2, 0, array3, 1, length);
        System.arraycopy(array, 0, array3, 1 + length, length2);
        final MessageDigest value = MD5Digest.get();
        value.update(array3);
        final byte[] digest = value.digest();
        final byte[] array4 = new byte[digest.length + 1];
        array4[0] = (byte)digest.length;
        System.arraycopy(digest, 0, array4, 1, digest.length);
        this.setData(4, array4);
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        return this.toAttributeList();
    }
    
    public final AttributeList createOTPRequest(final int packetIdentifier, final byte[] array) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(6, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createOTPResponse(final int packetIdentifier, final byte[] array) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(6, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createGTCRequest(final int packetIdentifier, final byte[] array) {
        this.setCode(1);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(6, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createGTCResponse(final int packetIdentifier, final byte[] array) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(6, array);
        return this.toAttributeList();
    }
    
    public final AttributeList createFailure(final int packetIdentifier) {
        this.setCode(4);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(null);
        return this.toServerAttributeList();
    }
    
    public final AttributeList createSuccess(final int packetIdentifier) {
        this.setCode(3);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(null);
        return this.toServerAttributeList();
    }
    
    public final AttributeList createNAKResponse(final int packetIdentifier, final byte[] array) {
        this.setCode(2);
        this.setPacketIdentifier(packetIdentifier);
        this.setData(3, array);
        return this.toAttributeList();
    }
    
    public final boolean isNAK() {
        return this.d == 3 && this.a == 2;
    }
    
    public final boolean isRequest() {
        return this.a == 1;
    }
    
    public final boolean isResponse() {
        return this.a == 2;
    }
    
    public final boolean isSuccess() {
        return this.a == 3;
    }
    
    public final boolean isFailure() {
        return this.a == 4;
    }
    
    public final boolean isIdentity() {
        return this.d == 1;
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer("EAP Packet: ");
        if (this.l > 1) {
            sb.append("(In ").append(this.l).append(" EAP_Message attributes) ");
        }
        switch (this.a) {
            case 1: {
                sb.append("Request (").append(this.a).append(')');
                break;
            }
            default: {
                sb.append("Unknown Code (").append(this.a).append(')');
                break;
            }
            case 2: {
                sb.append("Response (").append(this.a).append(')');
                break;
            }
            case 4: {
                sb.append("Failure (").append(this.a).append(')');
                break;
            }
            case 3: {
                sb.append("Success (").append(this.a).append(')');
                break;
            }
        }
        sb.append(", ID ").append(this.b);
        sb.append(", Length: ").append(this.c).append(", ");
        switch (this.d) {
            case 28: {
                sb.append("Internationalization (").append(this.d).append(')');
                break;
            }
            case 2: {
                sb.append("Notification (").append(this.d).append(')');
                break;
            }
            default: {
                sb.append("Unknown Type(").append(this.d).append(')');
                break;
            }
            case 254: {
                sb.append("EAP-EXPANDED (").append(this.d).append(')');
                break;
            }
            case 4: {
                sb.append("MD5 (").append(this.d).append(')');
                break;
            }
            case 25: {
                sb.append("EAP-PEAP (").append(this.d).append(')');
                break;
            }
            case 13: {
                sb.append("EAP-TLS (").append(this.d).append(')');
                break;
            }
            case 3: {
                sb.append("NAK (").append(this.d).append(')');
                break;
            }
            case 6: {
                sb.append("Generic Token Card (").append(this.d).append(')');
                break;
            }
            case 1: {
                sb.append("Identity (").append(this.d).append(')');
                break;
            }
            case 17: {
                sb.append("LEAP (").append(this.d).append(')');
                break;
            }
            case 5: {
                sb.append("OneTime Password (").append(this.d).append(')');
                break;
            }
        }
        if (this.d == 13) {
            if (this.f != 0) {
                final int n = this.e[0] & 0xFF;
                sb.append(", Flag: ").append("0x").append(Integer.toHexString(n)).append('(');
                switch (n) {
                    case 192: {
                        sb.append("LM");
                        break;
                    }
                    case 0: {
                        sb.append("None");
                        break;
                    }
                    case 64: {
                        sb.append('M');
                        break;
                    }
                    case 32: {
                        sb.append('S');
                        break;
                    }
                    case 128: {
                        sb.append('L');
                        break;
                    }
                }
                sb.append(')');
                if ((n & 0x80) == 0x80 && this.f >= 3) {
                    sb.append(", Data length ").append(ByteIterator.toShort(this.e, 1));
                }
                if (this.f > 3) {
                    final byte[] array = new byte[this.f - 3];
                    System.arraycopy(this.e, 3, array, 0, array.length);
                    sb.append(", ").append(new Attribute().a(array));
                }
                return sb.toString();
            }
            sb.append(", ACK (no data)");
        }
        else if (this.d == 254) {
            sb.append(", VendorId:").append(this.i).append(", VendorType:").append(this.j);
        }
        sb.append(", ").append(new Attribute().a(this.e));
        sb.append(" - ").append(this.e.length).append(" bytes\n");
        return sb.toString();
    }
    
    private final byte[] a(final AttributeList list) throws EAPException {
        final byte[][] allBinaryAttributes = list.getAllBinaryAttributes(79);
        if (allBinaryAttributes == null) {
            throw new EAPException("EAP: Packet does not contain any EAP-Message attributes.");
        }
        this.l = allBinaryAttributes.length;
        if (allBinaryAttributes.length == 1) {
            return allBinaryAttributes[0];
        }
        int n = 0;
        for (int i = 0; i < allBinaryAttributes.length; ++i) {
            n += allBinaryAttributes[i].length;
        }
        final byte[] array = new byte[n];
        int n2 = 0;
        for (int j = 0; j < allBinaryAttributes.length; ++j) {
            System.arraycopy(allBinaryAttributes[j], 0, array, n2, allBinaryAttributes[j].length);
            n2 += allBinaryAttributes[j].length;
        }
        return array;
    }
    
    public static int createPacketIdentifier() {
        if (EAPPacket.h == null) {
            EAPPacket.h = new RadRand();
        }
        return EAPPacket.h.nextUnsignedByte();
    }
    
    public static byte[] createChallenge16() {
        if (EAPPacket.h == null) {
            EAPPacket.h = new RadRand();
        }
        final byte[] array = new byte[16];
        EAPPacket.h.nextBytes(array);
        return array;
    }
    
    public static byte[] createChallenge8() {
        if (EAPPacket.h == null) {
            EAPPacket.h = new RadRand();
        }
        final byte[] array = new byte[8];
        EAPPacket.h.nextBytes(array);
        return array;
    }
}
