package com.adventnet.cache.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.security.MessageDigest;
import java.util.logging.Logger;

public class CacheUtil
{
    private static final Logger LOG;
    private static final char[] HEX;
    private static MessageDigest md5Digest;
    
    public static long getDuration(final int day, final int hr, final int minutes) {
        if (minutes <= 0 && hr <= 0 && day <= 0) {
            return -1L;
        }
        long retLong = 60000L;
        if (minutes > 0) {
            retLong *= minutes;
        }
        if (hr > 0) {
            retLong = retLong * hr * 60L;
        }
        if (day > 0) {
            retLong = retLong * day * 24L * 60L;
        }
        return retLong;
    }
    
    public static String encodeAndEncryptString(String keyStr) {
        CacheUtil.LOG.log(Level.FINE, "Inside encodeAndEncryptString == {0} ", keyStr);
        keyStr = encodeKey(keyStr);
        CacheUtil.LOG.log(Level.FINE, "After encode== {0}", keyStr);
        return encryptString(keyStr);
    }
    
    public static String encryptString(final String keyStr) {
        if (keyStr != null) {
            if (keyStr.length() < 250) {
                return keyStr;
            }
            try {
                CacheUtil.LOG.log(Level.WARNING, "Key Length is more than 250. Give appropriate Key {0}", keyStr);
                final long stTime = System.currentTimeMillis();
                final byte[] keyBytes = CacheUtil.md5Digest.digest(keyStr.getBytes());
                final long endTime = System.currentTimeMillis();
                CacheUtil.LOG.log(Level.SEVERE, "Time Taken to Encrypt " + (endTime - stTime));
                return getString(keyBytes);
            }
            catch (final Exception exp) {
                CacheUtil.LOG.log(Level.SEVERE, "Exception while Encrypting", exp);
                return keyStr;
            }
        }
        return null;
    }
    
    private static String encodeKey(final String key) {
        try {
            return URLEncoder.encode(key, "UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            Logger.getLogger(CacheUtil.class.getName()).log(Level.SEVERE, null, ex);
            return key;
        }
    }
    
    private static String getString(final byte[] input) {
        CacheUtil.LOG.info("Inside getString method........");
        final char[] toReturn = new char[input.length * 2];
        int i = 0;
        for (final byte c : input) {
            final int low = c & 0xF;
            final int high = (c & 0xF0) >> 4;
            toReturn[i++] = CacheUtil.HEX[high];
            toReturn[i++] = CacheUtil.HEX[low];
        }
        return new String(toReturn);
    }
    
    static {
        LOG = Logger.getLogger(CacheUtil.class.getName());
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            CacheUtil.md5Digest = MessageDigest.getInstance("SHA256");
        }
        catch (final Exception exp) {
            CacheUtil.LOG.log(Level.SEVERE, "Exception while getting Instance", exp);
        }
    }
}
