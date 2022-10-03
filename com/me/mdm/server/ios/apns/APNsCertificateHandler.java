package com.me.mdm.server.ios.apns;

import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.util.MDMTransactionManager;
import org.json.JSONArray;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.util.StringTokenizer;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.enrollment.ios.IOSUpgradeMobileConfigCommandHandler;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.math.BigInteger;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.openssl.PEMParser;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.dd.plist.Base64;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import java.util.Enumeration;
import java.io.FileInputStream;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import org.json.JSONException;
import org.bouncycastle.operator.OperatorCreationException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.StringWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import com.adventnet.persistence.DataObject;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.security.cert.Certificate;
import java.security.Key;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.security.KeyStore;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Date;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.Reader;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.InputStreamReader;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.logging.Logger;

public class APNsCertificateHandler
{
    private static APNsCertificateHandler apnsHandler;
    private String className;
    public Logger logger;
    private static final String TABLE = "TABLE";
    private static final String PEM_FILE_NAME = "MDM_ Zoho Corporation_Certificate.pem";
    private static final String APNS_FILE_NAME = "APNsCertificate.p12";
    private static final String MDM_HOME_LOCATION;
    private static final String CSR_FILE_NAME;
    private static final String PRIVATEKEY_FILE_NAME;
    public static final String PLIST_FILE_NAME;
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final String CERTIFICATE_FILE_UPLOAD = "CERTIFICATE_FILE_UPLOAD";
    public static final String USER_NAME = "USER_NAME";
    public static final int APNS_CERTIFICATE_EXPIRED = 1001;
    public static final int APNS_CERTIFICATE_REVOKED = 1002;
    
