package javax.activation;

public abstract class CommandMap
{
    private static CommandMap defaultCommandMap;
    static /* synthetic */ Class class$javax$activation$CommandMap;
    
    static {
        CommandMap.defaultCommandMap = null;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public abstract DataContentHandler createDataContentHandler(final String p0);
    
    public abstract CommandInfo[] getAllCommands(final String p0);
    
    public abstract CommandInfo getCommand(final String p0, final String p1);
    
    public static CommandMap getDefaultCommandMap() {
        if (CommandMap.defaultCommandMap == null) {
            CommandMap.defaultCommandMap = new MailcapCommandMap();
        }
        return CommandMap.defaultCommandMap;
    }
    
    public abstract CommandInfo[] getPreferredCommands(final String p0);
    
    public static void setDefaultCommandMap(final CommandMap defaultCommandMap) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                securityManager.checkSetFactory();
            }
            catch (final SecurityException ex) {
                if (((CommandMap.class$javax$activation$CommandMap != null) ? CommandMap.class$javax$activation$CommandMap : (CommandMap.class$javax$activation$CommandMap = class$("javax.activation.CommandMap"))).getClassLoader() != defaultCommandMap.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        CommandMap.defaultCommandMap = defaultCommandMap;
    }
}
