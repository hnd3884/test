package com.me.ems.framework.summaryscope.listenerimpl;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.ScopeEvent;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Properties;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import com.me.ems.framework.summaryscope.utils.SummaryScopeUtil;
import com.me.ems.framework.summaryscope.listener.SummaryScopeEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.ems.framework.summaryscope.utils.SummaryScopeHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.summaryscope.utils.SummaryScopeConstants;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class SummaryScopeUserListenerImpl extends AbstractUserListener
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void userAdded(final UserEvent userEvent) {
        final Long techID = userEvent.loginID;
        final Integer scope = userEvent.scope;
        List<Long> scopeList = userEvent.scopeList;
        Integer scopeType = SummaryScopeConstants.REMOTE_OFFICE;
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            try {
                if (scope.equals(1)) {
                    scopeType = SummaryScopeConstants.CUSTOM_GROUP;
                }
                else if (scope.equals(0)) {
                    scopeType = SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP;
                    if (CustomerInfoUtil.getInstance().isMSP()) {
                        scopeList = userEvent.managedCustomers;
                    }
                    else {
                        scopeList = CustomerInfoUtil.getInstance().getCustomerIdList();
                    }
                }
                final Long summaryScopeID = SummaryScopeHandler.getInstance().addTechnicianScope(techID, scopeList, scopeType);
                SummaryScopeUserListenerImpl.logger.log(Level.INFO, SummaryScopeUserListenerImpl.sourceClass + ":" + "userAdded" + ": TechID:" + techID + ", SummaryScopeID:" + summaryScopeID + " summaryScopeType:" + scopeType);
                final List<Long> valueList = new ArrayList<Long>();
                valueList.add(techID);
                final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.TECHNICIAN, valueList);
                SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_ADDED);
            }
            catch (final Exception ex) {
                SummaryScopeUserListenerImpl.logger.log(Level.SEVERE, "Exception while adding user and summary scope hanlding", ex);
            }
        }
        else {
            final List<Long> customerIdlist = userEvent.managedCustomers;
            SummaryScopeUtil.getInstance().fetchandCreateSummaryScopeMapping(customerIdlist.toArray(new Long[customerIdlist.size()]), SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, userEvent.loginID, SummaryScopeConstants.TECHNICIAN);
        }
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
        final List<Long> delSummaryScopeID = SummaryScopeUtil.getInstance().clearScopeRel(userEvent.loginID, SummaryScopeConstants.TECHNICIAN);
        final List<Long> valueList = new ArrayList<Long>();
        valueList.add(userEvent.loginID);
        SummaryScopeUserListenerImpl.logger.log(Level.INFO, SummaryScopeUserListenerImpl.sourceClass + ":" + "userDeleted" + ". TechID" + userEvent.loginID + ". Affected ScopeIDs:" + Arrays.toString(delSummaryScopeID.toArray()));
        for (final Long summaryScopeID : delSummaryScopeID) {
            final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.TECHNICIAN, valueList);
            SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_DELETED);
        }
    }
    
    @Override
    public void userModified(final UserEvent userEvent) {
        final Boolean scopeChanged = userEvent.isUserScopeChanged;
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            if (scopeChanged) {
                final Long techID = userEvent.loginID;
                final Integer scope = userEvent.scope;
                List<Long> scopeList = userEvent.scopeList;
                Integer scopeType = SummaryScopeConstants.REMOTE_OFFICE;
                if (scope.equals(1)) {
                    scopeType = SummaryScopeConstants.CUSTOM_GROUP;
                }
                else if (scope.equals(0)) {
                    scopeType = SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP;
                    scopeList = CustomerInfoUtil.getInstance().getCustomerIDsForLogIn(userEvent.loginID);
                }
                final Long summaryScopeID = SummaryScopeHandler.getInstance().modifyTechnicianScope(techID, scopeList, scopeType);
                SummaryScopeUserListenerImpl.logger.log(Level.INFO, SummaryScopeUserListenerImpl.sourceClass + ":" + "userModified" + ": TechID:" + techID + ", newSummaryScopeID:" + summaryScopeID + " summaryScopeType:" + scopeType);
                final List<Long> valueList = new ArrayList<Long>();
                valueList.add(techID);
                final Properties scopeProps = new Properties();
                ((Hashtable<String, String>)scopeProps).put("userEventType", "userModified");
                final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.TECHNICIAN, valueList, scopeProps);
                SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_MODIFIED);
            }
        }
        else {
            try {
                final Criteria deleteCriteria = new Criteria(Column.getColumn("TechnicianScopeRel", "TECH_ID"), (Object)userEvent.loginID, 0);
                SyMUtil.getPersistence().delete(deleteCriteria);
            }
            catch (final Exception exp) {
                SummaryScopeUserListenerImpl.logger.log(Level.SEVERE, "Error while deleting existing Summary Scope Mapping.  Exception is " + exp.getMessage());
            }
            final List<Long> customerIdlist = userEvent.managedCustomers;
            SummaryScopeUtil.getInstance().fetchandCreateSummaryScopeMapping(customerIdlist.toArray(new Long[customerIdlist.size()]), SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, userEvent.loginID, SummaryScopeConstants.TECHNICIAN);
        }
    }
    
    @Override
    public void userManagedComputerChanged(final ScopeEvent scopeEvent) {
        final List<Long> techIDs = scopeEvent.loginIds;
        if (scopeEvent.isCGScopeChanged || scopeEvent.isROScopeChanged) {
            for (final Long techID : techIDs) {
                final Long summaryScopeID = SummaryScopeUtil.getInstance().getMatchedSummaryScopeID(techID, SummaryScopeConstants.TECHNICIAN);
                final List<Long> valueList = new ArrayList<Long>();
                valueList.add(techID);
                final Properties scopeProps = new Properties();
                ((Hashtable<String, String>)scopeProps).put("userEventType", "userManagedComputerChanged");
                final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.TECHNICIAN, valueList, scopeProps);
                SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_MODIFIED);
            }
        }
        SummaryScopeUserListenerImpl.logger.log(Level.INFO, SummaryScopeUserListenerImpl.sourceClass + ":" + "userManagedComputerChanged");
    }
    
    static {
        SummaryScopeUserListenerImpl.logger = Logger.getLogger("SummaryScopeLogger");
        SummaryScopeUserListenerImpl.sourceClass = SummaryScopeUserListenerImpl.class.getName();
    }
}
