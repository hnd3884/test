package com.me.mdm.core.enrollment;

import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.dep.DEPTechnicianUserListener;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import java.math.BigInteger;
import java.util.Iterator;
import com.me.mdm.core.auth.MDMAPIKeyGeneratorAPI;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.me.mdm.server.adep.DEPConstants;
import com.me.mdm.server.adep.AppleDEPWebServicetHandler;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.auth.APIKey;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import java.security.SecureRandom;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class EnrollmentTemplateHandler
{
    public static Logger logger;
    private DataObject existingDO;
    public static final int DEP_ENROLLMENT_TYPE = 10;
    public static final int APPLE_CONFIGURATOR_TYPE = 11;
    public static final int MODERN_MGMT_MAC_ENROLLMENT_TYPE = 12;
    public static final int ANDROID_ADMIN_ENROLLMENT_TYPE = 20;
    public static final int KNOX_ENROLLMENT_TYPE = 21;
    public static final int ANDROID_QR_ENROLLMENT_TYPE = 22;
    public static final int ANDROID_ZT_ENROLLMENT_TYPE = 23;
    public static final int WINDOWS_WICD_ENROLLMENT_TYPE = 30;
    public static final int WINDOWS_LAPTOP_ENROLLMENT_TYPE = 31;
    public static final int WINDOWS_AZURE_AD_ENROLLMENT_TYPE = 32;
    public static final int WINDOWS_MODERN_MGMT_ENROLLMENT_TYPE = 33;
    public static final int GSUITE_CHROME_DEVICE_ENROLLMENT_TYPE = 40;
    public static final int MIGRATION_ENROLLMENT_TYPE = 50;
    public static final String TEMPLATE_TOKEN = "templateToken";
    public static final int MULTIPLE_TEMPLATE_TYPE = -1;
    
    public EnrollmentTemplateHandler() {
        this.existingDO = null;
    }
    
    public void addorUpdateDEPEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Apple DEP");
        templateJSON.put("TEMPLATE_TYPE", 10);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
        MDMApiFactoryProvider.getMDMUtilAPI().addAutoUserAssignRule(templateJSON);
    }
    
    public void addorUpdateIOSMigrationEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Migration Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 50);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateModernMgmtMacEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Apple Mac Modern Management");
        templateJSON.put("TEMPLATE_TYPE", 12);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateKNOXAdminEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"KNOX Mobile Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 21);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateAndroidAdminEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Android Admin NFC");
        templateJSON.put("TEMPLATE_TYPE", 20);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateAppleConfigEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Apple Configurator");
        templateJSON.put("TEMPLATE_TYPE", 11);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateWindowsWICDEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Windows WICD Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 30);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateWindowsLaptopEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Windows Laptop Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 31);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateWindowsModernMgmtTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Windows Modern Mgmt Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 33);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateQREnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Android QR Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 22);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
        final Long customerId = templateJSON.getLong("CUSTOMER_ID");
        final Long userId = templateJSON.getLong("ADDED_USER");
        if (MDMEnrollmentUtil.getInstance().isNATConfigured() && !MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("technician.qrgeneration.disable")) {
            new AndroidQREnrollmentHandler().generateAndSaveQRCode(userId, customerId, true);
            new AndroidQREnrollmentHandler().generateAndSaveAdvQRCode(userId, customerId, true);
        }
    }
    
    public void addorUpdateZTEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Zero Touch Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 23);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateGSChromeEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"GSuite - Chrome Device Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 40);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateWindowsAzureADEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Windows AzureAD Enrollment");
        templateJSON.put("TEMPLATE_TYPE", 32);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    public void addorUpdateMultipleMgmtEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        templateJSON.put("TEMPLATE_NAME", (Object)"Multiple Enrollment");
        templateJSON.put("TEMPLATE_TYPE", -1);
        this.addOrUpdateEnrollmentTemplate(templateJSON);
    }
    
    private void addOrUpdateEnrollmentTemplate(final JSONObject templateJSON) throws Exception {
        this.getExisitingDO(templateJSON);
        final Row enrollmentTemplateRow = this.addOrUpdateTemplateRow(templateJSON);
        final int enrollmentType = templateJSON.optInt("TEMPLATE_TYPE");
        if (enrollmentType == 10) {
            this.addOrUpdateDEPEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 20) {
            this.addOrUpdateAndroidAdminEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 11) {
            this.addOrUpdateAppleConfigEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 21) {
            this.addOrUpdateKNOXMobileEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 30) {
            this.addOrUpdateWindowsWICDEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 22) {
            this.addOrUpdateQREnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 31) {
            this.addOrUpdateWindowsLaptopEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 23) {
            this.addOrUpdateZTEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 40) {
            this.addOrUpdateGSChromeEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 32) {
            this.addOrUpdateWindowsAzureADEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 12) {
            this.addModernMgmtMacEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == -1) {
            this.addOrUpdateDEPEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateAndroidAdminEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateAppleConfigEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateKNOXMobileEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateWindowsWICDEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateQREnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateWindowsLaptopEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateZTEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateGSChromeEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addOrUpdateWindowsAzureADEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
            this.addModernMgmtMacEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 33) {
            this.addOrUpdateWindowsMordernMgmtEnrollmentTemplateTable(templateJSON, enrollmentTemplateRow);
        }
        else if (enrollmentType == 50) {
            this.addOrUpdateIOSMigrationEnrollmentTemplate(templateJSON, enrollmentTemplateRow);
        }
        final DataObject DO = MDMUtil.getPersistence().update(this.existingDO);
        templateJSON.put("TEMPLATE_ID", DO.getFirstValue("EnrollmentTemplate", "TEMPLATE_ID"));
        if (enrollmentType != -1 && !UserAssignmentRuleHandler.serverAsTemplateList.contains(enrollmentType) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReplicateUserAssignSettings")) {
            new UserAssignmentRuleHandler().replicateRulesFromOtherTemplates(templateJSON.getLong("TEMPLATE_ID"), enrollmentType, templateJSON.getLong("CUSTOMER_ID"));
        }
    }
    
    private void addOrUpdateWindowsMordernMgmtEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws DataAccessException {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("WindowsModernMgmtEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("WindowsModernMgmtEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("WindowsModernMgmtEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void getExisitingDO(final JSONObject templateJSON) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Join depTemplate = new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join enrollmentTemplateToGroup = new Join("DEPEnrollmentTemplate", "EnrollmentTemplateToGroupRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join enrollmentGroupToToken = new Join("EnrollmentTemplateToGroupRel", "DEPTokenToGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        final Join androidTemplate = new Join("EnrollmentTemplate", "AndroidAdminEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join appleConfigTemplate = new Join("EnrollmentTemplate", "AppleConfigEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join knoxMobileTemplate = new Join("EnrollmentTemplate", "KNOXMobileEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join windowsICDTemplate = new Join("EnrollmentTemplate", "WindowsICDEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join androidQRTemplate = new Join("EnrollmentTemplate", "AndroidQREnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join androidZTTemplate = new Join("EnrollmentTemplate", "AndroidZTEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join androidGSChromeTemplate = new Join("EnrollmentTemplate", "GSChromeEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join windowsLaptopTemplate = new Join("EnrollmentTemplate", "WindowsLaptopEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join windowsAzureADTemplate = new Join("EnrollmentTemplate", "WindowsAzureADEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join windowsmodernMgmtTemplate = new Join("EnrollmentTemplate", "WindowsModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join modernMacMgmtTemplate = new Join("EnrollmentTemplate", "MacModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        final Join enrollmentTemplateAuthRow = new Join("EnrollmentTemplate", "EnrollmentTemplateAccessCredentials", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1);
        selectQuery.addJoin(depTemplate);
        selectQuery.addJoin(enrollmentTemplateToGroup);
        selectQuery.addJoin(enrollmentGroupToToken);
        selectQuery.addJoin(androidTemplate);
        selectQuery.addJoin(appleConfigTemplate);
        selectQuery.addJoin(knoxMobileTemplate);
        selectQuery.addJoin(windowsICDTemplate);
        selectQuery.addJoin(androidQRTemplate);
        selectQuery.addJoin(androidZTTemplate);
        selectQuery.addJoin(androidGSChromeTemplate);
        selectQuery.addJoin(windowsLaptopTemplate);
        selectQuery.addJoin(windowsAzureADTemplate);
        selectQuery.addJoin(modernMacMgmtTemplate);
        selectQuery.addJoin(enrollmentTemplateAuthRow);
        selectQuery.addJoin(windowsmodernMgmtTemplate);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.addSortColumn(new SortColumn(new Column("EnrollmentTemplate", "ADDED_TIME"), false));
        selectQuery.setCriteria(this.getCriteriaForEnrollmentTemplateType(templateJSON));
        this.existingDO = MDMUtil.getPersistence().get(selectQuery);
    }
    
    private Criteria getCriteriaForEnrollmentTemplateType(final JSONObject templateJSON) {
        final Long templateId = templateJSON.optLong("TEMPLATE_ID", -1L);
        final Integer templateType = templateJSON.optInt("TEMPLATE_TYPE", -1);
        final Long userID = templateJSON.optLong("ADDED_USER", -1L);
        final Long customerId = templateJSON.optLong("CUSTOMER_ID", 1L);
        final Long depTokenId = templateJSON.optLong("DEP_TOKEN_ID", -1L);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
        final Criteria typeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        final Criteria userCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "ADDED_USER"), (Object)userID, 0);
        Criteria criteria;
        final Criteria customerCriteria = criteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0);
        if (templateType == 10 || templateType == 32 || templateType == 12 || templateType == 33) {
            criteria = criteria.and(typeCriteria);
            if (templateType == 10 && depTokenId != -1L) {
                final Criteria depTokenCriteria = new Criteria(Column.getColumn("DEPTokenToGroup", "DEP_TOKEN_ID"), (Object)templateJSON.optLong("DEP_TOKEN_ID"), 0);
                criteria = criteria.and(depTokenCriteria);
            }
        }
        else {
            criteria = criteria.and(typeCriteria).and(userCriteria);
        }
        if (templateId != -1L) {
            criteria = criteria.and(templateCriteria);
        }
        return criteria;
    }
    
    private Row addOrUpdateTemplateRow(final JSONObject templateJSON) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("EnrollmentTemplate");
            templateRow.set("TEMPLATE_NAME", (Object)templateJSON.optString("TEMPLATE_NAME"));
            templateRow.set("TEMPLATE_TYPE", (Object)templateJSON.optInt("TEMPLATE_TYPE"));
            templateRow.set("CUSTOMER_ID", (Object)templateJSON.optLong("CUSTOMER_ID"));
            templateRow.set("ADDED_TIME", (Object)SyMUtil.getCurrentTime());
            templateRow.set("ADDED_USER", (Object)templateJSON.optLong("ADDED_USER"));
            final String templateToken = this.getTemplateToken();
            templateRow.set("TEMPLATE_TOKEN", (Object)templateToken);
            templateJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("EnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("EnrollmentTemplate");
                templateRow.set("TEMPLATE_NAME", (Object)templateJSON.optString("TEMPLATE_NAME"));
                templateRow.set("TEMPLATE_TYPE", (Object)templateJSON.optInt("TEMPLATE_TYPE"));
                templateRow.set("CUSTOMER_ID", (Object)templateJSON.optLong("CUSTOMER_ID"));
                templateRow.set("ADDED_TIME", (Object)SyMUtil.getCurrentTime());
                templateRow.set("ADDED_USER", (Object)templateJSON.optLong("ADDED_USER"));
                final String templateToken = this.getTemplateToken();
                templateRow.set("TEMPLATE_TOKEN", (Object)templateToken);
                templateJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                this.existingDO.addRow(templateRow);
            }
            else {
                final int templateType = (int)templateRow.get("TEMPLATE_TYPE");
                if (templateType == 10) {
                    templateJSON.put("TEMPLATE_TOKEN", (Object)templateRow.get("TEMPLATE_TOKEN"));
                    templateRow.set("ADDED_TIME", (Object)SyMUtil.getCurrentTime());
                    templateRow.set("ADDED_USER", (Object)templateJSON.optLong("ADDED_USER"));
                    this.existingDO.updateRow(templateRow);
                }
            }
        }
        return templateRow;
    }
    
    private void addOrUpdateAndroidAdminEnrollmentTemplate(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("AndroidAdminEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("AndroidAdminEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("AndroidAdminEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateAppleConfigEnrollmentTemplate(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("AppleConfigEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("AppleConfigEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("AppleConfigEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
        AppleConfiguratorEnrollmentHandler.closeUrlChangeMsg(templateJSON.getLong("ADDED_USER"), templateJSON.getLong("CUSTOMER_ID"));
    }
    
    private void addOrUpdateIOSMigrationEnrollmentTemplate(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        final String templateToken = this.getTemplateToken();
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("IOSMigrationEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("IOSMigrationEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("IOSMigrationEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateQREnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("AndroidQREnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("AndroidQREnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("AndroidQREnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateZTEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("AndroidZTEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("AndroidZTEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("AndroidZTEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateGSChromeEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("GSChromeEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("GSChromeEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("GSChromeEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateKNOXMobileEnrollmentTemplate(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("KNOXMobileEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("KNOXMobileEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("KNOXMobileEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addModernMgmtMacEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        if (!templateJSON.has("TEMPLATE_TOKEN")) {
            Logger.getLogger("MDMModernMgmtLogger").log(Level.SEVERE, "This should not be called, template token should not be null , so ignoring it");
            return;
        }
        Row templateRow = null;
        String mdmUrl = "/mdm/client/v1/modern/mac/" + templateJSON.opt("TEMPLATE_TOKEN");
        final JSONObject keyParams = new JSONObject();
        keyParams.put("PURPOSE_KEY", 102);
        if (templateJSON.has("CUSTOMER_ID")) {
            keyParams.put("CUSTOMER_ID", templateJSON.get("CUSTOMER_ID"));
        }
        final APIKey key = MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().generateAPIKey(keyParams);
        final String authTokenStr = key.getAsURLParams();
        mdmUrl = mdmUrl + "?" + authTokenStr;
        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "Inside addModernMgmtMacEnrollmentTemplateTable()");
        if (this.existingDO != null && !this.existingDO.containsTable("MacModernMgmtEnrollmentTemplate")) {
            templateRow = new Row("MacModernMgmtEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("MDM_URL", (Object)mdmUrl);
            this.existingDO.addRow(templateRow);
            Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "Added Modern Mac Mgmt Enrollment template");
            final String userName = "MDMMacEnrollment";
            final String password = RandomStringUtils.random(20, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
            this.addOrUpdateEnrollmentTemplateBasicAuthentication(enrollmentTemplateRow, userName, password);
        }
        else {
            Logger.getLogger("MDMModernMgmtLogger").log(Level.SEVERE, "This should not be called,addModernMgmtMacEnrollmentTemplateTable  method called for already existing template token. Simply ignoring ");
        }
    }
    
    private void addOrUpdateEnrollmentTemplateBasicAuthentication(final Row enrollmentTemplateRow, final String username, final String password) throws Exception {
        Row templateAuthRow = null;
        boolean isUpdate = false;
        if (!this.existingDO.containsTable("EnrollmentTemplateAccessCredentials")) {
            templateAuthRow = new Row("EnrollmentTemplateAccessCredentials");
            templateAuthRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateAuthRow.set("AUTH_MODE", (Object)0);
        }
        else {
            isUpdate = true;
            templateAuthRow = this.existingDO.getFirstRow("EnrollmentTemplateAccessCredentials");
        }
        templateAuthRow.set("USERNAME", (Object)username);
        templateAuthRow.set("PASSWORD", (Object)password);
        if (isUpdate) {
            EnrollmentTemplateHandler.logger.log(Level.INFO, "Updated addOrUpdateEnrollmentTemplateBasicAuthentication for template");
            this.existingDO.updateRow(templateAuthRow);
        }
        else {
            this.existingDO.addRow(templateAuthRow);
            EnrollmentTemplateHandler.logger.log(Level.INFO, "Added addOrUpdateEnrollmentTemplateBasicAuthentication for template");
        }
    }
    
    private void addOrUpdateDEPEnrollmentTemplate(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        final JSONObject json = new JSONObject();
        boolean isUpdate = false;
        final long loginId = DMUserHandler.getLoginIdForUserId(Long.valueOf(templateJSON.getLong("ADDED_USER")));
        json.put("LOGIN_ID", loginId);
        final MDMAPIKeyGeneratorAPI generator = MDMUserAPIKeyGenerator.getInstance();
        String mdmUrl = "/mdm/client/v1/ios/dep/" + templateJSON.opt("TEMPLATE_TOKEN");
        if (generator != null) {
            json.put("TEMPLATE_TYPE", 10);
            final APIKey key = generator.generateAPIKey(json);
            final String authTokenStr = key.getAsURLParams();
            mdmUrl = mdmUrl + "?" + authTokenStr;
        }
        templateJSON.put("MDM_URL", (Object)mdmUrl);
        final Long customerId = templateJSON.optLong("CUSTOMER_ID", -1L);
        final Long depTokenID = templateJSON.optLong("DEP_TOKEN_ID", -1L);
        final JSONObject serverJson = DEPEnrollmentUtil.getDEPServerDetails(depTokenID);
        final String serverName = (serverJson == null) ? "" : serverJson.optString("SERVER_NAME".toLowerCase(), "");
        final String profileUUID = AppleDEPWebServicetHandler.getInstance(depTokenID, customerId).defineDEPProfile(templateJSON);
        templateJSON.accumulate(mdmUrl, (Object)profileUUID);
        templateJSON.put("PROFILE_UUID", (Object)profileUUID);
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("DEPEnrollmentTemplate");
        }
        else {
            templateRow = this.existingDO.getRow("DEPEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("DEPEnrollmentTemplate");
            }
            else {
                isUpdate = true;
            }
        }
        if (!isUpdate) {
            templateRow.set("MDM_URL", (Object)templateJSON.optString("MDM_URL", mdmUrl));
        }
        templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
        templateRow.set("ENABLE_SELF_ENROLL", (Object)templateJSON.optBoolean("ENABLE_SELF_ENROLL", (boolean)Boolean.FALSE));
        templateRow.set("PROFILE_UUID", (Object)templateJSON.optString("PROFILE_UUID"));
        templateRow.set("AUTO_ADVANCE_TV", (Object)templateJSON.optBoolean("AUTO_ADVANCE_TV", (boolean)Boolean.FALSE));
        templateRow.set("IS_MULTIUSER", (Object)templateJSON.optBoolean("IS_MULTIUSER", (boolean)Boolean.FALSE));
        templateRow.set("ENABLE_AWAIT_CONFIG", (Object)templateJSON.optBoolean("ENABLE_AWAIT_CONFIG", (boolean)Boolean.FALSE));
        templateRow.set("SUPPORT_PHONE_NO", (Object)templateJSON.optBoolean("SUPPORT_PHONE_NO", (boolean)Boolean.FALSE));
        templateRow.set("SUPPORT_EMAIL", (Object)templateJSON.optBoolean("SUPPORT_EMAIL", (boolean)Boolean.FALSE));
        templateRow.set("ACTIVATION_BY", (Object)templateJSON.optInt("ACTIVATION_BY", 1));
        final Iterator keysIt = templateRow.getColumns().iterator();
        while (keysIt.hasNext()) {
            final String eachKey = keysIt.next().toString();
            if (eachKey.startsWith("SKIP_")) {
                templateRow.set(eachKey, (Object)templateJSON.has(eachKey));
            }
        }
        String serverType = DEPConstants.apple_Business_Manager;
        if (serverJson != null && serverJson.getInt("ORG_TYPE".toLowerCase()) == AppleDEPServerConstants.DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION) {
            serverType = DEPConstants.apple_School_Manager;
        }
        final String remarkArg = serverType + "@@@" + serverName;
        if (isUpdate) {
            this.existingDO.updateRow(templateRow);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2068, null, DMUserHandler.getUserName(Long.valueOf(loginId)), "dc.mdm.dep.profile_modified", remarkArg, customerId);
        }
        else {
            this.existingDO.addRow(templateRow);
            DEPEnrollmentUtil.setDEPEnrollmentStatus(3, customerId);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2068, null, DMUserHandler.getUserName(Long.valueOf(loginId)), "dc.mdm.dep.profile_created", remarkArg, customerId);
        }
    }
    
    private void addOrUpdateWindowsWICDEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("WindowsICDEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("WindowsICDEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("WindowsICDEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateWindowsLaptopEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("WindowsLaptopEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("WindowsLaptopEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("WindowsLaptopEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                templateRow.set("LOGIN_ID", templateJSON.opt("LOGIN_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    private void addOrUpdateWindowsAzureADEnrollmentTemplateTable(final JSONObject templateJSON, final Row enrollmentTemplateRow) throws Exception {
        Row templateRow = null;
        if (this.existingDO.isEmpty()) {
            templateRow = new Row("WindowsAzureADEnrollmentTemplate");
            templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
            this.existingDO.addRow(templateRow);
        }
        else {
            templateRow = this.existingDO.getRow("WindowsAzureADEnrollmentTemplate");
            if (templateRow == null) {
                templateRow = new Row("WindowsAzureADEnrollmentTemplate");
                templateRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                this.existingDO.addRow(templateRow);
            }
        }
    }
    
    public static boolean validateEnrollmentTemplateBasicAuthentication(final String templateToken, final String userName, final String passcode) {
        try {
            final JSONObject templateDetailsJSON = getEnrollmentTemplateCredentialDetails(templateToken);
            if (templateDetailsJSON == null || !templateDetailsJSON.has("USERNAME")) {
                EnrollmentTemplateHandler.logger.log(Level.SEVERE, "EnrollmentTemplateAccessCredentials Template does not have row for templatetoken:{0}", templateToken);
                return false;
            }
            final String templateUname = String.valueOf(templateDetailsJSON.get("USERNAME"));
            final String templatePassword = String.valueOf(templateDetailsJSON.get("PASSWORD"));
            if (templateUname.equalsIgnoreCase(userName) && passcode.equals(templatePassword)) {
                return true;
            }
            if (templateUname.equalsIgnoreCase(userName)) {
                EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Unable to authenticate template token {0} as password is wrong", templateToken);
            }
            else {
                EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Unable to authenticate template token {0} as both username and password is wrong", templateToken);
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "ModernMacEnrollmentServlet exception while authenticating ...");
        }
        return false;
    }
    
    private String getTemplateToken() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(128, random).toString(16);
    }
    
    public JSONObject getEnrollmentTemplateForTemplateToken(final String templateToken) {
        final JSONObject templateJSON = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
            final Criteria cTemplateToken = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0);
            selectQuery.setCriteria(cTemplateToken);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (!DO.isEmpty()) {
                final Row userRow = DO.getFirstRow("EnrollmentTemplate");
                final Object templateId = userRow.get("TEMPLATE_ID");
                final Object templateType = userRow.get("TEMPLATE_TYPE");
                if (!this.isValidEnrollmentTemplate((int)templateType, (Long)templateId)) {
                    EnrollmentTemplateHandler.logger.log(Level.INFO, "Rejected enrollment: Invalid template token:{0}", userRow);
                    return null;
                }
                templateJSON.put("CUSTOMER_ID", userRow.get("CUSTOMER_ID"));
                templateJSON.put("TEMPLATE_TYPE", templateType);
                templateJSON.put("TEMPLATE_ID", templateId);
                templateJSON.put("ADDED_USER", userRow.get("ADDED_USER"));
            }
            else {
                EnrollmentTemplateHandler.logger.log(Level.SEVERE, "No rows found in ENROLLMENTTEMPLATE for token {0}", templateToken);
            }
        }
        catch (final Exception e) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception while getting auth token", e);
        }
        return templateJSON;
    }
    
    private boolean isValidEnrollmentTemplate(final int templateType, final Long templateId) throws Exception {
        switch (templateType) {
            case 10: {
                return new DEPAdminEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 11: {
                return new AppleConfiguratorEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 20: {
                return new AndroidAdminEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 21: {
                return new KNOXAdminEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 22: {
                return new AndroidQREnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 23: {
                return new AndroidZTEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 30: {
                return new WindowsWICDEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 31: {
                return new WindowsLaptopEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 40: {
                return new GSuiteChromeDeviceEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 32: {
                return new WindowsAzureADEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 12: {
                return new MacModernMgmtEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 33: {
                return new WindowsModernMgmtEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            case 50: {
                return new IOSMigrationEnrollmentHandler().isValidEnrollmentTemplate(templateId);
            }
            default: {
                return false;
            }
        }
    }
    
    public Integer getEnrollmentTemplateTypeForTemplateToken(final String templateToken) throws JSONException {
        final JSONObject json = this.getEnrollmentTemplateForTemplateToken(templateToken);
        if (json != null) {
            return json.getInt("TEMPLATE_TYPE");
        }
        return null;
    }
    
    public int getEnrollmentTemplateTypeForErid(final Long erid) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
            final Join requestMap = new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            selectQuery.addJoin(requestMap);
            final Criteria eridCriteria = new Criteria(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
            selectQuery.setCriteria(eridCriteria);
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (!DO.isEmpty()) {
                final Row templateRow = DO.getFirstRow("EnrollmentTemplate");
                return (int)templateRow.get("TEMPLATE_TYPE");
            }
        }
        catch (final Exception e) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception while getting auth token", e);
        }
        return -1;
    }
    
    public String getFirstTemplateToken(final Integer templateType) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getDefaultCustomer(), 0);
        sQuery.setCriteria(templateTypeCriteria.and(customerIdCriteria));
        sQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row templateRow = dO.getFirstRow("EnrollmentTemplate");
            return (String)templateRow.get("TEMPLATE_TOKEN");
        }
        throw new RuntimeException("No Token Found for Default Customer");
    }
    
    public String getTemplateTokenForUserId(final Long userId, final Integer templateType, final Long customerId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Criteria userCriteria = new Criteria(new Column("EnrollmentTemplate", "ADDED_USER"), (Object)userId, 0);
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.setCriteria(userCriteria.and(templateTypeCriteria).and(customerIdCriteria));
        sQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row templateRow = dO.getFirstRow("EnrollmentTemplate");
            return (String)templateRow.get("TEMPLATE_TOKEN");
        }
        throw new RuntimeException("No Token Found for User " + userId);
    }
    
    public Long getTemplateTokenIdForUserId(final Long userId, final Long customerId, final Integer templateType) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        final Criteria userCriteria = new Criteria(new Column("EnrollmentTemplate", "ADDED_USER"), (Object)userId, 0);
        final Criteria customerCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        sQuery.setCriteria(userCriteria.and(templateTypeCriteria).and(customerCriteria));
        sQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row templateRow = dO.getFirstRow("EnrollmentTemplate");
            return (Long)templateRow.get("TEMPLATE_ID");
        }
        throw new RuntimeException("No Token Found for User " + userId);
    }
    
    private static JSONObject getModenMacMgmtEnrollmentTemplateDetails(final Criteria cri) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        sQuery.addJoin(new Join("EnrollmentTemplate", "MacModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)12, 0);
        sQuery.setCriteria(templateTypeCriteria.and(cri));
        sQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
        sQuery.addSelectColumn(new Column("MacModernMgmtEnrollmentTemplate", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row templateRow = dO.getFirstRow("EnrollmentTemplate");
            final JSONObject templateJSON = MDMDBUtil.rowToJSON(templateRow);
            final Row modernRow = dO.getFirstRow("MacModernMgmtEnrollmentTemplate");
            final JSONObject modernJSON = MDMDBUtil.rowToJSON(modernRow);
            JSONUtil.putAll(templateJSON, modernJSON);
            return templateJSON;
        }
        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "getModenMacMgmtEnrollmentTemplateDetails -> No MACMODERNMGMTENROLLMENTTEMPLATE for customer , probably 1st device in Mac UEM");
        return null;
    }
    
    private static JSONObject getModenWindowsEnrollmentTemplateDetails(final Criteria cri) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        sQuery.addJoin(new Join("EnrollmentTemplate", "WindowsModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)33, 0);
        sQuery.setCriteria(templateTypeCriteria.and(cri));
        sQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
        sQuery.addSelectColumn(new Column("WindowsModernMgmtEnrollmentTemplate", "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Row templateRow = dO.getFirstRow("EnrollmentTemplate");
            final JSONObject templateJSON = MDMDBUtil.rowToJSON(templateRow);
            final Row modernRow = dO.getFirstRow("WindowsModernMgmtEnrollmentTemplate");
            final JSONObject modernJSON = MDMDBUtil.rowToJSON(modernRow);
            JSONUtil.putAll(templateJSON, modernJSON);
            return templateJSON;
        }
        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "getModenMacMgmtEnrollmentTemplateDetails -> No MACMODERNMGMTENROLLMENTTEMPLATE for customer , probably 1st device in Mac UEM");
        return null;
    }
    
    public static JSONObject getModenMacMgmtEnrollmentTemplateDetailsForCustomer(final Long customerID) throws Exception {
        return getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerID, MDMUtil.getAdminUserId());
    }
    
    public static JSONObject getModenMacMgmtEnrollmentTemplateDetailsForCustomer(final Long customerID, final Long userId) throws Exception {
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        JSONObject templateDetailsJSON = getModenMacMgmtEnrollmentTemplateDetails(customerIdCriteria);
        if (templateDetailsJSON == null) {
            EnrollmentTemplateHandler.logger.log(Level.INFO, "Modern Mac Mgmt enrollment template not already present for the customer : {0} | User Id: {1}. So adding now.", new Object[] { customerID, userId });
            final JSONObject templateJSON = new JSONObject();
            templateJSON.put("CUSTOMER_ID", (Object)customerID);
            templateJSON.put("ADDED_USER", (Object)userId);
            new MacModernMgmtEnrollmentHandler().addorUpdateAdminEnrollmentTemplate(templateJSON);
            templateDetailsJSON = getModenMacMgmtEnrollmentTemplateDetails(customerIdCriteria);
        }
        final JSONObject templateCredentiaJSON = getEnrollmentTemplateCredentialDetails(String.valueOf(templateDetailsJSON.get("TEMPLATE_TOKEN")));
        JSONUtil.putAll(templateDetailsJSON, templateCredentiaJSON);
        return templateDetailsJSON;
    }
    
    public static JSONObject getModenWindowsEnrollmentTemplateDetailsForCustomer(final Long customerID, final Long userId) throws Exception {
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        JSONObject templateDetailsJSON = getModenWindowsEnrollmentTemplateDetails(customerIdCriteria);
        if (templateDetailsJSON == null) {
            final JSONObject templateJSON = new JSONObject();
            templateJSON.put("CUSTOMER_ID", (Object)customerID);
            templateJSON.put("ADDED_USER", (Object)userId);
            new WindowsModernMgmtEnrollmentHandler().addorUpdateAdminEnrollmentTemplate(templateJSON);
            templateDetailsJSON = getModenWindowsEnrollmentTemplateDetails(customerIdCriteria);
        }
        return templateDetailsJSON;
    }
    
    public static JSONObject getModenMacMgmtEnrollmentTemplateDetailsForTemplateToken(final String templateToken) throws Exception {
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0);
        return getModenMacMgmtEnrollmentTemplateDetails(customerIdCriteria);
    }
    
    public static JSONObject getEnrollmentTemplateCredentialDetails(final String templateToken) throws Exception {
        final SelectQuery enrollmentCredQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        enrollmentCredQuery.addJoin(new Join("EnrollmentTemplate", "EnrollmentTemplateAccessCredentials", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        enrollmentCredQuery.setCriteria(new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0));
        enrollmentCredQuery.addSelectColumn(new Column("EnrollmentTemplateAccessCredentials", "*"));
        final DataObject enrolmentCredDO = MDMUtil.getPersistence().get(enrollmentCredQuery);
        if (!enrolmentCredDO.isEmpty()) {
            final Row enrollmentCredRow = enrolmentCredDO.getRow("EnrollmentTemplateAccessCredentials");
            return MDMDBUtil.rowToJSON(enrollmentCredRow);
        }
        return null;
    }
    
    public void addOrUpdateEnrollmentRequestToTemplate(final JSONObject dataJSON) {
        try {
            final String templateToken = dataJSON.optString("TEMPLATE_TOKEN", (String)null);
            Long templateId = dataJSON.optLong("TEMPLATE_ID", -1L);
            final Long enrollmentRequestId = dataJSON.getLong("ENROLLMENT_REQUEST_ID");
            if (templateId == -1L && templateToken != null) {
                templateId = (Long)DBUtil.getValueFromDB("EnrollmentTemplate", "TEMPLATE_TOKEN", (Object)templateToken, "TEMPLATE_ID");
            }
            else if (templateId == -1L) {
                throw new RuntimeException("Either Template Token or Template Id must be specified");
            }
            final List<Long> enrollmentRequestList = new ArrayList<Long>();
            enrollmentRequestList.add(enrollmentRequestId);
            this.addOrUpdateEnrollmentRequestToTemplate(enrollmentRequestList, templateId);
        }
        catch (final Exception e) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateEnrollmentRequestToTemplate ", e);
        }
    }
    
    public void addOrUpdateEnrollmentRequestToTemplate(final List<Long> enrollmentRequestIDList, final Long templateId) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplateToRequest"));
            sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            final Criteria criteria = new Criteria(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestIDList.toArray(), 8);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            sQuery.setCriteria(criteria);
            final int newTemplateType = getTemplateType(templateId);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            for (final Long enrollmentRequestID : enrollmentRequestIDList) {
                final Row existingRow = dO.getRow("EnrollmentTemplateToRequest", new Criteria(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
                if (existingRow == null) {
                    final Row row = new Row("EnrollmentTemplateToRequest");
                    row.set("TEMPLATE_ID", (Object)templateId);
                    row.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                    dO.addRow(row);
                    EnrollmentTemplateHandler.logger.log(Level.INFO, "Adding Enrollment Type for requestID {0} to templateID {1}", new Object[] { enrollmentRequestID, templateId });
                }
                else {
                    final Long existingTemplateID = (Long)existingRow.get("TEMPLATE_ID");
                    final Row existingTemplateRow = dO.getRow("EnrollmentTemplate", new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_ID"), (Object)existingTemplateID, 0));
                    final int existingTemplateType = (int)existingTemplateRow.get("TEMPLATE_TYPE");
                    if (((existingTemplateType == 11 || existingTemplateType == 12) && newTemplateType == 10) || newTemplateType == existingTemplateType) {
                        continue;
                    }
                    final Row enrollmentTempToReqToBeUpdatedRow = existingRow;
                    enrollmentTempToReqToBeUpdatedRow.set("TEMPLATE_ID", (Object)templateId);
                    dO.updateRow(enrollmentTempToReqToBeUpdatedRow);
                    EnrollmentTemplateHandler.logger.log(Level.INFO, "Changing Enrollment Type for requestID {0} to templateID {1}", new Object[] { enrollmentRequestID, templateId });
                }
            }
            MDMUtil.getPersistence().update(dO);
        }
        catch (final Exception e) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateEnrollmentRequestToTemplate ", e);
        }
    }
    
    public static synchronized boolean isUserAssignmentCompleted(final Long enrollmentId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentToRequest"));
        final Join baseJoin = new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2);
        final Join userJoin = new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1);
        sQuery.addJoin(baseJoin);
        sQuery.addJoin(userJoin);
        final Criteria criteria = new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentId, 0);
        sQuery.setCriteria(criteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator<Row> iter = dO.getRows("DeviceEnrollmentToUser");
            return iter.hasNext();
        }
        return true;
    }
    
    public int getPlatformForTemplate(final Integer templateType) {
        int platformType = -1;
        switch (templateType) {
            case 20:
            case 21:
            case 22:
            case 23: {
                platformType = 2;
                break;
            }
            case 10:
            case 11:
            case 12:
            case 50: {
                platformType = 1;
                break;
            }
            case 30:
            case 31:
            case 32:
            case 33: {
                platformType = 3;
                break;
            }
            case 40: {
                platformType = 4;
                break;
            }
        }
        return platformType;
    }
    
    public static void updateAdminEnrollmentTemplate(final Long roleID) {
        try {
            final boolean accessGiven = checkIfEnrollmentAccessGiven(roleID);
            final ArrayList<String> loginID = getLoginIDfromRoleID(roleID);
            if (loginID == null) {
                return;
            }
            final DataObject DO = SyMUtil.getPersistence().get("AndroidAdminEnrollmentTemplate", new Criteria(Column.getColumn("AndroidAdminEnrollmentTemplate", "LOGIN_ID"), (Object)loginID.toArray(), 8));
            if (!DO.isEmpty()) {
                final Iterator itr = DO.getRows("AndroidAdminEnrollmentTemplate");
                if (!accessGiven) {
                    while (itr.hasNext()) {
                        final Row row = itr.next();
                        final Long loginid = (Long)row.get("LOGIN_ID");
                        DEPTechnicianUserListener.deleteEnrollmentTemplates(loginid);
                        loginID.remove(String.valueOf(loginid));
                    }
                }
            }
            if (accessGiven) {
                for (int i = 0; i < loginID.size(); ++i) {
                    DEPTechnicianUserListener.addEnrollmentTemplates(Long.valueOf(loginID.get(i)));
                }
            }
        }
        catch (final Exception exp) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in update enrollmentTemplateTables: ", exp);
        }
    }
    
    public static boolean checkIfEnrollmentAccessGiven(final Long roleID) {
        boolean accessGiven = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMRoleModuleRelation"));
            selectQuery.addJoin(new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2));
            selectQuery.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)new String[] { "MDM_Enrollment_Write", "ModernMgmt_Enrollment_Write" }, 8).and(new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)roleID, 0)));
            selectQuery.addSelectColumn(Column.getColumn("UMModule", "UM_MODULE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("UMRoleModuleRelation", "*"));
            final DataObject DO = SyMUtil.getPersistence().get(selectQuery);
            if (!DO.isEmpty()) {
                accessGiven = true;
            }
        }
        catch (final Exception e) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in checkIfEnrollmentAccessGiven: ", e);
        }
        return accessGiven;
    }
    
    public static ArrayList<String> getLoginIDfromRoleID(final Long roleID) {
        final ArrayList<String> loginID = new ArrayList<String>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
            sQuery.addJoin(new Join("UMRole", "UsersRoleMapping", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("UsersRoleMapping", "UM_ROLE_ID"), (Object)roleID, 0);
            sQuery.addSelectColumn(Column.getColumn("UsersRoleMapping", "*"));
            sQuery.addSelectColumn(Column.getColumn("UMRole", "*"));
            sQuery.setCriteria(criteria);
            final DataObject dataObj = SyMUtil.getPersistence().get(sQuery);
            if (!dataObj.isEmpty()) {
                final Iterator itr = dataObj.getRows("UsersRoleMapping");
                while (itr.hasNext()) {
                    final Row LoginRow = itr.next();
                    loginID.add(String.valueOf(LoginRow.get("LOGIN_ID")));
                }
            }
        }
        catch (final Exception exp) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in getLoginIDfromRoleID: ", exp);
        }
        return loginID;
    }
    
    public static List<Long> getDefaultGroupIDForTemplate(final Long enrollmentTemplateID) {
        final List<Long> returnList = new ArrayList<Long>();
        try {
            final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplateToGroupRel"));
            sql.addJoin(new Join("EnrollmentTemplateToGroupRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria templateIDCriteira = new Criteria(new Column("EnrollmentTemplateToGroupRel", "TEMPLATE_ID"), (Object)enrollmentTemplateID, 0);
            final Criteria hiddenGroupCri = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1);
            sql.setCriteria(templateIDCriteira.and(hiddenGroupCri));
            sql.addSelectColumn(new Column("EnrollmentTemplateToGroupRel", "TEMPLATE_ID"));
            sql.addSelectColumn(new Column("EnrollmentTemplateToGroupRel", "GROUP_RESOURCE_ID"));
            final DataObject groupIDDO = MDMUtil.getPersistence().get(sql);
            if (!groupIDDO.isEmpty()) {
                final Iterator templateToGrpIt = groupIDDO.getRows("EnrollmentTemplateToGroupRel", (Criteria)null);
                while (templateToGrpIt.hasNext()) {
                    final Row row = templateToGrpIt.next();
                    returnList.add((Long)row.get("GROUP_RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in get Groups for Enrollment template {0}", ex);
        }
        return returnList;
    }
    
    public static void addOrUpdateEnrollmentTemplateToInvisibleGroup(final Long customGroupID, final Long enrollmentTemplateID) {
        final Criteria groupTypeCri = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 0);
        if (customGroupID != null && customGroupID != -1L) {
            final List<Long> customGrpIdList = new ArrayList<Long>();
            customGrpIdList.add(customGroupID);
            addOrUpdateEnrollmentTemplateToGroup(customGrpIdList, enrollmentTemplateID, groupTypeCri);
            final List<Long> devicesInGroup = getDeviceForEnrollmentIDsInCustomGroup(customGroupID);
            addOrUpdateTemplateToDeviceForEnrollment(devicesInGroup, enrollmentTemplateID);
        }
    }
    
    public static void addOrUpdateEnrollmentTemplateToGroup(final List<Long> customGroupID, final Long enrollmentTemplateID) {
        final Criteria groupTypeCri = new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1);
        addOrUpdateEnrollmentTemplateToGroup(customGroupID, enrollmentTemplateID, groupTypeCri);
    }
    
    private static void addOrUpdateEnrollmentTemplateToGroup(final List<Long> customGroupIDList, final Long enrollmentTemplateID, final Criteria groupTypeCri) {
        try {
            if (customGroupIDList != null) {
                final Long assignedTime = MDMUtil.getCurrentTimeInMillis();
                final Long templateCreatedUser = getTemplateCreatedUser(enrollmentTemplateID);
                final SelectQuery enrollmentTemplateToGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplateToGroupRel"));
                enrollmentTemplateToGroupQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria templateiDCri = new Criteria(new Column("EnrollmentTemplateToGroupRel", "TEMPLATE_ID"), (Object)enrollmentTemplateID, 0);
                enrollmentTemplateToGroupQuery.setCriteria(templateiDCri.and(groupTypeCri));
                enrollmentTemplateToGroupQuery.addSelectColumn(new Column("EnrollmentTemplateToGroupRel", "*"));
                final DataObject resultDo = MDMUtil.getPersistence().get(enrollmentTemplateToGroupQuery);
                for (final Long customGrpId : customGroupIDList) {
                    final Row row = resultDo.getRow("EnrollmentTemplateToGroupRel", new Criteria(Column.getColumn("EnrollmentTemplateToGroupRel", "GROUP_RESOURCE_ID"), (Object)customGrpId, 0));
                    if (row != null) {
                        row.set("ASSIGNED_TIME", (Object)assignedTime);
                        row.set("ASSIGNED_USER_ID", (Object)templateCreatedUser);
                        resultDo.updateRow(row);
                    }
                    else {
                        final Row relationRow = new Row("EnrollmentTemplateToGroupRel");
                        relationRow.set("GROUP_RESOURCE_ID", (Object)customGrpId);
                        relationRow.set("ASSIGNED_TIME", (Object)assignedTime);
                        relationRow.set("ASSIGNED_USER_ID", (Object)templateCreatedUser);
                        relationRow.set("TEMPLATE_ID", (Object)enrollmentTemplateID);
                        resultDo.addRow(relationRow);
                    }
                }
                resultDo.deleteRows("EnrollmentTemplateToGroupRel", new Criteria(Column.getColumn("EnrollmentTemplateToGroupRel", "ASSIGNED_TIME"), (Object)assignedTime, 7));
                MDMUtil.getPersistence().update(resultDo);
            }
            else {
                final DeleteQuery delQ = (DeleteQuery)new DeleteQueryImpl("EnrollmentTemplateToGroupRel");
                delQ.addJoin(new Join("EnrollmentTemplateToGroupRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria templateIDCri = new Criteria(new Column("EnrollmentTemplateToGroupRel", "TEMPLATE_ID"), (Object)enrollmentTemplateID, 0);
                delQ.setCriteria(templateIDCri.and(groupTypeCri));
                MDMUtil.getPersistence().delete(delQ);
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateEnrollmentTemplateToGroup while updating", ex);
        }
    }
    
    private static List<Long> getDeviceForEnrollmentIDsInCustomGroup(final Long customGroupID) {
        final List<Long> dfeList = new ArrayList<Long>();
        try {
            final List idList = DBUtil.getDistinctColumnValue("DeviceEnrollmentToGroup", "ENROLLMENT_DEVICE_ID", new Criteria(new Column("DeviceEnrollmentToGroup", "ASSOCIATED_GROUP_ID"), (Object)customGroupID, 0));
            for (int i = 0; i < idList.size(); ++i) {
                dfeList.add(Long.parseLong(idList.get(i)));
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in getDevcieForEnrollmentIDsInCustomGroup while updating", ex);
        }
        return dfeList;
    }
    
    public static void addOrUpdateTemplateToDeviceForEnrollment(final List<Long> deviceForEnrollmentList, final Long templateID) {
        try {
            final Long templateCreatedUser = getTemplateCreatedUser(templateID);
            final Long customerID = getCustomerIDForTemplate(templateID);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplateToDeviceEnrollment"));
            query.addJoin(new Join("EnrollmentTemplateToDeviceEnrollment", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            query.addSelectColumn(new Column("EnrollmentTemplateToDeviceEnrollment", "ENROLLMENT_DEVICE_ID"));
            query.addSelectColumn(new Column("EnrollmentTemplateToDeviceEnrollment", "TEMPLATE_ID"));
            final Criteria customerCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
            query.setCriteria(customerCriteria);
            final DataObject resultDO = MDMUtil.getPersistence().get(query);
            for (final Long deviceForEnrollMentID : deviceForEnrollmentList) {
                boolean isNewRow = false;
                Row dfeToTRow = resultDO.getRow("EnrollmentTemplateToDeviceEnrollment", new Criteria(new Column("EnrollmentTemplateToDeviceEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollMentID, 0));
                if (dfeToTRow == null) {
                    isNewRow = true;
                    dfeToTRow = new Row("EnrollmentTemplateToDeviceEnrollment");
                    dfeToTRow.set("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollMentID);
                }
                dfeToTRow.set("ASSIGNED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
                dfeToTRow.set("TEMPLATE_ID", (Object)templateID);
                dfeToTRow.set("ASSIGNED_USER_ID", (Object)templateCreatedUser);
                if (isNewRow) {
                    resultDO.addRow(dfeToTRow);
                }
                else {
                    resultDO.updateRow(dfeToTRow);
                }
            }
            MDMUtil.getPersistence().update(resultDO);
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateTemplateToDeviceForEnrollment while updating", ex);
        }
    }
    
    public static void addOrUpdateTemplateToDeviceForEnrollment(final Long tokenID) {
        try {
            final Long groupID = DEPEnrollmentUtil.createNewCustomGroupForDEPToken(tokenID);
            final List<Long> devicesInGroup = getDeviceForEnrollmentIDsInCustomGroup(groupID);
            final Long templateID = (Long)DBUtil.getValueFromDB("EnrollmentTemplateToGroupRel", "GROUP_RESOURCE_ID", (Object)groupID, "TEMPLATE_ID");
            if (templateID != null) {
                addOrUpdateTemplateToDeviceForEnrollment(devicesInGroup, templateID);
            }
            else {
                EnrollmentTemplateHandler.logger.log(Level.INFO, "TemplateID received is null . Probably because of FirstSync that happns soon after token upload.");
                EnrollmentTemplateHandler.logger.log(Level.INFO, "When templateId is null here , it means New DEP token is added and sync devices happens before DEP Profile creation.");
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateTemplateToDeviceForEnrollment while updating{0}", ex);
        }
    }
    
    public static Long getTemplateCreatedUser(final Long templateID) {
        try {
            return (Long)DBUtil.getValueFromDB("EnrollmentTemplate", "TEMPLATE_ID", (Object)templateID, "ADDED_USER");
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentTemplateHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static int getTemplateType(final Long templateID) {
        try {
            return (int)DBUtil.getValueFromDB("EnrollmentTemplate", "TEMPLATE_ID", (Object)templateID, "TEMPLATE_TYPE");
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentTemplateHandler.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public static Long getCustomerIDForTemplate(final Long templateID) {
        try {
            return (Long)DBUtil.getValueFromDB("EnrollmentTemplate", "TEMPLATE_ID", (Object)templateID, "CUSTOMER_ID");
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentTemplateHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static void updateUdidOnReEnroll(final Long enrollmentReqId, final String udid) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentReqId, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)2, 0));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row managedDeviceRow = dataObject.getFirstRow("ManagedDevice");
                managedDeviceRow.set("UDID", (Object)udid);
                dataObject.updateRow(managedDeviceRow);
                final Row deviceforEnrollmentRow = dataObject.getFirstRow("DeviceForEnrollment");
                deviceforEnrollmentRow.set("UDID", (Object)udid);
                dataObject.updateRow(deviceforEnrollmentRow);
                MDMUtil.getPersistenceLite().update(dataObject);
                Logger.getLogger("MDMEnrollment").log(Level.INFO, "Updating udid for device on re enrolling from awaiting user assignment!! ERID - {0} , Updated UDID - {1}", new Object[] { enrollmentReqId, udid });
            }
        }
        catch (final Exception exp) {
            Logger.getLogger(EnrollmentTemplateHandler.class.getName()).log(Level.SEVERE, null, exp);
        }
    }
    
    public static Long getTemplateIdForTemplateType(final int templateType, final Long userID, final Long customerId) {
        Long templateId = -1L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0).and(new Criteria(Column.getColumn("EnrollmentTemplate", "ADDED_USER"), (Object)userID, 0)).and(new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0)));
            switch (templateType) {
                case 20: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "AndroidAdminEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidAdminEnrollmentTemplate", "TEMPLATE_ID", "ANDROIDADMINENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 22: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "AndroidQREnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidQREnrollmentTemplate", "TEMPLATE_ID", "ANDROIDQRENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 21: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "KNOXMobileEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("KNOXMobileEnrollmentTemplate", "TEMPLATE_ID", "KNOXMOBILEENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 23: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "AndroidZTEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("AndroidZTEnrollmentTemplate", "TEMPLATE_ID", "ANDROIDZTENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 11: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "AppleConfigEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("AppleConfigEnrollmentTemplate", "TEMPLATE_ID", "APPLECONFIGENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 10: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("AppleConfigEnrollmentTemplate", "TEMPLATE_ID", "DEPENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 12: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "MacModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("MacModernMgmtEnrollmentTemplate", "TEMPLATE_ID", "MACMODERNMGMTENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 31: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "WindowsLaptopEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsLaptopEnrollmentTemplate", "TEMPLATE_ID", "WINDOWSLAPTOPENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 30: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "WindowsICDEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsICDEnrollmentTemplate", "TEMPLATE_ID", "WINDOWSICDENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 32: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "WindowsAzureADEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsAzureADEnrollmentTemplate", "TEMPLATE_ID", "WINDOWSAZUREADENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 40: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "GSChromeEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("GSChromeEnrollmentTemplate", "TEMPLATE_ID", "GSCHROMEENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
                case 33: {
                    selectQuery.addJoin(new Join("EnrollmentTemplate", "WindowsModernMgmtEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("WindowsModernMgmtEnrollmentTemplate", "TEMPLATE_ID", "WINDOWSMODERNMGMTENROLLMENTTEMPLATE.TEMPLATE_ID"));
                    break;
                }
            }
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                templateId = (Long)dataObject.getFirstValue("EnrollmentTemplate", "TEMPLATE_ID");
            }
        }
        catch (final Exception exp) {
            EnrollmentTemplateHandler.logger.log(Level.SEVERE, "Exception while retreving template id for template type and user: ", exp);
        }
        return templateId;
    }
    
    protected static String getTemplateTokenForTemplateId(final Long templateID) throws Exception {
        String templateToken = null;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        sq.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0));
        sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TOKEN"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
        if (!DO.isEmpty()) {
            templateToken = (String)DO.getFirstValue("EnrollmentTemplate", "TEMPLATE_TOKEN");
        }
        return templateToken;
    }
    
    public JSONObject getDeviceForEnrollmentIDDetails(final Long deviceID, final Long customerId) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        query.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "WinModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "MigrationDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
        query.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"));
        query.addSelectColumn(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        query.addSelectColumn(Column.getColumn("MigrationDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        final Criteria criteria = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceID, 0).and(new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0));
        query.setCriteria(criteria);
        DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
        if (!dataObject.isEmpty()) {
            final Long knoxDfeId = (Long)(dataObject.containsTable("KNOXMobileDeviceForEnrollment") ? dataObject.getFirstRow("KNOXMobileDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long nfcDfeId = (Long)(dataObject.containsTable("AndroidNFCDeviceForEnrollment") ? dataObject.getFirstRow("AndroidNFCDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long appleConfigDfeId = (Long)(dataObject.containsTable("AppleConfigDeviceForEnrollment") ? dataObject.getFirstRow("AppleConfigDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long winIcdDfeId = (Long)(dataObject.containsTable("WindowsICDDeviceForEnrollment") ? dataObject.getFirstRow("WindowsICDDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long emmDfeId = (Long)(dataObject.containsTable("AndroidQRDeviceForEnrollment") ? dataObject.getFirstRow("AndroidQRDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long ztLapDfeId = (Long)(dataObject.containsTable("AndroidZTDeviceForEnrollment") ? dataObject.getFirstRow("AndroidZTDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long winAzureDfeId = (Long)(dataObject.containsTable("WinAzureADDeviceForEnrollment") ? dataObject.getFirstRow("WinAzureADDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long depDfeId = (Long)(dataObject.containsTable("AppleDEPDeviceForEnrollment") ? dataObject.getFirstRow("AppleDEPDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long winLapDfeId = (Long)(dataObject.containsTable("WindowsLaptopDeviceForEnrollment") ? dataObject.getFirstRow("WindowsLaptopDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long chromeBookDfeId = (Long)(dataObject.containsTable("GSChromeDeviceForEnrollment") ? dataObject.getFirstRow("GSChromeDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long macDfeId = (Long)(dataObject.containsTable("MacModernMgmtDeviceForEnrollment") ? dataObject.getFirstRow("MacModernMgmtDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long winmmDfeId = (Long)(dataObject.containsTable("WinModernMgmtDeviceForEnrollment") ? dataObject.getFirstRow("WinModernMgmtDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            final Long migrationDfeId = (Long)(dataObject.containsTable("MigrationDeviceForEnrollment") ? dataObject.getFirstRow("MigrationDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
            Integer templateType = null;
            Integer platform = null;
            if (knoxDfeId != null || nfcDfeId != null || emmDfeId != null || ztLapDfeId != null) {
                templateType = ((knoxDfeId != null) ? 21 : ((nfcDfeId != null) ? 20 : ((emmDfeId != null) ? 22 : 23)));
                platform = 2;
            }
            else if (appleConfigDfeId != null || depDfeId != null || macDfeId != null) {
                templateType = ((appleConfigDfeId != null) ? 11 : ((depDfeId != null) ? 10 : 12));
                platform = 1;
            }
            else if (winAzureDfeId != null || winIcdDfeId != null || winLapDfeId != null || winmmDfeId != null) {
                templateType = ((winAzureDfeId != null) ? 32 : ((winIcdDfeId != null) ? 30 : ((winLapDfeId != null) ? 31 : 33)));
                platform = 3;
            }
            else if (chromeBookDfeId != null) {
                templateType = 40;
                platform = 4;
            }
            else if (migrationDfeId != null) {
                templateType = 50;
                platform = 0;
            }
            Row row = null;
            row = dataObject.getRow("DeviceForEnrollment");
            jsonObject.put("IMEI", row.get("IMEI"));
            jsonObject.put("SERIAL_NUMBER", row.get("SERIAL_NUMBER"));
            jsonObject.put("CUSTOMER_ID", row.get("CUSTOMER_ID"));
            jsonObject.put("UDID", row.get("UDID"));
            if (templateType == null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
                selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
                selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"));
                selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)deviceID, 0));
                dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (dataObject.isEmpty()) {
                    return null;
                }
                row = dataObject.getRow("EnrollmentTemplate");
                templateType = (Integer)row.get("TEMPLATE_TYPE");
                row = dataObject.getRow("DeviceEnrollmentRequest");
                platform = (Integer)row.get("PLATFORM_TYPE");
            }
            jsonObject.put("TEMPLATE_TYPE", (Object)templateType);
            jsonObject.put("PLATFORM_TYPE", (Object)platform);
            return jsonObject;
        }
        return null;
    }
    
    public static Long getTemplateIDForTemplateToken(final String templateToken) throws Exception {
        Long templateID = null;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        sq.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0));
        sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        sq.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TOKEN"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
        if (!DO.isEmpty()) {
            templateID = (Long)DO.getFirstValue("EnrollmentTemplate", "TEMPLATE_ID");
        }
        return templateID;
    }
    
    public JSONObject getTemplateDetailsFromTemplateID(final Long templateID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final JSONObject jsonObject = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("EnrollmentTemplate");
            final int templateType = (int)row.get("TEMPLATE_TYPE");
            final Long customerID = (Long)row.get("CUSTOMER_ID");
            final int platform = this.getPlatformForTemplate(templateType);
            jsonObject.put("TEMPLATE_TYPE", templateType);
            jsonObject.put("PLATFORM_TYPE", platform);
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
        }
        return jsonObject;
    }
    
    public static boolean validateWithGenericID(final String genericID, final String otp, final String templateToken, final String serialNumber) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "OTPPassword", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OTPPassword", "OTP_PASSWORD"));
        selectQuery.addSelectColumn(Column.getColumn("OTPPassword", "FAILED_ATTEMPTS"));
        final Criteria dcResCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0);
        final Criteria otpCriteria = new Criteria(Column.getColumn("OTPPassword", "OTP_PASSWORD"), (Object)otp, 0);
        final Criteria expiryCriteria = new Criteria(Column.getColumn("OTPPassword", "EXPIRE_TIME"), (Object)System.currentTimeMillis(), 5);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0);
        final Criteria serialNumCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumber, 0);
        selectQuery.setCriteria(dcResCriteria.and(otpCriteria).and(templateCriteria).and(expiryCriteria).and(serialNumCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        boolean isValid = false;
        if (!dataObject.isEmpty()) {
            final Long erid = (Long)dataObject.getRow("DeviceEnrollmentToRequest").get("ENROLLMENT_REQUEST_ID");
            final Row otpRow = dataObject.getRow("OTPPassword");
            final String otpFromDB = (String)otpRow.get("OTP_PASSWORD");
            final Integer failedAttempts = (Integer)otpRow.get("FAILED_ATTEMPTS");
            if (!MDMStringUtils.isEmpty(otpFromDB) && !MDMStringUtils.isEmpty(otp) && otpFromDB.equals(otp) && failedAttempts <= 3) {
                isValid = true;
                final Row row = dataObject.getFirstRow("DeviceEnrollmentToRequest");
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OTPPassword");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), row.get("ENROLLMENT_REQUEST_ID"), 0));
                MDMUtil.getPersistenceLite().delete(deleteQuery);
            }
            else {
                isValid = false;
                MDMEnrollmentRequestHandler.getInstance().incrementOTPFailedAttemptCount(erid);
            }
        }
        return isValid;
    }
    
    public String getTemplateTokenForEnrollmentRequest(final long enrollmentRequestId) throws DataAccessException {
        if (enrollmentRequestId != -1L) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
            final Join enrollmentTemplateToRequestJoin = new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            final Join enrollmentRequestJoin = new Join("EnrollmentTemplateToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            selectQuery.addJoin(enrollmentTemplateToRequestJoin);
            selectQuery.addJoin(enrollmentRequestJoin);
            selectQuery.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0));
            selectQuery.addSelectColumn(new Column("EnrollmentTemplate", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row enrollmentTemplateRow = dataObject.getFirstRow("EnrollmentTemplate");
                return (String)enrollmentTemplateRow.get("TEMPLATE_TOKEN");
            }
        }
        return null;
    }
    
    static {
        EnrollmentTemplateHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
