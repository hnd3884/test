package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AttributeRight
{
    SEARCH("search"), 
    READ("read"), 
    COMPARE("compare"), 
    WRITE("write"), 
    SELFWRITE_ADD("selfwrite_add"), 
    SELFWRITE_DELETE("selfwrite_delete"), 
    PROXY("proxy");
    
    private final String name;
    
    private AttributeRight(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static AttributeRight forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "search": {
                return AttributeRight.SEARCH;
            }
            case "read": {
                return AttributeRight.READ;
            }
            case "compare": {
                return AttributeRight.COMPARE;
            }
            case "write": {
                return AttributeRight.WRITE;
            }
            case "selfwriteadd":
            case "selfwrite-add":
            case "selfwrite_add":
            case "self-write-add":
            case "self_write_add": {
                return AttributeRight.SELFWRITE_ADD;
            }
            case "selfwritedelete":
            case "selfwrite-delete":
            case "selfwrite_delete":
            case "self-write-delete":
            case "self_write_delete":
            case "selfwritedel":
            case "selfwrite-del":
            case "selfwrite_del":
            case "self-write-del":
            case "self_write_del": {
                return AttributeRight.SELFWRITE_DELETE;
            }
            case "proxy": {
                return AttributeRight.PROXY;
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
