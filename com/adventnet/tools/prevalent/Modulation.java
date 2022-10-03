package com.adventnet.tools.prevalent;

import java.util.StringTokenizer;

public final class Modulation
{
    private static Modulation key;
    
    private Modulation() {
    }
    
    public static Modulation getInstance() {
        if (Modulation.key == null) {
            Modulation.key = new Modulation();
        }
        return Modulation.key;
    }
    
    public String getKey(final String user, final String company, final String product, final String type, final String version, final String mac) throws Exception {
        return this.getKey(user, company, product, type, version, null, null, mac);
    }
    
    public String getKey(String user, String company, final String product, final String type, final String version, String date, String userType, String mac) throws Exception {
        user = this.processUser(user);
        company = this.processComp(company);
        final char prod = this.getProduct(product);
        final char pType = this.getType(type);
        final char ver = this.getVersion(version);
        if (mac.equals("@@")) {
            mac = mac;
        }
        else {
            mac = this.getMac(mac);
        }
        if (date != null) {
            date = this.processDate(date);
        }
        else {
            date = "@@@";
        }
        if (userType != null) {
            if (userType.equals("T") || userType.equals("Trial user") || userType.equals("TRIAL USER")) {
                userType = "t";
            }
            else {
                userType = "@";
            }
        }
        else {
            userType = "@";
        }
        return this.makeIt(user, company, prod, pType, ver, date, userType, mac);
    }
    
    private String processUser(String user) {
        final StringBuffer returnBuff = new StringBuffer();
        final int size = user.length();
        if (size <= 5) {
            for (int diff = 5 - size, i = 0; i < diff; ++i) {
                user += "@";
            }
            return user;
        }
        if (size <= 10) {
            for (int diff = 10 - size, i = 0; i < diff; ++i) {
                user += "@";
            }
            for (int i = 0; i < 10; i += 2) {
                final char c = user.charAt(i);
                returnBuff.append(c);
            }
            return returnBuff.toString();
        }
        for (int diff = 15 - size, i = 0; i < diff; ++i) {
            user += "@";
        }
        for (int counter = 2, j = 0; j < 15; j += counter, ++counter) {
            final char c2 = user.charAt(j);
            returnBuff.append(c2);
        }
        return returnBuff.toString();
    }
    
    private String processComp(String company) {
        final int size = company.length();
        if (size < 5) {
            for (int diff = 5 - size, i = 0; i < diff; ++i) {
                company += "@";
            }
        }
        final StringBuffer returnBuff = new StringBuffer();
        returnBuff.append(company.charAt(1));
        returnBuff.append(company.charAt(4));
        return returnBuff.toString();
    }
    
    private char getProduct(final String product) throws Exception {
        return this.getChar(product);
    }
    
    private String getMac(final String mac) throws Exception {
        String str = mac;
        if (str.length() == 1) {
            str = "0" + str;
        }
        final String str2 = str.substring(0, 1);
        final String str3 = str.substring(1);
        final StringBuffer array = new StringBuffer(2);
        array.append(this.getChar(String.valueOf(Integer.parseInt(str2, 16))));
        array.append(this.getChar(String.valueOf(Integer.parseInt(str3, 16))));
        return array.toString();
    }
    
    private char getType(final String type) {
        if (type == null) {
            return '@';
        }
        return type.charAt(0);
    }
    
    private char getVersion(final String version) throws Exception {
        final int i = version.indexOf(".");
        String ver = null;
        if (i != -1) {
            ver = version.substring(0, i);
        }
        else {
            ver = version;
        }
        if (ver.equals("4")) {
            ver = "2";
        }
        return this.getChar(ver);
    }
    
    private String processDate(final String date) throws Exception {
        final StringTokenizer st = new StringTokenizer(date, " ");
        final StringBuffer returnBuff = new StringBuffer();
        while (st.hasMoreTokens()) {
            final String str = st.nextToken();
            returnBuff.append(this.getChar(str));
        }
        return returnBuff.toString();
    }
    
    private char getChar(final String s) throws Exception {
        final int p = Integer.parseInt(s);
        return ChordsConts.CONTS[p];
    }
    
    public int getInt(final char c) {
        return ChordsConts.getPos(c);
    }
    
