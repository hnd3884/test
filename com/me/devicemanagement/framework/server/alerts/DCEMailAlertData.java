package com.me.devicemanagement.framework.server.alerts;

import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;

public abstract class DCEMailAlertData
{
    public String htmlFile;
    public String pdfFile;
    String productName;
    public String mailSubject;
    public String alertLevel;
    public String[] mailAttachment;
    
    public DCEMailAlertData() {
        this.htmlFile = null;
        this.pdfFile = null;
        this.productName = ProductUrlLoader.getInstance().getValue("displayname");
        this.mailSubject = "Mail Alert From " + this.productName + " Server";
        this.alertLevel = "INFORMATION";
        this.mailAttachment = null;
    }
    
    public boolean generateXMLDataFile(final String fileName, final DataObject dataObject) throws Exception {
        return false;
    }
    
    public DataObject createEmailAlertDO(final JSONObject jsonObj, final DataObject dataObject) throws Exception {
        return dataObject;
    }
}
