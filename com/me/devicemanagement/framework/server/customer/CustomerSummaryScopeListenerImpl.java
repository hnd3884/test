package com.me.devicemanagement.framework.server.customer;

import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.ems.framework.summaryscope.listenerimpl.SummaryScopeCustomerListenerImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.WritableDataObject;
import com.me.ems.framework.summaryscope.utils.SummaryScopeConstants;
import com.me.ems.framework.summaryscope.listener.SummaryScopeEvent;
import com.me.ems.framework.summaryscope.listener.SummaryScopeListener;

public class CustomerSummaryScopeListenerImpl implements SummaryScopeListener
{
    @Override
    public void scopeAdded(final SummaryScopeEvent summaryScopeEvent) {
        if (summaryScopeEvent.getSummaryScopeType() == SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP) {
            final Long summaryScopeId = summaryScopeEvent.getSummaryScopeID();
            try {
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final ArrayList userList = DMUserHandler.getDefaultAdministratorRoleUserList();
                for (final Hashtable userHashTable : userList) {
                    final Long userID = userHashTable.get("LOGIN_ID");
                    final Row techScopeRelRow = new Row("TechnicianScopeRel");
                    techScopeRelRow.set("SUMMARY_SCOPE_ID", (Object)summaryScopeId);
                    techScopeRelRow.set("TECH_ID", (Object)userID);
                    dataObject.addRow(techScopeRelRow);
                }
                SyMUtil.getPersistence().add(dataObject);
            }
            catch (final Exception ex) {
                Logger.getLogger(SummaryScopeCustomerListenerImpl.class.getName()).log(Level.SEVERE, "Exception occurred during Customer added Scope Event. Exception is " + ex.getMessage());
            }
        }
    }
    
    @Override
    public void scopeModified(final SummaryScopeEvent summaryScopeEvent) {
    }
    
    @Override
    public void scopeDeleted(final SummaryScopeEvent summaryScopeEvent) {
    }
    
    @Override
    public void invokeSummaryForAllManaged(final SummaryScopeEvent summaryScopeEvent) {
    }
}
