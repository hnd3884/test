package com.me.devicemanagement.onpremise.server.certificate.client;

import java.util.Hashtable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.io.FileInputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import java.util.Scanner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.nio.file.OpenOption;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.Calendar;
import java.io.File;
import java.security.KeyPair;
import java.io.Reader;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.PEMParser;
import java.io.CharArrayReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.nio.file.Path;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.Properties;
import java.util.logging.Logger;

public class ClientCertificateUtil
{
    private static final Logger LOGGER;
    private static ClientCertificateUtil clientCertificateUtil;
    private static ClientCertAuthBean clientCertAuthBean;
    private static Properties webServerProps;
    
    protected ClientCertificateUtil() {
    }
    
    public static ClientCertificateUtil getInstance() {
        if (ClientCertificateUtil.clientCertificateUtil == null) {
            ClientCertificateUtil.clientCertificateUtil = new ClientCertificateUtil();
        }
        return ClientCertificateUtil.clientCertificateUtil;
    }
    
    public void setClientCertificateEnabledStatus() {
        ClientCertificateUtil.webServerProps = getWebServerSettings();
        if (ClientCertificateUtil.webServerProps != null) {
            ClientCertAuthBean.getInstance().setIsClientCertificateAuthenticationEnabled(Boolean.valueOf(((Hashtable<K, Boolean>)ClientCertificateUtil.webServerProps).getOrDefault("client.cert.auth.enabled", Boolean.FALSE).toString()));
            ClientCertAuthBean.getInstance().setIsClientCertAuthForceDisabled(Boolean.parseBoolean(((Hashtable<K, Boolean>)ClientCertificateUtil.webServerProps).getOrDefault("force.disable.client.cert.auth", Boolean.FALSE).toString()));
        }
    }
    
    public Boolean isClientCertAuthEnabledFromWebSettings() {
        if (ClientCertificateUtil.webServerProps != null) {
            return Boolean.valueOf(((Hashtable<K, Boolean>)ClientCertificateUtil.webServerProps).getOrDefault("client.cert.auth.enabled", Boolean.FALSE).toString());
        }
        return Boolean.FALSE;
    }
    
    public boolean isClientCertAuthForceDisabledInWebSettings() {
        if (ClientCertificateUtil.webServerProps != null) {
            return Boolean.parseBoolean(((Hashtable<K, Boolean>)ClientCertificateUtil.webServerProps).getOrDefault("force.disable.client.cert.auth", Boolean.FALSE).toString());
        }
        return Boolean.FALSE;
    }
    
