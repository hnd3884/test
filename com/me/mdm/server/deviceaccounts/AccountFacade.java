package com.me.mdm.server.deviceaccounts;

import com.me.mdm.server.adep.mac.AccountConfiguration;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AccountFacade
{
    private Logger logger;
    
    public AccountFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAllComputerAccounts(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            return handler.getAllAccounts(request);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting all the accounts", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting all the accounts", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getComputerAccount(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long customerID = APIUtil.getCustomerID(request);
            final Long accountID = APIUtil.getResourceID(request, "account_id");
            return handler.getAccountDetails(customerID, accountID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting  the accounts", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting  the accounts", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrModifyAccount(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long customerID = APIUtil.getCustomerID(request);
            final JSONObject msgBody = request.getJSONObject("msg_body");
            final Long accountID = APIUtil.getResourceID(request, "account_id");
            msgBody.put("ACCOUNT_ID", (Object)accountID);
            msgBody.put("CUSTOMER_ID", (Object)customerID);
            return handler.addOrUpdateAccount(msgBody);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting all the accounts", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting all the accounts", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteAccountDetails(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long accountID = APIUtil.getResourceID(request, "account_id");
            final Long customerID = APIUtil.getCustomerID(request);
            handler.deleteComputerAccount(customerID, accountID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in deleting the account", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in deleting the account", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAllAccountConfiguration(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long customerID = APIUtil.getCustomerID(request);
            final JSONObject response = new JSONObject();
            response.put("Configurations", (Object)handler.getAllMacAccountConfiguration(customerID));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting  the account configuration", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting  the account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAccountConfiguration(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long customerID = APIUtil.getCustomerID(request);
            final Long configID = APIUtil.getResourceID(request, "configuration_id");
            return handler.getMacAccountConfiguration(customerID, configID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting the account configuration", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting  the account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrModifyAccountConfiguration(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long customerID = APIUtil.getCustomerID(request);
            final JSONObject msgBody = request.getJSONObject("msg_body");
            final Long configID = APIUtil.getResourceID(request, "configuration_id");
            msgBody.put("ACCOUNT_CONFIG_ID", (Object)configID);
            msgBody.put("CUSTOMER_ID", (Object)customerID);
            return handler.addOrUpdateMacAccountConfig(msgBody);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in add or modify account configuration", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in add or modify account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAccountConfigForEnrollTemplate(final JSONObject request) {
        try {
            final Long enrollTemplateID = APIUtil.getResourceID(request, "enrollmenttemplate_id");
            final AccountConfiguration configuration = new AccountConfiguration();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("ACCOUNT_CONFIG_ID".toLowerCase(), (Object)configuration.getAccountConfigIDForDEPEnrollTemplate(enrollTemplateID));
            return jsonObject;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in getting account configuration for DEP token", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting account configuration for DEP token", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrModifyAccountConfigToEnrollTemplate(final JSONObject request) throws APIHTTPException {
        try {
            final AccountConfiguration configuration = new AccountConfiguration();
            final Long configID = APIUtil.getResourceID(request, "accountconfiguration_id");
            final Long depEnrollTemplateID = APIUtil.getResourceID(request, "enrollmenttemplate_id");
            configuration.addOrModifyAccounntConfigurationToDEPEnrollTemplate(configID, depEnrollTemplateID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in add or modify account configuration to DEP", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in add or modify account configuration to DEP", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void disassociateAccountConfig(final JSONObject request) throws APIHTTPException {
        try {
            final AccountConfiguration configuration = new AccountConfiguration();
            final Long depEnrollTemplateID = APIUtil.getResourceID(request, "enrollmenttemplate_id");
            configuration.deleteAccountConfigurationToDEPEnrollTemplate(depEnrollTemplateID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in deleting account configuration to DEP", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in deleting account configuration to DEP", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteAccountConfiguration(final JSONObject request) throws APIHTTPException {
        try {
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            final Long configID = APIUtil.getResourceID(request, "configuration_id");
            final Long customerID = APIUtil.getCustomerID(request);
            handler.deleteMacAccountConfig(customerID, configID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in deleting the account configuration", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in deleting the account configuration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
