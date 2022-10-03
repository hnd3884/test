package com.adventnet.tools.update.installer;

import java.util.StringTokenizer;

public class VersionChecker
{
    private final int EQUALS = 0;
    private final int GREATERTHAN = 1;
    private final int GREATERTHAN_EQUALS = 2;
    private final int LESSTHAN = 3;
    private final int LESSTHAN_EQUALS = 4;
    
    public boolean checkVersionCompatible(final String version, final String[] versions, final int option) {
        final StringTokenizer stoken = new StringTokenizer(version, ".");
        final int tokencount = stoken.countTokens();
        final String[] tokenString = new String[tokencount];
        int j = 0;
        String[] versionArray = versions;
        if (option == 0) {
            for (int i = 0; i < tokencount; ++i) {
                tokenString[j] = stoken.nextToken();
                versionArray = this.getVersionPresentArray(tokenString[j], versionArray, j);
                ++j;
            }
            return this.check(this.getString(version, j - 1), versionArray, j - 1, option);
        }
        if (option == 4 || option == 3) {
            for (int i = 0; i < tokencount; ++i) {
                tokenString[i] = stoken.nextToken();
            }
            for (int length = tokenString.length, k = 0; k < length - 1; ++k) {
                final String first = tokenString[k];
                final String second = tokenString[k + 1];
                try {
                    final int value = Integer.parseInt(second);
                    if (value == 0) {
                        final int val1 = Integer.parseInt(first);
                        final String[] firArray = this.getVersionPresentArray(Integer.toString(val1 - 1), versionArray, j);
                        final boolean firBoolean = this.check(this.getString(version, j), firArray, j, option);
                        final String[] secArray = this.getVersionPresentArray(first, versionArray, j);
                        final boolean secBoolean = this.check(this.getString(version, j + 1), secArray, j + 1, option);
                        return firBoolean || secBoolean;
                    }
                    versionArray = this.getVersionPresentArray(first, versionArray, j);
                    ++j;
                }
                catch (final Exception exp) {
                    exp.printStackTrace();
                }
            }
            return this.check(this.getString(version, j), versionArray, j, option);
        }
        for (int i = 0; i < tokencount - 1; ++i) {
            tokenString[j] = stoken.nextToken();
            versionArray = this.getVersionPresentArray(tokenString[j], versionArray, j);
            ++j;
        }
        return this.check(this.getString(version, j), versionArray, j, option);
    }
    
    private boolean check(final String str, final String[] list, final int val, final int option) {
        final int size = list.length;
        int k = 0;
        try {
            k = Integer.parseInt(str);
        }
        catch (final Exception exp) {
            final int ii = str.indexOf(".");
            if (ii != -1) {
                k = Integer.parseInt(str.substring(0, ii));
            }
        }
        for (int i = 0; i < size; ++i) {
            final String stri = this.getString(list[i], val);
            int j = 0;
            try {
                j = Integer.parseInt(stri);
            }
            catch (final Exception e) {
                final int ii2 = stri.indexOf(".");
                int test = 0;
                if (ii2 != -1) {
                    test = Integer.parseInt(stri.substring(0, ii2));
                }
                if (test == k) {
                    j = test + 1;
                }
                else {
                    j = test;
                }
            }
            if (option == 0) {
                if (j == k) {
                    return true;
                }
            }
            else if (option == 1) {
                if (j > k) {
                    return true;
                }
            }
            else if (option == 2) {
                if (j >= k) {
                    return true;
                }
            }
            else if (option == 3) {
                if (j < k) {
                    return true;
                }
            }
            else if (option == 4 && j <= k) {
                return true;
            }
        }
        return false;
    }
    
    private String getString(final String str, final int len) {
        String s1 = str;
        if (len == 0) {
            final int i1 = s1.indexOf(".");
            if (i1 != -1) {
                final String s2 = s1 = s1.substring(0, i1);
            }
        }
        for (int j = 0; j < len; ++j) {
            final int i2 = s1.indexOf(".");
            if (i2 != -1) {
                final String s3 = s1 = s1.substring(i2 + 1);
            }
            else {
                s1 = "0";
            }
        }
        return s1;
    }
    
    private String[] getVersionPresentArray(final String str, final String[] list, final int place) {
        String[] alist = new String[0];
        for (int i = 0; i < list.length; ++i) {
            final String s1 = list[i];
            String s2 = this.getString(s1, place);
            final int ii = s2.indexOf(".");
            if (ii != -1) {
                s2 = s2.substring(0, ii);
            }
            if (s2.equals(str)) {
                final int len = alist.length;
                final String[] tmp = new String[len + 1];
                System.arraycopy(alist, 0, tmp, 0, len);
                tmp[len] = s1;
                alist = tmp;
            }
        }
        return alist;
    }
    
    public int checkGreater(final String version1, final String version2) {
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            int fir = 0;
            int secon = 0;
            if (tokenString != null) {
                fir = Integer.parseInt(tokenString);
            }
            if (sec != null) {
                secon = Integer.parseInt(sec);
            }
            if (fir > secon) {
                return 0;
            }
            if (fir < secon) {
                return 1;
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        if (tokencount > count) {
            return 0;
        }
        return 1;
    }
}
