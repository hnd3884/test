package com.adventnet.sym.server.mdm.command.smscommand;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import java.util.Arrays;
import java.util.logging.Level;
import java.security.Key;
import java.security.InvalidKeyException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.logging.Logger;
import java.security.PublicKey;
import java.security.PrivateKey;

public class SmsKeysHandler
{
    private PrivateKey privateKey;
    private PublicKey publicKey;
    static Logger logger;
    
    public void generateKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(912);
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
    
    public SmsKeysHandler setPrivateKey(final String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] sigBytes = Base64.decodeBase64(privateKey);
        final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        keyFact = KeyFactory.getInstance("RSA");
        this.privateKey = keyFact.generatePrivate(pkcs8EncodedKeySpec);
        return this;
    }
    
    public SmsKeysHandler setPublicKey(final String publicKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] sigBytes = Base64.decodeBase64(publicKey);
        final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        keyFact = KeyFactory.getInstance("RSA");
        this.publicKey = keyFact.generatePublic(x509KeySpec);
        return this;
    }
    
    public String getPublicKey() {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final X509EncodedKeySpec publicKeySpec = keyFactory.getKeySpec(this.publicKey, X509EncodedKeySpec.class);
            return Base64.encodeBase64String(publicKeySpec.getEncoded());
        }
        catch (final Exception e) {
            SmsKeysHandler.logger.log(Level.WARNING, "Cannot get public key : {0}", e.toString());
            return null;
        }
    }
    
    public String getPrivateKey() {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = keyFactory.getKeySpec(this.privateKey, PKCS8EncodedKeySpec.class);
            final byte[] packed = pkcs8EncodedKeySpec.getEncoded();
            final String key64 = Base64.encodeBase64String(packed);
            Arrays.fill(packed, (byte)0);
            return key64;
        }
        catch (final Exception e) {
            SmsKeysHandler.logger.log(Level.WARNING, "Cannot get private key : {0}", e.toString());
            return null;
        }
    }
    
    public String encryptString(final String dataToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher RSACipher = Cipher.getInstance("RSA");
        RSACipher.init(1, this.privateKey);
        final byte[] encryptedBytes = RSACipher.doFinal(dataToEncrypt.getBytes());
        return Base64.encodeBase64String(encryptedBytes);
    }
    
    public String decryptString(final String dataToDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher RSACipher = Cipher.getInstance("RSA");
        RSACipher.init(2, this.publicKey);
        final byte[] encryptedDataInBytes = Base64.decodeBase64(dataToDecrypt);
        final byte[] decryptedBytes = RSACipher.doFinal(encryptedDataInBytes);
        return new String(decryptedBytes);
    }
    
    static {
        SmsKeysHandler.logger = Logger.getLogger(SmsDbHandler.class.getName());
    }
}
