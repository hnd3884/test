package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum KeyStoreAuthentication
{
    JavaKeyStorePassword;
    
    static KeyStoreAuthentication valueOfString(final String value) throws SQLServerException {
        KeyStoreAuthentication method = null;
        if (value.toLowerCase(Locale.US).equalsIgnoreCase(KeyStoreAuthentication.JavaKeyStorePassword.toString())) {
            method = KeyStoreAuthentication.JavaKeyStorePassword;
            return method;
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
        final Object[] msgArgs = { "keyStoreAuthentication", value };
        throw new SQLServerException(form.format(msgArgs), (Throwable)null);
    }
}
