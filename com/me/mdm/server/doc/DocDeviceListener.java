package com.me.mdm.server.doc;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class DocDeviceListener extends ManagedDeviceListener
{
    private JSONObject prepareRequest(final DeviceEvent deviceEvent) throws JSONException, DataAccessException {
        final Long deviceResID = deviceEvent.resourceID;
        final JSONArray docsAssociatedToDevices = DocMgmtDataHandler.getInstance().getDocsAssociatedToDevice(deviceResID);
        if (docsAssociatedToDevices == null || docsAssociatedToDevices.length() == 0) {
            return null;
        }
        final JSONObject docDeviceAssociation = new JSONObject();
        docDeviceAssociation.put("DOC_ID", (Object)docsAssociatedToDevices);
        docDeviceAssociation.put("MANAGEDDEVICE_ID", (Object)String.valueOf(deviceResID));
        return docDeviceAssociation;
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocDeviceListener:devicePreDelete");
        try {
            final JSONObject docDeviceAssociation = this.prepareRequest(deviceEvent);
            if (docDeviceAssociation == null) {
                return;
            }
            docDeviceAssociation.put("ASSOCIATE", false);
            final Long[] customerIDs = { deviceEvent.customerID };
            DocMgmt.getInstance().saveDocDeviceAssociation(customerIDs, docDeviceAssociation);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        DocMgmt.logger.log(Level.INFO, "Exiting DocDeviceListener:devicePreDelete");
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent deviceEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocDeviceListener:deviceDeleted");
        this.devicePreDelete(deviceEvent);
        DocMgmt.logger.log(Level.INFO, "Exiting DocDeviceListener:deviceDeleted");
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocDeviceListener:deviceUnmanaged");
        this.devicePreDelete(deviceEvent);
        DocMgmt.logger.log(Level.INFO, "Exiting DocDeviceListener:deviceUnmanaged");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocDeviceListener:deviceManaged");
        final Long deviceResID = deviceEvent.resourceID;
        final HashMap enrolledDeviceToManagedUser = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceResID);
        final Long managedUserID = enrolledDeviceToManagedUser.get("MANAGED_USER_ID");
        DocMgmt.logger.log(Level.INFO, "device resourceID = {0} managed user id = {1}", new Object[] { deviceResID, managedUserID });
        try {
            final JSONArray userDocDetails = DocMgmtDataHandler.getInstance().getDocsAssociatedToUser(managedUserID);
            if (userDocDetails == null || userDocDetails.length() == 0) {
                DocMgmt.logger.log(Level.INFO, "device resourceID = {0} managed user id = {1} has no docs", new Object[] { deviceResID, managedUserID });
                return;
            }
            final JSONArray jsonArray = new JSONArray();
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentPolicyResourceRel", new Criteria(Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), (Object)managedUserID, 0));
            for (int i = 0; i < userDocDetails.length(); ++i) {
                final JSONObject obj = userDocDetails.getJSONObject(i);
                final Long docId = obj.getLong("DOC_ID");
                obj.put("MANAGEDDEVICE_ID", (Object)String.valueOf(deviceResID));
                final Row row = dObj.getRow("DocumentPolicyResourceRel", new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docId, 0));
                if (row != null) {
                    final Long policyId = (Long)row.get("DEPLOYMENT_POLICY_ID");
                    if (policyId != null) {
                        obj.put("DEPLOYMENT_POLICY_ID", (Object)policyId);
                    }
                }
                jsonArray.put((Object)obj);
            }
            DocMgmt.logger.log(Level.INFO, "device resourceID = {0} managed user id = {1} docIds ={2}", new Object[] { deviceResID, managedUserID, userDocDetails.toString() });
            final JSONObject docDeviceAssociation = new JSONObject();
            docDeviceAssociation.put("DOC_ID", (Object)jsonArray);
            docDeviceAssociation.put("ASSOCIATE", true);
            final Long[] customerIDs = { deviceEvent.customerID };
            DocMgmt.getInstance().saveDocDeviceAssociation(customerIDs, docDeviceAssociation);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        DocMgmt.logger.log(Level.INFO, "Exiting DocDeviceListener:deviceManaged");
    }
    
    @Override
    public void userAssigned(final DeviceEvent userEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocDeviceListener:userAssigned");
        this.deviceUnmanaged(userEvent);
        this.deviceManaged(userEvent);
        DocMgmt.logger.log(Level.INFO, "Exiting DocDeviceListener:userAssigned");
    }
}
