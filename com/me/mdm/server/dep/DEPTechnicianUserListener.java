package com.me.mdm.server.dep;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.enrollment.AndroidZTEnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.me.mdm.core.enrollment.WindowsWICDEnrollmentHandler;
import com.me.mdm.core.enrollment.KNOXAdminEnrollmentHandler;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.enrollment.AndroidAdminEnrollmentHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONException;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import com.me.emsalerts.notifications.core.MediumDAOUtil;
import java.util.LinkedHashMap;
import com.me.emsalerts.notifications.core.TemplatesDAOUtil;
import java.util.HashMap;
import com.me.mdm.server.alerts.AlertConstants;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class DEPTechnicianUserListener extends AbstractUserListener
{
    private static final String MDM_ENROLLMENT_ROLE = "MDM_Enrollment_Write";
    private static final String MODERNMGMT_ENROLLMENT_ROLE = "ModernMgmt_Enrollment_Write";
    
    public void userAdded(final UserEvent userEvent) {
        final List roleNameList = DMUserHandler.getRoleNameListForLoginUser(userEvent.loginID);
        if (roleNameList.contains("MDM_Enrollment_Write") || roleNameList.contains("ModernMgmt_Enrollment_Write")) {
            this.addAdminEnrollmentTemplateForUser(userEvent);
            new AdminDeviceHandler().updateAdminDeviceLastSyncTime(userEvent.loginID, MDMUtil.getCurrentTimeInMillis());
        }
        RBDAUtil.deleteLoginAllDeviceGroupMappingCahae(userEvent.loginID);
        try {
            if (roleNameList.contains("MDM_Enrollment_Write") || roleNameList.contains("ModernMgmt_Enrollment_Write")) {
                Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.INFO, "Entered DEPTechnicianUserListener:userAdded to add default license percent medium data");
                final TemplatesUtil templatesUtil = new TemplatesUtil();
                final Long mediumId = templatesUtil.getMediumIdByName("EMAIL");
                Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
                Map templateMap = new HashMap();
                templateMap.put("templateName", "Device license exceeded alert Template " + System.currentTimeMillis());
                templateMap.put("userID", DMUserHandler.getUserIdForLoginId(userEvent.loginID));
                templateMap.put("description", "Template for sending alerts when Device license exceeds 100 percent " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
                templateMap.put("subCategoryID", subCategoryID);
                Long templateID = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap, (Long)null);
                LinkedHashMap mediumMap = new LinkedHashMap();
                mediumMap.put("mediumID", mediumId);
                mediumMap.put("mediumData", "{\"subject\": \"License Usage exceeded $mdm.specifiedpercent$% of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled ($mdm.enrolled_count$) devices into MDM. This exceeds $mdm.specifiedpercent$% of the licenses purchased for the month of $mdm.month$. To enroll and managed additional devices, purchase more licenses from MDM. Contact our support team at mdm-support@manageengine.com\"}");
                new MediumDAOUtil().populateMediumData(templateID, mediumMap);
                subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
                templateMap = new HashMap();
                templateMap.put("templateName", "Device license reached alert Template " + System.currentTimeMillis());
                templateMap.put("userID", DMUserHandler.getUserIdForLoginId(userEvent.loginID));
                templateMap.put("description", "Template for sending alerts when Device license drops below specified percent " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
                templateMap.put("subCategoryID", subCategoryID);
                templateID = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap, (Long)null);
                mediumMap = new LinkedHashMap();
                mediumMap.put("mediumID", mediumId);
                mediumMap.put("mediumData", "{\"subject\": \"License Usage reached $mdm.specifiedpercent$ of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled $mdm.enrolled_count$ devices, and have purchased $mdm.license_count$ licenses for the month of $mdm.month$. Kindly purchase only the required number of licenses for the devices enrolled.\"}");
                new MediumDAOUtil().populateMediumData(templateID, mediumMap);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, "Exception during adding medium data for newly added technician for license percent handling", e);
        }
    }
    
    public void userModified(final UserEvent userEvent) {
        try {
            final List roleNameList = DMUserHandler.getRoleNameListForLoginUser(userEvent.loginID);
            if (roleNameList.contains("MDM_Enrollment_Write") || roleNameList.contains("ModernMgmt_Enrollment_Write")) {
                this.addAdminEnrollmentTemplateForUser(userEvent);
            }
            else {
                final JSONObject json = new JSONObject();
                json.put("LOGIN_ID", (Object)userEvent.loginID);
                MDMUserAPIKeyGenerator.getInstance().revokeAPIKey(json);
                deleteEnrollmentTemplates(userEvent.loginID);
            }
            new AdminDeviceHandler().updateAdminDeviceLastSyncTime(userEvent.loginID, MDMUtil.getCurrentTimeInMillis());
            RBDAUtil.deleteLoginAllDeviceGroupMappingCahae(userEvent.loginID);
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void userDeleted(final UserEvent userEvent) {
        try {
            this.deleteUserPersonalData(userEvent);
            final JSONObject json = new JSONObject();
            json.put("LOGIN_ID", (Object)userEvent.loginID);
            MDMUserAPIKeyGenerator.getInstance().revokeAPIKey(json);
            new AdminDeviceHandler().updateAdminDeviceLastSyncTime(userEvent.loginID, MDMUtil.getCurrentTimeInMillis());
            RBDAUtil.deleteLoginAllDeviceGroupMappingCahae(userEvent.loginID);
        }
        catch (final JSONException ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public void adminUserDeleted(final UserEvent userEvent) {
        try {
            this.deleteUserPersonalData(userEvent);
            RBDAUtil.deleteLoginAllDeviceGroupMappingCahae(userEvent.loginID);
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addAdminEnrollmentTemplateForUser(final UserEvent userEvent) {
        try {
            final JSONObject enrollmentTemplateJSON = new JSONObject();
            final String domainName = (String)DBUtil.getValueFromDB("AaaLogin", "LOGIN_ID", (Object)userEvent.loginID, "DOMAINNAME");
            final Long userId = DMUserHandler.getDCUserID(userEvent.loginID);
            final Properties userInfo = DMUserHandler.getContactInfoProp(userId);
            final String email = userInfo.getProperty("EMAIL_ID");
            enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", (Object)((domainName != null) ? domainName : "MDM"));
            enrollmentTemplateJSON.put("ADDED_USER", (Object)userId);
            enrollmentTemplateJSON.put("LOGIN_ID", (Object)userEvent.loginID);
            enrollmentTemplateJSON.put("EMAIL_ADDRESS", (Object)email);
            final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
            final List<HashMap> customerList = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(userId);
            for (int i = 0; i < customerList.size(); ++i) {
                enrollmentTemplateJSON.put("CUSTOMER_ID", customerList.get(i).get("CUSTOMER_ID"));
                handler.addorUpdateAndroidAdminEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateAppleConfigEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateKNOXAdminEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateWindowsWICDEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateQREnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateZTEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateWindowsLaptopEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateGSChromeEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
                handler.addorUpdateIOSMigrationEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void deleteEnrollmentTemplates(final Long loginID) {
        try {
            new AndroidAdminEnrollmentHandler();
            AndroidAdminEnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
            new AppleConfiguratorEnrollmentHandler();
            AppleConfiguratorEnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
            new KNOXAdminEnrollmentHandler();
            KNOXAdminEnrollmentHandler.deleteKNOXEnrollmentTemplate(loginID);
            new WindowsWICDEnrollmentHandler();
            WindowsWICDEnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
            AndroidQREnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
            WindowsLaptopEnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
            AndroidZTEnrollmentHandler.deleteAdminEnrollmentTemplate(loginID);
        }
        catch (final Exception exp) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, exp);
        }
    }
    
    public static void addEnrollmentTemplates(final Long loginID) {
        try {
            final JSONObject enrollmentTemplateJSON = new JSONObject();
            final String domainName = (String)DBUtil.getValueFromDB("AaaLogin", "LOGIN_ID", (Object)loginID, "DOMAINNAME");
            final Long userId = DMUserHandler.getDCUserID(loginID);
            final Properties userInfo = DMUserHandler.getContactInfoProp(userId);
            final String email = userInfo.getProperty("EMAIL_ID");
            enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", (Object)((domainName != null) ? domainName : "MDM"));
            enrollmentTemplateJSON.put("ADDED_USER", (Object)userId);
            enrollmentTemplateJSON.put("LOGIN_ID", (Object)loginID);
            enrollmentTemplateJSON.put("EMAIL_ADDRESS", (Object)email);
            final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
            enrollmentTemplateJSON.put("CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId());
            handler.addorUpdateAndroidAdminEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateAppleConfigEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateKNOXAdminEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateWindowsWICDEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateQREnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateZTEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateGSChromeEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
            handler.addorUpdateWindowsLaptopEnrollmentTemplate(new JSONObject(enrollmentTemplateJSON.toString()));
        }
        catch (final Exception exp) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, null, exp);
        }
    }
    
    public void deleteUserPersonalDataForDefaultAdmin(final Long userID) {
        try {
            final UserEvent userEvent = new UserEvent();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
            selectQuery.addJoin(new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "CONTACTINFO_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userID, 0));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaContactInfo");
                final HashMap hmap = new HashMap();
                hmap.put("EMAILID", row.get("EMAILID"));
                hmap.put("additionalEmailIDsList", new ArrayList());
                userEvent.userContactInfo = hmap;
                this.deleteUserPersonalData(userEvent);
            }
        }
        catch (final Exception exp) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, "Exception while delete default admin user!!", exp);
        }
    }
    
    private void deleteUserPersonalData(final UserEvent userEvent) {
        try {
            final String emailID = userEvent.userContactInfo.get("EMAILID");
            List additionalEmailList = userEvent.userContactInfo.get("additionalEmailIDsList");
            String alternateEmail = userEvent.userContactInfo.get("alternateEmail");
            if (alternateEmail == null || alternateEmail == "") {
                alternateEmail = MDMUtil.getInstance().getCurrentlyLoggedInUserEmail();
            }
            if (additionalEmailList == null) {
                additionalEmailList = new ArrayList();
            }
            if (alternateEmail == null) {
                alternateEmail = "";
            }
            additionalEmailList.add(emailID);
            final DataObject DO = new MDMUserHandler().getNotifyConfiguredForEmailDO(additionalEmailList);
            final JSONObject json = new JSONObject();
            this.updateUserData(alternateEmail, DO, additionalEmailList);
            Criteria apnsemailIDcri = new Criteria(Column.getColumn("APNSCertificateDetails", "EMAIL_ADDRESS"), additionalEmailList.get(0), 12);
            Criteria reportsemailIDcri = new Criteria(Column.getColumn("ScheduleRepTask", "EMAIL_ADDRESS"), additionalEmailList.get(0), 12);
            for (int i = 1; i < additionalEmailList.size(); ++i) {
                final String emailIdFromList = additionalEmailList.get(i);
                apnsemailIDcri = apnsemailIDcri.or(new Criteria(Column.getColumn("APNSCertificateDetails", "EMAIL_ADDRESS"), (Object)emailIdFromList, 12));
                reportsemailIDcri = reportsemailIDcri.or(new Criteria(Column.getColumn("ScheduleRepTask", "EMAIL_ADDRESS"), additionalEmailList.get(0), 12));
            }
            final DataObject apnsobject = MDMUtil.getPersistenceLite().get("APNSCertificateDetails", apnsemailIDcri);
            json.put("alternateEmail", (Object)alternateEmail);
            json.put("tableName", (Object)"APNSCertificateDetails");
            json.put("colName", (Object)"EMAIL_ADDRESS");
            this.updateUserDataString(json, apnsobject, additionalEmailList);
            final DataObject reportsobjet = MDMUtil.getPersistenceLite().get("ScheduleRepTask", reportsemailIDcri);
            json.put("tableName", (Object)"ScheduleRepTask");
            json.put("colName", (Object)"EMAIL_ADDRESS");
            this.updateUserDataString(json, reportsobjet, additionalEmailList);
            ManagedUserHandler.getInstance().deleteEmailOnUserDelete(additionalEmailList);
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, "Exception while delete user!!", ex);
        }
    }
    
    public void updateUserDataString(final JSONObject json, final DataObject dataobject, final List<String> email) throws Exception {
        final String tablename = String.valueOf(json.get("tableName"));
        final String colname = String.valueOf(json.get("colName"));
        final String alternateEmail = String.valueOf(json.get("alternateEmail"));
        boolean isUpdated = false;
        if (dataobject != null && !dataobject.isEmpty()) {
            final Iterator mailAddrItr = dataobject.getRows(tablename);
            while (mailAddrItr.hasNext()) {
                final Row mailAddrRow = mailAddrItr.next();
                String mailAddr = (String)mailAddrRow.get(colname);
                final String[] split;
                final String[] maildAddrArray = split = mailAddr.split(",");
                for (final String s : split) {
                    if (!isUpdated && email.contains(s.trim()) && mailAddr.indexOf(alternateEmail) == -1) {
                        mailAddr = mailAddr.replace(s.trim(), alternateEmail);
                        isUpdated = true;
                    }
                    else if ((isUpdated && email.contains(s.trim())) || (!isUpdated && email.contains(s.trim()) && mailAddr.indexOf(alternateEmail) != -1)) {
                        mailAddr = mailAddr.replace(s.trim(), "");
                        isUpdated = true;
                    }
                }
                if (isUpdated) {
                    final StringBuilder emailString = new StringBuilder();
                    final String[] split2;
                    final String[] maildAddrarray = split2 = mailAddr.split(",");
                    for (final String s2 : split2) {
                        if (s2 != null && !s2.trim().equalsIgnoreCase("")) {
                            emailString.append(",");
                            emailString.append(s2);
                        }
                    }
                    mailAddrRow.set(colname, (Object)emailString.toString().substring(1));
                    dataobject.updateRow(mailAddrRow);
                }
            }
        }
        if (isUpdated) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.INFO, "Atleast One Email was deleted, going to Update DB");
            MDMUtil.getPersistenceLite().update(dataobject);
        }
    }
    
    public void updateUserData(final String alternateEmail, final DataObject dataobject, final List<String> email) throws Exception {
        String module = "";
        String prevmodule = "";
        boolean isUpdated = false;
        DataObject alternateDO = null;
        if (dataobject != null && !dataobject.isEmpty()) {
            final Iterator mailAddrItr = dataobject.getRows("EMailAddr");
            while (mailAddrItr.hasNext()) {
                final Row mailAddrRow = mailAddrItr.next();
                final String mailAddr = (String)mailAddrRow.get("EMAIL_ADDR");
                module = (String)mailAddrRow.get("MODULE");
                if (email.contains(mailAddr) && alternateEmail != null && (module.equalsIgnoreCase("") || (!module.equalsIgnoreCase("") && !prevmodule.equalsIgnoreCase(module)))) {
                    final Criteria cri = new Criteria(new Column("EMailAddr", "MODULE"), (Object)module, 0).and(new Criteria(new Column("EMailAddr", "EMAIL_ADDR"), (Object)alternateEmail, 0));
                    final int count = DBUtil.getRecordActualCount("EMailAddr", "EMAIL_ADDR_ID", cri);
                    if (count == 0) {
                        mailAddrRow.set("EMAIL_ADDR", (Object)alternateEmail);
                        dataobject.updateRow(mailAddrRow);
                    }
                    else {
                        alternateDO = MDMUtil.getPersistenceLite().get("EMailAddr", cri);
                        final Row alterRow = alternateDO.getFirstRow("EMailAddr");
                        alterRow.set("SEND_MAIL", (Object)true);
                        alternateDO.updateRow(alterRow);
                        mailAddrItr.remove();
                        dataobject.deleteRow(mailAddrRow);
                    }
                    isUpdated = true;
                    prevmodule = module;
                }
                else {
                    if (module.equalsIgnoreCase("") || !prevmodule.equalsIgnoreCase(module)) {
                        continue;
                    }
                    mailAddrItr.remove();
                    dataobject.deleteRow(mailAddrRow);
                    isUpdated = true;
                }
            }
        }
        if (isUpdated) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.INFO, "Atleast One Email was deleted, going to Update DB");
            MDMUtil.getPersistenceLite().update(dataobject);
            if (alternateDO != null) {
                MDMUtil.getPersistenceLite().update(alternateDO);
            }
        }
    }
    
    public void addAdminEnrollmentTemplateForDefaultAdminUser() {
        try {
            final UserEvent userEvent = new UserEvent();
            userEvent.loginID = DMUserHandler.getLoginIdForUser("admin");
            userEvent.userName = "admin";
            this.addAdminEnrollmentTemplateForUser(userEvent);
        }
        catch (final Exception ex) {
            Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.SEVERE, "Exception during adding enrollment template for default admin", ex);
        }
    }
}
