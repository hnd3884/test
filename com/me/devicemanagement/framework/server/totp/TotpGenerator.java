package com.me.devicemanagement.framework.server.totp;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import com.me.devicemanagement.framework.server.security.SecretKeyEncoder;
import java.util.logging.Logger;

public class TotpGenerator
{
    private static Logger logger;
    private static int keyLength;
    static final int NUMOFSCRATCHCODES = 5;
    static final int SCRATCHCODESIZE = 8;
    
    public Long generateTOTP(final String secret, final long timeInSec, final String algorithm) throws InvalidKeyException, NoSuchAlgorithmException {
        final byte[] decodedKey = SecretKeyEncoder.decode(secret);
        final long validityTime = System.currentTimeMillis() / 1000L / timeInSec;
        final long hash = generateHashCode(decodedKey, validityTime, algorithm);
        return hash;
    }
    
    private String generateSecretKey() {
        final byte[] buffer = new byte[TotpGenerator.keyLength + 40];
        new SecureRandom().nextBytes(buffer);
        final SecretKeyEncoder codec = new SecretKeyEncoder();
        final byte[] secretKey = Arrays.copyOf(buffer, TotpGenerator.keyLength);
        final String encodedKey = SecretKeyEncoder.encode(secretKey);
        return encodedKey;
    }
    
    private static int generateHashCode(final byte[] key, final long t, final String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] data = new byte[8];
        long value = t;
        int i = 8;
        while (i-- > 0) {
            data[i] = (byte)value;
            value >>>= 8;
        }
        final SecretKeySpec signKey = new SecretKeySpec(key, algorithm);
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(signKey);
        final byte[] hash = mac.doFinal(data);
        final int offset = hash[19] & 0xF;
        long truncatedHash = 0L;
        for (int j = 0; j < 4; ++j) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + j] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFFL;
        truncatedHash %= 1000000L;
        return (int)truncatedHash;
    }
    
    public Long addTotpDetails(final String algorithm, final Long timeInSec, final Long toleranceInSec) {
        final Row row = new Row("TotpDetails");
        Long totpId = null;
        try {
            final String secret = this.generateSecretKey();
            row.set("SECRET", (Object)secret);
            row.set("ALGORITHM", (Object)algorithm);
            row.set("VALIDITY_TIME", (Object)timeInSec);
            row.set("TOLERANCE", (Object)toleranceInSec);
            final DataObject dobj = SyMUtil.getPersistence().get("TotpDetails", (Criteria)null);
            dobj.addRow(row);
            SyMUtil.getPersistence().update(dobj);
            TotpGenerator.logger.log(Level.INFO, "Row Added in TOTPDETAILS table  successfully");
            final Row totpRow = dobj.getRow("TotpDetails", row);
            totpId = (Long)totpRow.get("TOTP_ID");
        }
        catch (final Exception e) {
            TotpGenerator.logger.log(Level.SEVERE, "Exception in adding TOTP Details" + e);
        }
        return totpId;
    }
    
    public void addRecoveryCodeDetails(final int recoveryCodePerDevice, final long totpId) {
        Row row = null;
        final DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            for (int i = 1; i <= recoveryCodePerDevice; ++i) {
                row = new Row("TotpRecoveryCode");
                row.set("TOTP_ID", (Object)totpId);
                row.set("RECOVERY_CODE", (Object)this.generateSecretKey());
                dataObject.addRow(row);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            TotpGenerator.logger.log(Level.SEVERE, "Exception in adding TOTPRECOVERYCODE Details " + e);
        }
    }
    
    static {
        TotpGenerator.logger = Logger.getLogger(TotpGenerator.class.getName());
        TotpGenerator.keyLength = 10;
    }
}
