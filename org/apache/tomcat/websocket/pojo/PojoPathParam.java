package org.apache.tomcat.websocket.pojo;

import org.apache.tomcat.websocket.Util;
import javax.websocket.DeploymentException;
import org.apache.tomcat.util.res.StringManager;

public class PojoPathParam
{
    private static final StringManager sm;
    private final Class<?> type;
    private final String name;
    
    public PojoPathParam(final Class<?> type, final String name) throws DeploymentException {
        if (name != null) {
            validateType(type);
        }
        this.type = type;
        this.name = name;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public String getName() {
        return this.name;
    }
    
    private static void validateType(final Class<?> type) throws DeploymentException {
        if (String.class == type) {
            return;
        }
        if (Util.isPrimitive(type)) {
            return;
        }
        throw new DeploymentException(PojoPathParam.sm.getString("pojoPathParam.wrongType", new Object[] { type.getName() }));
    }
    
    static {
        sm = StringManager.getManager((Class)PojoPathParam.class);
    }
}
