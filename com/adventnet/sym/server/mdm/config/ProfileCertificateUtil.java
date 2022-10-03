package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import java.security.cert.CertificateFactory;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerType;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.DMDataSetWrapper;
import javax.naming.InvalidNameException;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import org.json.JSONException;
import java.util.Collection;
import java.util.Properties;
import java.io.IOException;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrDbHandler;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONArray;
import java.util.regex.Pattern;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import java.util.Iterator;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.certificate.CertificateMapping;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadMapping;
import java.util.List;
import java.util.logging.Logger;

public class ProfileCertificateUtil
{
    private static ProfileCertificateUtil profileCertificateUtil;
    private static Logger logger;
    public static final int CERTIFICATE_TYPE_CA_UPLOADED_CERTIFICATE = 0;
    public static final int CERTIFICATE_TYPE_SCEP_CONFIGURATION = 1;
    public static final int CERTIFICATE_TYPE_FILEVAULT_PERSONAKEY_ESCROW = 2;
    public static final int CERTIFICATE_TYPE_FILEVAULT_INSTITUTIONAL_RECOVERY = 3;
    public static final int CERTIFICATE_TYPE_RA_CERTIFICATE = 4;
    public static final int CERTIFICATE_TYPE_AD_CERT_CONFIGURATION = 5;
    public static final int CERTIFICATE_TYPE_SUPERVISION_IDENTITY = 6;
    public List<ProfilePayloadMapping> certificatesMapptedTableList;
    public HashMap<String, List<Integer>> unConfigureMap;
    
    public static ProfileCertificateUtil getInstance() {
        if (ProfileCertificateUtil.profileCertificateUtil == null) {
            ProfileCertificateUtil.profileCertificateUtil = new ProfileCertificateUtil();
        }
        return ProfileCertificateUtil.profileCertificateUtil;
    }
    
    public List<ProfilePayloadMapping> getCertificateMap() {
        return this.certificatesMapptedTableList;
    }
    
