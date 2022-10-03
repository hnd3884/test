package com.sun.security.jgss;

import jdk.Exported;
import java.security.BasicPermission;

@Exported
public final class InquireSecContextPermission extends BasicPermission
{
    private static final long serialVersionUID = -7131173349668647297L;
    
    public InquireSecContextPermission(final String s) {
        super(s);
    }
}
