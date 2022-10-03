package com.me.mdm.api.technician;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;

public class ActionsForTechnicianFacade
{
    public JSONObject getAllowedActionsForTechnician(final JSONObject requestJSON) throws APIHTTPException {
        try {
            final JSONObject jsonObject = new JSONObject();
            final String emailID = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("email_address", "--");
            final String domain = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("domain", "-");
            if (!MDMUtil.getInstance().isValidEmail(emailID)) {
                throw new APIHTTPException("COM0005", new Object[] { "EMAIL_ADDRESS" });
            }
            final Long loginID = this.getTechnicianLoginIDFromEmail(emailID, domain);
            if (loginID == -1L) {
                throw new APIHTTPException("COM0005", new Object[] { "EMAIL_ADDRESS" });
            }
            final List<String> roleList = DMUserHandler.getRoleNameListForLoginUser(loginID);
            if (roleList.contains("MDM_Enrollment_Write") || roleList.contains("ModernMgmt_Enrollment_Write")) {
                jsonObject.put("deprovision_permission", true);
            }
            else {
                jsonObject.put("deprovision_permission", false);
            }
            if (roleList.contains("MDM_Inventory_Write") || roleList.contains("ModernMgmt_Inventory_Write")) {
                jsonObject.put("locate_device_permission", true);
            }
            else {
                jsonObject.put("locate_device_permission", false);
            }
            if (roleList.contains("MDM_AppMgmt_Write") || roleList.contains("ModernMgmt_AppMgmt_Write")) {
                jsonObject.put("app_distribution_permission", true);
            }
            else {
                jsonObject.put("app_distribution_permission", false);
            }
            if (roleList.contains("MDM_Configurations_Write") || roleList.contains("ModernMgmt_Configurations_Write")) {
                jsonObject.put("profile_association_permission", true);
            }
            else {
                jsonObject.put("profile_association_permission", false);
            }
            return jsonObject;
        }
        catch (final APIHTTPException exp) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while get access for technician: ", exp);
            throw exp;
        }
        catch (final Exception exp2) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while get access for technician: ", exp2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Long getTechnicianLoginIDFromEmail(final String emailID, final String domain) throws Exception {
        Long technicianLoginId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID", "AAALOGIN.USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "USER_ID", "AAAUSERCONTACTINFO.USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "CONTACTINFO_ID", "AAAUSERCONTACTINFO.CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
        final Criteria emailCri = new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)emailID, 0, false);
        Criteria domainCri = null;
        if (!domain.equalsIgnoreCase("-")) {
            domainCri = new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domain, 0, false);
        }
        else {
            domainCri = new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domain, 0, false).or(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)null, 0));
        }
        selectQuery.setCriteria(emailCri.and(domainCri));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        if (ds.next()) {
            technicianLoginId = (Long)ds.getValue("LOGIN_ID");
        }
        return technicianLoginId;
    }
}
