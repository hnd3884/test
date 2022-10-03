package com.adventnet.iam.security.antivirus.icap;

import com.adventnet.iam.security.antivirus.AVScanFailureInfo;

public class IcapClientException extends RuntimeException
{
    private AVScanFailureInfo.FailedCause cause;
    
    public IcapClientException() {
    }
    
    public IcapClientException(final AVScanFailureInfo.FailedCause cause, final String message) {
        super(message);
        this.cause = cause;
    }
    
    public AVScanFailureInfo.FailedCause getFailedCause() {
        return this.cause;
    }
    
    public IcapClientException(final String message) {
        super(message);
    }
    
    public IcapClientException(final Throwable cause) {
        super(cause);
    }
    
    public IcapClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
