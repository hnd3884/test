package com.me.ems.framework.summaryscope.listenerimpl;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import com.me.ems.framework.summaryscope.listener.SummaryScopeEvent;
import com.me.ems.framework.summaryscope.utils.SummaryScopeConstants;
import com.me.ems.framework.summaryscope.utils.SummaryScopeUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupListener;

public class SummaryScopeCGListenerImpl implements CustomGroupListener
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void customGroupAdded(final CustomGroupEvent cgEvent) {
        final List<Long> valueID = new ArrayList<Long>();
        final Long customGroupID = cgEvent.customGroupID;
        valueID.add(customGroupID);
        Integer groupCategory = -1;
        try {
            groupCategory = (Integer)DBUtil.getValueFromDB("CustomGroup", "RESOURCE_ID", customGroupID, "GROUP_CATEGORY");
        }
        catch (final Exception ex) {
            SummaryScopeCGListenerImpl.logger.log(Level.SEVERE, "Unable to fetch the cg type. Stacktrace::", ex);
        }
        if (groupCategory.equals(5) || groupCategory.equals(1)) {
            final Long summaryScopeID = SummaryScopeUtil.getInstance().createSummaryScope(cgEvent.customGroupID, SummaryScopeConstants.CUSTOM_GROUP);
            SummaryScopeCGListenerImpl.logger.log(Level.INFO, SummaryScopeCGListenerImpl.sourceClass + ":" + "customGroupAdded" + ": customGroupID:" + customGroupID + ", SummaryScopeID:" + summaryScopeID + " summaryScopeType:" + SummaryScopeConstants.CUSTOM_GROUP);
            final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOM_GROUP, valueID);
            SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_ADDED);
        }
    }
    
    @Override
    public void customGroupDeleted(final CustomGroupEvent cgEvent) {
        final List<Long> valueID = new ArrayList<Long>();
        valueID.add(cgEvent.customGroupID);
        final List<Long> delSummaryScopeID = SummaryScopeUtil.getInstance().clearScopeRel(cgEvent.customGroupID, SummaryScopeConstants.CUSTOM_GROUP);
        SummaryScopeCGListenerImpl.logger.log(Level.INFO, SummaryScopeCGListenerImpl.sourceClass + ":" + "customGroupDeleted" + ". customGroupID" + cgEvent.customGroupID + ". Affected ScopeIDs:" + Arrays.toString(delSummaryScopeID.toArray()));
        for (final Long summaryScopeID : delSummaryScopeID) {
            final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOM_GROUP, valueID);
            SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_DELETED);
        }
    }
    
    @Override
    public void customGroupModified(final CustomGroupEvent cgEvent) {
        final List<Long> valueID = new ArrayList<Long>();
        valueID.add(cgEvent.customGroupID);
        SummaryScopeCGListenerImpl.logger.log(Level.INFO, SummaryScopeCGListenerImpl.sourceClass + ":" + "customGroupModified");
        Integer groupCategory = -1;
        try {
            groupCategory = (Integer)DBUtil.getValueFromDB("CustomGroup", "RESOURCE_ID", cgEvent.customGroupID, "GROUP_CATEGORY");
        }
        catch (final Exception ex) {
            SummaryScopeCGListenerImpl.logger.log(Level.SEVERE, "Unable to fetch the cg type. Stacktrace::", ex);
        }
        if (groupCategory.equals(5) || groupCategory.equals(1)) {
            final Long summaryScopeID = SummaryScopeUtil.getInstance().getMatchedSummaryScopeID(cgEvent.customGroupID, SummaryScopeConstants.CUSTOM_GROUP);
            SummaryScopeCGListenerImpl.logger.log(Level.INFO, SummaryScopeCGListenerImpl.sourceClass + ":" + "userModified" + ": CustomGroupID:" + cgEvent.customGroupID + ", newSummaryScopeID:" + summaryScopeID + " summaryScopeType:" + SummaryScopeConstants.CUSTOM_GROUP);
            final SummaryScopeEvent event = new SummaryScopeEvent(summaryScopeID, SummaryScopeConstants.CUSTOM_GROUP, valueID);
            SummaryScopeUtil.getInstance().invokeSummaryScopeListeners(event, SummaryScopeConstants.EVENT_SCOPE_MODIFIED);
        }
    }
    
    static {
        SummaryScopeCGListenerImpl.logger = Logger.getLogger("SummaryScopeLogger");
        SummaryScopeCGListenerImpl.sourceClass = SummaryScopeCGListenerImpl.class.getName();
    }
}
