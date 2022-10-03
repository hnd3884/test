package com.me.mdm.server.apps;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import com.me.mdm.api.error.APIHTTPException;
import com.zoho.security.api.wrapper.ZipInputStreamWrapper;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.regex.Pattern;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.files.FileFacade;
import java.util.Collection;
import com.adventnet.ds.query.DeleteQuery;
import java.util.List;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.ArrayList;
import java.io.InputStream;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.Calendar;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.windows.WindowsAppDependecyHandler;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppDependencyHandler
{
    private static AppDependencyInterface appDependencyInterface;
    private int platformType;
    public Logger logger;
    
    public AppDependencyHandler(final int platformType) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.platformType = platformType;
    }
    
    public JSONObject validateDependencyUpload(String fileSourceDestFileName, final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        if (fileSourceDestFileName != null && !fileSourceDestFileName.equals("")) {
            final AppDependencyInterface handler = this.getAppProcessor();
            final HashMap props = handler.extractAndValidateUpload(fileSourceDestFileName);
            if (props != null && props.get("success")) {
                fileSourceDestFileName = this.moveDependencyFile(fileSourceDestFileName, customerID);
                props.put("fileLocation", fileSourceDestFileName);
                final Long dependencyID = this.addOrUpdateDependency(props, customerID);
                response.put("name", props.get("name"));
                response.put("architecture", props.get("architecture"));
                response.put("version", props.get("version"));
                response.put("dependencyID", (Object)dependencyID);
                response.put("success", true);
            }
            else {
                response.put("success", false);
                response.put("message", props.get("message"));
            }
        }
        else {
            response.put("success", false);
            response.put("message", (Object)"file Copy Failed");
        }
        return response;
    }
    
    private AppDependencyInterface getAppProcessor() {
        if (AppDependencyHandler.appDependencyInterface == null) {
            AppDependencyHandler.appDependencyInterface = new WindowsAppDependecyHandler();
        }
        return AppDependencyHandler.appDependencyInterface;
    }
    
    private Long addOrUpdateDependency(final HashMap hashMap, final Long customerID) throws DataAccessException {
        final String identifier = hashMap.get("identifier");
        final String arch = hashMap.get("architecture");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDependency"));
        selectQuery.addSelectColumn(Column.getColumn("AppDependency", "*"));
        final Criteria identifierCriteria = new Criteria(Column.getColumn("AppDependency", "BUNDLE_IDENTIFIER"), (Object)identifier, 0);
        final Criteria archCriteria = new Criteria(Column.getColumn("AppDependency", "SUPPORTED_ARCH"), (Object)arch, 0);
        selectQuery.setCriteria(identifierCriteria.and(archCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Row row = null;
        if (dataObject.isEmpty()) {
            row = new Row("AppDependency");
            row.set("BUNDLE_IDENTIFIER", (Object)identifier);
            row.set("DEPENDENCY_NAME", hashMap.get("name"));
            row.set("VERSION", hashMap.get("version"));
            row.set("FILE_LOC", hashMap.get("fileLocation"));
            row.set("SUPPORTED_ARCH", hashMap.get("architecture"));
            row.set("PLATFORM_TYPE", (Object)this.platformType);
            row.set("CUSTOMER_ID", (Object)customerID);
            dataObject.addRow(row);
        }
        else {
            row = dataObject.getFirstRow("AppDependency");
            final String version = (String)row.get("VERSION");
            if (new IOSAppVersionChecker().isVersionNumberGreater(version, hashMap.get("version"))) {
                row.set("BUNDLE_IDENTIFIER", (Object)identifier);
                row.set("DEPENDENCY_NAME", hashMap.get("name"));
                row.set("VERSION", hashMap.get("version"));
                row.set("FILE_LOC", hashMap.get("fileLocation"));
                row.set("SUPPORTED_ARCH", hashMap.get("architecture"));
                row.set("PLATFORM_TYPE", (Object)this.platformType);
                row.set("CUSTOMER_ID", (Object)customerID);
                dataObject.updateRow(row);
            }
        }
        MDMUtil.getPersistence().update(dataObject);
        return (Long)row.get("DEPENDENCY_ID");
    }
    
    public JSONArray ListDependencies(final String searchString, final Long dependencyID) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDependency"));
        selectQuery.addSelectColumn(Column.getColumn("AppDependency", "*"));
        Criteria finalCriteria = new Criteria(Column.getColumn("AppDependency", "PLATFORM_TYPE"), (Object)this.platformType, 0);
        if (searchString != null && !searchString.equals("")) {
            final Criteria identifierCriteria = new Criteria(Column.getColumn("AppDependency", "BUNDLE_IDENTIFIER"), (Object)searchString, 2);
            final Criteria nameCriteria = new Criteria(Column.getColumn("AppDependency", "DEPENDENCY_NAME"), (Object)searchString, 2);
            finalCriteria = finalCriteria.and(identifierCriteria.or(nameCriteria));
        }
        if (dependencyID != null) {
            finalCriteria.and(new Criteria(Column.getColumn("AppDependency", "DEPENDENCY_ID"), (Object)dependencyID, 0));
        }
        selectQuery.setCriteria(finalCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("AppDependency");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            jsonArray.put((Object)this.getDependencyJSON(row));
        }
        return jsonArray;
    }
    
    private JSONObject getDependencyJSON(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("identifier", row.get("BUNDLE_IDENTIFIER"));
        jsonObject.put("name", row.get("DEPENDENCY_NAME"));
        jsonObject.put("version", row.get("VERSION"));
        jsonObject.put("fileLocation", row.get("FILE_LOC"));
        jsonObject.put("architecture", (Object)((String)row.get("SUPPORTED_ARCH")).toLowerCase());
        jsonObject.put("dependencyID", row.get("DEPENDENCY_ID"));
        return jsonObject;
    }
    
    public String getDependencyTempSourceFolderPath(final Long customerId) throws Exception {
        final Calendar cal = Calendar.getInstance();
        final String sourceFolder = "DepSource_" + cal.getTimeInMillis();
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + "dependencies" + File.separator + customerId + File.separator + "dependencyUpload" + File.separator + sourceFolder;
        return appRepositoryFolder;
    }
    
    private String moveDependencyFile(final String tempFilePath, final Long customerId) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + "dependencies" + File.separator + customerId + File.separator + this.platformType;
        final String dbFilePath = File.separator + "MDM" + File.separator + "dependencies" + File.separator + customerId + File.separator + this.platformType;
        final HashMap hm = new HashMap();
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", true);
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            final InputStream in = new FileInputStream(tempFilePath);
            ApiFactoryProvider.getFileAccessAPI().writeFile(tempFilePath, in);
        }
        final HashMap xapFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(tempFilePath, appRepositoryFolder, dbFilePath, true, false);
        hm.put("path", xapFileSourceMap.get("destDCFileName"));
        final String fileSourceDestFileName = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
        return fileSourceDestFileName;
    }
    
    public void handleDependencyMapping(final JSONObject appJSON) throws Exception {
        final String dependencyStr = appJSON.optString("DependencyIds");
        final JSONArray requiredDependencies = appJSON.optJSONArray("DependencyList");
        final Long configDataItemID = appJSON.getLong("CONFIG_DATA_ITEM_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDependencyPolicy"));
        selectQuery.addSelectColumn(Column.getColumn("AppDependencyPolicy", "*"));
        final Criteria configCriteria = new Criteria(Column.getColumn("AppDependencyPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
        selectQuery.setCriteria(configCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dependencyStr != null && !dependencyStr.equals("")) {
            final JSONArray dependencies = new JSONArray(dependencyStr);
            final List dependencyList = new ArrayList();
            for (int i = 0; i < dependencies.length(); ++i) {
                final Long dependencyID = new Long(dependencies.get(i).toString());
                dependencyList.add(dependencyID);
                final Criteria dependencyCriteria = new Criteria(Column.getColumn("AppDependencyPolicy", "DEPENDENCY_ID"), (Object)dependencyID, 0);
                Row row = dataObject.getRow("AppDependencyPolicy", dependencyCriteria);
                if (row == null) {
                    row = new Row("AppDependencyPolicy");
                    row.set("CONFIG_DATA_ITEM_ID", (Object)configDataItemID);
                    row.set("DEPENDENCY_ID", (Object)dependencyID);
                    dataObject.addRow(row);
                }
            }
            dataObject.deleteRows("AppDependencyPolicy", new Criteria(Column.getColumn("AppDependencyPolicy", "DEPENDENCY_ID"), (Object)dependencyList.toArray(), 9).and(configCriteria));
            MDMUtil.getPersistence().update(dataObject);
        }
        else {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AppDependencyPolicy");
            deleteQuery.setCriteria(configCriteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        if (requiredDependencies != null) {
            final JSONArray jsonArray = appJSON.optJSONArray("AppIDList");
            if (jsonArray != null) {
                final List appList = new ArrayList();
                for (int j = 0; j < jsonArray.length(); ++j) {
                    appList.add(jsonArray.get(j));
                }
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AppToRequiredDependency"));
                query.setCriteria(new Criteria(Column.getColumn("AppToRequiredDependency", "APP_ID"), (Object)appList.toArray(), 8));
                query.addSelectColumn(Column.getColumn("AppToRequiredDependency", "*"));
                final DataObject dataObject2 = MDMUtil.getPersistence().get(query);
                for (int k = 0; k < jsonArray.length(); ++k) {
                    for (int l = 0; l < requiredDependencies.length(); ++l) {
                        final Long app_id = (Long)jsonArray.get(k);
                        final JSONObject requiredDependency = (JSONObject)requiredDependencies.get(l);
                        final String dependencyIdentifier = requiredDependency.getString("name");
                        final String dependencyMinVersion = requiredDependency.optString("min_version", "0");
                        final Criteria appCriteria = new Criteria(Column.getColumn("AppToRequiredDependency", "APP_ID"), (Object)app_id, 0);
                        final Criteria dependencyCriteria2 = new Criteria(Column.getColumn("AppToRequiredDependency", "DEPENDENCY_IDENTIFIER"), (Object)dependencyIdentifier, 0);
                        Row row2 = dataObject2.getRow("AppToRequiredDependency", dependencyCriteria2.and(appCriteria));
                        if (row2 == null) {
                            row2 = new Row("AppToRequiredDependency");
                            row2.set("APP_ID", (Object)app_id);
                            row2.set("DEPENDENCY_IDENTIFIER", (Object)dependencyIdentifier);
                            row2.set("DEPENDENCY_MIN_VERSION", (Object)dependencyMinVersion);
                            dataObject2.addRow(row2);
                        }
                        else {
                            row2.set("DEPENDENCY_MIN_VERSION", (Object)dependencyMinVersion);
                            dataObject2.updateRow(row2);
                        }
                    }
                }
                MDMUtil.getPersistence().update(dataObject2);
            }
        }
    }
    
    public JSONObject getListofDependenceies(final String fileName, final Long customerID) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final List list = this.getAppProcessor().getDependenciesFromApp(fileName);
        jsonObject.put("requiredDependencies", (Object)new JSONArray((Collection)list));
        jsonObject.put("availableDependencies", (Object)this.getAvailableDependencies(this.getDependencyBundleIdentifiers(list), customerID));
        return jsonObject;
    }
    
    public JSONArray validateAndUploadMultipleDependencies(final String fileName, final Long customerID) throws Exception {
        final File file = new File(fileName);
        final File extratedDirectory = new File(file.getParent());
        final JSONArray jsonArray = new JSONArray();
        try {
            this.unzip(fileName, extratedDirectory.toString());
            final List uploadedDependencies = this.getAppProcessor().getDependencyFilePaths(extratedDirectory.toString());
            for (final String curdependency : uploadedDependencies) {
                final JSONObject jsonObject = this.validateDependencyUpload(curdependency, customerID);
                if (!jsonObject.getBoolean("success")) {
                    jsonObject.put("fileName", (Object)curdependency);
                    jsonObject.remove("success");
                }
                jsonArray.put((Object)jsonObject);
            }
        }
        finally {
            new FileFacade().deleteFile(file.getParent());
        }
        return jsonArray;
    }
    
    public JSONObject validateAndReturnDependencyCompatibility(final String[] arch, final ArrayList reqDep, final JSONArray dependencies) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final int numArch = 3;
        final Boolean[][] availableMatrix = new Boolean[3][reqDep.size()];
        final List addedDependencies = new ArrayList();
        final JSONArray missingDependencies = new JSONArray();
        final VersionChecker versionChecker = new VersionChecker();
        for (int i = 0; i < numArch; ++i) {
            Arrays.fill(availableMatrix[i], Boolean.TRUE);
        }
        for (int i = 0; i < arch.length; ++i) {
            if (arch[i].toLowerCase().equals("arm")) {
                Arrays.fill(availableMatrix[0], Boolean.FALSE);
            }
            else if (arch[i].toLowerCase().equals("x64")) {
                Arrays.fill(availableMatrix[1], Boolean.FALSE);
            }
            else if (arch[i].toLowerCase().equals("x86")) {
                Arrays.fill(availableMatrix[2], Boolean.FALSE);
            }
        }
        if (dependencies != null) {
            for (int i = 0; i < dependencies.length(); ++i) {
                final JSONObject depdency = dependencies.getJSONObject(i);
                final String name = (String)depdency.opt("name");
                final String arc = (String)depdency.opt("architecture");
                final String version = (String)depdency.opt("version");
                if (name != null && arc != null) {
                    int depIndex = -1;
                    for (int it = 0; it < reqDep.size(); ++it) {
                        final JSONObject reqDepObject = reqDep.get(it);
                        final String reqMinVersion = reqDepObject.optString("min_version", "0");
                        final String reqDepName = reqDepObject.getString("name");
                        if (reqDepName.equals(name) && versionChecker.isGreaterOrEqualIncludeTrailingZeros(version, reqMinVersion)) {
                            depIndex = it;
                            break;
                        }
                    }
                    if (depIndex != -1) {
                        final Long dependencyID = depdency.getLong("dependencyID");
                        if (arc.toLowerCase().equals("arm")) {
                            availableMatrix[0][depIndex] = Boolean.TRUE;
                        }
                        else if (arc.toLowerCase().equals("x64")) {
                            availableMatrix[1][depIndex] = Boolean.TRUE;
                        }
                        else if (arc.toLowerCase().equals("x86")) {
                            availableMatrix[2][depIndex] = Boolean.TRUE;
                        }
                        else if (arc.toLowerCase().equals("neutral")) {
                            for (int j = 0; j < 3; ++j) {
                                availableMatrix[j][depIndex] = Boolean.TRUE;
                            }
                        }
                        addedDependencies.add(dependencyID.toString());
                    }
                }
            }
        }
        for (int i = 0; i < numArch; ++i) {
            for (int k = 0; k < reqDep.size(); ++k) {
                if (!availableMatrix[i][k]) {
                    final String arc2 = (i == 0) ? "arm" : ((i == 1) ? "x64" : "x86");
                    final JSONObject requiredDep = reqDep.get(k);
                    final String name2 = requiredDep.getString("name");
                    final String minVersion = requiredDep.optString("min_version", "0");
                    final JSONObject missingDep = new JSONObject();
                    missingDep.put("name", (Object)name2);
                    missingDep.put("architecture", (Object)arc2);
                    missingDep.put("min_version", (Object)minVersion);
                    missingDependencies.put((Object)missingDep);
                }
            }
        }
        jsonObject.put("missingDependencies", (Object)missingDependencies);
        jsonObject.put("availableDependencies", (Object)new JSONArray((Collection)addedDependencies));
        return jsonObject;
    }
    
    public JSONArray getAvailableDependencies(final List appDependencyList, final Long customerID) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppDependency"));
        selectQuery.addSelectColumn(Column.getColumn("AppDependency", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("AppDependency", "BUNDLE_IDENTIFIER"), (Object)appDependencyList.toArray(), 8);
        final Criteria custCriteria = new Criteria(Column.getColumn("AppDependency", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(criteria.and(custCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("AppDependency");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final JSONObject jsonObject = this.getDependencyJSON(row);
            jsonArray.put((Object)jsonObject);
        }
        return jsonArray;
    }
    
    private void unzip(final String zipFilePath, final String destDirectory) throws Exception {
        final File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        final List<String> allowedExtn = new ArrayList<String>();
        allowedExtn.add("appx");
        allowedExtn.add("msix");
        final Pattern pattern = Pattern.compile("application/zip");
        final ZipSanitizerRule zipSanitizerRule = new ZipSanitizerRule("appxRule", (String)null, -1L, -1, -1, (List)allowedExtn, (List)null, pattern, (Pattern)null);
        final ZipInputStreamWrapper zipIn = new ZipInputStreamWrapper((InputStream)new FileInputStream(zipFilePath), zipSanitizerRule);
        for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
            if (!FileFacade.getInstance().testForPathTraversal(entry.getName())) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final String filePath = destDirectory + File.separator + entry.getName();
            FileFacade.getInstance().validateFileToUnzip(filePath, destDirectory);
            if (!entry.isDirectory()) {
                this.extractFile(zipIn, filePath);
            }
            else {
                final File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }
    
    private void extractFile(final ZipInputStreamWrapper zipIn, final String filePath) throws Exception {
        final int BUFFER_SIZE = 4096;
        if (!FileFacade.getInstance().testForPathTraversal(filePath)) {
            throw new Exception("Path traversal detected in zip");
        }
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        try {
            final byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn, 0, bytesIn.length)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        finally {
            bos.close();
        }
    }
    
    private List getDependencyBundleIdentifiers(final List dependencies) {
        final List dependencyBundlerIdList = new ArrayList();
        for (final Object dependency : dependencies) {
            if (dependency instanceof JSONObject) {
                final JSONObject dependencyObject = (JSONObject)dependency;
                if (!dependencyObject.has("name")) {
                    continue;
                }
                dependencyBundlerIdList.add(dependencyObject.getString("name"));
            }
        }
        return dependencyBundlerIdList;
    }
    
    static {
        AppDependencyHandler.appDependencyInterface = null;
    }
}
