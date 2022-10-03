package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Collection;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQuery;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.server.adep.mac.AccountConfiguration;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppleDEPRequestProcessor
{
    public static Logger logger;
    
    public static JSONObject deleteDEPToken(final JSONObject deleteRequestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            final Long tokenID = (Long)deleteRequestJSON.get("DEP_TOKEN_ID");
            final Long customerID = (Long)deleteRequestJSON.get("CUSTOMER_ID");
            final Long loginId = (Long)deleteRequestJSON.get("login_id");
            final Long depEnrollementTemplateID = AccountConfiguration.getInstance().getEnrollmentTemplateIDFromDEPTokenID(tokenID);
            final JSONObject serverJson = DEPEnrollmentUtil.getDEPServerDetails(tokenID);
            final String serverName = (serverJson == null) ? "--" : serverJson.optString("SERVER_NAME".toLowerCase(), "--");
            final String serverUdid = (serverJson == null) ? "" : serverJson.optString("SERVER_UDID".toLowerCase(), "");
            responseJSON.put("SERVER_NAME", (Object)serverName);
            responseJSON.put("server_id", (Object)tokenID);
            if (new DEPAdminEnrollmentHandler().getDevicesEnrolledAndNotAssignedUserCount(customerID, tokenID) > 0) {
                responseJSON.put("success", false);
                responseJSON.put("error", (Object)new APIHTTPException("ABM020", new Object[] { serverName }).toJSONObject());
                return responseJSON;
            }
            if (!deleteRequestJSON.getBoolean("force_delete")) {
                removeDepProfileForDevices(tokenID, customerID);
            }
            if (depEnrollementTemplateID != null) {
                AppleDEPProfileHandler.getInstance(tokenID, customerID).disassociateAccountConfigFromEnrollmentTemplate(customerID, depEnrollementTemplateID);
            }
            if (new DEPAdminEnrollmentHandler().getAdminEnrolledDeviceCount(customerID, tokenID) == 0) {
                final DeleteQuery delQueryEnrollmentTemplate = (DeleteQuery)new DeleteQueryImpl("EnrollmentTemplate");
                delQueryEnrollmentTemplate.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                delQueryEnrollmentTemplate.addJoin(new Join("DEPEnrollmentTemplate", "EnrollmentTemplateToGroupRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                delQueryEnrollmentTemplate.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPTokenToGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
                delQueryEnrollmentTemplate.setCriteria(new Criteria(new Column("DEPTokenToGroup", "DEP_TOKEN_ID"), (Object)tokenID, 0));
                MDMUtil.getPersistence().delete(delQueryEnrollmentTemplate);
                AppleDEPRequestProcessor.logger.log(Level.INFO, "Deleted from ENROLLMENTTEMPLATE...");
            }
            else {
                AppleDEPRequestProcessor.logger.log(Level.INFO, "Devices are being managed with this token so ENROLLMENTTEMPLATE is not deleted...");
            }
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleConfigDeviceForEnrollment"));
            sq.addJoin(new Join("AppleConfigDeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            sq.addSelectColumn(new Column("AppleConfigDeviceForEnrollment", "*"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
            List<Long> appleConfigDfeIdsList = new ArrayList<Long>();
            appleConfigDfeIdsList = DBUtil.getColumnValuesAsList(DO.getRows("AppleConfigDeviceForEnrollment"), "ENROLLMENT_DEVICE_ID");
            final DeleteQuery delQuerydfe = (DeleteQuery)new DeleteQueryImpl("DeviceForEnrollment");
            delQuerydfe.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            final Criteria tokenCri = new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)tokenID, 0);
            final Criteria configuratorEnrolledCri = new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)appleConfigDfeIdsList.toArray(), 9);
            delQuerydfe.setCriteria(tokenCri.and(configuratorEnrolledCri));
            MDMUtil.getPersistence().delete(delQuerydfe);
            final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("DEPTokenDetails");
            delQuery.setCriteria(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            MDMUtil.getPersistence().delete(delQuery);
            AppleDEPRequestProcessor.logger.log(Level.INFO, "Deleted from DEPTOKENDETAILS...");
            DEPEnrollmentUtil.validateDEPTokenExpiry();
            AppleDEPRequestProcessor.logger.log(Level.INFO, "Closing all DEP expiry related messages...");
            DEPEnrollmentUtil.checkAndResetDEPStatus(customerID);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(AppleDEPCertificateHandler.getInstance().getDEPCertificateFolder(customerID, tokenID));
            if (!MDMStringUtils.isEmpty(serverUdid)) {
                String serverTypeStr = DEPConstants.apple_Business_Manager;
                if (serverJson != null && serverJson.getInt("ORG_TYPE".toLowerCase()) == AppleDEPServerConstants.DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION) {
                    serverTypeStr = DEPConstants.apple_School_Manager;
                }
                final String remarkArg = serverTypeStr + "@@@" + serverName;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2068, null, DMUserHandler.getUserName(loginId), "dc.mdm.dep.token_removed", remarkArg, customerID);
            }
            responseJSON.put("success", true);
            return responseJSON;
        }
        catch (final Exception ex) {
            AppleDEPRequestProcessor.logger.log(Level.SEVERE, "Exception while removing ABM/ASM server..", ex);
            throw ex;
        }
    }
    
    private static void removeDepProfileForDevices(final Long tokenID, final Long customerId) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            AppleDEPWebServicetHandler.getInstance(tokenID, customerId).updateCursor(null);
            final JSONObject dataJSON = AppleDEPProfileHandler.getInstance(tokenID, customerId).fetchOrSyncDEPDevices();
            final List synDevicesList = (List)dataJSON.get("addedDevicesList");
            if (synDevicesList != null && !synDevicesList.isEmpty()) {
                final JSONObject deviceJSON = new JSONObject();
                deviceJSON.put("devices", (Collection)synDevicesList);
                AppleDEPWebServicetHandler.getInstance(tokenID).removeDEPProfile(deviceJSON);
            }
        }
        catch (final SyMException ex) {
            AppleDEPRequestProcessor.logger.log(Level.INFO, "Exception handled while deleting token {0}", ex.getMessage());
        }
    }
    
    static {
        AppleDEPRequestProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