    private String makeIt(final String user, final String company, final char product, final char type, final char version, final String date, final String userType, final String mac) {
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(user.charAt(3));
        strBuff.append(company.charAt(1));
        strBuff.append(date);
        strBuff.append(user.charAt(4));
        strBuff.append(product);
        strBuff.append(user.charAt(0));
        strBuff.append(userType);
        strBuff.append(type);
        strBuff.append(mac);
        strBuff.append(user.charAt(2));
        strBuff.append(version);
        strBuff.append(company.charAt(0));
        strBuff.append(user.charAt(1));
        strBuff.reverse();
        final String temp = strBuff.toString();
        final String hc = String.valueOf(temp.hashCode());
        final int l = hc.length();
        strBuff.append(hc.charAt(l - 1));
        return AString.getString(hc.charAt(l - 3) + this.processStringBuff(strBuff).toString());
    }
    
    private StringBuffer processStringBuff(final StringBuffer strBuff) {
        final StringBuffer returnStrBuff = new StringBuffer();
        int i = 0;
        while (i < strBuff.length()) {
            returnStrBuff.append((char)~(~strBuff.charAt(i++) + 5));
        }
        return returnStrBuff;
    }
    
    public String getOldKey(final String user, final String company, final String product, final String version, final int type, final int node) {
        return this.encode(user, company, product, version, type, node);
    }
    
    private String encode(final String user, final String company, final String product, final String version, final int type, final int node) {
        final StringBuffer encryptedString = new StringBuffer();
        final StringBuffer addedString = new StringBuffer(this.getStringToEncrypt(user, company, product, version, type, node));
        for (int t_length = 13, endIndex = addedString.length(), i = 0; i < endIndex; i += 2, t_length -= 2) {
            encryptedString.append((char)(~(~addedString.charAt(i)) + 5));
            if (t_length > 0) {
                encryptedString.append((char)(~(~addedString.charAt(t_length)) + 5));
            }
        }
        return BString.encode(encryptedString.toString());
    }
    
    private String getStringToEncrypt(final String user, final String company, final String product, final String version, final int type, final int node) {
        StringBuffer stringToEncrypt = new StringBuffer();
        final String userName = user;
        int length = userName.length();
        if (length >= 14) {
            for (int i = 0; i < 14; i += 2) {
                stringToEncrypt.append(userName.charAt(i));
            }
        }
        else if (length >= 7) {
            for (int i = 0; i < 7; ++i) {
                stringToEncrypt.append(userName.charAt(i));
            }
        }
        else {
            stringToEncrypt.append(userName);
            for (int padlength = 7 - length, j = 1; j <= padlength; ++j) {
                stringToEncrypt.append("@");
            }
        }
        final String companyName = company;
        length = companyName.length();
        if (length >= 4) {
            for (int j = 0; j < 4; j += 2) {
                stringToEncrypt.append(companyName.charAt(j));
            }
        }
        else if (length >= 2) {
            for (int j = 0; j < 2; ++j) {
                stringToEncrypt.append(companyName.charAt(j));
            }
        }
        else {
            stringToEncrypt.append(companyName);
            for (int padlength2 = 2 - length, k = 1; k <= padlength2; ++k) {
                stringToEncrypt.append("@");
            }
        }
        stringToEncrypt.append(type);
        stringToEncrypt.append(node);
        final String productName = product;
        length = productName.length();
        if (length == 2) {
            for (int k = 0; k < 2; ++k) {
                stringToEncrypt.append(productName.charAt(k));
            }
        }
        else if (length == 1) {
            stringToEncrypt.append("0");
            stringToEncrypt.append(productName);
        }
        final Long sum = new Long(this.hashCode(stringToEncrypt.toString()));
        final int sumlength = sum.toString().length();
        String temp = "" + sum.toString().charAt(sumlength - 1);
        temp += stringToEncrypt.toString();
        stringToEncrypt = new StringBuffer(temp);
        stringToEncrypt.append("" + sum.toString().charAt(sumlength - 2));
        return stringToEncrypt.toString();
    }
    
    private int hashCode(final String s) {
        int h = 0;
        int off = 0;
        final char[] val = s.toCharArray();
        for (int len = s.length(), i = 0; i < len; ++i) {
            h = 31 * h + val[off++];
        }
        return h;
    }
    
    static {
        Modulation.key = null;
    }
}
