package org.bouncycastle.asn1;

import java.util.concurrent.ConcurrentHashMap;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class ASN1ObjectIdentifier extends ASN1Primitive
{
    private final String identifier;
    private byte[] body;
    private static final long LONG_LIMIT = 72057594037927808L;
    private static final ConcurrentMap<OidHandle, ASN1ObjectIdentifier> pool;
    
    public static ASN1ObjectIdentifier getInstance(final Object o) {
        if (o == null || o instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)o;
        }
        if (o instanceof ASN1Encodable && ((ASN1Encodable)o).toASN1Primitive() instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)((ASN1Encodable)o).toASN1Primitive();
        }
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            try {
                return (ASN1ObjectIdentifier)ASN1Primitive.fromByteArray(array);
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1ObjectIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1ObjectIdentifier) {
            return getInstance(object);
        }
        return fromOctetString(ASN1OctetString.getInstance(object).getOctets());
    }
    
    ASN1ObjectIdentifier(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        long n = 0L;
        BigInteger bigInteger = null;
        int n2 = 1;
        for (int i = 0; i != array.length; ++i) {
            final int n3 = array[i] & 0xFF;
            if (n <= 72057594037927808L) {
                long n4 = n + (n3 & 0x7F);
                if ((n3 & 0x80) == 0x0) {
                    if (n2 != 0) {
                        if (n4 < 40L) {
                            sb.append('0');
                        }
                        else if (n4 < 80L) {
                            sb.append('1');
                            n4 -= 40L;
                        }
                        else {
                            sb.append('2');
                            n4 -= 80L;
                        }
                        n2 = 0;
                    }
                    sb.append('.');
                    sb.append(n4);
                    n = 0L;
                }
                else {
                    n = n4 << 7;
                }
            }
            else {
                if (bigInteger == null) {
                    bigInteger = BigInteger.valueOf(n);
                }
                BigInteger bigInteger2 = bigInteger.or(BigInteger.valueOf(n3 & 0x7F));
                if ((n3 & 0x80) == 0x0) {
                    if (n2 != 0) {
                        sb.append('2');
                        bigInteger2 = bigInteger2.subtract(BigInteger.valueOf(80L));
                        n2 = 0;
                    }
                    sb.append('.');
                    sb.append(bigInteger2);
                    bigInteger = null;
                    n = 0L;
                }
                else {
                    bigInteger = bigInteger2.shiftLeft(7);
                }
            }
        }
        this.identifier = sb.toString();
        this.body = Arrays.clone(array);
    }
    
    public ASN1ObjectIdentifier(final String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("'identifier' cannot be null");
        }
        if (!isValidIdentifier(identifier)) {
            throw new IllegalArgumentException("string " + identifier + " not an OID");
        }
        this.identifier = identifier;
    }
    
    ASN1ObjectIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        if (!isValidBranchID(s, 0)) {
            throw new IllegalArgumentException("string " + s + " not a valid OID branch");
        }
        this.identifier = asn1ObjectIdentifier.getId() + "." + s;
    }
    
    public String getId() {
        return this.identifier;
    }
    
    public ASN1ObjectIdentifier branch(final String s) {
        return new ASN1ObjectIdentifier(this, s);
    }
    
    public boolean on(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String id = this.getId();
        final String id2 = asn1ObjectIdentifier.getId();
        return id.length() > id2.length() && id.charAt(id2.length()) == '.' && id.startsWith(id2);
    }
    
    private void writeField(final ByteArrayOutputStream byteArrayOutputStream, long n) {
        final byte[] array = new byte[9];
        int n2 = 8;
        array[n2] = (byte)((int)n & 0x7F);
        while (n >= 128L) {
            n >>= 7;
            array[--n2] = (byte)(((int)n & 0x7F) | 0x80);
        }
        byteArrayOutputStream.write(array, n2, 9 - n2);
    }
    
    private void writeField(final ByteArrayOutputStream byteArrayOutputStream, final BigInteger bigInteger) {
        final int n = (bigInteger.bitLength() + 6) / 7;
        if (n == 0) {
            byteArrayOutputStream.write(0);
        }
        else {
            BigInteger shiftRight = bigInteger;
            final byte[] array = new byte[n];
            for (int i = n - 1; i >= 0; --i) {
                array[i] = (byte)((shiftRight.intValue() & 0x7F) | 0x80);
                shiftRight = shiftRight.shiftRight(7);
            }
            final byte[] array2 = array;
            final int n2 = n - 1;
            array2[n2] &= 0x7F;
            byteArrayOutputStream.write(array, 0, array.length);
        }
    }
    
    private void doOutput(final ByteArrayOutputStream byteArrayOutputStream) {
        final OIDTokenizer oidTokenizer = new OIDTokenizer(this.identifier);
        final int n = Integer.parseInt(oidTokenizer.nextToken()) * 40;
        final String nextToken = oidTokenizer.nextToken();
        if (nextToken.length() <= 18) {
            this.writeField(byteArrayOutputStream, n + Long.parseLong(nextToken));
        }
        else {
            this.writeField(byteArrayOutputStream, new BigInteger(nextToken).add(BigInteger.valueOf(n)));
        }
        while (oidTokenizer.hasMoreTokens()) {
            final String nextToken2 = oidTokenizer.nextToken();
            if (nextToken2.length() <= 18) {
                this.writeField(byteArrayOutputStream, Long.parseLong(nextToken2));
            }
            else {
                this.writeField(byteArrayOutputStream, new BigInteger(nextToken2));
            }
        }
    }
    
    private synchronized byte[] getBody() {
        if (this.body == null) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            this.doOutput(byteArrayOutputStream);
            this.body = byteArrayOutputStream.toByteArray();
        }
        return this.body;
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() throws IOException {
        final int length = this.getBody().length;
        return 1 + StreamUtil.calculateBodyLength(length) + length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        final byte[] body = this.getBody();
        asn1OutputStream.write(6);
        asn1OutputStream.writeLength(body.length);
        asn1OutputStream.write(body);
    }
    
    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive == this || (asn1Primitive instanceof ASN1ObjectIdentifier && this.identifier.equals(((ASN1ObjectIdentifier)asn1Primitive).identifier));
    }
    
    @Override
    public String toString() {
        return this.getId();
    }
    
    private static boolean isValidBranchID(final String s, final int n) {
        boolean b = false;
        int length = s.length();
        while (--length >= n) {
            final char char1 = s.charAt(length);
            if ('0' <= char1 && char1 <= '9') {
                b = true;
            }
            else {
                if (char1 != '.') {
                    return false;
                }
                if (!b) {
                    return false;
                }
                b = false;
            }
        }
        return b;
    }
    
    private static boolean isValidIdentifier(final String s) {
        if (s.length() < 3 || s.charAt(1) != '.') {
            return false;
        }
        final char char1 = s.charAt(0);
        return char1 >= '0' && char1 <= '2' && isValidBranchID(s, 2);
    }
    
    public ASN1ObjectIdentifier intern() {
        final OidHandle oidHandle = new OidHandle(this.getBody());
        ASN1ObjectIdentifier asn1ObjectIdentifier = ASN1ObjectIdentifier.pool.get(oidHandle);
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = ASN1ObjectIdentifier.pool.putIfAbsent(oidHandle, this);
            if (asn1ObjectIdentifier == null) {
                asn1ObjectIdentifier = this;
            }
        }
        return asn1ObjectIdentifier;
    }
    
    static ASN1ObjectIdentifier fromOctetString(final byte[] array) {
        final ASN1ObjectIdentifier asn1ObjectIdentifier = ASN1ObjectIdentifier.pool.get(new OidHandle(array));
        if (asn1ObjectIdentifier == null) {
            return new ASN1ObjectIdentifier(array);
        }
        return asn1ObjectIdentifier;
    }
    
    static {
        pool = new ConcurrentHashMap<OidHandle, ASN1ObjectIdentifier>();
    }
    
    private static class OidHandle
    {
        private final int key;
        private final byte[] enc;
        
        OidHandle(final byte[] enc) {
            this.key = Arrays.hashCode(enc);
            this.enc = enc;
        }
        
        @Override
        public int hashCode() {
            return this.key;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof OidHandle && Arrays.areEqual(this.enc, ((OidHandle)o).enc);
        }
    }
}
