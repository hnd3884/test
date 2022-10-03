package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class CollectionToAppUpdateConfigHandler
{
    private static CollectionToAppUpdateConfigHandler collectionToAppUpdateConfigHandler;
    
    public static CollectionToAppUpdateConfigHandler getInstance() {
        if (CollectionToAppUpdateConfigHandler.collectionToAppUpdateConfigHandler == null) {
            CollectionToAppUpdateConfigHandler.collectionToAppUpdateConfigHandler = new CollectionToAppUpdateConfigHandler();
        }
        return CollectionToAppUpdateConfigHandler.collectionToAppUpdateConfigHandler;
    }
    
    public void addCollectionToAppUpdateConfigRelation(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws DataAccessException {
        final Row row = new Row("AutoAppUpdateConfigToCollection");
        row.set("APP_UPDATE_CONF_ID", appUpdatePolicyModel.getAppUpdateConfigId());
        row.set("COLLECTION_ID", (Object)appUpdatePolicyModel.getCollectionId());
        row.set("DEPLOYMENT_TEMPLATE_ID", appUpdatePolicyModel.getDeploymentTemplateId());
        dataObject.addRow(row);
    }
    
    static {
        CollectionToAppUpdateConfigHandler.collectionToAppUpdateConfigHandler = null;
    }
}
