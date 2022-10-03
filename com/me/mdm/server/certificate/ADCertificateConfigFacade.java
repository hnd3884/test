package com.me.mdm.server.certificate;

import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONArray;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.paging.PagingUtil;
import java.util.logging.Logger;

public class ADCertificateConfigFacade
{
    Logger logger;
    
    public ADCertificateConfigFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getADCertConfigurations(final Long customerID, final Long collectionID, final PagingUtil pagingUtil) throws Exception {
        this.logger.log(Level.INFO, "getADCertConfigurations:- for customerID={0}", customerID);
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject meta = new JSONObject();
            final DataObject dataObject = MDMConfigUtil.getConfigDataItemDOByCollectionId(765, collectionID);
            if (dataObject.isEmpty() || dataObject.getRow("ConfigDataItem") == null) {
                meta.put("total_record_count", 0);
                responseJSON.put("metadata", (Object)meta);
                responseJSON.put("adcs", (Object)new JSONArray());
                return responseJSON;
            }
            final Row row = dataObject.getFirstRow("ConfigDataItem");
            final Long configID = (Long)row.get("CONFIG_DATA_ITEM_ID");
            final Long certID = (Long)DBUtil.getValueFromDB("ADCertPolicy", "CONFIG_DATA_ITEM_ID", (Object)configID, "AD_CONFIG_ID");
            final Criteria certIDCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certID, 0);
            final int adcsCount = ProfileCertificateUtil.getInstance().getADCertConfigCount(customerID, certIDCriteria);
            meta.put("total_record_count", adcsCount);
            responseJSON.put("metadata", (Object)meta);
            final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
            responseJSON.put("adcs", (Object)ProfileCertificateUtil.getInstance().getADCertConfigDetails(customerID, certIDCriteria, range));
            if (adcsCount != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(adcsCount);
                if (pagingJSON != null) {
                    responseJSON.put("paging", (Object)pagingJSON);
                }
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getADCertConfigurations:- ", ex);
            throw ex;
        }
    }
    
    public JSONObject addADCertConfiguration(final Long customerID, final Long loginID, final JSONObject adCertJSON) throws Exception {
        this.logger.log(Level.INFO, "addADCertConfiguration:- for customerID={0},loginID={1}, adCertJSON={2}", new Object[] { customerID, loginID, adCertJSON });
        try {
            final String eventLogRemarks = "mdm.mgmt.adcs.server_added";
            final int eventConstant = 72430;
            this.logger.log(Level.INFO, "going to add AD Certificate server with details {0}", adCertJSON);
            final Long adCertConfigID = adCertJSON.optLong("AD_CONFIG_ID");
            final JSONObject adCertDBJson = ProfileCertificateUtil.getInstance().addOrUpdateADCertConfiguration(customerID, adCertConfigID, adCertJSON);
            this.logger.log(Level.INFO, "ADCS configuration is added and added config detail is {0}", adCertDBJson);
            final String arg = adCertDBJson.getString("AD_CONFIG_NAME".toLowerCase());
            final List remarkList = new ArrayList();
            remarkList.add(arg);
            MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(loginID), eventLogRemarks, remarkList, customerID, System.currentTimeMillis());
            return adCertDBJson;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addADCertConfiguration:- ", ex);
            throw ex;
        }
    }
    
    public JSONObject getADCertServer(final Long customerID, final Long adConfigID) throws Exception {
        this.logger.log(Level.INFO, "getADCertServer:- customerID={0}, adConfigID={1}", new Object[] { customerID, adConfigID });
        try {
            JSONObject responseJSON = new JSONObject();
            final Criteria configIDCriteria = new Criteria(new Column("ADCertConfiguration", "AD_CONFIG_ID"), (Object)adConfigID, 0);
            final JSONArray jsonArray = ProfileCertificateUtil.getInstance().getADCertConfigDetails(customerID, configIDCriteria, null);
            if (jsonArray.length() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { String.valueOf(adConfigID) });
            }
            responseJSON = jsonArray.getJSONObject(0);
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getADCertServer:- ", ex);
            throw ex;
        }
    }
    
    public JSONObject deleteADCertificateServer(final Long loginID, final Long userID, final Long customerID, final Long adConfigID, final boolean isRedistributionNeed) throws Exception {
        this.logger.log(Level.INFO, "deleteADCertificateServer:- for loginID={0}, userID={1}, customerID={2}, adConfigID={3}", new Object[] { loginID, userID, customerID, adConfigID });
        try {
            String eventLogRemarks = "mdm.mgmt.adcs.server_deleted";
            final int eventConstant = 72432;
            if (!APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "All_Managed_Mobile_Devices" })) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            this.logger.log(Level.INFO, "Going to delete ADCS for the config id ", adConfigID);
            final JSONObject adCertJson = this.getADCertServer(customerID, adConfigID);
            final List templates = new ArrayList();
            templates.add(adConfigID);
            this.logger.log(Level.INFO, "The following templates will be moved to trash as a part of server delete ", templates);
            if (isRedistributionNeed) {
                eventLogRemarks += "_redistributed";
            }
            new ProfilePayloadOperator(ProfileCertificateUtil.getInstance().certificatesMapptedTableList, ProfileCertificateUtil.getInstance().unConfigureMap).performPayloadOperation(templates, customerID, userID, -1L, Boolean.TRUE, isRedistributionNeed);
            ProfileCertificateUtil.getInstance().moveCertificatesToTrash(templates, customerID);
            ProfileCertificateUtil.getInstance().deleteADCertConfig(adConfigID);
            final String arg = adCertJson.optString("AD_CONFIG_NAME".toLowerCase());
            final List remarkList = new ArrayList();
            remarkList.add(arg);
            MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(loginID), eventLogRemarks, remarkList, customerID, System.currentTimeMillis());
            this.logger.log(Level.INFO, "ADCS was deleted for the config id {0}", adConfigID);
            final JSONObject response = new JSONObject();
            response.put("status", (Object)Boolean.TRUE);
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleteADCertificateServer:- ", ex);
            throw ex;
        }
    }
    
    public JSONObject modifyADCertServerDetails(final Long loginID, final Long userID, final Long customerID, final Long adConfigID, final boolean isRedistributionNeed, final JSONObject adCertJson) throws Exception {
        this.logger.log(Level.INFO, "modifyADCertServerDetails:- for loginID={0}, userID={1}, customerID={2}, adConfigID={3}, isRedistributionNeed={4}, adCertJSON={5}", new Object[] { loginID, userID, customerID, adConfigID, isRedistributionNeed, adCertJson });
        try {
            String eventLogRemarks = "mdm.mgmt.adcs.server_modified";
            final int eventConstant = 72431;
            if (!APIUtil.getNewInstance().checkRolesForCurrentUser(new String[] { "All_Managed_Mobile_Devices" })) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            this.getADCertServer(customerID, adConfigID);
            this.logger.log(Level.INFO, "modify ADCS request is received for config id ", adConfigID);
            final JSONObject adCertDbJSON = ProfileCertificateUtil.getInstance().addOrUpdateADCertConfiguration(customerID, adConfigID, adCertJson);
            this.logger.log(Level.INFO, "modify ADCS request is done for config id ", adConfigID);
            final String arg = adCertDbJSON.getString("AD_CONFIG_NAME".toLowerCase());
            final List remarkList = new ArrayList();
            remarkList.add(arg);
            final boolean isUrlChanged = adCertDbJSON.getBoolean("isUrlChanged");
            if (isRedistributionNeed || isUrlChanged) {
                eventLogRemarks = ((isRedistributionNeed == Boolean.TRUE) ? (eventLogRemarks + "_redistributed") : (eventLogRemarks + "_republished"));
                final List templates = new ArrayList();
                templates.add(adConfigID);
                this.logger.log(Level.INFO, "redistribution is needed because isRedistributionNeed = {0} or isUrlChanged = {1} and the republished templates are = {2}", new Object[] { isRedistributionNeed, isUrlChanged, templates });
                new ProfilePayloadOperator(ProfileCertificateUtil.getInstance().certificatesMapptedTableList, ProfileCertificateUtil.getInstance().unConfigureMap).rePublishPayloadProfiles(templates, customerID, userID, isRedistributionNeed, Boolean.FALSE);
            }
            MDMEventLogHandler.getInstance().addEvent(eventConstant, DMUserHandler.getDCUser(loginID), eventLogRemarks, remarkList, customerID, System.currentTimeMillis());
            final JSONObject response = new JSONObject();
            response.put("AD_CONFIG_ID", (Object)adConfigID);
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in modifyADCertServerDetails:- ", ex);
            throw ex;
        }
    }
}
