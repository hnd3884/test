package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum EntryRight
{
    ADD("add"), 
    DELETE("delete"), 
    READ("read"), 
    WRITE("write"), 
    PROXY("proxy");
    
    private final String name;
    
    private EntryRight(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static EntryRight forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "add": {
                return EntryRight.ADD;
            }
            case "delete": {
                return EntryRight.DELETE;
            }
            case "read": {
                return EntryRight.READ;
            }
            case "write": {
                return EntryRight.WRITE;
            }
            case "proxy": {
                return EntryRight.PROXY;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
