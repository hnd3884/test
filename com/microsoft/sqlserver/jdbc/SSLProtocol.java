package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.util.Locale;

enum SSLProtocol
{
    TLS("TLS"), 
    TLS_V10("TLSv1"), 
    TLS_V11("TLSv1.1"), 
    TLS_V12("TLSv1.2");
    
    private final String name;
    
    private SSLProtocol(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static SSLProtocol valueOfString(final String value) throws SQLServerException {
        SSLProtocol protocol = null;
        if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(SSLProtocol.TLS.toString())) {
            protocol = SSLProtocol.TLS;
        }
        else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(SSLProtocol.TLS_V10.toString())) {
            protocol = SSLProtocol.TLS_V10;
        }
        else if (value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(SSLProtocol.TLS_V11.toString())) {
            protocol = SSLProtocol.TLS_V11;
        }
        else {
            if (!value.toLowerCase(Locale.ENGLISH).equalsIgnoreCase(SSLProtocol.TLS_V12.toString())) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSSLProtocol"));
                final Object[] msgArgs = { value };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            protocol = SSLProtocol.TLS_V12;
        }
        return protocol;
    }
}
