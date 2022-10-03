package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.util.Objects;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import java.io.Serializable;

class UserPassKey implements Serializable
{
    private static final long serialVersionUID = 5142970911626584817L;
    private final String userName;
    private final char[] userPassword;
    
    UserPassKey(final String userName) {
        this(userName, (char[])null);
    }
    
    UserPassKey(final String userName, final char[] password) {
        this.userName = userName;
        this.userPassword = password;
    }
    
    UserPassKey(final String userName, final String userPassword) {
        this(userName, Utils.toCharArray(userPassword));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final UserPassKey other = (UserPassKey)obj;
        return Objects.equals(this.userName, other.userName);
    }
    
    String getPassword() {
        return Utils.toString(this.userPassword);
    }
    
    char[] getPasswordCharArray() {
        return this.userPassword;
    }
    
    String getUserName() {
        return this.userName;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.userName);
    }
}
