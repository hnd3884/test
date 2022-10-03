package org.bouncycastle.jce.provider;

import java.util.Date;

class CertStatus
{
    public static final int UNREVOKED = 11;
    public static final int UNDETERMINED = 12;
    int certStatus;
    Date revocationDate;
    
    CertStatus() {
        this.certStatus = 11;
        this.revocationDate = null;
    }
    
    public Date getRevocationDate() {
        return this.revocationDate;
    }
    
    public void setRevocationDate(final Date revocationDate) {
        this.revocationDate = revocationDate;
    }
    
    public int getCertStatus() {
        return this.certStatus;
    }
    
    public void setCertStatus(final int certStatus) {
        this.certStatus = certStatus;
    }
}
