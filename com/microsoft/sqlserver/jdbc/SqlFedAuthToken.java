package com.microsoft.sqlserver.jdbc;

import java.util.Date;
import java.io.Serializable;

class SqlFedAuthToken implements Serializable
{
    private static final long serialVersionUID = -1343105491285383937L;
    final Date expiresOn;
    final String accessToken;
    
    SqlFedAuthToken(final String accessToken, final long expiresIn) {
        this.accessToken = accessToken;
        final Date now = new Date();
        now.setTime(now.getTime() + expiresIn * 1000L);
        this.expiresOn = now;
    }
    
    SqlFedAuthToken(final String accessToken, final Date expiresOn) {
        this.accessToken = accessToken;
        this.expiresOn = expiresOn;
    }
}
