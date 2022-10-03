package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import com.me.mdm.server.inv.InventoryCertificateDataHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class CertificateDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String CERTIFICATE_NAME = "CommonName";
    private static final String CERTIFICATE_TYPE = "CertificateType";
    private static final String CERTIFICATE_VERSION = "CertificateVersion";
    private static final String CERTIFICATE_SERIAL_NUMBER = "CertificateSerialNumber";
    private static final String CERTIFICATE_SIGNATURE_ALGORITHM_OID = "SingnatureAlgorithmOID";
    private static final String CERTIFICATE_SIGNATURE_ALGORITHM_NAME = "SignatureAlgorithmName";
    private static final String CERTIFICATE_SIGNATURE = "CertificateSignature";
    private static final String CERTIFICATE_EXPIRE = "CertificateExpire";
    private static final String CERTIFICATE_ISSUER_DN = "CertificateIssuerDN";
    private static final String CERTIFICATE_SUBJECT_DN = "CertificateSubjectDN";
    private static final String CERTIFICATE_IS_IDENTITY = "IsIdentity";
    private static final String SCEP_CERTIFICATE = "ScepCertificate";
    private static final String CERTIFICATE_CONTENT = "CertificateContent";
    private JSONUtil jsonUtil;
    
    public CertificateDetailsMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
        this.jsonUtil = JSONUtil.getInstance();
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject wrappedJSON = new JSONObject(inventoryObject.strData);
            final JSONArray certificateArray = wrappedJSON.getJSONArray("CertificateList");
            final JSONObject newCertificateObject = new JSONObject();
            final JSONArray newCertificateArray = new JSONArray();
            if (certificateArray != null) {
                for (int i = 0; i < certificateArray.length(); ++i) {
                    final JSONObject certificateInfoDetails = (JSONObject)certificateArray.get(i);
                    final JSONObject certificateInfo = this.isolateCertificateInfo(certificateInfoDetails);
                    newCertificateArray.put((Object)certificateInfo);
                }
                newCertificateObject.put("CertificateList", (Object)newCertificateArray);
                inventoryObject.strData = newCertificateObject.toString();
                final InventoryCertificateDataHandler dataHandler = new InventoryCertificateDataHandler();
                dataHandler.populateInventoryData(inventoryObject);
            }
            else {
                this.logger.log(Level.INFO, "Cerificate Array Empty.");
            }
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
    
    private JSONObject isolateCertificateInfo(final JSONObject certificateDetails) {
        final JSONObject certificateInfo = new JSONObject();
        try {
            certificateInfo.put("CommonName", (Object)getCommonName(this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSubjectDN", null)));
            certificateInfo.put("IsIdentity", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "IsIdentity", Boolean.FALSE.toString()));
            certificateInfo.put("CERTIFICATE_TYPE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateType", null));
            certificateInfo.put("CERTIFICATE_VERSION", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateVersion", null));
            certificateInfo.put("CERTIFICATE_SERIAL_NUMBER", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSerialNumber", null));
            certificateInfo.put("SIGNATURE_ALGORITHM_OID", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "SingnatureAlgorithmOID", null));
            certificateInfo.put("SIGNATURE_ALGORITHM_NAME", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "SignatureAlgorithmName", null));
            certificateInfo.put("CERTIFICATE_SIGNATURE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSignature", null));
            certificateInfo.put("CERTIFICATE_EXPIRE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateExpire", null));
            certificateInfo.put("CERTIFICATE_ISSUER_DN", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateIssuerDN", null));
            certificateInfo.put("CERTIFICATE_SUBJECT_DN", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSubjectDN", null));
            certificateInfo.put("CERTIFICATE_CONTENT", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateContent", null));
            final String certName = this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CommonName", null);
            if (certName.contains("SCEP_ID")) {
                certificateInfo.put("IsScepCertificate", true);
            }
            else {
                certificateInfo.put("IsScepCertificate", false);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating certificate information from response data.{0}", ex);
        }
        return certificateInfo;
    }
    
    private static String getCommonName(final String dn) {
        String finalCommonName = " ";
        if (dn == null) {
            return dn;
        }
        final String[] list2 = dn.split("=");
        for (int i = 0; i < list2.length; ++i) {
            final String subString = list2[i];
            if (subString.contains("CN") && !list2[i + 1].contains("SCEP")) {
                String commonName = list2[i + 1];
                final int pos = commonName.lastIndexOf(",");
                if (pos != -1) {
                    commonName = commonName.substring(0, pos);
                }
                finalCommonName = commonName.trim();
                break;
            }
        }
        return finalCommonName;
    }
}
