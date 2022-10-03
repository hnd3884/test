package com.microsoft.sqlserver.jdbc;

import java.util.SimpleTimeZone;
import java.util.TimeZone;

final class UTC
{
    static final TimeZone timeZone;
    
    private UTC() {
    }
    
    static {
        timeZone = new SimpleTimeZone(0, "UTC");
    }
}
