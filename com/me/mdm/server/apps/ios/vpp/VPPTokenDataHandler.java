package com.me.mdm.server.apps.ios.vpp;

import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.vpp.VPPResponseProcessor;
import com.adventnet.sym.server.mdm.apps.vpp.VPPServiceConfigHandler;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import java.text.ParseException;
import java.io.IOException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Properties;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class VPPTokenDataHandler
{
    public static VPPTokenDataHandler vppTokenDataHandler;
    public static Logger logger;
    
    public static VPPTokenDataHandler getInstance() {
        if (VPPTokenDataHandler.vppTokenDataHandler == null) {
            VPPTokenDataHandler.vppTokenDataHandler = new VPPTokenDataHandler();
        }
        return VPPTokenDataHandler.vppTokenDataHandler;
    }
    
    private Date decodeVppToken(final InputStreamReader in, final Properties properties) throws IOException, ParseException {
        Date expDate = null;
        try {
            final BufferedReader stream = new BufferedReader(in);
            String line = stream.readLine();
            final StringBuffer strOutput = new StringBuffer();
            while (line != null) {
                strOutput.append(line);
                line = stream.readLine();
            }
            final String sToken = strOutput.toString();
            ((Hashtable<String, String>)properties).put("STOKEN", sToken);
            final BASE64Decoder decoder = new BASE64Decoder();
            final byte[] decodeByte = decoder.decodeBuffer(sToken);
            final String decode = new String(decodeByte);
            final JSONObject decodeJSON = new JSONObject(decode);
            final String expDateStr = (String)decodeJSON.get("expDate");
            final String orgName = (String)decodeJSON.get("orgName");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            expDate = dateFormat.parse(expDateStr);
            ((Hashtable<String, String>)properties).put("ORGANISATION_NAME", orgName);
            ((Hashtable<String, Long>)properties).put("EXPIRY_DATE", expDate.getTime());
            ((Hashtable<String, String>)properties).put("S_TOKEN", sToken);
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in decodeVppToken", e);
            throw e;
        }
        return expDate;
    }
    
    public Properties decodeAndValidateVPPToken(final String tempFilePathDM, final Long userID, final Long customerID, Long businessStoreID) throws Exception {
        final String tempFileLocation = new FileFacade().getTempLocation(tempFilePathDM);
        ApiFactoryProvider.getFileAccessAPI().writeFile(tempFileLocation, ApiFactoryProvider.getFileAccessAPI().getInputStream(tempFilePathDM));
        InputStreamReader in = null;
        final Properties properties = new Properties();
        try {
            in = new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().getInputStream(tempFileLocation));
            ((Hashtable<String, Long>)properties).put("TOKEN_ADDED_BY", userID);
            Hashtable hashtable = new Hashtable();
            hashtable = DateTimeUtil.determine_From_To_Times("today");
            final Long today = hashtable.get("date1");
            final Date expDate = this.decodeVppToken(in, properties);
            if (today > expDate.getTime()) {
                ((Hashtable<String, String>)properties).put("errorMessage", "expired");
            }
            else {
                final String sToken = ((Hashtable<K, String>)properties).get("S_TOKEN");
                VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
                final String command = new VPPAPIRequestGenerator(sToken).getVPPClientConfigCommand();
                final String dummyCommand = command.replace(sToken, "*****");
                VPPTokenDataHandler.logger.log(Level.INFO, "Request for VPPClientConfigSrv(First time token upload/ Token Modify): {0}", new Object[] { dummyCommand });
                final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "clientConfigSrvUrl", sToken, null);
                VPPTokenDataHandler.logger.log(Level.INFO, "Response  received for VPPClientConfigSrv(First time token upload/ Token Modify");
                final Properties clientConfigProp = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "clientConfigSrvUrl");
                if (clientConfigProp.containsKey("errorMessage")) {
                    VPPTokenDataHandler.logger.log(Level.WARNING, "Error: Response for clientConfigSrvUrl of businessStoreID: {0}", new Object[] { businessStoreID, responseJSON });
                }
                else if (!clientConfigProp.containsKey("LOCATION_ID") && clientConfigProp.containsKey("ORGANISATION_NAME")) {
                    VPPTokenDataHandler.logger.log(Level.WARNING, "No location details found for uploaded token of businessStoreID: {0} belonging to organization: {1}. (Legacy Token)", new Object[] { businessStoreID, ((Hashtable<K, Object>)clientConfigProp).get("ORGANISATION_NAME") });
                }
                else if (clientConfigProp.containsKey("LOCATION_ID")) {
                    VPPTokenDataHandler.logger.log(Level.INFO, "Response received for clientConfigSrvUrl of businessStoreID: {0} - Location Name: {1}", new Object[] { businessStoreID, ((Hashtable<K, Object>)clientConfigProp).get("LOCATION_NAME") });
                }
                else {
                    VPPTokenDataHandler.logger.log(Level.WARNING, "No location or orgName found");
                }
                final Integer errorNumber = ((Hashtable<K, Integer>)clientConfigProp).get("errorNumber");
                if (errorNumber != null) {
                    if (errorNumber == 9625) {
                        ((Hashtable<String, String>)properties).put("errorMessage", "revoked");
                    }
                    else if (errorNumber == 9621) {
                        ((Hashtable<String, String>)properties).put("errorMessage", "expired");
                    }
                    else if (errorNumber == 10008) {
                        ((Hashtable<String, String>)properties).put("errorMessage", "notReachable");
                    }
                    else {
                        ((Hashtable<String, String>)properties).put("errorMessage", "unknownError");
                    }
                }
                else {
                    final String newUId = ((Hashtable<K, String>)clientConfigProp).get("UNIQUE_ID");
                    final JSONObject json = this.checkIfTheTokenUsedByOtherCustomer(customerID, newUId);
                    if (json.length() > 0) {
                        ((Hashtable<String, String>)properties).put("errorMessage", "alreadyUsedByOtherCustomer");
                    }
                    else {
                        String clientContext = ((Hashtable<K, String>)clientConfigProp).get("clientContext");
                        ((Hashtable<String, String>)properties).put("UNIQUE_ID", newUId);
                        ((Hashtable<String, Object>)properties).put("LOCATION_NAME", ((Hashtable<K, Object>)clientConfigProp).get("LOCATION_NAME"));
                        ((Hashtable<String, Object>)properties).put("LOCATION_ID", ((Hashtable<K, Object>)clientConfigProp).get("LOCATION_ID"));
                        ((Hashtable<String, Object>)properties).put("COUNTRY_CODE", ((Hashtable<K, Object>)clientConfigProp).get("COUNTRY_CODE"));
                        ((Hashtable<String, Object>)properties).put("DEFAULT_PLATFORM", ((Hashtable<K, Object>)clientConfigProp).get("DEFAULT_PLATFORM"));
                        ((Hashtable<String, Object>)properties).put("ORG_ID_HASH", ((Hashtable<K, Object>)clientConfigProp).get("ORG_ID_HASH"));
                        if (businessStoreID == null) {
                            businessStoreID = this.getBusinessStoreID(customerID, newUId);
                            if (businessStoreID != null && businessStoreID != -1L) {
                                ((Hashtable<String, String>)properties).put("errorMessage", "alreadyUsedByCustomer");
                            }
                            else if (!clientContext.equalsIgnoreCase("")) {
                                String guid = "--";
                                JSONObject clientContextJson;
                                if (clientContext.equals("token being used in v2")) {
                                    VPPTokenDataHandler.logger.log(Level.INFO, "ClientContext: token being used in v2 in businessStore : {0}", new Object[] { businessStoreID });
                                    clientContextJson = this.getNewClientContextJSON(sToken);
                                    if (clientContextJson.length() > 0) {
                                        VPPTokenDataHandler.logger.log(Level.INFO, "New Client context response: {0}", new Object[] { clientContextJson });
                                        clientContext = clientContextJson.toString();
                                    }
                                }
                                else {
                                    try {
                                        clientContextJson = new JSONObject(clientContext);
                                    }
                                    catch (final Exception e) {
                                        VPPTokenDataHandler.logger.log(Level.INFO, "Client Context is not a JSONObject string as expected: Client Context: {0}. Hence, setting empty client context");
                                        clientContextJson = new JSONObject();
                                    }
                                }
                                guid = clientContextJson.optString("guid", "--");
                                if (!guid.equals("--")) {
                                    ((Hashtable<String, String>)properties).put("warning", "differentClientContext");
                                    ((Hashtable<String, String>)properties).put("clientContext", clientContext);
                                }
                            }
                        }
                        else {
                            final Long tempBusinessStoreID = this.getBusinessStoreID(customerID, newUId);
                            if (tempBusinessStoreID != null && !tempBusinessStoreID.equals(businessStoreID)) {
                                ((Hashtable<String, String>)properties).put("errorMessage", "alreadyUsedByCustomer");
                            }
                            else {
                                final JSONObject tokenDetailsJson = this.getVppTokenDetails(businessStoreID);
                                final String oldUid = tokenDetailsJson.optString("UNIQUE_ID", (String)null);
                                if (oldUid != null && !MDMStringUtils.isEmpty(oldUid) && !newUId.equalsIgnoreCase(oldUid)) {
                                    VPPTokenDataHandler.logger.log(Level.INFO, "Old Unique ID {0} does not match new unique ID {1}", new Object[] { oldUid, newUId });
                                    if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotCheckSameVPPTokenReplace")) {
                                        ((Hashtable<String, String>)properties).put("errorMessage", "mismatch");
                                        final JSONObject oldAndNewVPPDetails = new JSONObject();
                                        oldAndNewVPPDetails.put("oldAccountName", (Object)tokenDetailsJson.optString("EMAIL", ""));
                                        oldAndNewVPPDetails.put("newAccountName", ((Hashtable<K, Object>)clientConfigProp).get("EMAIL"));
                                        if (clientConfigProp.containsKey("LOCATION_NAME")) {
                                            oldAndNewVPPDetails.put("newLocationName", ((Hashtable<K, Object>)clientConfigProp).get("LOCATION_NAME"));
                                        }
                                        if (tokenDetailsJson.has("LOCATION_NAME")) {
                                            oldAndNewVPPDetails.put("oldLocationName", tokenDetailsJson.get("LOCATION_NAME"));
                                        }
                                        ((Hashtable<String, JSONObject>)properties).put("oldAndNewVPPDetails", oldAndNewVPPDetails);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return properties;
        }
        catch (final Exception e2) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in decode and validating vpp token");
            throw e2;
        }
        finally {
            if (in != null) {
                in.close();
            }
            final boolean isFileDeleted = ApiFactoryProvider.getFileAccessAPI().deleteFile(tempFileLocation);
            if (isFileDeleted) {
                VPPTokenDataHandler.logger.log(Level.INFO, "Deleting the temp file");
            }
        }
    }
    
    public JSONObject getNewClientContextJSON(final String sToken) throws Exception {
        final JSONObject newVPPResponse = VPPAppAPIRequestHandler.getInstance().getNewVppClientConfigResponse(sToken);
        VPPTokenDataHandler.logger.log(Level.INFO, "New VPP API Response: {0}", new Object[] { newVPPResponse });
        return VPPResponseProcessor.getInstance().getNewVPPClientContextJSON(newVPPResponse);
    }
    
    public void updateCountryCode(final String countryCode, final Long businessStoreID) throws DataAccessException {
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdVPPTokenDetails");
        uQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
        uQuery.setUpdateColumn("COUNTRY_CODE", (Object)countryCode);
        MDMUtil.getPersistence().update(uQuery);
    }
    
    private Long getBusinessStoreID(final Long customerId, final String uid) {
        Long businessStoreID = null;
        try {
            final JSONObject businessStoreDetails = new IOSStoreHandler(businessStoreID, customerId).getBusinessStoreDetails(uid);
            if (!businessStoreDetails.has("Error") && businessStoreDetails.length() > 0) {
                businessStoreID = businessStoreDetails.optLong("BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getBusinessStoreID", e);
        }
        return businessStoreID;
    }
    
    public void addorUpdateVppTokenDetails(final Properties prop) throws DataAccessException, SyMException {
        Long businessStoreID = ((Hashtable<K, Long>)prop).get("BUSINESSSTORE_ID");
        final Long customerId = ((Hashtable<K, Long>)prop).get("CUSTOMER_ID");
        final String uID = ((Hashtable<K, String>)prop).get("UNIQUE_ID");
        final Long userID = ((Hashtable<K, Long>)prop).get("USER_ID");
        try {
            businessStoreID = new IOSStoreHandler(businessStoreID, customerId).addOrUpdateManagedStore(uID, userID);
            final SelectQuery tokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPTokenDetails"));
            tokenQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            tokenQuery.setCriteria(new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            tokenQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "*"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(tokenQuery);
            if (DO.isEmpty()) {
                Row tokenRow = new Row("MdVPPTokenDetails");
                tokenRow = this.setVppTokenRow(tokenRow, prop, customerId);
                tokenRow.set("VPP_TOKEN_ADDED_TIME", (Object)System.currentTimeMillis());
                final Long userId = ((Hashtable<K, Long>)prop).get("TOKEN_ADDED_BY");
                tokenRow.set("TOKEN_ADDED_BY", (Object)userId);
                tokenRow.set("CUSTOMER_ID", (Object)customerId);
                DO.addRow(tokenRow);
                final Row vppToBSRow = new Row("MdBusinessStoreToVppRel");
                vppToBSRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
                vppToBSRow.set("TOKEN_ID", tokenRow.get("TOKEN_ID"));
                DO.addRow(vppToBSRow);
                MDMUtil.getPersistence().add(DO);
            }
            else {
                Row tokenRow = DO.getFirstRow("MdVPPTokenDetails");
                tokenRow = this.setVppTokenRow(tokenRow, prop, customerId);
                DO.updateRow(tokenRow);
                final Row vppToBSRow2 = DO.getFirstRow("MdBusinessStoreToVppRel");
                vppToBSRow2.set("BUSINESSSTORE_ID", (Object)businessStoreID);
                vppToBSRow2.set("TOKEN_ID", tokenRow.get("TOKEN_ID"));
                DO.updateRow(vppToBSRow2);
                MDMUtil.getPersistence().update(DO);
            }
            if (businessStoreID != null) {
                ((Hashtable<String, Long>)prop).put("BUSINESSSTORE_ID", businessStoreID);
            }
            MessageProvider.getInstance().hideMessage("VPP_EXPIRED", customerId);
            MessageProvider.getInstance().hideMessage("VPP_ABOUT_TO_EXPIRE", customerId);
            MessageProvider.getInstance().hideMessage("SILENT_INSTALL_APPS", customerId);
        }
        catch (final Exception ex) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in add or update vpp settings", ex);
            throw ex;
        }
    }
    
    private Row setVppTokenRow(final Row tokenRow, final Properties prop, final Long customerId) {
        final String sToken = ((Hashtable<K, String>)prop).get("STOKEN");
        if (sToken != null) {
            tokenRow.set("S_TOKEN", (Object)sToken);
        }
        final String userModifiedToken = ((Hashtable<K, String>)prop).get("USER_SINCE_MODIFIED_TOKEN");
        if (userModifiedToken != null) {
            tokenRow.set("USER_SINCE_MODIFIED_TOKEN", (Object)userModifiedToken);
        }
        final String licenseModifiedToken = ((Hashtable<K, String>)prop).get("LICENSE_SINCE_MODIFIED_TOKEN");
        if (licenseModifiedToken != null) {
            tokenRow.set("LICENSE_SINCE_MODIFIED_TOKEN", (Object)licenseModifiedToken);
        }
        final Long expDate = ((Hashtable<K, Long>)prop).get("EXPIRY_DATE");
        if (expDate != null) {
            tokenRow.set("EXPIRY_DATE", (Object)expDate);
        }
        final String orgName = ((Hashtable<K, String>)prop).get("ORGANISATION_NAME");
        if (orgName != null) {
            tokenRow.set("ORGANISATION_NAME", (Object)orgName);
        }
        final Integer licenseAssignType = ((Hashtable<K, Integer>)prop).get("LICENSE_ASSIGN_TYPE");
        if (licenseAssignType != null) {
            tokenRow.set("LICENSE_ASSIGN_TYPE", (Object)licenseAssignType);
        }
        if (customerId != null) {
            tokenRow.set("CUSTOMER_ID", (Object)customerId);
        }
        final String orgId = ((Hashtable<K, String>)prop).get("ORGANIZATION_ID");
        if (orgId != null) {
            tokenRow.set("ORGANIZATION_ID", (Object)orgId);
        }
        final String orgIDHash = ((Hashtable<K, String>)prop).get("ORG_ID_HASH");
        if (orgIDHash != null) {
            tokenRow.set("ORG_ID_HASH", (Object)orgIDHash);
        }
        final String uniqueId = ((Hashtable<K, String>)prop).get("UNIQUE_ID");
        if (uniqueId != null && !MDMStringUtils.isEmpty(uniqueId)) {
            tokenRow.set("UNIQUE_ID", (Object)Long.parseLong(uniqueId));
        }
        final String locationName = ((Hashtable<K, String>)prop).get("LOCATION_NAME");
        if (locationName != null) {
            tokenRow.set("LOCATION_NAME", (Object)locationName);
        }
        final String locationId = ((Hashtable<K, String>)prop).get("LOCATION_ID");
        if (locationId != null) {
            tokenRow.set("LOCATION_ID", (Object)locationId);
        }
        final String appleId = ((Hashtable<K, String>)prop).get("APPLEID");
        if (appleId != null) {
            tokenRow.set("APPLEID", (Object)appleId);
        }
        final String emailId = ((Hashtable<K, String>)prop).get("EMAIL");
        if (emailId != null) {
            tokenRow.set("EMAIL", (Object)emailId);
        }
        final Integer defaultPlatform = ((Hashtable<K, Integer>)prop).get("DEFAULT_PLATFORM");
        if (defaultPlatform != null) {
            tokenRow.set("DEFAULT_PLATFORM", (Object)defaultPlatform);
        }
        final String countryCode = ((Hashtable<K, String>)prop).get("COUNTRY_CODE");
        if (countryCode != null) {
            tokenRow.set("COUNTRY_CODE", (Object)countryCode);
        }
        return tokenRow;
    }
    
    public boolean checkIfAnyVppTokenIsExpiredOrAboutToExpire(final Long customerID) {
        boolean status = false;
        try {
            final SelectQuery tokenQuery = this.getVppTokenDetailsQuery();
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LOCATION_NAME"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "EXPIRY_DATE"));
            tokenQuery.setCriteria(new Criteria(Column.getColumn("CUSTOMER_ID", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject tokenDO = MDMUtil.getPersistence().get(tokenQuery);
            final Iterator iter = tokenDO.getRows("MdVPPTokenDetails");
            while (iter.hasNext()) {
                final Row tokenRow = iter.next();
                final Long expiryDate = (Long)tokenRow.get("EXPIRY_DATE");
                Hashtable ht = new Hashtable();
                ht = DateTimeUtil.determine_From_To_Times("today");
                final Long today = ht.get("date1");
                final Long diff = expiryDate - today;
                final int remainingDay = (int)(diff / 86400000L);
                if (remainingDay < 15) {
                    status = true;
                    break;
                }
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getVPPPendingExpiryDaysForVppToken", e);
        }
        return status;
    }
    
    public SelectQuery getVppTokenDetailsWithDEPJoin() {
        final SelectQuery vppTokenQuery = this.getVppTokenDetailsQuery();
        vppTokenQuery.addJoin(new Join("Resource", "DEPTokenDetails", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        Criteria depAccountJoinCriteria = new Criteria(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)Column.getColumn("DEPAccountDetails", "DEP_TOKEN_ID"), 0);
        depAccountJoinCriteria = depAccountJoinCriteria.and(new Criteria(Column.getColumn("MdVPPTokenDetails", "ORG_ID_HASH"), (Object)Column.getColumn("DEPAccountDetails", "ORG_ID_HASH"), 0));
        vppTokenQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", depAccountJoinCriteria, 1));
        return vppTokenQuery;
    }
    
    public SelectQuery getVppTokenDetailsQuery() {
        final SelectQuery selectQuery = MDBusinessStoreUtil.getBusinessStoreQuery();
        selectQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        return selectQuery;
    }
    
    public String getVppToken(final Long businessStoreID) {
        String sToken = null;
        try {
            final SelectQuery tokenQuery = this.getVppTokenDetailsQuery();
            tokenQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "S_TOKEN"));
            final DataObject DO = MDMUtil.getPersistence().get(tokenQuery);
            if (!DO.isEmpty()) {
                final Row tokenRow = DO.getFirstRow("MdVPPTokenDetails");
                sToken = tokenRow.get("S_TOKEN").toString();
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception while getting VPP sToken", e);
        }
        return sToken;
    }
    
    public JSONObject getVppTokenDetails(final Long businessStoreID) throws DataAccessException {
        JSONObject response = new JSONObject();
        try {
            final SelectQuery tokenQuery = this.getVppTokenDetailsWithDEPJoin();
            tokenQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            tokenQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
            tokenQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ADDED_BY"));
            tokenQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "LAST_MODIFIED_BY"));
            tokenQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("Resource", "DB_ADDED_TIME"));
            tokenQuery.addSelectColumn(Column.getColumn("Resource", "DB_UPDATED_TIME"));
            tokenQuery.addSelectColumn(Column.getColumn("DEPAccountDetails", "*"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            final DataObject tokenDo = MDMUtil.getPersistence().get(tokenQuery);
            if (!tokenDo.isEmpty()) {
                final Row storeRow = tokenDo.getFirstRow("ManagedBusinessStore");
                final Row tokenRow = tokenDo.getFirstRow("MdVPPTokenDetails");
                final Row resourceRow = tokenDo.getFirstRow("Resource");
                response = MDMDBUtil.rowToJSON(tokenRow);
                int orgType = 0;
                if (tokenDo.containsTable("DEPAccountDetails")) {
                    final Row depRow = tokenDo.getFirstRow("DEPAccountDetails");
                    orgType = (int)depRow.get("ORG_TYPE");
                }
                response.put("ORG_TYPE", orgType);
                response.put("UNIQUE_ID", storeRow.get("BUSINESSSTORE_IDENTIFICATION"));
                response.put("BUSINESSSTORE_ID", storeRow.get("BUSINESSSTORE_ID"));
                response.put("BUSINESSSTORE_ADDED_BY", storeRow.get("BUSINESSSTORE_ADDED_BY"));
                response.put("LAST_MODIFIED_BY", storeRow.get("LAST_MODIFIED_BY"));
                response.put("DB_ADDED_TIME", resourceRow.get("DB_ADDED_TIME"));
                response.put("DB_UPDATED_TIME", resourceRow.get("DB_UPDATED_TIME"));
            }
        }
        catch (final Exception ex) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getVppTokenDetails", ex);
            throw ex;
        }
        return response;
    }
    
    public String getVppTokenUUID(final Long businessStoreID) {
        String vppTokenUUID = null;
        try {
            final SelectQuery tokenQuery = this.getVppTokenDetailsQuery();
            tokenQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "VPP_TOKEN_UUID"));
            final DataObject DO = MDMUtil.getPersistence().get(tokenQuery);
            if (!DO.isEmpty()) {
                final Row tokenRow = DO.getFirstRow("MdVPPTokenDetails");
                vppTokenUUID = tokenRow.get("VPP_TOKEN_UUID").toString();
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception while getting VPP sToken", e);
        }
        return vppTokenUUID;
    }
    
    public DataObject getVPPTokenDO(final Long businessStoreID) {
        DataObject dataObject = null;
        try {
            final SelectQuery tokenDOQuery = this.getVppTokenDetailsQuery();
            tokenDOQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            tokenDOQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "*"));
            dataObject = MDMUtil.getPersistence().get(tokenDOQuery);
            if (!dataObject.isEmpty()) {
                return dataObject;
            }
        }
        catch (final Exception ex) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception while getting VPP sToken - {0}", ex);
        }
        return dataObject;
    }
    
    public void validateAndSendVPPTokenExpiryMail(final Long businessStoreID) throws DataAccessException, Exception {
        final JSONObject vppTokenDetails = this.getVppTokenDetails(businessStoreID);
        if (vppTokenDetails.length() > 0) {
            final String uuid = (String)vppTokenDetails.opt("VPP_TOKEN_UUID");
            if (uuid != null || uuid != "--") {
                final Long expDate = (Long)vppTokenDetails.get("EXPIRY_DATE");
                final Long customerid = (Long)vppTokenDetails.get("CUSTOMER_ID");
                final String orgName = vppTokenDetails.getString("ORGANISATION_NAME");
                String locationName = (String)vppTokenDetails.opt("LOCATION_NAME");
                if (locationName == null) {
                    locationName = orgName;
                }
                final int orgType = vppTokenDetails.getInt("ORG_TYPE");
                String org = "Apple Business Manager/Apple School Manager";
                String portalUrl = "business.apple.com";
                if (orgType == 1) {
                    org = "Apple Business Manager";
                }
                else if (orgType == 2) {
                    org = "Apple School Manager";
                    portalUrl = "school.apple.com";
                }
                Hashtable ht = new Hashtable();
                ht = DateTimeUtil.determine_From_To_Times("today");
                final Long alertDate = expDate - 1296000000L;
                final Long today = ht.get("date1");
                if (today > alertDate) {
                    final Properties prop = new Properties();
                    ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerid);
                    ((Hashtable<String, Long>)prop).put("BUSINESSSTORE_ID", businessStoreID);
                    final Long diff = expDate - today;
                    final int remainingDays = (int)(diff / 86400000L);
                    ((Hashtable<String, Integer>)prop).put("remainingDays", remainingDays);
                    ((Hashtable<String, String>)prop).put("LOCATION_NAME", locationName);
                    ((Hashtable<String, String>)prop).put("ORGANISATION_NAME", orgName);
                    ((Hashtable<String, String>)prop).put("portalUrl", portalUrl);
                    ((Hashtable<String, String>)prop).put("org", org);
                    if (remainingDays > 0) {
                        MDMMailNotificationHandler.getInstance().sendVPPAboutToExpireMail(prop);
                    }
                    else {
                        MDMMailNotificationHandler.getInstance().sendVPPExpireMail(prop);
                    }
                }
            }
        }
    }
    
    public JSONArray getAllVppTokenDetailsForCustomer(final Long customerID) {
        final JSONArray responseArray = new JSONArray();
        try {
            final SelectQuery selectQuery = this.getVppTokenDetailsQuery();
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LOCATION_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "ORGANISATION_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (!DO.isEmpty()) {
                final Iterator storeIter = DO.getRows("ManagedBusinessStore");
                final Iterator tokenIter = DO.getRows("MdVPPTokenDetails");
                while (storeIter.hasNext() && tokenIter.hasNext()) {
                    final Row storeRow = storeIter.next();
                    final Row tokenRow = tokenIter.next();
                    final JSONObject resultJSON = new JSONObject();
                    final String locationName = (String)tokenRow.get("LOCATION_NAME");
                    final String orgName = (String)tokenRow.get("ORGANISATION_NAME");
                    resultJSON.put("BUSINESSSTORE_ID", storeRow.get("BUSINESSSTORE_ID"));
                    resultJSON.put("LOCATION_NAME", (Object)locationName);
                    resultJSON.put("ORGANISATION_NAME", (Object)orgName);
                    resultJSON.put("LICENSE_ASSIGN_TYPE", tokenRow.get("LICENSE_ASSIGN_TYPE"));
                    responseArray.put((Object)resultJSON);
                }
            }
        }
        catch (final Exception ex) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getAllVppTokenDetailsForCustomer()", ex);
        }
        return responseArray;
    }
    
    public Long getVppSyncTime(final Long businessStoreID) {
        return MDBusinessStoreUtil.getLastSuccessfulSyncTime(businessStoreID);
    }
    
    public void asyncVppAppData(final Long businessStoreID, final Long customerId, final Boolean syncWithNewClientContext) throws Exception {
        final Properties taskProps = new Properties();
        final JSONObject queueData = new JSONObject();
        queueData.put("PlatformType", 1);
        queueData.put("BUSINESSSTORE_ID", (Object)businessStoreID);
        final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        ((Hashtable<String, Long>)taskProps).put("CUSTOMER_ID", customerId);
        if (userId != null) {
            queueData.put("userID", (Object)userId);
        }
        if (syncWithNewClientContext != null) {
            queueData.put("toSetNewClientContext", (Object)syncWithNewClientContext);
        }
        final CommonQueueData syncAppsData = new CommonQueueData();
        syncAppsData.setCustomerId(customerId);
        syncAppsData.setTaskName("SyncAppsTask");
        syncAppsData.setClassName("com.me.mdm.server.apps.businessstore.SyncAppsTask");
        syncAppsData.setJsonQueueData(queueData);
        CommonQueueUtil.getInstance().addToQueue(syncAppsData, CommonQueues.MDM_APP_MGMT);
        VPPTokenDataHandler.logger.log(Level.INFO, "SyncAppsTask added in queue for businessstoreId {0} of IOS platform", businessStoreID);
    }
    
    public void updateVppTokenUUID(final String vppTokenUUID, final Long businessStoreID) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdVPPTokenDetails");
            uQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("VPP_TOKEN_UUID", (Object)vppTokenUUID);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in updateVppTokenUUID", ex);
        }
    }
    
    public String getVppCountryCode(final Long businessStoreID) {
        String vppCountrycode = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVPPTokenDetails"));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "COUNTRY_CODE"));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (!DO.isEmpty()) {
                final Row tokenRow = DO.getFirstRow("MdVPPTokenDetails");
                vppCountrycode = tokenRow.get("COUNTRY_CODE").toString();
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getVppCountryCode", e);
        }
        return vppCountrycode;
    }
    
    private JSONObject checkIfTheTokenUsedByOtherCustomer(final Long customerId, final String uid) {
        final JSONObject resultJSON = new JSONObject();
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        try {
            final Criteria customerIdNotInCri = new Criteria(new Column("MdVPPTokenDetails", "CUSTOMER_ID"), (Object)customerId, 1);
            final Criteria uIDCri = new Criteria(new Column("MdVPPTokenDetails", "UNIQUE_ID"), (Object)uid, 0);
            final DataObject dataObject = cachedPersistence.get("MdVPPTokenDetails", customerIdNotInCri.and(uIDCri));
            if (!dataObject.isEmpty()) {
                final Row settingsRow = dataObject.getFirstRow("MdVPPTokenDetails");
                final Long custId = (Long)settingsRow.get("CUSTOMER_ID");
                resultJSON.put("CUSTOMER_ID", (Object)custId);
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getVppTokenUUID", e);
        }
        return resultJSON;
    }
    
    public boolean isVppTokenConfigured(final Long customerID) {
        boolean isVppConfigured = false;
        try {
            final SelectQuery tokenQuery = this.getVppTokenDetailsQuery();
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            tokenQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject tokenDO = MDMUtil.getPersistence().get(tokenQuery);
            if (!tokenDO.isEmpty()) {
                isVppConfigured = true;
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in isVppTokenConfigured", e);
        }
        return isVppConfigured;
    }
    
    public Long getVppTokenAddedByUserID(final Long businessStoreID) {
        Long userID = null;
        try {
            final SelectQuery selectQuery = this.getVppTokenDetailsQuery();
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ADDED_BY"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject tokenDO = MDMUtil.getPersistence().get(selectQuery);
            if (!tokenDO.isEmpty()) {
                final Row tokenRow = tokenDO.getFirstRow("MdVPPTokenDetails");
                userID = (Long)tokenRow.get("TOKEN_ADDED_BY");
            }
        }
        catch (final Exception e) {
            VPPTokenDataHandler.logger.log(Level.SEVERE, "Exception in getVppTokenAddedByUserID", e);
        }
        return userID;
    }
    
    static {
        VPPTokenDataHandler.vppTokenDataHandler = null;
        VPPTokenDataHandler.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
}
