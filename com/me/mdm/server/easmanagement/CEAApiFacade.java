package com.me.mdm.server.easmanagement;

import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import javax.transaction.SystemException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CEAApiFacade
{
    private Logger logger;
    
    public CEAApiFacade() {
        this.logger = Logger.getLogger("EASMgmtLogger");
    }
    
    public JSONObject getCEAServerDetails(final JSONObject requestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            if (serverId != -1L) {
                new CEAApiHandler().validateCEAServer(serverId, customerId);
            }
            final JSONObject responseJSON = new CEAApiHandler().getServerDetails(customerId, serverId);
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getCEAServerDetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getCEAServerDetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addCEAServer(final JSONObject requestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            if (bodyJSON == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_name", (Object)userName);
            bodyJSON.put("install_exo_v2", bodyJSON.optBoolean("install_exo_v2", false));
            bodyJSON.put("task", (Object)"add");
            final JSONObject responseJSON = new CEAApiHandler().addOrUpdateCEAServer(bodyJSON);
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- addCEAServer() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addCEAServer() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void modifyCEAServer(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            if (bodyJSON == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_name", (Object)userName);
            bodyJSON.put("server_id", (Object)serverId);
            bodyJSON.put("install_exo_v2", bodyJSON.optBoolean("install_exo_v2", false));
            bodyJSON.put("task", (Object)"update");
            new CEAApiHandler().addOrUpdateCEAServer(bodyJSON);
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- modifyCEAServer() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- modifyCEAServer() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void removeCEAServer(JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            final Boolean rollback_blocked_devices = requestJSON.getJSONObject("msg_header").getJSONObject("filters").getBoolean("rollback_blocked_devices");
            final CEAApiHandler ceaApiHandler = new CEAApiHandler();
            ceaApiHandler.validateCEAServer(serverId, customerId);
            requestJSON = new JSONObject();
            requestJSON.put("server_id", (Object)serverId);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            requestJSON.put("rollback_blocked_devices", (Object)rollback_blocked_devices.toString());
            requestJSON.put("task", (Object)"delete");
            ceaApiHandler.addOrUpdateCEAServer(requestJSON);
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removeCEAServer() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removeCEAServer() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addCEAPolicy(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            if (bodyJSON == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONArray userList = bodyJSON.optJSONArray("user_list");
            if (userList != null && userList.length() > 0) {
                new CEAApiHandler().validateMailBoxDetails(userList, serverId);
            }
            bodyJSON.put("server_id", (Object)serverId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_name", (Object)userName);
            bodyJSON.put("task", (Object)"add");
            final JSONObject responseJSON = new CEAApiHandler().addOrUpdateCEAPolicy(bodyJSON);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- addCEAPolicy() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addCEAPolicy() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteCEAPolicy(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            final Boolean rollback_blocked_devices = requestJSON.getJSONObject("msg_header").getJSONObject("filters").getBoolean("rollback_blocked_devices");
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            requestJSON.put("server_id", (Object)serverId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_name", (Object)userName);
            requestJSON.put("rollback_blocked_devices", (Object)rollback_blocked_devices.toString());
            requestJSON.put("task", (Object)"delete");
            new CEAApiHandler().addOrUpdateCEAPolicy(requestJSON);
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- deleteCEAPolicy() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- deleteCEAPolicy() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getCEAPolicyDetails(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            requestJSON.put("server_id", (Object)serverId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONObject responseJSON = new CEAApiHandler().getCEAPolicyDetails(requestJSON);
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getCEAPolicyDetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getCEAPolicyDetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void modifyCEAPolicy(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long policyId = APIUtil.getResourceID(requestJSON, "polic_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            if (bodyJSON == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            new CEAApiHandler().validateCEAPolicy(serverId, policyId);
            bodyJSON.put("server_id", (Object)serverId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_name", (Object)userName);
            bodyJSON.put("task", (Object)"update");
            bodyJSON.put("policy_id", (Object)policyId);
            new CEAApiHandler().addOrUpdateCEAPolicy(bodyJSON);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- modifyCEAPolicy() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- modifyCEAPolicy() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getCEADevicesDetails(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            requestJSON.put("server_id", (Object)serverId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("customer_id", (Object)customerId);
            final JSONObject responseJSON = new CEAApiHandler().getCEADeviceDetails(requestJSON);
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getCEADevicesDetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getCEADevicesDetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void removeCEADevices(final JSONObject requestJSON) {
        try {
            final Long serverId = APIUtil.getResourceID(requestJSON, "ce_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final Long userId = APIUtil.getUserID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            if (bodyJSON == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONArray devicesJSONArray = bodyJSON.getJSONArray("devices");
            new CEAApiHandler().validateCEADevices(devicesJSONArray, serverId);
            bodyJSON.put("server_id", (Object)serverId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_name", (Object)userName);
            new CEAApiHandler().removeCEADevices(bodyJSON);
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removeCEADevices() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removeCEADevices() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAllMailboxesForServer(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long serverId = APIUtil.getResourceID(apiRequestJSON, "ce_id");
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            return new CEAApiHandler().getMailBoxPickList(apiRequestJSON);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getAllMailboxesForServer() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getAllMailboxesForServer() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void syncCEAServer(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long serverId = APIUtil.getResourceID(apiRequestJSON, "ce_id");
            new CEAApiHandler().validateCEAServer(serverId, customerId);
            new CEAApiHandler().syncCEAServer(serverId, customerId);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- syncCEAServer() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- syncCEAServer() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
