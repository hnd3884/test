package com.theorem.radius3;

import com.theorem.radius3.radutil.Ctype;
import com.theorem.radius3.radutil.Util;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Date;
import com.theorem.radius3.radutil.ByteIterator;
import java.io.Serializable;

public class Attribute extends A implements Serializable
{
    public static final int DATA_TYPE_UNKNOWN = 0;
    public static final int DATA_TYPE_INTEGER = 1;
    public static final int DATA_TYPE_OCTETS = 2;
    public static final int DATA_TYPE_STRING = 4;
    public static final int DATA_TYPE_DATE = 8;
    public static final int DATA_TYPE_IPADDRESS = 16;
    public static final int DATA_TYPE_TUNNEL = 32;
    public static final int DATA_TYPE_ENCRYPT1 = 64;
    public static final int DATA_TYPE_ENCRYPT2 = 128;
    public static final int DATA_TYPE_ENCRYPT3 = 256;
    private int a;
    protected int b;
    private boolean c;
    public int length;
    protected byte[] d;
    
    protected Attribute() {
        this.a = -1;
        this.c = false;
    }
    
    public Attribute(final int b, final byte[] d) {
        this.a = -1;
        this.c = false;
        this.length = d.length + 2;
        if (this.length > 255) {
            this.length = 255;
        }
        this.b = b;
        this.d = d;
        if (this.d == null) {
            this.d = new byte[0];
        }
    }
    
    public Attribute(final int b) {
        this.a = -1;
        this.c = false;
        this.d = new byte[0];
        this.length = 2;
        this.b = b;
    }
    
    public Attribute(final int b, final int n) {
        this.a = -1;
        this.c = false;
        this.length = 6;
        this.b = b;
        this.d = ByteIterator.toBytes(n);
    }
    
    public Attribute(final int b, final Date date) {
        this.a = -1;
        this.c = false;
        this.length = 6;
        this.b = b;
        this.d = ByteIterator.toBytes((int)date.getTime() / 1000);
    }
    
    public Attribute(final int b, final int a, final byte[] d) {
        this.a = -1;
        this.c = false;
        this.length = d.length + 2 + 1;
        if (this.length > 255) {
            this.length = 255;
        }
        this.b = b;
        this.a = a;
        this.d = d;
    }
    
    public final Attribute setLongTag() {
        return this.setLongTag(true);
    }
    
    public final Attribute setLongTag(final boolean c) {
        if (!this.c && c) {
            ++this.length;
        }
        this.c = c;
        return this;
    }
    
    public final boolean isLongTag() {
        return this.c;
    }
    
    public final byte[] getAttribute() {
        final byte[] array = new byte[this.length];
        if (this.a == -1) {
            if (this.c) {
                array[0] = (byte)(this.b >> 8);
                array[1] = (byte)this.b;
                array[2] = (byte)this.length;
                System.arraycopy(this.d, 0, array, 3, this.d.length);
            }
            else {
                array[0] = (byte)this.b;
                array[1] = (byte)this.length;
                System.arraycopy(this.d, 0, array, 2, this.d.length);
            }
        }
        else {
            array[0] = (byte)this.b;
            array[1] = (byte)this.length;
            array[2] = (byte)this.a;
            System.arraycopy(this.d, 0, array, 3, this.d.length);
        }
        return array;
    }
    
    public final int getTag() {
        return this.b;
    }
    
    public final boolean isTunnelAttribute() {
        return this.a != -1;
    }
    
    public final int getTunnelTag() {
        return this.a;
    }
    
