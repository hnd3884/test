package org.apache.poi.poifs.crypt.dsig.services;

public interface SignaturePolicyService
{
    String getSignaturePolicyIdentifier();
    
    String getSignaturePolicyDescription();
    
    String getSignaturePolicyDownloadUrl();
    
    byte[] getSignaturePolicyDocument();
}
