package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.tracker.mics.MICSMailerAPI;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.me.mdm.server.tracker.mics.MICSAppleEnrollmentFeatureController;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.KeyTransRecipientId;
import java.security.PrivateKey;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.Map;
import org.bouncycastle.cms.CMSException;
import java.util.HashMap;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.File;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.logging.Level;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.cert.X509Certificate;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppleDEPAuthTokenHandler
{
    private static AppleDEPAuthTokenHandler tokenHandler;
    public static Logger logger;
    
    public static AppleDEPAuthTokenHandler getInstance() {
        if (AppleDEPAuthTokenHandler.tokenHandler == null) {
            AppleDEPAuthTokenHandler.tokenHandler = new AppleDEPAuthTokenHandler();
        }
        return AppleDEPAuthTokenHandler.tokenHandler;
    }
    
    public Long uploadTokenDetails(final JSONObject tokenJson) throws Exception {
        JSONObject depJson = null;
        depJson = this.getDEPServerAccessToken(tokenJson);
        depJson.put("DEP_TOKEN_ID", (Object)String.valueOf(tokenJson.get("DEP_TOKEN_ID")));
        depJson.put("server_uuid", (Object)String.valueOf(tokenJson.get("server_uuid")));
        final Long customerId = tokenJson.getLong("CUSTOMER_ID");
        final Long tokenid = this.addOrUpdateDepTokenDetails(depJson, customerId);
        return tokenid;
    }
    
    private X509Certificate getCertificate(final String certFilePath) throws CertificateParsingException, CertificateException {
        ByteArrayInputStream bais = null;
        X509Certificate certificate = null;
        try {
            final byte[] value = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(certFilePath);
            bais = new ByteArrayInputStream(value);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate)certFactory.generateCertificate(bais);
        }
        catch (final CertificateParsingException exp) {
            throw exp;
        }
        catch (final CertificateException exp2) {
            throw exp2;
        }
        catch (final Exception exp3) {
            AppleDEPAuthTokenHandler.logger.log(Level.WARNING, "Exception in BouncyCastlePayloadSigning:getCertificate : {0}", exp3);
        }
        return certificate;
    }
    
    public JSONObject getDEPServerAccessToken(final JSONObject tokenJson) throws Exception {
        final Properties depJsons = System.getProperties();
        final Session session = Session.getDefaultInstance(depJsons, (Authenticator)null);
        JSONObject jsonObject = null;
        final Long customerId = tokenJson.getLong("CUSTOMER_ID");
        final Long depTokenID = tokenJson.getLong("DEP_TOKEN_ID");
        final File tokenfile = (File)tokenJson.get("CERTIFICATE_FILE_UPLOAD");
        byte[] recData = null;
        try (final InputStream depAccessTokenStream = new FileInputStream(tokenfile)) {
            final MimeMessage msg = new MimeMessage(session, depAccessTokenStream);
            final SMIMEEnveloped envelope = new SMIMEEnveloped(msg);
            final HashMap<String, String> depTokensPathMap = new HashMap<String, String>();
            final AppleDEPCertificateHandler handler = AppleDEPCertificateHandler.getInstance();
            if (!handler.isKeyPairExist(customerId, depTokenID)) {
                AppleDEPAuthTokenHandler.logger.log(Level.INFO, "ABM/ASM token's key pair is missing. Throw key & token mismatch error..");
                throw new CMSException("ABM/ASM token's key pair is missing.");
            }
            depTokensPathMap.put(handler.getDEPServerPublicKeyPath(customerId, depTokenID), handler.getDEPServerPrivateKeyPath(customerId, depTokenID));
            final Set depCertificatePublicCertFilePathSet = depTokensPathMap.entrySet();
            final Iterator certificateItr = depCertificatePublicCertFilePathSet.iterator();
            while (true) {
                final Map.Entry entry = certificateItr.next();
                final String depAccessTokenCertificateFilePath = entry.getKey();
                final String depAccessTokenPrivateKeyFilePath = entry.getValue();
                final X509Certificate certificate = this.getCertificate(depAccessTokenCertificateFilePath);
                final PrivateKey key = CertificateUtils.loadPrivateKeyFromApiFactory(new File(depAccessTokenPrivateKeyFilePath));
                final KeyTransRecipientId recId = (KeyTransRecipientId)new JceKeyTransRecipientId(certificate.getIssuerX500Principal(), certificate.getSerialNumber());
                final RecipientInformationStore recipients = envelope.getRecipientInfos();
                final RecipientInformation recipientinfo = recipients.get((RecipientId)recId);
                if (recipientinfo == null) {
                    AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Recipient info from the ABM/ASM token is empty. Wrong public key must be used..");
                    throw new CMSException("Recipient info is empty or does not match with our public key..");
                }
                final JceKeyTransEnvelopedRecipient recipient = new JceKeyTransEnvelopedRecipient(key);
                try {
                    recData = recipientinfo.getContent((Recipient)recipient);
                }
                catch (final Exception ex) {
                    if (!certificateItr.hasNext()) {
                        throw ex;
                    }
                    if (certificateItr.hasNext()) {
                        continue;
                    }
                }
                final MimeBodyPart bp = SMIMEUtil.toMimeBodyPart(recData);
                final String tokenData = (String)bp.getContent();
                final int firstindex = tokenData.indexOf("{");
                final int lastindex = tokenData.indexOf("}");
                jsonObject = new JSONObject(tokenData.substring(firstindex, lastindex + 1));
                if (jsonObject != null && jsonObject.has("consumer_key")) {
                    final String webappsDir = AppleDEPCertificateHandler.getInstance().getDEPCertificateFolder(customerId, depTokenID);
                    final String depAccessTokenKeyFilePath = webappsDir + File.separator + tokenfile.getName();
                    ApiFactoryProvider.getFileAccessAPI().writeFile(depAccessTokenKeyFilePath, (InputStream)new FileInputStream(tokenfile));
                    tokenJson.put("CERTIFICATE_FILE_PATH", (Object)depAccessTokenKeyFilePath);
                }
                break;
            }
        }
        catch (final CMSException ex2) {
            AppleDEPAuthTokenHandler.logger.log(Level.WARNING, "Exception in Decrypting the file : ", (Throwable)ex2);
            throw ex2;
        }
        catch (final NullPointerException ex3) {
            AppleDEPAuthTokenHandler.logger.log(Level.WARNING, "Exception in Decrypting the file : SMIMEEnveloped : ", ex3);
            throw new Exception("SMIMEEnveloped_Exception");
        }
        return jsonObject;
    }
    
    private Long addOrUpdateDepTokenDetails(final JSONObject depJson, final Long customerId) {
        Long tokenId = null;
        try {
            final SelectQuery depDelQry = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            depDelQry.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            depDelQry.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
            Criteria tokenOrUuidCri = null;
            if (depJson.has("DEP_TOKEN_ID")) {
                tokenOrUuidCri = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depJson.get("DEP_TOKEN_ID"), 0);
            }
            else if (depJson.has("server_uuid")) {
                depDelQry.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
                tokenOrUuidCri = new Criteria(new Column("DEPAccountDetails", "SERVER_UDID"), (Object)depJson.get("server_uuid"), 0);
            }
            final Criteria custCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            if (tokenOrUuidCri == null) {
                throw new Exception("COM0009");
            }
            depDelQry.setCriteria(tokenOrUuidCri.and(custCri));
            final DataObject DO = MDMUtil.getPersistence().get(depDelQry);
            if (DO.isEmpty()) {
                final Row tokenRow = new Row("DEPTokenDetails");
                tokenRow.set("CUSTOMER_KEY", (Object)depJson.opt("consumer_key"));
                tokenRow.set("CUSTOMER_SECRET_ENCRYPTED", (Object)depJson.opt("consumer_secret"));
                tokenRow.set("ACCESS_TOKEN_ENCRYPTED", (Object)depJson.opt("access_token"));
                tokenRow.set("ACCESS_SECRET_ENCRYPTED", (Object)depJson.opt("access_secret"));
                final String expDateStr = (String)depJson.get("access_token_expiry");
                final Date expDate = MDMEnrollmentUtil.getInstance().getDateInStandardFormat(expDateStr);
                tokenRow.set("ACCESS_TOKEN_EXPIRY_DATE", (Object)expDate.getTime());
                tokenRow.set("TOKEN_ADDED_TIME", (Object)MDMUtil.getCurrentTime());
                tokenRow.set("CUSTOMER_ID", (Object)customerId);
                DO.addRow(tokenRow);
                MDMUtil.getPersistence().add(DO);
                tokenId = (Long)tokenRow.get("DEP_TOKEN_ID");
            }
            else {
                final Row tokenRow = DO.getFirstRow("DEPTokenDetails");
                tokenId = (Long)tokenRow.get("DEP_TOKEN_ID");
                tokenRow.set("CUSTOMER_KEY", (Object)depJson.get("consumer_key"));
                tokenRow.set("CUSTOMER_SECRET_ENCRYPTED", (Object)depJson.get("consumer_secret"));
                tokenRow.set("ACCESS_TOKEN_ENCRYPTED", (Object)depJson.get("access_token"));
                tokenRow.set("ACCESS_SECRET_ENCRYPTED", (Object)depJson.get("access_secret"));
                final String expDateStr = (String)depJson.get("access_token_expiry");
                final Date expDate = MDMEnrollmentUtil.getInstance().getDateInStandardFormat(expDateStr);
                tokenRow.set("ACCESS_TOKEN_EXPIRY_DATE", (Object)expDate.getTime());
                DO.updateRow(tokenRow);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception ex) {
            AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "Exception in add or update settings", ex);
        }
        return tokenId;
    }
    
    public JSONObject getDEPTokenDetails(final Long tokenID) {
        final JSONObject tokenJSON = new JSONObject();
        try {
            final Criteria tokenIDCriteria = new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            sq.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 1));
            sq.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            sq.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
            sq.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_KEY"));
            sq.addSelectColumn(new Column("DEPTokenDetails", "ACCESS_TOKEN_ENCRYPTED"));
            sq.addSelectColumn(new Column("DEPTokenDetails", "ACCESS_SECRET_ENCRYPTED"));
            sq.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_SECRET_ENCRYPTED"));
            sq.addSelectColumn(new Column("DEPAccountDetails", "DEP_TOKEN_ID"));
            sq.addSelectColumn(new Column("DEPAccountDetails", "SERVER_UDID"));
            sq.setCriteria(tokenIDCriteria);
            final DataObject DO = MDMUtil.getPersistence().get(sq);
            if (!DO.isEmpty()) {
                final Row tokenRow = DO.getFirstRow("DEPTokenDetails");
                tokenJSON.put("consumer_key", (Object)tokenRow.get("CUSTOMER_KEY"));
                tokenJSON.put("consumer_secret", (Object)tokenRow.get("CUSTOMER_SECRET_ENCRYPTED"));
                tokenJSON.put("access_token", (Object)tokenRow.get("ACCESS_TOKEN_ENCRYPTED"));
                tokenJSON.put("access_secret", (Object)tokenRow.get("ACCESS_SECRET_ENCRYPTED"));
                tokenJSON.put("CUSTOMER_ID", tokenRow.get("CUSTOMER_ID"));
                if (DO.containsTable("DEPAccountDetails")) {
                    final Row accountRow = DO.getFirstRow("DEPAccountDetails");
                    tokenJSON.put("server_uuid", (Object)accountRow.get("SERVER_UDID"));
                }
                else {
                    tokenJSON.put("server_uuid", (Object)"new");
                }
            }
        }
        catch (final Exception e) {
            AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "Exception in getDEPTokenDetails", e);
        }
        return tokenJSON;
    }
    
    public Long getNewDEPTokenId(final Long customerId) throws Exception {
        try {
            Long tokenId = null;
            final SelectQuery depDelQry = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            depDelQry.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
            depDelQry.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
            final Criteria depTokenCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria nullConsumerKeyCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_KEY"), (Object)null, 0);
            final Criteria nullConsumerSecretCri = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_SECRET_ENCRYPTED"), (Object)null, 0);
            final Criteria nullAccessTokenCri = new Criteria(new Column("DEPTokenDetails", "ACCESS_TOKEN_ENCRYPTED"), (Object)null, 0);
            final Criteria nullAccessSecretCri = new Criteria(new Column("DEPTokenDetails", "ACCESS_SECRET_ENCRYPTED"), (Object)null, 0);
            depDelQry.setCriteria(depTokenCri.and(nullConsumerKeyCri.and(nullConsumerSecretCri.and(nullAccessTokenCri.and(nullAccessSecretCri)))));
            final DataObject DO = MDMUtil.getPersistence().get(depDelQry);
            if (DO.isEmpty()) {
                final JSONObject newTokenJson = new JSONObject();
                final SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                final String expDateStr = utcFormat.format(new Date().getTime() + 86400000L);
                newTokenJson.put("access_token_expiry", (Object)expDateStr);
                newTokenJson.put("server_uuid", (Object)"new");
                tokenId = this.addOrUpdateDepTokenDetails(newTokenJson, customerId);
            }
            else {
                final Row tokenRow = DO.getFirstRow("DEPTokenDetails");
                tokenId = (Long)tokenRow.get("DEP_TOKEN_ID");
            }
            return tokenId;
        }
        catch (final Exception ex) {
            AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "Exception while creating New ABM Server ID..", ex);
            throw ex;
        }
    }
    
    private void uploadNewToken(final JSONObject jsonObject) throws Exception {
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        try {
            MICSFeatureTrackerUtil.appleAdminEnrollmentStart(MICSAppleEnrollmentFeatureController.EnrollmentType.DEP);
            MDMUtil.getUserTransaction().begin();
            final Long tokenId = getInstance().uploadTokenDetails(jsonObject);
            AppleDEPAccountDetailsHandler.getInstance().manageAccountDetails(customerID, tokenId);
            DEPEnrollmentUtil.setDEPEnrollmentStatus(2, customerID);
            final Long hiddenCustomGroupForTokenID = DEPEnrollmentUtil.createNewCustomGroupForDEPToken(tokenId);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception exception) {
            MDMUtil.getUserTransaction().rollback();
            DEPEnrollmentUtil.checkAndResetDEPStatus(customerID);
            AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "Exception while saving DEP token", exception);
            throw exception;
        }
    }
    
    private Long getServerIdForUuid(final String uuid) throws Exception {
        final Object tokenId = DBUtil.getValueFromDB("DEPAccountDetails", "SERVER_UDID", (Object)uuid, "DEP_TOKEN_ID");
        if (tokenId == null) {
            return null;
        }
        return Long.parseLong(tokenId.toString());
    }
    
    public void addOrReplaceDEPToken(final JSONObject jsonObject, final long loginId) throws Exception {
        try {
            Long tokenId = jsonObject.getLong("DEP_TOKEN_ID");
            final Long customerID = jsonObject.getLong("CUSTOMER_ID");
            final JSONObject fileContentsJson = new JSONObject();
            fileContentsJson.put("CUSTOMER_ID", (Object)customerID);
            fileContentsJson.put("DEP_TOKEN_ID", (Object)tokenId);
            fileContentsJson.put("CERTIFICATE_FILE_UPLOAD", jsonObject.get("CERTIFICATE_FILE_UPLOAD"));
            final JSONObject serverAccessToken = getInstance().getDEPServerAccessToken(fileContentsJson);
            serverAccessToken.put("CUSTOMER_ID", (Object)customerID);
            serverAccessToken.put("DEP_TOKEN_ID", (Object)tokenId);
            serverAccessToken.put("DEPServiceRequestName", (Object)"Account");
            final AppleDEPServerRequestHandler reqHandler = new AppleDEPServerRequestHandler();
            final JSONObject responseJSON = reqHandler.processRequest(serverAccessToken);
            AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Successfully read new uploaded token...");
            final String accountResponseStatus = String.valueOf(responseJSON.get("DEPServiceStatus"));
            JSONObject accountJSON = new JSONObject();
            if (!accountResponseStatus.equalsIgnoreCase("Acknowledged")) {
                throw new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null);
            }
            AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Response is acknowledged...");
            accountJSON = responseJSON.optJSONObject("DEPServiceResponseData");
            final int orgType = AppleDEPAccountDetailsHandler.getInstance().getOrgTypeFromResponse(accountJSON.getString("org_type"));
            final String serverTypeStr = (orgType == AppleDEPServerConstants.DEP_ORG_TYPE_ENTERPRISE_ORGANISATION) ? DEPConstants.apple_Business_Manager : DEPConstants.apple_School_Manager;
            final String serverName = accountJSON.getString("server_name");
            fileContentsJson.put("server_uuid", (Object)String.valueOf(accountJSON.get("server_uuid")));
            final JSONObject existingServerAccessToken = getInstance().getDEPTokenDetails(tokenId);
            if (existingServerAccessToken != null) {
                if (String.valueOf(existingServerAccessToken.get("server_uuid")).equals("new")) {
                    AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Going to upload New token..... Fetched new account details from apple from new token uploaded..");
                    final Long abmServerId = this.getServerIdForUuid(accountJSON.get("server_uuid").toString());
                    if (abmServerId != null) {
                        AppleDEPAuthTokenHandler.logger.log(Level.INFO, "ABM server with the same UUID already exists: {0}", new Object[] { abmServerId });
                        throw new APIHTTPException("ABM022", new Object[] { serverTypeStr, serverName });
                    }
                    fileContentsJson.put("USER_ID", jsonObject.get("USER_ID"));
                    this.uploadNewToken(fileContentsJson);
                    AppleDEPWebServicetHandler.getInstance(tokenId, customerID).updateCursor(null);
                    AppleDEPProfileHandler.getInstance(tokenId, customerID).fetchOrSyncDEPDevices();
                    final String remarkArg = serverTypeStr + "@@@" + serverName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2068, null, DMUserHandler.getUserName(Long.valueOf(loginId)), "dc.mdm.dep.token_uploaded", remarkArg, customerID);
                }
                else {
                    if (!accountJSON.get("server_uuid").equals(String.valueOf(existingServerAccessToken.get("server_uuid")))) {
                        AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Mismatch in new account and old account");
                        AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Old account : {0}", existingServerAccessToken);
                        AppleDEPAuthTokenHandler.logger.log(Level.INFO, "New account : {0}", accountJSON);
                        throw new APIHTTPException("ABM001", new Object[0]);
                    }
                    AppleDEPAuthTokenHandler.logger.log(Level.INFO, "Going to replace token..... Fetched new account details from apple from new token uploaded..");
                    tokenId = getInstance().uploadTokenDetails(fileContentsJson);
                    accountJSON.put("CUSTOMER_ID", (Object)customerID);
                    accountJSON.put("DEP_TOKEN_ID", (Object)tokenId);
                    AppleDEPAccountDetailsHandler.getInstance().addOrUpdateAccountDetails(accountJSON);
                    final JSONObject profileJSON = AppleDEPProfileHandler.getInstance(tokenId, customerID).getDEPProfileDetails();
                    profileJSON.put("ADDED_USER", jsonObject.get("USER_ID"));
                    AppleDEPProfileHandler.getInstance(tokenId, customerID).createProfile(profileJSON);
                    final String remarkArg = serverTypeStr + "@@@" + serverName;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2068, null, DMUserHandler.getUserName(Long.valueOf(loginId)), "dc.mdm.dep.token_replaced", remarkArg, customerID);
                }
                final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
                mailGenerator.setCustomerEMailAddress((long)customerID, String.valueOf(jsonObject.get("email_id")), "MdM-DEP");
                MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerID);
                MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerID);
                DEPEnrollmentUtil.validateDEPTokenExpiry();
            }
            else {
                AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "No existing info for the token is already found..");
            }
        }
        catch (final Exception e) {
            AppleDEPAuthTokenHandler.logger.log(Level.SEVERE, "Exception while add or replace DEP token..", e);
            throw e;
        }
    }
    
    static {
        AppleDEPAuthTokenHandler.tokenHandler = null;
        AppleDEPAuthTokenHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
