package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.AppsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2MacOSAppPayload implements DO2Payload
{
    public Logger logger;
    
    public DO2MacOSAppPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final AppsPayload[] payloadArray = { null };
        AppsPayload appsPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("InstallAppPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final long appId = (long)row.get("APP_ID");
                final Criteria criteria = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
                final Row apppkgRow = dataObject.getRow("MdPackageToAppData", criteria);
                final Long packageID = (Long)apppkgRow.get("PACKAGE_ID");
                final Criteria appCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appId, 0);
                final Row appRow = dataObject.getRow("MdAppDetails", appCriteria);
                final Long customerID = (Long)appRow.get("CUSTOMER_ID");
                final String manifestFileUrl = MDMAppMgmtHandler.getInstance().getAppRepositoryFolderPath(customerID, packageID, appId) + File.separator + "manifest.plist";
                appsPayload = new AppsPayload();
                appsPayload.setPinningRevocationCheckRequired(Boolean.FALSE);
                appsPayload.setRequestType("InstallEnterpriseApplication");
                if (manifestFileUrl != null && !manifestFileUrl.equals("")) {
                    appsPayload.setManifest(manifestFileUrl);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in creating MacOS Enterprise App Payload", ex);
        }
        payloadArray[0] = appsPayload;
        return payloadArray;
    }
}
