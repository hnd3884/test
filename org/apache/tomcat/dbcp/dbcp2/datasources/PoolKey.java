package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.util.Objects;
import java.io.Serializable;

class PoolKey implements Serializable
{
    private static final long serialVersionUID = 2252771047542484533L;
    private final String dataSourceName;
    private final String userName;
    
    PoolKey(final String dataSourceName, final String userName) {
        this.dataSourceName = dataSourceName;
        this.userName = userName;
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
        final PoolKey other = (PoolKey)obj;
        return Objects.equals(this.dataSourceName, other.dataSourceName) && Objects.equals(this.userName, other.userName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.dataSourceName, this.userName);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(50);
        sb.append("PoolKey(");
        sb.append("UserName").append(", ").append(this.dataSourceName);
        sb.append(')');
        return sb.toString();
    }
}
