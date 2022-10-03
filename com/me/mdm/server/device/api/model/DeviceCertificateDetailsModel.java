package com.me.mdm.server.device.api.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCertificateDetailsModel
{
    @JsonProperty("managedcertificates")
    private List<CertifcateDetailsModel> managedCertificates;
    @JsonProperty("unmanagedcertificates")
    private List<CertifcateDetailsModel> unManagedCertificates;
    
    public DeviceCertificateDetailsModel() {
        this.managedCertificates = new ArrayList<CertifcateDetailsModel>();
        this.unManagedCertificates = new ArrayList<CertifcateDetailsModel>();
    }
    
    public List<CertifcateDetailsModel> getManagedCertificates() {
        return this.managedCertificates;
    }
    
    public void setManagedCertificates(final List<CertifcateDetailsModel> managedCertificates) {
        this.managedCertificates = managedCertificates;
    }
    
    public List<CertifcateDetailsModel> getUnManagedCertificates() {
        return this.unManagedCertificates;
    }
    
    public void setUnManagedCertificates(final List<CertifcateDetailsModel> unManagedCertificates) {
        this.unManagedCertificates = unManagedCertificates;
    }
}
