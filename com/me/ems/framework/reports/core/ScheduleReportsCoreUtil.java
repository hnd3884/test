package com.me.ems.framework.reports.core;

import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.me.devicemanagement.framework.server.util.EMSServiceUtil;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import java.util.HashSet;
import java.util.Set;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.lang.reflect.Method;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.license.LicenseDiffChecker;
import com.me.devicemanagement.framework.server.license.License;
import java.util.ArrayList;
import java.util.LinkedList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.util.EMSServerUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import org.json.simple.JSONArray;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import java.io.File;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.framework.common.api.v1.model.Node;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.List;
import org.json.simple.parser.ContainerFactory;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class ScheduleReportsCoreUtil
{
    private static Logger logger;
    private static final String INVENTORY = "Inventory";
    private static final String USB = "USB";
    private static final String SS_PATCH = "SS_Patch";
    private static final String SS_INVENTORY = "SS_Inventory";
    private static LinkedHashMap metaObject;
    private static ContainerFactory containerFactory;
    
    public static Node<String> getAllPredefinedReports(final List<String> applicableModuleList, final User user) throws Exception {
        final Node<String> predefinedReports = new Node<String>();
        try {
            final String installDir = SyMUtil.getInstallationDir();
            final JSONParser jsonParser = new JSONParser();
            predefinedReports.setId("predefinedReports");
            predefinedReports.addProperty("reportType", String.valueOf(1));
            if (ScheduleReportsCoreUtil.metaObject == null) {
                final String scheduleReportsConfFilePath = ProductUrlLoader.getInstance().getValue("schedule_reports_meta");
                if (scheduleReportsConfFilePath == null || scheduleReportsConfFilePath.isEmpty()) {
                    throw new APIException("GENERIC0011", "ems.rest.api_sch_rep_file_not_found", new String[] { "schedule_reports_meta" });
                }
                ScheduleReportsCoreUtil.metaObject = parseJSON(installDir + File.separator + scheduleReportsConfFilePath);
            }
            for (final String key : ScheduleReportsCoreUtil.metaObject.keySet()) {
                final String fileName = ScheduleReportsCoreUtil.metaObject.get(key);
                if (fileName == null || fileName.isEmpty()) {
                    ScheduleReportsCoreUtil.logger.log(Level.WARNING, "No Meta Json path found for the {0} module", fileName);
                }
                else {
                    final String metaFilePathStr = installDir + File.separator + fileName;
                    final File metaJsonFile = new File(metaFilePathStr);
                    if (!metaJsonFile.exists()) {
                        ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Meta Json File Not found for the {0} module in {1} ", new Object[] { fileName, metaFilePathStr });
                    }
                    else {
                        final JSONObject jsonObject = (JSONObject)jsonParser.parse((Reader)new FileReader(installDir + File.separator + fileName));
                        final JSONArray jsonArray = (JSONArray)jsonParser.parse(jsonObject.get((Object)"scheduleReportsFiles").toString());
                        for (final Object eachReport : jsonArray) {
                            final JSONObject jsonReport = (JSONObject)eachReport;
                            if (validateJsonData(jsonReport, user)) {
                                final Node<String> report = new Node<String>();
                                final String subFilePath = (String)jsonReport.get((Object)"filePath");
                                final JSONObject reportsDetails = (JSONObject)jsonParser.parse((Reader)new FileReader(installDir + File.separator + subFilePath));
                                if (!validateJsonData(reportsDetails, user)) {
                                    continue;
                                }
                                report.setId((String)reportsDetails.get((Object)"id"));
                                final String displayName = (String)reportsDetails.get((Object)"displayName");
                                report.setLabel(I18N.getMsg(displayName, new Object[0]));
                                final JSONArray properties = (JSONArray)reportsDetails.get((Object)"properties");
                                if (properties != null) {
                                    final Map propertyMap = convertJsontoMap(properties);
                                    report.setProperties(propertyMap);
                                }
                                JSONArray children = null;
                                if (reportsDetails.containsKey((Object)"children")) {
                                    children = (JSONArray)reportsDetails.get((Object)"children");
                                }
                                JSONArray subComponents = null;
                                if (reportsDetails.containsKey((Object)"subFileNames")) {
                                    final JSONArray subFiles = (JSONArray)reportsDetails.get((Object)"subFileNames");
                                    subComponents = getScheduleReportsComponents(subFiles);
                                }
                                if (children != null) {
                                    addNode(children, report, user);
                                    predefinedReports.addChild(report);
                                }
                                else if (subComponents != null) {
                                    addNode(subComponents, report, user);
                                    predefinedReports.addChild(report);
                                }
                                else {
                                    predefinedReports.addChild(report);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception while fetching available custom reports", e);
            throw e;
        }
        return predefinedReports;
    }
    
    private static Node<String> addNode(final JSONArray childReports, final Node<String> parent, final User user) throws Exception {
        for (final Object jsonInArray : childReports) {
            final Node<String> oneReport = new Node<String>();
            final JSONObject reportsDetails = (JSONObject)jsonInArray;
            if (validateJsonData(reportsDetails, user)) {
                oneReport.setId((String)reportsDetails.get((Object)"id"));
                final String displayName = (String)reportsDetails.get((Object)"displayName");
                oneReport.setLabel(I18N.getMsg(displayName, new Object[0]));
                final JSONArray properties = (JSONArray)reportsDetails.get((Object)"properties");
                if (properties != null) {
                    final Map propertyMap = convertJsontoMap(properties);
                    oneReport.setProperties(propertyMap);
                }
                JSONArray children = null;
                if (reportsDetails.containsKey((Object)"children")) {
                    children = (JSONArray)reportsDetails.get((Object)"children");
                }
                JSONArray subComponents = null;
                if (reportsDetails.containsKey((Object)"subFileNames")) {
                    final JSONArray subFiles = (JSONArray)reportsDetails.get((Object)"subFileNames");
                    subComponents = getScheduleReportsComponents(subFiles);
                }
                if (children != null) {
                    final Node childNode = addNode(children, oneReport, user);
                    parent.addChild(childNode);
                }
                else if (subComponents != null) {
                    final Node childNode = addNode(subComponents, oneReport, user);
                    parent.addChild(childNode);
                }
                else {
                    parent.addChild(oneReport);
                }
            }
        }
        return parent;
    }
    
    private static boolean validateJsonData(final JSONObject sectionDetails, final User user) {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final String productType = LicenseProvider.getInstance().getProductType();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
        return sectionDetails != null && !sectionDetails.isEmpty() && isSasCheck((String)sectionDetails.get((Object)"forSAS")) && isMspCheck((String)sectionDetails.get((Object)"forMSP")) && roleCheck((String)sectionDetails.get((Object)"roles"), (String)sectionDetails.get((Object)"multiRoleCheck"), (String)sectionDetails.get((Object)"rolesNegation"), user) && validateData((String)sectionDetails.get((Object)"productCode"), (String)sectionDetails.get((Object)"isProductCodeNegation"), productCode) && validateData((String)sectionDetails.get((Object)"productType"), (String)sectionDetails.get((Object)"isProductTypeNegation"), productType) && validateData((String)sectionDetails.get((Object)"licenseType"), (String)sectionDetails.get((Object)"isLicenseTypeNegation"), licenseType) && validateData((String)sectionDetails.get((Object)"licenseVersion"), (String)sectionDetails.get((Object)"isLicenseVersionNegation"), licenseVersion) && EMSServerUtil.isMatchingServerType((String)sectionDetails.get((Object)"emsServerType")) && customCheck((String)sectionDetails.get((Object)"customCheckClass"), (String)sectionDetails.get((Object)"customCheckMethod"));
    }
    
    private static JSONArray getScheduleReportsComponents(final JSONArray fileArray) {
        final JSONArray subReportsArray = new JSONArray();
        try {
            final List<String> reportSubFiles = getFilesList(fileArray);
            for (final String file : reportSubFiles) {
                final JSONObject reportJSON = FileAccessUtil.secureReadJSON(file);
                subReportsArray.add((Object)reportJSON);
            }
        }
        catch (final Exception e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in getScheduleReportsComponents", e);
        }
        return subReportsArray;
    }
    
    private static List<String> getFilesList(final JSONArray fileArray) {
        final List<String> reportSubFiles = new LinkedList<String>();
        try {
            final String outFileName = SyMUtil.getInstallationDir() + File.separator;
            for (final Object file : fileArray) {
                reportSubFiles.add(outFileName + file.toString());
            }
        }
        catch (final Exception e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in getFilesList", e);
        }
        return reportSubFiles;
    }
    
    public static List<String> getApplicableReportModules() {
        List<String> applicableReportModulesForCurrentLicense = new ArrayList<String>();
        try {
            final License license = License.getNewLicenseObject();
            applicableReportModulesForCurrentLicense = LicenseDiffChecker.getEnabledModules(license);
            if (CustomerInfoUtil.isDC()) {
                applicableReportModulesForCurrentLicense.add("Inventory");
                applicableReportModulesForCurrentLicense.add("USB");
            }
            if (SyMUtil.isSummaryServer()) {
                if (applicableReportModulesForCurrentLicense.contains("Patch")) {
                    applicableReportModulesForCurrentLicense.remove("Patch");
                }
                applicableReportModulesForCurrentLicense.add("SS_Patch");
                applicableReportModulesForCurrentLicense.add("SS_Inventory");
            }
        }
        catch (final Exception e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in getting applicableReportModulesForCurrentLicense ", e);
        }
        return applicableReportModulesForCurrentLicense;
    }
    
    private static boolean customCheck(final String customCheckClass, final String customCheckMethod) {
        if (customCheckClass != null && !customCheckClass.isEmpty() && customCheckMethod != null && !customCheckMethod.isEmpty()) {
            try {
                final Object classObject = Class.forName(customCheckClass).newInstance();
                if (classObject != null) {
                    final Method method = classObject.getClass().getDeclaredMethod(customCheckMethod, (Class<?>[])new Class[0]);
                    return (boolean)method.invoke(classObject, new Object[0]);
                }
            }
            catch (final Exception ex) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean validateData(final String key, final String keyNegate, final String valueComparator) {
        boolean isValid = false;
        if (key != null && !key.isEmpty()) {
            final String[] split;
            final String[] dataArray = split = key.split(",");
            for (final String data : split) {
                if (valueComparator != null && valueComparator.equalsIgnoreCase(data)) {
                    isValid = true;
                    break;
                }
            }
            if (keyNegate != null && keyNegate.equalsIgnoreCase("true")) {
                isValid = !isValid;
            }
        }
        else {
            isValid = true;
        }
        return isValid;
    }
    
    private static boolean negatedRoleCheck(final String negatedRoles, final User user) {
        if (negatedRoles != null && !negatedRoles.isEmpty()) {
            final String[] split;
            final String[] rolesArray = split = negatedRoles.split(",");
            for (final String role : split) {
                if (user.isUserInRole(role)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean roleCheck(final String roles, final String multiRoleCheck, final String negateRoles, final User user) {
        boolean isValid = false;
        final boolean negateRolecheck = negatedRoleCheck(negateRoles, user);
        if (negateRolecheck && roles != null && !roles.isEmpty()) {
            final String[] split;
            final String[] rolesArray = split = roles.split(",");
            for (final String role : split) {
                if (multiRoleCheck != null && multiRoleCheck.equalsIgnoreCase("true")) {
                    if (!user.isUserInRole(role)) {
                        isValid = false;
                        break;
                    }
                    isValid = true;
                }
                else {
                    if (user.isUserInRole(role)) {
                        isValid = true;
                        break;
                    }
                    isValid = false;
                }
            }
        }
        else {
            isValid = negateRolecheck;
        }
        return isValid;
    }
    
    private static boolean isSasCheck(final String forSAS) {
        if (forSAS == null || forSAS.isEmpty()) {
            return true;
        }
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            return forSAS.equalsIgnoreCase("true");
        }
        return !forSAS.equalsIgnoreCase("true");
    }
    
    private static boolean isMspCheck(final String forMSP) {
        if (forMSP == null || forMSP.isEmpty()) {
            return true;
        }
        if (CustomerInfoUtil.getInstance().isMSP()) {
            return forMSP.equalsIgnoreCase("true");
        }
        return !forMSP.equalsIgnoreCase("true");
    }
    
    private static Map convertJsontoMap(final JSONArray properties) {
        final Map propertiesMap = new HashMap();
        for (final JSONObject j : properties) {
            for (final Object key : j.keySet()) {
                propertiesMap.put(key, j.get(key));
            }
        }
        return propertiesMap;
    }
    
    public static void addScheduleReportTask(final DataObject scheduleReportsDO, final Map scheduleReports, final Long taskID, final Long customerID) {
        final String reportDescription = scheduleReports.get("reportDescription");
        final String reportFormats = scheduleReports.get("reportFormats");
        final String deliveryFormat = scheduleReports.get("deliveryFormat");
        final Integer attachmentLimits = scheduleReports.getOrDefault("attachmentLimits", 5);
        final String notificationEmails = scheduleReports.get("notificationEmails");
        final String emailSubject = scheduleReports.get("emailSubject");
        final String emailContent = scheduleReports.get("emailContent");
        final String redactType = scheduleReports.get("redactType").toString();
        final Boolean useNATLink = scheduleReports.getOrDefault("useNATLink", false);
        final Boolean isEmptyReportNeeded = scheduleReports.getOrDefault("isEmptyReportNeeded", true);
        final Row scheduleRepTask = new Row("ScheduleRepTask");
        scheduleRepTask.set("TASK_ID", (Object)taskID);
        scheduleRepTask.set("REPORT_FORMAT", (Object)Integer.valueOf(reportFormats));
        scheduleRepTask.set("DESCRIPTION", (Object)((reportDescription == null) ? "" : reportDescription));
        scheduleRepTask.set("DELIVERY_FORMAT", (Object)Integer.valueOf(deliveryFormat));
        scheduleRepTask.set("ATTACHMENT_LIMIT", (Object)attachmentLimits);
        scheduleRepTask.set("EMAIL_ADDRESS", (Object)notificationEmails);
        scheduleRepTask.set("SUBJECT", (Object)emailSubject);
        scheduleRepTask.set("CONTENT", (Object)((emailContent == null) ? "" : emailContent));
        scheduleRepTask.set("CUSTOMER_ID", (Object)customerID);
        scheduleRepTask.set("REDACT_TYPE", (Object)Integer.valueOf(redactType));
        scheduleRepTask.set("USE_NAT_LINK", (Object)useNATLink);
        scheduleRepTask.set("IS_EMPTY_REPORT_NEEDED", (Object)isEmptyReportNeeded);
        try {
            scheduleReportsDO.addRow(scheduleRepTask);
        }
        catch (final DataAccessException ex) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in ScheduleReportsCoreUtil.addScheduleReportTask(),taskID: " + taskID, (Throwable)ex);
        }
    }
    
    public static void setFilterToSR(final Long appliedFilter, final DataObject scheduleReportsDO, final Map oneReport) {
        try {
            final String viewID = oneReport.get("viewID");
            final Criteria repIDCrit = new Criteria(Column.getColumn("ScheduleRepToReportRel", "REPORT_ID"), (Object)viewID, 0);
            final Row scheduleRepRelRow = scheduleReportsDO.getRow("ScheduleRepToReportRel", repIDCrit);
            final Row filterRelRow = new Row("SRToFilterRel");
            filterRelRow.set("FILTER_ID", (Object)appliedFilter);
            filterRelRow.set("SCHEDULE_REP_ID", scheduleRepRelRow.get("SCHEDULE_REP_ID"));
            scheduleReportsDO.addRow(filterRelRow);
            final SelectQuery filterToCriteriaQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCViewFilterCriteria"));
            filterToCriteriaQuery.addSelectColumn(Column.getColumn("DCViewFilterCriteria", "CRITERIA_COLUMN_ID"));
            filterToCriteriaQuery.setCriteria(new Criteria(Column.getColumn("DCViewFilterCriteria", "FILTER_ID"), (Object)appliedFilter, 0));
            final DataObject filterToCrDO = SyMUtil.getPersistence().get(filterToCriteriaQuery);
            if (filterToCrDO != null && !filterToCrDO.isEmpty()) {
                final Iterator itr = filterToCrDO.getRows("DCViewFilterCriteria");
                while (itr.hasNext()) {
                    final Long criColId = Long.parseLong(itr.next().get("CRITERIA_COLUMN_ID").toString());
                    final Row srToCriteriaRow = new Row("SRToCriteriaRel");
                    srToCriteriaRow.set("SCHEDULE_REP_ID", scheduleRepRelRow.get("SCHEDULE_REP_ID"));
                    srToCriteriaRow.set("CRITERIA_COLUMN_ID", (Object)criColId);
                    scheduleReportsDO.addRow(srToCriteriaRow);
                }
            }
        }
        catch (final DataAccessException ex) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in Scheduler=ReportsCoreUtil.setFilterToSR()", (Throwable)ex);
        }
    }
    
    public static void setRepToTaskRel(final Map oneReport, final DataObject scheduleReportsDO, final Long taskID) {
        final String viewID = oneReport.get("viewID");
        final String reportType = oneReport.get("reportType");
        final Row scheduleRepRelRow = new Row("ScheduleRepToReportRel");
        scheduleRepRelRow.set("REPORT_ID", (Object)Long.valueOf(viewID));
        scheduleRepRelRow.set("TASK_ID", (Object)taskID);
        scheduleRepRelRow.set("REPORT_TYPE", (Object)Integer.valueOf(reportType));
        try {
            scheduleReportsDO.addRow(scheduleRepRelRow);
        }
        catch (final DataAccessException ex) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in ScheduleReportsCoreUtil.setRepToTaskRel(),taskID: " + taskID, (Throwable)ex);
        }
    }
    
    public static DataObject getScheduleRepDO(final Long taskID, final Long userID, final Long customerID, final boolean isAdmin) throws DataAccessException {
        final SelectQuery srQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
        final Criteria taskCrit = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
        final Criteria userCrit = new Criteria(Column.getColumn("TaskToUserRel", "USER_ID"), (Object)userID, 0);
        final Criteria customerCrit = new Criteria(Column.getColumn("ScheduleRepTask", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria criteria = isAdmin ? taskCrit.and(customerCrit) : taskCrit.and(userCrit).and(customerCrit);
        final Join taskToUserJoin = new Join("TaskDetails", "TaskToUserRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
        final Join srTaskJoin = new Join("TaskDetails", "ScheduleRepTask", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
        final Join srRepJoin = new Join("TaskDetails", "ScheduleRepToReportRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
        final Join srCriteriaJoin = new Join("ScheduleRepToReportRel", "SRToCriteriaRel", new String[] { "SCHEDULE_REP_ID" }, new String[] { "SCHEDULE_REP_ID" }, 1);
        final Join srFilterJoin = new Join("ScheduleRepToReportRel", "SRToFilterRel", new String[] { "SCHEDULE_REP_ID" }, new String[] { "SCHEDULE_REP_ID" }, 1);
        final Join critColumnJoin = new Join("SRToCriteriaRel", "CriteriaColumnDetails", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 1);
        srQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        srQuery.addJoin(taskToUserJoin);
        srQuery.addJoin(srTaskJoin);
        srQuery.addJoin(srRepJoin);
        srQuery.addJoin(srCriteriaJoin);
        srQuery.addJoin(srFilterJoin);
        srQuery.addJoin(critColumnJoin);
        srQuery.setCriteria(criteria);
        DataObject scheduleRepDO = null;
        try {
            scheduleRepDO = SyMUtil.getPersistence().get(srQuery);
        }
        catch (final DataAccessException e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in ScheduleReportsCoreUtil.getScheduleRepDO(),taskID: " + taskID + "userID: " + userID + "customerID: " + customerID, (Throwable)e);
            throw e;
        }
        return scheduleRepDO;
    }
    
    public static Map<Integer, Set<Long>> getEligibleReports(final List<String> allRoles) throws DataAccessException {
        final Map eligibleRepCategories = getEligibleCategories(allRoles);
        final Map<Integer, Set<Long>> eligibleReports = new HashMap<Integer, Set<Long>>();
        try {
            if (eligibleRepCategories.containsKey(1)) {
                final DataObject repCategoryDO = getReportsCategoryDO(eligibleRepCategories.get(1));
                if (repCategoryDO == null) {
                    throw new DataAccessException("Exception while getting Reports Category DO");
                }
                final Iterator viewsItr = repCategoryDO.getRows("ViewParams");
                final Set<Long> eligibleSet = new HashSet<Long>();
                while (viewsItr.hasNext()) {
                    final Row viewRow = viewsItr.next();
                    eligibleSet.add((long)(int)viewRow.get("VIEW_ID"));
                }
                eligibleReports.put(1, eligibleSet);
            }
            if (eligibleRepCategories.containsKey(2)) {
                eligibleReports.put(2, null);
            }
            if (eligibleRepCategories.containsKey(3)) {
                eligibleReports.put(3, null);
            }
        }
        catch (final DataAccessException ex) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in ScheduleReportsCoreUtil.getEligibleReports(),roles: " + allRoles.toString(), (Throwable)ex);
            throw ex;
        }
        return eligibleReports;
    }
    
    public static DataObject getReportsCategoryDO(final Set<Integer> eligibleCategory) {
        final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
        Criteria criteria = null;
        final List excludedList = scheduleReportUtil.getReportList();
        final Integer[] excludedReports = excludedList.toArray(new Integer[excludedList.size()]);
        if (excludedReports.length > 0) {
            final Criteria excludeReportsCriteria = criteria = new Criteria(Column.getColumn("ViewParams", "VIEW_ID"), (Object)excludedReports, 9);
        }
        if (!eligibleCategory.isEmpty()) {
            final Integer[] eligibleCategoryArray = eligibleCategory.toArray(new Integer[eligibleCategory.size()]);
            final Criteria categoryCriteria = new Criteria(Column.getColumn("ReportCategory", "CATEGORY_ID"), (Object)eligibleCategoryArray, 8);
            criteria = ((criteria == null) ? categoryCriteria : criteria.and(categoryCriteria));
        }
        final SelectQuery viewQuery = SYMReportUtil.getViewQuery(criteria);
        DataObject categoryDO = null;
        try {
            categoryDO = SyMUtil.getPersistence().get(viewQuery);
        }
        catch (final DataAccessException e) {
            ScheduleReportsCoreUtil.logger.log(Level.WARNING, "Exception in ScheduleReportsCoreUtil.getReportsCategoryDO(),eligibleCategory: " + eligibleCategory.toString(), (Throwable)e);
        }
        return categoryDO;
    }
    
    public static Map getEligibleCategories(final List<String> allRoles) {
        final Set<Integer> eligibleSet = new HashSet<Integer>();
        final Map eligibleCategories = new HashMap();
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        CustomerInfoUtil.getInstance();
        final boolean isSAS = CustomerInfoUtil.isSAS();
        final boolean show_only_patch = Boolean.valueOf(SyMUtil.getSyMParameter("show_only_patch"));
        final boolean isVMP = EMSServiceUtil.isVulnerabilityEnabled() || CustomerInfoUtil.isVMPProduct();
        final boolean isProfessionalEdition = new LicenseProvider().isProfessionalEdition();
        final boolean isProfVMP = isProfessionalEdition && CustomerInfoUtil.isVMPProduct();
        if (allRoles.contains("Report_Read")) {
            if (!show_only_patch) {
                eligibleSet.add(5370);
                eligibleSet.add(200);
                if (allRoles.contains("SecUSB_Epr")) {
                    eligibleSet.add(1500);
                }
                if (allRoles.contains("MDM_Report_Read") && !isSAS) {
                    eligibleSet.add(40000);
                }
            }
            if (allRoles.contains("PatchMgmt_Read")) {
                if (!isProfVMP) {
                    eligibleSet.add(500);
                }
                if (isVMP) {
                    eligibleSet.add(750);
                }
            }
            if (!allRoles.contains("Patch_Edition_Role")) {
                eligibleSet.add(179);
                if (!isMSP) {
                    eligibleSet.add(2);
                    if (allRoles.contains("All_Managed_Computer")) {
                        eligibleSet.add(1);
                        eligibleSet.add(3);
                        eligibleSet.add(10);
                        eligibleSet.add(78);
                        eligibleSet.add(6);
                    }
                }
            }
        }
        eligibleCategories.put(1, eligibleSet);
        if ((allRoles.contains("Report_Admin") || allRoles.contains("MDM_Report_Write") || allRoles.contains("PatchMgmt_Write")) && !isSAS && (!isMSP || allRoles.contains("Common_Write"))) {
            eligibleCategories.put(3, "queryReports");
        }
        if (allRoles.contains("Report_Write") && !show_only_patch) {
            eligibleCategories.put(2, "customReports");
        }
        return eligibleCategories;
    }
    
    public static boolean setRetentionPeriod(final Long userID, final Long customerID, final Map retentionPeriod) {
        boolean status = false;
        final Long taskID = null;
        final String enableBackup = "false";
        final String retentionPeriodString = retentionPeriod.get("retentionPeriod").toString();
        final int statusCode = ScheduleReportUtil.getInstance().saveScheduleHistoryWithTaskId(enableBackup, retentionPeriodString, taskID, userID, customerID);
        if (statusCode == FrameworkStatusCodes.SUCCESS_RESPONSE_CODE) {
            status = true;
        }
        else {
            ScheduleReportsCoreUtil.logger.log(Level.SEVERE, "Exception while setting retention period: " + retentionPeriodString + ",userID: " + userID + "customerID: " + customerID);
        }
        return status;
    }
    
    public static LinkedHashMap parseJSON(final String fileName) {
        LinkedHashMap map = new LinkedHashMap();
        try {
            final JSONParser parser = new JSONParser();
            map = (LinkedHashMap)parser.parse((Reader)new FileReader(fileName), ScheduleReportsCoreUtil.containerFactory);
        }
        catch (final Exception e) {
            ScheduleReportsCoreUtil.logger.log(Level.SEVERE, "JSON parse exception", e);
        }
        return map;
    }
    
    static {
        ScheduleReportsCoreUtil.logger = Logger.getLogger("ScheduleReportLogger");
        ScheduleReportsCoreUtil.metaObject = null;
        ScheduleReportsCoreUtil.containerFactory = (ContainerFactory)new ContainerFactory() {
            public Map createObjectContainer() {
                return new LinkedHashMap();
            }
            
            public List creatArrayContainer() {
                return new LinkedList();
            }
        };
    }
}
