package org.xbill.DNS.security;

import org.xbill.DNS.Record;
import org.xbill.DNS.Name;
import org.xbill.DNS.KEYRecord;
import org.xbill.DNS.DNSKEYRecord;
import org.xbill.DNS.Options;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import javax.crypto.interfaces.DHPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.math.BigInteger;

public class KEYConverter
{
    private static final BigInteger DHPRIME768;
    private static final BigInteger DHPRIME1024;
    private static final BigInteger TWO;
    
    static int BigIntegerLength(final BigInteger i) {
        final byte[] b = i.toByteArray();
        return (b[0] == 0) ? (b.length - 1) : b.length;
    }
    
    static BigInteger readBigInteger(final DataInputStream in, final int len) throws IOException {
        final byte[] b = new byte[len];
        final int n = in.read(b);
        if (n < len) {
            throw new IOException("end of input");
        }
        return new BigInteger(1, b);
    }
    
    static void writeBigInteger(final ByteArrayOutputStream out, final BigInteger val) {
        final byte[] b = val.toByteArray();
        if (b[0] == 0) {
            out.write(b, 1, b.length - 1);
        }
        else {
            out.write(b, 0, b.length);
        }
    }
    
    static void writeShort(final ByteArrayOutputStream out, final int i) {
        out.write(i >> 8 & 0xFF);
        out.write(i & 0xFF);
    }
    
    static RSAPublicKey parseRSA(final DataInputStream in) throws IOException {
        int exponentLength = in.readUnsignedByte();
        if (exponentLength == 0) {
            exponentLength = in.readUnsignedShort();
        }
        final BigInteger exponent = readBigInteger(in, exponentLength);
        final int modulusLength = in.available();
        final BigInteger modulus = readBigInteger(in, modulusLength);
        final RSAPublicKey rsa = new RSAPubKey(modulus, exponent);
        return rsa;
    }
    
    static DHPublicKey parseDH(final DataInputStream in) throws IOException {
        int special = 0;
        final int pLength = in.readUnsignedShort();
        if (pLength < 16 && pLength != 1 && pLength != 2) {
            return null;
        }
        BigInteger p;
        if (pLength == 1 || pLength == 2) {
            if (pLength == 1) {
                special = in.readUnsignedByte();
            }
            else {
                special = in.readUnsignedShort();
            }
            if (special != 1 && special != 2) {
                return null;
            }
            if (special == 1) {
                p = KEYConverter.DHPRIME768;
            }
            else {
                p = KEYConverter.DHPRIME1024;
            }
        }
        else {
            p = readBigInteger(in, pLength);
        }
        final int gLength = in.readUnsignedShort();
        BigInteger g;
        if (gLength == 0) {
            if (special == 0) {
                return null;
            }
            g = KEYConverter.TWO;
        }
        else {
            g = readBigInteger(in, gLength);
        }
        final int yLength = in.readUnsignedShort();
        final BigInteger y = readBigInteger(in, yLength);
        return new DHPubKey(p, g, y);
    }
    
    static DSAPublicKey parseDSA(final DataInputStream in) throws IOException {
        final byte t = in.readByte();
        final BigInteger q = readBigInteger(in, 20);
        final BigInteger p = readBigInteger(in, 64 + t * 8);
        final BigInteger g = readBigInteger(in, 64 + t * 8);
        final BigInteger y = readBigInteger(in, 64 + t * 8);
        final DSAPublicKey dsa = new DSAPubKey(p, q, g, y);
        return dsa;
    }
    
    static PublicKey parseRecord(final int alg, final byte[] data) {
        final ByteArrayInputStream bytes = new ByteArrayInputStream(data);
        final DataInputStream in = new DataInputStream(bytes);
        try {
            switch (alg) {
                case 1:
                case 5: {
                    return parseRSA(in);
                }
                case 2: {
                    return parseDH(in);
                }
                case 3: {
                    return parseDSA(in);
                }
                default: {
                    return null;
                }
            }
        }
        catch (final IOException e) {
            if (Options.check("verboseexceptions")) {
                System.err.println(e);
            }
            return null;
        }
    }
    
