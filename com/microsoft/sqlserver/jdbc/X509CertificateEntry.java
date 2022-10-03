package com.microsoft.sqlserver.jdbc;

import java.time.Instant;

class X509CertificateEntry
{
    private static final long EIGHT_HOUR_IN_SECONDS = 28800L;
    private byte[] certificates;
    private long timeCreatedInSeconds;
    
    X509CertificateEntry(final byte[] b) {
        this.certificates = b;
        this.timeCreatedInSeconds = Instant.now().getEpochSecond();
    }
    
    boolean expired() {
        return Instant.now().getEpochSecond() - this.timeCreatedInSeconds > 28800L;
    }
    
    byte[] getCertificates() {
        return this.certificates;
    }
}
