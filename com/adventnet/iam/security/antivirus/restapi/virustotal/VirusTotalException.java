package com.adventnet.iam.security.antivirus.restapi.virustotal;

import com.adventnet.iam.security.antivirus.AVScanFailureInfo;

public class VirusTotalException extends RuntimeException
{
    private AVScanFailureInfo.FailedCause cause;
    
    public VirusTotalException() {
    }
    
    public VirusTotalException(final String message) {
        super(message);
    }
    
    public VirusTotalException(final AVScanFailureInfo.FailedCause cause, final String message) {
        super(message);
        this.cause = cause;
    }
    
    public VirusTotalException(final Throwable cause) {
        super(cause);
    }
    
    public VirusTotalException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AVScanFailureInfo.FailedCause getFailedCause() {
        return this.cause;
    }
}
