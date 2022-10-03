package com.adventnet.sym.server.mdm.chrome.payload;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.chrome.payload.transform.DO2ChromeCertificatePolicy;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONException;
import org.json.JSONObject;

public class ChromeONCPayload extends ChromePayload
{
    public ChromeONCPayload() {
    }
    
    public ChromeONCPayload(final String payloadVersion, final String payloadType, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, payloadType, payloadIdentifier, payloadDisplayName);
        JSONObject initialJSON = new JSONObject();
        this.getPayloadJSON().put("PayloadData", (Object)initialJSON);
        initialJSON = new JSONObject();
        this.getPayloadDataJSON().put(payloadType, (Object)initialJSON);
        this.setONCGUID(payloadIdentifier);
        this.setONCName(payloadDisplayName);
        this.setONCType(payloadType);
    }
    
    public JSONObject getPayloadDataJSON() throws JSONException {
        return this.getPayloadJSON().getJSONObject("PayloadData");
    }
    
    public void setONCGUID(final String guid) throws JSONException {
        this.getPayloadDataJSON().put("GUID", (Object)guid);
    }
    
    public void setONCName(final String name) throws JSONException {
        this.getPayloadDataJSON().put("Name", (Object)name);
    }
    
    public void setONCType(final String type) throws JSONException {
        this.getPayloadDataJSON().put("Type", (Object)type);
    }
    
    public String getONCType() throws JSONException {
        return String.valueOf(this.getPayloadDataJSON().get("Type"));
    }
    
    public JSONObject getONCPayloadObject() throws JSONException {
        return this.getPayloadDataJSON().getJSONObject(this.getONCType());
    }
    
    public void setProxySettings(final JSONObject proxySettings) throws JSONException {
        this.getPayloadDataJSON().put("ProxySettings", (Object)proxySettings);
    }
    
    public void initCertificatePayload() {
    }
    
    public static ChromeCertificatePayload getCertificatePayload(final Long certificateID) {
        ChromeCertificatePayload certPayload = null;
        final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0);
        final DataObject certDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
        try {
            if (certDO != null && !certDO.isEmpty()) {
                final Row identityRow = certDO.getFirstRow("Certificates");
                final int type = (int)identityRow.get("CERTIFICATE_TYPE");
                if (type == 0) {
                    final DO2ChromeCertificatePolicy certificate = new DO2ChromeCertificatePolicy();
                    certPayload = certificate.createPayload(certDO);
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return certPayload;
    }
    
    public void addCertificateToPayload(final ChromeCertificatePayload certPayload) throws JSONException {
        this.getPayloadJSON().put("certificates", (Object)certPayload);
    }
}
