package com.me.devicemanagement.onpremise.server.twofactor;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;

public class GoogleTwoFactorPassword
{
    private static Logger logger;
    private static int window_size;
    
    public boolean handleAuth(final Long userId, final ServletRequest request) throws Exception {
        GoogleTwoFactorPassword.logger.log(Level.INFO, "In handle method of GoogleTwoFactorPassword");
        try {
            boolean isGAuthVerified = true;
            final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userId);
            isGAuthVerified = googleAuthAction.isFirstTimeStatus();
            if (!isGAuthVerified) {
                GoogleTwoFactorPassword.logger.log(Level.INFO, "Using Google Authentication for the first time hence fetching configuration details");
                final String barUrl = googleAuthAction.getQRBarPath();
                request.setAttribute("barUrl", (Object)barUrl);
                final String keyLabel = googleAuthAction.getKeyLabel();
                request.setAttribute("keyLabel", (Object)keyLabel);
                final String secret = googleAuthAction.getSecret();
                request.setAttribute("secretKey", (Object)secret);
            }
            final String sendEmail = request.getParameter("resendOTP");
            if (sendEmail != null && sendEmail.equalsIgnoreCase("true")) {
                TwoFactorAction.sendEmailInvitation(userId);
            }
        }
        catch (final Exception e) {
            GoogleTwoFactorPassword.logger.log(Level.SEVERE, "Caught exception while handling GoogleTwoFactorPassword", e);
        }
        return true;
    }
    
    public boolean validate(final Long userId, final HttpServletRequest request) throws Exception {
        GoogleTwoFactorPassword.logger.log(Level.INFO, "In validation method of GoogleTwoFactorPassword");
        boolean status = false;
        final String password = request.getParameter("2factor_password");
        final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userId);
        if (password == null || password.equals("")) {
            GoogleTwoFactorPassword.logger.log(Level.SEVERE, "In validating Google Two Factor Password....Password is null");
            return false;
        }
        long code;
        try {
            code = Long.parseLong(password);
        }
        catch (final NumberFormatException ex) {
            GoogleTwoFactorPassword.logger.log(Level.SEVERE, "Number Format Exception in validating Google Two Factor Password...." + ex);
            return false;
        }
        final long time = System.currentTimeMillis();
        try {
            status = checkTOTPCode(googleAuthAction.getSecret(), code, time);
        }
        catch (final NumberFormatException ex2) {
            GoogleTwoFactorPassword.logger.log(Level.SEVERE, "Exception in validating Google Two Factor Password...." + ex2);
            return false;
        }
        if (status) {
            GoogleTwoFactorPassword.logger.log(Level.INFO, "Second Factor verification completed successfully.Going to update totp Status...");
            googleAuthAction.setFirstTimeStatus(true);
            googleAuthAction.updateUserFirstTimeStatus(userId, true);
        }
        return status;
    }
    
    public static boolean checkTOTPCode(final String secret, final long code, final long timeMsec) {
        GoogleTwoFactorPassword.logger.log(Level.INFO, "Inside checkTOTPCode method");
        final SecretKeyEncoder codec = new SecretKeyEncoder();
        final byte[] decodedKey = SecretKeyEncoder.decode(secret);
        final long t = timeMsec / 1000L / 30L;
        for (int i = -GoogleTwoFactorPassword.window_size; i <= GoogleTwoFactorPassword.window_size; ++i) {
            long hash;
            try {
                hash = generateHashCode(decodedKey, t + i);
            }
            catch (final Exception e) {
                GoogleTwoFactorPassword.logger.log(Level.SEVERE, "Number Format Exception in checkTOTPCode method:" + e);
                throw new RuntimeException(e.getMessage());
            }
            if (hash == code) {
                GoogleTwoFactorPassword.logger.log(Level.INFO, "TOTPCode matched in checkTOTPCode method");
                return true;
            }
        }
        GoogleTwoFactorPassword.logger.log(Level.INFO, "No match for TOTPCode in checkTOTPCode method");
        return false;
    }
    
    private static int generateHashCode(final byte[] key, final long t) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] data = new byte[8];
        long value = t;
        int i = 8;
        while (i-- > 0) {
            data[i] = (byte)value;
            value >>>= 8;
        }
        final SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        final Mac mac = Mac.getInstance("HmacSHA1");
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
    
    static {
        GoogleTwoFactorPassword.logger = Logger.getLogger("UserManagementLogger");
        GoogleTwoFactorPassword.window_size = 3;
    }
}
