package com.me.mdm.certificate;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.util.MDMCheckSumProvider;
import org.json.JSONObject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PublicKey;
import java.security.Key;
import javax.crypto.Cipher;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.util.logging.Logger;

public class CryptographyUtil
{
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int AES_KEY_SIZE = 16;
    private static final String AES_CIPHER_INSTANCE = "AES/GCM/NoPadding";
    private static Logger logger;
    private static final String AAD = "y0&C7#Bx10*1@Az";
    
    public static KeyPair createRsaKeyPair(final int keySize) throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
    
    public static byte[] encryptWithPublicKey(final String publicKey, final String inputData, final String alogrithm) throws Exception {
        final PublicKey key = KeyFactory.getInstance(alogrithm).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        final Cipher cipher = Cipher.getInstance(alogrithm);
        cipher.init(1, key);
        final byte[] encryptedBytes = cipher.doFinal(inputData.getBytes());
        return encryptedBytes;
    }
    
    public static byte[] encryptWithPublicKey(final String publicKey, final byte[] inputData, final String alogrithm) throws Exception {
        final PublicKey key = KeyFactory.getInstance(alogrithm).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        final Cipher cipher = Cipher.getInstance(alogrithm);
        cipher.init(1, key);
        final byte[] encryptedBytes = cipher.doFinal(inputData);
        return encryptedBytes;
    }
    
    public static byte[] encryptWithPrivateKey(final String privateKey, final String inputData, final String alogrithm) throws Exception {
        final PrivateKey key = KeyFactory.getInstance(alogrithm).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        final Cipher cipher = Cipher.getInstance(alogrithm);
        cipher.init(1, key);
        final byte[] encryptedBytes = cipher.doFinal(inputData.getBytes());
        return encryptedBytes;
    }
    
    public static byte[] decryptWithPrivateKey(final String privateKey, final byte[] inputData, final String alogrithm) throws Exception {
        final PrivateKey key = KeyFactory.getInstance(alogrithm).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        final Cipher cipher = Cipher.getInstance(alogrithm);
        cipher.init(2, key);
        final byte[] decryptedBytes = cipher.doFinal(inputData);
        return decryptedBytes;
    }
    
    public static byte[] decryptWithPublicKey(final String publicKey, final byte[] inputData, final String alogrithm) throws Exception {
        final PublicKey key = KeyFactory.getInstance(alogrithm).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        final Cipher cipher = Cipher.getInstance(alogrithm);
        cipher.init(2, key);
        final byte[] decryptedBytes = cipher.doFinal(inputData);
        return decryptedBytes;
    }
    
    private String getPrivateKeyFromDB(final Long customerId, final int keyType) throws Exception {
        final Criteria customerCri = new Criteria(Column.getColumn("KeyPairSummary", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria featureCri = new Criteria(Column.getColumn("KeyPairSummary", "KEY_TYPE"), (Object)keyType, 0);
        final Criteria finalCri = customerCri.and(featureCri);
        final DataObject resultDO = MDMUtil.getPersistence().get("KeyPairSummary", finalCri);
        if (!resultDO.isEmpty()) {
            final Row row = resultDO.getFirstRow("KeyPairSummary");
            MDMOneLineLogger.log(Level.INFO, "Private Key Fetched for ", "{0} {1}", new String[] { "FEATURE : ", String.valueOf(keyType) });
            return (String)row.get("PRIVATE_KEY");
        }
        return "";
    }
    
    public static SecretKey generateAESSecretKey() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        final SecretKey secretKey = new SecretKeySpec(key, "AES");
        MDMOneLineLogger.log(Level.INFO, "AES KEY Create", "Successful");
        return secretKey;
    }
    
    public static byte[] encryptWithAESKey(final String plaintext, final SecretKey secretKey, final byte[] associatedData) throws Exception {
        CryptographyUtil.logger.log(Level.INFO, "AES encryption starts here");
        final byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(1, secretKey, parameterSpec);
        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }
        final byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        final ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        CryptographyUtil.logger.log(Level.INFO, "AES encryption ends here");
        return byteBuffer.array();
    }
    
    public static String decryptWithAESKey(final byte[] cipherMessage, final SecretKey secretKey, final byte[] associatedData) throws Exception {
        CryptographyUtil.logger.log(Level.INFO, "AES decryption starts here");
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, 12);
        cipher.init(2, secretKey, gcmIv);
        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }
        final byte[] plainText = cipher.doFinal(cipherMessage, 12, cipherMessage.length - 12);
        CryptographyUtil.logger.log(Level.INFO, "AES decryption ends here");
        return new String(plainText, StandardCharsets.UTF_8);
    }
    
    public JSONObject encrypt(final String plainText, final String publicKey) throws Exception {
        CryptographyUtil.logger.log(Level.INFO, "Encrypting starts here");
        final JSONObject response = new JSONObject();
        final String code = MDMCheckSumProvider.getInstance().getSHA256HashFromString(plainText);
        final SecretKey key = generateAESSecretKey();
        final byte[] encryptedData = encryptWithAESKey(plainText, key, "y0&C7#Bx10*1@Az".getBytes(StandardCharsets.UTF_8));
        final String tag = Base64.getEncoder().encodeToString(encryptWithPublicKey(publicKey, key.getEncoded(), "RSA"));
        response.put("tag", (Object)tag);
        response.put("data", (Object)Base64.getEncoder().encodeToString(encryptedData));
        response.put("code", (Object)code);
        CryptographyUtil.logger.log(Level.INFO, "Encrypting end here");
        return response;
    }
    
    public JSONObject decrypt(final String encryptedText, final String tag, final String code, final int keyType) throws Exception {
        CryptographyUtil.logger.log(Level.INFO, "Decrypting for {0} start", keyType);
        final byte[] requestData = Base64.getDecoder().decode(encryptedText);
        final byte[] tagArray = Base64.getDecoder().decode(tag);
        final String privateKey = this.getPrivateKeyFromDB((long)CustomerInfoUtil.getInstance().getCustomerId(), keyType);
        final byte[] encodedKey = decryptWithPrivateKey(privateKey, tagArray, "RSA");
        final SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        final String response = decryptWithAESKey(requestData, key, "y0&C7#Bx10*1@Az".getBytes(StandardCharsets.UTF_8));
        final String destCode = MDMCheckSumProvider.getInstance().getSHA256HashFromString(response);
        if (code.compareTo(destCode) == 0) {
            CryptographyUtil.logger.log(Level.INFO, "Decrypting for {0} end", keyType);
            return new JSONObject(response);
        }
        CryptographyUtil.logger.log(Level.INFO, "HashCode Not Matching for {0}", keyType);
        throw new APIHTTPException("COM0004", new Object[0]);
    }
    
    static {
        CryptographyUtil.logger = Logger.getLogger("MDMLogger");
    }
}
