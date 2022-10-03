package com.adventnet.sym.server.mdm.message;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.logging.Logger;

public class SSLCertificateMessageHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        try {
            X509Certificate x509Certificate = null;
            x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
            Date expiryDate = x509Certificate.getNotAfter();
            Date todaysDate = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            expiryDate = sdf.parse(sdf.format(expiryDate));
            todaysDate = sdf.parse(sdf.format(todaysDate));
            final Long expiryAlertTime = todaysDate.getTime() + 2592000000L;
            if (expiryDate.getTime() <= expiryAlertTime) {
                return false;
            }
        }
        catch (final CertificateExpiredException ex) {
            SSLCertificateMessageHandler.logger.log(Level.SEVERE, "Exception while Getting Expired date of Certificate , Expired message will be opened...", ex);
            return false;
        }
        catch (final Exception ex2) {
            SSLCertificateMessageHandler.logger.log(Level.SEVERE, "Exception while checking certificate expiry in Message Handler..", ex2);
        }
        return true;
    }
    
    static {
        SSLCertificateMessageHandler.logger = Logger.getLogger("MDMLogger");
    }
}
