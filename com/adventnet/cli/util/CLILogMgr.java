package com.adventnet.cli.util;

import com.adventnet.management.log.LogBaseWriter;
import com.adventnet.management.log.DefaultLogUser;
import com.adventnet.management.log.NmsPrintStream;
import com.adventnet.management.log.LogMgr;
import com.adventnet.management.log.LogUser;

public class CLILogMgr
{
    private static boolean cliDebugOption;
    public static LogUser CLIUSER;
    public static LogUser CLIERR;
    private static boolean enableOutErr;
    private static boolean debugFlag;
    
    CLILogMgr() {
    }
    
    public CLILogMgr(final String s, final String s2) {
        this.init(s, s2);
    }
    
    private void init(final String errAndOut, String s) {
        try {
            final String property;
            if ((property = System.getProperty("logging.file")) != null) {
                s = property;
            }
            LogMgr.init(errAndOut, s);
            CLILogMgr.CLIUSER = this.assignLogUser(CLILogMgr.CLIUSER, "CLIUSER");
            CLILogMgr.CLIERR = this.assignLogUser(CLILogMgr.CLIERR, "CLIERR");
            if (CLILogMgr.enableOutErr) {
                NmsPrintStream.setErrAndOut(errAndOut);
            }
        }
        catch (final Exception ex) {
            System.err.println(" exception during logging parameters initialization " + ex);
        }
    }
    
    private LogUser assignLogUser(final LogUser logUser, final String s) {
        final LogUser logUser2 = LogMgr.getLogUser(s);
        if (logUser2 != null) {
            return logUser2;
        }
        return logUser;
    }
    
    public void setDebugLevelForLogging(int n) {
        new Boolean(System.getProperty("cli.debug", "false")).booleanValue();
        if (CLILogMgr.cliDebugOption) {
            n = 4;
        }
        CLILogMgr.CLIUSER.setLevel(n);
        CLILogMgr.CLIERR.setLevel(n);
    }
    
    public static void setEnableOutErr(final boolean enableOutErr) {
        CLILogMgr.enableOutErr = enableOutErr;
    }
    
    public static boolean getEnableOutErr() {
        return CLILogMgr.enableOutErr;
    }
    
    public static void setDebugOption(boolean debugFlag) {
        if (CLILogMgr.cliDebugOption) {
            debugFlag = true;
        }
        CLILogMgr.debugFlag = debugFlag;
    }
    
    public static boolean isDebugOption() {
        return CLILogMgr.debugFlag;
    }
    
    public static void setDebugMessage(final String s, final String s2, final int n, final Exception ex) {
        if (CLILogMgr.debugFlag && (s != null || s.length() > 1)) {
            if (s.trim().equalsIgnoreCase("CLIUSER")) {
                CLILogMgr.CLIUSER.log(s2, n);
            }
            else {
                CLILogMgr.CLIERR.fail(s2, (Throwable)ex);
            }
        }
    }
    
    static {
        CLILogMgr.cliDebugOption = false;
        CLILogMgr.cliDebugOption = new Boolean(System.getProperty("cli.debug", "false"));
        CLILogMgr.CLIUSER = (LogUser)new DefaultLogUser((String)null, 3, (LogBaseWriter)null);
        CLILogMgr.CLIERR = (LogUser)new DefaultLogUser((String)null, 3, (LogBaseWriter)null);
        CLILogMgr.enableOutErr = false;
        CLILogMgr.debugFlag = true;
    }
}
