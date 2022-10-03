package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum SqlAuthentication
{
    NotSpecified, 
    SqlPassword, 
    ActiveDirectoryPassword, 
    ActiveDirectoryIntegrated, 
    ActiveDirectoryMSI;
    
    static SqlAuthentication valueOfString(final String value) throws SQLServerException {
        SqlAuthentication method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(SqlAuthentication.NotSpecified.toString())) {
            method = SqlAuthentication.NotSpecified;
        }
        else if (value.toLowerCase(Locale.US).equalsIgnoreCase(SqlAuthentication.SqlPassword.toString())) {
            method = SqlAuthentication.SqlPassword;
        }
        else if (value.toLowerCase(Locale.US).equalsIgnoreCase(SqlAuthentication.ActiveDirectoryPassword.toString())) {
            method = SqlAuthentication.ActiveDirectoryPassword;
        }
        else if (value.toLowerCase(Locale.US).equalsIgnoreCase(SqlAuthentication.ActiveDirectoryIntegrated.toString())) {
            method = SqlAuthentication.ActiveDirectoryIntegrated;
        }
        else {
            if (!value.toLowerCase(Locale.US).equalsIgnoreCase(SqlAuthentication.ActiveDirectoryMSI.toString())) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                final Object[] msgArgs = { "authentication", value };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            method = SqlAuthentication.ActiveDirectoryMSI;
        }
        return method;
    }
}
