package com.sun.jndi.dns;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.naming.CommunicationException;

public class ResourceRecord
{
    static final int TYPE_A = 1;
    static final int TYPE_NS = 2;
    static final int TYPE_CNAME = 5;
    static final int TYPE_SOA = 6;
    static final int TYPE_PTR = 12;
    static final int TYPE_HINFO = 13;
    static final int TYPE_MX = 15;
    static final int TYPE_TXT = 16;
    static final int TYPE_AAAA = 28;
    static final int TYPE_SRV = 33;
    static final int TYPE_NAPTR = 35;
    static final int QTYPE_AXFR = 252;
    static final int QTYPE_STAR = 255;
    static final String[] rrTypeNames;
    static final int CLASS_INTERNET = 1;
    static final int CLASS_HESIOD = 2;
    static final int QCLASS_STAR = 255;
    static final String[] rrClassNames;
    private static final int MAXIMUM_COMPRESSION_REFERENCES = 16;
    byte[] msg;
    int msgLen;
    boolean qSection;
    int offset;
    int rrlen;
    DnsName name;
    int rrtype;
    String rrtypeName;
    int rrclass;
    String rrclassName;
    int ttl;
    int rdlen;
    Object rdata;
    private static final boolean debug = false;
    
    ResourceRecord(final byte[] msg, final int msgLen, final int offset, final boolean qSection, final boolean b) throws CommunicationException {
        this.ttl = 0;
        this.rdlen = 0;
        this.rdata = null;
        this.msg = msg;
        this.msgLen = msgLen;
        this.offset = offset;
        this.qSection = qSection;
        this.decode(b);
    }
    
    @Override
    public String toString() {
        String s = this.name + " " + this.rrclassName + " " + this.rrtypeName;
        if (!this.qSection) {
            s = s + " " + this.ttl + " " + ((this.rdata != null) ? this.rdata : "[n/a]");
        }
        return s;
    }
    
    public DnsName getName() {
        return this.name;
    }
    
    public int size() {
        return this.rrlen;
    }
    
    public int getType() {
        return this.rrtype;
    }
    
    public int getRrclass() {
        return this.rrclass;
    }
    
    public Object getRdata() {
        return this.rdata;
    }
    
    public static String getTypeName(final int n) {
        return valueToName(n, ResourceRecord.rrTypeNames);
    }
    
    public static int getType(final String s) {
        return nameToValue(s, ResourceRecord.rrTypeNames);
    }
    
    public static String getRrclassName(final int n) {
        return valueToName(n, ResourceRecord.rrClassNames);
    }
    
    public static int getRrclass(final String s) {
        return nameToValue(s, ResourceRecord.rrClassNames);
    }
    
    private static String valueToName(final int n, final String[] array) {
        String string = null;
        if (n > 0 && n < array.length) {
            string = array[n];
        }
        else if (n == 255) {
            string = "*";
        }
        if (string == null) {
            string = Integer.toString(n);
        }
        return string;
    }
    
