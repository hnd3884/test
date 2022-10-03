package com.adventnet.sym.server.mdm.certificates.scep.request;

import java.security.cert.X509Certificate;

public interface ScepEnrollmentRequestValidator
{
    boolean isEligibleForRenewal(final X509Certificate p0);
    
    boolean isValidPasscode(final String p0);
}
