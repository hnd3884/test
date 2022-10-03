package com.microsoft.sqlserver.jdbc;

import java.time.Instant;
import com.google.gson.JsonArray;

class JWTCertificateEntry
{
    private static final long TWENTY_FOUR_HOUR_IN_SECONDS = 86400L;
    private JsonArray certificates;
    private long timeCreatedInSeconds;
    
    JWTCertificateEntry(final JsonArray j) {
        this.certificates = j;
        this.timeCreatedInSeconds = Instant.now().getEpochSecond();
    }
    
    boolean expired() {
        return Instant.now().getEpochSecond() - this.timeCreatedInSeconds > 86400L;
    }
    
    JsonArray getCertificates() {
        return this.certificates;
    }
}
