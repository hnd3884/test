package com.me.devicemanagement.onpremise.server.certificate;

public class CertificatePropertiesDAO
{
    private static CertificatePropertiesDAO certificatePropertiesDAO;
    private String serverCertificatePath;
    private String serverKeyPath;
    private String rootKeyPath;
    private String rootCertificatePath;
    
    public static CertificatePropertiesDAO getInstance() throws Exception {
        if (CertificatePropertiesDAO.certificatePropertiesDAO == null) {
            CertificatePropertiesDAO.certificatePropertiesDAO = new CertificatePropertiesDAO();
        }
        return CertificatePropertiesDAO.certificatePropertiesDAO;
    }
    
    private CertificatePropertiesDAO() throws Exception {
        this.rootCertificatePath = SSLCertificateUtil.getInstance().getSelfSignedRootCertificatePath();
        this.rootKeyPath = SSLCertificateUtil.getInstance().getSelfSignedRootKeyPath();
        this.serverCertificatePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
        this.serverKeyPath = SSLCertificateUtil.getInstance().getServerPrivateKeyFilePath();
    }
    
    public String getServerCertificatePath() {
        return this.serverCertificatePath;
    }
    
    public String getServerKeyPath() {
        return this.serverKeyPath;
    }
    
    public String getRootKeyPath() {
        return this.rootKeyPath;
    }
    
    public String getRootCertificatePath() {
        return this.rootCertificatePath;
    }
    
    static {
        CertificatePropertiesDAO.certificatePropertiesDAO = null;
    }
}
