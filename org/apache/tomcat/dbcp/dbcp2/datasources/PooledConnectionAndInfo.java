package org.apache.tomcat.dbcp.dbcp2.datasources;

import org.apache.tomcat.dbcp.dbcp2.Utils;
import javax.sql.PooledConnection;

final class PooledConnectionAndInfo
{
    private final PooledConnection pooledConnection;
    private final char[] userPassword;
    private final String userName;
    private final UserPassKey upKey;
    
    PooledConnectionAndInfo(final PooledConnection pc, final String userName, final char[] userPassword) {
        this.pooledConnection = pc;
        this.userName = userName;
        this.userPassword = userPassword;
        this.upKey = new UserPassKey(userName, userPassword);
    }
    
    @Deprecated
    PooledConnectionAndInfo(final PooledConnection pc, final String userName, final String userPassword) {
        this(pc, userName, Utils.toCharArray(userPassword));
    }
    
    PooledConnection getPooledConnection() {
        return this.pooledConnection;
    }
    
    UserPassKey getUserPassKey() {
        return this.upKey;
    }
    
    String getPassword() {
        return Utils.toString(this.userPassword);
    }
    
    char[] getPasswordCharArray() {
        return this.userPassword;
    }
    
    String getUsername() {
        return this.userName;
    }
}
