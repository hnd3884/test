package com.me.mdm.server.nsclient;

import java.util.logging.Level;
import java.util.Hashtable;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.NSResponseAPI;

public class NSResponse implements NSResponseAPI
{
    private int success_code;
    private int failed_code;
    private static Logger logger;
    Hashtable htable;
    
    public NSResponse() {
        this.success_code = 0;
        this.failed_code = 1;
        this.htable = new Hashtable();
    }
    
    public void parseResponse(final byte[] b) throws Exception {
        final String responseStr = new String(b, "UTF-8");
        NSResponse.logger.log(Level.INFO, "Response received from NS : {0} ", responseStr);
        if (responseStr != null) {
            final String[] splitstr = responseStr.split(";");
            if (splitstr.length == 2) {
                final String successdata = this.getData(splitstr[0]);
                final String faileddata = this.getData(splitstr[1]);
                if (successdata != null && !successdata.equals("")) {
                    this.updateHash(successdata, this.success_code);
                }
                if (faileddata != null && !faileddata.equals("")) {
                    this.updateHash(faileddata, this.failed_code);
                }
            }
        }
    }
    
    public String getData(final String data) {
        String returnStr = null;
        if (data != null) {
            final int startIndex = data.indexOf("{");
            final int endIndex = data.lastIndexOf("}");
            returnStr = data.substring(startIndex + 1, endIndex);
        }
        return returnStr;
    }
    
    public void updateHash(final String buffer, final int statuscode) {
        final String[] resource = buffer.split(",");
        for (int reslength = resource.length, i = 0; i < reslength; ++i) {
            this.htable.put(new Long(resource[i]), new Integer(statuscode));
        }
    }
    
    public void setResponseStatus(final long resId, final int status) {
        this.htable.put(new Long(resId), new Integer(status));
    }
    
    public boolean isEmpty() {
        return this.htable.size() == 0;
    }
    
    public int getResponseStatus(final Long resourceID) {
        int returnstatus = -1;
        final Integer status = this.htable.get(resourceID);
        if (status != null) {
            returnstatus = status;
        }
        return returnstatus;
    }
    
    static {
        NSResponse.logger = Logger.getLogger("NSClientLogger");
    }
}
