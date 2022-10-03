package com.steadystate.css.userdata;

public final class UserDataConstants
{
    private static final String KEY_PREFIX;
    public static final String KEY_LOCATOR;
    
    private UserDataConstants() {
    }
    
    static {
        KEY_PREFIX = UserDataConstants.class.getPackage().getName();
        KEY_LOCATOR = UserDataConstants.KEY_PREFIX + ".locator";
    }
}
