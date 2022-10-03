package com.me.mdm.webclient.formbean;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import java.io.File;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2LockScreenPayload;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.config.PayloadProperty;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MDMLockScreenMsgFormBean extends MDMDefaultFormBean
{
    private static final String MESSAGES = "MESSAGES";
    private static Logger logger;
    private static final long IMAGESIZE = 20971520L;
    private List<Integer> orders;
    
    public MDMLockScreenMsgFormBean() {
        this.orders = new ArrayList<Integer>();
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        try {
            for (final JSONObject dynaForm : dynaActionForm) {
                this.validateFormForLockScreen(dataObject, dynaForm);
                final int executionOrder;
                this.insertConfigDataItem(dynaForm, dataObject, executionOrder);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "LockScreenConfiguration", "LOCK_SCREEN_CONFIGURATION_ID");
                final Object lockScreenConfigurationId = dataObject.getFirstRow("LockScreenConfiguration").get("LOCK_SCREEN_CONFIGURATION_ID");
                final String messageString = dynaForm.optString("MESSAGES");
                if (MDMStringUtils.isEmpty(messageString)) {
                    throw new PayloadException("PAY0002");
                }
                final JSONArray messageArray = new JSONArray(messageString);
                this.deleteExistingMessageDetails(dataObject);
                if (messageArray.length() == 0) {
                    throw new PayloadException("PAY0002");
                }
                for (int j = 0; j < messageArray.length(); ++j) {
                    final JSONObject messageObject = messageArray.getJSONObject(j);
                    if (!messageObject.has("ORDER") && !messageObject.has("ORDER".toLowerCase())) {
                        messageObject.put("ORDER", j + 1);
                    }
                    this.addMessageDetails(dataObject, messageObject);
                }
                dynaForm.put("LOCK_SCREEN_CONFIGURATION_ID", lockScreenConfigurationId);
                this.modifyDetails(dataObject, dynaForm, multipleConfigForm, "LockScreenToCfgDataItem", "CONFIG_DATA_ITEM_ID");
            }
        }
        catch (final PayloadException ex) {
            throw ex;
        }
        catch (final Exception e) {
            MDMLockScreenMsgFormBean.logger.log(Level.SEVERE, "Exception while adding the lock screen information", e);
            throw new PayloadException("PAY0003");
        }
    }
    
    @Override
    protected boolean getTransformedFormPropertyValue(final JSONObject multipleConfigForm, final JSONObject dynaFormData, final PayloadProperty payloadProperty) throws Exception {
        final boolean isModified = dynaFormData.optBoolean("WALLPAPER_MODIFIED", true);
        if (!payloadProperty.name.contains("WALLPAPER_PATH")) {
            return true;
        }
        if (!isModified) {
            return false;
        }
        final Long collectionId = multipleConfigForm.optLong("COLLECTION_ID");
        final String wallpaperSource = dynaFormData.optString("WALLPAPER");
        final String folderPath = ProfileUtil.getAndroidWallpaperFolderPath(collectionId);
        final String folderDBPath = ProfileUtil.getAndroidWallpaperDBPath(collectionId);
        if (!MDMStringUtils.isEmpty(wallpaperSource)) {
            final String fileName = DO2LockScreenPayload.lockScreenImageName;
            final boolean upload = ProfileUtil.getInstance().uploadProfileImageFile(wallpaperSource, folderPath, fileName);
            if (upload) {
                payloadProperty.value = folderDBPath.replaceAll("\\\\", "/");
                return true;
            }
            throw new Exception("Unable to upload Lock screen image ");
        }
        else {
            if (isModified) {
                payloadProperty.value = folderDBPath.replaceAll("\\\\", "/");
                return true;
            }
            payloadProperty.value = null;
            return true;
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = configDOFromDB.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object clonedConfigDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object lockScreenId = configDOFromDB.getValue("LockScreenToCfgDataItem", "LOCK_SCREEN_CONFIGURATION_ID", new Criteria(new Column("LockScreenToCfgDataItem", "CONFIG_DATA_ITEM_ID"), configDataItemId, 0));
        Object newLockScreenId = null;
        final Iterator lockScreenConfigurationIterator = configDOFromDB.getRows("LockScreenConfiguration");
        while (lockScreenConfigurationIterator.hasNext()) {
            final Row lockScreenRow = lockScreenConfigurationIterator.next();
            final Row clonedLockScreenRow = new Row("LockScreenConfiguration");
            newLockScreenId = this.cloneRow(lockScreenRow, clonedLockScreenRow, "LOCK_SCREEN_CONFIGURATION_ID");
            cloneConfigDO.addRow(clonedLockScreenRow);
        }
        final Iterator messageIterator = configDOFromDB.getRows("LockScreenToMsgInfo", new Criteria(new Column("LockScreenToMsgInfo", "LOCK_SCREEN_CONFIGURATION_ID"), lockScreenId, 0));
        final List<Long> messageList = new ArrayList<Long>();
        while (messageIterator.hasNext()) {
            final Row messageRow = messageIterator.next();
            messageList.add((Long)messageRow.get("MESSAGE_ID"));
        }
        final Long[] messageArray = new Long[messageList.size()];
        messageList.toArray(messageArray);
        final Iterator messageListIterator = configDOFromDB.getRows("LockScreenMessages", new Criteria(new Column("LockScreenMessages", "MESSAGE_ID"), (Object)messageArray, 8));
        while (messageListIterator.hasNext()) {
            final Row messageRow2 = messageListIterator.next();
            final Row clonedMessageRow = new Row("LockScreenMessages");
            final Object clonedMessageId = this.cloneRow(messageRow2, clonedMessageRow, "MESSAGE_ID");
            final Row clonedLockToMsgRow = new Row("LockScreenToMsgInfo");
            clonedLockToMsgRow.set("MESSAGE_ID", clonedMessageId);
            clonedLockToMsgRow.set("LOCK_SCREEN_CONFIGURATION_ID", newLockScreenId);
            cloneConfigDO.addRow(clonedMessageRow);
            cloneConfigDO.addRow(clonedLockToMsgRow);
        }
        final Row clonedLockToCfgRow = new Row("LockScreenToCfgDataItem");
        clonedLockToCfgRow.set("CONFIG_DATA_ITEM_ID", clonedConfigDataItemId);
        clonedLockToCfgRow.set("LOCK_SCREEN_CONFIGURATION_ID", newLockScreenId);
        cloneConfigDO.addRow(clonedLockToCfgRow);
    }
    
    private void deleteExistingMessageDetails(final DataObject dataObject) throws Exception {
        dataObject.deleteRows("LockScreenToMsgInfo", (Criteria)null);
        dataObject.deleteRows("LockScreenMessages", (Criteria)null);
    }
    
    private void addMessageDetails(final DataObject dataObject, final JSONObject formData) throws Exception {
        this.validateMessageForLockscreen(formData);
        final Integer order = formData.getInt("ORDER");
        if (this.orders.contains(order)) {
            throw new PayloadException("PAY0004");
        }
        this.orders.add(order);
        final Row payloadRow = new Row("LockScreenMessages");
        final PayloadProperty payloadProperty = new PayloadProperty();
        final List columnList = payloadRow.getColumns();
        for (int i = 0; i < columnList.size(); ++i) {
            payloadProperty.name = columnList.get(i);
            payloadProperty.value = formData.opt(payloadProperty.name);
            if (!payloadProperty.name.equals("MESSAGE_ID")) {
                payloadRow.set(payloadProperty.name, payloadProperty.value);
            }
        }
        final Row lockConfToMsgRow = new Row("LockScreenToMsgInfo");
        lockConfToMsgRow.set("LOCK_SCREEN_CONFIGURATION_ID", dataObject.getFirstRow("LockScreenConfiguration").get("LOCK_SCREEN_CONFIGURATION_ID"));
        lockConfToMsgRow.set("MESSAGE_ID", payloadRow.get("MESSAGE_ID"));
        dataObject.addRow(payloadRow);
        dataObject.addRow(lockConfToMsgRow);
    }
    
    private String getMessageDetails(final DataObject dataObject) {
        final JSONArray messageArray = new JSONArray();
        try {
            final SortColumn sortColumn = new SortColumn(new Column("LockScreenMessages", "ORDER"), true);
            dataObject.sortRows("LockScreenMessages", new SortColumn[] { sortColumn });
            final Iterator messageIterator = dataObject.getRows("LockScreenMessages");
            while (messageIterator.hasNext()) {
                final Row messageRow = messageIterator.next();
                final JSONObject messgaeObject = new JSONObject();
                final List columnList = messageRow.getColumns();
                for (int i = 0; i < columnList.size(); ++i) {
                    final String columnName = columnList.get(i);
                    final Object columnValue = messageRow.get(columnName);
                    if (!columnName.equals("MESSAGE_ID")) {
                        messgaeObject.put(columnName, columnValue);
                    }
                }
                messageArray.put((Object)messgaeObject);
            }
        }
        catch (final Exception e) {
            MDMLockScreenMsgFormBean.logger.log(Level.SEVERE, "Exception while getting message details in lockscreen", e);
        }
        return messageArray.toString();
    }
    
    @Override
    protected boolean getTransformedPropertyValue(final DataObject dataObject, final PayloadProperty policyRowData) {
        try {
            if (policyRowData.name.equals("WALLPAPER_PATH")) {
                final Row row = dataObject.getFirstRow("LockScreenConfiguration");
                final Integer wallpaperType = (Integer)row.get("WALLPAPER_TYPE");
                if (wallpaperType == 2) {
                    final String tempPath = policyRowData.value + File.separator + DO2LockScreenPayload.lockScreenImageName;
                    policyRowData.value = tempPath.replaceAll("\\\\", "/");
                    policyRowData.value = this.constructFileUrl(policyRowData.value);
                }
                else if (wallpaperType == 1) {
                    return false;
                }
            }
        }
        catch (final Exception ex) {
            return false;
        }
        return true;
    }
    
    private void validateFormForLockScreen(final DataObject dataObject, final JSONObject formData) throws PayloadException, JSONException, DataAccessException {
        final Integer wallpaperType = formData.optInt("WALLPAPER_TYPE");
        final String wallpaperSource = formData.optString("WALLPAPER");
        String wallpaperSourcePath = null;
        if (!dataObject.isEmpty()) {
            final Row lockscreenRow = dataObject.getRow("LockScreenConfiguration");
            if (lockscreenRow != null) {
                wallpaperSourcePath = (String)lockscreenRow.get("WALLPAPER_PATH");
            }
        }
        final boolean isModified = formData.optBoolean("WALLPAPER_MODIFIED", true);
        final Integer orientation = formData.optInt("ORIENTATION", 1);
        if (wallpaperType == 0 || (wallpaperType == 1 && MDMStringUtils.isEmpty(formData.optString("BG_COLOUR"))) || (wallpaperType == 2 && MDMStringUtils.isEmpty(wallpaperSource) && isModified) || (!isModified && wallpaperType == 2 && MDMStringUtils.isEmpty(wallpaperSourcePath))) {
            throw new PayloadException("PAY0002");
        }
        if (!MDMStringUtils.isEmpty(wallpaperSource) && this.isImageSizeGreater(wallpaperSource)) {
            throw new PayloadException("PAY0001");
        }
        formData.put("ORIENTATION", (Object)orientation);
    }
    
    private void validateMessageForLockscreen(final JSONObject messageJSON) throws PayloadException {
        final String message = messageJSON.optString("MESSAGE");
        final Pattern pattern = Pattern.compile("%(.*?)%", 32);
        final Matcher matcher = pattern.matcher(message);
        final String replaceString = matcher.replaceAll(" ");
        final String hexaCode = messageJSON.optString("TEXT_COLOUR");
        if (!MDMStringUtils.isEmpty(message) && !MDMStringUtils.isEmpty(hexaCode) && replaceString.length() < 75 && Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").matcher(hexaCode).matches()) {
            return;
        }
        throw new PayloadException("PAY0004");
    }
    
    private boolean isImageSizeGreater(final String source) {
        final long uploadedImageSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(source);
        return 20971520L < uploadedImageSize;
    }
    
    static {
        MDMLockScreenMsgFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
