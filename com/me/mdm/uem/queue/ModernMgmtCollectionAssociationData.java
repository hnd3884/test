package com.me.mdm.uem.queue;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.List;

public class ModernMgmtCollectionAssociationData implements ModernMgmtOperationData
{
    Long collectionID;
    List resList;
    Integer operationType;
    Long userID;
    Long customerID;
    Logger logger;
    private static int ASSOCIATE_PROFILE;
    private static int DIS_ASSOCIATE_PROFILE;
    
    public ModernMgmtCollectionAssociationData(final JSONObject jsonObject) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.collectionID = jsonObject.getLong("COLLECTION_ID");
        this.userID = jsonObject.getLong("USER_ID");
        this.customerID = jsonObject.getLong("CUSTOMER_ID");
        final JSONArray jsonArray = jsonObject.getJSONArray("RESOURCE_ID");
        final List arrayList = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final Long resID = jsonArray.getLong(i);
            arrayList.add(resID);
        }
        this.resList = arrayList;
        this.operationType = jsonObject.getInt("operation_type");
    }
    
    @Override
    public void processData() {
        try {
            this.logger.log(Level.INFO, "[Modern][Configuration] : going to process profile {2} For resource {0} collection ID {1} which was delegated from Legacy", new Object[] { this.resList, this.collectionID, (this.operationType == ModernMgmtCollectionAssociationData.ASSOCIATE_PROFILE) ? "Assocaite" : "disassocaite" });
            if (this.operationType == ModernMgmtCollectionAssociationData.ASSOCIATE_PROFILE) {
                new ProfileAssociateHandler().associateCollectionToResources(this.collectionID, this.resList, this.customerID, this.userID);
            }
            else if (this.operationType == ModernMgmtCollectionAssociationData.DIS_ASSOCIATE_PROFILE) {
                new ProfileAssociateHandler().disassociateCollectionToResources(this.collectionID, this.resList, this.customerID, this.userID);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "[Modern][Configuration] : failed to perform the delegated modern operation", (Throwable)e);
        }
    }
    
    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("COLLECTION_ID", (Object)this.collectionID);
        jsonObject.put("USER_ID", (Object)this.userID);
        jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
        final JSONArray jsonArray = new JSONArray();
        for (final Long resID : this.resList) {
            jsonArray.put((Object)resID);
        }
        jsonObject.put("RESOURCE_ID", (Object)jsonArray);
        jsonObject.put("operation_type", (Object)this.operationType);
        return jsonObject;
    }
    
    static {
        ModernMgmtCollectionAssociationData.ASSOCIATE_PROFILE = 1;
        ModernMgmtCollectionAssociationData.DIS_ASSOCIATE_PROFILE = 2;
    }
}
