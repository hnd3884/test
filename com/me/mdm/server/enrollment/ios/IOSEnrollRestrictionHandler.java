package com.me.mdm.server.enrollment.ios;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollRestrictionHandler;

public class IOSEnrollRestrictionHandler extends EnrollRestrictionHandler
{
    private Logger logger;
    
    public IOSEnrollRestrictionHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void allowEnrollment(final Long customerId, final boolean isSelfEnrollReq, final JSONObject paramsJSON) throws SyMException {
        super.allowEnrollment(customerId, isSelfEnrollReq, paramsJSON);
        final HashMap apnsCertificateDetails = (HashMap)APNsCertificateHandler.getAPNSCertificateDetails();
        if (apnsCertificateDetails.isEmpty()) {
            throw new SyMException(51012, "APNs  is not configured in the Desktop Central server. Contact your system administrator to continue Enrollment.", "dc.mdm.enroll.self_anps_not_uploaded", (Throwable)null);
        }
        Hashtable ht = null;
        try {
            ht = DateTimeUtil.determine_From_To_Times("today");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting date: {0}", e);
        }
        if (ht != null && ht.containsKey("date2")) {
            if (ht.get("date2") > apnsCertificateDetails.get("EXPIRY_DATE")) {
                try {
                    APNsCertificateHandler.getInstance().validateAPNSCertificateExpiry();
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception in IOSEnrollRestrictionHandler: Wheile checking APNSExpiry", ex);
                }
                throw new SyMException(51016, "certificate_expired :APNs  Certificate has expired. Contact your system administrator to continue Enrollment.", "dc.mdm.enroll.self_anps_expired", (Throwable)null);
            }
            this.logger.log(Level.SEVERE, "Date is not present!! {0}", ht);
        }
    }
}
