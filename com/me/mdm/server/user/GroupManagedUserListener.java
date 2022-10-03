package com.me.mdm.server.user;

import java.util.HashMap;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.UserEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class GroupManagedUserListener implements ManagedUserListener
{
    private Logger logger;
    
    public GroupManagedUserListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
        try {
            this.logger.log(Level.INFO, ">>  Entering GroupManagedUserListener.userTrashed() ");
            final ArrayList userList = new ArrayList();
            userList.add(userEvent.resourceID);
            final HashMap userMap = new MDMUserHandler().getUserIdsBasedOnType(userList, false);
            final ArrayList mangagedUserList = userMap.get(1);
            if (mangagedUserList != null && !mangagedUserList.isEmpty() && mangagedUserList.contains(userEvent.resourceID)) {
                DataAccess.delete(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)userEvent.resourceID, 0));
            }
            this.logger.log(Level.INFO, "<<  Exiting GroupManagedUserListener.userTrashed() ");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in GroupManagedUserListener.userTrashed() ", e);
        }
    }
}
