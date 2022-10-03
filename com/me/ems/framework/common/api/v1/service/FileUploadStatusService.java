package com.me.ems.framework.common.api.v1.service;

import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.FileAccess;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.Map;
import java.util.logging.Logger;

public class FileUploadStatusService
{
    private static final Logger LOGGER;
    
    public JSONObject getFileUploadStatus(final Map data) throws APIException {
        try {
            final List<Long> idList = new ArrayList<Long>();
            final ArrayList idArrayList = data.get("fileIDs");
            for (final Object fileID : idArrayList) {
                final Long fileId = Long.valueOf(fileID.toString());
                ApiFactoryProvider.getFileAccessAPI().handleUploadTimeOut(fileId);
                idList.add(fileId);
            }
            FileUploadStatusService.LOGGER.log(Level.INFO, "File ID List : {0}", idList);
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final JSONArray statusArray = new FileAccess().getFileStatus(idList, customerID);
            FileUploadStatusService.LOGGER.log(Level.INFO, "File Upload Status Array {0}", statusArray);
            final JSONObject response = new JSONObject();
            response.put("response", (Object)statusArray);
            FileUploadStatusService.LOGGER.log(Level.INFO, "File Upload Status Response {0}", response);
            return response;
        }
        catch (final Exception exception) {
            FileUploadStatusService.LOGGER.log(Level.SEVERE, "Exception occurred in FileUploadStatusService while getting file upload status array - {0}", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(FileUploadStatusService.class.getName());
    }
}