    public ProfileCertificateUtil() {
        this.certificatesMapptedTableList = new ArrayList<ProfilePayloadMapping>();
        this.unConfigureMap = new HashMap<String, List<Integer>>();
        this.certificatesMapptedTableList.add(new CertificateMapping("WifiEnterprise", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("WifiEnterprise", "IDENTITY_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("PayloadWifiEnterprise", "IDENTITY_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("PayloadWifiEnterprise", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnL2TP", "CA_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnL2TP", "USER_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnPPTP", "CA_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnIPSec", "CA_CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnIPSec", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnIKEv2", "CA_CERTIFICATE_ID", "VpnToPolicyRel", "VPN_POLICY_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnCisco", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnJuniperSSL", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnF5SSL", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnPaloAlto", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnCustomSSL", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("CertificatePolicy", "CERTIFICATE_ID", Boolean.TRUE));
        this.certificatesMapptedTableList.add(new CertificateMapping("OpenVPNPolicy", "CERTIFICATE_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("ExchangeActiveSyncPolicy", "IDENTITY_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("ExchangeActiveSyncPolicy", "ENCRYPTION_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("ExchangeActiveSyncPolicy", "SIGNING_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("AndroidActiveSyncPolicy", "IDENTITY_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("VpnPolicyToCertificate", "CLIENT_CERT_ID", "VpnToPolicyRel", "VPN_POLICY_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("EMailPolicy", "ENCRYPTION_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("EMailPolicy", "SIGNING_CERT_ID"));
        this.certificatesMapptedTableList.add(new CertificateMapping("SCEPPolicy", "SCEP_CONFIG_ID", Boolean.TRUE));
        this.certificatesMapptedTableList.add(new CertificateMapping("ADCertPolicy", "AD_CONFIG_ID", Boolean.TRUE));
        this.certificatesMapptedTableList.add(new CertificateMapping("SSOToCertificateRel", "CLIENT_CERT_ID"));
        final List<Integer> certUnConfigureList = new ArrayList<Integer>();
        final List<Integer> scepUnConfigureList = new ArrayList<Integer>();
        final List<Integer> adcsUnConfigureList = new ArrayList<Integer>();
        certUnConfigureList.add(607);
        scepUnConfigureList.add(606);
        scepUnConfigureList.add(516);
        scepUnConfigureList.add(773);
        scepUnConfigureList.add(566);
        adcsUnConfigureList.add(765);
        certUnConfigureList.add(555);
        certUnConfigureList.add(515);
        certUnConfigureList.add(772);
        this.unConfigureMap.put("SCEPPolicy", scepUnConfigureList);
        this.unConfigureMap.put("ADCertPolicy", adcsUnConfigureList);
        this.unConfigureMap.put("CertificatePolicy", certUnConfigureList);
    }
    
    public boolean removeCertificateFile(final String path) {
        boolean status = false;
        try {
            if (ApiFactoryProvider.getFileAccessAPI().deleteFile(path)) {
                status = ApiFactoryProvider.getFileAccessAPI().deleteFile(path);
            }
        }
        catch (final Exception e) {
            ProfileCertificateUtil.logger.log(Level.WARNING, "Exception in removeCertificateFile method while deleteting path...", e);
        }
        return status;
    }
    
    public Long addOrUpdateCredentialCertificateDetails(final JSONObject certificateDetails) throws Exception {
        Long certificateId = null;
        final String certFileName = String.valueOf(certificateDetails.get("certFileName"));
        final String certPassword = String.valueOf(certificateDetails.get("certPassword"));
        final String certificateName = String.valueOf(certificateDetails.get("CommonName"));
        final String thumbPrint = String.valueOf(certificateDetails.get("CERTIFICATE_THUMB_PRINT"));
        final Long customerId = certificateDetails.optLong("customerId");
        final int certificateType = certificateDetails.optInt("certificateType", 0);
        if (certificateDetails.getLong("NotAfter") <= System.currentTimeMillis()) {
            return CredentialsMgmtAction.CERTIFICATE_EXPIRED_ERROR_CODE;
        }
        if (certFileName != null && certPassword != null) {
            final DataObject CredentialCertificateDetailsDO = this.getCredentialCertificateDetailsDO(certificateName, thumbPrint, customerId);
            Row credentialCertificateDetailsRow = null;
            if (CredentialCertificateDetailsDO.isEmpty()) {
                credentialCertificateDetailsRow = new Row("CredentialCertificateInfo");
                credentialCertificateDetailsRow.set("CERTIFICATE_DISPLAY_NAME", (Object)certificateName);
                credentialCertificateDetailsRow.set("CERTIFICATE_FILE_NAME", (Object)certFileName);
                credentialCertificateDetailsRow.set("CERTIFICATE_PASSWORD", (Object)certPassword);
                credentialCertificateDetailsRow.set("CUSTOMER_ID", (Object)customerId);
                credentialCertificateDetailsRow.set("CERTIFICATE_SERIAL_NUMBER", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_SERIAL_NUMBER")));
                credentialCertificateDetailsRow.set("CERTIFICATE_ISSUER_DN", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_ISSUER_DN")));
                credentialCertificateDetailsRow.set("CERTIFICATE_SUBJECT_DN", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_SUBJECT_DN")));
                credentialCertificateDetailsRow.set("CERTIFICATE_NOTBEFORE", (Object)certificateDetails.getLong("NotBefore"));
                credentialCertificateDetailsRow.set("CERTIFICATE_NOTAFTER", (Object)certificateDetails.getLong("NotAfter"));
                credentialCertificateDetailsRow.set("CERTIFICATE_THUMBPRINT", (Object)thumbPrint);
                certificateId = addOrUpdateCertificates(null, certificateType, customerId);
                credentialCertificateDetailsRow.set("CERTIFICATE_ID", (Object)certificateId);
                CredentialCertificateDetailsDO.addRow(credentialCertificateDetailsRow);
                MDMUtil.getPersistence().add(CredentialCertificateDetailsDO);
            }
            else {
                final Row row = CredentialCertificateDetailsDO.getFirstRow("Certificates");
                if (!(boolean)row.get("IS_ACTIVE")) {
                    credentialCertificateDetailsRow = CredentialCertificateDetailsDO.getFirstRow("CredentialCertificateInfo");
                    credentialCertificateDetailsRow.set("CERTIFICATE_DISPLAY_NAME", (Object)certificateName);
                    credentialCertificateDetailsRow.set("CERTIFICATE_FILE_NAME", (Object)certFileName);
                    credentialCertificateDetailsRow.set("CERTIFICATE_PASSWORD", (Object)certPassword);
                    credentialCertificateDetailsRow.set("CERTIFICATE_SERIAL_NUMBER", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_SERIAL_NUMBER")));
                    credentialCertificateDetailsRow.set("CERTIFICATE_ISSUER_DN", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_ISSUER_DN")));
                    credentialCertificateDetailsRow.set("CERTIFICATE_SUBJECT_DN", (Object)String.valueOf(certificateDetails.get("CERTIFICATE_SUBJECT_DN")));
                    credentialCertificateDetailsRow.set("CERTIFICATE_NOTBEFORE", (Object)certificateDetails.getLong("NotBefore"));
                    credentialCertificateDetailsRow.set("CERTIFICATE_NOTAFTER", (Object)certificateDetails.getLong("NotAfter"));
                    credentialCertificateDetailsRow.set("CERTIFICATE_THUMBPRINT", (Object)thumbPrint);
                    CredentialCertificateDetailsDO.updateRow(credentialCertificateDetailsRow);
                    row.set("IS_ACTIVE", (Object)true);
                    CredentialCertificateDetailsDO.updateRow(row);
                    MDMUtil.getPersistenceLite().update(CredentialCertificateDetailsDO);
                    certificateId = (Long)credentialCertificateDetailsRow.get("CERTIFICATE_ID");
                    ProfileCertificateUtil.logger.log(Level.INFO, "The trashed certificate was restored as it was added again by the user {0}", credentialCertificateDetailsRow.getAsJSON());
                }
            }
        }
        return certificateId;
    }
    
    public static Long addOrUpdateCertificates(Long id, final int type, final Long customerID) {
        try {
            final Criteria criteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)id, 0);
            final DataObject certificateDO = DataAccess.get("Certificates", criteria);
            Row certificateRow = null;
            if (certificateDO.isEmpty()) {
                certificateRow = new Row("Certificates");
                certificateRow.set("CERTIFICATE_TYPE", (Object)type);
                certificateRow.set("CUSTOMER_ID", (Object)customerID);
                certificateDO.addRow(certificateRow);
                MDMUtil.getPersistence().add(certificateDO);
                id = (Long)certificateRow.get("CERTIFICATE_RESOURCE_ID");
            }
            else {
                certificateRow = certificateDO.getRow("Certificates", criteria);
                certificateRow.set("CERTIFICATE_TYPE", (Object)type);
                certificateRow.set("CUSTOMER_ID", (Object)customerID);
                certificateDO.updateRow(certificateRow);
                MDMUtil.getPersistence().update(certificateDO);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(ProfileCertificateUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return id;
    }
    
    private DataObject getCredentialCertificateDetailsDO(final String certificateName, final String thumbPrint, final Long customerId) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("CredentialCertificateInfo"));
        query.addJoin(new Join("CredentialCertificateInfo", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
        Criteria criteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
        if (certificateName != null) {
            final Criteria certificateNameCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_DISPLAY_NAME"), (Object)certificateName, 0);
            criteria = criteria.and(certificateNameCriteria);
        }
        if (thumbPrint != null) {
            final Criteria certificatethumbPrintCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_THUMBPRINT"), (Object)thumbPrint, 0);
            criteria = criteria.and(certificatethumbPrintCriteria);
        }
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
        query.addSelectColumn(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"));
        query.addSelectColumn(new Column("Certificates", "CUSTOMER_ID"));
        query.addSelectColumn(new Column("Certificates", "IS_ACTIVE"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public boolean removeCredentialCertificateDetails(final Long certID) {
        boolean status = false;
        final Criteria criteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certID, 0);
        try {
            MDMUtil.getPersistence().delete(criteria);
            status = true;
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in removeCredentialCertificateDetails", ex);
        }
        return status;
    }
    
    public HashMap getCertificateDetails(final String certificateFilePath, final String password) {
        final HashMap certificateDetails = new HashMap();
        KeyStore ks = null;
        try {
            if (certificateFilePath != null) {
                ks = KeyStore.getInstance("PKCS12");
                ks.load(new FileInputStream(certificateFilePath), password.toCharArray());
                X509Certificate cert = null;
                final Enumeration enume = ks.aliases();
                while (enume.hasMoreElements()) {
                    final String alias = enume.nextElement();
                    if (ks.isKeyEntry(alias)) {
                        cert = (X509Certificate)ks.getCertificate(alias);
                        final Date notAfter = cert.getNotAfter();
                        final Date notBefore = cert.getNotBefore();
                        final Principal subjectPrincipal = cert.getSubjectDN();
                        final String subjectName = subjectPrincipal.getName();
                        if (subjectName != null) {
                            final StringTokenizer tokenizer = new StringTokenizer(subjectName, ", ");
                            while (tokenizer.hasMoreElements()) {
                                final String token = (String)tokenizer.nextElement();
                                final String[] strArray = token.split("=");
                                if (token.startsWith("CN=")) {
                                    certificateDetails.put("CertificateName", strArray[1]);
                                }
                                else {
                                    if (!token.startsWith("UID=")) {
                                        continue;
                                    }
                                    certificateDetails.put("Topic", strArray[1]);
                                }
                            }
                        }
                        certificateDetails.put("CreationDate", notBefore.toString());
                        certificateDetails.put("ExpiryDate", notAfter.toString());
                    }
                    ks.deleteEntry(alias);
                }
            }
        }
        catch (final Exception exp) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getCertificateDetails", exp);
        }
        return certificateDetails;
    }
    
    public HashMap getCredentialCertificateList(final Long customerId, final String configName, final Long configDataID) throws Exception {
        final HashMap<String, ArrayList> certificateList = new HashMap<String, ArrayList>();
        Long scepID = null;
        final Criteria cerCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"), (Object)".cer", 11);
        final Criteria crtCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"), (Object)".crt", 11);
        final Criteria derCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"), (Object)".der", 11);
        final Criteria pemCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"), (Object)".pem", 11);
        final Criteria userUplodedCertificate = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)0, 0);
        final Criteria fileVaultPersonalKeyCert = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)2, 0);
        final Criteria fileVaultInstKeyCert = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)3, 0);
        Criteria customerCri = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
        Criteria windowsCertificateTypeCriteria = null;
        if (configName != null) {
            if (configName.trim().startsWith("WINDOWS_WIFI_POLICY")) {
                windowsCertificateTypeCriteria = cerCriteria.or(crtCriteria).or(derCriteria);
            }
            else if (configName.trim().startsWith("WINDOWS_CERTIFICATE_POLICY")) {
                windowsCertificateTypeCriteria = cerCriteria.or(crtCriteria).or(derCriteria).or(pemCriteria);
            }
        }
        if (configName.equalsIgnoreCase("MAC_FILE_VAULT")) {
            customerCri = customerCri.and(fileVaultInstKeyCert);
        }
        else {
            customerCri = customerCri.and(fileVaultInstKeyCert.negate().and(fileVaultPersonalKeyCert.negate()));
        }
        final DataObject certificatesDO = getCertificateDO(customerCri);
        if (!certificatesDO.isEmpty()) {
            final ArrayList certList = new ArrayList();
            final Iterator credentialIterator = certificatesDO.getRows("CredentialCertificateInfo", windowsCertificateTypeCriteria);
            while (credentialIterator.hasNext()) {
                final HashMap certificateMap = new HashMap();
                final Row row = credentialIterator.next();
                certificateMap.put("CERTIFICATE_ID", row.get("CERTIFICATE_ID"));
                certificateMap.put("CERTIFICATE_NAME", row.get("CERTIFICATE_DISPLAY_NAME"));
                certificateMap.put("CERTIFICATE_ISSUER", row.get("CERTIFICATE_ISSUER_DN"));
                certificateMap.put("CERTIFICATE_EXPIRY", row.get("CERTIFICATE_NOTAFTER"));
                certificateMap.put("CERTIFICATE_TYPE", 0);
                certList.add(certificateMap);
            }
            certificateList.put("CredentialCertificateInfo", certList);
        }
        if (configName.trim().equalsIgnoreCase("EXCHANGE_ACTIVE_SYNC_POLICY") || configName.trim().equalsIgnoreCase("VPN_POLICY") || configName.trim().equalsIgnoreCase("WIFI_POLICY") || configName.trim().equalsIgnoreCase("EMAIL_POLICY")) {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("CfgDataToCollection"));
            query.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("ConfigDataItem", "SCEPPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria configIDCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)configDataID, 0);
            query.setCriteria(configIDCriteria);
            query.addSelectColumn(new Column("SCEPPolicy", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row scepPolicyRow = dataObject.getFirstRow("SCEPPolicy");
                scepID = (Long)scepPolicyRow.get("SCEP_CONFIG_ID");
            }
            final ArrayList scepList = new ArrayList();
            final Iterator scepIterator = certificatesDO.getRows("SCEPConfigurations", new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepID, 0));
            while (scepIterator.hasNext()) {
                final HashMap scepMap = new HashMap();
                final Row scepRow = scepIterator.next();
                scepMap.put("CERTIFICATE_ID", scepRow.get("SCEP_CONFIG_ID"));
                scepMap.put("CERTIFICATE_NAME", scepRow.get("SCEP_CONFIGURATION_NAME"));
                scepMap.put("CERTIFICATE_TYPE", 1);
                scepList.add(scepMap);
            }
            certificateList.put("SCEPConfigurations", scepList);
        }
        return certificateList;
    }
    
    public X509Certificate getCertificate(final Long customerID, final Long certID) throws Exception {
        final DataObject certDO = this.getCredentialCertificateDataObject(customerID, certID, null);
        try {
            DEPAdminEnrollmentHandler.logger.log(Level.INFO, "getCertificate():- Going to get the certificate for customerID={0} & certificateID={1}", new Object[] { customerID, certID });
            if (!certDO.isEmpty()) {
                final Row credentialRow = certDO.getFirstRow("CredentialCertificateInfo");
                final String certificateName = (String)credentialRow.get("CERTIFICATE_FILE_NAME");
                final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
                final String certPath = cerFolder + File.separator + certificateName;
                final String password = (String)credentialRow.get("CERTIFICATE_PASSWORD");
                return CredentialsMgmtAction.readCertificateFromPKCS12(certPath, password);
            }
            DEPAdminEnrollmentHandler.logger.log(Level.INFO, "getCertificate():- Fetched certificate for certificateID= {0}", certID);
        }
        catch (final Exception ex) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "getCertificate():- Exception while parsing certificate", ex);
            throw ex;
        }
        return null;
    }
    
    public String getSupervisionIdentityCertFileContents(final Long customerId, final Long certId, final Criteria criteria) throws Exception {
        DEPAdminEnrollmentHandler.logger.log(Level.INFO, "getSupervisionIdentityCertFileContents():- for customerId={0} & certId={1}", new Object[] { customerId, certId });
        try {
            final DataObject dataObject = this.getCredentialCertificateDataObject(customerId, certId, criteria);
            final Row row = dataObject.getFirstRow("CredentialCertificateInfo");
            final String certFileName = (String)row.get("CERTIFICATE_FILE_NAME");
            final String certFolder = MDMUtil.getCredentialCertificateFolder(customerId);
            final String certPath = certFolder + File.separator + certFileName;
            return this.getCertFileContents(certPath);
        }
        catch (final Exception e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "getSupervisionIdentityCertFileContents():- Exception is ", e);
            throw e;
        }
    }
    
    public String getCertFileContents(final String certPath) throws Exception {
        try {
            if (certPath != null) {
                final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(certPath);
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final byte[] data = new byte[1024];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                final byte[] encoded = Base64.encodeBase64(buffer.toByteArray());
                buffer.flush();
                buffer.close();
                return new String(encoded);
            }
            return null;
        }
        catch (final Exception e) {
            DEPAdminEnrollmentHandler.logger.log(Level.SEVERE, "downloadCertificate():- Exception is ", e);
            throw e;
        }
    }
    
    public DataObject getCertificateInfo(final Long customerID, final Long certID) throws Exception {
        if (certID == null || certID < 0L) {
            return null;
        }
        final SelectQueryImpl query = new SelectQueryImpl(new Table("CredentialCertificateInfo"));
        Criteria criteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria certificateNameCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certID, 0);
        criteria = criteria.and(certificateNameCriteria);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public static DataObject updateCredentialCertiInfoTable(final Long customerId, final Long certificateID) {
        DataObject certificateDO = null;
        try {
            final Criteria certIDCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certificateID, 0);
            certificateDO = SyMUtil.getPersistence().get("CredentialCertificateInfo", certIDCriteria);
            if (!certificateDO.isEmpty()) {
                final Iterator certRows = certificateDO.getRows("CredentialCertificateInfo");
                String certFileName = null;
                String certPassword = null;
                while (certRows.hasNext()) {
                    final Row certRow = certRows.next();
                    if (certRow.get("CERTIFICATE_SERIAL_NUMBER") == null) {
                        certFileName = certRow.get("CERTIFICATE_FILE_NAME").toString();
                        if (!certRow.get("CERTIFICATE_PASSWORD").equals("")) {
                            certPassword = (String)certRow.get("CERTIFICATE_PASSWORD");
                        }
                        final String certPath = MDMUtil.getCredentialCertificateFolder(customerId);
                        final JSONObject certificateJSON = CredentialsMgmtAction.extractCertificateDetails(certPath + File.separator + certFileName, certPassword);
                        certRow.set("CERTIFICATE_DISPLAY_NAME", (Object)String.valueOf(certificateJSON.get("CommonName")));
                        certRow.set("CERTIFICATE_SERIAL_NUMBER", (Object)String.valueOf(certificateJSON.get("CERTIFICATE_SERIAL_NUMBER")));
                        certRow.set("CERTIFICATE_ISSUER_DN", (Object)String.valueOf(certificateJSON.get("CERTIFICATE_ISSUER_DN")));
                        certRow.set("CERTIFICATE_SUBJECT_DN", (Object)String.valueOf(certificateJSON.get("CERTIFICATE_SUBJECT_DN")));
                        certRow.set("CERTIFICATE_NOTBEFORE", (Object)certificateJSON.getLong("NotBefore"));
                        certRow.set("CERTIFICATE_NOTAFTER", (Object)certificateJSON.getLong("NotAfter"));
                        certRow.set("CERTIFICATE_THUMBPRINT", (Object)String.valueOf(certificateJSON.get("CERTIFICATE_THUMB_PRINT")));
                        certificateDO.updateRow(certRow);
                    }
                }
                MDMUtil.getPersistence().update(certificateDO);
            }
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception Occured while Updating CredentialCertiInfoTable", ex);
        }
        return certificateDO;
    }
    
    public static DataObject getSCEPConfigDO(final Criteria scepCri, final Range range) {
        DataObject dobj = null;
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Certificates"));
            query.addJoin(new Join("Certificates", "SCEPConfigurations", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "SCEP_CONFIG_ID" }, 2));
            query.addJoin(new Join("SCEPConfigurations", "SCEPServerToTemplate", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
            query.addJoin(new Join("SCEPServerToTemplate", "SCEPServers", new String[] { "SCEP_SERVER_ID" }, new String[] { "SERVER_ID" }, 1));
            query.addJoin(new Join("SCEPConfigurations", "SCEPRenewal", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
            query.addJoin(new Join("SCEPConfigurations", "ScepDyChallengeCredentials", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
            query.addSelectColumn(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"));
            query.addSelectColumn(new Column("Certificates", "CERTIFICATE_TYPE"));
            query.addSelectColumn(new Column("Certificates", "IS_ACTIVE"));
            query.addSelectColumn(new Column("Certificates", "CUSTOMER_ID"));
            query.addSelectColumn(new Column("SCEPConfigurations", "SCEP_CONFIG_ID", "SCEPCONFIGURATIONS.SCEP_CONFIG_ID"));
            query.addSelectColumn(new Column("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"));
            query.addSelectColumn(new Column("SCEPConfigurations", "URL"));
            query.addSelectColumn(new Column("SCEPConfigurations", "NAME"));
            query.addSelectColumn(new Column("SCEPConfigurations", "SUBJECT"));
            query.addSelectColumn(new Column("SCEPConfigurations", "CHALLENGE_ENCRYPTED"));
            query.addSelectColumn(new Column("SCEPConfigurations", "CHALLENGE_TYPE"));
            query.addSelectColumn(new Column("SCEPConfigurations", "KEY_SIZE"));
            query.addSelectColumn(new Column("SCEPConfigurations", "KEY_USAGE"));
            query.addSelectColumn(new Column("SCEPConfigurations", "RETRIES"));
            query.addSelectColumn(new Column("SCEPConfigurations", "RETRY_DELAY"));
            query.addSelectColumn(new Column("SCEPConfigurations", "SUBJECT_ALTNAME_TYPE"));
            query.addSelectColumn(new Column("SCEPConfigurations", "SUBJECT_ALTNAME_VALUE"));
            query.addSelectColumn(new Column("SCEPConfigurations", "NT_PRINCIPAL"));
            query.addSelectColumn(new Column("SCEPConfigurations", "CA_FINGER_PRINT"));
            query.addSelectColumn(new Column("SCEPServers", "SERVER_NAME"));
            query.addSelectColumn(new Column("SCEPServers", "SERVER_ID"));
            query.addSelectColumn(new Column("SCEPServers", "TYPE"));
            query.addSelectColumn(new Column("SCEPServerToTemplate", "SCEP_SERVER_ID"));
            query.addSelectColumn(new Column("SCEPRenewal", "SCEP_CONFIG_ID", "SCEPRENEWAL.SCEP_CONFIG_ID"));
            query.addSelectColumn(new Column("SCEPRenewal", "NUM_DAYS"));
            query.addSelectColumn(new Column("SCEPRenewal", "AUTO_RENEW_ENABLED"));
            query.addSelectColumn(new Column("ScepDyChallengeCredentials", "SCEP_CONFIG_ID", "SCEPDYCHALLENGECREDENTIALS.SCEP_CONFIG_ID"));
            query.addSelectColumn(new Column("ScepDyChallengeCredentials", "SCEP_ADMIN_CHALLENGE_ENDPOINT_URL"));
            query.addSelectColumn(new Column("ScepDyChallengeCredentials", "SCEP_ADMIN_CHALLENGE_USERNAME"));
            query.addSelectColumn(new Column("ScepDyChallengeCredentials", "SCEP_ADMIN_CHALLENGE_PASSWORD"));
            if (scepCri != null) {
                query.setCriteria(scepCri);
            }
            if (range != null) {
                query.setRange(range);
            }
            query.addSortColumn(new SortColumn("Certificates", "CERTIFICATE_RESOURCE_ID", true));
            dobj = MDMUtil.getPersistence().get((SelectQuery)query);
        }
        catch (final Exception exp) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getting scep configdo", exp);
        }
        return dobj;
    }
    
    public static DataObject getADCertConfigDO(final Long customerID, final Criteria adConfigCriteria, final Range range) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "getADCertConfigDO :- for customerID={0}, adConfigCriteria={1}, range={2}", new Object[] { customerID, adConfigCriteria, range });
        DataObject dataObject = null;
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Certificates"));
            query.addJoin(new Join("Certificates", "ADCertConfiguration", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "AD_CONFIG_ID" }, 2));
            query.addSelectColumn(new Column("Certificates", "*"));
            query.addSelectColumn(new Column("ADCertConfiguration", "*"));
            Criteria customerIDCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria certTypeCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_TYPE"), (Object)5, 0);
            final Criteria isActiveCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
            if (adConfigCriteria != null) {
                customerIDCriteria = customerIDCriteria.and(adConfigCriteria);
            }
            query.setCriteria(customerIDCriteria.and(certTypeCriteria).and(isActiveCriteria));
            if (range != null) {
                query.setRange(range);
            }
            query.addSortColumn(new SortColumn("Certificates", "CERTIFICATE_RESOURCE_ID", true));
            dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            return dataObject;
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getADCertConfigDO:- ", ex);
            throw ex;
        }
    }
    
    public static DataObject getCertificateDO(final Long customerID) {
        final Criteria scepIDCri = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
        final DataObject scepDo = getCertificateDO(scepIDCri);
        return scepDo;
    }
    
    public static DataObject getCertificateDO(final Criteria certificateCri) {
        DataObject dobj = null;
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Certificates"));
            query.addJoin(new Join("Certificates", "SCEPConfigurations", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
            query.addJoin(new Join("Certificates", "ADCertConfiguration", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "AD_CONFIG_ID" }, 1));
            query.addJoin(new Join("Certificates", "CredentialCertificateInfo", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "CERTIFICATE_ID" }, 1));
            query.addSelectColumn(new Column("Certificates", "*"));
            query.addSelectColumn(new Column("SCEPConfigurations", "*"));
            query.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
            query.addSelectColumn(new Column("ADCertConfiguration", "*"));
            query.setCriteria(certificateCri);
            dobj = MDMUtil.getPersistence().get((SelectQuery)query);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return dobj;
    }
    
    public static JSONObject getSCEPConfigDetail(final Long scepConfigID, final Long customerId) {
        JSONObject scepConfig = null;
        final Criteria scepIDCriteria = new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepConfigID, 0);
        final Criteria customerIDCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria activeIDCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
        try {
            final DataObject scepDO = getSCEPConfigDO(scepIDCriteria.and(customerIDCriteria).and(activeIDCriteria), null);
            if (scepDO != null && !scepDO.isEmpty()) {
                final Row scepDetails = scepDO.getFirstRow("SCEPConfigurations");
                scepConfig = scepDetails.getAsJSON();
                if (scepConfig.opt("CHALLENGE_ENCRYPTED".toLowerCase()) != null) {
                    scepConfig.put("CHALLENGE".toLowerCase(), scepConfig.get("CHALLENGE_ENCRYPTED".toLowerCase()));
                }
            }
            final Row renewalRow = scepDO.getRow("SCEPRenewal");
            if (renewalRow != null) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("AUTO_RENEW_ENABLED", renewalRow.get("AUTO_RENEW_ENABLED"));
                jsonObject.put("NUM_DAYS", renewalRow.get("NUM_DAYS"));
                scepConfig.put("renewal_settings", (Object)jsonObject);
            }
            final Row challengeCredentailsRow = scepDO.getRow("ScepDyChallengeCredentials");
            if (challengeCredentailsRow != null) {
                includeChallengeDetails(scepConfig, challengeCredentailsRow);
            }
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getting SCEP Config", ex);
        }
        return scepConfig;
    }
    
    private static void includeChallengeDetails(final JSONObject scepConfig, final Row challengeCredentialsRow) {
        String domain = "";
        String username = (String)challengeCredentialsRow.get("SCEP_ADMIN_CHALLENGE_USERNAME");
        if (username.contains("\\")) {
            final String[] domainAndUser = username.split(Pattern.quote("\\"));
            domain = domainAndUser[0];
            username = domainAndUser[1];
        }
        scepConfig.put("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL", challengeCredentialsRow.get("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL"));
        scepConfig.put("domain".toUpperCase(), (Object)domain);
        scepConfig.put("SCEP_ADMIN_CHALLENGE_USERNAME", (Object)username);
        scepConfig.put("SCEP_ADMIN_CHALLENGE_PASSWORD", challengeCredentialsRow.get("SCEP_ADMIN_CHALLENGE_PASSWORD"));
    }
    
    public static JSONArray getSCEPConfigDetails(final Long customerId, final Criteria criteria, final Range range) throws Exception {
        final JSONArray scepArray = new JSONArray();
        Criteria customerIDCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
        try {
            final Criteria scepType = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)1, 0);
            if (criteria != null) {
                customerIDCriteria = customerIDCriteria.and(criteria);
            }
            final DataObject scepDO = getSCEPConfigDO(customerIDCriteria.and(scepType), range);
            if (scepDO != null && !scepDO.isEmpty()) {
                final Iterator iterator = scepDO.getRows("SCEPConfigurations");
                while (iterator.hasNext()) {
                    final Row scepRow = iterator.next();
                    final JSONObject scepJSON = scepRow.getAsJSON();
                    if (scepJSON.opt("CHALLENGE_ENCRYPTED".toLowerCase()) != null) {
                        scepJSON.put("CHALLENGE".toLowerCase(), scepJSON.get("CHALLENGE_ENCRYPTED".toLowerCase()));
                    }
                    if (scepJSON.length() != 0) {
                        final Row renewalRow = scepDO.getRow("SCEPRenewal", new Criteria(Column.getColumn("SCEPRenewal", "SCEP_CONFIG_ID"), scepRow.get("SCEP_CONFIG_ID"), 0));
                        if (renewalRow != null) {
                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("AUTO_RENEW_ENABLED", renewalRow.get("AUTO_RENEW_ENABLED"));
                            jsonObject.put("NUM_DAYS", renewalRow.get("NUM_DAYS"));
                            scepJSON.put("renewal_settings", (Object)jsonObject);
                        }
                        final Row challengeCredentailsRow = scepDO.getRow("ScepDyChallengeCredentials", new Criteria(Column.getColumn("ScepDyChallengeCredentials", "SCEP_CONFIG_ID"), scepRow.get("SCEP_CONFIG_ID"), 0));
                        if (challengeCredentailsRow != null) {
                            scepJSON.put("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL", challengeCredentailsRow.get("SCEP_ADMIN_CHALLENGE_ENDPOINT_URL"));
                            scepJSON.put("SCEP_ADMIN_CHALLENGE_USERNAME", challengeCredentailsRow.get("SCEP_ADMIN_CHALLENGE_USERNAME"));
                            scepJSON.put("SCEP_ADMIN_CHALLENGE_PASSWORD", challengeCredentailsRow.get("SCEP_ADMIN_CHALLENGE_PASSWORD"));
                        }
                        scepArray.put((Object)scepJSON);
                    }
                }
            }
            return scepArray;
        }
        catch (final DataAccessException e) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getting row from DO", (Throwable)e);
            throw e;
        }
    }
    
    public JSONArray getADCertConfigDetails(final Long customerID, final Criteria criteria, final Range range) throws Exception {
        final JSONArray adcsArray = new JSONArray();
        try {
            final DataObject adcsDO = getADCertConfigDO(customerID, criteria, range);
            if (adcsDO != null && !adcsDO.isEmpty()) {
                final Iterator iterator = adcsDO.getRows("ADCertConfiguration");
                while (iterator.hasNext()) {
                    final Row adcsRow = iterator.next();
                    final JSONObject adcsJSON = adcsRow.getAsJSON();
                    if (adcsJSON.length() != 0) {
                        adcsArray.put((Object)adcsJSON);
                    }
                }
            }
            return adcsArray;
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getADCertConfigDetails:- ", ex);
            throw ex;
        }
    }
    
    public int getADCertConfigCount(final Long customerID, final Criteria criteria) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "getADCertConfigCount:- for customerID={0}", customerID);
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Certificates"));
            query.addJoin(new Join("Certificates", "ADCertConfiguration", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "AD_CONFIG_ID" }, 2));
            Criteria customerIdCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria notTrashedCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
            final Criteria adCertTypeCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)5, 0);
            if (criteria != null) {
                customerIdCriteria = customerIdCriteria.and(criteria);
            }
            query.setCriteria(customerIdCriteria.and(adCertTypeCriteria).and(notTrashedCriteria));
            query.addSelectColumn(new Column("Certificates", "CERTIFICATE_RESOURCE_ID").distinct().count());
            return DBUtil.getRecordCount((SelectQuery)query);
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getADCertConfigCount:- ", ex);
            throw ex;
        }
    }
    
    public static int getSCEPCount(final Long customerId, final Criteria criteria) throws Exception {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Certificates"));
            query.addJoin(new Join("Certificates", "SCEPConfigurations", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "SCEP_CONFIG_ID" }, 2));
            query.addJoin(new Join("SCEPConfigurations", "SCEPServerToTemplate", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 2));
            query.addJoin(new Join("SCEPServerToTemplate", "SCEPServers", new String[] { "SCEP_SERVER_ID" }, new String[] { "SERVER_ID" }, 2));
            Criteria customerIdCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria notTrashedCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
            final Criteria scepCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)1, 0);
            if (criteria != null) {
                customerIdCriteria = customerIdCriteria.and(criteria);
            }
            query.setCriteria(customerIdCriteria.and(scepCriteria).and(notTrashedCriteria));
            query.addSelectColumn(new Column("Certificates", "CERTIFICATE_RESOURCE_ID").distinct().count());
            return DBUtil.getRecordCount((SelectQuery)query);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static Long addorUpdateSCEPConfiguration(final JSONObject scepConfigurationJSON) throws Exception {
        Long scepConfigID = null;
        try {
            scepConfigID = scepConfigurationJSON.optLong("SCEP_CONFIG_ID", 0L);
            final String scepConfigName = String.valueOf(scepConfigurationJSON.get("SCEP_CONFIGURATION_NAME"));
            final Long customerID = scepConfigurationJSON.getLong("CUSTOMER_ID");
            final Criteria scepIDCriteria = new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepConfigID, 0);
            final DataObject dobj = getSCEPConfigDO(scepIDCriteria, null);
            boolean isnew = false;
            Row scepRow = null;
            if (dobj.isEmpty()) {
                scepRow = new Row("SCEPConfigurations");
                scepRow.set("SCEP_CONFIG_ID", (Object)addOrUpdateCertificates(null, 1, customerID));
                isnew = true;
            }
            else {
                scepRow = dobj.getRow("SCEPConfigurations", scepIDCriteria);
            }
            if (scepRow != null) {
                scepRow.set("URL", (Object)String.valueOf(scepConfigurationJSON.get("URL")));
                scepRow.set("SCEP_CONFIGURATION_NAME", (Object)scepConfigName);
                scepRow.set("NAME", (Object)scepConfigurationJSON.getString("NAME"));
                scepRow.set("SUBJECT", (Object)scepConfigurationJSON.optString("SUBJECT", ""));
                scepRow.set("CHALLENGE_ENCRYPTED", (Object)scepConfigurationJSON.optString("CHALLENGE", (String)null));
                scepRow.set("SUBJECT_ALTNAME_TYPE", (Object)scepConfigurationJSON.getInt("SUBJECT_ALTNAME_TYPE"));
                scepRow.set("SUBJECT_ALTNAME_VALUE", (Object)scepConfigurationJSON.optString("SUBJECT_ALTNAME_VALUE", ""));
                scepRow.set("RETRIES", (Object)scepConfigurationJSON.optLong("RETRIES"));
                scepRow.set("RETRY_DELAY", (Object)scepConfigurationJSON.optLong("RETRY_DELAY"));
                scepRow.set("CHALLENGE_TYPE", (Object)scepConfigurationJSON.getInt("CHALLENGE_TYPE"));
                scepRow.set("KEY_SIZE", (Object)scepConfigurationJSON.getInt("KEY_SIZE"));
                scepRow.set("KEY_USAGE", (Object)scepConfigurationJSON.getInt("KEY_USAGE"));
                scepRow.set("CA_FINGER_PRINT", (Object)scepConfigurationJSON.optString("CA_FINGER_PRINT", ""));
                scepRow.set("NT_PRINCIPAL", (Object)scepConfigurationJSON.optString("NT_PRINCIPAL", ""));
            }
            if (isnew) {
                dobj.addRow(scepRow);
                MDMUtil.getPersistence().add(dobj);
                scepConfigID = (Long)scepRow.get("SCEP_CONFIG_ID");
            }
            else {
                dobj.updateRow(scepRow);
                MDMUtil.getPersistence().update(dobj);
            }
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in adding SCEP Configuration", ex);
            throw ex;
        }
        return scepConfigID;
    }
    
    public JSONObject addOrUpdateADCertConfiguration(final Long customerID, final Long adConfigID, final JSONObject adCertJSON) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "addOrUpdateADCertConfiguration:- for customerID={0}, adConfigID={1}, adCertJSON{2}", new Object[] { customerID, adConfigID, adCertJSON });
        DataObject dataObject = null;
        JSONObject adCertDbJSON = null;
        Boolean isUrlChanged = Boolean.FALSE;
        try {
            final String adConfigName = adCertJSON.getString("AD_CONFIG_NAME");
            if (getInstance().isADCertConfigNameExist(customerID, adConfigID, adConfigName)) {
                throw new APIHTTPException("ADCS0001", new Object[0]);
            }
            final Criteria adConfigIDCriteria = new Criteria(new Column("ADCertConfiguration", "AD_CONFIG_ID"), (Object)adConfigID, 0);
            dataObject = getADCertConfigDO(customerID, adConfigIDCriteria, null);
            boolean isNew = false;
            final String certServerAddress = adCertJSON.getString("CERT_SERVER_ADDRESS");
            Row adCertConfigRow = null;
            if (dataObject.isEmpty()) {
                adCertConfigRow = new Row("ADCertConfiguration");
                adCertConfigRow.set("AD_CONFIG_ID", (Object)addOrUpdateCertificates(null, 5, customerID));
                isNew = true;
            }
            else {
                adCertConfigRow = dataObject.getRow("ADCertConfiguration");
                final String dbCertServerAddress = adCertConfigRow.get("CERT_SERVER_ADDRESS").toString();
                if (!certServerAddress.equalsIgnoreCase(dbCertServerAddress)) {
                    isUrlChanged = Boolean.TRUE;
                }
            }
            if (adCertConfigRow != null) {
                adCertConfigRow.set("AD_CONFIG_NAME", (Object)adConfigName);
                adCertConfigRow.set("CERT_SERVER_ADDRESS", (Object)certServerAddress);
                adCertConfigRow.set("DESCRIPTION", (Object)adCertJSON.optString("DESCRIPTION", "--"));
                adCertConfigRow.set("CERT_AUTHORITY", (Object)adCertJSON.getString("CERT_AUTHORITY"));
                adCertConfigRow.set("CERT_TEMPLATE_NAME", (Object)adCertJSON.getString("CERT_TEMPLATE_NAME"));
                adCertConfigRow.set("CERT_EXP_NOTIFY_THREAD", (Object)adCertJSON.optInt("CERT_EXP_NOTIFY_THREAD"));
                adCertConfigRow.set("RSA_KEY_SIZE", (Object)adCertJSON.optInt("RSA_KEY_SIZE"));
                adCertConfigRow.set("AUTO_RENEW_ENABLED", (Object)adCertJSON.optBoolean("AUTO_RENEW_ENABLED", false));
                adCertConfigRow.set("ALLOW_ALL_APPS_TO_ACCESS", (Object)adCertJSON.optBoolean("ALLOW_ALL_APPS_TO_ACCESS", false));
                adCertConfigRow.set("IS_KEY_EXTRACTABLE", (Object)adCertJSON.optBoolean("IS_KEY_EXTRACTABLE", false));
            }
            if (isNew) {
                ProfileCertificateUtil.logger.log(Level.INFO, "addOrUpdateADCertConfiguration:- inserting new row in ADCERTCONFIGURATION");
                dataObject.addRow(adCertConfigRow);
                MDMUtil.getPersistence().add(dataObject);
            }
            else {
                ProfileCertificateUtil.logger.log(Level.INFO, "addOrUpdateADCertConfiguration:- updating the row in ADCERTCONFIGURATION");
                dataObject.updateRow(adCertConfigRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            adCertDbJSON = dataObject.getFirstRow("ADCertConfiguration").getAsJSON();
            adCertDbJSON.put("isUrlChanged", (Object)isUrlChanged);
            return adCertDbJSON;
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateADCertConfiguration:- ", ex);
            throw ex;
        }
    }
    
    public static boolean checkSCEPConfigName(final String configName, final Long scepId) throws Exception {
        final int scepNameCount = DBUtil.getRecordCount("SCEPConfigurations", "SCEP_CONFIGURATION_NAME", new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"), (Object)configName, 0, false).and(new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepId, 1)));
        return scepNameCount > 0;
    }
    
    public boolean isADCertConfigNameExist(final Long customerID, final Long adConfigID, final String configName) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "isADCertConfigNameExist:- for customerID={}, adConfigID={1}, configName={2}", new Object[] { customerID, adConfigID, configName });
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ADCertConfiguration"));
            query.addJoin(new Join("ADCertConfiguration", "Certificates", new String[] { "AD_CONFIG_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
            final Criteria configNameCriteria = new Criteria(new Column("ADCertConfiguration", "AD_CONFIG_NAME"), (Object)configName, 0, false);
            final Criteria customerIDCriteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria isActiveCriteria = new Criteria(new Column("Certificates", "IS_ACTIVE"), (Object)true, 0);
            final Criteria configIDCriteria = new Criteria(new Column("ADCertConfiguration", "AD_CONFIG_ID"), (Object)adConfigID, 1);
            query.setCriteria(configNameCriteria.and(customerIDCriteria).and(isActiveCriteria).and(configIDCriteria));
            final int count = DBUtil.getRecordCount(query, "ADCertConfiguration", "AD_CONFIG_NAME");
            return count > 0;
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "exception in isADCertConfigNameExist ", ex);
            throw ex;
        }
    }
    
    public static JSONObject addCredentials(final JSONObject certificateDetails) throws JSONException {
        final String filePath = String.valueOf(certificateDetails.get("CERTIFICATE_FILE_UPLOAD"));
        final JSONObject responseJSON = new JSONObject();
        String certFileName = ApiFactoryProvider.getFileAccessAPI().getFileName(filePath);
        final String certPassword = certificateDetails.getString("CERTIFICATE_PASSWORD");
        final Long customerId = certificateDetails.getLong("CUSTOMER_ID");
        final int certificateType = certificateDetails.getInt("CERTIFICATE_TYPE");
        Long certificateID = -1L;
        String destFolder = "";
        boolean fileUploaded = false;
        if (!certFileName.trim().equals("")) {
            try {
                destFolder = MDMUtil.getCredentialCertificateFolder(customerId);
                if (ApiFactoryProvider.getFileAccessAPI().isFileExists(destFolder + File.separator + certFileName)) {
                    final String[] tokens = certFileName.split("\\.(?=[^\\.]+$)");
                    certFileName = tokens[0].trim() + System.currentTimeMillis() + "." + tokens[1];
                }
                fileUploaded = MDMFileUtil.uploadFileToDirectory(filePath, destFolder, certFileName);
                if (fileUploaded) {
                    final JSONObject jsonObject = CredentialsMgmtAction.extractCertificateDetails(destFolder + File.separator + certFileName, certPassword);
                    final String commonName = jsonObject.optString("CommonName");
                    jsonObject.put("certFileName", (Object)certFileName);
                    if (certFileName.contains(".p7b")) {
                        if (certPassword.isEmpty()) {
                            jsonObject.put("certPassword", (Object)CertificateUtil.getInstance().generateRandomCertPassword(10));
                        }
                    }
                    else {
                        jsonObject.put("certPassword", (Object)certPassword);
                    }
                    jsonObject.put("customerId", (Object)customerId);
                    jsonObject.put("certificateType", certificateType);
                    certificateID = getInstance().addOrUpdateCredentialCertificateDetails(jsonObject);
                    if (certificateID != null && certificateID == -2L) {
                        responseJSON.put("ERROR_CODE", (Object)(CredentialsMgmtAction.CERTIFICATE_EXPIRED_ERROR_CODE + ""));
                        responseJSON.put("MESSAGE", (Object)I18N.getMsg("dc.mdm.profile.CERTIFICATE_HAS_EXPIRED", new Object[0]));
                        getInstance().removeCertificateFile(destFolder + File.separator + certFileName);
                    }
                    else if (certificateID != null) {
                        responseJSON.put("ERROR_CODE", (Object)(CredentialsMgmtAction.CERTIFICATE_ADDED_ERROR_CODE + ""));
                        responseJSON.put("MESSAGE", (Object)I18N.getMsg("dc.mdm.profile.CERTIFICATE_ADDED_SUCCESSFULLY", new Object[0]));
                        responseJSON.put("CERTIFICATE_NAME", (Object)commonName);
                        responseJSON.put("CERTIFICATE_ID", (Object)String.valueOf(certificateID));
                        certificateDetails.put("CERTIFICATE_ID", (Object)certificateID);
                        certificateDetails.put("CERTIFICATE_PASSWORD", (Object)certPassword);
                    }
                    else {
                        responseJSON.put("ERROR_CODE", (Object)(CredentialsMgmtAction.CERTIFICATE_ALREADY_EXIST_ERROR_CODE + ""));
                        responseJSON.put("MESSAGE", (Object)I18N.getMsg("dc.mdm.profile.CERTIFICATE_ALREADY_ADDED", new Object[0]));
                    }
                }
            }
            catch (final IOException ex) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "IO Exception while uploading Credential Certificate", ex);
                responseJSON.put("ERROR_CODE", (Object)(CredentialsMgmtAction.CERTIFICATE_PASSWORD_ERROR + ""));
                try {
                    if (certificateID != -1L) {
                        getInstance().removeCredentialCertificateDetails(certificateID);
                        if (certificateType == 4) {
                            MdmCsrDbHandler.deleteCsrToCertRel(certificateID);
                        }
                    }
                    if (fileUploaded) {
                        getInstance().removeCertificateFile(destFolder + File.separator + certFileName);
                    }
                    responseJSON.put("MESSAGE", (Object)I18N.getMsg("dc.mdm.profile.UNABLE_TO_ADD_VERIFY_CERTIFICATE_OR_PASSWORD", new Object[0]));
                }
                catch (final Exception ex2) {
                    ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Certificate File ", ex2);
                }
            }
            catch (final APIHTTPException e) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Credential Certificate", e);
                try {
                    if (certificateID != -1L) {
                        getInstance().removeCredentialCertificateDetails(certificateID);
                    }
                    if (fileUploaded) {
                        getInstance().removeCertificateFile(destFolder + File.separator + certFileName);
                    }
                }
                catch (final Exception ex2) {
                    ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Certificate File ", ex2);
                }
            }
            catch (final Exception ex3) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Credential Certificate", ex3);
                responseJSON.put("ERROR_CODE", (Object)(CredentialsMgmtAction.CERTIFICATE_PARSING_ERROR_CODE + ""));
                try {
                    if (certificateID != -1L) {
                        getInstance().removeCredentialCertificateDetails(certificateID);
                    }
                    if (fileUploaded) {
                        getInstance().removeCertificateFile(destFolder + File.separator + certFileName);
                    }
                    responseJSON.put("MESSAGE", (Object)I18N.getMsg("dc.mdm.profile.unable_to_add_verify_file", new Object[0]));
                }
                catch (final Exception ex2) {
                    ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Certificate File ", ex2);
                }
            }
            certificateDetails.put("UpdateStatus", (Object)responseJSON);
        }
        else {
            final Properties msgProps = new Properties();
            final ArrayList arrStatus = new ArrayList();
            ((Hashtable<String, String>)msgProps).put("ERROR_CODE", CredentialsMgmtAction.CERTIFICATE_PARSING_ERROR_CODE + "");
            try {
                ((Hashtable<String, String>)msgProps).put("MESSAGE", I18N.getMsg("dc.mdm.profile.unable_to_add_verify_file", new Object[0]));
            }
            catch (final Exception e2) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Certificate File ", e2);
            }
            arrStatus.add(msgProps);
            certificateDetails.put("UpdateStatus", (Collection)arrStatus);
        }
        return certificateDetails;
    }
    
    private static DataObject getCertificatePasswordDO(final Long certificateID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
        selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
        final Criteria certCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certificateID, 0);
        selectQuery.setCriteria(certCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public JSONObject deleteCredentials(final JSONArray certificateIds, final Long customerID, final Long userID, final Boolean isRedistribute) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "Request To delete Certificates is recieved for {0} customer ID is {1} the user performing the delete action is {2} Redistribute flag is {3}", new Object[] { certificateIds, customerID, userID, isRedistribute });
        final List idList = new ArrayList();
        for (int i = 0; i < certificateIds.length(); ++i) {
            idList.add(Long.parseLong(certificateIds.get(i).toString()));
        }
        if (!getInstance().validateCertIdsToCustomer(customerID, idList)) {
            throw new APIHTTPException("COM0008", new Object[] { String.valueOf(idList) });
        }
        new ProfilePayloadOperator(this.certificatesMapptedTableList, this.unConfigureMap).performPayloadOperation(idList, customerID, userID, null, Boolean.TRUE, isRedistribute);
        this.moveCertificatesToTrash(idList, customerID);
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", (Object)Boolean.TRUE);
        jsonObject.put("CERTIFICATE_RESOURCE_ID", (Object)certificateIds);
        ProfileCertificateUtil.logger.log(Level.INFO, "the certificates Ids were moved to trash successfully {0}", jsonObject);
        return jsonObject;
    }
    
    public JSONObject modifyCredentials(final HashMap replacementCerts, final Long customerID, final Long userID, final Boolean isRedistribute) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "Request To Modify  Certificates is recieved for {0} customer ID is {1} the user performing the delete action is {2} redistribtuiion flag is {3}", new Object[] { replacementCerts, customerID, userID, isRedistribute });
        for (final Long newCertID : replacementCerts.keySet()) {
            final List oldCertList = replacementCerts.get(newCertID);
            new ProfilePayloadOperator(this.certificatesMapptedTableList, this.unConfigureMap).performPayloadOperation(oldCertList, customerID, userID, newCertID, Boolean.FALSE, isRedistribute);
            this.moveCertificatesToTrash(oldCertList, customerID);
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", (Object)Boolean.TRUE);
        ProfileCertificateUtil.logger.log(Level.INFO, "the certificates Ids were modified succesfully {0}", replacementCerts);
        return jsonObject;
    }
    
    public void moveCertificatesToTrash(final List idList, final Long customerID) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Certificates");
        updateQuery.setCriteria(new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)idList.toArray(), 8).and(new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0)));
        updateQuery.setUpdateColumn("IS_ACTIVE", (Object)false);
        MDMUtil.getPersistenceLite().update(updateQuery);
        ProfileCertificateUtil.logger.log(Level.INFO, "updated the trash flag for the following certificates {0}", idList);
    }
    
    public static JSONObject getCertificateFileJSON(final Long certificateID) {
        final JSONObject responseJSON = new JSONObject();
        try {
            final DataObject certDO = getCertificateDO(new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0));
            if (certDO != null && !certDO.isEmpty() && certDO.containsTable("CredentialCertificateInfo")) {
                final Row credentialROW = certDO.getRow("CredentialCertificateInfo");
                return MDMDBUtil.rowToJSON(credentialROW);
            }
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while uploading Certificate File ", ex);
        }
        return responseJSON;
    }
    
    public static JSONArray getCertificateDetails(final Long customerId, final Criteria additionalCriteria, final Range range) throws DataAccessException, JSONException {
        final JSONArray certificateArray = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
            selectQuery.addJoin(new Join("CredentialCertificateInfo", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "*"));
            Criteria customerCriteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            if (additionalCriteria != null) {
                customerCriteria = customerCriteria.and(additionalCriteria);
            }
            if (range != null) {
                selectQuery.setRange(range);
            }
            selectQuery.addSortColumn(new SortColumn("CredentialCertificateInfo", "CERTIFICATE_ID", true));
            selectQuery.setCriteria(customerCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
                while (iterator.hasNext()) {
                    final Row certificateRow = iterator.next();
                    final JSONObject certificate = convertCertificateRowToJSON(certificateRow);
                    if (certificate.length() != 0) {
                        certificateArray.put((Object)certificate);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            ProfileCertificateUtil.logger.log(Level.INFO, "Exception in getting all certificate details", (Throwable)e);
            throw e;
        }
        return certificateArray;
    }
    
    public static SelectQuery getCredentialCertificateQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
        selectQuery.addJoin(new Join("CredentialCertificateInfo", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
        return selectQuery;
    }
    
    public DataObject getCredentialCertificateDataObject(final Long customerID, final Long certID, final Criteria crit) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "getCredentialCertificateDataObject():- for customerID={0} & certID={1}", new Object[] { customerID, certID });
        try {
            final SelectQuery selectQuery = getCredentialCertificateQuery();
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_FILE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_PASSWORD"));
            selectQuery.addSelectColumn(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Certificates", "CERTIFICATE_TYPE"));
            Criteria criteria = new Criteria(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certID, 0).and(new Criteria(Column.getColumn("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerID, 0));
            if (crit != null) {
                criteria = criteria.and(crit);
            }
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Unknown certificate id");
                throw new APIHTTPException("COM0008", new Object[] { "certificate_id: " + certID });
            }
            return dataObject;
        }
        catch (final Exception e) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "getCredentialCertificateDataObject():- Exception is ", e);
            throw e;
        }
    }
    
    public static int getCertificateCount(final Long customerId, final Criteria criteria) throws Exception {
        try {
            final SelectQuery selectQuery = getCredentialCertificateQuery();
            selectQuery.addSelectColumn(new Column("CredentialCertificateInfo", "CERTIFICATE_ID").distinct().count());
            Criteria customerCriteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            if (criteria != null) {
                customerCriteria = customerCriteria.and(criteria);
            }
            selectQuery.setCriteria(customerCriteria);
            return DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static JSONObject getCertificateDetail(final Long customerId, final Long certificateId) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria customerIdCriteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria certificateIdCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)certificateId, 0);
            selectQuery.setCriteria(customerIdCriteria.and(certificateIdCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row certificateRow = dataObject.getRow("CredentialCertificateInfo");
                return convertCertificateRowToJSON(certificateRow);
            }
        }
        catch (final DataAccessException e) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getting certificate details", (Throwable)e);
            throw e;
        }
        return new JSONObject();
    }
    
    public static JSONObject convertCertificateRowToJSON(Row row) {
        final JSONObject certificateObj = new JSONObject();
        try {
            if (row != null) {
                final String serialNumber = (String)row.get("CERTIFICATE_SERIAL_NUMBER");
                final Long certificateId = (Long)row.get("CERTIFICATE_ID");
                final Long customerId = (Long)row.get("CUSTOMER_ID");
                if (MDMStringUtils.isEmpty(serialNumber)) {
                    final DataObject dataObject = updateCredentialCertiInfoTable(customerId, certificateId);
                    row = dataObject.getRow("CredentialCertificateInfo");
                }
                certificateObj.put("CERTIFICATE_ID", (Object)certificateId);
                certificateObj.put("CERTIFICATE_NAME", row.get("CERTIFICATE_DISPLAY_NAME"));
                certificateObj.put("CERTIFICATE_SERIAL_NUMBER", row.get("CERTIFICATE_SERIAL_NUMBER"));
                certificateObj.put("CERTIFICATE_SUBJECT_DN", row.get("CERTIFICATE_SUBJECT_DN"));
                certificateObj.put("CERTIFICATE_NOTBEFORE", row.get("CERTIFICATE_NOTBEFORE"));
                certificateObj.put("CERTIFICATE_NOTAFTER", row.get("CERTIFICATE_NOTAFTER"));
                certificateObj.put("CERTIFICATE_ISSUER_DN", row.get("CERTIFICATE_ISSUER_DN"));
                certificateObj.put("CERTIFICATE_FILE_NAME", row.get("CERTIFICATE_FILE_NAME"));
                certificateObj.put("CERTIFICATE_THUMBPRINT", row.get("CERTIFICATE_THUMBPRINT"));
                final LdapName subjectDistinguishedName = new LdapName((String)row.get("CERTIFICATE_SUBJECT_DN"));
                final List<Rdn> subjectRdns = subjectDistinguishedName.getRdns();
                for (final Rdn rdn : subjectRdns) {
                    final String type = rdn.getType();
                    if (type.equalsIgnoreCase("O")) {
                        certificateObj.put("CERTIFICATE_ORG", (Object)rdn.getValue());
                    }
                }
                final LdapName issuerDistinguishedName = new LdapName((String)row.get("CERTIFICATE_ISSUER_DN"));
                final List<Rdn> issuerRdns = issuerDistinguishedName.getRdns();
                for (final Rdn rdn2 : issuerRdns) {
                    final String type2 = rdn2.getType();
                    if (type2.equalsIgnoreCase("O")) {
                        certificateObj.put("ISSUER_ORG", (Object)rdn2.getValue());
                    }
                    else {
                        if (!type2.equalsIgnoreCase("CN")) {
                            continue;
                        }
                        certificateObj.put("ISSUER_NAME", (Object)rdn2.getValue());
                    }
                }
                final Long expiryDate = (Long)row.get("CERTIFICATE_NOTAFTER");
                final Long diff = expiryDate - System.currentTimeMillis();
                certificateObj.put("DAYS_TO_EXPIRE", diff / 86400000L + 1L);
                final String password = (String)row.get("CERTIFICATE_PASSWORD");
                certificateObj.put("IS_IDENTITY", !MDMStringUtils.isEmpty(password));
            }
        }
        catch (final InvalidNameException e) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in getting ldap name in certificate info", e);
        }
        catch (final JSONException e2) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "JSON exception in certificate info", (Throwable)e2);
        }
        catch (final DataAccessException e3) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in updating certificate serial info", (Throwable)e3);
        }
        return certificateObj;
    }
    
    public String getCertificataeContentType(final String filename) throws Exception {
        final String[] fileNameSplit = filename.split("\\.");
        String certificateExtn = fileNameSplit[fileNameSplit.length - 1];
        final String lowerCase;
        certificateExtn = (lowerCase = certificateExtn.toLowerCase());
        switch (lowerCase) {
            case "p12": {
                return "application/x-pkcs12";
            }
            case "pem": {
                return "application/x-pem-file";
            }
            case "pfx": {
                return "application/x-pkcs12";
            }
            case "crt": {
                return "application/x-x509-ca-cert";
            }
            case "cer": {
                return "application/x-x509-ca-cert";
            }
            case "der": {
                return "application/x-x509-ca-cert";
            }
            default: {
                return "application/x-x509-ca-cert";
            }
        }
    }
    
    public static Long getScepIdForCollectionId(final Long collectionId) throws Exception {
        Long scepId = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        final Join join = new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
        final Join join2 = new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
        final Join join3 = new Join("ConfigDataItem", "SCEPPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
        final Criteria collectionIdCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria iosCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)516, 0);
        final Criteria androidCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)566, 0);
        final Criteria windowsCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)606, 0);
        final Criteria macCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)773, 0);
        selectQuery.setCriteria(collectionIdCriteria.and(iosCriteria.or(androidCriteria).or(windowsCriteria).or(macCriteria)));
        selectQuery.addJoin(join);
        selectQuery.addJoin(join2);
        selectQuery.addJoin(join3);
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
        selectQuery.addSelectColumn(new Column("ConfigData", "*"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
        selectQuery.addSelectColumn(new Column("SCEPPolicy", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("SCEPPolicy");
            scepId = (Long)row.get("SCEP_CONFIG_ID");
        }
        return scepId;
    }
    
    public static Criteria getCertificateCriteria(final Long certificateId) {
        final Criteria criteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateId, 0);
        return criteria;
    }
    
    public static Criteria getCertificateTypeCriteria(final int certificateType) {
        final Criteria criteria = new Criteria(new Column("Certificates", "CERTIFICATE_TYPE"), (Object)certificateType, 0);
        return criteria;
    }
    
    public static Criteria getCustomerIdCriteria(final Long customerId) {
        final Criteria criteria = new Criteria(new Column("Certificates", "CUSTOMER_ID"), (Object)customerId, 0);
        return criteria;
    }
    
    public JSONArray getAssociatedProfileDetails(final List certificateIDs) throws Exception {
        final SelectQuery selectQuery = this.getAssociatedProfilesSelectQuery(certificateIDs);
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigData", "LABEL"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONArray jsonArray = new JSONArray();
        final HashMap hashMap = new HashMap();
        while (dmDataSetWrapper.next()) {
            final Long collectionID = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
            List list = hashMap.get(collectionID);
            if (list == null) {
                list = new ArrayList();
            }
            list.add(dmDataSetWrapper.getValue("LABEL"));
            hashMap.put(collectionID, list);
        }
        final Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            final JSONObject profileJSON = new JSONObject();
            final Long collectionID2 = iterator.next();
            profileJSON.put("COLLECTION_ID", (Object)collectionID2);
            final JSONArray configs = new JSONArray((Collection)hashMap.get(collectionID2));
            profileJSON.put("configured_payloads", (Object)configs);
            jsonArray.put((Object)profileJSON);
        }
        return jsonArray;
    }
    
    public SelectQuery getAssociatedProfilesSelectQuery(final List certificateIDs) {
        final SelectQuery subSelectQuery = this.getCertConfigSelectQuery(certificateIDs);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        final DerivedColumn derivedColumn = new DerivedColumn("CertConfigDataItems", subSelectQuery);
        selectQuery.setCriteria(new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)derivedColumn, 8));
        return selectQuery;
    }
    
    public SelectQuery getCertConfigSelectQuery(final List certificateIDs) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ConfigDataItem"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        Criteria criteria = null;
        for (final CertificateMapping certificateMapping : this.certificatesMapptedTableList) {
            if (!selectQuery.getTableList().contains(Table.getTable(certificateMapping.getTableName()))) {
                certificateMapping.addCfgDataItemJoin(selectQuery, 1);
            }
            if (criteria == null) {
                criteria = certificateMapping.getCriteria(certificateIDs);
            }
            else {
                criteria = criteria.or(certificateMapping.getCriteria(certificateIDs));
            }
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public Long getServerIDFromURL(final String url, final String name, final String caThumbprint, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPServers"));
        selectQuery.addSelectColumn(Column.getColumn("SCEPServers", "*"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("SCEPServers", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria urlCriteria = new Criteria(Column.getColumn("SCEPServers", "URL"), (Object)url, 0);
        selectQuery.setCriteria(customerCriteria.and(urlCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            final Row row = new Row("SCEPServers");
            row.set("URL", (Object)url);
            if (url.contains("certsrv/mscep")) {
                row.set("TYPE", (Object)ScepServerType.ADCS.type);
            }
            else {
                row.set("TYPE", (Object)ScepServerType.GENERIC.type);
            }
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("CA_FINGER_PRINT", (Object)caThumbprint);
            row.set("SERVER_NAME", (Object)name);
            dataObject.addRow(row);
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        final Row row = dataObject.getFirstRow("SCEPServers");
        ProfileCertificateUtil.logger.log(Level.INFO, "the server mapped to the URL is returned server {1}", new Object[] { row.get("SERVER_ID") });
        return (Long)row.get("SERVER_ID");
    }
    
    public void addServerToTemplateMapping(final Long serverID, final Long templateID) throws DataAccessException {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row row = new Row("SCEPServerToTemplate");
        row.set("SCEP_CONFIG_ID", (Object)templateID);
        row.set("SCEP_SERVER_ID", (Object)serverID);
        dataObject.addRow(row);
        MDMUtil.getPersistenceLite().add(dataObject);
        ProfileCertificateUtil.logger.log(Level.INFO, "mapped the server  {0} with the template {1}", new Object[] { serverID, templateID });
    }
    
    public void handleAutoRenewalForCustomer(final Long customerID) throws DataAccessException {
        ProfileCertificateUtil.logger.log(Level.INFO, "inside handleAutoRenewalForCustomer customer {0}", customerID);
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Certificates"));
        selectQuery.addJoin(new Join("Certificates", "SCEPRenewal", new String[] { "CERTIFICATE_RESOURCE_ID" }, new String[] { "SCEP_CONFIG_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("SCEPRenewal", "*"));
        Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
        customerCriteria = customerCriteria.and(new Criteria(Column.getColumn("Certificates", "IS_ACTIVE"), (Object)true, 0));
        final Criteria renewCriteria = new Criteria(Column.getColumn("SCEPRenewal", "AUTO_RENEW_ENABLED"), (Object)true, 0);
        selectQuery.setCriteria(customerCriteria.and(renewCriteria));
        final DataObject dataObject = MDMUtil.getReadOnlyPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("SCEPRenewal");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long scepID = (Long)row.get("SCEP_CONFIG_ID");
            final Long userID = (Long)row.get("USER_ID");
            final List idList = new ArrayList();
            idList.add(scepID);
            final Integer numdays = (Integer)row.get("NUM_DAYS");
            if (numdays != null) {
                if (numdays <= 0) {
                    continue;
                }
                ProfileCertificateUtil.logger.log(Level.INFO, "SCEPID choosen for renewal {0} with num days is {1}", new Object[] { scepID, numdays });
                selectQuery = this.getAssociatedProfilesSelectQuery(idList);
                selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
                selectQuery.addJoin(new Join("ProfileToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("CollnToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0)));
                final DataObject collndataObject = MDMUtil.getReadOnlyPersistence().get(selectQuery);
                final Iterator collnItr = collndataObject.getRows("ProfileToCollection");
                final Date currentDate = new Date();
                final long currentDateInLong = currentDate.getTime();
                final long renewaloffset = numdays * 24L * 60L * 60L * 1000L;
                final Criteria expiryCriteria = new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_EXPIRE"), (Object)new Long[] { currentDateInLong, currentDateInLong + renewaloffset }, 14);
                final Criteria expiredCriteria = new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_EXPIRE"), (Object)currentDateInLong, 7);
                final Criteria resCustCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                while (collnItr.hasNext()) {
                    final HashMap redistribueProfileCollectionMap = new HashMap();
                    final Row collnRow = collnItr.next();
                    final Long collectionID = (Long)collnRow.get("COLLECTION_ID");
                    final Long profileID = (Long)collnRow.get("PROFILE_ID");
                    final List resList = this.getCollnDistributedCertificateResources(collectionID, expiryCriteria.or(expiredCriteria).and(resCustCriteria));
                    redistribueProfileCollectionMap.put(profileID, collectionID);
                    final Properties associationParams = new Properties();
                    ((Hashtable<String, Boolean>)associationParams).put("isAppConfig", false);
                    ((Hashtable<String, Long>)associationParams).put("customerId", customerID);
                    ((Hashtable<String, HashMap>)associationParams).put("profileCollectionMap", redistribueProfileCollectionMap);
                    ((Hashtable<String, Long>)associationParams).put("loggedOnUser", userID);
                    ((Hashtable<String, Boolean>)associationParams).put("associateToDevice", true);
                    ((Hashtable<String, String>)associationParams).put("commandName", "InstallProfile");
                    ((Hashtable<String, List>)associationParams).put("resourceList", resList);
                    if (resList.size() > 0) {
                        ProfileCertificateUtil.logger.log(Level.INFO, "Renewal profile going to associate for colln : {0} and profile : {1} and resList {2}", new Object[] { collectionID, profileID, resList });
                        new ProfileAssociateHandler().associateCollectionForResource(associationParams);
                    }
                }
            }
        }
    }
    
    private List getCollnDistributedCertificateResources(final Long collectionID, final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCertificateResourceRel"));
        selectQuery.addJoin(new Join("MdCertificateResourceRel", "MdCertificateInfo", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
        selectQuery.addJoin(new Join("MdCertificateResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria scepIDCriteria = new Criteria(Column.getColumn("MdCertificateInfo", "CERTIFICATE_SUBJECT_DN"), (Object)collectionID.toString(), 12);
        final Criteria manageddeviceJoinCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0);
        selectQuery.addJoin(new Join("ManagedDevice", "RecentProfileForResource", scepIDCriteria.and(manageddeviceJoinCriteria), 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria statusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria collnIDCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria successCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)6, 0);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        selectQuery.setCriteria(statusCriteria.and(scepIDCriteria).and(criteria).and(successCriteria).and(collnIDCriteria).and(markedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdCertificateResourceRel", "*"));
        final DataObject dataObject = MDMUtil.getReadOnlyPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdCertificateResourceRel");
        final List resList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            resList.add(row.get("RESOURCE_ID"));
        }
        return resList;
    }
    
    public void addOrUpdateSCEPRenewalSettings(final Long scepId, final Long userID, final JSONObject settings) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPRenewal"));
        selectQuery.addSelectColumn(Column.getColumn("SCEPRenewal", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("SCEPRenewal", "SCEP_CONFIG_ID"), (Object)scepId, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Row row = dataObject.getRow("SCEPRenewal");
        if (row == null) {
            row = new Row("SCEPRenewal");
            row.set("SCEP_CONFIG_ID", (Object)scepId);
            row.set("NUM_DAYS", (Object)settings.optInt("NUM_DAYS".toLowerCase(), -1));
            row.set("AUTO_RENEW_ENABLED", settings.get("AUTO_RENEW_ENABLED".toLowerCase()));
            row.set("USER_ID", (Object)userID);
            dataObject.addRow(row);
            ProfileCertificateUtil.logger.log(Level.INFO, "SCEP renewal settings added {0}", row);
        }
        else {
            ProfileCertificateUtil.logger.log(Level.INFO, "SCEP renewal settings before update {0}", row);
            row.set("NUM_DAYS", (Object)settings.optInt("NUM_DAYS".toLowerCase(), -1));
            row.set("AUTO_RENEW_ENABLED", settings.get("AUTO_RENEW_ENABLED".toLowerCase()));
            ProfileCertificateUtil.logger.log(Level.INFO, "SCEP renewal settings updated {0}", row);
            dataObject.updateRow(row);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
    
    public String getCertNames(final JSONArray certificateIds, final Long customerID) throws DataAccessException {
        final List list = new ArrayList();
        for (int i = 0; i < certificateIds.length(); ++i) {
            list.add(certificateIds.get(i));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
        selectQuery.addJoin(new Join("CredentialCertificateInfo", "Certificates", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_DISPLAY_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CredentialCertificateInfo", "CERTIFICATE_ID"), (Object)list.toArray(), 8).and(new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        StringBuilder retVal = new StringBuilder();
        final Iterator iterator = dataObject.getRows("CredentialCertificateInfo");
        while (iterator.hasNext()) {
            if (retVal.length() == 0) {
                retVal = new StringBuilder((String)iterator.next().get("CERTIFICATE_DISPLAY_NAME"));
            }
            else {
                retVal.append(", ").append((String)iterator.next().get("CERTIFICATE_DISPLAY_NAME"));
            }
        }
        return retVal.toString();
    }
    
    public String getTemplateNames(final JSONArray certificateIds, final Long customerID) throws DataAccessException {
        final List list = new ArrayList();
        for (int i = 0; i < certificateIds.length(); ++i) {
            list.add(certificateIds.get(i));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPConfigurations"));
        selectQuery.addJoin(new Join("SCEPConfigurations", "Certificates", new String[] { "SCEP_CONFIG_ID" }, new String[] { "CERTIFICATE_RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("SCEPConfigurations", "SCEP_CONFIG_ID"));
        selectQuery.addSelectColumn(Column.getColumn("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)list.toArray(), 8).and(new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        StringBuilder retVal = new StringBuilder();
        final Iterator iterator = dataObject.getRows("SCEPConfigurations");
        while (iterator.hasNext()) {
            if (retVal.length() == 0) {
                retVal = new StringBuilder((String)iterator.next().get("SCEP_CONFIGURATION_NAME"));
            }
            else {
                retVal.append(", ").append((String)iterator.next().get("SCEP_CONFIGURATION_NAME"));
            }
        }
        return retVal.toString();
    }
    
    public Boolean validateCertIdsToCustomer(final Long customerID, final List certIds) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Certificates"));
        final Criteria idsCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certIds.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Certificates", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.addSelectColumn(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"));
        selectQuery.setCriteria(idsCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("Certificates");
        final List list = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            list.add(row.get("CERTIFICATE_RESOURCE_ID"));
        }
        return list.size() == certIds.size();
    }
    
    public void updateCertificateIsActive(final Long certID, final boolean isActive) throws Exception {
        ProfileCertificateUtil.logger.log(Level.INFO, "updateCertificateIsActive(): updating is_active status in certificates table for certID={0}, isActive={1}", new Object[] { certID, isActive });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Certificates");
        final Criteria certCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certID, 0);
        updateQuery.setCriteria(certCriteria);
        updateQuery.setUpdateColumn("IS_ACTIVE", (Object)isActive);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    public static X509Certificate readX509Certificate(final String apnsCertificateFilePath) {
        X509Certificate apnsCertificate = null;
        InputStream stream = null;
        try {
            stream = ApiFactoryProvider.getFileAccessAPI().readFile(apnsCertificateFilePath);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            apnsCertificate = (X509Certificate)certFactory.generateCertificate(stream);
        }
        catch (final Exception exp) {
            ProfileCertificateUtil.logger.log(Level.WARNING, "Exception while parsing Certificate", exp);
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Exception closing InputStream stream", ex);
            }
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex2) {
                ProfileCertificateUtil.logger.log(Level.WARNING, "Exception closing InputStream stream", ex2);
            }
        }
        return apnsCertificate;
    }
    
    public static CredentialCertificate getCACertDetails(final long customerId, final long caCertID) throws DataAccessException {
        if (caCertID == 0L) {
            return null;
        }
        final Criteria caCertCriteria = new Criteria(Column.getColumn("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)caCertID, 0);
        final JSONArray caCertDetailsJson = getCertificateDetails(customerId, caCertCriteria, new Range(1, 1));
        if (caCertDetailsJson.length() == 0) {
            throw new APIHTTPException("COM0005", new Object[] { "CA Certificate ID" });
        }
        final JSONObject certJson = caCertDetailsJson.getJSONObject(0);
        final CredentialCertificate credentialCertificate = new CredentialCertificate();
        credentialCertificate.setCertificateId(caCertID);
        credentialCertificate.setCustomerId(customerId);
        credentialCertificate.setCertificateFileName(certJson.getString("CERTIFICATE_FILE_NAME"));
        credentialCertificate.setCertificateSerialNumber(certJson.getString("CERTIFICATE_SERIAL_NUMBER"));
        credentialCertificate.setCertificateIssuerDn(certJson.getString("CERTIFICATE_ISSUER_DN"));
        credentialCertificate.setCertificateSubjectDn(certJson.getString("CERTIFICATE_SUBJECT_DN"));
        credentialCertificate.setCertificateNotBefore(certJson.getLong("CERTIFICATE_NOTBEFORE"));
        credentialCertificate.setCertificateNotAfter(certJson.getLong("CERTIFICATE_NOTAFTER"));
        credentialCertificate.setCertificateThumbprint(certJson.getString("CERTIFICATE_THUMBPRINT"));
        return credentialCertificate;
    }
    
    public void deleteADCertConfig(final Long adConfigID) throws Exception {
        ProfileCertificateUtil.logger.info("deleteADCertConfig:- for adConfigID=" + adConfigID);
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ADCertConfiguration");
            final Criteria serverCriteria = new Criteria(Column.getColumn("ADCertConfiguration", "AD_CONFIG_ID"), (Object)adConfigID, 0);
            deleteQuery.setCriteria(serverCriteria);
            MDMUtil.getPersistenceLite().delete(deleteQuery);
        }
        catch (final Exception ex) {
            ProfileCertificateUtil.logger.log(Level.SEVERE, "Exception in deleteADCertConfig ", ex);
            throw ex;
        }
    }
    
    static {
        ProfileCertificateUtil.profileCertificateUtil = null;
        ProfileCertificateUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
