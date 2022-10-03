package com.me.idps.core.factory;

import com.me.idps.core.sync.synch.DirSingletonQueue;
import java.util.Properties;
import java.util.List;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;

public abstract class IdpsProdEnvAPI
{
    public abstract void normalizeDB();
    
    public abstract SelectQuery getOPcountQuery(final Long p0);
    
    public abstract void reset(final Connection p0, final Long p1, final boolean p2);
    
    public abstract void handleServerStartup();
    
    public abstract String getSchemaName();
    
    public abstract String getDomainName(final String p0, final String p1);
    
    public abstract void startADSyncScheduler(final Long p0);
    
    protected abstract void stopADSyncScheduler(final Long p0);
    
    public boolean checkAndStopADSyncSchduler(final Long customerId) {
        final List<Properties> domainPropsList = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerId);
        if (domainPropsList == null || domainPropsList.isEmpty()) {
            this.stopADSyncScheduler(customerId);
            return true;
        }
        return false;
    }
    
    public abstract boolean isManualVAdisabled(final boolean p0);
    
    public abstract long[] allocatePKs(final DirSingletonQueue p0, final int p1) throws Exception;
    
    @Deprecated
    public boolean isPrePlanEnabled() {
        return true;
    }
}
