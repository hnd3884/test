package com.me.mdm.server.inv.ios;

import java.util.Hashtable;
import org.apache.commons.io.IOUtils;
import java.security.cert.CertificateFactory;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Properties;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.io.File;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DeviceInstalledCertificateDataHandler
{
    private static final Logger LOGGER;
    public static final String CERTIFICATE_ISSUER_NAME = "CertificateIssuerName";
    public static final String CERTIFICATE_NAME = "CertificateName";
    public static final String CERTIFICATE_EXPIRY = "CertificateExpiry";
    public static final String IS_IDENTITY = "IsIdentity";
    public static final String SERIAL_NUMBER = "SerialNumber";
    public static final String SIGNATURE_ALGOITHM_NAME = "SignatureAlgorithmName";
    public static final String SIGNATURE_ALGORITHM_OID = "SignatureAlgorithmOID";
    public static final String CERTIFICATE_SUBJECT_NAME = "CertificateSubjectName";
    public static final String IS_MANAGED = "IsManaged";
    public static final String MANAGED_CERTIFICATES = "managedcertificates";
    public static final String UNMANAGED_CERTIFICATE = "unmanagedcertificates";
    DataObject certificateRepositry;
    JSONObject sslServerCertificatePathJSON;
    JSONObject sslCAJSON;
    JSONObject apnsCertificateJSON;
    
    public DeviceInstalledCertificateDataHandler() {
        this.certificateRepositry = null;
        this.sslServerCertificatePathJSON = null;
        this.sslCAJSON = null;
        this.apnsCertificateJSON = null;
        try {
            final SelectQuery managedCertificateQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
            managedCertificateQuery.addSelectColumn(new Column((String)null, "*"));
            this.certificateRepositry = MDMUtil.getPersistence().get(managedCertificateQuery);
            final String sslCertificatePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            this.sslServerCertificatePathJSON = CredentialsMgmtAction.extractCertificateDetails(sslCertificatePath, null);
            final String sslCACertificatePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
            this.sslCAJSON = CredentialsMgmtAction.extractCertificateDetails(sslCACertificatePath, null);
            final HashMap apnsCertificateInfo = APNsCertificateHandler.getInstance().getAPNsCertificateInfo();
            final String certFileName = apnsCertificateInfo.get("CERTIFICATE_FILE_NAME");
            final String certPassword = apnsCertificateInfo.get("CERTIFICATE_PASSWORD");
            final String apnsFilePath = MDMUtil.getAPNsCertificateFolderPath() + File.separator + certFileName;
            this.apnsCertificateJSON = CredentialsMgmtAction.extractCertificateDetails(apnsFilePath, certPassword);
        }
        catch (final Exception ex) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception in DeviceInstalledCertificateDataHandler", ex);
        }
    }
    
    public DataObject getCertificateObjectForResource(final Long resourceID, final Long expiry) {
        DataObject dataObject = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdCertificateResourceRel"));
            query.addJoin(new Join("MdCertificateResourceRel", "MdCertificateInfo", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
            query.addJoin(new Join("MdCertificateInfo", "MDScepCertificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 1));
            query.setCriteria(new Criteria(new Column("MdCertificateResourceRel", "RESOURCE_ID"), (Object)resourceID, 0));
            query.addSelectColumn(new Column((String)null, "*"));
            if (expiry != null) {
                final Criteria criteria = new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_EXPIRE"), (Object)expiry, 6);
                query.setCriteria(query.getCriteria().and(criteria));
            }
            dataObject = MDMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception while getting certificate object for resource", e);
        }
        return dataObject;
    }
    
    public JSONObject getInstalledCertificateList(final Long resourceID, final Long expiry) {
        final JSONObject installedCertificateObject = new JSONObject();
        try {
            installedCertificateObject.put("managedcertificates", (Object)this.getManagedCertificateDetails(resourceID, expiry));
            installedCertificateObject.put("unmanagedcertificates", (Object)this.getUnmanagedCertificateDetails(resourceID, expiry));
        }
        catch (final Exception e) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception while getting installed certificate list", e);
        }
        return installedCertificateObject;
    }
    
    private JSONObject getCertificateDetailsFromRow(final Row certificateRow) {
        final JSONObject managedCertificateJSON = new JSONObject();
        try {
            managedCertificateJSON.put("CertificateName", certificateRow.get("CERTIFICATE_NAME"));
            managedCertificateJSON.put("CertificateIssuerName", certificateRow.get("CERTIFICATE_ISSUER_DN"));
            managedCertificateJSON.put("CertificateExpiry", certificateRow.get("CERTIFICATE_EXPIRE"));
            managedCertificateJSON.put("IsIdentity", certificateRow.get("IDENTIFY"));
            managedCertificateJSON.put("SerialNumber", certificateRow.get("CERTIFICATE_SERIAL_NUMBER"));
            managedCertificateJSON.put("SignatureAlgorithmName", certificateRow.get("SIGNATURE_ALGORITHM_NAME"));
            managedCertificateJSON.put("SignatureAlgorithmOID", certificateRow.get("SIGNATURE_ALGORITHM_OID"));
            managedCertificateJSON.put("CertificateSubjectName", certificateRow.get("CERTIFICATE_SUBJECT_DN"));
            if (certificateRow.get("CERTIFICATE_CONTENT") != null) {
                final InputStream inputStream = (ByteArrayInputStream)certificateRow.get("CERTIFICATE_CONTENT");
                managedCertificateJSON.put("CERTIFICATE_CONTENT", (Object)this.getCertificateRawContent(inputStream));
            }
        }
        catch (final Exception ex) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception while getting certificate details", ex);
        }
        return managedCertificateJSON;
    }
    
    public JSONArray getManagedCertificateDetails(final Long resourceID, final Long expiry) {
        final JSONArray installedArray = new JSONArray();
        try {
            final DataObject dataObject = this.getCertificateObjectForResource(resourceID, expiry);
            final List managedSerialNumber = new ArrayList();
            if (!this.certificateRepositry.isEmpty()) {
                final Iterator iterator = this.certificateRepositry.getRows("CredentialCertificateInfo");
                while (iterator.hasNext()) {
                    final Row managedCertificateRow = iterator.next();
                    managedSerialNumber.add(managedCertificateRow.get("CERTIFICATE_SERIAL_NUMBER"));
                }
            }
            managedSerialNumber.add(this.sslServerCertificatePathJSON.optString("serialNumber"));
            managedSerialNumber.add(this.sslCAJSON.optString("serialNumber"));
            managedSerialNumber.add(this.apnsCertificateJSON.optString("serialNumber"));
            final String[] managedSerial = managedSerialNumber.toArray(new String[managedSerialNumber.size()]);
            final Criteria serialNumberCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_SERIAL_NUMBER"), (Object)managedSerial, 8);
            Criteria scepCertificateCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_NAME"), (Object)"%scep%", 2);
            scepCertificateCriteria = scepCertificateCriteria.and(new Column("MdCertificateInfo", "IDENTIFY"), (Object)true, 0);
            if (!dataObject.isEmpty()) {
                final Iterator managedCertificateList = dataObject.getRows("MdCertificateInfo", serialNumberCriteria.or(scepCertificateCriteria));
                while (managedCertificateList.hasNext()) {
                    final Row managedCertificateRow2 = managedCertificateList.next();
                    final JSONObject managedCertificateJSON = this.getCertificateDetailsFromRow(managedCertificateRow2);
                    installedArray.put((Object)managedCertificateJSON);
                }
            }
        }
        catch (final Exception ex) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, ex, () -> "Exception while getting the details for certificate details for resource:" + n.toString());
        }
        return installedArray;
    }
    
    public JSONArray getUnmanagedCertificateDetails(final Long resourceID, final Long expiry) {
        JSONArray installedArray = new JSONArray();
        try {
            final DataObject dataObject = this.getCertificateObjectForResource(resourceID, expiry);
            installedArray = this.getUnmanagedCertificateDetails(dataObject, resourceID);
        }
        catch (final Exception ex) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, ex, () -> "Exception while getting the details for certificate details for resource:" + n.toString());
        }
        return installedArray;
    }
    
    public JSONArray getUnmanagedCertificateDetails(final DataObject dataObject, final Long resourceId) {
        return this.getUnmanagedCertificateDetailsAndPost(dataObject, false, false, resourceId);
    }
    
    public JSONArray getUnmanagedCertificateDetailsAndPost(final DataObject dataObject, final boolean postIndividualCertificate, final boolean postAllCertificate, final Long resourceId) {
        final JSONArray installedArray = new JSONArray();
        try {
            Long customerId = null;
            final List managedSerialNumber = new ArrayList();
            if (!this.certificateRepositry.isEmpty()) {
                final Iterator iterator = this.certificateRepositry.getRows("CredentialCertificateInfo");
                while (iterator.hasNext()) {
                    final Row managedCertificateRow = iterator.next();
                    managedSerialNumber.add(managedCertificateRow.get("CERTIFICATE_SERIAL_NUMBER"));
                }
            }
            if (postIndividualCertificate || postAllCertificate) {
                customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            }
            managedSerialNumber.add(this.sslServerCertificatePathJSON.optString("serialNumber"));
            managedSerialNumber.add(this.sslCAJSON.optString("serialNumber"));
            managedSerialNumber.add(this.apnsCertificateJSON.optString("serialNumber"));
            final String[] managedSerial = managedSerialNumber.toArray(new String[managedSerialNumber.size()]);
            final Criteria serialNumberCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_SERIAL_NUMBER"), (Object)managedSerial, 9);
            Criteria scepCertificateCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_NAME"), (Object)"%scep%", 3);
            scepCertificateCriteria = scepCertificateCriteria.or(new Column("MdCertificateInfo", "IDENTIFY"), (Object)true, 1);
            if (!dataObject.isEmpty()) {
                final Iterator managedCertificateList = dataObject.getRows("MdCertificateInfo", serialNumberCriteria.and(scepCertificateCriteria));
                while (managedCertificateList.hasNext()) {
                    final Row managedCertificateRow2 = managedCertificateList.next();
                    final JSONObject managedCertificateJSON = this.getCertificateDetailsFromRow(managedCertificateRow2);
                    installedArray.put((Object)managedCertificateJSON);
                    if (postIndividualCertificate) {
                        this.postIndividualCertificateToSource(managedCertificateJSON, resourceId, customerId);
                    }
                }
            }
            if (postAllCertificate) {
                this.postCertificateToSource(installedArray, resourceId, customerId);
            }
        }
        catch (final Exception e) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception while verifying unmanaged certificates", e);
        }
        return installedArray;
    }
    
    public void postCertificateToSource(final JSONArray diffUnmanagedCertificateList, final Long resourceId, final Long customerId) {
        DeviceInstalledCertificateDataHandler.LOGGER.log(Level.INFO, "diff unmanaged certificate list{0}", new Object[] { diffUnmanagedCertificateList });
    }
    
    public void postIndividualCertificateToSource(final JSONObject certificateDetails, final Long resourceId, final Long customerId) {
        try {
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
            final Properties prop = new Properties();
            final String certName = certificateDetails.optString("CertificateName", "");
            final String certIssuerName = certificateDetails.optString("CertificateIssuerName", "");
            final String serialNumber = certificateDetails.optString("SerialNumber", "");
            final String sigAlgrthmName = certificateDetails.optString("SignatureAlgorithmName", "");
            final String certSubjName = certificateDetails.optString("CertificateSubjectName", "");
            ((Hashtable<String, String>)prop).put("$device_name$", deviceName);
            ((Hashtable<String, String>)prop).put("$certificate_name$", certName);
            ((Hashtable<String, String>)prop).put("$certificate_issuer_name$", certIssuerName);
            ((Hashtable<String, String>)prop).put("$certificate_subject_name$", certSubjName);
            ((Hashtable<String, String>)prop).put("$serial_number$", serialNumber);
            ((Hashtable<String, String>)prop).put("$signature_algorithm$", sigAlgrthmName);
            ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerId);
            MDMApiFactoryProvider.getSDPIntegrationAPI().handleSDPAlerts(prop, "SDP_MDM_HELPDESK_UNMANAGED_CERTIFICATE_ALERT");
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.INFO, "**Posting unmanaged certificate Details{0}***", new Object[] { certificateDetails });
        }
        catch (final Exception ex) {
            DeviceInstalledCertificateDataHandler.LOGGER.log(Level.SEVERE, "Exception while posting certificate details to SDP", ex);
        }
    }
    
    public String getCertificateRawContent(final InputStream inputStream) throws Exception {
        final CertificateFactory x509Factory = CertificateFactory.getInstance("X.509");
        final String certificateString = IOUtils.toString(inputStream);
        final String beginCertificate = "-----BEGIN CERTIFICATE-----";
        final String endCertificate = "-----END CERTIFICATE-----";
        return beginCertificate + certificateString + endCertificate;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
