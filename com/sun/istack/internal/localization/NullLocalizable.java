package com.sun.istack.internal.localization;

public final class NullLocalizable implements Localizable
{
    private final String msg;
    
    public NullLocalizable(final String msg) {
        if (msg == null) {
            throw new IllegalArgumentException();
        }
        this.msg = msg;
    }
    
    @Override
    public String getKey() {
        return "\u0000";
    }
    
    @Override
    public Object[] getArguments() {
        return new Object[] { this.msg };
    }
    
    @Override
    public String getResourceBundleName() {
        return "";
    }
}
