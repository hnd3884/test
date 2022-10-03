package com.microsoft.sqlserver.jdbc;

final class SQLServerConnectionSecurityManager
{
    static final String dllName;
    String serverName;
    int portNumber;
    
    SQLServerConnectionSecurityManager(final String serverName, final int portNumber) {
        this.serverName = serverName;
        this.portNumber = portNumber;
    }
    
    public void checkConnect() throws SecurityException {
        final SecurityManager security = System.getSecurityManager();
        if (null != security) {
            security.checkConnect(this.serverName, this.portNumber);
        }
    }
    
    public void checkLink() throws SecurityException {
        final SecurityManager security = System.getSecurityManager();
        if (null != security) {
            security.checkLink(SQLServerConnectionSecurityManager.dllName);
        }
    }
    
    static {
        dllName = SQLServerDriver.AUTH_DLL_NAME + ".dll";
    }
}