    public static PublicKey parseRecord(final DNSKEYRecord r) {
        final int alg = r.getAlgorithm();
        final byte[] data = r.getKey();
        return parseRecord(alg, data);
    }
    
    public static PublicKey parseRecord(final KEYRecord r) {
        final int alg = r.getAlgorithm();
        final byte[] data = r.getKey();
        return parseRecord(alg, data);
    }
    
    static byte[] buildRSA(final RSAPublicKey key) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigInteger exponent = key.getPublicExponent();
        final BigInteger modulus = key.getModulus();
        final int exponentLength = BigIntegerLength(exponent);
        if (exponentLength < 256) {
            out.write(exponentLength);
        }
        else {
            out.write(0);
            writeShort(out, exponentLength);
        }
        writeBigInteger(out, exponent);
        writeBigInteger(out, modulus);
        return out.toByteArray();
    }
    
    static byte[] buildDH(final DHPublicKey key) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigInteger p = key.getParams().getP();
        final BigInteger g = key.getParams().getG();
        final BigInteger y = key.getY();
        int pLength;
        int gLength;
        if (g.equals(KEYConverter.TWO) && (p.equals(KEYConverter.DHPRIME768) || p.equals(KEYConverter.DHPRIME1024))) {
            pLength = 1;
            gLength = 0;
        }
        else {
            pLength = BigIntegerLength(p);
            gLength = BigIntegerLength(g);
        }
        final int yLength = BigIntegerLength(y);
        writeShort(out, pLength);
        if (pLength == 1) {
            if (p.bitLength() == 768) {
                out.write(1);
            }
            else {
                out.write(2);
            }
        }
        else {
            writeBigInteger(out, p);
        }
        writeShort(out, gLength);
        if (gLength > 0) {
            writeBigInteger(out, g);
        }
        writeShort(out, yLength);
        writeBigInteger(out, y);
        return out.toByteArray();
    }
    
    static byte[] buildDSA(final DSAPublicKey key) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigInteger q = key.getParams().getQ();
        final BigInteger p = key.getParams().getP();
        final BigInteger g = key.getParams().getG();
        final BigInteger y = key.getY();
        final int t = (p.toByteArray().length - 64) / 8;
        out.write(t);
        writeBigInteger(out, q);
        writeBigInteger(out, p);
        writeBigInteger(out, g);
        writeBigInteger(out, y);
        return out.toByteArray();
    }
    
    public static KEYRecord buildRecord(final Name name, final int dclass, final long ttl, final int flags, final int proto, final PublicKey key) {
        byte alg;
        if (key instanceof RSAPublicKey) {
            alg = 1;
        }
        else if (key instanceof DHPublicKey) {
            alg = 2;
        }
        else {
            if (!(key instanceof DSAPublicKey)) {
                return null;
            }
            alg = 3;
        }
        return (KEYRecord)buildRecord(name, 25, dclass, ttl, flags, proto, alg, key);
    }
    
    public static Record buildRecord(final Name name, final int type, final int dclass, final long ttl, final int flags, final int proto, final int alg, final PublicKey key) {
        if (type != 25 && type != 48) {
            throw new IllegalArgumentException("type must be KEY or DNSKEY");
        }
        byte[] data;
        if (key instanceof RSAPublicKey) {
            data = buildRSA((RSAPublicKey)key);
        }
        else if (key instanceof DHPublicKey) {
            data = buildDH((DHPublicKey)key);
        }
        else {
            if (!(key instanceof DSAPublicKey)) {
                return null;
            }
            data = buildDSA((DSAPublicKey)key);
        }
        if (data == null) {
            return null;
        }
        if (type == 48) {
            return new DNSKEYRecord(name, dclass, ttl, flags, proto, alg, data);
        }
        return new KEYRecord(name, dclass, ttl, flags, proto, alg, data);
    }
    
    static {
        DHPRIME768 = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A3620FFFFFFFFFFFFFFFF", 16);
        DHPRIME1024 = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF", 16);
        TWO = new BigInteger("2", 16);
    }
}
