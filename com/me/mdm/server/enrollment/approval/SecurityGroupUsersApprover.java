package com.me.mdm.server.enrollment.approval;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.List;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.ArrayList;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.logging.Logger;

public class SecurityGroupUsersApprover implements EnrollmentApprover
{
    public Logger logger;
    
    public SecurityGroupUsersApprover() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void allowEnrollment(final EnrollmentRequest enrollmentRequest) throws SyMException {
        try {
            if (DMDomainDataHandler.getInstance().isADManagedDomain(enrollmentRequest.user.domainName, enrollmentRequest.customerID)) {
                final JSONObject json = EnrollmentApprovalHandler.getInstance().getCriteria(1);
                if (json != null) {
                    final JSONArray groups = json.getJSONArray("RESOURCE_LIST");
                    final List<String> DNs = new ArrayList<String>();
                    final List<String> GUIDs = new ArrayList<String>();
                    for (int groupIndex = 0; groupIndex < groups.length(); ++groupIndex) {
                        final JSONObject group = groups.getJSONObject(groupIndex);
                        if (String.valueOf(group.get("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase(enrollmentRequest.user.domainName)) {
                            if (group.has("DN") && !group.isNull("DN")) {
                                DNs.add(String.valueOf(group.get("DN")));
                            }
                            GUIDs.add(String.valueOf(group.get("DIRECTORY_IDENTIFIER")));
                        }
                    }
                    final Boolean validationCriteria = !GUIDs.isEmpty() && IdpsFactoryProvider.getIdpsAccessAPI(enrollmentRequest.user.domainName, enrollmentRequest.customerID).isUserMemberOfAnyGroup(enrollmentRequest.user.domainName, enrollmentRequest.user.userName, enrollmentRequest.user.emailAddress, enrollmentRequest.user.password, (List)DNs, (List)GUIDs, enrollmentRequest.customerID);
                    final Boolean validationResponse = json.optBoolean("INCLUDE_RESOURCE", true) ? validationCriteria : (!validationCriteria);
                    this.logger.info("User " + enrollmentRequest.user.userName + "is " + (validationResponse ? "" : "not ") + "allowed to enroll their device via self enrollment");
                    if (!validationResponse) {
                        final String sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_approval_failed";
                        MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, enrollmentRequest.user.userName, enrollmentRequest.customerID);
                        throw new SyMException(12014, "You cannot enroll devices, as you are not a member of the allowed AD Group(s). Contact Admin", "dc.mdm.enroll.ad_authorization_failed_group", (Throwable)null);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while approving user : {0}", ex);
            throw new SyMException(12014, "You cannot enroll devices, as you are not a member of the allowed AD Group(s). Contact Admin", "dc.mdm.enroll.ad_authorization_failed_group", (Throwable)null);
        }
    }
}
