package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

final class NTLM
{
    public static final String DEFAULT_CHARSET = "ASCII";
    private byte[] currentResponse;
    private int currentPosition;
    private String credentialCharset;
    
    NTLM() {
        this.currentPosition = 0;
        this.credentialCharset = "ASCII";
    }
    
    public final String getResponseFor(final String message, final String username, final String password, final String host, final String domain) throws AuthenticationException {
        String response;
        if (message == null || message.trim().equals("")) {
            response = this.getType1Message(host, domain);
        }
        else {
            response = this.getType3Message(username, password, host, domain, this.parseType2Message(message));
        }
        return response;
    }
    
    private Cipher getCipher(byte[] key) throws AuthenticationException {
        try {
            final Cipher ecipher = Cipher.getInstance("DES/ECB/NoPadding");
            key = this.setupKey(key);
            ecipher.init(1, new SecretKeySpec(key, "DES"));
            return ecipher;
        }
        catch (final NoSuchAlgorithmException e) {
            throw new AuthenticationException("DES encryption is not available.", e);
        }
        catch (final InvalidKeyException e2) {
            throw new AuthenticationException("Invalid key for DES encryption.", e2);
        }
        catch (final NoSuchPaddingException e3) {
            throw new AuthenticationException("NoPadding option for DES is not available.", e3);
        }
    }
    
    private byte[] setupKey(final byte[] key56) {
        final byte[] key57 = { (byte)(key56[0] >> 1 & 0xFF), (byte)(((key56[0] & 0x1) << 6 | ((key56[1] & 0xFF) >> 2 & 0xFF)) & 0xFF), (byte)(((key56[1] & 0x3) << 5 | ((key56[2] & 0xFF) >> 3 & 0xFF)) & 0xFF), (byte)(((key56[2] & 0x7) << 4 | ((key56[3] & 0xFF) >> 4 & 0xFF)) & 0xFF), (byte)(((key56[3] & 0xF) << 3 | ((key56[4] & 0xFF) >> 5 & 0xFF)) & 0xFF), (byte)(((key56[4] & 0x1F) << 2 | ((key56[5] & 0xFF) >> 6 & 0xFF)) & 0xFF), (byte)(((key56[5] & 0x3F) << 1 | ((key56[6] & 0xFF) >> 7 & 0xFF)) & 0xFF), (byte)(key56[6] & 0x7F) };
        for (int i = 0; i < key57.length; ++i) {
            key57[i] <<= 1;
        }
        return key57;
    }
    
    private byte[] encrypt(final byte[] key, final byte[] bytes) throws AuthenticationException {
        final Cipher ecipher = this.getCipher(key);
        try {
            final byte[] enc = ecipher.doFinal(bytes);
            return enc;
        }
        catch (final IllegalBlockSizeException e) {
            throw new AuthenticationException("Invalid block size for DES encryption.", e);
        }
        catch (final BadPaddingException e2) {
            throw new AuthenticationException("Data not padded correctly for DES encryption.", e2);
        }
    }
    
    private void prepareResponse(final int length) {
        this.currentResponse = new byte[length];
        this.currentPosition = 0;
    }
    
    private void addByte(final byte b) {
        this.currentResponse[this.currentPosition] = b;
        ++this.currentPosition;
    }
    
