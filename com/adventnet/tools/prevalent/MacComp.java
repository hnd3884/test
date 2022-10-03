package com.adventnet.tools.prevalent;

import java.util.StringTokenizer;

public final class MacComp
{
    private static char[] CONTS;
    private static MacComp comp;
    
    private MacComp() {
    }
    
    public static MacComp getInstance() {
        if (MacComp.comp == null) {
            MacComp.comp = new MacComp();
        }
        return MacComp.comp;
    }
    
    private void printASCII() {
        for (int i = 0; i < 128; ++i) {
            if (i % 16 == 0) {}
        }
    }
    
    private String getTheReminder(final String str) {
        final int j = str.length();
        final StringBuffer strBuff = new StringBuffer();
        for (int i = 0; i < j; ++i) {
            final int n = this.getPos(str.charAt(i));
            strBuff.append(n);
            strBuff.append("*");
        }
        return strBuff.toString();
    }
    
    private int getPos(final char c) {
        return String.copyValueOf(MacComp.CONTS).indexOf(c);
    }
    
    private long getLongValue(final String str) {
        final int size = str.length();
        final int diff = 12 - size;
        if (diff > 0) {}
        long l = 0L;
        final StringTokenizer st = new StringTokenizer(str, "*");
        for (int i = 0; st.hasMoreTokens() && i < size; ++i) {
            final String temp = st.nextToken();
            final long templong = (long)Math.pow(64.0, 7 - i);
            l += templong * Long.parseLong(temp);
        }
        return l;
    }
    
    public String getTheFinalValue(final String s) {
        final String macVal = this.getTheReminder(s);
        final long lval = this.getLongValue(macVal);
        final String themac = Long.toHexString(lval);
        return themac;
    }
    
    public String getTheStringForProcess(final String s) {
        String themac = s;
        final int len = themac.length();
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 12 - len; ++i) {
            themac = "0" + themac;
        }
        for (int length = themac.length(), j = 0; j < length; j += 2) {
            buf.append(themac.substring(j, j + 2));
            buf.append(":");
        }
        final String macValue = buf.toString().substring(0, 17);
        return macValue;
    }
    
    public static String processString(final String m) {
        final StringTokenizer st = new StringTokenizer(m, ":");
        final int[] i = new int[st.countTokens()];
        int j = 0;
        while (st.hasMoreTokens()) {
            final String str = st.nextToken();
            try {
                i[j++] = Integer.parseInt(str, 16);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        final int val1 = i[0] + i[2] + i[4];
        final int val2 = i[1] + i[3] + i[5];
        final String val3 = String.valueOf(val1) + ":" + String.valueOf(val2);
        int val4 = i[0] + i[2] + i[4] - i[1] - i[3] - i[5];
        if (val4 < 0) {
            val4 = Math.abs(val4);
        }
        if (val4 > 255) {
            val4 /= 3;
        }
        final String hexStr = Integer.toHexString(val4);
        return hexStr;
    }
    
    static {
        MacComp.CONTS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', '[', 'm', 'n', '{', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '}', '=', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', ']', 'J', 'K', 'L', 'M', 'N', '#', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '?', '@' };
        MacComp.comp = null;
    }
}