    public void addAgentCertDetails(final Long resourceID, final String csr, final String clientCertificate, final Boolean isDS) throws Exception {
        try {
            Row row = null;
            if (isDS) {
                row = new Row("DSClientCertificateAuthentication");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("CLIENT_CERTIFICATE", (Object)clientCertificate);
            }
            else {
                row = new Row("ClientCertificateAuthentication");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("CLIENT_CERTIFICATE", (Object)clientCertificate);
            }
            final DataObject clientCertDetailsDO = (DataObject)new WritableDataObject();
            clientCertDetailsDO.addRow(row);
            SyMUtil.getPersistence().add(clientCertDetailsDO);
        }
        catch (final DataAccessException dae) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Data Acess Exception in adding details to DB", (Throwable)dae);
            throw dae;
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Exception in adding details to DB", e);
            throw e;
        }
    }
    
    public void updateAgentCertDetails(final Long resourceID, final String csr, final String clientCertificate, final Boolean isDS) throws Exception {
        try {
            UpdateQuery updateQuery;
            if (isDS) {
                updateQuery = (UpdateQuery)new UpdateQueryImpl("DSClientCertificateAuthentication");
                final Criteria criteria = new Criteria(new Column("DSClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceID, 0);
                updateQuery.setCriteria(criteria);
                updateQuery.setUpdateColumn("CLIENT_CERTIFICATE", (Object)clientCertificate);
            }
            else {
                updateQuery = (UpdateQuery)new UpdateQueryImpl("ClientCertificateAuthentication");
                final Criteria criteria = new Criteria(new Column("ClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceID, 0);
                updateQuery.setCriteria(criteria);
                updateQuery.setUpdateColumn("CLIENT_CERTIFICATE", (Object)clientCertificate);
            }
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException dae) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Data Acess Exception in updating details to DB", (Throwable)dae);
            throw dae;
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Exception in updating details to DB", e);
            throw e;
        }
    }
    
    public void deleteAgentClientCertificateDetails(final Long resourceId, final Boolean isDS) {
        try {
            Criteria criteria;
            if (isDS) {
                ClientCertificateUtil.clientCertAuthBean.removeEntryToDSClientCertAuthMap(String.valueOf(resourceId));
                criteria = new Criteria(Column.getColumn("DSClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            else {
                ClientCertificateUtil.clientCertAuthBean.removeEntryToClientCertAuthMap(String.valueOf(resourceId));
                criteria = new Criteria(Column.getColumn("ClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            SyMUtil.getPersistence().delete(criteria);
            this.removeResourceAuthStatusDetail(resourceId);
        }
        catch (final Exception ex) {
            ClientCertificateUtil.LOGGER.log(Level.WARNING, "Caught exception while deleting Security Parameter: RESOURCE_ID" + resourceId + " from DB.", ex);
        }
    }
    
    private void removeResourceAuthStatusDetail(final Long resourceId) throws DataAccessException {
        final DeleteQuery resourceAuthStatusDeleteQuery = (DeleteQuery)new DeleteQueryImpl("ResourceAuthStatus");
        resourceAuthStatusDeleteQuery.setCriteria(new Criteria(new Column("ResourceAuthStatus", "RESOURCE_ID"), (Object)resourceId, 0));
        SyMUtil.getPersistence().delete(resourceAuthStatusDeleteQuery);
    }
    
    public boolean isCertificateIssued(final Long resourceID, final Boolean isDS) throws Exception {
        try {
            DataObject existingRecord;
            if (isDS) {
                existingRecord = DBUtil.getDataObjectFromDB("DSClientCertificateAuthentication", "RESOURCE_ID", (Object)resourceID);
            }
            else {
                existingRecord = DBUtil.getDataObjectFromDB("ClientCertificateAuthentication", "RESOURCE_ID", (Object)resourceID);
            }
            return existingRecord != null;
        }
        catch (final DataAccessException dae) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Data Acess Exception in retrieving issued certificate from DB", (Throwable)dae);
            throw dae;
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Exception in retrieving issued certificate from DB", e);
            throw e;
        }
    }
    
    public String getIssuedCertificate(final Long resourceID, final Boolean isDS) {
        DataObject issuedCertificateDataObject = null;
        String issuedCertificate = null;
        try {
            if (isDS) {
                final Criteria equalsResourceIDCriteria = new Criteria(new Column("DSClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceID, 0);
                issuedCertificateDataObject = getDSClientCertificateDataObject(equalsResourceIDCriteria);
            }
            else {
                final Criteria equalsResourceIDCriteria = new Criteria(new Column("ClientCertificateAuthentication", "RESOURCE_ID"), (Object)resourceID, 0);
                issuedCertificateDataObject = getClientCertificateDataObject(equalsResourceIDCriteria);
            }
            if (!issuedCertificateDataObject.isEmpty()) {
                if (isDS) {
                    issuedCertificate = String.valueOf(issuedCertificateDataObject.getFirstRow("DSClientCertificateAuthentication").get(2));
                }
                else {
                    issuedCertificate = String.valueOf(issuedCertificateDataObject.getFirstRow("ClientCertificateAuthentication").get(2));
                }
            }
        }
        catch (final DataAccessException exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while getting issued certificate from DataBase" + exception);
        }
        return issuedCertificate;
    }
    
    public void storeAllTheClientCertFingerPrintToReddisAndMap() {
        DataObject clientCertDataObject = null;
        try {
            ClientCertificateUtil.clientCertAuthBean.clearAuthDataMap();
            clientCertDataObject = getClientCertificateDataObject(null);
            final Iterator clientCertDataSetItr = clientCertDataObject.getRows("ClientCertificateAuthentication");
            while (clientCertDataSetItr.hasNext()) {
                final Row clientCertRow = clientCertDataSetItr.next();
                final String certificateSerialNumber = getSerialNumberOfCertificate(String.valueOf(clientCertRow.get(2)));
                if (ClientCertificateUtil.clientCertAuthBean.getIsRedisEnabled()) {
                    setRedisWithSet(String.valueOf(clientCertRow.get(1)), certificateSerialNumber);
                }
                ClientCertificateUtil.clientCertAuthBean.addEntryToClientCertAuthMap(String.valueOf(clientCertRow.get(1)), certificateSerialNumber);
            }
            ClientCertificateUtil.LOGGER.info("AGENT MAP SIZE AFTER POPULATING  " + ClientCertificateUtil.clientCertAuthBean.getAgentAuthMapSize());
            clientCertDataObject = getDSClientCertificateDataObject(null);
            final Iterator dsClientCertDataSetItr = clientCertDataObject.getRows("DSClientCertificateAuthentication");
            while (dsClientCertDataSetItr.hasNext()) {
                final Row dsClientCertRow = dsClientCertDataSetItr.next();
                final String certificateSerialNumber = getSerialNumberOfCertificate(String.valueOf(dsClientCertRow.get(2)));
                ClientCertificateUtil.clientCertAuthBean.addEntryToDSClientCertAuthMap(String.valueOf(dsClientCertRow.get(1)), certificateSerialNumber);
            }
            ClientCertificateUtil.LOGGER.info("DS MAP SIZE AFTER POPULATING  " + ClientCertificateUtil.clientCertAuthBean.getDSMapSize());
            this.setSgsClientCertSerialNumber();
        }
        catch (final DataAccessException exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while getting fingerprint from DataBase", (Throwable)exception);
        }
    }
    
    private void setSgsClientCertSerialNumber() {
        try {
            final String sgsClientCert = SecurityUtil.getAdvancedSecurityDetail("SGS_CLIENT_CERTIFICATE");
            if (sgsClientCert != null) {
                final String sgsClientCertSerial = getSerialNumberOfCertificate(sgsClientCert);
                ClientCertificateUtil.LOGGER.log(Level.INFO, "Caching SGS Client Certificate for Authentication. Serial: {0}", new Object[] { sgsClientCertSerial });
                ClientCertificateUtil.clientCertAuthBean.setSgsClientCertSerial(sgsClientCertSerial);
            }
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while setting SGS client cert serial number: ", e);
        }
    }
    
    public void updateSerialNumberOfNewlyAddedAgent(final String resourceId, final String clientCertString, final Boolean isDS) {
        final String serialNumberOfCertificate = getSerialNumberOfCertificate(clientCertString);
        if (isDS) {
            ClientCertificateUtil.clientCertAuthBean.addEntryToDSClientCertAuthMap(resourceId, serialNumberOfCertificate);
        }
        else {
            if (ClientCertificateUtil.clientCertAuthBean.getIsRedisEnabled()) {
                setRedisWithSet(resourceId, serialNumberOfCertificate);
            }
            ClientCertificateUtil.clientCertAuthBean.addEntryToClientCertAuthMap(resourceId, serialNumberOfCertificate);
        }
    }
    
    public String getSerialNumberOfResourceCertificate(final String resourceID, final Boolean isDS) {
        if (isDS) {
            return ClientCertificateUtil.clientCertAuthBean.getEntryFromDSClientCertAuthMap(resourceID);
        }
        try {
            if (ClientCertificateUtil.clientCertAuthBean.getIsRedisEnabled()) {
                return getRedisWithGet(resourceID);
            }
        }
        catch (final Exception exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Cannot retrieve from jedis. so trying to get fingerprint of the resource from database. ", exception);
        }
        return ClientCertificateUtil.clientCertAuthBean.getEntryFromClientCertAuthMap(resourceID);
    }
    
    public String getSgsClientCertSerialNumber() {
        return ClientCertificateUtil.clientCertAuthBean.getSgsClientCertSerial();
    }
    
    public static String getSerialNumberOfCertificate(final String base64EncodedCertificate) {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(base64EncodedCertificate.getBytes());
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final X509Certificate x509Certificate = (X509Certificate)certFactory.generateCertificate(inputStream);
            return x509Certificate.getSerialNumber().toString(16).toLowerCase();
        }
        catch (final CertificateException exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while getting fingerprint from Certificate. ", exception);
            return null;
        }
    }
    
    public void updateClientCertAuthEnableDisableCount() {
        Integer clientCertAuthEnableDisabledCount = this.getClientCertAuthEnableDisableCount();
        if (clientCertAuthEnableDisabledCount != null) {
            SecurityUtil.updateSecurityParameter("CLIENT_CERT_AUTH_ENABLE_DISABLE_COUNT", String.valueOf(++clientCertAuthEnableDisabledCount));
        }
    }
    
    public Integer getClientCertAuthEnableDisableCount() {
        final String clientCertAuthEnableDisabledCount = SecurityUtil.getSecurityParameter("CLIENT_CERT_AUTH_ENABLE_DISABLE_COUNT");
        try {
            return (clientCertAuthEnableDisabledCount != null) ? Integer.parseInt(clientCertAuthEnableDisabledCount) : 0;
        }
        catch (final Exception ex) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while getting clientCertAuthEnableDisabledCount value from Security params table ", ex);
            return null;
        }
    }
    
    public X509Certificate getClientRootCACertificate() {
        final X509Certificate clientRootCACertificate = null;
        final Path clientRootCACertPath = getInstance().getClientRootCACertificatePath();
        if (Files.exists(clientRootCACertPath, new LinkOption[0])) {
            try {
                return CertificateUtils.loadX509CertificateFromFile(clientRootCACertPath.toFile());
            }
            catch (final Exception e) {
                ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading Client Root CA Cert. from path " + clientRootCACertPath, e);
            }
        }
        ClientCertificateUtil.LOGGER.info("Client Root CA Certificate doesn't exists so loading from DataBase.");
        try {
            String base64EncodedCertificate = SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_CERTIFICATE");
            base64EncodedCertificate = base64EncodedCertificate.replace("-----BEGIN CERTIFICATE-----", "");
            base64EncodedCertificate = base64EncodedCertificate.replace("-----END CERTIFICATE-----", "");
            return CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCertificate);
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading Client Root CA Cert. from DataBase ", e);
            return clientRootCACertificate;
        }
    }
    
    public PrivateKey getClientRootPrivateKey() {
        final Path clientRootKeyPath = getInstance().getClientRootCAPrivateKeyPath();
        if (Files.exists(clientRootKeyPath, new LinkOption[0])) {
            try {
                final String pemEncodedPrivateKey = new String(Files.readAllBytes(clientRootKeyPath));
                return this.loadECPrivateKey(pemEncodedPrivateKey);
            }
            catch (final IOException e) {
                ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading private key.", e);
            }
        }
        ClientCertificateUtil.LOGGER.info("Client Root CA PrivateKey Certificate doesn't exists so loading from DataBase.");
        try {
            return this.loadECPrivateKey(SecurityUtil.getAdvancedSecurityDetail("CLIENT_ROOT_PRIVATE_KEY"));
        }
        catch (final Exception e2) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading Client Root CA Cert. from DataBase ", e2);
            return null;
        }
    }
    
    public PrivateKey loadECPrivateKey(final String pemEncodedPrivateKey) {
        PrivateKey clientRootCAPrivateKey = null;
        try {
            final Reader reader = new CharArrayReader(pemEncodedPrivateKey.toCharArray());
            final PEMParser parser = new PEMParser(reader);
            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            final Object pemObject = parser.readObject();
            parser.close();
            final KeyPair keypair = converter.getKeyPair((PEMKeyPair)pemObject);
            clientRootCAPrivateKey = keypair.getPrivate();
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading Client Root CA Cert. ", e);
        }
        return clientRootCAPrivateKey;
    }
    
    public Boolean isCertificateGoingToExpire(final String pemEncodedCertificate, final int fieldToAdd, final int amountToAdd) {
        String base64EncodedCertificate = pemEncodedCertificate;
        base64EncodedCertificate = base64EncodedCertificate.replace("-----BEGIN CERTIFICATE-----", "");
        base64EncodedCertificate = base64EncodedCertificate.replace("-----END CERTIFICATE-----", "");
        try {
            return this.isCertificateGoingToExpire(CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCertificate), fieldToAdd, amountToAdd);
        }
        catch (final CertificateException e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading X509Certificate from pemEncodedCertificate ", e);
            return Boolean.FALSE;
        }
    }
    
    public Boolean isCertificateGoingToExpire(final File certificateFile, final int fieldToAdd, final int amountToAdd) {
        try {
            return this.isCertificateGoingToExpire(CertificateUtils.loadX509CertificateFromFile(certificateFile), fieldToAdd, amountToAdd);
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading X509Certificate from pemEncodedCertificate ", e);
            return Boolean.FALSE;
        }
    }
    
    public Boolean isCertificateGoingToExpire(final X509Certificate x509Certificate, final int fieldToAdd, final int amountToAdd) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(fieldToAdd, amountToAdd);
        try {
            x509Certificate.checkValidity(calendar.getTime());
        }
        catch (final CertificateExpiredException | CertificateNotYetValidException exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "The client certificate is going to expire within next one year so going to regenerate the clientroot cert. ");
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, " CertificateExpiredException  ", exception);
            return Boolean.TRUE;
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading  x509Certificate. ");
        }
        return Boolean.FALSE;
    }
    
    public static void updateComponentSettings(final String componentID, final String componentFlag) {
        try {
            Long[] customerIDs = null;
            if (CustomerInfoUtil.getInstance().isMSP()) {
                customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            }
            else {
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                customerIDs = new Long[] { customerId };
            }
            for (final Long customerID : customerIDs) {
                final String directory = File.separator + "agent";
                final Path folderPath = Paths.get(DCMetaDataUtil.getInstance().getClientDataDir() + File.separator + customerID + directory, new String[0]);
                if (!Files.exists(folderPath, new LinkOption[0])) {
                    Files.createDirectories(folderPath, (FileAttribute<?>[])new FileAttribute[0]);
                    ClientCertificateUtil.LOGGER.log(Level.INFO, "created : " + folderPath);
                }
                final Path agentComponentSettingsFilePath = folderPath.resolve("AgentComponentSettings.json");
                final JSONObject jsonObject = new JSONObject();
                String filecontent = "";
                if (Files.exists(agentComponentSettingsFilePath, new LinkOption[0])) {
                    final byte[] jsonFileAsByte = Files.readAllBytes(agentComponentSettingsFilePath);
                    filecontent = new String(jsonFileAsByte);
                    ClientCertificateUtil.LOGGER.log(Level.INFO, "filecontent:" + filecontent);
                }
                jsonObject.put(componentID, (Object)componentFlag);
                ClientCertificateUtil.LOGGER.log(Level.INFO, "writing to : " + agentComponentSettingsFilePath.toAbsolutePath());
                ClientCertificateUtil.LOGGER.info("Content to write\n " + jsonObject);
                Files.write(agentComponentSettingsFilePath, jsonObject.toString().getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
            }
        }
        catch (final Exception ex) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while setting component options... ", ex);
        }
    }
    
    public String getServerHashTimeValue() {
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final Path mesuiFilePath = Paths.get(serverHome + File.separator + "lib" + File.separator + "mesui.dat", new String[0]);
        final String emptyString = "";
        try {
            return Files.exists(mesuiFilePath, new LinkOption[0]) ? new String(Files.readAllBytes(mesuiFilePath)) : emptyString;
        }
        catch (final IOException e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while reading mesui.dat file ", e);
            return emptyString;
        }
    }
    
    public Path getClientRootCACertificatePath() {
        String clientRootCACertificateFilePath = "";
        final String serverHome = System.getProperty("server.home");
        final String clientRootCACertRelativePath = ClientCertificateUtil.webServerProps.getProperty("client.rootca.certificate.file");
        if (serverHome != null && clientRootCACertRelativePath != null) {
            clientRootCACertificateFilePath = serverHome + File.separator + ClientCertificateUtil.webServerProps.getProperty("client.rootca.certificate.file");
        }
        return Paths.get(clientRootCACertificateFilePath, new String[0]);
    }
    
    public Path getClientRootCAPrivateKeyPath() {
        String clientRootCAKeyFilePath = "";
        final String serverHome = System.getProperty("server.home");
        final String clientRootCAKeyRelativePath = ClientCertificateUtil.webServerProps.getProperty("client.rootca.privatekey.file");
        if (serverHome != null && clientRootCAKeyRelativePath != null) {
            clientRootCAKeyFilePath = System.getProperty("server.home") + File.separator + clientRootCAKeyRelativePath;
        }
        return Paths.get(clientRootCAKeyFilePath, new String[0]);
    }
    
    public Path getClientCertAuthConfigFilePath() {
        final String serverHome = System.getProperty("server.home");
        final String clientCertAuthConfigFile = ClientCertificateUtil.webServerProps.getProperty("client.cert.auth.config.loc");
        String clientCertAuthConfigFilePath = "";
        if (serverHome != null && clientCertAuthConfigFile != null) {
            clientCertAuthConfigFilePath = System.getProperty("server.home") + File.separator + ClientCertificateUtil.webServerProps.getProperty("client.cert.auth.config.loc");
            return Paths.get(clientCertAuthConfigFilePath, new String[0]);
        }
        return Paths.get(clientCertAuthConfigFilePath, new String[0]);
    }
    
    public void reloadWebServerSettings() {
        ClientCertificateUtil.webServerProps = getWebServerSettings();
    }
    
    private static Properties getWebServerSettings() {
        Properties webServerProperties = null;
        try {
            webServerProperties = ApiFactoryProvider.getUtilAccessAPI().getWebServerSettings();
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while getting webserver properties from websettings.conf", e);
        }
        return webServerProperties;
    }
    
    private static DataObject getClientCertificateDataObject(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ClientCertificateAuthentication"));
        selectQuery.addSelectColumn(Column.getColumn("ClientCertificateAuthentication", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ClientCertificateAuthentication", "CLIENT_CERTIFICATE"));
        selectQuery.setCriteria(criteria);
        return SyMUtil.getPersistence().get(selectQuery);
    }
    
    private static DataObject getDSClientCertificateDataObject(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DSClientCertificateAuthentication"));
        selectQuery.addSelectColumn(Column.getColumn("DSClientCertificateAuthentication", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DSClientCertificateAuthentication", "CLIENT_CERTIFICATE"));
        selectQuery.setCriteria(criteria);
        return SyMUtil.getPersistence().get(selectQuery);
    }
    
    private static void setRedisWithSet(final String param, final String value) {
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            jedis.set(param, value);
        }
        catch (final JedisConnectionException e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Jedis Process is not running. Exception", (Throwable)e);
            try {
                final boolean isRedisRunning = isProcessRunning("dmredis-server.exe");
                ClientCertificateUtil.LOGGER.log(Level.WARNING, "Redis Server Running (checking tasklist ) :" + isRedisRunning);
            }
            catch (final Exception ex) {
                ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception while checking redis process from tasklist", ex);
            }
        }
        catch (final Exception e2) {
            ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception while checking redis connection", e2);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    private static String getRedisWithGet(final String param) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            result = jedis.get(param);
        }
        catch (final JedisConnectionException jedisConnectionException) {
            ClientCertificateUtil.LOGGER.log(Level.INFO, "Jedis Process is not running");
            try {
                final boolean isRedisRunning = isProcessRunning("dmredis-server.exe");
                ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception when access jedis :", (Throwable)jedisConnectionException);
                ClientCertificateUtil.LOGGER.log(Level.WARNING, "Redis Server Running (checking tasklist ) :" + isRedisRunning);
                throw jedisConnectionException;
            }
            catch (final Exception ex) {
                ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception while checking redis process from tasklist", ex);
            }
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception while checking redis connection", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }
    
    public void setRedisEnabledStatus() {
        boolean isRedisEnabled = false;
        try {
            final Properties productSettingsProperties = SyMUtil.getProductSettingsProperties();
            if (productSettingsProperties != null && productSettingsProperties.containsKey("enableRedis")) {
                isRedisEnabled = Boolean.parseBoolean(productSettingsProperties.getProperty("enableRedis").trim());
            }
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.WARNING, "Exception while checking if redis is enabled", e);
        }
        ClientCertificateUtil.LOGGER.info("Is Reddis cache enabled : " + isRedisEnabled);
        ClientCertificateUtil.clientCertAuthBean.setIsRedisEnabled(isRedisEnabled);
    }
    
    public static boolean isProcessRunning(final String processName) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "tasklist.exe" });
        final Process process = processBuilder.start();
        final String tasksList = toString(process.getInputStream());
        return tasksList.contains(processName);
    }
    
    private static String toString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        final String string = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return string;
    }
    
    public void setAgentTomcatPort() {
        ClientCertificateUtil.clientCertAuthBean.setAgentTomcatPort(Integer.parseInt(ClientCertificateUtil.webServerProps.getProperty("tomcat.agent.port").trim()));
    }
    
    public void loadClientCertAuthConfig() {
        final Yaml clientCertAuthConfigYaml = new Yaml();
        Map clientCertAuthConfigMap = null;
        final Path clientCertAuthConfigPath = this.getClientCertAuthConfigFilePath();
        try {
            if (!clientCertAuthConfigPath.equals(Paths.get("", new String[0]))) {
                final File clientCertAuthConfigFile = clientCertAuthConfigPath.toFile();
                if (clientCertAuthConfigFile.exists()) {
                    clientCertAuthConfigMap = (Map)clientCertAuthConfigYaml.load((InputStream)new FileInputStream(clientCertAuthConfigPath.toFile()));
                    ClientCertificateUtil.LOGGER.info("Client Cert Auth config loaded from yaml file. " + clientCertAuthConfigMap);
                    ClientCertificateUtil.clientCertAuthBean.setClientCertAuthConfig(clientCertAuthConfigMap);
                }
            }
        }
        catch (final Exception e) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while loading cient cert yaml configuration file.", e);
        }
    }
    
    public boolean isClientCertRegenerationRequired(final String clientCertificateString) throws CertificateException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(clientCertificateString.getBytes());
        boolean isRegenerationRequired = false;
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final X509Certificate clientCertificate = (X509Certificate)certFactory.generateCertificate(inputStream);
            final X509Certificate rootCACertificate = this.getClientRootCACertificate();
            if (clientCertificate.getIssuerX500Principal().equals(rootCACertificate.getSubjectX500Principal())) {
                try {
                    clientCertificate.verify(rootCACertificate.getPublicKey());
                }
                catch (final SignatureException | NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException verifyException) {
                    ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while verifying client certificate signature with root certificate's public key.", verifyException);
                    ClientCertificateUtil.LOGGER.info("Going to regenerate client certificate.");
                    isRegenerationRequired = true;
                }
            }
            else {
                ClientCertificateUtil.LOGGER.warning(" Client certificate issuer name doesn't match with root certificate common name. Going to regenerate client certificate.");
                isRegenerationRequired = true;
            }
            return isRegenerationRequired;
        }
        catch (final CertificateException exception) {
            ClientCertificateUtil.LOGGER.log(Level.SEVERE, "Exception while generating certificate from certificate string ", exception);
            throw exception;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("AgentServerAuthLogger");
        ClientCertificateUtil.clientCertificateUtil = null;
        ClientCertificateUtil.clientCertAuthBean = ClientCertAuthBean.getInstance();
        ClientCertificateUtil.webServerProps = getWebServerSettings();
    }
}
