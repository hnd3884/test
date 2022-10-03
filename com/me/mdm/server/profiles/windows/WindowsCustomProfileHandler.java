package com.me.mdm.server.profiles.windows;

import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayOutputStream;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.files.FileFacade;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.CustomProfileHandler;

public class WindowsCustomProfileHandler extends CustomProfileHandler
{
    Logger logger;
    public static final Long MAX_FILE_UPLOAD_SIZE;
    
    public WindowsCustomProfileHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public Object addCustomProfile(final JSONObject customProfileJSON, final DataObject dataObject, final List<String> payloadTypeList) throws Exception {
        final Object customProfileId = this.addCustomProfileDetails("", dataObject);
        this.addPayloadTypeDetails(payloadTypeList, dataObject);
        this.mapCustomProfileWithPayload(dataObject, payloadTypeList, customProfileId);
        this.addWindowsData(customProfileJSON.getJSONArray("syncml_commands".toUpperCase()), customProfileId, dataObject);
        return customProfileId;
    }
    
    protected void addWindowsData(final JSONArray syncml_commands, final Object customProfileID, final DataObject dataObject) throws Exception {
        for (int i = 0; i < syncml_commands.length(); ++i) {
            final JSONObject syncMLCommand = syncml_commands.getJSONObject(i);
            final Row windowsCustomProfileDatRow = new Row("WindowsCustomProfilesData");
            windowsCustomProfileDatRow.set("LOC_URI", (Object)syncMLCommand.optString("LOC_URI".toLowerCase()));
            windowsCustomProfileDatRow.set("POSITION", (Object)syncMLCommand.optInt("POSITION".toLowerCase(), 0));
            windowsCustomProfileDatRow.set("ACTION_TYPE", (Object)syncMLCommand.optInt("ACTION_TYPE".toLowerCase()));
            windowsCustomProfileDatRow.set("DATA_TYPE", (Object)syncMLCommand.optInt("DATA_TYPE".toLowerCase(), -1));
            windowsCustomProfileDatRow.set("DATA", (Object)syncMLCommand.optString("DATA".toLowerCase(), (String)null));
            windowsCustomProfileDatRow.set("NAME", (Object)syncMLCommand.optString("NAME".toLowerCase(), (String)null));
            windowsCustomProfileDatRow.set("CUSTOM_PROFILE_ID", customProfileID);
            final Long fileID = syncMLCommand.optLong("data_file_id", -1L);
            final Long customDataID = syncMLCommand.optLong("CUSTOM_PROFILE_DATA_ID".toLowerCase(), -1L);
            final Boolean isModified = syncMLCommand.optBoolean("is_modified", (boolean)Boolean.FALSE);
            if (fileID != -1L) {
                this.handleDataExtnFromFile(dataObject, windowsCustomProfileDatRow, fileID);
            }
            else if (!isModified && customDataID != -1L) {
                final Row row = dataObject.getRow("WindowsCustomProfilesDataExtn", new Criteria(Column.getColumn("WindowsCustomProfilesDataExtn", "CUSTOM_PROFILE_DATA_ID"), (Object)customDataID, 0));
                if (row != null) {
                    final Row extnRow = new Row("WindowsCustomProfilesDataExtn");
                    extnRow.set("CUSTOM_PROFILE_DATA_ID", windowsCustomProfileDatRow.get("CUSTOM_PROFILE_DATA_ID"));
                    extnRow.set("DATA_BLOB", row.get("DATA_BLOB"));
                    dataObject.addRow(extnRow);
                }
            }
            dataObject.addRow(windowsCustomProfileDatRow);
        }
    }
    
