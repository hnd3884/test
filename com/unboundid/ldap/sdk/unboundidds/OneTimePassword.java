package com.unboundid.ldap.sdk.unboundidds;

import javax.crypto.SecretKey;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.unboundid.ldap.sdk.ResultCode;
import java.text.DecimalFormat;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OneTimePassword
{
    public static final int DEFAULT_HOTP_NUM_DIGITS = 6;
    public static final int DEFAULT_TOTP_INTERVAL_DURATION_SECONDS = 30;
    public static final int DEFAULT_TOTP_NUM_DIGITS = 6;
    private static final String HMAC_ALGORITHM_SHA_1 = "HmacSHA1";
    private static final String KEY_ALGORITHM_RAW = "RAW";
    
    private OneTimePassword() {
    }
    
    public static String hotp(final byte[] sharedSecret, final long counter) throws LDAPException {
        return hotp(sharedSecret, counter, 6);
    }
    
    public static String hotp(final byte[] sharedSecret, final long counter, final int numDigits) throws LDAPException {
        try {
            int modulus = 0;
            DecimalFormat decimalFormat = null;
            switch (numDigits) {
                case 6: {
                    modulus = 1000000;
                    decimalFormat = new DecimalFormat("000000");
                    break;
                }
                case 7: {
                    modulus = 10000000;
                    decimalFormat = new DecimalFormat("0000000");
                    break;
                }
                case 8: {
                    modulus = 100000000;
                    decimalFormat = new DecimalFormat("00000000");
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.PARAM_ERROR, UnboundIDDSMessages.ERR_HOTP_INVALID_NUM_DIGITS.get(numDigits));
                }
            }
            final byte[] counterBytes = { (byte)(counter >> 56 & 0xFFL), (byte)(counter >> 48 & 0xFFL), (byte)(counter >> 40 & 0xFFL), (byte)(counter >> 32 & 0xFFL), (byte)(counter >> 24 & 0xFFL), (byte)(counter >> 16 & 0xFFL), (byte)(counter >> 8 & 0xFFL), (byte)(counter & 0xFFL) };
            final SecretKey k = new SecretKeySpec(sharedSecret, "RAW");
            final Mac m = Mac.getInstance("HmacSHA1");
            m.init(k);
            final byte[] hmacBytes = m.doFinal(counterBytes);
            final int dtOffset = hmacBytes[19] & 0xF;
            final int dtValue = (hmacBytes[dtOffset] & 0x7F) << 24 | (hmacBytes[dtOffset + 1] & 0xFF) << 16 | (hmacBytes[dtOffset + 2] & 0xFF) << 8 | (hmacBytes[dtOffset + 3] & 0xFF);
            return decimalFormat.format(dtValue % modulus);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UnboundIDDSMessages.ERR_HOTP_ERROR_GENERATING_PW.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static String totp(final byte[] sharedSecret) throws LDAPException {
        return totp(sharedSecret, System.currentTimeMillis(), 30, 6);
    }
    
    public static String totp(final byte[] sharedSecret, final long authTime, final int intervalDurationSeconds, final int numDigits) throws LDAPException {
        if (numDigits < 6 || numDigits > 8) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UnboundIDDSMessages.ERR_TOTP_INVALID_NUM_DIGITS.get(numDigits));
        }
        try {
            final long timeIntervalNumber = authTime / 1000L / intervalDurationSeconds;
            return hotp(sharedSecret, timeIntervalNumber, numDigits);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UnboundIDDSMessages.ERR_TOTP_ERROR_GENERATING_PW.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
}