    public APNsCertificateHandler() {
        this.className = "APNsCertificateHandler";
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static APNsCertificateHandler getInstance() {
        APNsCertificateHandler.apnsHandler = new APNsCertificateHandler();
        Security.addProvider((Provider)new BouncyCastleProvider());
        return APNsCertificateHandler.apnsHandler;
    }
    
    private void createCSR(final JSONObject jsonObject, final String csrFilelocation, final String privateKeyLocation) throws Exception {
        final JSONObject csrDetail = this.createCSRFromJSON(jsonObject);
        final String csr = String.valueOf(csrDetail.get("CSR"));
        final String privateKey = String.valueOf(csrDetail.get("PrivateKey"));
        final byte[] csr_encoded = (byte[])csrDetail.get("CSREncoded");
        ApiFactoryProvider.getFileAccessAPI().writeFile(csrFilelocation, csr.getBytes());
        ApiFactoryProvider.getFileAccessAPI().writeFile(privateKeyLocation, privateKey.getBytes());
    }
    
    private void createOrRenewAPNsCertificate(final String certPassword) throws Exception {
        final JSONObject csrInfoObject = this.getCSRInfo();
        final String srcFilePath = APNsCertificateHandler.MDM_HOME_LOCATION + "MDM_ Zoho Corporation_Certificate.pem";
        final String privateKeyFile = (String)csrInfoObject.get("PRIVATEKEY_LOCATION");
        final File directoryPath = new File(getAPNsCertificateTempFolderPath());
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }
        final String destFilePath = directoryPath + File.separator + "APNsCertificate.p12";
        final Map oldAPNSData = getAPNSCertificateDetails();
        if (!oldAPNSData.isEmpty()) {
            final PemReader pemreader = new PemReader((Reader)new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(srcFilePath)));
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
            final X509Certificate signedCertificate = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(pemreader.readPemObject().getContent()));
            final String newTopic = this.getUID(signedCertificate);
            final String oldAPNSDataString = oldAPNSData.get("TOPIC").trim();
            this.logger.log(Level.INFO, "Old UID: {0}", oldAPNSDataString);
            this.logger.log(Level.INFO, "New UID: {0}", newTopic);
            if (!oldAPNSDataString.equalsIgnoreCase(newTopic)) {
                final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_CSR_UPLOAD_FAILURE_MISMATCH");
                }
                throw new APIHTTPException("APNS004", new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl") + "/kb/mdm-apns-not-recognized.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2) });
            }
            final Date expiryDate = signedCertificate.getNotAfter();
            final Date oldAPNsExpiryDate = new Date(oldAPNSData.get("EXPIRY_DATE"));
            if (oldAPNsExpiryDate.compareTo(expiryDate) >= 0) {
                final Long creationDate = signedCertificate.getNotBefore().getTime();
                final EvaluatorAPI evaluatorApi2 = ApiFactoryProvider.getEvaluatorAPI();
                if (evaluatorApi2 != null) {
                    evaluatorApi2.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_CSR_UPLOAD_FAILURE_OLD_CERTIFICATE");
                }
                throw new APIHTTPException("APNS007", new Object[] { MDMUtil.getDate((long)creationDate) });
            }
        }
        this.createAPNsCertificate(srcFilePath, privateKeyFile, destFilePath, certPassword);
    }
    
    private void createAPNsCertificate(final String srcFilePath, final String privateKeyFile, final String destFilePath, final String exportPassword) throws Exception {
        FileOutputStream fout = null;
        try {
            this.logger.log(Level.INFO, "Source File prop: {0}", new File(srcFilePath).lastModified());
            this.logger.log(Level.INFO, "Key prop: {0}", new File(privateKeyFile).lastModified());
            final PemReader pemreader = new PemReader((Reader)new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(srcFilePath)));
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
            final X509Certificate signedCertificate = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(pemreader.readPemObject().getContent()));
            final KeyStore store = KeyStore.getInstance("PKCS12", "BC");
            store.load(null, null);
            final X509Certificate[] chain = { signedCertificate };
            final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromApiFactory(new File(privateKeyFile));
            store.setKeyEntry("APNsCertificate", privateKey, exportPassword.toCharArray(), chain);
            if (!CertificateUtil.getInstance().isValidCertificateAndPrivateKey(signedCertificate, privateKey)) {
                final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_CSR_UPLOAD_FAILURE_MISMATCH");
                }
                final DataObject dataObject = this.getAPNsCertificateDetailsDO();
                if (!dataObject.isEmpty()) {
                    throw new APIHTTPException("APNS005", new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl") + "/kb/mdm-apns-mismatch.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2) });
                }
                throw new APIHTTPException("APNS006", new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl") + "/kb/mdm-apns-mismatch.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2) });
            }
            else {
                fout = new FileOutputStream(destFilePath);
                store.store(fout, exportPassword.toCharArray());
            }
        }
        catch (final CertificateException e) {
            throw new APIHTTPException("APNS001", new Object[0]);
        }
        catch (final Exception exp) {
            throw exp;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception exp2) {
                exp2.printStackTrace();
            }
        }
    }
    
    private JSONObject createCSRFromJSON(final JSONObject jsonObject) throws JSONException, Exception {
        try {
            final X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            if (jsonObject != null) {
                if (!jsonObject.isNull("O")) {
                    builder.addRDN(BCStyle.O, String.valueOf(jsonObject.get("O")));
                }
                if (!jsonObject.isNull("CN")) {
                    builder.addRDN(BCStyle.CN, String.valueOf(jsonObject.get("CN")));
                }
                if (!jsonObject.isNull("EmailAddress")) {
                    builder.addRDN(BCStyle.EmailAddress, String.valueOf(jsonObject.get("EmailAddress")));
                }
                if (!jsonObject.isNull("L")) {
                    builder.addRDN(BCStyle.O, String.valueOf(jsonObject.get("L")));
                }
                if (!jsonObject.isNull("C")) {
                    builder.addRDN(BCStyle.O, String.valueOf(jsonObject.get("C")));
                }
                if (!jsonObject.isNull("ST")) {
                    builder.addRDN(BCStyle.O, String.valueOf(jsonObject.get("ST")));
                }
            }
            final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
            kpGen.initialize(2048, new SecureRandom());
            KeyPair keyPair = null;
            JcaContentSignerBuilder signerBuilder = null;
            signerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
            keyPair = kpGen.generateKeyPair();
            final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance((Object)keyPair.getPublic().getEncoded());
            final PKCS10CertificationRequestBuilder csrBuilder = new PKCS10CertificationRequestBuilder(builder.build(), publicKeyInfo);
            final ContentSigner signer = signerBuilder.build(keyPair.getPrivate());
            final PKCS10CertificationRequest csr = csrBuilder.build(signer);
            final StringWriter csrContent = new StringWriter();
            final PEMWriter csrWriter = new PEMWriter((Writer)csrContent);
            csrWriter.writeObject((Object)csr);
            csrWriter.flush();
            csrWriter.close();
            final StringWriter privateKeyContent = new StringWriter();
            final PEMWriter keyWriter = new PEMWriter((Writer)privateKeyContent);
            keyWriter.writeObject((Object)keyPair.getPrivate());
            keyWriter.flush();
            keyWriter.close();
            final JSONObject resultObject = new JSONObject();
            resultObject.put("status", true);
            resultObject.put("CSR", (Object)csrContent.getBuffer().toString());
            resultObject.put("PrivateKey", (Object)privateKeyContent.getBuffer().toString());
            resultObject.put("CSREncoded", (Object)csr.getEncoded());
            return resultObject;
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "Exception in createCSRFromJSON {0}", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            this.logger.log(Level.SEVERE, "Exception in createCSRFromJSON {0}", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            this.logger.log(Level.SEVERE, "Exception in createCSRFromJSON {0}", ex3);
        }
        catch (final OperatorCreationException ex4) {
            this.logger.log(Level.SEVERE, "Exception in createCSRFromJSON {0}", (Throwable)ex4);
        }
        catch (final JSONException ex5) {
            this.logger.log(Level.SEVERE, "Exception in createCSRFromJSON {0}", (Throwable)ex5);
        }
        return null;
    }
    
    private String validateAPNSCertificateDetails(final String password) {
        String validationRemarks = "";
        try {
            final String apnsCertificateFolder = getAPNsCertificateTempFolderPath();
            final String apnsCertificateFilePath = apnsCertificateFolder + File.separator + "APNsCertificate.p12";
            final KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(apnsCertificateFilePath), password.toCharArray());
            final Enumeration enume = ks.aliases();
            if (!enume.hasMoreElements()) {
                validationRemarks = "APNS001";
            }
        }
        catch (final IOException ioE) {
            validationRemarks = "APNS003";
        }
        catch (final Exception e) {
            validationRemarks = "APNS001";
        }
        return validationRemarks;
    }
    
    private Long addOrUpdateAPNsCertificateDetails(final String certPassword) throws Exception {
        if (certPassword != null) {
            final DataObject apnsCertificateDetailsDO = this.getAPNsCertificateDetailsDO();
            Row apnsCertificateDetailsRow = null;
            if (apnsCertificateDetailsDO.isEmpty()) {
                apnsCertificateDetailsRow = new Row("APNSCertificateInfo");
                apnsCertificateDetailsRow.set("CERTIFICATE_FILE_NAME", (Object)"APNsCertificate.p12");
                apnsCertificateDetailsRow.set("CERTIFICATE_PASSWORD", (Object)certPassword);
                apnsCertificateDetailsRow.set("CREATED_BY", (Object)CustomerInfoUtil.getInstance().getCustomerId());
                apnsCertificateDetailsDO.addRow(apnsCertificateDetailsRow);
                MDMUtil.getPersistence().add(apnsCertificateDetailsDO);
            }
            else {
                apnsCertificateDetailsRow = apnsCertificateDetailsDO.getFirstRow("APNSCertificateInfo");
                apnsCertificateDetailsRow.set("CERTIFICATE_FILE_NAME", (Object)"APNsCertificate.p12");
                apnsCertificateDetailsRow.set("CERTIFICATE_PASSWORD", (Object)certPassword);
                apnsCertificateDetailsRow.set("CREATED_BY", (Object)CustomerInfoUtil.getInstance().getCustomerId());
                apnsCertificateDetailsDO.updateRow(apnsCertificateDetailsRow);
                MDMUtil.getPersistence().update(apnsCertificateDetailsDO);
            }
            return (Long)apnsCertificateDetailsRow.get("CERTIFICATE_ID");
        }
        return null;
    }
    
    private DataObject getAPNsCertificateDetailsDO() throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("APNSCertificateInfo"));
        query.addSelectColumn(new Column("APNSCertificateInfo", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public HashMap getAPNsCertificateInfo() {
        final HashMap apnsCertificateInfo = new HashMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("APNSCertificateInfo"));
            sq.addJoin(new Join("APNSCertificateInfo", "APNSCertificateErrorDetails", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 1));
            sq.addSelectColumn(Column.getColumn("APNSCertificateErrorDetails", "CERTIFICATE_ID"));
            sq.addSelectColumn(Column.getColumn("APNSCertificateErrorDetails", "ERROR_CODE"));
            sq.addSelectColumn(Column.getColumn("APNSCertificateInfo", "CERTIFICATE_ID"));
            sq.addSelectColumn(Column.getColumn("APNSCertificateInfo", "CERTIFICATE_FILE_NAME"));
            sq.addSelectColumn(Column.getColumn("APNSCertificateInfo", "CERTIFICATE_PASSWORD"));
            final DataObject apnsCertificateDetailsDO = MDMUtil.getPersistence().get(sq);
            if (!apnsCertificateDetailsDO.isEmpty()) {
                final Row apnsCertificateDetailsRow = apnsCertificateDetailsDO.getFirstRow("APNSCertificateInfo");
                apnsCertificateInfo.put("CERTIFICATE_FILE_NAME", apnsCertificateDetailsRow.get("CERTIFICATE_FILE_NAME"));
                apnsCertificateInfo.put("CERTIFICATE_PASSWORD", apnsCertificateDetailsRow.get("CERTIFICATE_PASSWORD"));
                apnsCertificateInfo.put("CERTIFICATE_ID", apnsCertificateDetailsRow.get("CERTIFICATE_ID"));
                if (apnsCertificateDetailsDO.containsTable("APNSCertificateErrorDetails")) {
                    apnsCertificateInfo.put("ERROR_CODE", apnsCertificateDetailsDO.getFirstRow("APNSCertificateErrorDetails").get("ERROR_CODE"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while getting APNsCertificate Info", ex);
        }
        return apnsCertificateInfo;
    }
    
    public void addOrUpdateCertificateDetails(final HashMap certificateDetails, final Long certificateId) throws Exception {
        final Criteria certId = new Criteria(new Column("APNSCertificateDetails", "CERTIFICATE_ID"), (Object)certificateId, 0);
        final DataObject apnsCertificateDetailsDO = MDMUtil.getPersistence().get("APNSCertificateDetails", certId);
        Row apnsCertificateDetailsRow = null;
        if (apnsCertificateDetailsDO.isEmpty()) {
            apnsCertificateDetailsRow = new Row("APNSCertificateDetails");
            apnsCertificateDetailsRow.set("CERTIFICATE_ID", (Object)certificateId);
            apnsCertificateDetailsRow.set("EXPIRY_DATE", certificateDetails.get("EXPIRY_DATE"));
            apnsCertificateDetailsRow.set("CERTIFICATE_NAME", certificateDetails.get("CERTIFICATE_NAME"));
            apnsCertificateDetailsRow.set("ISSUER_NAME", certificateDetails.get("ISSUER_NAME"));
            apnsCertificateDetailsRow.set("ISSUER_OU_NAME", certificateDetails.get("ISSUER_OU_NAME"));
            apnsCertificateDetailsRow.set("ISSUER_ORG_NAME", certificateDetails.get("ISSUER_ORG_NAME"));
            apnsCertificateDetailsRow.set("TOPIC", certificateDetails.get("TOPIC"));
            apnsCertificateDetailsRow.set("CREATION_DATE", certificateDetails.get("CREATION_DATE"));
            apnsCertificateDetailsRow.set("EMAIL_ADDRESS", certificateDetails.get("EMAIL_ADDRESS"));
            apnsCertificateDetailsRow.set("APPLE_ID", certificateDetails.get("APPLE_ID"));
            apnsCertificateDetailsRow.set("SERIAL_NO", certificateDetails.get("SERIAL_NO"));
            apnsCertificateDetailsDO.addRow(apnsCertificateDetailsRow);
            MDMUtil.getPersistence().add(apnsCertificateDetailsDO);
        }
        else {
            apnsCertificateDetailsRow = apnsCertificateDetailsDO.getRow("APNSCertificateDetails");
            if (certificateDetails.get("EXPIRY_DATE") != null) {
                apnsCertificateDetailsRow.set("EXPIRY_DATE", certificateDetails.get("EXPIRY_DATE"));
            }
            if (certificateDetails.get("CERTIFICATE_NAME") != null) {
                apnsCertificateDetailsRow.set("CERTIFICATE_NAME", certificateDetails.get("CERTIFICATE_NAME"));
            }
            if (certificateDetails.get("ISSUER_NAME") != null) {
                apnsCertificateDetailsRow.set("ISSUER_NAME", certificateDetails.get("ISSUER_NAME"));
            }
            if (certificateDetails.get("ISSUER_OU_NAME") != null) {
                apnsCertificateDetailsRow.set("ISSUER_OU_NAME", certificateDetails.get("ISSUER_OU_NAME"));
            }
            if (certificateDetails.get("ISSUER_ORG_NAME") != null) {
                apnsCertificateDetailsRow.set("ISSUER_ORG_NAME", certificateDetails.get("ISSUER_ORG_NAME"));
            }
            if (certificateDetails.get("TOPIC") != null) {
                apnsCertificateDetailsRow.set("TOPIC", certificateDetails.get("TOPIC"));
            }
            if (certificateDetails.get("CREATION_DATE") != null) {
                apnsCertificateDetailsRow.set("CREATION_DATE", certificateDetails.get("CREATION_DATE"));
            }
            if (certificateDetails.get("EMAIL_ADDRESS") != null) {
                apnsCertificateDetailsRow.set("EMAIL_ADDRESS", certificateDetails.get("EMAIL_ADDRESS"));
            }
            if (certificateDetails.get("APPLE_ID") != null) {
                apnsCertificateDetailsRow.set("APPLE_ID", certificateDetails.get("APPLE_ID"));
            }
            if (certificateDetails.get("SERIAL_NO") != null) {
                apnsCertificateDetailsRow.set("SERIAL_NO", certificateDetails.get("SERIAL_NO"));
            }
            apnsCertificateDetailsDO.updateRow(apnsCertificateDetailsRow);
            MDMUtil.getPersistence().update(apnsCertificateDetailsDO);
        }
    }
    
    public JSONObject getCSRInfo() throws Exception {
        final JSONObject csrJSONObject = new JSONObject();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("CSRInfo"));
        query.addSelectColumn(new Column("CSRInfo", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (dataObject.isEmpty()) {
            throw new Exception("No CSR Information found");
        }
        final Row csrRow = dataObject.getFirstRow("CSRInfo");
        csrJSONObject.put("CSR_ID", csrRow.get("CSR_ID"));
        csrJSONObject.put("CSR_LOCATION", csrRow.get("CSR_LOCATION"));
        csrJSONObject.put("PRIVATEKEY_LOCATION", csrRow.get("PRIVATEKEY_LOCATION"));
        csrJSONObject.put("EMAIL_ADDRESS", csrRow.get("EMAIL_ADDRESS"));
        csrJSONObject.put("ORGANIZATION_NAME", csrRow.get("ORGANIZATION_NAME"));
        csrJSONObject.put("CSR_CREATED_TIME", csrRow.get("CSR_CREATED_TIME"));
        return csrJSONObject;
    }
    
    public JSONObject getVendorSignedInfo() throws Exception {
        final JSONObject vendorSignedInfoJSONObject = new JSONObject();
        final SelectQueryImpl query = new SelectQueryImpl(new Table("VendorSignedInfo"));
        query.addSelectColumn(new Column("VendorSignedInfo", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        if (dataObject.isEmpty()) {
            return vendorSignedInfoJSONObject;
        }
        final Row vendorSignedInfoRow = dataObject.getFirstRow("VendorSignedInfo");
        vendorSignedInfoJSONObject.put("CSR_ID", vendorSignedInfoRow.get("CSR_ID"));
        vendorSignedInfoJSONObject.put("CSR_REQUEST_ID", vendorSignedInfoRow.get("CSR_REQUEST_ID"));
        vendorSignedInfoJSONObject.put("CSR_SIGNED_TIME", vendorSignedInfoRow.get("CSR_SIGNED_TIME"));
        vendorSignedInfoJSONObject.put("VENDOR_CERTIFICATE_EXPIRY_TIME", vendorSignedInfoRow.get("VENDOR_CERTIFICATE_EXPIRY_TIME"));
        vendorSignedInfoJSONObject.put("VENDOR_SIGNED_CSR", vendorSignedInfoRow.get("VENDOR_SIGNED_CSR"));
        return vendorSignedInfoJSONObject;
    }
    
    public void apnsAddOrUpdateTableInfo(final JSONObject updateData) throws Exception {
        final String tableName = String.valueOf(updateData.get("TABLE"));
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table(tableName));
        query.addSelectColumn(new Column(tableName, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        boolean update;
        Row row;
        if (dataObject.isEmpty()) {
            update = false;
            row = new Row(tableName);
        }
        else {
            update = true;
            row = dataObject.getFirstRow(tableName);
        }
        final Iterator keys = updateData.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (!key.equals("TABLE")) {
                row.set(key, updateData.get(key));
            }
        }
        if (update) {
            dataObject.updateRow(row);
            MDMUtil.getPersistence().update(dataObject);
        }
        else {
            dataObject.addRow(row);
            MDMUtil.getPersistence().add(dataObject);
        }
    }
    
    private String getEncodedCSRContent() throws Exception {
        final JSONObject csrInfoObject = this.getCSRInfo();
        final String csrLocation = (String)csrInfoObject.get("CSR_LOCATION");
        final PemReader reader = new PemReader((Reader)new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(csrLocation)));
        final byte[] csrContent = reader.readPemObject().getContent();
        reader.close();
        return Base64.encodeBytes(csrContent);
    }
    
    public void handleVendorSignedResponse(final JSONObject vendorResponse) throws Exception {
        try {
            final String VALID_RESPONSE_FROM_VENDOR = "VALID_RESPONSE_FROM_VENDOR";
            final String PLIST = "plist";
            final String VENDOR_EXPIRY_DATE = "vendorExpiryDate";
            final String VENDOR_SIGNED_TIME = "vendorSignedTime";
            final String methodName = "handleVendorSignedResponse";
            this.logger.logp(Level.INFO, this.className, methodName, "Vendor Response : " + vendorResponse);
            if (vendorResponse == null || !vendorResponse.has("plist")) {
                throw new Exception("Failed to Read Vendor Response");
            }
            final String pemFileLocation = APNsCertificateHandler.PLIST_FILE_NAME;
            final String pemContent = (String)vendorResponse.get("plist");
            final Long vendorExpiryDate = vendorResponse.getLong("vendorExpiryDate");
            final Long vendorSignedTime = vendorResponse.getLong("vendorSignedTime");
            ApiFactoryProvider.getFileAccessAPI().writeFile(pemFileLocation, pemContent.getBytes());
            this.logger.logp(Level.INFO, this.className, methodName, "Finished writing to Pem file : " + pemFileLocation + " Pem Content : " + pemContent);
            final JSONObject updateVendorInfo = new JSONObject();
            updateVendorInfo.put("TABLE", (Object)"VendorSignedInfo");
            updateVendorInfo.put("VENDOR_CERTIFICATE_EXPIRY_TIME", (Object)vendorExpiryDate);
            updateVendorInfo.put("CSR_SIGNED_TIME", (Object)vendorSignedTime);
            this.logger.logp(Level.INFO, this.className, methodName, "Updated vendor sign info table : " + updateVendorInfo);
            updateVendorInfo.put("CSR_ID", DBUtil.getFirstValueFromDBWithOutCriteria("CSRInfo", "CSR_ID"));
            updateVendorInfo.put("VENDOR_SIGNED_CSR", (Object)APNsCertificateHandler.PLIST_FILE_NAME);
            this.apnsAddOrUpdateTableInfo(updateVendorInfo);
            this.logger.logp(Level.INFO, this.className, methodName, "Signed Plist Retrieved Successfully!!");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while handling Vendor Signed response.. ", ex);
            throw ex;
        }
    }
    
    public JSONObject getAPNSDetail() throws Exception {
        final HashMap certificateDetails = (HashMap)getAPNSCertificateDetails();
        if (certificateDetails.isEmpty()) {
            return null;
        }
        final JSONObject jsonObject = new JSONObject();
        for (final Object key : certificateDetails.keySet()) {
            jsonObject.put((String)key, certificateDetails.get(key));
        }
        if (jsonObject.has("CERTIFICATE_PASSWORD")) {
            jsonObject.remove("CERTIFICATE_PASSWORD");
        }
        jsonObject.put("ERROR_CODE", (Object)this.getApnsCertErrorDetails(jsonObject.getLong("CERTIFICATE_ID")));
        return jsonObject;
    }
    
    private boolean isValidCSRAndPrivateKey(final String csrLocation, final String privateKeyLocation) throws Exception {
        final PemReader pemreader = new PemReader((Reader)new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(csrLocation)));
        final PEMParser pemParser = new PEMParser((Reader)pemreader);
        final PKCS10CertificationRequest csr = (PKCS10CertificationRequest)pemParser.readObject();
        final PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromApiFactory(new File(privateKeyLocation));
        final RSAKeyParameters publicKeyParams = (RSAKeyParameters)PublicKeyFactory.createKey(csr.getSubjectPublicKeyInfo());
        final BigInteger publicKeyModulus = publicKeyParams.getModulus();
        final RSAKeyParameters privKeyParams = (RSAKeyParameters)PrivateKeyFactory.createKey(privateKey.getEncoded());
        final BigInteger privateKeyModulus = privKeyParams.getModulus();
        if (publicKeyModulus.equals(privateKeyModulus)) {
            return true;
        }
        this.logger.log(Level.INFO, "ApnsCertificateHandler: CSR & Private key modulus mismatch");
        return false;
    }
    
    private boolean uploadPemFile(final File file) throws Exception {
        InputStream stream = null;
        boolean fileUploaded = false;
        try {
            final File directoryPath = new File("mdm");
            if (!directoryPath.exists()) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(directoryPath.getPath());
            }
            final String completeFilePath = APNsCertificateHandler.MDM_HOME_LOCATION + "MDM_ Zoho Corporation_Certificate.pem";
            stream = new FileInputStream(file);
            ApiFactoryProvider.getFileAccessAPI().writeFile(completeFilePath, stream);
            fileUploaded = true;
        }
        catch (final Exception e) {
            fileUploaded = false;
            this.logger.log(Level.SEVERE, "ApnsCertificateHandler: Exception while uploading file: ", e);
            throw e;
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "ApnsCertificateHandler: Exception while uploading file: ", e2);
            }
        }
        return fileUploaded;
    }
    
    private void createCSR(final Long customerID) throws Exception {
        final String methodName = "createCSR";
        final JSONObject csrInfoJSONObject = new JSONObject();
        csrInfoJSONObject.put("TABLE", (Object)"CSRInfo");
        csrInfoJSONObject.put("EMAIL_ADDRESS", (Object)"mdm@zohocorp.com");
        csrInfoJSONObject.put("ORGANIZATION_NAME", (Object)"ManageEngine");
        csrInfoJSONObject.put("CSR_CREATED_TIME", System.currentTimeMillis());
        DMSecurityLogger.info(this.logger, this.className, methodName, "CSR Info: {0}", (Object)csrInfoJSONObject);
        csrInfoJSONObject.put("CSR_LOCATION", (Object)APNsCertificateHandler.CSR_FILE_NAME);
        csrInfoJSONObject.put("PRIVATEKEY_LOCATION", (Object)APNsCertificateHandler.PRIVATEKEY_FILE_NAME);
        this.apnsAddOrUpdateTableInfo(csrInfoJSONObject);
        this.logger.logp(Level.INFO, this.className, methodName, "CSR Information Updated ");
        this.createCSR(null, APNsCertificateHandler.CSR_FILE_NAME, APNsCertificateHandler.PRIVATEKEY_FILE_NAME);
        this.logger.info("CSR and Private Key Files generated and populated");
    }
    
    public boolean isVendorSignCSRValid() throws Exception {
        final Long plistExpiryTme = this.getVendorSignedInfo().optLong("VENDOR_CERTIFICATE_EXPIRY_TIME", -1L);
        final Long currentTime = System.currentTimeMillis();
        return ApiFactoryProvider.getFileAccessAPI().isFileExists(APNsCertificateHandler.PLIST_FILE_NAME) && currentTime < plistExpiryTme;
    }
    
    public boolean SignCSR(final Long custID) throws JSONException, Exception {
        final String methodName = "SignCSR";
        final boolean isRenewal = !this.getAPNsCertificateDetailsDO().isEmpty();
        Label_0117: {
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(APNsCertificateHandler.CSR_FILE_NAME) && ApiFactoryProvider.getFileAccessAPI().getFileSize(APNsCertificateHandler.CSR_FILE_NAME) != 0L && ApiFactoryProvider.getFileAccessAPI().isFileExists(APNsCertificateHandler.PRIVATEKEY_FILE_NAME)) {
                if (ApiFactoryProvider.getFileAccessAPI().getFileSize(APNsCertificateHandler.PRIVATEKEY_FILE_NAME) != 0L) {
                    break Label_0117;
                }
            }
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(APNsCertificateHandler.PLIST_FILE_NAME);
            }
            catch (final Exception e) {
                this.logger.log(Level.INFO, "Handled exception when trying to delete VendorSignCSR..");
            }
            this.createCSR(custID);
        }
        if (!CertificateUtil.getInstance().isValidCSRAndPrivateKey(APNsCertificateHandler.CSR_FILE_NAME, APNsCertificateHandler.PRIVATEKEY_FILE_NAME)) {
            throw new Exception("Private Key and CSR File Courrupted");
        }
        this.logger.logp(Level.INFO, this.className, methodName, "Valid CSR and Private Key!!");
        if (this.isVendorSignCSRValid()) {
            this.logger.info("Signed File Not Expired yet ");
            return true;
        }
        final JSONObject submitJSONObject = new JSONObject();
        submitJSONObject.put("IsRenewal", isRenewal);
        submitJSONObject.put("CSRContent", (Object)this.getEncodedCSRContent());
        submitJSONObject.put("ProductCode", (Object)ProductUrlLoader.getInstance().getValue("productcode"));
        final JSONObject result = MDMApiFactoryProvider.getSecureKeyProviderAPI().signCSR(submitJSONObject);
        if (result != null) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(custID, "APPLE_APNS_MODULE", "PLIST_CREATED");
            return true;
        }
        return false;
    }
    
    public void uploadAPNsCertificate(final JSONObject jsonObject) throws Exception {
        MEMDMTrackParamManager.getInstance().incrementTrackValue((Long)jsonObject.get("CUSTOMER_ID"), "APPLE_APNS_MODULE", "APNS_UPLOAD_REQUEST");
        final String methodName = "uploadAPNsCertificate";
        DMSecurityLogger.info(this.logger, this.className, methodName, "uploadAPNsCertificate {0}", (Object)jsonObject);
        final boolean isRenewal = !this.getAPNsCertificateDetailsDO().isEmpty();
        final File cerFile = (File)jsonObject.get("CERTIFICATE_FILE_UPLOAD");
        Long certificateId = 0L;
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        String certPassword = null;
        certPassword = CertificateUtil.getInstance().generateRandomCertPassword(5);
        HashMap certificateDetails = new HashMap();
        final boolean fileUploaded = this.uploadPemFile(cerFile);
        this.logger.logp(Level.INFO, this.className, methodName, "PEM file uploaded? " + fileUploaded);
        if (!fileUploaded) {
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_CSR_UPLOAD_FAILURE_OTHERS");
            }
            MEMDMTrackParamManager.getInstance().incrementTrackValue((Long)jsonObject.get("CUSTOMER_ID"), "APPLE_APNS_MODULE", "APNS_UPLOAD_FAILURE");
            throw new APIHTTPException("APNS008", new Object[0]);
        }
        this.createOrRenewAPNsCertificate(certPassword);
        this.logger.logp(Level.INFO, this.className, methodName, "Created or renew APNs");
        final String validateRemarks = this.validateAPNSCertificateDetails(certPassword);
        this.logger.logp(Level.INFO, this.className, methodName, "Validated APNS with password..Result: " + validateRemarks);
        if (!validateRemarks.isEmpty() && evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("MDM_Module", "MDM_CSR_UPLOAD_FAILURE_OTHERS");
        }
        if (!isRenewal) {
            final Long custId = jsonObject.getLong("CUSTOMER_ID");
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (custId != null && custId != -1L) {
                IosNativeAppHandler.getInstance().addIosAgentAsync(custId, userId);
            }
        }
        MDMAgentSettingsHandler.getInstance().toggleMDMMacAgentAutoDistributionStatus(CustomerInfoUtil.getInstance().getCustomerId(), Boolean.TRUE);
        if (validateRemarks.isEmpty()) {
            final String apnsCertificateTempFolder = getAPNsCertificateTempFolderPath();
            final String apnsCertificateTempFilePath = apnsCertificateTempFolder + File.separator + "APNsCertificate.p12";
            certificateDetails = (HashMap)getAPNSCertificateDetailsFromFile(apnsCertificateTempFilePath, certPassword);
            final String apnsCertificateFolder = getAPNsCertificateFolderPath() + File.separator + "APNsCertificate.p12";
            ApiFactoryProvider.getFileAccessAPI().writeFile(apnsCertificateFolder, FileAccessUtil.getFileAsInputStream(apnsCertificateTempFilePath));
            certificateId = this.addOrUpdateAPNsCertificateDetails(certPassword);
            this.addOrUpdateCertificateDetails(certificateDetails, certificateId);
            this.logger.logp(Level.INFO, this.className, methodName, "Add or update APNS details completed..");
            try {
                this.logger.log(Level.INFO, "Apns dispatcher: closing connection");
                APNsWakeUpProcessorWrapper.closeCurrentUserClient();
                APNSImpl.getInstance().reinitialize();
            }
            catch (final Throwable e) {
                this.logger.log(Level.SEVERE, "APNsCertHandler: Error while reinitialize: {0}", e.getMessage());
            }
            this.clearApnsCertErrorDetails(certificateId);
            APNSCommunicationErrorHandler.hideAllAPNSMessages();
            Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "APNsCertificateHandler: Adding upgrade mobile config for eligible devices.");
            IOSUpgradeMobileConfigCommandHandler.getInstance().addIosUpgradeMobileConfigCommand(null, true, false);
            this.logger.logp(Level.INFO, this.className, methodName, "Added apns renew command to device");
            MEMDMTrackParamManager.getInstance().incrementTrackValue((Long)jsonObject.get("CUSTOMER_ID"), "APPLE_APNS_MODULE", "APNS_UPLOAD_SUCCESS");
            final String sEventLogRemarks = "dc.mdm.actionlog.settings.apns_upload_success";
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2061, null, jsonObject.get("USER_NAME").toString(), sEventLogRemarks, null, null);
            MDMApiFactoryProvider.getMDMUtilAPI().addAutoUserAssignRule(new JSONObject());
            new UserAssignmentRuleHandler().postUserAssignmentSettingsforAllCustomers(Boolean.TRUE);
            ApiFactoryProvider.getUtilAccessAPI().invokeOnpremiseComponents();
            this.logger.log(Level.INFO, "SGS proxy data sync triggered after apns addition");
            this.logger.log(Level.INFO, "Ended uploading apns certificate");
            return;
        }
        MEMDMTrackParamManager.getInstance().incrementTrackValue((Long)jsonObject.get("CUSTOMER_ID"), "APPLE_APNS_MODULE", "APNS_UPLOAD_FAILURE");
        throw new APIHTTPException(validateRemarks, new Object[0]);
    }
    
    public static String getAPNsCertificateFolderPath() throws Exception {
        final String apnsCertificateFolder = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("apnsCertificate");
        return apnsCertificateFolder;
    }
    
    public static String getAPNsCertificateTempFolderPath() throws Exception {
        final String apnsCertificateFolder = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("apnsCertificate") + File.separator + "backup";
        return apnsCertificateFolder;
    }
    
    public static Map getAPNSCertificateDetails() {
        final Map certificateDetails = new HashMap();
        try {
            final DataObject apnsInfo = MDMUtil.getPersistence().get("APNSCertificateInfo", (Criteria)null);
            if (!apnsInfo.isEmpty()) {
                final Row apnsInfoRow = apnsInfo.getFirstRow("APNSCertificateInfo");
                certificateDetails.put("CERTIFICATE_FILE_NAME", apnsInfoRow.get("CERTIFICATE_FILE_NAME"));
                certificateDetails.put("CERTIFICATE_PASSWORD", apnsInfoRow.get("CERTIFICATE_PASSWORD"));
                certificateDetails.putAll(getApnsCertificateDetailsDBValues());
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return certificateDetails;
    }
    
    private static Map getAPNSCertificateDetailsFromFile(final String certificateFilePath, final String password) {
        final Map certificateDetails = new HashMap();
        KeyStore ks = null;
        try {
            if (certificateFilePath != null) {
                ks = KeyStore.getInstance("PKCS12");
                ks.load(new FileInputStream(certificateFilePath), password.toCharArray());
                X509Certificate cert = null;
                final Enumeration enume = ks.aliases();
                PrivateKey pKey = null;
                while (enume.hasMoreElements()) {
                    final String alias = enume.nextElement();
                    if (ks.isKeyEntry(alias)) {
                        pKey = (PrivateKey)ks.getKey(alias, password.toCharArray());
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
                                    certificateDetails.put("CERTIFICATE_NAME", strArray[1]);
                                }
                                else {
                                    if (!token.startsWith("UID=")) {
                                        continue;
                                    }
                                    certificateDetails.put("TOPIC", strArray[1]);
                                }
                            }
                        }
                        final X500Principal issuerPrincipal = cert.getIssuerX500Principal();
                        final String issuerDistinguishedName = issuerPrincipal.getName();
                        if (issuerDistinguishedName != null) {
                            final String[] strIssuerNameArray = issuerDistinguishedName.split(",");
                            for (int issuerNameIndex = 0; issuerNameIndex < strIssuerNameArray.length; ++issuerNameIndex) {
                                final String issuerName = strIssuerNameArray[issuerNameIndex];
                                final String[] strArray2 = issuerName.split("=");
                                if (issuerName.startsWith("CN=")) {
                                    certificateDetails.put("ISSUER_NAME", strArray2[1]);
                                }
                                else if (issuerName.startsWith("OU=")) {
                                    certificateDetails.put("ISSUER_OU_NAME", strArray2[1]);
                                }
                                else if (issuerName.startsWith("O=")) {
                                    certificateDetails.put("ISSUER_ORG_NAME", strArray2[1]);
                                }
                            }
                        }
                        certificateDetails.put("SERIAL_NO", cert.getSerialNumber());
                        certificateDetails.put("CREATION_DATE", notBefore.getTime());
                        certificateDetails.put("EXPIRY_DATE", notAfter.getTime());
                    }
                    ks.deleteEntry(alias);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return certificateDetails;
    }
    
    public JSONObject removeAPNsCertificate(final String userName, final Long customerId) throws Exception {
        final String methodName = "removeAPNsCertificate";
        final JSONObject resultJSON = new JSONObject();
        resultJSON.put("isAPNsCertificateRemoved", false);
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final int managedIOSCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount();
            if (managedIOSCount > 0) {
                this.logger.logp(Level.INFO, this.className, methodName, "No. of managed IOS devices: " + managedIOSCount);
                throw new APIHTTPException("APNS011", new Object[0]);
            }
            final Criteria nullCriteria = null;
            final JSONArray depServerDetails = DEPEnrollmentUtil.getDEPServerDetails(nullCriteria);
            if (depServerDetails.length() > 0) {
                this.logger.logp(Level.INFO, this.className, methodName, "No. of DEP Servers: " + depServerDetails.length());
                String serverType = "ABM";
                for (int i = 0; i < depServerDetails.length(); ++i) {
                    final JSONObject jo = (JSONObject)depServerDetails.get(0);
                    if (!jo.optString("ORG_TYPE").equals(AppleDEPServerConstants.DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION.toString())) {
                        serverType = "ABM";
                        break;
                    }
                    serverType = "ASM";
                }
                throw new APIHTTPException("APNS010", new Object[] { serverType });
            }
            final int appleConfDetails = new AppleConfiguratorEnrollmentHandler().getUnassignedDeviceCount(-1L);
            if (appleConfDetails > 0) {
                this.logger.logp(Level.INFO, this.className, methodName, "No. Pending devices in Apple Conf: " + appleConfDetails);
                throw new APIHTTPException("APNS012", new Object[0]);
            }
            final boolean isAPNsBackupCompleted = this.backupAPNsandCSRDetails();
            if (isAPNsBackupCompleted) {
                this.logger.logp(Level.INFO, this.className, methodName, "APNs and CSR details backed up..");
                this.logger.log(Level.INFO, "Apns dispatcher: closing connection");
                APNsWakeUpProcessorWrapper.closeCurrentUserClient();
                this.removeAPNsCertificateDetails(customerId);
                this.logger.logp(Level.INFO, this.className, methodName, "Removed APNs Certificate details..");
                resultJSON.put("isAPNsCertificateRemoved", true);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2067, null, userName, "dc.mdm.actionlog.apns.removal_success", null, customerId);
                final Long apns_removal_time = System.currentTimeMillis();
                MDMUtil.updateSyMParameter("apns_removal_time", apns_removal_time.toString());
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "APPLE_APNS_MODULE", "REMOVE_APNS");
                return resultJSON;
            }
            this.logger.logp(Level.INFO, this.className, methodName, "APNs Backup Failed");
            throw new APIHTTPException("APNS009", new Object[0]);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.info("Failed to Remove APNs Certificate Properly due to " + ex2);
            throw ex2;
        }
        finally {
            CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        }
        return resultJSON;
    }
    
    private boolean backupAPNsandCSRDetails() {
        try {
            final HashMap apnsMap = (HashMap)getAPNSCertificateDetails();
            if (!apnsMap.isEmpty()) {
                final String backupDirectory = "mdmbackup";
                final String backupFolder = backupDirectory + File.separator + System.currentTimeMillis();
                final JSONObject apnsCertificateDetails = new JSONObject((Map)apnsMap);
                final JSONObject csrInfo = this.getCSRInfo();
                final JSONObject vendorSignedInfo = this.getVendorSignedInfo();
                final JSONObject resultJSON = new JSONObject();
                resultJSON.put("APNSCertificateDetails", (Object)apnsCertificateDetails);
                resultJSON.put("CSRInfo", (Object)csrInfo);
                resultJSON.put("VendorSignedInfo", (Object)vendorSignedInfo);
                final String fileContest = resultJSON.toString();
                ApiFactoryProvider.getFileAccessAPI().createDirectory(backupFolder);
                ApiFactoryProvider.getFileAccessAPI().copyDirectory(getAPNsCertificateFolderPath(), backupFolder);
                ApiFactoryProvider.getFileAccessAPI().copyDirectory("mdm", backupFolder);
                ApiFactoryProvider.getFileAccessAPI().writeFile(backupFolder + File.separator + "APNS_CERTIFICATE.DAT", fileContest.getBytes());
                return true;
            }
            return false;
        }
        catch (final Exception ex) {
            this.logger.info("Failed to backup APNS Certificate and CSR " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    private void removeAPNsCertificateDetails(final Long customerId) throws Exception {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            DataAccess.delete("APNSCertificateDetails", (Criteria)null);
            final DataObject apnsDataObject = SyMUtil.getPersistence().get("APNSCertificateInfo", (Criteria)null);
            if (!apnsDataObject.isEmpty()) {
                MDMUtil.getPersistence().delete(apnsDataObject.getFirstRow("APNSCertificateInfo"));
            }
            final UpdateQuery updateVendorSignedInfo = (UpdateQuery)new UpdateQueryImpl("VendorSignedInfo");
            updateVendorSignedInfo.setUpdateColumn("CSR_REQUEST_ID", (Object)null);
            updateVendorSignedInfo.setUpdateColumn("CSR_SIGNED_TIME", (Object)0);
            updateVendorSignedInfo.setUpdateColumn("VENDOR_CERTIFICATE_EXPIRY_TIME", (Object)0);
            MDMUtil.getPersistence().update(updateVendorSignedInfo);
            mdmTransactionManager.commit();
        }
        catch (final Exception ex) {
            this.logger.severe("Exception when removing Apns. removeAPNsCertificateDetails..." + ex);
            mdmTransactionManager.rollBack();
        }
        APNSCommunicationErrorHandler.hideAllAPNSMessages();
        MessageProvider.getInstance().unhideMessage("APNS_NOT_UPLOADED");
        MessageProvider.getInstance().unhideMessage("APNS_NOT_UPLOADED_CLOSABLE");
        MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerId);
    }
    
    private String getUID(final X509Certificate signedCertificate) {
        String newTopic = null;
        final String subjectName = signedCertificate.getSubjectDN().getName();
        if (subjectName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(subjectName, ", ");
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                final String[] strArray = token.split("=");
                if (token.startsWith("UID=")) {
                    newTopic = strArray[1].trim();
                    break;
                }
            }
        }
        return newTopic;
    }
    
    private boolean validateIfFileExists(final String filename) {
        return ApiFactoryProvider.getFileAccessAPI().isFileExists(filename) && ApiFactoryProvider.getFileAccessAPI().getFileSize(filename) > 0L;
    }
    
    private static HashMap getApnsCertificateDetailsDBValues() throws DataAccessException {
        final HashMap certificateDetails = new HashMap();
        final DataObject apnsCertificateDetailsDO = MDMUtil.getPersistence().get("APNSCertificateDetails", (Criteria)null);
        if (!apnsCertificateDetailsDO.isEmpty()) {
            final Row apnsCertificateDetailsRow = apnsCertificateDetailsDO.getFirstRow("APNSCertificateDetails");
            certificateDetails.put("EXPIRY_DATE", apnsCertificateDetailsRow.get("EXPIRY_DATE"));
            certificateDetails.put("CERTIFICATE_NAME", apnsCertificateDetailsRow.get("CERTIFICATE_NAME"));
            certificateDetails.put("EXPIRY_DATE_STRING", MDMUtil.getDate((long)apnsCertificateDetailsRow.get("EXPIRY_DATE")));
            certificateDetails.put("CREATION_DATE_STRING", MDMUtil.getDate((long)apnsCertificateDetailsRow.get("CREATION_DATE")));
            certificateDetails.put("ISSUER_NAME", apnsCertificateDetailsRow.get("ISSUER_NAME"));
            certificateDetails.put("ISSUER_OU_NAME", apnsCertificateDetailsRow.get("ISSUER_OU_NAME"));
            certificateDetails.put("ISSUER_ORG_NAME", apnsCertificateDetailsRow.get("ISSUER_ORG_NAME"));
            certificateDetails.put("CREATION_DATE", apnsCertificateDetailsRow.get("CREATION_DATE"));
            certificateDetails.put("TOPIC", apnsCertificateDetailsRow.get("TOPIC"));
            certificateDetails.put("APPLE_ID", apnsCertificateDetailsRow.get("APPLE_ID"));
            certificateDetails.put("EMAIL_ADDRESS", apnsCertificateDetailsRow.get("EMAIL_ADDRESS"));
            certificateDetails.put("CERTIFICATE_ID", apnsCertificateDetailsRow.get("CERTIFICATE_ID"));
            certificateDetails.put("SERIAL_NO", apnsCertificateDetailsRow.get("SERIAL_NO"));
        }
        return certificateDetails;
    }
    
    public void validateAPNSCertificateExpiry() throws Exception {
        final HashMap apnsMap = (HashMap)getAPNSCertificateDetails();
        if (!apnsMap.isEmpty()) {
            final Long expiryDate = apnsMap.get("EXPIRY_DATE");
            final Long alertDate = expiryDate - 3974400000L;
            final Long today = MDMUtil.getCurrentTimeInMillis();
            final Long notificationDate = expiryDate + 604800000L;
            final Long dayAfterExpiry = expiryDate + 86400000L;
            this.logger.info("Validate APNS Expiry - Today: " + today + " notificationDate: " + notificationDate + " expiryDate: " + expiryDate + " alertDate: " + alertDate + " dayAfterExpiry: " + dayAfterExpiry);
            if (today > alertDate) {
                final Integer managedDeviceCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceAndWaitingForUserAssignCount();
                final Long remainingDay = this.getAPNSExpiryPendingDays();
                final Properties prop = new Properties();
                ((Hashtable<String, String>)prop).put("$apns_expiry_date$", MDMUtil.getDate((long)expiryDate));
                ((Hashtable<String, String>)prop).put("$user_emailid$", apnsMap.get("EMAIL_ADDRESS"));
                ((Hashtable<String, Long>)prop).put("$remaingDay$", remainingDay);
                ((Hashtable<String, String>)prop).put("$apple_id$", apnsMap.get("APPLE_ID"));
                if (today < expiryDate) {
                    if (remainingDay <= new Long(30L)) {
                        MessageProvider.getInstance().unhideMessage("APNS_ABOUT_TO_EXPIRED");
                        MessageProvider.getInstance().hideMessage("APNS_EXPIRED_NON_CLOSABLE");
                        MessageProvider.getInstance().hideMessage("APNS_EXPIRED_CLOSABLE");
                    }
                    if ((remainingDay.equals(new Long(45L)) || remainingDay.equals(new Long(30L)) || remainingDay.equals(new Long(15L)) || remainingDay < new Long(7L)) && managedDeviceCount > 0) {
                        MDMMailNotificationHandler.getInstance().sendAPNSAboutToExpireMail(prop);
                    }
                    this.SignCSR(0L);
                }
                else if (today <= notificationDate) {
                    this.addApnsCertErrorDetails(this.getAPNSDetail().getLong("CERTIFICATE_ID"), 1001);
                    if (today >= expiryDate && today <= dayAfterExpiry) {
                        MessageProvider.getInstance().unhideMessage("APNS_EXPIRED_CLOSABLE");
                    }
                    MessageProvider.getInstance().hideMessage("APNS_PORT_BLOCKED");
                    MessageProvider.getInstance().unhideMessage("APNS_EXPIRED_NON_CLOSABLE");
                    MessageProvider.getInstance().hideMessage("APNS_ABOUT_TO_EXPIRED");
                    if (managedDeviceCount > 0) {
                        MDMMailNotificationHandler.getInstance().sendAPNSExpiredMail(prop);
                    }
                }
                else {
                    MessageProvider.getInstance().hideMessage("APNS_PORT_BLOCKED");
                    MessageProvider.getInstance().unhideMessage("APNS_EXPIRED_NON_CLOSABLE");
                    MessageProvider.getInstance().hideMessage("APNS_ABOUT_TO_EXPIRED");
                }
            }
        }
    }
    
    private DataObject getApnsCertErrorDetailsDO(final Long apnsCertID) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("APNSCertificateErrorDetails"));
        sq.addSelectColumn(Column.getColumn("APNSCertificateErrorDetails", "*"));
        sq.setCriteria(new Criteria(Column.getColumn("APNSCertificateErrorDetails", "CERTIFICATE_ID"), (Object)apnsCertID, 0));
        final DataObject dO = MDMUtil.getPersistence().get(sq);
        return dO;
    }
    
    public void addApnsCertErrorDetails(final Long apnsCertID, final int error) throws Exception {
        final DataObject dO = this.getApnsCertErrorDetailsDO(apnsCertID);
        if (dO.isEmpty()) {
            final Row errorRow = new Row("APNSCertificateErrorDetails");
            errorRow.set("CERTIFICATE_ID", (Object)apnsCertID);
            errorRow.set("ERROR_CODE", (Object)error);
            dO.addRow(errorRow);
            MDMUtil.getPersistence().add(dO);
        }
    }
    
    public Integer getApnsCertErrorDetails(final Long apnsCertID) throws Exception {
        Integer error = null;
        final DataObject dO = this.getApnsCertErrorDetailsDO(apnsCertID);
        if (!dO.isEmpty()) {
            error = Integer.parseInt(dO.getFirstRow("APNSCertificateErrorDetails").get("ERROR_CODE").toString());
        }
        return error;
    }
    
    public void clearApnsCertErrorDetails(final Long apnsCertID) throws Exception {
        final DeleteQuery dQ = (DeleteQuery)new DeleteQueryImpl("APNSCertificateErrorDetails");
        dQ.setCriteria(new Criteria(Column.getColumn("APNSCertificateErrorDetails", "CERTIFICATE_ID"), (Object)apnsCertID, 0));
        MDMUtil.getPersistenceLite().delete(dQ);
        this.logger.log(Level.INFO, "Cleared Apns cert error details for: {0}", apnsCertID);
    }
    
    public Long getAPNSExpiryPendingDays() {
        Long days = 0L;
        try {
            final HashMap apnsMap = (HashMap)getAPNSCertificateDetails();
            final Long expiryDate = apnsMap.get("EXPIRY_DATE");
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date2");
            final Long diff = expiryDate - today;
            days = diff / 86400000L;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while checking for APNS expiry pending days. So returning 0.");
        }
        return days;
    }
    
    public Integer getApnsErrorCode() {
        Integer apnsErrorCode = null;
        try {
            final Map apnsCertificateDetails = getAPNSCertificateDetails();
            if (apnsCertificateDetails != null && !apnsCertificateDetails.isEmpty()) {
                final Integer apnsError = this.getApnsCertErrorDetails(apnsCertificateDetails.get("CERTIFICATE_ID"));
                if (apnsError != null && (apnsError == 1001 || apnsError == 1002)) {
                    apnsErrorCode = apnsError;
                }
                else {
                    final Long expiryDate = apnsCertificateDetails.get("EXPIRY_DATE");
                    final Long currentTime = System.currentTimeMillis();
                    if (currentTime > expiryDate) {
                        apnsErrorCode = 1001;
                    }
                }
            }
        }
        catch (final Exception ex) {
            apnsErrorCode = -1;
            this.logger.log(Level.INFO, "Exception in get Apns Error Code. So returning -1.", ex);
        }
        return apnsErrorCode;
    }
    
    public static String getI18KeyForErrorCode(final Integer errorCode) {
        switch (errorCode) {
            case 1001: {
                return "mdm.apns_expired_device_scan_failed";
            }
            case 1002: {
                return "mdm.apns_revoke_device_scan_failed";
            }
            default: {
                return "mdm.api.error.internal_server_error";
            }
        }
    }
    
    static {
        APNsCertificateHandler.apnsHandler = null;
        MDM_HOME_LOCATION = "mdm" + File.separator;
        CSR_FILE_NAME = APNsCertificateHandler.MDM_HOME_LOCATION + "Customer.csr";
        PRIVATEKEY_FILE_NAME = APNsCertificateHandler.MDM_HOME_LOCATION + "PrivateKey.key";
        PLIST_FILE_NAME = APNsCertificateHandler.MDM_HOME_LOCATION + "VendorSignedCSR.plist";
    }
}
