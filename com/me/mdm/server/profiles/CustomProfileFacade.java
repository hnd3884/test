package com.me.mdm.server.profiles;

import java.util.List;
import java.util.Iterator;
import com.dd.plist.NSObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import com.me.mdm.server.profiles.windows.WindowsCustomProfileHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadTypeConstants;
import org.json.JSONArray;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.server.util.MDMSecurityLogger;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;

public class CustomProfileFacade
{
    private static final Logger LOGGER;
    
    public void downloadCustomProfile(final APIRequest apiRequest) throws Exception {
        BufferedOutputStream buffOut = null;
        InputStream is = null;
        try {
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long customProfileId = APIUtil.getResourceID(requestJson, "customprofile_id");
            final Long customerId = APIUtil.getCustomerID(requestJson);
            final Criteria customProfileCriteria = new Criteria(new Column("CustomProfileDetails", "CUSTOM_PROFILE_ID"), (Object)customProfileId, 0);
            final Criteria customerIdCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject dataObject = new CustomProfileHandler().getCustomProfileDO(customProfileCriteria.and(customerIdCriteria));
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { customProfileId });
            }
            final Row customProfileDetail = dataObject.getRow("CustomProfileDetails");
            String customProfileDbPath = (String)customProfileDetail.get("CUSTOM_PROFILE_PATH");
            if (!MDMStringUtils.isEmpty(customProfileDbPath)) {
                customProfileDbPath = customProfileDbPath.replace("/", File.separator);
                final String webdir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String customProfileFilePath = webdir + File.separator + customProfileDbPath;
                final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileName(customProfileFilePath);
                apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                is = ApiFactoryProvider.getFileAccessAPI().readFile(customProfileFilePath);
                final int fileSize = (int)ApiFactoryProvider.getFileAccessAPI().getFileSize(customProfileFilePath);
                int read = 0;
                final byte[] bytes = new byte[fileSize];
                buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
                while ((read = is.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, read);
                }
                buffOut.flush();
                return;
            }
            throw new APIHTTPException("COM0008", new Object[] { customProfileId });
        }
        catch (final APIHTTPException e) {
            CustomProfileFacade.LOGGER.log(Level.SEVERE, "Exception in downloading custom profile : ", e);
            throw e;
        }
        catch (final Exception e2) {
            CustomProfileFacade.LOGGER.log(Level.SEVERE, "Exception in downloading custom profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (is != null) {
                is.close();
            }
            if (buffOut != null) {
                buffOut.close();
            }
        }
    }
    
    public JSONObject getCustomProfileDetails(final JSONObject apiRequest) throws Exception {
        final JSONObject responseObject = new JSONObject();
        try {
            final Long customProfileId = APIUtil.getResourceID(apiRequest, "customprofile_id");
            APIUtil.getNewInstance();
            final Integer platformType = APIUtil.getIntegerFilter(apiRequest, "platform");
            if (platformType == -1) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(apiRequest);
            final Criteria customProfileCriteria = new Criteria(new Column("CustomProfileDetails", "CUSTOM_PROFILE_ID"), (Object)customProfileId, 0);
            final Criteria customerIdCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria platformCriteria = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
            switch (platformType) {
                case 1: {
                    final DataObject dataObject = new CustomProfileHandler().getCustomProfileDO(customProfileCriteria.and(customerIdCriteria).and(platformCriteria));
                    if (dataObject.isEmpty()) {
                        throw new APIHTTPException("COM0008", new Object[] { customProfileId });
                    }
                    final Row customProfileDetail = dataObject.getRow("CustomProfileDetails");
                    String customProfileDbPath = (String)customProfileDetail.get("CUSTOM_PROFILE_PATH");
                    customProfileDbPath = customProfileDbPath.replace("/", File.separator);
                    final String webdir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                    final String customProfileFilePath = webdir + File.separator + customProfileDbPath;
                    final NSObject payloadData = DMSecurityUtil.parsePropertyList(ApiFactoryProvider.getFileAccessAPI().readFile(customProfileFilePath));
                    final HashSet keyHash = MDMSecurityLogger.getPlistsensitiveKeyHash();
                    final String encryptedData = PlistWrapper.getInstance().replaceDictionaryData(payloadData, keyHash, "*****").toXMLPropertyList();
                    responseObject.put("data", (Object)encryptedData);
                    responseObject.put("custom_profile_id", (Object)customProfileId);
                    final Iterator iterator = dataObject.getRows("PayloadTypeDetails");
                    final JSONArray payloadList = new JSONArray();
                    final JSONArray existingPayload = new JSONArray();
                    final List supportedPayloadList = PayloadTypeConstants.getSupportedPayloadTypes();
                    while (iterator.hasNext()) {
                        final Row payloadRow = iterator.next();
                        final String payload = (String)payloadRow.get("PAYLOAD_TYPE");
                        if (supportedPayloadList.contains(payload)) {
                            existingPayload.put((Object)payload);
                        }
                        payloadList.put((Object)payload);
                    }
                    responseObject.put("payloads", (Object)payloadList);
                    responseObject.put("existingpayloads", (Object)existingPayload);
                    break;
                }
                case 3: {
                    final DataObject dataObject = new WindowsCustomProfileHandler().getCustomProfileDO(customProfileCriteria.and(customerIdCriteria).and(platformCriteria));
                    if (dataObject.isEmpty()) {
                        throw new APIHTTPException("COM0008", new Object[] { customProfileId });
                    }
                    responseObject.put("custom_profile_id", (Object)customProfileId);
                    final Iterator iterator2 = dataObject.getRows("WindowsCustomProfilesData");
                    final JSONArray syncMLCommands = new JSONArray();
                    while (iterator2.hasNext()) {
                        final Row profileDataRow = iterator2.next();
                        final JSONObject syncMLCommand = new JSONObject();
                        final String locURI = (String)profileDataRow.get("LOC_URI");
                        final int actionType = (int)profileDataRow.get("ACTION_TYPE");
                        final int dataType = (int)profileDataRow.get("DATA_TYPE");
                        final int position = (int)profileDataRow.get("POSITION");
                        String data = (String)profileDataRow.get("DATA");
                        final Long customProfileDataID = (Long)profileDataRow.get("DATA");
                        String dataBlob = null;
                        final Row extnRow = dataObject.getRow("WindowsCustomProfilesDataExtn", new Criteria(Column.getColumn("WindowsCustomProfilesDataExtn", "CUSTOM_PROFILE_DATA_ID"), (Object)customProfileDataID, 0));
                        if (extnRow != null) {
                            final InputStream inputStream = (ByteArrayInputStream)extnRow.get("DATA_BLOB");
                            dataBlob = IOUtils.toString(inputStream);
                        }
                        syncMLCommand.put("LOC_URI".toLowerCase(), (Object)locURI);
                        syncMLCommand.put("ACTION_TYPE".toLowerCase(), actionType);
                        syncMLCommand.put("POSITION".toLowerCase(), position);
                        syncMLCommand.put("CUSTOM_PROFILE_DATA_ID".toLowerCase(), (Object)customProfileDataID);
                        if (dataType != -1) {
                            syncMLCommand.put("DATA_TYPE".toLowerCase(), dataType);
                        }
                        if (!MDMStringUtils.isEmpty(data) || !MDMStringUtils.isEmpty(dataBlob)) {
                            if (!MDMStringUtils.isEmpty(dataBlob)) {
                                data = dataBlob;
                                syncMLCommand.put("is_blob", true);
                            }
                            data = new String(Base64.decodeBase64(data));
                            syncMLCommand.put("DATA".toLowerCase(), (Object)data);
                        }
                        syncMLCommands.put((Object)syncMLCommand);
                    }
                    responseObject.put("syncml_commands", (Object)syncMLCommands);
                    break;
                }
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            CustomProfileFacade.LOGGER.log(Level.SEVERE, "Exception in getting custom profile details", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseObject;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
