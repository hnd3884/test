package com.me.mdm.server.doc;

import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.GroupEvent;
import com.adventnet.sym.server.mdm.group.MDMCustomGroupListner;

public class DocCGListener implements MDMCustomGroupListner
{
    @Override
    public void customGroupAdded(final GroupEvent groupEvent) {
    }
    
    @Override
    public void customGroupDeleted(final GroupEvent groupEvent) {
        try {
            final Long custID = groupEvent.customerID;
            final List<Long> docsList = DocMgmtDataHandler.getInstance().getDocsList(custID);
            DocSummaryHandler.getInstance().reviseDocSummary(docsList);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void customGroupModified(final GroupEvent groupEvent) {
    }
    
    @Override
    public void customGroupPreDelete(final GroupEvent groupEvent) {
    }
}
