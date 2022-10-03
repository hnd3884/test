package com.me.mdm.server.directory;

import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DirectoryFacade
{
    private Logger logger;
    
    public DirectoryFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAllDirectoryTemplate(final JSONObject request) throws APIHTTPException {
        try {
            final DirectoryTemplateHandler handler = new DirectoryTemplateHandler();
            return handler.getAllDirectoryTemplate(request);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getAllDirectoryTemplates] ~~~~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getAllDirectoryTemplates] ~~~~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDirectoryTemplate(final JSONObject message) throws APIHTTPException {
        try {
            final DirectoryTemplateHandler handler = new DirectoryTemplateHandler();
            final Long bind_policy_id = APIUtil.getResourceID(message, "bindpolicytemplate_id");
            final Long customer = APIUtil.getCustomerID(message);
            return handler.getDirectoryTemplate(bind_policy_id, customer);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDirectoryTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDirectoryTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject modifyTemplate(final JSONObject message) throws APIHTTPException, JSONException, DataAccessException {
        try {
            final DirectoryTemplateHandler handler = new DirectoryTemplateHandler();
            return handler.modifyTemplate(message);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:modifyTemplate] ~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "\n~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:modifyTemplate] ~~~~~~~~~~~~~~~~~~~\n", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject createTemplate(final JSONObject message) throws APIHTTPException {
        try {
            final DirectoryTemplateHandler handler = new DirectoryTemplateHandler();
            return handler.createTemplate(message);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:createTemplate] ~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:createTemplate] ~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteTemplate(final JSONObject message) throws APIHTTPException, JSONException, QueryConstructionException, SQLException {
        try {
            final DirectoryTemplateHandler handler = new DirectoryTemplateHandler();
            handler.deleteTemplate(message);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:deleteTemplate] ~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:deleteTemplate] ~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