    public final byte[] getTunnelValue() {
        if (this.a == -1) {
            this.convertToTunnel();
        }
        return this.d;
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final int getDataLength() {
        return this.d.length;
    }
    
    public final byte[] getAttributeData() {
        if (this.a == -1) {
            return this.d;
        }
        final byte[] array = new byte[this.d.length + 1];
        array[0] = (byte)this.a;
        System.arraycopy(this.d, 0, array, 1, this.d.length);
        return array;
    }
    
    public final int getInt() {
        final byte[] d = this.d;
        if (d.length != 4) {
            return 0;
        }
        return d[0] << 24 | (d[1] & 0xFF) << 16 | (d[2] & 0xFF) << 8 | (d[3] & 0xFF);
    }
    
    public final Date getDate() {
        return new Date(this.getInt() * 1000L);
    }
    
    public final InetAddress getIP() {
        if (this.d.length != 4 || this.d.length == 16) {
            return null;
        }
        return AttributeList.parseIPAttribute(this.d);
    }
    
    public final String getString() {
        try {
            return new String(this.d, "UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(this.d);
        }
    }
    
    public final int getVendorID() {
        if (this.length < 6) {
            return 0;
        }
        return ByteIterator.toInt(this.getAttributeData());
    }
    
    public final String toString() {
        return this.toString(0);
    }
    
    public final String toString(final int n) {
        byte[] array = this.d;
        final int tag = this.getTag();
        String s = AttributeName.lookup(n, tag) + " (" + tag;
        if (this.c) {
            s += " -long";
        }
        String s2 = s + "), Length: " + (this.c ? (this.length - 1) : this.length);
        if (n == 0 && (this.getDataType() & 0x20) == 0x20) {
            this.convertToTunnel();
            array = this.d;
        }
        if (this.a != -1) {
            s2 = s2 + ", Tunnel Group: " + this.a;
        }
        if (tag == 80 && this.getLength() == 2) {
            return s2 + ", Data: Not Defined Yet.";
        }
        return s2 + ", " + this.a(array);
    }
    
    protected final String a(final byte[] array) {
        String s = "" + "Data: ";
        if (array.length == 0) {
            return s + "None";
        }
        if (isPrintable(array, 0, array.length)) {
            s = s + "[" + Util.toUTF8(array) + "], ";
        }
        if (array.length == 16) {
            switch (this.b) {
                case 95:
                case 96:
                case 97:
                case 98: {
                    s = s + "IP " + toIPString(array) + ", ";
                    break;
                }
            }
        }
        if (array.length == 4) {
            final long n = (long)ByteIterator.toInt(array) & 0xFFFFFFFFL;
            final String lookup = AV.lookup(this.b, (int)n);
            String s2;
            if (!lookup.equals("")) {
                s2 = s + "[# " + (n + " (" + lookup + ")") + "]";
            }
            else if (n >>> 24 == 0L) {
                s2 = s + "[# " + n + "]";
            }
            else {
                s2 = s + "[# " + n + "] / [IP " + toIPString(array) + "]";
            }
            s = s2 + ", ";
        }
        return s + "0x" + Util.toHexString(array);
    }
    
    public final boolean isPrintableAscii() {
        return isPrintableAscii(this.d);
    }
    
    public static boolean isPrintableAscii(final byte[] array) {
        return isPrintableAscii(array, 0, array.length);
    }
    
    public static boolean isPrintableAscii(final byte[] array, final int n, int n2) {
        int n3;
        for (n2 += n, n3 = n; n3 < n2 && Ctype.isprint(array[n3]); ++n3) {}
        return n3 == n2;
    }
    
    public final boolean isPrintable() {
        return isPrintable(this.d);
    }
    
    public static boolean isPrintable(final byte[] array) {
        return isPrintable(array, 0, array.length);
    }
    
    public static boolean isPrintable(final byte[] array, final int n, final int n2) {
        char[] charArray;
        int length;
        int n3;
        for (charArray = new String(array, n, n2).toCharArray(), length = charArray.length, n3 = 0; n3 < length && !Character.isISOControl(charArray[n3]); ++n3) {}
        return n3 == length;
    }
    
    public static String toIPString(final byte[] array) {
        return toIPString(array, 0);
    }
    
    public static String toIPString(final byte[] array, final int n) {
        final StringBuffer sb = new StringBuffer();
        if (array.length - n == 16) {
            final byte[] array2 = new byte[16];
            System.arraycopy(array, n, array2, 0, 16);
            sb.append(Integer.toHexString((array2[0] & 0xFF) << 8 | (array2[1] & 0xFF))).append(':');
            sb.append(Integer.toHexString((array2[2] & 0xFF) << 8 | (array2[3] & 0xFF))).append(':');
            sb.append(Integer.toHexString((array2[4] & 0xFF) << 8 | (array2[5] & 0xFF))).append(':');
            sb.append(Integer.toHexString((array2[6] & 0xFF) << 8 | (array2[7] & 0xFF))).append(':');
            sb.append(Integer.toHexString((array2[8] & 0xFF) << 8 | (array2[9] & 0xFF))).append(':');
            sb.append(Integer.toHexString((array2[10] & 0xFF) << 8 | (array2[11] & 0xFF))).append(':');
            sb.append(array2[12] & 0xFF).append('.');
            sb.append(array2[13] & 0xFF).append('.');
            sb.append(array2[14] & 0xFF).append('.');
            sb.append(array2[15] & 0xFF);
        }
        else {
            sb.append(array[0 + n] & 0xFF).append('.');
            sb.append(array[1 + n] & 0xFF).append('.');
            sb.append(array[2 + n] & 0xFF).append('.');
            sb.append(array[3 + n] & 0xFF);
        }
        return sb.toString();
    }
    
    public final void convertToTunnel() {
        if (this.a != -1) {
            return;
        }
        byte[] d;
        if (this.d.length > 1) {
            d = new byte[this.d.length - 1];
            System.arraycopy(this.d, 1, d, 0, d.length);
            this.a = (this.d[0] & 0xFF);
        }
        else {
            d = new byte[0];
            this.a = 0;
        }
        this.d = d;
    }
    
    public static String getName(final int n) {
        return AttributeName.lookup(n);
    }
    
    public final String getName() {
        return AttributeName.lookup(this.b);
    }
    
    public static int getTag(final String s) {
        return AttributeName.lookup(s);
    }
    
    public static int getValueName(final int n, final String s) {
        return AV.lookup(n, s);
    }
    
    public final String getValueName() {
        if (this.d.length != 4) {
            return null;
        }
        return AV.lookup(this.b, ByteIterator.toInt(this.d));
    }
    
    public final boolean equals(final Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute attribute = (Attribute)o;
        if (attribute.length == this.length && attribute.a == this.a) {
            for (int i = 0; i < this.d.length; ++i) {
                if (attribute.d[i] != this.d[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public final int hashCode() {
        return this.d.hashCode();
    }
    
    public final int getDataType() {
        return this.getDataType(0);
    }
    
    public final int getDataType(final int n) {
        return AttributeDataType.getDataType(n, this.b);
    }
    
    public static int getDataType(final int n, final int n2) {
        return AttributeDataType.getDataType(n, n2);
    }
    
    public static void setDataType(final int n, final int n2) {
        AttributeDataType.setDataType(n, n2);
    }
}
