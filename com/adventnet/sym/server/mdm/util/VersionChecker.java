package com.adventnet.sym.server.mdm.util;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionChecker
{
    public static Logger logger;
    
    public boolean isGreater(final String version1, final String version2) {
        if (!version1.matches("[0-9.]+$") || !version2.matches("[0-9.]+$")) {
            VersionChecker.logger.log(Level.SEVERE, "As version contains special characters version comparison is not done properly");
            return !version1.equals(version2);
        }
        return this.checkIfGreater(this.removeTrailingZeros(version1), this.removeTrailingZeros(version2));
    }
    
    public boolean isGreaterIncludeTrailingZeros(final String version1, final String version2) {
        return this.checkIfGreater(version1, version2);
    }
    
    public boolean isGreaterOrEqual(final String version1, final String version2) {
        if (!version1.matches("[0-9.]+$") || !version2.matches("[0-9.]+$")) {
            VersionChecker.logger.log(Level.SEVERE, "As version contains special characters version comparison is not done properly");
            return true;
        }
        return this.checkIfGreaterOrEqual(this.removeTrailingZeros(version1), this.removeTrailingZeros(version2));
    }
    
    public boolean isGreaterOrEqualIncludeTrailingZeros(final String version1, final String version2) {
        return this.checkIfGreaterOrEqual(version1, version2);
    }
    
    public boolean isEqual(final String version1, final String version2) {
        return this.removeTrailingZeros(version1).equalsIgnoreCase(this.removeTrailingZeros(version2));
    }
    
    private boolean checkIfGreater(final String version1, final String version2) {
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            long fir = 0L;
            long secon = 0L;
            if (tokenString != null) {
                fir = Long.parseLong(tokenString);
            }
            if (sec != null && !sec.isEmpty()) {
                secon = Long.parseLong(sec);
            }
            if (fir > secon) {
                return true;
            }
            if (fir < secon) {
                return false;
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        return tokencount > count;
    }
    
    private boolean checkIfGreaterOrEqual(final String version1, final String version2) {
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            long fir = 0L;
            long secon = 0L;
            if (tokenString != null) {
                fir = Long.parseLong(tokenString);
            }
            if (sec != null) {
                secon = Long.parseLong(sec);
            }
            if (fir > secon) {
                return true;
            }
            if (fir < secon) {
                return false;
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        return tokencount >= count;
    }
    
    private String getString(final String str, final int len) {
        String s1 = str;
        final int i1;
        if (len == 0 && (i1 = s1.indexOf(".")) != -1) {
            s1 = s1.substring(0, i1);
        }
        for (int j = 0; j < len; ++j) {
            final int i2 = s1.indexOf(".");
            s1 = ((i2 != -1) ? s1.substring(i2 + 1) : "0");
        }
        return s1;
    }
    
    public String removeTrailingZeros(final String string) {
        final StringBuffer buffer = new StringBuffer(string);
        final int length = buffer.length();
        for (int index = length - 1; index > 0; --index) {
            final char c = buffer.charAt(index);
            if (c == '.') {
                buffer.delete(index, buffer.length());
            }
            else if (c != '0') {
                break;
            }
        }
        return buffer.toString();
    }
    
    private String filterVersion(final String version) {
        if (!MDMStringUtils.isEmpty(version)) {
            final StringBuffer buffer = new StringBuffer(version.replaceAll("[^A-Za-z0-9.]", "."));
            final StringBuffer buffer2 = new StringBuffer();
            final StringTokenizer st = new StringTokenizer(buffer.toString(), ".");
            while (st.hasMoreTokens()) {
                buffer2.append(st.nextToken() + ".");
            }
            return buffer2.toString().substring(0, buffer2.toString().length() - 1);
        }
        return version;
    }
    
    public boolean checkIfFilteredVersionGreater(String version1, String version2) {
        version1 = this.filterVersion(version1);
        version2 = this.filterVersion(version2);
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            long fir = 0L;
            long secon = 0L;
            String firstChar = null;
            String secondChar = null;
            if (tokenString != null) {
                try {
                    fir = Long.parseLong(tokenString);
                }
                catch (final Exception e) {
                    firstChar = tokenString;
                }
            }
            if (sec != null) {
                try {
                    secon = Long.parseLong(sec);
                }
                catch (final Exception e) {
                    secondChar = sec;
                }
            }
            if (firstChar != null || secondChar != null) {
                if (firstChar == null && tokenString != null) {
                    firstChar = tokenString;
                }
                if (secondChar == null && sec != null) {
                    secondChar = sec;
                }
                if (firstChar.compareTo(secondChar) > 0) {
                    return true;
                }
            }
            else {
                if (fir > secon) {
                    return true;
                }
                if (fir < secon) {
                    return false;
                }
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        return tokencount > count;
    }
    
    static {
        VersionChecker.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
