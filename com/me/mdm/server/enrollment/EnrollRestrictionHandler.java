package com.me.mdm.server.enrollment;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.certificate.CertificateCacheHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.json.JSONObject;

public class EnrollRestrictionHandler
{
    public void allowEnrollment(Long customerId, final boolean isSelfEnrollReq, final JSONObject paramsJSON) throws SyMException {
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", CustomerInfoUtil.getInstance().getCustomerId());
        if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
            throw new SyMException(12012, "License count exceeded. Contact your system administrator to continue Enrollment.", "dc.mdm.enroll.remarks.license_reached", (Throwable)null);
        }
        final String authMode = (paramsJSON == null) ? "" : paramsJSON.optString("AuthMode", "");
        if (isSelfEnrollReq && !authMode.equalsIgnoreCase("AzureADToken")) {
            if (customerId == null) {
                customerId = CustomerInfoUtil.getInstance().getCustomerId();
            }
            final boolean isSelfEnrollEnabled = EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerId);
            if (!isSelfEnrollEnabled) {
                throw new SyMException(51011, "Self Enrollment is Disabled! Contact your system administrator to enroll your device.", "dc.mdm.enroll.self_enrollment_disable", (Throwable)null);
            }
        }
        HashMap certCache = CertificateCacheHandler.getInstance().getAll();
        if (certCache == null) {
            try {
                final String NATAddress = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties().getProperty("NAT_ADDRESS", "");
                SSLCertificateUtil.getInstance().verifyCertificate(NATAddress);
                certCache = CertificateCacheHandler.getInstance().getAll();
            }
            catch (final Exception ex) {
                Logger.getLogger(EnrollRestrictionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (certCache != null) {
            if (certCache.containsKey("SSL_HOST_NAME_MISMATCH") && certCache.get("SSL_HOST_NAME_MISMATCH")) {
                throw new SyMException(52004, "SSL certificate issue: Host mismatch. Contact your system administrator to enroll your device.", "dc.common.msg.certificate_name_mismatch.remark", (Throwable)null);
            }
            if (certCache.containsKey("CERT_CHAIN_NOT_VERIFIED") && certCache.get("CERT_CHAIN_NOT_VERIFIED")) {
                throw new SyMException(52005, "SSL certificate issue: Certificate chain not verified. Contact your system administrator to enroll your device.", "dc.common.msg.cert_chain_not_verified.remark", (Throwable)null);
            }
            if (certCache.containsKey("SSL_CERTIFICATE_EXPIRED") && certCache.get("SSL_CERTIFICATE_EXPIRED")) {
                throw new SyMException(52006, "SSL certificate issue: Certificate has expired. Contact your system administrator to enroll your device.", "dc.common.msg.cert_expired.remark", (Throwable)null);
            }
        }
    }
}
