package com.me.mdm.core.auth;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public abstract class MDMUserAPIKeyGenerator implements MDMAPIKeyGeneratorAPI
{
    private static MDMAPIKeyGeneratorAPI mdmAPIKeyGeneratorAPI;
    public static final String USER_API_KEY = "zapikey";
    protected static final Logger LOGGER;
    
    public static MDMAPIKeyGeneratorAPI getInstance() {
        if (MDMUserAPIKeyGenerator.mdmAPIKeyGeneratorAPI == null) {
            try {
                if (CustomerInfoUtil.isSAS) {
                    MDMUserAPIKeyGenerator.mdmAPIKeyGeneratorAPI = (MDMAPIKeyGeneratorAPI)Class.forName("com.me.mdmcloud.server.authentication.MDMCloudUserAPIKeyGenerator").newInstance();
                }
                else {
                    MDMUserAPIKeyGenerator.mdmAPIKeyGeneratorAPI = (MDMAPIKeyGeneratorAPI)Class.forName("com.me.mdm.onpremise.server.authentication.MDMOnPremiseUserAPIKeyGenerator").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getUserAPIKeyGenerator... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getUserAPIKeyGenerator...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getUserAPIKeyGenerator...", ie2);
            }
            catch (final Exception ex) {
                MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getUserAPIKeyGenerator...", ex);
            }
        }
        return MDMUserAPIKeyGenerator.mdmAPIKeyGeneratorAPI;
    }
    
    @Override
    public APIKey generateAPIKey(final JSONObject json) {
        try {
            return this.createAPIKey(json);
        }
        catch (final Exception ex) {
            MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "Exception in creatingAPIKey", ex);
            return null;
        }
    }
    
    @Override
    public APIKey getAPIKey(final JSONObject json) {
        return this.generateAPIKey(json);
    }
    
    @Override
    public APIKey updateAPIKey(final JSONObject json) {
        return null;
    }
    
    @Override
    public boolean validateAPIKey(final JSONObject json) {
        Boolean isValid = Boolean.FALSE;
        try {
            final Long userID = json.getLong("USER_ID");
            final String templateToken = json.getString("templateToken");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
            final Criteria userCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "ADDED_USER"), (Object)userID, 0);
            final Criteria tokenCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TOKEN"), (Object)templateToken, 0);
            selectQuery.setCriteria(userCriteria.and(tokenCriteria));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                isValid = Boolean.TRUE;
            }
        }
        catch (final Exception e) {
            MDMUserAPIKeyGenerator.LOGGER.log(Level.SEVERE, "Exception in validating API key ", e);
        }
        return isValid;
    }
    
    @Override
    public void revokeAPIKey(final JSONObject json) {
    }
    
    static {
        MDMUserAPIKeyGenerator.mdmAPIKeyGeneratorAPI = null;
        LOGGER = Logger.getLogger(MDMUserAPIKeyGenerator.class.getName());
    }
}
