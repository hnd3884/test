package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum ColumnEncryptionSetting
{
    Enabled, 
    Disabled;
    
    static ColumnEncryptionSetting valueOfString(final String value) throws SQLServerException {
        ColumnEncryptionSetting method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(ColumnEncryptionSetting.Enabled.toString())) {
            method = ColumnEncryptionSetting.Enabled;
        }
        else {
            if (!value.toLowerCase(Locale.US).equalsIgnoreCase(ColumnEncryptionSetting.Disabled.toString())) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                final Object[] msgArgs = { "columnEncryptionSetting", value };
                throw new SQLServerException(form.format(msgArgs), (Throwable)null);
            }
            method = ColumnEncryptionSetting.Disabled;
        }
        return method;
    }
}
