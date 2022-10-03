package com.me.mdm.server.msp.sync;

import com.me.mdm.server.apps.MacAppDataHandler;
import com.me.mdm.server.apps.ChromeAppDataHandler;
import com.me.mdm.server.apps.IOSAppDatahandler;
import com.me.mdm.server.apps.AndroidAppDataHandler;
import com.me.mdm.server.apps.WindowsAppDataHandler;
import com.me.mdm.server.apps.AppDataHandlerInterface;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.files.FileFacade;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import org.json.JSONObject;

public abstract class AppsSyncEngine extends BaseConfigurationsSyncEngine
{
    public String appUniqueIdentifier;
    public String appIdentifier;
    public String appVersion;
    public String appVersionCode;
    public JSONObject requestJSON;
    String appTempFileName;
    String displayImageTempFileName;
    String fullImageTempFileName;
    String parentAppFileLoc;
    String parentDisplayImageLoc;
    String parentFullImageLoc;
    JSONObject childSpecificRequest;
    int eventLogConstant;
    
    AppsSyncEngine(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.appTempFileName = null;
        this.displayImageTempFileName = null;
        this.fullImageTempFileName = null;
        this.parentAppFileLoc = null;
        this.parentDisplayImageLoc = null;
        this.parentFullImageLoc = null;
        this.childSpecificRequest = null;
        this.eventLogConstant = 2031;
        this.requestJSON = this.qData.getJSONObject("msg_body");
        this.appUniqueIdentifier = this.requestJSON.getString("app_unique_identifier");
        final String[] appUniqueIds = this.appUniqueIdentifier.split("@@@");
        this.appIdentifier = appUniqueIds[0];
        this.appVersion = appUniqueIds[1];
        this.appVersionCode = appUniqueIds[2];
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        final Criteria appVersionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)this.appVersion, 0);
        final Criteria appVersionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)this.appVersionCode, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)this.platform, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(appIdentifierCriteria.and(appVersionCriteria).and(appVersionCodeCriteria).and(platformCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row packageRow = dataObject.getFirstRow("MdPackage");
            final Row releaseLabelRow = dataObject.getFirstRow("AppReleaseLabel");
            final Long packageId = (Long)packageRow.get("PACKAGE_ID");
            final Long labelId = (Long)releaseLabelRow.get("RELEASE_LABEL_ID");
            response.put("app_id", (Object)packageId);
            response.put("label_id", (Object)labelId);
        }
        else {
            AppsSyncEngine.logger.log(Level.SEVERE, "App version not present in child customer {0} props {1}", new Object[] { customerID, this.parentProfileDO });
        }
        return response;
    }
    
    protected void createTempFilesForBinaries() throws Exception {
        final Row mdPackageToAppDataRow = this.parentProfileDO.getFirstRow("MdPackageToAppData");
        this.parentAppFileLoc = (String)mdPackageToAppDataRow.get("APP_FILE_LOC");
        this.parentDisplayImageLoc = (String)mdPackageToAppDataRow.get("DISPLAY_IMAGE_LOC");
        this.parentFullImageLoc = (String)mdPackageToAppDataRow.get("FULL_IMAGE_LOC");
        if (this.requestJSON.has("app_file")) {
            final String[] fileNameSplit = String.valueOf(this.parentAppFileLoc).split("\\.");
            final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
            final String fileName = MDMUtil.getCurrentTime() + "." + strContentType;
            final UUID randomId = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String folderPath = serverHome + File.separator + "sync_temp_dir" + File.separator + randomId;
            final File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdir();
            }
            this.appTempFileName = folderPath + File.separator + fileName;
            this.parentAppFileLoc = AppMgmtConstants.APP_BASE_PATH + this.parentAppFileLoc;
            new FileFacade().writeFile(this.appTempFileName, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(this.parentAppFileLoc));
        }
        if (this.requestJSON.has("display_image") && !MDMStringUtils.isEmpty(this.parentDisplayImageLoc) && !this.parentDisplayImageLoc.equalsIgnoreCase("Not Available")) {
            final String[] fileNameSplit = this.parentDisplayImageLoc.split("\\.");
            final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
            final String fileName = MDMUtil.getCurrentTime() + "." + strContentType;
            final UUID randomId = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String folderPath = serverHome + File.separator + "sync_temp_dir" + File.separator + randomId;
            final File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdir();
            }
            this.displayImageTempFileName = folderPath + File.separator + fileName;
            this.parentDisplayImageLoc = AppMgmtConstants.APP_BASE_PATH + this.parentDisplayImageLoc;
            new FileFacade().writeFile(this.displayImageTempFileName, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(this.parentDisplayImageLoc));
        }
        if (this.requestJSON.has("full_image") && !MDMStringUtils.isEmpty(this.parentFullImageLoc) && !this.parentFullImageLoc.equalsIgnoreCase("Not Available")) {
            final String[] fileNameSplit = this.parentFullImageLoc.split("\\.");
            final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
            final String fileName = MDMUtil.getCurrentTime() + "." + strContentType;
            final UUID randomId = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String folderPath = serverHome + File.separator + "sync_temp_dir" + File.separator + randomId;
            final File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdir();
            }
            this.fullImageTempFileName = folderPath + File.separator + fileName;
            this.parentFullImageLoc = AppMgmtConstants.APP_BASE_PATH + this.parentFullImageLoc;
            new FileFacade().writeFile(this.fullImageTempFileName, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(this.parentFullImageLoc));
        }
    }
    
    protected void cloneReleaseLabelForChildCustomer(final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        selectQuery.setCriteria(new Criteria(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)this.getLabelId(), 0));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)this.customerId, 0)));
        selectQuery.addSelectColumn(new Column("AppReleaseLabel", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { this.requestJSON.getLong("release_label") });
        }
        final Row existingReleaseLabelRow = dataObject.getFirstRow("AppReleaseLabel");
        Row releaseLabelRow = new Row("AppReleaseLabel");
        releaseLabelRow.set("RELEASE_LABEL_DISPLAY_NAME", existingReleaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"));
        releaseLabelRow.set("CUSTOMER_ID", (Object)customerID);
        DataObject newDO = (DataObject)new WritableDataObject();
        newDO.addRow(releaseLabelRow);
        newDO = MDMUtil.getPersistence().update(newDO);
        releaseLabelRow = newDO.getFirstRow("AppReleaseLabel");
        this.setLabelId((Long)releaseLabelRow.get("RELEASE_LABEL_ID"));
    }
    
    protected void updateCustomerSpecificUVHKeys(final Long customerID) throws Exception {
        this.childSpecificRequest.put("customerID", (Object)customerID);
        final JSONObject childSpecificUVHs = this.getChildSpecificUVH(customerID);
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("filters").has("customer_id")) {
            this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("filters").put("customer_id", (Object)customerID);
        }
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").has("label_id")) {
            this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").put("label_id", childSpecificUVHs.getLong("label_id"));
        }
        if (this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").has("app_id")) {
            this.childSpecificRequest.getJSONObject("msg_header").getJSONObject("resource_identifier").put("app_id", childSpecificUVHs.getLong("app_id"));
        }
    }
    
    protected AppDataHandlerInterface getInstance() {
        AppDataHandlerInterface appDataHandlerInterface = null;
        if (this.platform == 3) {
            appDataHandlerInterface = new WindowsAppDataHandler(this.childSpecificRequest);
        }
        else if (this.platform == 2) {
            appDataHandlerInterface = new AndroidAppDataHandler(this.childSpecificRequest);
        }
        else if (this.platform == 1) {
            appDataHandlerInterface = new IOSAppDatahandler(this.childSpecificRequest);
        }
        else if (this.platform == 4) {
            appDataHandlerInterface = new ChromeAppDataHandler(this.childSpecificRequest);
        }
        else {
            if (this.platform != 6) {
                throw new APIHTTPException("COM0014", new Object[] { "Invalid platform" });
            }
            appDataHandlerInterface = new MacAppDataHandler(this.childSpecificRequest);
        }
        return appDataHandlerInterface;
    }
    
    @Override
    public void setParentDO() throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        final Criteria appIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)this.appIdentifier, 0);
        final Criteria appVersionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)this.appVersion, 0);
        final Criteria appVersionCodeCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"), (Object)this.appVersionCode, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)this.platform, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)this.customerId, 0);
        selectQuery.setCriteria(appIdentifierCriteria.and(appVersionCriteria).and(appVersionCodeCriteria).and(platformCriteria).and(customerCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        this.parentProfileDO = DataAccess.get(selectQuery);
    }
    
    @Override
    public abstract void sync();
    
    protected Long getLabelId() {
        return null;
    }
    
    protected void setLabelId(final Long labelId) {
    }
}
