package com.me.ems.framework.common.api.v1.service;

import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.Iterator;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.me.ems.framework.common.api.utils.FileAccess;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.persistence.DataAccess;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.UUID;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.adventnet.iam.security.UploadedFileItem;
import java.util.logging.Logger;

public class FileService
{
    public static String serverHome;
    public static String apiTempFolder;
    public static String dcFileTempFolder;
    private static Logger logger;
    
    public boolean validateContentType(final String contentTypeFromRequest, final UploadedFileItem uploadedFileItem) {
        final String contentType = uploadedFileItem.getRequestContentType();
        return contentTypeFromRequest != null && contentTypeFromRequest.equalsIgnoreCase(contentType) && contentTypeFromRequest.equalsIgnoreCase(uploadedFileItem.getDetectedContentType());
    }
    
    public List<File> saveUploadedFile(final Map<String, UploadedFileItem> uploadedFileItems, final Long customerID, final User user, final String moduleName) throws APIException {
        final List dcFileList = new ArrayList();
        final WritableDataObject writableDataObject = new WritableDataObject();
        try {
            for (final Map.Entry<String, UploadedFileItem> entry : uploadedFileItems.entrySet()) {
                final UploadedFileItem uploadedFileItem = entry.getValue();
                final File uploadedFile = uploadedFileItem.getUploadedFile();
                final String fileName = uploadedFileItem.getFileName();
                final UUID randomid = UUID.randomUUID();
                final String folderName = FileService.dcFileTempFolder + File.separator + randomid;
                final String filePath = folderName + File.separator + SyMUtil.getCurrentTime() + "_" + fileName;
                final String contentType = uploadedFileItem.getDetectedContentType();
                final String sourceFilePath = uploadedFile.getAbsolutePath();
                final Long fileLength = uploadedFile.length();
                final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                fileAccessAPI.createDirectory(folderName);
                fileAccessAPI.writeFile(filePath, new FileInputStream(uploadedFile));
                fileAccessAPI.deleteFile(sourceFilePath);
                if (!new File(filePath).getCanonicalPath().startsWith(new File(folderName).getCanonicalPath())) {
                    throw new IOException("Filepath is outside of the target dir: " + filePath);
                }
                final Row fileDetailsRow = this.constructFileDetailsRow(fileName, filePath, contentType, fileLength, "", customerID, moduleName);
                DataAccess.generateValues(fileDetailsRow);
                writableDataObject.addRow(fileDetailsRow);
                final Long fileID = (Long)fileDetailsRow.get("FILE_ID");
                final Long expiryTime = (Long)fileDetailsRow.get("EXPIRY_TIME");
                final String expiryDate = Utils.getTime(expiryTime, user.getTimeFormat());
                final com.me.ems.framework.common.api.v1.model.File dcFileDetails = new com.me.ems.framework.common.api.v1.model.File(fileID, fileName, customerID, expiryDate, FileAccess.FILE_STATUS_READY);
                dcFileList.add(dcFileDetails);
            }
            DataAccess.add((DataObject)writableDataObject);
        }
        catch (final Exception ex) {
            FileService.logger.log(Level.SEVERE, "Exception while saving file", ex);
            throw new APIException("FILE0002");
        }
        return dcFileList;
    }
    
    public Row constructFileDetailsRow(final String fileName, final String filePath, final String contentType, final Long contentLength, final String remarks, final Long customerID, final String moduleName) {
        final FileAccess fileAccess = new FileAccess();
        final Long expiryOffset = 600000L;
        final Long expiryTime = SyMUtil.getCurrentTimeInMillis() + expiryOffset;
        return fileAccess.constructFileDetailsRow(fileName, filePath, contentType, contentLength, remarks, customerID, expiryTime, moduleName);
    }
    
    static {
        FileService.serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        FileService.apiTempFolder = "apiTempDownloads";
        FileService.dcFileTempFolder = FileService.serverHome + File.separator + FileService.apiTempFolder;
        FileService.logger = Logger.getLogger(FileService.class.getName());
    }
}
