package com.me.devicemanagement.framework.webclient.reports;

import org.json.JSONObject;

public class ReportsProductInvoker
{
    public boolean isValidViewForThisEdition(final Integer viewID) {
        return true;
    }
    
    @Deprecated
    public String getDownloadUrlForFile(final Object object) {
        return null;
    }
    
    public String getDownloadUrlForFile(final JSONObject taskRelevantDetails) {
        try {
            return this.getDownloadUrlForFile(taskRelevantDetails.get("file_name"));
        }
        catch (final Exception e) {
            return null;
        }
    }
}
