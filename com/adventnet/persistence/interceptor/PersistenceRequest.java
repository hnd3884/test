package com.adventnet.persistence.interceptor;

import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import java.util.Set;
import java.util.Map;

public abstract class PersistenceRequest
{
    public static final int CREATE = 600;
    public static final int MODIFY = 601;
    public static final int DELETE = 602;
    public static final int RETRIEVE = 603;
    public static final int TERMINAL = 604;
    private Map<String, Object> requestProps;
    
    public abstract int getOperationType();
    
    public abstract Set<String> getTableList() throws DataAccessException;
    
    public void setRequestProperty(final String key, final Object value) {
        if (this.requestProps == null) {
            this.requestProps = new HashMap<String, Object>();
        }
        this.requestProps.put(key, value);
    }
    
    public Map<String, Object> getRequestProperty() {
        return this.requestProps;
    }
}