    public boolean isRemoveProfileEmpty(final Long collectionId) throws DataAccessException {
        final SelectQuery customProfileSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        customProfileSelectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        customProfileSelectQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        customProfileSelectQuery.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        customProfileSelectQuery.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
        customProfileSelectQuery.addJoin(new Join("CustomProfileDetails", "WindowsCustomProfilesData", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
        customProfileSelectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
        customProfileSelectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
        customProfileSelectQuery.addSelectColumn(new Column("WindowsCustomProfilesData", "CUSTOM_PROFILE_DATA_ID"));
        customProfileSelectQuery.addSelectColumn(new Column("WindowsCustomProfilesData", "ACTION_TYPE"));
        customProfileSelectQuery.setCriteria(new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject payloadsDO = MDMUtil.getPersistenceLite().get(customProfileSelectQuery);
        if (payloadsDO.isEmpty()) {
            return true;
        }
        Iterator iterator = payloadsDO.getRows("ConfigData");
        while (iterator.hasNext()) {
            final Row configDataRow = iterator.next();
            final int configId = (int)configDataRow.get("CONFIG_ID");
            if (configId != 612) {
                return false;
            }
        }
        iterator = payloadsDO.getRows("WindowsCustomProfilesData");
        while (iterator.hasNext()) {
            final Row customProfileDataRow = iterator.next();
            final int actionType = (int)customProfileDataRow.get("ACTION_TYPE");
            if (actionType != 2) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public DataObject getCustomProfileDO(final Criteria criteria) throws Exception {
        final SelectQuery selectQuery = ProfileUtil.getProfileToConfigIdQuery();
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileDetails", "WindowsCustomProfilesData", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 1));
        selectQuery.addJoin(new Join("WindowsCustomProfilesData", "WindowsCustomProfilesDataExtn", new String[] { "CUSTOM_PROFILE_DATA_ID" }, new String[] { "CUSTOM_PROFILE_DATA_ID" }, 1));
        selectQuery.addSelectColumn(new Column("CustomProfileDetails", "*"));
        selectQuery.addSelectColumn(new Column("CustomProfileToCfgDataItem", "*"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("WindowsCustomProfilesData", "*"));
        selectQuery.addSelectColumn(new Column("WindowsCustomProfilesDataExtn", "*"));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("WindowsCustomProfilesData", "POSITION"), true));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    private Boolean isAllowedFileSize(final String filepath) {
        final long uploadedImageSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(filepath);
        return WindowsCustomProfileHandler.MAX_FILE_UPLOAD_SIZE > uploadedImageSize;
    }
    
    private void handleDataExtnFromFile(final DataObject dataObject, final Row windowsCustomProfileDatRow, final Long fileID) throws Exception {
        Row row = dataObject.getRow("CollnToCustomerRel");
        Long customerID = null;
        if (row != null) {
            customerID = (Long)row.get("CUSTOMER_ID");
        }
        else {
            row = dataObject.getRow("CfgDataToCollection");
            customerID = (Long)DBUtil.getValueFromDB("CollnToCustomerRel", "COLLECTION_ID", row.get("COLLECTION_ID"), "CUSTOMER_ID");
        }
        final FileFacade fileFacade = new FileFacade();
        final String tempFilePathDM = fileFacade.validateIfExistsAndReturnFilePath(fileID, customerID);
        if (!this.isAllowedFileSize(tempFilePathDM)) {
            throw new APIHTTPException("PAY0001", new Object[0]);
        }
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(tempFilePathDM);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];
        String value = null;
        try {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            value = new String(Base64.encodeBase64(byteArrayOutputStream.toByteArray()));
        }
        catch (final IOException e) {
            this.logger.log(Level.WARNING, "exception while reading data from Blob", e);
            throw e;
        }
        finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                }
                catch (final Exception e2) {
                    this.logger.log(Level.WARNING, "exception while closing Output stream", e2);
                }
                inputStream.close();
            }
            if (tempFilePathDM != null) {
                fileFacade.deleteFile(tempFilePathDM);
            }
        }
        final Row extnRow = new Row("WindowsCustomProfilesDataExtn");
        extnRow.set("CUSTOM_PROFILE_DATA_ID", windowsCustomProfileDatRow.get("CUSTOM_PROFILE_DATA_ID"));
        extnRow.set("DATA_BLOB", (Object)value);
        dataObject.addRow(extnRow);
    }
    
    static {
        MAX_FILE_UPLOAD_SIZE = 400000L;
    }
}
