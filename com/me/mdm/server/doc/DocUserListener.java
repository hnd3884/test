package com.me.mdm.server.doc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.UserEvent;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class DocUserListener implements ManagedUserListener
{
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
        DocMgmt.logger.log(Level.INFO, "Entering DocUserListener:userDeleted");
        try {
            final Long userResID = userEvent.resourceID;
            final JSONArray docsAssociatedToUser = DocMgmtDataHandler.getInstance().getDocsAssociatedToUser(userResID);
            if (docsAssociatedToUser == null || docsAssociatedToUser.length() == 0) {
                return;
            }
            final JSONObject docUserAssociation = new JSONObject();
            docUserAssociation.put("ASSOCIATE", false);
            docUserAssociation.put("DOC_ID", (Object)docsAssociatedToUser);
            docUserAssociation.put("MANAGED_USER_ID", (Object)String.valueOf(userResID));
            final Long[] customerIDs = { userEvent.customerID };
            DocMgmt.getInstance().saveDocDeviceAssociation(customerIDs, docUserAssociation);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        DocMgmt.logger.log(Level.INFO, "Exiting DocUserListener:userDeleted");
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
    }
}
