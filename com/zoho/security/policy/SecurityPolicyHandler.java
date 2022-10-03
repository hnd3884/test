package com.zoho.security.policy;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.logging.Logger;

public abstract class SecurityPolicyHandler
{
    private static final Logger LOGGER;
    private static Map<String, Constructor<?>> customHandlers;
    protected String type;
    
    public SecurityPolicyHandler(final String type) {
        this.type = null;
        SecurityPolicyHandler.LOGGER.log(Level.FINEST, "No need to store any data");
    }
    
    public String getType() {
        return this.type;
    }
    
    public static synchronized void registerCustomHandler(final String type, final Class<?> classObj) {
        if (SecurityPolicyHandler.class.isAssignableFrom(classObj)) {
            try {
                final Constructor<?> cosnst = classObj.getConstructor(String.class);
                SecurityPolicyHandler.customHandlers.put(type, cosnst);
                return;
            }
            catch (final NoSuchMethodException | SecurityException e) {
                SecurityPolicyHandler.LOGGER.log(Level.SEVERE, "No valid constructor found for the Custom Handler. Class Name : {0}, Exception : {1}", new Object[] { classObj.getName(), e.getMessage() });
                throw new SecurityPolicyException("INVALID_HANDLER");
            }
        }
        SecurityPolicyHandler.LOGGER.log(Level.SEVERE, "Passed handler is not extended the SecurityPolicyHandler class. Class Name : {0}", classObj.getName());
        throw new SecurityPolicyException("INVALID_HANDLER");
    }
    
    public static SecurityPolicyHandler getInstance(final String type, final String value) {
        final POLICY policy = POLICY.valueOf(type.toUpperCase());
        switch (policy) {
            case IP: {
                return new IPPolicy(value);
            }
            case GEO_LOCATION: {
                return new GeoLocationPolicy(value);
            }
            default: {
                final Constructor<?> constructor = SecurityPolicyHandler.customHandlers.get(type);
                if (constructor != null) {
                    try {
                        return (SecurityPolicyHandler)constructor.newInstance(value);
                    }
                    catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        SecurityPolicyHandler.LOGGER.log(Level.SEVERE, "Exception occurred while creating instance for type : {0}, Class Name : {1}, Exception : {2}", new Object[] { type, constructor.getClass().getName(), e.getMessage() });
                    }
                }
                throw new SecurityPolicyException("INVALID_HANDLER_NAME");
            }
        }
    }
    
    public abstract boolean isAccessAllowed(final HttpServletRequest p0);
    
    static {
        LOGGER = Logger.getLogger(SecurityPolicyHandler.class.getName());
        SecurityPolicyHandler.customHandlers = new HashMap<String, Constructor<?>>();
    }
    
    public enum POLICY
    {
        IP, 
        GEO_LOCATION;
    }
}
