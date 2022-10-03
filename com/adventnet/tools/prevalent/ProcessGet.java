package com.adventnet.tools.prevalent;

public final class ProcessGet
{
    private static ProcessGet pget;
    
    private ProcessGet() {
    }
    
    public static ProcessGet getInstance() {
        if (ProcessGet.pget == null) {
            ProcessGet.pget = new ProcessGet();
        }
        return ProcessGet.pget;
    }
    
    public String getStringValue(final String str) {
        final String returnStr = EString.decode(str);
        return this.processString(returnStr).toString();
    }
    
    private StringBuffer processString(final String str) {
        final StringBuffer returnStrBuff = new StringBuffer();
        int i = 0;
        while (i < str.length()) {
            returnStrBuff.append((char)~(~str.charAt(i++) - 5));
        }
        return returnStrBuff;
    }
    
    static {
        ProcessGet.pget = null;
    }
}
