package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.List;

public class RevocationData
{
    private final List<byte[]> crls;
    private final List<byte[]> ocsps;
    
    public RevocationData() {
        this.crls = new ArrayList<byte[]>();
        this.ocsps = new ArrayList<byte[]>();
    }
    
    public void addCRL(final byte[] encodedCrl) {
        this.crls.add(encodedCrl);
    }
    
    public void addCRL(final X509CRL crl) {
        byte[] encodedCrl;
        try {
            encodedCrl = crl.getEncoded();
        }
        catch (final CRLException e) {
            throw new IllegalArgumentException("CRL coding error: " + e.getMessage(), e);
        }
        this.addCRL(encodedCrl);
    }
    
    public void addOCSP(final byte[] encodedOcsp) {
        this.ocsps.add(encodedOcsp);
    }
    
    public List<byte[]> getCRLs() {
        return this.crls;
    }
    
    public List<byte[]> getOCSPs() {
        return this.ocsps;
    }
    
    public boolean hasOCSPs() {
        return !this.ocsps.isEmpty();
    }
    
    public boolean hasCRLs() {
        return !this.crls.isEmpty();
    }
    
    public boolean hasRevocationDataEntries() {
        return this.hasOCSPs() || this.hasCRLs();
    }
}
