package com.me.ems.framework.summaryscope.listenerimpl;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import com.me.ems.framework.summaryscope.listener.SummaryScopeEvent;
import java.util.logging.Level;
import com.me.ems.framework.summaryscope.utils.SummaryScopeConstants;
import com.me.ems.framework.summaryscope.utils.SummaryScopeUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class SummaryScopeCustomerListenerImpl implements CustomerListener
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void customerAdded(final CustomerEvent customerEvent) {
        final Long customerID = customerEvent.customerID;
        final List<Long> valueID = new ArrayList<Long>();
        valueID.add(customerID);
        final Long summaryScopeID = SummaryScopeUtil.getInstance().createSummaryScope(customerID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP);
        SummaryScopeCustomerListenerImpl.logger.log(Level.INFO, SummaryScopeCustomerListenerImpl.sourceClass + ":" + "customerAdded" + ": CustomerID:" + customerID + ", summaryScopeID:" + summaryScopeID + ", summaryScopeType:" + SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP);
        final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, valueID);
        SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_ADDED);
    }
    
    @Override
    public void customerDeleted(final CustomerEvent customerEvent) {
        final Long customerID = customerEvent.customerID;
        final List<Long> valueID = new ArrayList<Long>();
        valueID.add(customerID);
        final List<Long> delSummaryScopeID = SummaryScopeUtil.getInstance().clearScopeRel(customerID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP);
        SummaryScopeCustomerListenerImpl.logger.log(Level.INFO, SummaryScopeCustomerListenerImpl.sourceClass + ":" + "customerDeleted" + ", customerID:" + customerID + ", affectedScopeIDs:" + Arrays.toString(delSummaryScopeID.toArray()));
        for (final Long summaryScopeID : delSummaryScopeID) {
            final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, valueID);
            SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_DELETED);
        }
    }
    
    @Override
    public void customerUpdated(final CustomerEvent customerEvent) {
        SummaryScopeCustomerListenerImpl.logger.log(Level.INFO, SummaryScopeCustomerListenerImpl.sourceClass + ":" + "customerUpdated");
        final Long customerID = customerEvent.customerID;
        final List<Long> valueID = new ArrayList<Long>();
        valueID.add(customerID);
        final Long summaryScopeID = SummaryScopeUtil.getInstance().getMatchedSummaryScopeID(customerID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP);
        SummaryScopeCustomerListenerImpl.logger.log(Level.INFO, SummaryScopeCustomerListenerImpl.sourceClass + ":" + "customerUpdated" + ", customerID:" + customerID + ", affectedScopeID:" + summaryScopeID);
        final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, valueID);
        SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_MODIFIED);
    }
    
    @Override
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
    
    static {
        SummaryScopeCustomerListenerImpl.logger = Logger.getLogger("SummaryScopeLogger");
        SummaryScopeCustomerListenerImpl.sourceClass = SummaryScopeCustomerListenerImpl.class.getName();
    }
}