    private void addBytes(final byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            this.currentResponse[this.currentPosition] = bytes[i];
            ++this.currentPosition;
        }
    }
    
    private String getResponse() {
        byte[] resp;
        if (this.currentResponse.length > this.currentPosition) {
            final byte[] tmp = new byte[this.currentPosition];
            for (int i = 0; i < this.currentPosition; ++i) {
                tmp[i] = this.currentResponse[i];
            }
            resp = tmp;
        }
        else {
            resp = this.currentResponse;
        }
        return EncodingUtil.getAsciiString(Base64.encodeBase64(resp));
    }
    
    public String getType1Message(String host, String domain) {
        host = host.toUpperCase();
        domain = domain.toUpperCase();
        final byte[] hostBytes = EncodingUtil.getBytes(host, "ASCII");
        final byte[] domainBytes = EncodingUtil.getBytes(domain, "ASCII");
        final int finalLength = 32 + hostBytes.length + domainBytes.length;
        this.prepareResponse(finalLength);
        final byte[] protocol = EncodingUtil.getBytes("NTLMSSP", "ASCII");
        this.addBytes(protocol);
        this.addByte((byte)0);
        this.addByte((byte)1);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)6);
        this.addByte((byte)82);
        this.addByte((byte)0);
        this.addByte((byte)0);
        final int iDomLen = domainBytes.length;
        final byte[] domLen = this.convertShort(iDomLen);
        this.addByte(domLen[0]);
        this.addByte(domLen[1]);
        this.addByte(domLen[0]);
        this.addByte(domLen[1]);
        final byte[] domOff = this.convertShort(hostBytes.length + 32);
        this.addByte(domOff[0]);
        this.addByte(domOff[1]);
        this.addByte((byte)0);
        this.addByte((byte)0);
        final byte[] hostLen = this.convertShort(hostBytes.length);
        this.addByte(hostLen[0]);
        this.addByte(hostLen[1]);
        this.addByte(hostLen[0]);
        this.addByte(hostLen[1]);
        final byte[] hostOff = this.convertShort(32);
        this.addByte(hostOff[0]);
        this.addByte(hostOff[1]);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(hostBytes);
        this.addBytes(domainBytes);
        return this.getResponse();
    }
    
    public byte[] parseType2Message(final String message) {
        final byte[] msg = Base64.decodeBase64(EncodingUtil.getBytes(message, "ASCII"));
        final byte[] nonce = new byte[8];
        for (int i = 0; i < 8; ++i) {
            nonce[i] = msg[i + 24];
        }
        return nonce;
    }
    
    public String getType3Message(String user, final String password, String host, String domain, final byte[] nonce) throws AuthenticationException {
        final int ntRespLen = 0;
        final int lmRespLen = 24;
        domain = domain.toUpperCase();
        host = host.toUpperCase();
        user = user.toUpperCase();
        final byte[] domainBytes = EncodingUtil.getBytes(domain, "ASCII");
        final byte[] hostBytes = EncodingUtil.getBytes(host, "ASCII");
        final byte[] userBytes = EncodingUtil.getBytes(user, this.credentialCharset);
        final int domainLen = domainBytes.length;
        final int hostLen = hostBytes.length;
        final int userLen = userBytes.length;
        final int finalLength = 64 + ntRespLen + lmRespLen + domainLen + userLen + hostLen;
        this.prepareResponse(finalLength);
        final byte[] ntlmssp = EncodingUtil.getBytes("NTLMSSP", "ASCII");
        this.addBytes(ntlmssp);
        this.addByte((byte)0);
        this.addByte((byte)3);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(24));
        this.addBytes(this.convertShort(24));
        this.addBytes(this.convertShort(finalLength - 24));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(0));
        this.addBytes(this.convertShort(0));
        this.addBytes(this.convertShort(finalLength));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(domainLen));
        this.addBytes(this.convertShort(domainLen));
        this.addBytes(this.convertShort(64));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(userLen));
        this.addBytes(this.convertShort(userLen));
        this.addBytes(this.convertShort(64 + domainLen));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(hostLen));
        this.addBytes(this.convertShort(hostLen));
        this.addBytes(this.convertShort(64 + domainLen + userLen));
        for (int i = 0; i < 6; ++i) {
            this.addByte((byte)0);
        }
        this.addBytes(this.convertShort(finalLength));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)6);
        this.addByte((byte)82);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(domainBytes);
        this.addBytes(userBytes);
        this.addBytes(hostBytes);
        this.addBytes(this.hashPassword(password, nonce));
        return this.getResponse();
    }
    
    private byte[] hashPassword(final String password, final byte[] nonce) throws AuthenticationException {
        final byte[] passw = EncodingUtil.getBytes(password.toUpperCase(), this.credentialCharset);
        final byte[] lmPw1 = new byte[7];
        final byte[] lmPw2 = new byte[7];
        int len = passw.length;
        if (len > 7) {
            len = 7;
        }
        int idx;
        for (idx = 0; idx < len; ++idx) {
            lmPw1[idx] = passw[idx];
        }
        while (idx < 7) {
            lmPw1[idx] = 0;
            ++idx;
        }
        len = passw.length;
        if (len > 14) {
            len = 14;
        }
        for (idx = 7; idx < len; ++idx) {
            lmPw2[idx - 7] = passw[idx];
        }
        while (idx < 14) {
            lmPw2[idx - 7] = 0;
            ++idx;
        }
        final byte[] magic = { 75, 71, 83, 33, 64, 35, 36, 37 };
        final byte[] lmHpw1 = this.encrypt(lmPw1, magic);
        final byte[] lmHpw2 = this.encrypt(lmPw2, magic);
        final byte[] lmHpw3 = new byte[21];
        for (int i = 0; i < lmHpw1.length; ++i) {
            lmHpw3[i] = lmHpw1[i];
        }
        for (int i = 0; i < lmHpw2.length; ++i) {
            lmHpw3[i + 8] = lmHpw2[i];
        }
        for (int i = 0; i < 5; ++i) {
            lmHpw3[i + 16] = 0;
        }
        final byte[] lmResp = new byte[24];
        this.calcResp(lmHpw3, nonce, lmResp);
        return lmResp;
    }
    
    private void calcResp(final byte[] keys, final byte[] plaintext, final byte[] results) throws AuthenticationException {
        final byte[] keys2 = new byte[7];
        final byte[] keys3 = new byte[7];
        final byte[] keys4 = new byte[7];
        for (int i = 0; i < 7; ++i) {
            keys2[i] = keys[i];
        }
        for (int i = 0; i < 7; ++i) {
            keys3[i] = keys[i + 7];
        }
        for (int i = 0; i < 7; ++i) {
            keys4[i] = keys[i + 14];
        }
        final byte[] results2 = this.encrypt(keys2, plaintext);
        final byte[] results3 = this.encrypt(keys3, plaintext);
        final byte[] results4 = this.encrypt(keys4, plaintext);
        for (int j = 0; j < 8; ++j) {
            results[j] = results2[j];
        }
        for (int j = 0; j < 8; ++j) {
            results[j + 8] = results3[j];
        }
        for (int j = 0; j < 8; ++j) {
            results[j + 16] = results4[j];
        }
    }
    
    private byte[] convertShort(final int num) {
        final byte[] val = new byte[2];
        String hex;
        for (hex = Integer.toString(num, 16); hex.length() < 4; hex = "0" + hex) {}
        final String low = hex.substring(2, 4);
        final String high = hex.substring(0, 2);
        val[0] = (byte)Integer.parseInt(low, 16);
        val[1] = (byte)Integer.parseInt(high, 16);
        return val;
    }
    
    public String getCredentialCharset() {
        return this.credentialCharset;
    }
    
    public void setCredentialCharset(final String credentialCharset) {
        this.credentialCharset = credentialCharset;
    }
}
