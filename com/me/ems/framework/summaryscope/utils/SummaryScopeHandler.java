package com.me.ems.framework.summaryscope.utils;

import java.util.List;
import java.util.logging.Logger;

public class SummaryScopeHandler
{
    private Logger logger;
    private static String sourceClass;
    private static SummaryScopeHandler summaryScopeHandler;
    
    public SummaryScopeHandler() {
        this.logger = Logger.getLogger("SummaryScopeLogger");
    }
    
    public static SummaryScopeHandler getInstance() {
        if (SummaryScopeHandler.summaryScopeHandler == null) {
            SummaryScopeHandler.summaryScopeHandler = new SummaryScopeHandler();
        }
        return SummaryScopeHandler.summaryScopeHandler;
    }
    
    public Long modifyTechnicianScope(final Long techID, final List<Long> scopeList, final Integer scopeType) {
        SummaryScopeUtil.getInstance().clearScopeRel(techID, SummaryScopeConstants.TECHNICIAN);
        return this.addTechnicianScope(techID, scopeList, scopeType);
    }
    
    public Long addTechnicianScope(final Long techID, final List<Long> scopeList, final Integer scopeType) {
        final SummaryScopeUtil util = SummaryScopeUtil.getInstance();
        Long summaryScopeID = -1L;
        if (scopeType.equals(SummaryScopeConstants.REMOTE_OFFICE) || scopeType.equals(SummaryScopeConstants.CUSTOM_GROUP)) {
            if (scopeList.size() > 1) {
                summaryScopeID = util.getSimilarScopeForTechnician(scopeList, scopeType);
                if (summaryScopeID.equals(-1L)) {
                    summaryScopeID = SummaryScopeUtil.getInstance().createTechSummaryScope(techID, scopeList, scopeType);
                }
                else {
                    util.mapToExistingScope(techID, summaryScopeID, SummaryScopeConstants.TECHNICIAN);
                }
            }
            else {
                summaryScopeID = util.getMatchedSummaryScopeID(scopeList.get(0), scopeType);
                util.mapToExistingScope(techID, summaryScopeID, SummaryScopeConstants.TECHNICIAN);
            }
        }
        else {
            summaryScopeID = util.getOrCreateAllManagedScope();
            util.mapToExistingScope(techID, summaryScopeID, SummaryScopeConstants.TECHNICIAN);
        }
        return summaryScopeID;
    }
    
    static {
        SummaryScopeHandler.sourceClass = SummaryScopeHandler.class.getName();
        SummaryScopeHandler.summaryScopeHandler = null;
    }
}
