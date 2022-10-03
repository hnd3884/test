package com.me.mdm.server.inv;

import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.persistence.Row;
import com.me.mdm.server.inv.ios.DeviceInstalledCertificateDataHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;

public class InventoryCertificateDataHandler
{
    private Logger logger;
    
    public InventoryCertificateDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject wrappedJSON = new JSONObject(inventoryObject.strData);
            final JSONArray certificateArray = wrappedJSON.getJSONArray("CertificateList");
            final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final List certificateId = new ArrayList();
            final DeviceInstalledCertificateDataHandler certificateDataHandler = new DeviceInstalledCertificateDataHandler();
            final DataObject alreadyAvailableCertificate = certificateDataHandler.getCertificateObjectForResource(inventoryObject.resourceId, null);
            if (!alreadyAvailableCertificate.isEmpty()) {
                final Iterator iterator = alreadyAvailableCertificate.getRows("MdCertificateResourceRel");
                while (iterator.hasNext()) {
                    final Row certificateRow = iterator.next();
                    certificateId.add(certificateRow.get("CERTIFICATE_ID"));
                }
            }
            MDMInvDataPopulator.getInstance().deleteScepCertDetails(inventoryObject.resourceId);
            MDMInvDataPopulator.getInstance().deleteCertToResourceRelDetails(inventoryObject.resourceId);
            if (certificateArray != null) {
                for (int i = 0; i < certificateArray.length(); ++i) {
                    final JSONObject certificateInfoDetails = (JSONObject)certificateArray.get(i);
                    MDMInvDataPopulator.getInstance().addOrUpdateCertificatesInfo(inventoryObject.resourceId, certificateInfoDetails, dataObject);
                }
                if (!dataObject.isEmpty()) {
                    this.logger.log(Level.INFO, "Going to update the certificate to resource:{0}", new Object[] { inventoryObject.resourceId });
                    MDMUtil.getPersistence().add(dataObject);
                }
                dataObject.deleteRows("MdCertificateInfo", new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_ID"), (Object)certificateId.toArray(), 8));
                final JSONArray certificateList = certificateDataHandler.getUnmanagedCertificateDetails(inventoryObject.resourceId, null);
                certificateDataHandler.getUnmanagedCertificateDetailsAndPost(dataObject, true, false, inventoryObject.resourceId);
            }
            else {
                this.logger.log(Level.INFO, "Cerificate Array Empty.");
            }
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