    private static int nameToValue(final String s, final String[] array) {
        if (s.equals("")) {
            return -1;
        }
        if (s.equals("*")) {
            return 255;
        }
        if (Character.isDigit(s.charAt(0))) {
            try {
                return Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {}
        }
        for (int i = 1; i < array.length; ++i) {
            if (array[i] != null && s.equalsIgnoreCase(array[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static int compareSerialNumbers(final long n, final long n2) {
        final long n3 = n2 - n;
        if (n3 == 0L) {
            return 0;
        }
        if ((n3 > 0L && n3 <= 2147483647L) || (n3 < 0L && -n3 > 2147483647L)) {
            return -1;
        }
        return 1;
    }
    
    private void decode(final boolean b) throws CommunicationException {
        final int offset = this.offset;
        this.name = new DnsName();
        int decodeName = this.decodeName(offset, this.name);
        this.rrtype = this.getUShort(decodeName);
        this.rrtypeName = ((this.rrtype < ResourceRecord.rrTypeNames.length) ? ResourceRecord.rrTypeNames[this.rrtype] : null);
        if (this.rrtypeName == null) {
            this.rrtypeName = Integer.toString(this.rrtype);
        }
        decodeName += 2;
        this.rrclass = this.getUShort(decodeName);
        this.rrclassName = ((this.rrclass < ResourceRecord.rrClassNames.length) ? ResourceRecord.rrClassNames[this.rrclass] : null);
        if (this.rrclassName == null) {
            this.rrclassName = Integer.toString(this.rrclass);
        }
        decodeName += 2;
        if (!this.qSection) {
            this.ttl = this.getInt(decodeName);
            decodeName += 4;
            this.rdlen = this.getUShort(decodeName);
            decodeName += 2;
            this.rdata = ((b || this.rrtype == 6) ? this.decodeRdata(decodeName) : null);
            if (this.rdata instanceof DnsName) {
                this.rdata = this.rdata.toString();
            }
            decodeName += this.rdlen;
        }
        this.rrlen = decodeName - this.offset;
        this.msg = null;
    }
    
    private int getUByte(final int n) {
        return this.msg[n] & 0xFF;
    }
    
    private int getUShort(final int n) {
        return (this.msg[n] & 0xFF) << 8 | (this.msg[n + 1] & 0xFF);
    }
    
    private int getInt(final int n) {
        return this.getUShort(n) << 16 | this.getUShort(n + 2);
    }
    
    private long getUInt(final int n) {
        return (long)this.getInt(n) & 0xFFFFFFFFL;
    }
    
    private DnsName decodeName(final int n) throws CommunicationException {
        final DnsName dnsName = new DnsName();
        this.decodeName(n, dnsName);
        return dnsName;
    }
    
    private int decodeName(int n, final DnsName dnsName) throws CommunicationException {
        int n2 = -1;
        int i = 0;
        Label_0192: {
            try {
                while (i <= 16) {
                    final int n3 = this.msg[n] & 0xFF;
                    if (n3 == 0) {
                        ++n;
                        dnsName.add(0, "");
                        break Label_0192;
                    }
                    if (n3 <= 63) {
                        ++n;
                        dnsName.add(0, new String(this.msg, n, n3, StandardCharsets.ISO_8859_1));
                        n += n3;
                    }
                    else {
                        if ((n3 & 0xC0) != 0xC0) {
                            throw new IOException("Invalid label type: " + n3);
                        }
                        ++i;
                        if (n2 == -1) {
                            n2 = n + 2;
                        }
                        n = (this.getUShort(n) & 0x3FFF);
                    }
                }
                throw new IOException("Too many compression references");
            }
            catch (final IOException | InvalidNameException ex) {
                final CommunicationException ex2 = new CommunicationException("DNS error: malformed packet");
                ex2.initCause((Throwable)ex);
                throw ex2;
            }
        }
        if (n2 == -1) {
            n2 = n;
        }
        return n2;
    }
    
    private Object decodeRdata(final int n) throws CommunicationException {
        if (this.rrclass == 1) {
            switch (this.rrtype) {
                case 1: {
                    return this.decodeA(n);
                }
                case 28: {
                    return this.decodeAAAA(n);
                }
                case 2:
                case 5:
                case 12: {
                    return this.decodeName(n);
                }
                case 15: {
                    return this.decodeMx(n);
                }
                case 6: {
                    return this.decodeSoa(n);
                }
                case 33: {
                    return this.decodeSrv(n);
                }
                case 35: {
                    return this.decodeNaptr(n);
                }
                case 16: {
                    return this.decodeTxt(n);
                }
                case 13: {
                    return this.decodeHinfo(n);
                }
            }
        }
        final byte[] array = new byte[this.rdlen];
        System.arraycopy(this.msg, n, array, 0, this.rdlen);
        return array;
    }
    
    private String decodeMx(int n) throws CommunicationException {
        final int uShort = this.getUShort(n);
        n += 2;
        return uShort + " " + this.decodeName(n);
    }
    
    private String decodeSoa(int n) throws CommunicationException {
        final DnsName dnsName = new DnsName();
        n = this.decodeName(n, dnsName);
        final DnsName dnsName2 = new DnsName();
        n = this.decodeName(n, dnsName2);
        final long uInt = this.getUInt(n);
        n += 4;
        final long uInt2 = this.getUInt(n);
        n += 4;
        final long uInt3 = this.getUInt(n);
        n += 4;
        final long uInt4 = this.getUInt(n);
        n += 4;
        final long uInt5 = this.getUInt(n);
        n += 4;
        return dnsName + " " + dnsName2 + " " + uInt + " " + uInt2 + " " + uInt3 + " " + uInt4 + " " + uInt5;
    }
    
    private String decodeSrv(int n) throws CommunicationException {
        final int uShort = this.getUShort(n);
        n += 2;
        final int uShort2 = this.getUShort(n);
        n += 2;
        final int uShort3 = this.getUShort(n);
        n += 2;
        return uShort + " " + uShort2 + " " + uShort3 + " " + this.decodeName(n);
    }
    
    private String decodeNaptr(int n) throws CommunicationException {
        final int uShort = this.getUShort(n);
        n += 2;
        final int uShort2 = this.getUShort(n);
        n += 2;
        final StringBuffer sb = new StringBuffer();
        n += this.decodeCharString(n, sb);
        final StringBuffer sb2 = new StringBuffer();
        n += this.decodeCharString(n, sb2);
        final StringBuffer sb3 = new StringBuffer(this.rdlen);
        n += this.decodeCharString(n, sb3);
        return uShort + " " + uShort2 + " " + (Object)sb + " " + (Object)sb2 + " " + (Object)sb3 + " " + this.decodeName(n);
    }
    
    private String decodeTxt(int i) {
        final StringBuffer sb = new StringBuffer(this.rdlen);
        final int n = i + this.rdlen;
        while (i < n) {
            i += this.decodeCharString(i, sb);
            if (i < n) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    
    private String decodeHinfo(int n) {
        final StringBuffer sb = new StringBuffer(this.rdlen);
        n += this.decodeCharString(n, sb);
        sb.append(' ');
        n += this.decodeCharString(n, sb);
        return sb.toString();
    }
    
    private int decodeCharString(int n, final StringBuffer sb) {
        final int length = sb.length();
        final int uByte = this.getUByte(n++);
        boolean b = uByte == 0;
        for (int i = 0; i < uByte; ++i) {
            final int uByte2 = this.getUByte(n++);
            b |= (uByte2 == 32);
            if (uByte2 == 92 || uByte2 == 34) {
                b = true;
                sb.append('\\');
            }
            sb.append((char)uByte2);
        }
        if (b) {
            sb.insert(length, '\"');
            sb.append('\"');
        }
        return uByte + 1;
    }
    
    private String decodeA(final int n) {
        return (this.msg[n] & 0xFF) + "." + (this.msg[n + 1] & 0xFF) + "." + (this.msg[n + 2] & 0xFF) + "." + (this.msg[n + 3] & 0xFF);
    }
    
    private String decodeAAAA(int n) {
        final int[] array = new int[8];
        for (int i = 0; i < 8; ++i) {
            array[i] = this.getUShort(n);
            n += 2;
        }
        int n2 = -1;
        int n3 = 0;
        int n4 = -1;
        int n5 = 0;
        for (int j = 0; j < 8; ++j) {
            if (array[j] == 0) {
                if (n2 == -1) {
                    n2 = j;
                    n3 = 1;
                }
                else if (++n3 >= 2 && n3 > n5) {
                    n4 = n2;
                    n5 = n3;
                }
            }
            else {
                n2 = -1;
            }
        }
        if (n4 == 0) {
            if (n5 == 6 || (n5 == 7 && array[7] > 1)) {
                return "::" + this.decodeA(n - 4);
            }
            if (n5 == 5 && array[5] == 65535) {
                return "::ffff:" + this.decodeA(n - 4);
            }
        }
        final boolean b = n4 != -1;
        final StringBuffer sb = new StringBuffer(40);
        if (n4 == 0) {
            sb.append(':');
        }
        for (int k = 0; k < 8; ++k) {
            if (!b || k < n4 || k >= n4 + n5) {
                sb.append(Integer.toHexString(array[k]));
                if (k < 7) {
                    sb.append(':');
                }
            }
            else if (b && k == n4) {
                sb.append(':');
            }
        }
        return sb.toString();
    }
    
    private static void dprint(final String s) {
    }
    
    static {
        rrTypeNames = new String[] { null, "A", "NS", null, null, "CNAME", "SOA", null, null, null, null, null, "PTR", "HINFO", null, "MX", "TXT", null, null, null, null, null, null, null, null, null, null, null, "AAAA", null, null, null, null, "SRV", null, "NAPTR" };
        rrClassNames = new String[] { null, "IN", null, null, "HS" };
    }
}
