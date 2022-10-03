package com.me.mdm.server.enrollment.approval;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.logging.Logger;

public class SelfEnrollmentLimitApprover implements EnrollmentApprover
{
    public Logger logger;
    
    public SelfEnrollmentLimitApprover() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void allowEnrollment(final EnrollmentRequest enrollmentRequest) throws SyMException {
        try {
            final String allowedCount = CustomerParamsHandler.getInstance().getParameterValue("selfEnrollDeviceLimit", (long)CustomerInfoUtil.getInstance().getCustomerId());
            if (allowedCount != null) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("EMAIL_ADDRESS", (Object)enrollmentRequest.user.emailAddress);
                jsonObject.put("DOMAIN_NETBIOS_NAME", (Object)enrollmentRequest.user.domainName);
                jsonObject.put("COUNT", Integer.parseInt(allowedCount));
                ManagedDeviceHandler.getInstance().getSelfEnrolledDeviceCountForUser(jsonObject);
            }
        }
        catch (final SyMException exp) {
            this.logger.log(Level.SEVERE, "self enrollment count exceeded", (Throwable)exp);
            throw new SyMException(51020, "Enrollment failed as no of devices allowed per user via self enrollment limit is exceeded. Contact Admin!!", "mdm.enroll.self_enroll_limit", (Throwable)null);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while self enrollment count exceeded check", e);
        }
    }
}
