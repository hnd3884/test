package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum ApplicationIntent
{
    READ_WRITE("readwrite"), 
    READ_ONLY("readonly");
    
    private final String value;
    
    private ApplicationIntent(final String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    static ApplicationIntent valueOfString(String value) throws SQLServerException {
        ApplicationIntent applicationIntent = ApplicationIntent.READ_WRITE;
        assert value != null;
        value = value.toUpperCase(Locale.US).toLowerCase(Locale.US);
        if (value.equalsIgnoreCase(ApplicationIntent.READ_ONLY.toString())) {
            applicationIntent = ApplicationIntent.READ_ONLY;
        }
        else {
            if (!value.equalsIgnoreCase(ApplicationIntent.READ_WRITE.toString())) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidapplicationIntent"));
                final Object[] msgArgs = { value };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            applicationIntent = ApplicationIntent.READ_WRITE;
        }
        return applicationIntent;
    }
}
