package com.github.odiszapc.nginxparser;

import java.util.HashMap;
import java.util.Map;

public enum NgxEntryType
{
    PARAM((Class<? extends NgxEntry>)NgxParam.class), 
    COMMENT((Class<? extends NgxEntry>)NgxComment.class), 
    IF((Class<? extends NgxEntry>)NgxIfBlock.class), 
    BLOCK((Class<? extends NgxEntry>)NgxBlock.class);
    
    private final Class<? extends NgxEntry> clazz;
    private static Map<Class<? extends NgxEntry>, NgxEntryType> types;
    
    Class<? extends NgxEntry> getType() {
        return this.clazz;
    }
    
    private NgxEntryType(final Class<? extends NgxEntry> clazz) {
        this.clazz = clazz;
    }
    
    public static NgxEntryType fromClass(final Class<? extends NgxEntry> clazz) {
        return NgxEntryType.types.get(clazz);
    }
    
    static {
        NgxEntryType.types = new HashMap<Class<? extends NgxEntry>, NgxEntryType>();
        for (final NgxEntryType ngxEntryType : values()) {
            NgxEntryType.types.put(ngxEntryType.clazz, ngxEntryType);
        }
    }
}
