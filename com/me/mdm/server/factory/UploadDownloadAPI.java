package com.me.mdm.server.factory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import com.me.mdm.core.auth.APIKey;
import java.util.HashMap;
import org.json.JSONObject;

public interface UploadDownloadAPI
{
    JSONObject getUploadData() throws Exception;
    
    void writeFileInAppServer(final String p0, final String p1) throws Exception;
    
    void deleteDirectoryInAppServer(final String p0) throws Exception;
    
    JSONObject isSpaceExists(final int p0, final String p1) throws Exception;
    
    String constructFileURLwithDownloadServer(final HashMap p0) throws Exception;
    
    String replaceDeviceAPIKeyPlaceHolderForUDWindows(final String p0, final APIKey p1, final boolean p2) throws Exception;
    
    default void getAntivirusScanResult(final File scanFile, final HttpServletRequest request) throws Exception {
    }
    
    default void handleUploadTimeOut(final Long fileID) throws Exception {
    }
}
