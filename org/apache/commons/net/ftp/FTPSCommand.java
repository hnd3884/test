package org.apache.commons.net.ftp;

@Deprecated
public final class FTPSCommand
{
    public static final int AUTH = 0;
    public static final int ADAT = 1;
    public static final int PBSZ = 2;
    public static final int PROT = 3;
    public static final int CCC = 4;
    public static final int AUTHENTICATION_SECURITY_MECHANISM = 0;
    public static final int AUTHENTICATION_SECURITY_DATA = 1;
    public static final int PROTECTION_BUFFER_SIZE = 2;
    public static final int DATA_CHANNEL_PROTECTION_LEVEL = 3;
    public static final int CLEAR_COMMAND_CHANNEL = 4;
    private static final String[] _commands;
    
    public static final String getCommand(final int command) {
        return FTPSCommand._commands[command];
    }
    
    static {
        _commands = new String[] { "AUTH", "ADAT", "PBSZ", "PROT", "CCC" };
    }
}
