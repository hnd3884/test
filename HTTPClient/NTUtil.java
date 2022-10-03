package HTTPClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

public class NTUtil
{
    private static final byte[] ARRAY;
    
    private static Key findDESKey(final byte[] keyData, final int offset) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] desKeyData = new byte[8];
        final int[] k = new int[7];
        for (int i = 0; i < 7; ++i) {
            k[i] = convertUnsignedByteToInt(keyData[offset + i]);
        }
        desKeyData[0] = (byte)(k[0] >>> 1);
        desKeyData[1] = (byte)((k[0] & 0x1) << 6 | k[1] >>> 2);
        desKeyData[2] = (byte)((k[1] & 0x3) << 5 | k[2] >>> 3);
        desKeyData[3] = (byte)((k[2] & 0x7) << 4 | k[3] >>> 4);
        desKeyData[4] = (byte)((k[3] & 0xF) << 3 | k[4] >>> 5);
        desKeyData[5] = (byte)((k[4] & 0x1F) << 2 | k[5] >>> 6);
        desKeyData[6] = (byte)((k[5] & 0x3F) << 1 | k[6] >>> 7);
        desKeyData[7] = (byte)(k[6] & 0x7F);
        for (int i = 0; i < 8; ++i) {
            desKeyData[i] = (byte)(convertUnsignedByteToInt(desKeyData[i]) << 1);
        }
        final KeySpec desKeySpec = new DESKeySpec(desKeyData);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        final SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        return secretKey;
    }
    
    private static int convertUnsignedByteToInt(final byte b) {
        return b & 0xFF;
    }
    
    public static byte[] getLanManagerPassword(final String password) throws IllegalArgumentException, NoSuchPaddingException, NoSuchAlgorithmException {
        if (password == null) {
            throw new IllegalArgumentException("password : null value not allowed");
        }
        try {
            int len = password.length();
            if (len > 14) {
                len = 14;
            }
            final Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            final byte[] lm_pw = new byte[14];
            final byte[] bytes = password.toUpperCase().getBytes();
            int i;
            for (i = 0; i < len; ++i) {
                lm_pw[i] = bytes[i];
            }
            while (i < 14) {
                lm_pw[i] = 0;
                ++i;
            }
            final byte[] lm_hpw = new byte[16];
            Key k = findDESKey(lm_pw, 0);
            c.init(1, k);
            c.doFinal(NTUtil.ARRAY, 0, 8, lm_hpw, 0);
            k = findDESKey(lm_pw, 7);
            c.init(1, k);
            c.doFinal(NTUtil.ARRAY, 0, 8, lm_hpw, 8);
            return lm_hpw;
        }
        catch (final InvalidKeySpecException ex) {
            return null;
        }
        catch (final InvalidKeyException ex2) {
            return null;
        }
        catch (final BadPaddingException ex3) {
            return null;
        }
        catch (final IllegalBlockSizeException ex4) {
            return null;
        }
        catch (final ShortBufferException ex5) {
            return null;
        }
    }
    
    public static byte[] getNonce(final byte[] msg) throws IllegalArgumentException {
        if (msg.length < 32) {
            throw new IllegalArgumentException("msg : illegal size");
        }
        final byte[] nonce = new byte[8];
        System.arraycopy(msg, 24, nonce, 0, 8);
        return nonce;
    }
    
    public static byte[] getNTHashedPassword(final String password) throws IllegalArgumentException, NoSuchAlgorithmException {
        if (password == null) {
            throw new IllegalArgumentException("password : null value not allowed");
        }
        int len = password.length();
        if (len > 14) {
            len = 14;
        }
        final byte[] nt_pw = new byte[2 * len];
        for (int i = 0; i < len; ++i) {
            final char ch = password.charAt(i);
            nt_pw[2 * i] = (byte)ch;
            nt_pw[2 * i + 1] = (byte)(ch >>> 8 & 0xFF);
        }
        final MessageDigest md = MessageDigest.getInstance("MD4");
        return md.digest(nt_pw);
    }
    
    public static byte[] buildResponse(String host, final String user, String userDomain, final byte[] lmPassword, final byte[] ntPassword, final byte[] nonce) throws IllegalArgumentException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        if (host == null) {
            throw new IllegalArgumentException("host : null value not allowed");
        }
        if (user == null) {
            throw new IllegalArgumentException("user : null value not allowed");
        }
        if (userDomain == null) {
            throw new IllegalArgumentException("userDomain : null value not allowed");
        }
        if (lmPassword == null) {
            throw new IllegalArgumentException("lmPassword : null value not allowed");
        }
        if (ntPassword == null) {
            throw new IllegalArgumentException("ntPassword : null value not allowed");
        }
        if (nonce == null) {
            throw new IllegalArgumentException("nonce : null value not allowed");
        }
        if (lmPassword.length != 16) {
            throw new IllegalArgumentException("lmPassword : illegal size");
        }
        if (ntPassword.length != 16) {
            throw new IllegalArgumentException("ntPassword : illegal size");
        }
        if (nonce.length != 8) {
            throw new IllegalArgumentException("nonce : illegal size");
        }
        final byte[] lmResponse = new byte[24];
        final byte[] ntResponse = new byte[24];
        buildNTLMResponse(lmPassword, ntPassword, nonce, lmResponse, ntResponse);
        userDomain = userDomain.toUpperCase();
        host = host.toUpperCase();
        final short lmRespLen = 24;
        final short ntRespLen = 24;
        final short domainLen = (short)(2 * userDomain.length());
        final short hostLen = (short)(2 * host.length());
        final short userLen = (short)(2 * user.length());
        final short domainOff = 64;
        final short userOff = (short)(domainOff + domainLen);
        final short hostOff = (short)(userOff + userLen);
        final short lmRespOff = (short)(hostOff + hostLen);
        final short ntRespOff = (short)(lmRespOff + lmRespLen);
        final short msgLen = (short)(ntRespOff + ntRespLen);
        ByteArrayOutputStream os = null;
        DataOutputStream dataOut = null;
        try {
            os = new ByteArrayOutputStream(1024);
            dataOut = new DataOutputStream(os);
            dataOut.writeBytes("NTLMSSP\u0000");
            dataOut.writeByte(3);
            dataOut.writeByte(0);
            dataOut.writeByte(0);
            dataOut.writeByte(0);
            dataOut.writeShort(switchBytes(lmRespLen));
            dataOut.writeShort(switchBytes(lmRespLen));
            dataOut.writeShort(switchBytes(lmRespOff));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(ntRespLen));
            dataOut.writeShort(switchBytes(ntRespLen));
            dataOut.writeShort(switchBytes(ntRespOff));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(domainLen));
            dataOut.writeShort(switchBytes(domainLen));
            dataOut.writeShort(switchBytes(domainOff));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(userLen));
            dataOut.writeShort(switchBytes(userLen));
            dataOut.writeShort(switchBytes(userOff));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(hostLen));
            dataOut.writeShort(switchBytes(hostLen));
            dataOut.writeShort(switchBytes(hostOff));
            dataOut.writeShort(0);
            dataOut.writeInt(0);
            dataOut.writeShort(switchBytes(msgLen));
            dataOut.writeShort(0);
            dataOut.writeShort(0);
            dataOut.writeShort(0);
            for (int i = 0; i < userDomain.length(); ++i) {
                dataOut.writeShort(switchBytes((short)userDomain.charAt(i)));
            }
            for (int i = 0; i < user.length(); ++i) {
                dataOut.writeShort(switchBytes((short)user.charAt(i)));
            }
            for (int i = 0; i < host.length(); ++i) {
                dataOut.writeShort(switchBytes((short)host.charAt(i)));
            }
            dataOut.write(lmResponse);
            dataOut.write(ntResponse);
            dataOut.flush();
        }
        finally {
            os.close();
            dataOut.close();
        }
        return os.toByteArray();
    }
    
    private static short switchBytes(final short s) {
        return (short)((s << 8 & 0xFF00) | (s >>> 8 & 0xFF));
    }
    
    private static void buildNTLMResponse(final byte[] lmPassword, final byte[] ntPassword, final byte[] nonce, final byte[] lmResponse, final byte[] ntResponse) throws IllegalArgumentException, NoSuchPaddingException, NoSuchAlgorithmException {
        if (lmPassword.length != 16) {
            throw new IllegalArgumentException("lmPassword : illegal size");
        }
        if (ntPassword.length != 16) {
            throw new IllegalArgumentException("ntPassword : illegal size");
        }
        if (nonce.length != 8) {
            throw new IllegalArgumentException("nonce : illegal size");
        }
        if (lmResponse.length != 24) {
            throw new IllegalArgumentException("lmResponse : illegal size");
        }
        if (ntResponse.length != 24) {
            throw new IllegalArgumentException("ntResponse : illegal size");
        }
        try {
            final byte[] lmHPw = new byte[21];
            final byte[] ntHPw = new byte[21];
            System.arraycopy(lmPassword, 0, lmHPw, 0, 16);
            System.arraycopy(ntPassword, 0, ntHPw, 0, 16);
            for (int i = 16; i < 21; ++i) {
                ntHPw[i] = (lmHPw[i] = 0);
            }
            System.arraycopy(encrypt(lmHPw, nonce), 0, lmResponse, 0, 24);
            System.arraycopy(encrypt(ntHPw, nonce), 0, ntResponse, 0, 24);
        }
        catch (final ShortBufferException ex) {}
        catch (final IllegalBlockSizeException ex2) {}
        catch (final BadPaddingException ex3) {}
        catch (final InvalidKeySpecException ex4) {}
        catch (final InvalidKeyException ex5) {}
    }
    
    private static byte[] encrypt(final byte[] keys, final byte[] plaintext) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        final byte[] ciphertext = new byte[24];
        final Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
        Key k = findDESKey(keys, 0);
        c.init(1, k);
        c.doFinal(plaintext, 0, 8, ciphertext, 0);
        k = findDESKey(keys, 7);
        c.init(1, k);
        c.doFinal(plaintext, 0, 8, ciphertext, 8);
        k = findDESKey(keys, 14);
        c.init(1, k);
        c.doFinal(plaintext, 0, 8, ciphertext, 16);
        return ciphertext;
    }
    
    public static byte[] buildRequest(String host, String hostDomain) throws IOException {
        hostDomain = hostDomain.toUpperCase();
        host = host.toUpperCase();
        final short domainLen = (short)hostDomain.length();
        final short hostLen = (short)host.length();
        final short hostOff = 32;
        final short domainOff = (short)(hostOff + hostLen);
        ByteArrayOutputStream os = null;
        DataOutputStream dataOut = null;
        try {
            os = new ByteArrayOutputStream(1024);
            dataOut = new DataOutputStream(os);
            dataOut.writeBytes("NTLMSSP\u0000");
            dataOut.writeByte(1);
            dataOut.writeByte(0);
            dataOut.writeByte(0);
            dataOut.writeByte(0);
            dataOut.writeShort(switchBytes((short)(-19965)));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(domainLen));
            dataOut.writeShort(switchBytes(domainLen));
            dataOut.writeShort(switchBytes(domainOff));
            dataOut.writeShort(0);
            dataOut.writeShort(switchBytes(hostLen));
            dataOut.writeShort(switchBytes(hostLen));
            dataOut.writeShort(switchBytes(hostOff));
            dataOut.writeShort(0);
            dataOut.write(host.getBytes());
            dataOut.write(hostDomain.getBytes());
            dataOut.flush();
        }
        finally {
            os.close();
            dataOut.close();
        }
        return os.toByteArray();
    }
    
    static {
        ARRAY = new byte[] { 75, 71, 83, 33, 64, 35, 36, 37 };
    }
}
