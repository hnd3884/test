package com.me.mdm.onpremise.server.support;

import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.webclient.support.UploadAction;
import com.me.mdm.server.support.MDMUploadAction;

public class MDMPUploadActionImpl implements MDMUploadAction
{
    public boolean checkUploadAccess() throws Exception {
        return UploadAction.checkUploadAccess();
    }
    
    public boolean uploadSupportFile(final String userAgent, final String supportFile, final String fromAddress, final String userMessage, final String ticketID) throws Exception {
        return UploadAction.uploadSupportFile(userAgent, supportFile, fromAddress, userMessage);
    }
    
    public String getEnvironment() throws Exception {
        return null;
    }
    
    public Properties getProductInfo() throws Exception {
        return SyMUtil.getProductInfo();
    }
    
    public String getSubject(final int type) throws Exception {
        String subject = "File Uploaded";
        switch (type) {
            case 1: {
                subject = "MDM Logs For Diagnosis";
                break;
            }
        }
        return subject;
    }
}
