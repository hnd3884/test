package com.adventnet.tools.prevalent;

public final class Encode
{
    private static String INITIAL_STRING;
    private static char[] CHAR_MAP;
    
    private Encode() {
    }
    
    public static String getKey(final String user, final String company, final String emailId, final String mac, final String expiryDate, final String noOfDays, final String licenseType, final String productName, final String productVersion, final String productLType, final String categoryType) {
        return makeIt(user, company, emailId, mac, expiryDate, noOfDays, licenseType, productName, productVersion, productLType, categoryType, null, null, null, null, null, null, null);
    }
    
    public static String getKey(final String user, final String company, final String emailId, final String mac, final String expiryDate, final String noOfDays, final String licenseType, final String productName, final String productVersion, final String productLType, final String categoryType, final String noOfRTLicense, final String emailRestrict, final String generatedDate) {
        return makeIt(user, company, emailId, mac, expiryDate, noOfDays, licenseType, productName, productVersion, productLType, categoryType, noOfRTLicense, emailRestrict, generatedDate, null, null, null, null);
    }
    
    public static String getKey(final String user, final String company, final String email, final String mac, final String expiryDate, final String numberOfDays, final String userType, final String pName, final String pVersion, final String licType, final String catType, final String noOfRuntimeLicense, final String restrictEmail, final String currentDateString, final String maxTrialPeriod, final String trialMacPolicy) {
        return makeIt(user, company, email, mac, expiryDate, numberOfDays, userType, pName, pVersion, licType, catType, noOfRuntimeLicense, restrictEmail, currentDateString, maxTrialPeriod, trialMacPolicy, null, null);
    }
    
    public static String getKey(final String user, final String company, final String email, final String mac, final String expiryDate, final String numberOfDays, final String userType, final String pName, final String pVersion, final String licType, final String catType, final String noOfRuntimeLicense, final String restrictEmail, final String currentDateString, final String maxTrialPeriod, final String trialMacPolicy, final String noAllowed, final String expiryRelative) {
        return makeIt(user, company, email, mac, expiryDate, numberOfDays, userType, pName, pVersion, licType, catType, noOfRuntimeLicense, restrictEmail, currentDateString, maxTrialPeriod, trialMacPolicy, noAllowed, expiryRelative);
    }
    
    private static String makeIt(final String userName, final String companyName, final String mail, final String macAdd, final String expDate, final String days, final String type, final String pName, final String version, final String productLicType, final String productCatType, final String noOfRTLicense, final String emailRestrict, final String generatedDate, final String maxEvalPeriod, final String evalMacPolicy, final String numberAllowed, final String expiryRelative) {
        final StringBuffer strBuff = new StringBuffer();
        if (userName != null) {
            strBuff.append(swap(userName));
        }
        if (companyName != null) {
            strBuff.append(mapByChar(companyName));
        }
        if (mail != null) {
            strBuff.append(mapByChar(mail));
        }
        if (macAdd != null) {
            strBuff.append(mapByChar(macAdd));
        }
        if (expDate != null) {
            strBuff.append(swap(expDate));
        }
        if (days != null) {
            strBuff.append(swap(days));
        }
        if (type != null) {
            strBuff.append(mapByChar(type));
        }
        if (pName != null) {
            strBuff.append(mapByChar(pName));
        }
        if (version != null) {
            strBuff.append(swap(version));
        }
        if (productLicType != null) {
            strBuff.append(mapByChar(productLicType));
        }
        if (productCatType != null) {
            strBuff.append(mapByChar(productCatType));
        }
        if (noOfRTLicense != null) {
            strBuff.append(mapByChar(noOfRTLicense));
        }
        if (emailRestrict != null) {
            strBuff.append(mapByChar(emailRestrict));
        }
        if (numberAllowed != null) {
            strBuff.append(mapByChar(numberAllowed));
        }
        if (expiryRelative != null) {
            strBuff.append(mapByChar(expiryRelative));
        }
        if (generatedDate != null) {
            strBuff.append(mapByChar(generatedDate));
        }
        if (maxEvalPeriod != null) {
            strBuff.append(mapByChar(maxEvalPeriod));
        }
        if (evalMacPolicy != null) {
            strBuff.append(mapByChar(evalMacPolicy));
        }
        strBuff.reverse();
        final String temp = strBuff.toString();
        final String hash = String.valueOf(temp.hashCode());
        final int len = hash.length();
        strBuff.append(hash.charAt(len - 3));
        return Heraldry.getString(strBuff.toString());
    }
    
    public static String swap(final String str) {
        final char[] b = str.toCharArray();
        final int len = b.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; i += 2) {
            if (i + 1 < len) {
                ret[i] = b[i + 1];
                ret[i + 1] = b[i];
            }
            else {
                ret[i] = b[i];
            }
        }
        return reverse(ret);
    }
    
    public static int[] shiftBytes(final String str) {
        final char[] by = str.toCharArray();
        final int len = by.length;
        final int[] ret = new int[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = by[i] << 1;
        }
        return ret;
    }
    
    public static char[] revShiftBytes(final int[] by) {
        final int len = by.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = (char)(by[i] >> 1);
        }
        return ret;
    }
    
    public static String getFinalKey(final StringBuffer keyBuffer) {
        return mapByChar(keyBuffer.toString());
    }
    
    public static String mapByChar(final String str) {
        final char[] b = str.toCharArray();
        final int len = b.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; ++i) {
            final char ch = b[i];
            final int index = Encode.INITIAL_STRING.indexOf(ch);
            if (index == -1) {
                ret[i] = 's';
            }
            else {
                ret[i] = Encode.CHAR_MAP[index];
            }
        }
        return reverse(ret);
    }
    
    private static String reverse(final char[] arr) {
        final String ss = new String(arr);
        final StringBuffer sb = new StringBuffer();
        sb.append(ss);
        return sb.reverse().toString();
    }
    
    private static int hashCode(final String s) {
        int h = 0;
        int off = 0;
        final char[] val = s.toCharArray();
        for (int len = s.length(), i = 0; i < len; ++i) {
            h = 31 * h + val[off++];
        }
        return h;
    }
    
    static {
        Encode.INITIAL_STRING = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-.@_";
        Encode.CHAR_MAP = new char[] { 'X', 'Q', 'Y', 'G', 'v', 'F', 'i', 'C', '8', 'J', 'y', 'L', 'm', 'N', 'j', 'H', 'q', 'R', 's', '4', 'K', '3', 'v', '2', 'k', '9', '0', '1', 'W', 'V', 'T', '5', '6', 'i', 'E', 'Z', 'A', 'b', 'P', 'd', 'g', 'f', 'D', 'h', 'j', 'S', 'z', 'l', 'M', 'n', 'O', 'p', 'B', 'r', 'o', 't', 'w', 'e', 'a', 'x', 'c', 'u', 'U', 'I', 'a', '7' };
    }
}
