package com.me.ems.onpremise.security.certificate.api.model;

import java.util.ArrayList;
import java.nio.file.Path;

public class CertificateFormBean
{
    private Boolean automatic;
    private Boolean intermediateGiven;
    private Boolean rootGiven;
    private int intermediateCount;
    private Path serverCertificateFilePath;
    private Path privateKeyFilePath;
    private String pfxPassword;
    private ArrayList<Path> intermediateCertificateFilePathList;
    private Boolean confirmedChangeInNatSettings;
    private Boolean isSelfSignedCAConfirmed;
    private Boolean isPFXCertificateUploaded;
    private String serverCertificateFileName;
    
    public CertificateFormBean() {
        this.intermediateGiven = Boolean.FALSE;
        this.rootGiven = Boolean.FALSE;
        this.pfxPassword = "";
    }
    
    public Boolean getAutomatic() {
        return this.automatic;
    }
    
    public void setAutomatic(final Boolean automatic) {
        this.automatic = automatic;
    }
    
    public Boolean getIntermediateGiven() {
        return this.intermediateGiven;
    }
    
    public void setIntermediateGiven(final Boolean intermediateGiven) {
        this.intermediateGiven = intermediateGiven;
    }
    
    public Boolean getRootGiven() {
        return this.rootGiven;
    }
    
    public void setRootGiven(final Boolean rootGiven) {
        this.rootGiven = rootGiven;
    }
    
    public int getIntermediateCount() {
        return this.intermediateCount;
    }
    
    public void setIntermediateCount(final int intermediateCount) {
        this.intermediateCount = intermediateCount;
    }
    
    public Path getServerCertificateFilePath() {
        return this.serverCertificateFilePath;
    }
    
    public void setServerCertificateFilePath(final Path serverCertificateFilePath) {
        this.serverCertificateFilePath = serverCertificateFilePath;
    }
    
    public Path getPrivateKeyFilePath() {
        return this.privateKeyFilePath;
    }
    
    public void setPrivateKeyFilePath(final Path privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }
    
    public String getPfxPassword() {
        return this.pfxPassword;
    }
    
    public void setPfxPassword(final String pfxPassword) {
        this.pfxPassword = pfxPassword;
    }
    
    public ArrayList<Path> getIntermediateCertificateFilePathList() {
        return this.intermediateCertificateFilePathList;
    }
    
    public void setIntermediateCertificateFilePathList(final ArrayList<Path> intermediateCertificateFilePathList) {
        this.intermediateCertificateFilePathList = intermediateCertificateFilePathList;
    }
    
    public Boolean getConfirmedChangeInNatSettings() {
        return this.confirmedChangeInNatSettings;
    }
    
    public void setConfirmedChangeInNatSettings(final Boolean confirmedChangeInNatSettings) {
        this.confirmedChangeInNatSettings = confirmedChangeInNatSettings;
    }
    
    public Boolean getConfirmedSelfSignedCA() {
        return this.isSelfSignedCAConfirmed;
    }
    
    public void setConfirmedSelfSignedCA(final Boolean isSelfSignedCAConfirmed) {
        this.isSelfSignedCAConfirmed = isSelfSignedCAConfirmed;
    }
    
    public Boolean getPFXCertificateUploaded() {
        return this.isPFXCertificateUploaded;
    }
    
    public void setPFXCertificateUploaded(final Boolean pfxCertificateUploaded) {
        this.isPFXCertificateUploaded = pfxCertificateUploaded;
    }
    
    public String getServerCertificateFileName() {
        return this.serverCertificateFileName;
    }
    
    public void setServerCertificateFileName(final String serverCertificateFileName) {
        this.serverCertificateFileName = serverCertificateFileName;
    }
    
    @Override
    public String toString() {
        return "CertificateFormBean{automatic=" + this.automatic + ", intermediateGiven=" + this.intermediateGiven + ", rootGiven=" + this.rootGiven + ", intermediateCount=" + this.intermediateCount + ", serverCertificate=" + this.serverCertificateFilePath + ", intermediateCertificateList=" + this.intermediateCertificateFilePathList + ", confirmedChangeInNatSettings=" + this.confirmedChangeInNatSettings + ", serverCertificateFileSize=" + this.serverCertificateFilePath.toFile().length() + ", serverKey=" + this.serverCertificateFilePath.toFile().length() + " , isPFXCertificateUploaded=" + this.isPFXCertificateUploaded + '}';
    }
}
