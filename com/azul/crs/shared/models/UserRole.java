package com.azul.crs.shared.models;

public enum UserRole
{
    ADMIN(0), 
    OPERATOR(1), 
    DEFAULT(2), 
    RUNTIME(3);
    
    private final int level;
    
    private UserRole(final int level) {
        this.level = level;
    }
    
    public boolean exceeds(final UserRole role) {
        return role == null || this.level < role.level;
    }
    
    public static UserRole valueOf(final String name, final UserRole defaultValue) {
        for (final UserRole value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultValue;
    }
}
