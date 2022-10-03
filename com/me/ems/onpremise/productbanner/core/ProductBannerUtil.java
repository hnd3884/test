package com.me.ems.onpremise.productbanner.core;

import java.util.concurrent.ConcurrentHashMap;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.function.Function;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.productbanner.api.v1.model.Node;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProductBannerUtil
{
    private static final Logger LOGGER;
    private static ProductBannerUtil instance;
    private final Map<String, List<String>> dataTypeToComparator;
    private static final Map<String, List<Map<String, Object>>> BANNER_MAP_FROM_CRS;
    
    public static ProductBannerUtil getInstance() {
        if (ProductBannerUtil.instance == null) {
            ProductBannerUtil.instance = new ProductBannerUtil();
        }
        return ProductBannerUtil.instance;
    }
    
    private ProductBannerUtil() {
        this.dataTypeToComparator = Collections.unmodifiableMap((Map<? extends String, ? extends List<String>>)new HashMap<String, List<String>>(4) {
            {
                this.put("String", Collections.unmodifiableList((List<? extends String>)Arrays.asList("equals", "notEquals")));
                this.put("Long", Collections.unmodifiableList((List<? extends String>)Arrays.asList("greaterThan", "lessThan", "greaterThanEquals", "lessThanEquals")));
                this.put("List", Collections.unmodifiableList((List<? extends String>)Arrays.asList("in", "notIn")));
            }
        });
        this.updateJSONInMemory();
    }
    
    public Map<String, Object> processBannerJSON(final boolean isAdminUser) {
        if (ProductBannerUtil.BANNER_MAP_FROM_CRS.isEmpty()) {
            this.updateJSONInMemory();
        }
        final List<Map<String, Object>> bannerCollection = ProductBannerUtil.BANNER_MAP_FROM_CRS.getOrDefault("banners", Collections.emptyList());
        return bannerCollection.stream().parallel().filter(this::isBannerEnabled).filter(this::dateCriteria).filter(this::conditionCriteria).filter(bannerData -> this.scopeCriteria(bannerData, isAdminUser2)).findFirst().orElse(Collections.emptyMap());
    }
    
    public final void updateJSONInMemory() {
        try {
            ProductBannerUtil.LOGGER.log(Level.INFO, "Loading JSON content into memory");
            final Map<String, Object> contentMap = this.readProductBannerJSONFromFile();
            if (!contentMap.isEmpty()) {
                this.updateJSONInMemory(contentMap.get("content"));
            }
        }
        catch (final Exception ex) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception occurred while loading content into memory : ", ex);
        }
    }
    
    private void updateJSONInMemory(final Map<String, List<Map<String, Object>>> updatedContentMap) {
        ProductBannerUtil.BANNER_MAP_FROM_CRS.clear();
        ProductBannerUtil.BANNER_MAP_FROM_CRS.putAll(updatedContentMap);
    }
    
    public final String getProductReviewFilePath(final String fileName) {
        return System.getProperty("server.home") + File.separator + "conf" + File.separator + "EMSFramework" + File.separator + fileName;
    }
    
    private Map<String, Object> readProductBannerJSONFromFile() {
        final String destinationPath = this.getProductReviewFilePath("product-banner.json");
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        Map<String, Object> contentMap;
        if (fileAccessAPI.isFileExists(destinationPath)) {
            try (final InputStream fileInputStream = fileAccessAPI.readFile(destinationPath)) {
                final JSONParser parser = new JSONParser();
                final Reader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                contentMap = Collections.singletonMap("content", parser.parse(reader));
                reader.close();
            }
            catch (final Exception ex) {
                ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception occurred while reading product-banner.json", ex);
                contentMap = Collections.emptyMap();
            }
        }
        else {
            contentMap = Collections.emptyMap();
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "File not found : product-banner.json");
        }
        return contentMap;
    }
    
    private boolean isBannerEnabled(final Map<String, Object> bannerData) {
        try {
            final boolean isBannerDisabled = bannerData.getOrDefault("disableBanner", Boolean.FALSE);
            return !isBannerDisabled;
        }
        catch (final Exception ex) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception while checking for date criteria : ", ex);
            return Boolean.TRUE;
        }
    }
    
    private boolean dateCriteria(final Map<String, Object> bannerData) {
        try {
            final String startDate = bannerData.getOrDefault("startDate", "").toString().trim();
            final String endDate = bannerData.getOrDefault("endDate", "").toString().trim();
            final DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            final long currentTimeInMillis = System.currentTimeMillis();
            final long startTimeInMillis = startDate.isEmpty() ? 0L : format.parse(startDate).getTime();
            final long endTimeInMillis = endDate.isEmpty() ? (currentTimeInMillis + 1L) : format.parse(endDate).getTime();
            return currentTimeInMillis > startTimeInMillis && currentTimeInMillis < endTimeInMillis && this.evaluateDefaultCriteria();
        }
        catch (final Exception ex) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception while checking for date criteria : ", ex);
            return Boolean.TRUE;
        }
    }
    
    private boolean conditionCriteria(final Map<String, Object> bannerData) {
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String licenseType = licenseProvider.getLicenseType();
        final String productType = licenseProvider.getProductType();
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final String count = licenseProvider.getNoOfComutersManaged();
        final int computerCount = (count == null || "unlimited".equalsIgnoreCase(count)) ? 1000000 : Integer.parseInt(count);
        final int mobileDeviceCount = SYMClientUtil.getMobileDeviceCount();
        final List<Map<String, String>> conditions = bannerData.getOrDefault("conditions", Collections.emptyList());
        if (conditions.isEmpty()) {
            return "R".equalsIgnoreCase(licenseType);
        }
        final Map<String, String> actualValueMap = new HashMap<String, String>(7);
        actualValueMap.put("licenseType", licenseType);
        actualValueMap.put("productType", productType);
        actualValueMap.put("productCode", productCode);
        actualValueMap.put("computerCount", String.valueOf(computerCount));
        actualValueMap.put("mobileDeviceCount", String.valueOf(mobileDeviceCount));
        final String conditionOperators = bannerData.getOrDefault("conditionOperators", "").toString();
        final List<Boolean> conditionOutputs = conditions.stream().map(condition -> this.processConditions(condition, actualMap)).collect((Collector<? super Object, ?, List<Boolean>>)Collectors.toList());
        final String regex = "\\((\\d+(?:\\)?(?:AND|OR|and|or)\\d+)*)\\)";
        final Pattern pattern = Pattern.compile(regex);
        final String trimmedConditionOperators = conditionOperators.replaceAll("\\s", "");
        final Node<String> treeNode = new Node<String>(trimmedConditionOperators);
        this.addChildToNodes(treeNode, pattern);
        return this.evaluateNodes(treeNode, conditionOutputs);
    }
    
    private boolean evaluateDefaultCriteria() {
        final String installationDateInMillis = SyMUtil.getInstallationProperty("it");
        final String licenseRegisteredDateInMillis = SyMUtil.getSyMParameter("licenseRegisteredDate");
        final long currentTimeInMillis = System.currentTimeMillis();
        final boolean isRegisteredCustomer = "R".equals(LicenseProvider.getInstance().getLicenseType());
        final int defaultDays = FrameworkConfigurations.getFrameworkConfigurations().optInt("product_banner_days_criteria_after_license", 30);
        final boolean isLicensedDateCriteriaSatisfied = (licenseRegisteredDateInMillis != null) ? (currentTimeInMillis > Long.parseLong(licenseRegisteredDateInMillis) + TimeUnit.DAYS.toMillis(defaultDays)) : Boolean.FALSE;
        if (installationDateInMillis != null && !installationDateInMillis.isEmpty()) {
            final boolean isInstallationCriteriaSatisfied = currentTimeInMillis > Long.parseLong(installationDateInMillis) + TimeUnit.DAYS.toMillis(90L);
            if (licenseRegisteredDateInMillis != null) {
                return isRegisteredCustomer && (isLicensedDateCriteriaSatisfied || isInstallationCriteriaSatisfied);
            }
            return isRegisteredCustomer && isInstallationCriteriaSatisfied;
        }
        else {
            if (licenseRegisteredDateInMillis != null) {
                return isRegisteredCustomer && isLicensedDateCriteriaSatisfied;
            }
            try {
                return DBUtil.getRecordCount("DCServerBuildHistory", "BUILD_NUMBER", (Criteria)null) > 1;
            }
            catch (final Exception ex) {
                ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception occurred while fetching DCSERVERBUILDHISTORY table record count", ex);
                return Boolean.FALSE;
            }
        }
    }
    
    private boolean processConditions(final Map<String, String> conditionMap, final Map<String, String> actualMap) {
        final String key = conditionMap.get("key").trim();
        final String comparator = conditionMap.get("comparator").trim();
        final String value = conditionMap.get("value").trim();
        final String valueToBeCompared = actualMap.getOrDefault(key, "");
        if (valueToBeCompared.isEmpty()) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Criteria not supported by Framework : " + key);
            return Boolean.FALSE;
        }
        final String dataType = this.dataTypeToComparator.entrySet().stream().filter(entry -> entry.getValue().contains(s2)).map((Function<? super Object, ? extends String>)Map.Entry::getKey).findFirst().orElse("String");
        final String s = comparator;
        switch (s) {
            case "in": {
                return Arrays.asList(value.split(",")).contains(valueToBeCompared);
            }
            case "notIn": {
                return !Arrays.asList(value.split(",")).contains(valueToBeCompared);
            }
            case "greaterThan": {
                return Long.parseLong(valueToBeCompared) > Long.parseLong(value);
            }
            case "lessThan": {
                return Long.parseLong(valueToBeCompared) < Long.parseLong(value);
            }
            case "greaterThanEquals": {
                return Long.parseLong(valueToBeCompared) >= Long.parseLong(value);
            }
            case "lessThanEquals": {
                return Long.parseLong(valueToBeCompared) <= Long.parseLong(value);
            }
            case "equals": {
                return dataType.equals("Long") ? (Long.parseLong(valueToBeCompared) == Long.parseLong(value)) : valueToBeCompared.equalsIgnoreCase(value);
            }
            case "notEquals": {
                return dataType.equals("Long") ? (Long.parseLong(valueToBeCompared) != Long.parseLong(value)) : (!valueToBeCompared.equalsIgnoreCase(value));
            }
            default: {
                ProductBannerUtil.LOGGER.log(Level.SEVERE, "Comparator not supported by Framework : " + comparator);
                return Boolean.FALSE;
            }
        }
    }
    
    private void addChildToNodes(final Node<String> treeNode, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(treeNode.getId());
        while (matcher.find()) {
            final String matchedString = matcher.group(1);
            final Node<String> childNode = new Node<String>(matchedString);
            treeNode.addChild(childNode);
            this.addChildToNodes(childNode, pattern);
        }
    }
    
    private boolean evaluateNodes(final Node<String> treeNode, final List<Boolean> evaluatedConditions) {
        try {
            final String currentTreeID = treeNode.getId();
            if (treeNode.isLeaf()) {
                treeNode.addProperty(currentTreeID, this.convertToBooleanValue(currentTreeID, evaluatedConditions));
            }
            else {
                final List<Node<String>> childNodes = treeNode.getChildren();
                String resultantID = currentTreeID;
                for (final Node<String> childNode : childNodes) {
                    final String childID = childNode.getId();
                    this.evaluateNodes(childNode, evaluatedConditions);
                    final boolean property = childNode.getProperties().get(childID);
                    resultantID = resultantID.replaceAll("\\(".concat(childID).concat("\\)"), String.valueOf(property));
                }
                treeNode.addProperty(currentTreeID, this.convertToBooleanValue(resultantID, evaluatedConditions));
            }
            return treeNode.getProperties().get(currentTreeID);
        }
        catch (final Exception ex) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception occurred while parsing and evaluating : ", ex);
            return Boolean.FALSE;
        }
    }
    
    public boolean convertToBooleanValue(final String resultantID, final List<Boolean> evaluatedConditions) {
        List<String> orConditionRoleList = null;
        if (resultantID.isEmpty()) {
            return evaluatedConditions.stream().parallel().allMatch(Boolean::booleanValue);
        }
        final Predicate<String> criteria = condition -> (condition.equals(Boolean.TRUE.toString()) || condition.equals(Boolean.FALSE.toString())) ? Boolean.parseBoolean(condition) : list.get(Integer.parseInt(condition) - 1);
        boolean finalResult;
        if (resultantID.contains("AND")) {
            String conditionString = "";
            final List<String> andConditionRoleList = Arrays.stream(resultantID.split("AND")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
            for (final String orRole : andConditionRoleList) {
                if (orRole.contains("OR")) {
                    orConditionRoleList = Arrays.stream(orRole.split("OR")).collect((Collector<? super String, ?, List<String>>)Collectors.toList());
                    conditionString = orRole;
                }
            }
            andConditionRoleList.remove(conditionString);
            final boolean andCondition = andConditionRoleList.stream().allMatch((Predicate<? super Object>)criteria);
            if (orConditionRoleList != null) {
                final boolean orCondition = orConditionRoleList.stream().anyMatch((Predicate<? super Object>)criteria);
                finalResult = (orCondition && andCondition);
            }
            else {
                finalResult = andCondition;
            }
        }
        else {
            orConditionRoleList = Arrays.asList(resultantID.split("OR"));
            finalResult = orConditionRoleList.stream().anyMatch((Predicate<? super Object>)criteria);
        }
        return finalResult;
    }
    
    private boolean scopeCriteria(final Map<String, Object> bannerData, final boolean isAdminUser) {
        final Long scope = bannerData.getOrDefault("scope", 1L);
        return (scope == 2L) ? Boolean.TRUE : isAdminUser;
    }
    
    public boolean isBannerClosed(final String bannerName, final Long loginID) {
        try {
            final Row existingUserStatusRow = this.getDataObjectFromDB(bannerName, loginID).getRow("BannerContentUserStatus");
            if (existingUserStatusRow == null) {
                final Row userStatusRow = new Row("BannerContentUserStatus");
                userStatusRow.set("BANNER_NAME", (Object)bannerName);
                userStatusRow.set("LOGIN_ID", (Object)loginID);
                userStatusRow.set("BANNER_STATUS", (Object)"OPEN");
                userStatusRow.set("OPENED_AT", (Object)System.currentTimeMillis());
                final DataObject writableDO = (DataObject)new WritableDataObject();
                writableDO.addRow(userStatusRow);
                SyMUtil.getPersistence().add(writableDO);
            }
            else {
                final String s;
                final String currentStatus = s = (String)existingUserStatusRow.get("BANNER_STATUS");
                switch (s) {
                    case "DO_NOT_SHOW":
                    case "REVIEWED": {
                        return Boolean.TRUE;
                    }
                    case "OPEN": {
                        return Boolean.FALSE;
                    }
                    case "REMIND_LATER": {
                        final Long remindAt = (Long)existingUserStatusRow.get("REMIND_AT");
                        if (System.currentTimeMillis() > remindAt) {
                            final Row clonedRow = (Row)existingUserStatusRow.clone();
                            clonedRow.set("BANNER_STATUS", (Object)"OPEN");
                            clonedRow.set("OPENED_AT", (Object)System.currentTimeMillis());
                            final DataObject writableDO2 = (DataObject)new WritableDataObject();
                            writableDO2.updateBlindly(clonedRow);
                            SyMUtil.getPersistence().update(writableDO2);
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                }
            }
        }
        catch (final Exception ex) {
            ProductBannerUtil.LOGGER.log(Level.SEVERE, "Exception while checking for closed status : ", ex);
        }
        return Boolean.FALSE;
    }
    
    public boolean showOptions(final String bannerName, final Long loginID) throws Exception {
        final Long remindAt = (Long)this.getValueOfColumn(bannerName, loginID, "REMIND_AT");
        return (remindAt == 0L) ? Boolean.FALSE : (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) >= TimeUnit.MILLISECONDS.toDays(remindAt));
    }
    
    private DataObject getDataObjectFromDB(final String bannerName, final Long loginID) throws Exception {
        final Column col = Column.getColumn("BannerContentUserStatus", "BANNER_NAME");
        final Criteria criteria = new Criteria(col, (Object)bannerName, 0);
        final Criteria userCriteria = new Criteria(Column.getColumn("BannerContentUserStatus", "LOGIN_ID"), (Object)loginID, 0);
        return SyMUtil.getCachedPersistence().get("BannerContentUserStatus", criteria.and(userCriteria));
    }
    
    private Object getValueOfColumn(final String bannerName, final Long loginID, final String columnName) throws Exception {
        return this.getDataObjectFromDB(bannerName, loginID).getFirstValue("BannerContentUserStatus", columnName);
    }
    
    public void doNotShowAgainForUser(final String bannerName, final Long loginID, final Long closedAt) throws Exception {
        final Row clonedExistingUserStatusRow = (Row)this.getDataObjectFromDB(bannerName, loginID).getRow("BannerContentUserStatus").clone();
        clonedExistingUserStatusRow.set("BANNER_STATUS", (Object)"DO_NOT_SHOW");
        clonedExistingUserStatusRow.set("CLOSED_AT", (Object)closedAt);
        clonedExistingUserStatusRow.set("REMIND_AT", (Object)0);
        final DataObject writableDO = (DataObject)new WritableDataObject();
        writableDO.updateBlindly(clonedExistingUserStatusRow);
        SyMUtil.getPersistence().update(writableDO);
    }
    
    public void remindMeLater(final String bannerName, final Long loginID, final Long remindAt) throws Exception {
        final Row clonedExistingUserStatusRow = (Row)this.getDataObjectFromDB(bannerName, loginID).getRow("BannerContentUserStatus").clone();
        clonedExistingUserStatusRow.set("BANNER_STATUS", (Object)"REMIND_LATER");
        clonedExistingUserStatusRow.set("REMIND_AT", (Object)remindAt);
        final DataObject writableDO = (DataObject)new WritableDataObject();
        writableDO.updateBlindly(clonedExistingUserStatusRow);
        SyMUtil.getPersistence().update(writableDO);
    }
    
    public void reviewed(final String bannerName, final Long loginID, final Long closedAt) throws Exception {
        final Row clonedExistingUserStatusRow = (Row)this.getDataObjectFromDB(bannerName, loginID).getRow("BannerContentUserStatus").clone();
        clonedExistingUserStatusRow.set("BANNER_STATUS", (Object)"REVIEWED");
        clonedExistingUserStatusRow.set("CLOSED_AT", (Object)closedAt);
        clonedExistingUserStatusRow.set("REMIND_AT", (Object)0);
        final DataObject writableDO = (DataObject)new WritableDataObject();
        writableDO.updateBlindly(clonedExistingUserStatusRow);
        SyMUtil.getPersistence().update(writableDO);
    }
    
    static {
        LOGGER = Logger.getLogger(ProductBannerUtil.class.getName());
        BANNER_MAP_FROM_CRS = new ConcurrentHashMap<String, List<Map<String, Object>>>();
    }
}
