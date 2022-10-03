package com.me.mdm.uem;

import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.me.mdm.server.apps.AppPermissionFacade;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultFacade;
import com.me.mdm.server.directory.DirectoryTemplateHandler;
import com.me.mdm.server.payload.PayloadException;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.core.management.ManagementConstants;
import java.util.logging.Level;
import com.me.mdm.uem.queue.ModernMgmtOperationData;
import com.me.mdm.uem.queue.ModernMgmtQueueOperation;
import com.me.mdm.uem.queue.ModernMgmtCollectionAssociationData;
import com.me.mdm.server.util.MDMSecurityLogger;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMConfigurationActionListenerImpl
{
    private Logger logger;
    
    public MDMConfigurationActionListenerImpl() {
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
    
    public JSONObject associateModernCollectionToResource(final JSONObject request) {
        final JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "MDMConfigurationActionListenerImpl", "[Modern][Configuration] : A request to associate profile was recieved from legacy : {0}", request);
            new ModernMgmtQueueOperation(1, new ModernMgmtCollectionAssociationData(request)).addToModernMgmtOperationQueue();
            this.logger.log(Level.INFO, "[Modern][Configuration] : The request was completed ");
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            response.put("message", (Object)"request added to queue");
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : Association has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public JSONObject createModernCollection(final JSONObject request) {
        JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "createModernCollection", "[Modern][Configuration] : A request to create a collection was recieved from legacy {0}", request);
            request.getJSONObject("msg_body").put("management_type", (Object)ManagementConstants.Types.MODERN_MGMT);
            response = new ProfileFacade().createProfile(request);
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            this.logger.log(Level.INFO, "[Modern][Configuration] : The request was completed ");
        }
        catch (final PayloadException e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : Create has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", (Throwable)e);
        }
        return response;
    }
    
    public JSONObject publishModernCollection(final JSONObject request) {
        final JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "publishModernCollection", "[Modern][Configuration] : A request to create publish collection was recieved from legacy {0}", request);
            new ProfileFacade().publishProfile(request);
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            response.put("message", (Object)"[Modern][Configuration] : Collection is published");
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : publish has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public JSONObject getModernCollectionDetails(final JSONObject request) {
        JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "getModernCollectionDetails", "[Modern][Configuration] : A request to get  collection deatils was recieved from legacy {0}", request);
            response = new ProfileFacade().getPayloadsFromCollectionID(request);
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            this.logger.log(Level.INFO, "[Modern][Configuration] : Details returned successfully {0}", response);
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : get has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public JSONObject modifyModernCollection(final JSONObject request) {
        JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "modifyModernCollection", "[Modern][Configuration] : A request to modify a collection was recieved from legacy {0}", request);
            response = new ProfileFacade().modifyCollection(request);
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            this.logger.log(Level.INFO, "[Modern][Configuration] : Profile modified successfully");
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : modify has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public JSONObject getMDMProfileUtlAPIs(final JSONObject request) {
        JSONObject response = null;
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "getMDMProfileUtlAPIs", "[Modern][Configuration] : A request for MDMProfileUtil was received from legacy {0}", request);
            final JSONObject jsonObject = request.getJSONObject("msg_body");
            final JSONObject header = request.getJSONObject("msg_header");
            final String string;
            final String operation = string = header.getString("operation");
            switch (string) {
                case "bindpolicytemplate": {
                    final Long bindPolicyID = jsonObject.getLong("BIND_POLICY_ID");
                    final Long customerID = jsonObject.getLong("CUSTOMER_ID");
                    response = new DirectoryTemplateHandler().getDirectoryTemplate(bindPolicyID, customerID);
                    break;
                }
                case "filevaults": {
                    final Long encryptionID = jsonObject.getLong("ENCRYPTION_SETTINGS_ID");
                    final JSONObject resourceJSON = new JSONObject();
                    resourceJSON.put("filevault_id", (Object)encryptionID);
                    header.put("resource_identifier", (Object)resourceJSON);
                    response = new MDMFileVaultFacade().getFileVaultDetail(request);
                    break;
                }
                case "apppermissions": {
                    final Long encryptionID = jsonObject.getLong("APP_PERMISSION_CONFIG_ID");
                    final JSONObject resourceJSON = new JSONObject();
                    resourceJSON.put("apppermission_id", (Object)encryptionID);
                    header.put("resource_identifier", (Object)resourceJSON);
                    response = new AppPermissionFacade().getAppPermissionDetails(request);
                    break;
                }
            }
            this.logger.log(Level.INFO, "[Modern][Configuration] : MDMProfileUtil successfully executed");
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : MDMProfileUtil has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public JSONObject moveCollectionToTrash(final JSONObject request) {
        final JSONObject response = new JSONObject();
        try {
            MDMSecurityLogger.info(this.logger, "MDMConfigurationActionListenerImpl", "moveCollectionToTrash", "[Modern][Configuration] : A request to trash a collection was recieved from legacy {0}", request);
            final JSONObject body = request.getJSONObject("msg_body");
            request.put("move_to_trash", (Object)Boolean.TRUE);
            Object[] collectionIDs;
            if (body.get("COLLECTION_ID") instanceof JSONArray) {
                final JSONArray jsonArray = body.getJSONArray("COLLECTION_ID");
                collectionIDs = new Object[jsonArray.length()];
                for (int index = 0; index < jsonArray.length(); ++index) {
                    collectionIDs[index] = jsonArray.get(index);
                }
            }
            else {
                collectionIDs = (Object[])body.get("COLLECTION_ID");
            }
            final SelectQuery selectQuery = QueryConstructor.get("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionIDs, 8));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Iterator iterator = dataObject.getRows("ProfileToCollection");
            final List list = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long profileID = (Long)row.get("PROFILE_ID");
                list.add(profileID);
            }
            body.put("profile_ids", (Object)new JSONArray((Collection)list));
            new ProfileFacade().deleteOrTrashProfile(request);
            response.put("isSuccessfull", (Object)Boolean.TRUE);
            this.logger.log(Level.INFO, "[Modern][Configuration] : trashed successfully ");
        }
        catch (final Exception e) {
            response.put("isSuccessfull", (Object)Boolean.FALSE);
            response.put("message", (Object)"failed to add queue");
            this.logger.log(Level.WARNING, "[Modern][Configuration] : trashing has failed {0}", request);
            this.logger.log(Level.SEVERE, "Exception Trace", e);
        }
        return response;
    }
    
    public static final class ProfileUtilAPIConstants
    {
        public static final String OPERATION = "operation";
        
        public static final class DirectoryBindingConstants
        {
            public static final String OPERATION_KEY = "bindpolicytemplate";
        }
        
        public static final class FilevaultConstants
        {
            public static final String OPERATION_KEY = "filevaults";
        }
        
        public static final class AppPermissionConstants
        {
            public static final String OPERATION_KEY = "apppermissions";
        }
    }
}
