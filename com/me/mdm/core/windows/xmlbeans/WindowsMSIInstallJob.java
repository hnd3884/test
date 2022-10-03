package com.me.mdm.core.windows.xmlbeans;

import org.json.JSONException;
import java.util.Arrays;
import org.json.JSONObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MsiInstallJob")
public class WindowsMSIInstallJob
{
    private String id;
    private Product product;
    
    public String getId() {
        return this.id;
    }
    
    @XmlAttribute(name = "id")
    public void setId(final String id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return this.product;
    }
    
    @XmlElement(name = "Product")
    public void setProduct(final Product product) {
        this.product = product;
    }
    
    public static WindowsMSIInstallJob getWinMSIInstallJobBean(final JSONObject msiDetailsJSON) throws JSONException {
        final Enforcement enforcementNode = new Enforcement();
        enforcementNode.setRetryCount(msiDetailsJSON.optInt("retryCount", 3));
        enforcementNode.setRetryInterval(msiDetailsJSON.optInt("retryInterval", 5));
        enforcementNode.setTimeOut(msiDetailsJSON.optInt("timeOut", 5));
        if (msiDetailsJSON.has("commandLine")) {
            enforcementNode.setCommandLine(String.valueOf(msiDetailsJSON.get("commandLine")));
        }
        final Download downloadNode = new Download();
        downloadNode.setContentURLList(Arrays.asList(String.valueOf(msiDetailsJSON.get("downloadURL"))));
        final Product productNode = new Product();
        productNode.setVersion(String.valueOf(msiDetailsJSON.get("version")));
        productNode.setDownload(downloadNode);
        productNode.setFileHashList(Arrays.asList(String.valueOf(msiDetailsJSON.get("fileHash"))));
        productNode.setEnforcement(enforcementNode);
        final WindowsMSIInstallJob msiInstallJob = new WindowsMSIInstallJob();
        msiInstallJob.setId(String.valueOf(msiDetailsJSON.get("productID")));
        msiInstallJob.setProduct(productNode);
        return msiInstallJob;
    }
}
